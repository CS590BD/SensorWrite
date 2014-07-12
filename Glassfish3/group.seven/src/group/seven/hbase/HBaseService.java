package group.seven.hbase;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.MasterNotRunningException;
import org.apache.hadoop.hbase.ZooKeeperConnectionException;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;

@Path("/hbase")
public class HBaseService {

	private final String HBASE_ZOOKEEPER_QUORUM_IP = "localhost.localdomain";
	private final String HBASE_ZOOKEEPER_PROPERTY_CLIENTPORT = "2181";
	private final String HBASE_MASTER = HBASE_ZOOKEEPER_QUORUM_IP + ":60010";

	/**
	 * CREATE TABLE
	 * http://localhost:8080/goup.seven/rest/hbase/create/tablename/column1:column2:column3:column4:column5
	 * 
	 * @param tablename
	 * @param columnFamilies
	 * @return
	 */
	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/create/{tablename:.+}/{columnFamilies:.+}")
	public String createTable(
			@PathParam("tablename") String tablename,
			@PathParam("columnFamilies") String columnFamilies) {
		String line = "{'status':'init'}";
		HBaseAdmin hba = null;
		Configuration config = getHBaseConfiguration();
		try {
			// create a table
			HTableDescriptor ht = new HTableDescriptor(tablename);
			// add columns
			for (String columnFamily : columnFamilies.split(":")) {
				ht.addFamily(new HColumnDescriptor(columnFamily));
			}
			try { // save the table
				hba = new HBaseAdmin(config);
				hba.createTable(ht);
				line = "{'status':'ok'}";
			} catch (Exception ex) {
				line = exceptionToJson(ex);
			}
		} finally { // close the connection
			try {
				hba.close();
			} catch (IOException e) {
				// do nothing
			}
		}
		return line;
	}

	/**
	 * READ ALL FROM TABLE
	 * http://localhost:8080/group.seven/rest/hbase/fetch/tablename
	 * 
	 * @param table
	 * @return
	 */
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/fetch/{tablename:.+}")
	public String readAll(@PathParam("tablename") String table) {
		String line = "";
		Configuration config = getHBaseConfiguration();
		try {
			HTable ht = new HTable(config, table);
			Scan s = new Scan();
			ResultScanner ss = ht.getScanner(s);
			for (Result r : ss) {
				for (KeyValue kv : r.raw()) {
					line = line + new String(kv.getRow()) + " ";
					line = line + new String(kv.getFamily()) + ":";
					line = line + new String(kv.getQualifier()) + " ";
					line = line + kv.getTimestamp() + " ";
					line = line + new String(kv.getValue());
					line = line + "\n\n";
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return line;
	}

	/**
	 * UPDATE AT QUALIFIER
	 * http://localhost.localdomain:8080/goup.seven/rest/hbase/insert/tablename/row/family/qualifier
	 * 
	 * @param value - passed in the header message
	 * @param table
	 * @param row
	 * @param family
	 * @param qualifier
	 * @return
	 */
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/insert/{table:.+}/{row:.+}/{family:.+}/{qualifier:.+}")
	public String insertSingle(String value, 
			@PathParam("table") String table,
			@PathParam("row") String row, 
			@PathParam("family") String family,
			@PathParam("qualifier") String qualifier) {

		String line = "{'status':'init'}";
		Configuration config = getHBaseConfiguration();
		HTable ht = null;
		try {
			ht = new HTable(config, table);
			Put put = new Put(Bytes.toBytes(row));
			put.add(Bytes.toBytes(family), Bytes.toBytes(qualifier), System.currentTimeMillis(), Bytes.toBytes(value));
			ht.put(put);
			line = "{'status':'ok'}";
		} catch (Exception ex) {
			line = exceptionToJson(ex);
		}
		return line;
	}

	/**
	 * DELETE TABLE
	 * http://localhost:8080/group.seven/rest/hbase/delete/tablename
	 * 
	 * @param table
	 * @return
	 */
	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/delete/{table:.+}")
	public String deleteTable(@PathParam("table") String table) {
		String line = "{'status':'init}";
		try {
			Configuration config = getHBaseConfiguration();
			HBaseAdmin admin = new HBaseAdmin(config);
			admin.disableTable(table);
			admin.deleteTable(table);
			line = "{'status':'ok'}";
		} catch (Exception ex) {
			line = exceptionToJson(ex);
		}
		return line;
	}

	/**
	 * INSERT FILE
	 * http://localhost:8080/group.seven/rest/hbase/insert-sensor-txt
	 * /ttjj_sensor_txt//path
	 * 
	 * @param tablename
	 * @param filepath
	 * @param columnFamilies
	 * @return
	 */
	@GET
	@Produces("application/json")
	@Path("/insert-sensor-txt/{tablename:.+}/{filepath:.+}")
	public String insertFile(@PathParam("tablename") String table,
			@PathParam("filepath") String filePath) {

		String line = "{'status':'init'}";
		try {
			HBase.insertSensorsTxt(table, filePath);
			line = "{'status':'ok'}";
		} catch (Exception ex) {
			line = exceptionToJson(ex);
		}
		return line;
	}

	/**
	 * RETURN THE HBASE CONFIGURATION
	 * 
	 * @return
	 */
	private Configuration getHBaseConfiguration() {
		Configuration config = HBaseConfiguration.create();
		config.clear();
		config.set("hbase.zookeeper.quorum", HBASE_ZOOKEEPER_QUORUM_IP);
		config.set("hbase.zookeeper.property.clientPort",
				HBASE_ZOOKEEPER_PROPERTY_CLIENTPORT);
		config.set("hbase.master", HBASE_MASTER);
		return config;
	}

	/**
	 * HANDLE ALL EXCEPTIONS
	 * 
	 * @param ex
	 * @return
	 */
	private String exceptionToJson(Exception ex) {
		String json = "{'status':'fail','exception':'";
		String exception = "";
		String message = "";
		if (ex instanceof MasterNotRunningException) {
			exception = MasterNotRunningException.class.toString();
			message = ex.getMessage();
		} else if (ex instanceof ZooKeeperConnectionException) {
			exception = ZooKeeperConnectionException.class.toString();
			message = ex.getMessage();
		} else if (ex instanceof FileNotFoundException) {
			exception = FileNotFoundException.class.toString();
			message = ex.getMessage();
		} else if (ex instanceof IOException) {
			exception = IOException.class.toString();
			message = ex.getMessage();
		} else if (ex instanceof NullPointerException) {
			exception = NullPointerException.class.toString();
			message = ex.getMessage();
		} else {
			exception = Exception.class.toString();
			message = ex.getMessage();
		}
		json += exception + "','msg':'" + message + "'}";
		return json;
	}
}

package group.seven.hbase;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.MasterNotRunningException;
import org.apache.hadoop.hbase.ZooKeeperConnectionException;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;

import com.sun.org.apache.bcel.internal.generic.INSTANCEOF;

@Path("/hbase")
public class HBaseService {
		
	private final String HBASE_ZOOKEEPER_QUORUM_IP = "localhost.localdomain";
	private final String HBASE_ZOOKEEPER_PROPERTY_CLIENTPORT = "2181";
	private final String HBASE_MASTER = HBASE_ZOOKEEPER_QUORUM_IP + ":60010";

	/**
	 * CREATE
	 * http://server:port/application/rest/hbase/create/tablename/column1:column2:column3:column4:column5
	 * 
	 * @param tablename
	 * @param columnFamilies
	 * @return
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/create/{tablename:.+}/{columnFamilies:.+}")
	public String createTable(@PathParam("tablename") String tablename,
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
			} catch (Exception ex){
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
	 * READ
	 * @param table
	 * @return
	 */
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/fetch/{tablename:.+}")
    public String hbaseRetrieveAll(@PathParam("tablename") String table) {
        String line="{'status':'init'}";
        Configuration config = getHBaseConfiguration();
 		try{
             HTable ht = new HTable(config, table);
             Scan s = new Scan();
             ResultScanner ss = ht.getScanner(s);
             for(Result r:ss){
                 for(KeyValue kv : r.raw()){
                	 line = line+ new String(kv.getRow()) + " ";
                	 line = line + new String(kv.getFamily()) + ":";
                	 line = line + new String(kv.getQualifier()) + " ";
                	 line = line + kv.getTimestamp() + " ";
                	 line = line + new String(kv.getValue());
                	 line = line + "/n";
                 }
             }
        } catch (IOException e){
            e.printStackTrace();
        }
		return line;
    }
	
	/**
	 * UPDATE
	 * /update/alphabet/A/time:x:y:z
	 * @param tableName
	 * @param columnFamilies
	 * @return
	 */
	@PUT
	@Path("/insert/{table:.+}/{row:.+}/{family:.+}/{qualifier:.+}/{value:.+}")
	@Consumes(MediaType.APPLICATION_JSON)
	public String insert(
			String headerString,
			@PathParam("table") String table,
			@PathParam("row") String row,
			@PathParam("family") String family,
			@PathParam("qualifier") String qualifier,
			@PathParam("value") String value) {
		
		String line = "{'status':'init'}";
		Configuration config = getHBaseConfiguration();
		HTable ht = null;
		
		try {
			ht = new HTable(config, table);
		} catch (IOException e) {
			line = "{'status':'fail','exception':'IOException','msg':'" + e.getMessage() + "'}";
			e.printStackTrace();
		}
		
		Put put = new Put(Bytes.toBytes(row));
		put.add(Bytes.toBytes(family), Bytes.toBytes(qualifier), Bytes.toBytes(value));
		
		try {
			ht.put(put);
			line = "{'status':'ok'}";
		} catch (IOException e) {
			line = "{'status':'fail','exception':'IOException','msg':'" + e.getMessage() + "'}";
		}
		
		return line;
	}

	/**
	 * DELETE
	 * @param table
	 * @return
	 */
	public String deleteTable(String table) {
		String line = "{'status':'init}";
		try {
			Configuration config = getHBaseConfiguration();
			HBaseAdmin admin = new HBaseAdmin(config);
			admin.disableTable(table);
			admin.deleteTable(table);
			line = "{'status':'ok'}";
		} catch (MasterNotRunningException ex) {
			line = "{'status':'fail','exception':'MasterNotRunningException','msg':'" + ex.getMessage() + "'}";
		} catch (ZooKeeperConnectionException ex) {
			line = "{'status':'fail','exception':'ZooKeeperConnectionException','msg':'" + ex.getMessage() + "'}";
		} catch (IOException ex) {
			line = "{'status':'fail','exception':'IOException','msg':'" + ex.getMessage() + "'}";
		} 
		return line;
	}

	
	@PUT
	@Produces("application/json")
	@Consumes(MediaType.APPLICATION_JSON)
	public String putSensorsTxt() {
		String line = "{'status':'init'}";
		
		return line;
	}
	

	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public String doPost(String message) {
		String line = "{'status':'fail','exception':'NotYetImplemented','msg':'POST method not yet implemented'}";
		return line;
	}
	
	/*
	@PUT
	@Path("/insert/{tableName:.+}/{rowName:.+}/{columnFamilies:.+}")
	@Consumes(MediaType.APPLICATION_JSON)
	public String insert(
			String headerString,
			@PathParam("tableName") String tableName,
			@PathParam("rowName") String rowName,
			@PathParam("columnFamilies") String columnFamilies) {
		
		String line = "{'status':'init'}";
		Configuration config = getHBaseConfiguration();
		HTable table = null;

		try {
			table = new HTable(config, tableName);
		} catch (IOException e1) {
			line = "{'status':'fail','exception':'IOException','msg':'" + e1.getMessage() + "'}";
			e1.printStackTrace();
		}

		Put put = new Put(Bytes.toBytes(rowName));
		
		String[] values = headerString.split(":");
		String[] columns = columnFamilies.split(":");

		try {
			int count = 0;
		for (String s : values) {
			put.add(Bytes.toBytes(columns[count]), Bytes.toBytes(s));
			count++;
		}
		} catch (NullPointerException e) {
			line = "{'status':'fail','exception':'NullPointerException','msg':'" + e.getMessage() + "'}";
		}
		
		try {
				Put p = new Put(Bytes.toBytes("A"), timeStamp);

				p.add(Bytes.toBytes(cf1), Bytes.toBytes("col" + count),
						Bytes.toBytes(latitude + "," + longitude));

				p.add(Bytes.toBytes(cf2), Bytes.toBytes("col" + (count + 2)),
						Bytes.toBytes(Date));

				p.add(Bytes.toBytes(cf3), Bytes.toBytes("col" + (count + 3)),
						Bytes.toBytes(x + "," + y + "," + z));

				table.put(p);

				count = count + 1;
				timestamp = timestamp + 1;

		} catch (IOException e) {
			e.printStackTrace();
			line = e.toString();
		} finally {
		}

		return line;
	}
	

	
	/**
	 * INSERT FILE
	 * @param tablename
	 * @param filepath
	 * @param columnFamilies
	 * @return
	 */
	@GET
	@Produces("application/json")
	@Path("insert/{tablename:.+}/{filepath:.+}/{columnFamilies:.+}")
	public String insertFile(
			@PathParam("tablename") String tablename,
			@PathParam("filepath") String filepath,
			@PathParam("columnFamilies") String columnFamilies) {
		
		String line = "insert success";

		Configuration config = HBaseConfiguration.create();
		config.clear();
		config.set("hbase.zookeeper.quorum", HBASE_ZOOKEEPER_QUORUM_IP);
		config.set("hbase.zookeeper.property.clientPort", HBASE_ZOOKEEPER_PROPERTY_CLIENTPORT);
		config.set("hbase.master", HBASE_MASTER);

		String latitude = "", longitude = "", Date = "", x = "", y = "", z = "";

		HTable table = null;
		try {
			table = new HTable(config, tablename);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		int count = 1;
		int timestamp = 10000;

		BufferedReader br = null;

		String finalpath = filepath.replace("-", "/");

		try {

			String sCurrentLine;

			br = new BufferedReader(new FileReader(finalpath));

			while ((sCurrentLine = br.readLine()) != null) {

				Put p = new Put(Bytes.toBytes("row1"), timestamp);

				if (sCurrentLine.equals("")) {
					continue;
				}

				String[] array = sCurrentLine.split("\t");
				latitude = array[0];
				longitude = array[1];
				Date = array[2];
				x = array[3];
				y = array[4];
				z = array[5];

				String cf1 = columnFamilies.split(":")[0];
				String cf2 = columnFamilies.split(":")[1];
				String cf3 = columnFamilies.split(":")[2];

				p.add(Bytes.toBytes(cf1), Bytes.toBytes("col" + count),
						Bytes.toBytes(latitude + "," + longitude));

				// p.add(Bytes.toBytes("longitude"),
				// Bytes.toBytes("col"+(count+1)),Bytes.toBytes(longitude));

				p.add(Bytes.toBytes(cf2), Bytes.toBytes("col" + (count + 2)),
						Bytes.toBytes(Date));

				p.add(Bytes.toBytes(cf3), Bytes.toBytes("col" + (count + 3)),
						Bytes.toBytes(x + "," + y + "," + z));

				// p.add(Bytes.toBytes("y"),
				// Bytes.toBytes("col"+(count+4)),Bytes.toBytes(y));

				// p.add(Bytes.toBytes("z"),
				// Bytes.toBytes("col"+(count+5)),Bytes.toBytes(z));

				table.put(p);

				count = count + 1;
				timestamp = timestamp + 1;

			}

		} catch (IOException e) {
			e.printStackTrace();
			line = e.toString();
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
				line = ex.toString();
			}
		}

		return line;
	}
	
	
	private Configuration getHBaseConfiguration() {
		Configuration config = HBaseConfiguration.create();
		config.clear();
		config.set("hbase.zookeeper.quorum", HBASE_ZOOKEEPER_QUORUM_IP);
		config.set("hbase.zookeeper.property.clientPort", HBASE_ZOOKEEPER_PROPERTY_CLIENTPORT);
		config.set("hbase.master", HBASE_MASTER);
		return config;
	}
	
	/**
	 * HANDLE ALL EXCEPTIONS
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

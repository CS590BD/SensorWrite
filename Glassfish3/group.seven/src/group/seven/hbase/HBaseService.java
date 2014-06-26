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

@Path("/hbase")
public class HBaseService {
		
	//Local Cloudera
	private final String HBASE_ZOOKEEPER_QUORUM_IP = "192.168.101.129";
	private final String HBASE_ZOOKEEPER_PROPERTY_CLIENTPORT = "2181";
	private final String HBASE_MASTER = HBASE_ZOOKEEPER_QUORUM_IP + ":60010";

	//UMKC Cloudera
	//private final String HBASE_ZOOKEEPER_QUORUM_IP = "134.193.136.147";
	//private final String HBASE_ZOOKEEPER_PROPERTY_CLIENTPORT = "2181";
	//private final String HBASE_MASTER = HBASE_ZOOKEEPER_QUORUM_IP + ":60010";

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
	public String create(@PathParam("tablename") String tablename,
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
			} catch (MasterNotRunningException e) {
				line = "{'status':'fail','exception':'MasterNotRunningException','msg':'"
						+ e.getMessage() + "'}";
				e.printStackTrace();
			} catch (ZooKeeperConnectionException e) {
				line = "{'status':'fail','exception':'ZooKeeperConnectionException','msg':'"
						+ e.getMessage() + "'}";
				e.printStackTrace();
			} catch (IOException e) {
				line = "{'status':'fail','exception':'IOException','msg':'"
						+ e.getMessage() + "'}";
				e.printStackTrace();
			} catch (NullPointerException e) {
				line = "{'status':'fail','exception':'NullPointerException','msg':'" + e.getMessage() + "'}";
				e.printStackTrace();
			} catch (Exception e){
				line = "{'status':'fail','exception':'Exception','msg':'" + e.getMessage() + "'}";
			}

		} finally { // close the connection
			try {
				hba.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return line;
	}

	/**
	 * INSERT
	 * /insert/alphabet/A/time:x:y:z
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

	@PUT
	@Produces("application/json")
	@Consumes(MediaType.APPLICATION_JSON)
	public String putSensorsTxt() {
		String line = "{'status':'init'}";
		
		return line;
	}
	
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/hbaseRetrieveAll/{tablename:.+}")
    public String hbaseRetrieveAll(@QueryParam("callback") String callback,@PathParam("tablename") String tablename) {
        String line="";
    	
        Configuration config = getHBaseConfiguration();         
      
 		try{
             HTable table = new HTable(config, tablename);
             Scan s = new Scan();
             ResultScanner ss = table.getScanner(s);
             for(Result r:ss){
                 for(KeyValue kv : r.raw()){
                	 line = line+ new String(kv.getRow()) + " ";
                	 line = line + new String(kv.getFamily()) + ":";
                	 line = line + new String(kv.getQualifier()) + " ";
                	 line = line + kv.getTimestamp() + " ";
                	 line = line + new String(kv.getValue());
                	 line = line + "/n";
                   /* System.out.print(new String(kv.getRow()) + " ");
                    System.out.print(new String(kv.getFamily()) + ":");
                    System.out.print(new String(kv.getQualifier()) + " ");
                    System.out.print(kv.getTimestamp() + " ");
                    System.out.println(new String(kv.getValue()));*/
                 }
             }
        } catch (IOException e){
            e.printStackTrace();
        }
 		
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
}

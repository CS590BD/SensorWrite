package group.seven.hbase;

import java.io.BufferedReader;
import java.io.FileReader;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;

public class HBase {
	
	// My Cloudera
	private static final String HBASE_ZOOKEEPER_QUORUM_IP = "192.168.101.129";
	private static final String HBASE_ZOOKEEPER_PROPERTY_CLIENTPORT = "2181";
	private static final String HBASE_MASTER = HBASE_ZOOKEEPER_QUORUM_IP + ":60010";

	// UMKC Cloudera
	//private static final String HBASE_ZOOKEEPER_QUORUM_IP = "134.193.136.147";
	//private static final String HBASE_ZOOKEEPER_PROPERTY_CLIENTPORT = "2181";
	//private static final String HBASE_MASTER = HBASE_ZOOKEEPER_QUORUM_IP + ":60010";

	public static void createTable(String table, String columnFamilies)
			throws Exception {
		HBaseAdmin hba = null;
		Configuration config = getHBaseConfiguration();
		HTableDescriptor ht = new HTableDescriptor(table);
		for (String columnFamily : columnFamilies.split(":")) {
			ht.addFamily(new HColumnDescriptor(columnFamily));
		}
		hba = new HBaseAdmin(config);
		hba.createTable(ht);
		hba.close();
	}

	public static void insertRow(String table, String row, String family,
			String qualifier, String value) throws Exception {
		Configuration config = getHBaseConfiguration();
		HTable ht = new HTable(config, table);
		Put put = new Put(Bytes.toBytes(row));
		put.add(Bytes.toBytes(family), Bytes.toBytes(qualifier),
				Bytes.toBytes(value));
		ht.put(put);
	}

	public static String getRecord(String table) throws Exception {
		String line = "";

		Configuration config = getHBaseConfiguration();

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
				line = line + "/n";
			}
		}
		return line;
	}

	public static void insertSensorsTxt(String table, String pathToFile)
			throws Exception {
		String ambient_temp = "ambient_temp";
		String object_temp = "ambient_temp";
		String relative_humidity = "relative_humidity";
		String acceleration = "acceleration";
		String orientation = "orientation";
		String family = "";
		String qualifier = "";
		String value = "";
		Configuration config = getHBaseConfiguration();
		HTable ht = new HTable(config, table);
		BufferedReader br = null;
		String sCurrentLine;
		br = new BufferedReader(new FileReader(pathToFile));
		int count = 1;
		Put put = null;
		while ((sCurrentLine = br.readLine()) != null) {
			System.out.println(sCurrentLine);

			value = sCurrentLine.split(":")[3].trim();
			switch (count % 5) {
			case 1:
				family = ambient_temp;
			//	put = new Put(Bytes.toBytes(val));
				put.add(Bytes.toBytes(family), Bytes.toBytes("qualifier"+count), Bytes.toBytes(value));

				break;
			case 2:
				family = object_temp;
				put.add(Bytes.toBytes(family), Bytes.toBytes("qualifier"+count+2), Bytes.toBytes(value));

				break;
			case 3:
				family = relative_humidity;
				put.add(Bytes.toBytes(family), Bytes.toBytes("qualifier"+count+3), Bytes.toBytes(value));

				break;
			case 4:
				family = acceleration;
				put.add(Bytes.toBytes(family), Bytes.toBytes("qualifier"+count+4), Bytes.toBytes(value));

				break;
			case 5:
				family = orientation;
				put.add(Bytes.toBytes(family), Bytes.toBytes("qualifier"+count+5), Bytes.toBytes(value));
				
				
				break;
			}
			
			ht.put(put);
			count++;
		}
		if (br != null) {
			br.close();
		}
	}

	public static void deleteTable(String table) throws Exception {
		Configuration config = getHBaseConfiguration();
		HBaseAdmin admin = new HBaseAdmin(config);
		admin.disableTable(table);
		admin.deleteTable(table);
	}

	private static Configuration getHBaseConfiguration() {
		Configuration config = HBaseConfiguration.create();
		config.clear();
		config.set("hbase.zookeeper.quorum", HBASE_ZOOKEEPER_QUORUM_IP);
		config.set("hbase.zookeeper.property.clientPort", HBASE_ZOOKEEPER_PROPERTY_CLIENTPORT);
		config.set("hbase.master", HBASE_MASTER);
		return config;
	}
}

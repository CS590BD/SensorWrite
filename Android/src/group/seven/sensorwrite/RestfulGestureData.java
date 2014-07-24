package group.seven.sensorwrite;

import android.content.Context;

public class RestfulGestureData {
	
	private String ip = "";
	private String port = "";
	private String application = "";
	private String mappingPattern = "";
	private String storage = "";
	private String method = "";
	private String table = "";
	private String family = "";
	private String row = "";
	private String qualifier = "";
	private String value = "";
	
	/**
	 * Constructor for GET ROW
	 * @param context
	 */
	public RestfulGestureData(Context context, String method, String table, String row) {
		setGlassfishContext(context);
		this.method = method;
		this.table = table;
		this.row = row;
	}
	
	/**
	 * Constructor for GET CELL
	 * @param context
	 * @param character
	 */
	public RestfulGestureData(Context context, String method, String table, String row, String family, String qualifier, String value) {
		setGlassfishContext(context);
		this.method = method;
		this.table = table;
		this.row = row;
		this.table = table;
		this. row = row;
		this.family = family;
		this.qualifier = qualifier;
		this.value = value;
	}
	
	private void setGlassfishContext(Context context) {
		this.ip = context.getResources().getString(R.string.IP);
		this.port = context.getResources().getString(R.string.Port);
		this.application = context.getResources().getString(R.string.Application);
		this.mappingPattern = context.getResources().getString(R.string.MappingPattern);
		this.storage = context.getResources().getString(R.string.Storage);
	}
	
	/**
	 * Based on existing values only, builds as far as the qualifier:
	 * http://localhost:8080/group.seven/rest/hbase/post/table/row/family/qualifier
	 * or
	 * http://localhost:8080/group.seven/rest/hbase/get/characters/james
	 * @return
	 */
	public String toRestfulUrl() {
		StringBuilder builder = new StringBuilder();
		builder.append("http://" + ip);
		builder.append(":" + port);
		builder.append("/" + application);
		builder.append("/" + mappingPattern);
		builder.append("/" + storage);
		if(method.length() > 0)
			builder.append("/" + method);
		if(table.length() > 0)
			builder.append("/" + table);
		if(row.length() > 0)
			builder.append("/" + row);
		if(family.length() > 0)
			builder.append("/" + family);
		if(qualifier.length() > 0)
			builder.append("/" + qualifier);
		return builder.toString();
	}
}

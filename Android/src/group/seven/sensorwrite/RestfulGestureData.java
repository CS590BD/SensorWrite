package group.seven.sensorwrite;

import android.content.Context;

public class RestfulGestureData {
	
	private String ip;
	private String port;
	private String application;
	private String mappingPattern;
	private String storage;
	public String method;
	public String tableName;
	public String family;
	public String row;
	public String qualifier;
	public String value;
	
	public RestfulGestureData(Context context, String character) {
		setGlassfishContext(context);
		setFamily(character);
	}
	
	private void setGlassfishContext(Context context) {
		this.ip = context.getResources().getString(R.string.IP);
		this.port = context.getResources().getString(R.string.Port);
		this.application = context.getResources().getString(R.string.Application);
		this.mappingPattern = context.getResources().getString(R.string.MappingPattern);
		this.storage = context.getResources().getString(R.string.Storage);
	}
	
	private void setFamily(String character) {
		char c = character.charAt(0);
		if(Character.isUpperCase(c)) {
			this.family = "capital";
		} else if (Character.isLowerCase(c)) {
			this.family = "lowercase";
		} else if (Character.isDigit(c)) {
			this.family = "numeric";
		} else {
			this.family = "punctuation";
		}
	}
	/**
	 * Based on this restful web service:
	 * http://localhost.localdomain:8080/group.seven/rest/hbase/post/tablename/row/family/qualifier
	 * @return
	 */
	public String toRestfulUrl() {
		StringBuilder builder = new StringBuilder();
		builder.append("http://" + ip);
		builder.append(":" + port);
		builder.append("/" + application);
		builder.append("/" + mappingPattern);
		builder.append("/" + storage);
		builder.append("/" + method);
		builder.append("/" + tableName);
		builder.append("/" + row);
		builder.append("/" + family);
		builder.append("/" + qualifier);
		return builder.toString();
	}
}

package group.seven.sensorwrite;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import android.os.Environment;

public class SequenceFileWriter {
	public static boolean writeSequence(File file, String value) {
		String path = Environment.getExternalStorageDirectory() + "/SensorWrite/";
		try {
			String contents = "";
			FileWriter filewriter = new FileWriter(path + file, true);
		    String[] lines = value.split("\n");
		    
		    for(int i = 0; i < lines.length; i++) {
		    	String[] values = lines[i].split("\t");
		    	String x = values[1];
		    	String y = values[2];
		    	String z = values[3];
				contents += "[ " + x + "\t" + y + "\t" + z + " ] ; ";
		    }
			    filewriter.write(contents + "\n");
			    filewriter.close();
			    return true;
		} catch (IOException exception) {
			//do nothing?
		}
		return false;
	}
	public static boolean writeTempSequence(File file, String value) {
		String path = Environment.getExternalStorageDirectory() + "/SensorWrite/";
		try {
			String contents = "";
			FileWriter filewriter = new FileWriter(path + file, false);
		    String[] lines = value.split("\n");
		    
		    for(int i = 0; i < lines.length; i++) {
		    	String[] values = lines[i].split("\t");
		    	String x = values[1];
		    	String y = values[2];
		    	String z = values[3];
				contents += "[ " + x + "\t" + y + "\t" + z + " ] ; ";
		    }
			    filewriter.write(contents + "\n");
			    filewriter.close();
			    return true;
		} catch (IOException exception) {
			//do nothing?
		}
		return false;
	}
}

//http://www.androidhive.info/2013/07/how-to-implement-android-splash-screen-2/
//http://developer.android.com/training/basics/data-storage/files.html#WriteInternalStorage

package group.seven.sensorwrite;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;

public class SplashScreen extends Activity {
	// Splash screen timer
	private static int SPLASH_TIME_OUT = 1000;
	
	String[] characters = {"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z","!",",",".","temp"};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);

		//make sure all the sequence files are ready
		if(isExternalStorageWritable()) {
			File folder = new File(Environment.getExternalStorageDirectory() + "/SensorWrite");
			boolean success = true;
			if (!folder.exists()) {
			    success = folder.mkdir();
			}
			if (success) {
				for(String character : characters) {
					String filename = character + ".seq";
					File file = new File(folder.getPath() + "/" + filename);
					if(!file.exists()) {
						try {
							file.createNewFile();
						} catch (IOException ex) {
							//in case of failure?
						}
					}
				}
			} else {
				//in case of failure?
			}
		}

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				Intent i = new Intent(SplashScreen.this, MainActivity.class);
				startActivity(i);
				finish();
			}
		}, SPLASH_TIME_OUT);
	}
	
	/* Checks if external storage is available for read and write */
	public boolean isExternalStorageWritable() {
	    String state = Environment.getExternalStorageState();
	    if (Environment.MEDIA_MOUNTED.equals(state)) {
	        return true;
	    }
	    return false;
	}

	/* Checks if external storage is available to at least read */
	public boolean isExternalStorageReadable() {
	    String state = Environment.getExternalStorageState();
	    if (Environment.MEDIA_MOUNTED.equals(state) ||
	        Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
	        return true;
	    }
	    return false;
	}
}

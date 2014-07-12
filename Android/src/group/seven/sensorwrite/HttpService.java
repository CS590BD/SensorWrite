package group.seven.sensorwrite;

import java.io.IOException;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

public class HttpService extends Service {
	private final IBinder binder = new HttpServiceBinder();
	public String url = "";
	public String value = "";
	
	public class HttpServiceBinder extends Binder {
		HttpService getService() {
			return HttpService.this;
		}
	}
	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		String status = "{'status':'init'}";
		try {
			status = HTTP.post(url, value);
		} catch (IOException ex) {
			status = "{'status':'fail','exception':'" + ex.getMessage() + "'}";
		}

		Toast.makeText(this, status, Toast.LENGTH_LONG).show();
		return START_NOT_STICKY;
	}
}

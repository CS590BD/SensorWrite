package group.seven.sensorwrite;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	private ConnectionServiceReceiver receiver;

	TextView lblX, lblY, lblZ;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		lblX = (TextView)findViewById(R.id.lblX);
		lblY = (TextView)findViewById(R.id.lblY);
		lblZ = (TextView)findViewById(R.id.lblZ);

		Intent intent = new Intent(MainActivity.this, ConnectionService.class);
		IntentFilter filter = new IntentFilter(ConnectionServiceReceiver.PROCESS_RESPONSE);
		filter.addCategory(Intent.CATEGORY_DEFAULT);
		receiver = new ConnectionServiceReceiver();
		registerReceiver(receiver, filter);
		startService(intent);
	}
	
	/**
	 * BROADCAST RECEIVER
	 */
	public class ConnectionServiceReceiver extends BroadcastReceiver {
		public static final String PROCESS_RESPONSE = "group.seven.sensorwrite.intent.action.PROCESS_RESPONSE";
		@Override
		public void onReceive(Context context, Intent intent) {
			String X = intent.getStringExtra(ConnectionService.X);
			String Y = intent.getStringExtra(ConnectionService.Y);
			String Z = intent.getStringExtra(ConnectionService.Z);
			//long timestamp = intent.getLongExtra("TIMESTAMP", System.currentTimeMillis());
			lblX.setText(X);
			lblY.setText(Y);
			lblZ.setText(Z);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}

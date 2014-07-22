package group.seven.sensorwrite;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {
	
	Button btnTrainData, btnBrowseHBase;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//startService(new Intent(this,ConnectionService.class));
		
		btnTrainData = (Button)findViewById(R.id.btnTrainData);
		btnBrowseHBase = (Button)findViewById(R.id.btnBrowseHBase);
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
	public void goToHBaseBrowser(View view) {
		Intent intent = new Intent(MainActivity.this, HBaseActivity.class);
		startActivity(intent);
	}
	public void goToTrainData(View view) {
		
	}
}

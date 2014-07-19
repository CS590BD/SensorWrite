package group.seven.sensorwrite;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.wtf("system.out", "MainActivity loaded");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//inflate menu items for use in action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_activity_actions, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		//handle presses on action bar items
		switch(item.getItemId()) {
			case R.id.action_edit:
				//do nothing - already here
				return true;
			case R.id.action_storage:
				openStorage();
				return true;
			case R.id.action_settings:
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	private void openStorage() {
		Intent intent = new Intent(MainActivity.this, DataTrainingActivity.class);
	    startActivity(intent);
	}
}

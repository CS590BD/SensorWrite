package group.seven.sensorwrite;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class HBaseRowActivity extends ListActivity implements IHttpAsyncResponse {
	
	public final static String SELECTED_VALUE = "group.seven.sensorwrite.SELECTED_VALUE";
	
	ArrayList<String> list;
	ArrayAdapter<String> adapter;
	
	private HashMap<String, String> map;
	
	@Override
	public void processFinish(String output) {
		String[] columns = output.split("===");
		for(String column : columns) {
			String[] cell = column.split(":");
			String qualifier = cell[0];
			String value = cell[1];
			list.add(qualifier);
			map.put(qualifier, value);
		}
		Collections.sort(list);
		adapter.notifyDataSetChanged();
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
 		String key = (String) getListAdapter().getItem(position);
		String value = map.get(key);
 		openGraph(value);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.wtf("system.out", "HBaseRowActivity loaded");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hbaserow);
		//WTF: http://stackoverflow.com/questions/4540754/add-dynamically-elements-to-a-listview-android
		Context context = HBaseRowActivity.this;
		int resource = android.R.layout.simple_list_item_1; //the default list item layout supplied by Android
		list = new ArrayList<String>();
		adapter = new ArrayAdapter<String>(context, resource, list);
		setListAdapter(adapter);
		map = new HashMap<String, String>();
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		Context context = HBaseRowActivity.this;
		String method = "get";
		String table = "characters";
		String row = getResources().getString(R.string.user);
		String url = new RestfulGestureData(context, method, table, row).toRestfulUrl();

		HttpAsyncTask task = new HttpAsyncTask(context, url);
		task.delegate = HBaseRowActivity.this;
		task.execute("get");
		Log.wtf("url", url);
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
			case R.id.action_graph:
				//do nothing - already here
				return true;
			case R.id.action_edit:
				openWrite();
				return true;
			case R.id.action_storage:
				openStorage();
				return true;
			case R.id.action_settings:
				//settings not implemented
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	private void openGraph(String value) {
		Intent intent = new Intent(getApplicationContext(), GraphingActivity.class);
		intent.putExtra("SELECTED_VALUE", value);
		startActivity(intent);
//		Toast.makeText(this, value.toString(), Toast.LENGTH_SHORT).show();
	}
	private void openWrite() {
		Intent intent = new Intent(HBaseRowActivity.this, MainActivity.class);
	    startActivity(intent);
	}
	private void openStorage() {
		Intent intent = new Intent(HBaseRowActivity.this, DataTrainingActivity.class);
	    startActivity(intent);
	}
}

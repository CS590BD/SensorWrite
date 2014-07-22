//based upon: http://www.codeproject.com/Articles/797563/Creating-Charts-in-Android-using-the-AChartEngine

package group.seven.sensorwrite;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;

public class GraphingActivity extends Activity {

	private GraphicalView chart;
	private XYSeriesRenderer renderX, renderY, renderZ;
	private XYMultipleSeriesRenderer renderMulti;
	private XYSeries seriesX, seriesY, seriesZ;
	private XYMultipleSeriesDataset dataset;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_graphing);
	}

	@Override
	protected void onResume() {
		super.onResume();
		LinearLayout layout = (LinearLayout) findViewById(R.id.chart);
		if (chart == null) {
			initializeChart();
			addData();
			chart = ChartFactory.getLineChartView(this, dataset, renderMulti);
			layout.addView(chart);
		} else {
			chart.repaint();
		}
	}

	private void initializeChart() {
		renderX = new XYSeriesRenderer();
		renderY = new XYSeriesRenderer();
		renderZ = new XYSeriesRenderer();
		renderX.setColor(Color.RED);
		renderY.setColor(Color.GREEN);
		renderZ.setColor(Color.BLUE);
		renderMulti = new XYMultipleSeriesRenderer();
		renderMulti.addSeriesRenderer(renderX);
		renderMulti.addSeriesRenderer(renderY);
		renderMulti.addSeriesRenderer(renderZ);
		renderMulti.setPanEnabled(false);
		renderMulti.setYAxisMax(2);
		renderMulti.setYAxisMin(-2);
		seriesX = new XYSeries("X");
		seriesY = new XYSeries("Y");
		seriesZ = new XYSeries("Z");
		dataset = new XYMultipleSeriesDataset();
		dataset.addSeries(seriesX);
		dataset.addSeries(seriesY);
		dataset.addSeries(seriesZ);
	}

	private void addData() {
		
		/*
		String character = "S";
		RestfulGestureData record = new RestfulGestureData(GraphingActivity.this, character); //sets family internally
		record.tableName = "characters";
		record.row = GraphingActivity.this.getResources().getString(R.string.user);
		record.qualifier = character;
		record.method = "get";
		String url = record.toRestfulUrl();
		HttpAsyncTask task = new HttpAsyncTask(GraphingActivity.this, url);
		task.execute(record.method);
		Log.wtf("url", url);
		String data = task.getData();
		
		String[] lines = data.split("\n");
		for(String line : lines) {
			String[] values = line.split("\t");
			Double timestamp = Double.parseDouble(values[0]);
			Double x = Double.parseDouble(values[1]);
			Double y = Double.parseDouble(values[2]);
			Double z = Double.parseDouble(values[3]);
			seriesX.add(timestamp, x);
			seriesY.add(timestamp, y);
			seriesZ.add(timestamp, z);
		}
		*/
		

		seriesX.add(1405983851728d, 0.4192289);
		seriesX.add(1405983851729d, 0.7016753);
		seriesX.add(1405983851730d, 0.50137144);
		seriesX.add(1405983851731d, 0.35302472);
		seriesX.add(1405983851732d, 0.7544926);
		seriesX.add(1405983851733d, 0.82490706);
		seriesX.add(1405983851734d, 0.9199208);
		seriesX.add(1405983851735d, 0.35909843);

		seriesY.add(1405983851728d, 0.7823952);
		seriesY.add(1405983851729d, 0.57416534);
		seriesY.add(1405983851730d, 0.22668737);
		seriesY.add(1405983851731d, 0.27401525);
		seriesY.add(1405983851732d, 0.50155413);
		seriesY.add(1405983851733d, 0.398569);
		seriesY.add(1405983851734d, 0.20537454);
		seriesY.add(1405983851735d, 0.59514654);

		seriesZ.add(1405983851728d, 0.17223662);
		seriesZ.add(1405983851729d, 0.6850002);
		seriesZ.add(1405983851730d, 0.17167646);
		seriesZ.add(1405983851731d, 0.8227964);
		seriesZ.add(1405983851732d, 0.6553871);
		seriesZ.add(1405983851733d, 0.5550845);
		seriesZ.add(1405983851734d, 0.15228134);
		seriesZ.add(1405983851735d, 0.25429183);
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
	private void openWrite() {
		Intent intent = new Intent(GraphingActivity.this, MainActivity.class);
	    startActivity(intent);
	}
	private void openStorage() {
		Intent intent = new Intent(GraphingActivity.this, DataTrainingActivity.class);
	    startActivity(intent);
	}
}

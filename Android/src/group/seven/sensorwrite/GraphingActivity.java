//based upon: http://www.codeproject.com/Articles/797563/Creating-Charts-in-Android-using-the-AChartEngine
//get data from an intent: http://stackoverflow.com/questions/2091465/how-do-i-pass-data-between-activities-in-android

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
import android.widget.LinearLayout;

public class GraphingActivity extends Activity {	
	
	//internal charting
	private GraphicalView chart;
	private XYSeriesRenderer renderX, renderY, renderZ;
	private XYMultipleSeriesRenderer renderMulti;
	private XYSeries seriesX, seriesY, seriesZ;
	private XYMultipleSeriesDataset dataset;
	
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_graphing);
	}

	@Override
	protected void onResume() {
		super.onResume();
		initializeChart();
		addData();
		showChart();
	}
	private void addData() {
		Bundle extras = getIntent().getExtras();
	    String data = extras.getString("SELECTED_VALUE");
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
		Log.wtf("system.out", data);
	}
	private void showChart() {
		LinearLayout layout = (LinearLayout)findViewById(R.id.chart);
		chart = ChartFactory.getLineChartView(GraphingActivity.this, dataset, renderMulti);
		layout.addView(chart);

	}
}

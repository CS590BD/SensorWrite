package group.seven.sensorwrite;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import be.ac.ulg.montefiore.run.jahmm.Hmm;
import be.ac.ulg.montefiore.run.jahmm.ObservationVector;
import be.ac.ulg.montefiore.run.jahmm.OpdfMultiGaussianFactory;
import be.ac.ulg.montefiore.run.jahmm.io.ObservationSequencesReader;
import be.ac.ulg.montefiore.run.jahmm.io.ObservationVectorReader;
import be.ac.ulg.montefiore.run.jahmm.learn.BaumWelchLearner;
import be.ac.ulg.montefiore.run.jahmm.learn.KMeansLearner;

public class MainActivity extends Activity {
	
	String[] characters = {"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z","!",",","."};

	HashMap<String, Hmm<ObservationVector>> learnMap;

	//foreach file
		//if file has contents
			//train it
	public void train() {
		Log.wtf("system.out", "train()");
		learnMap = new HashMap<String, Hmm<ObservationVector>>();
		//foreach file
		for(String character : characters) {
			try {
				//if file has contents
				String directoryPath = Environment.getExternalStorageDirectory() + "/SensorWrite";
				String fileName = character + ".seq";
				String filePath = directoryPath + "/" + fileName;
				BufferedReader br = new BufferedReader(new FileReader(filePath));
				if (br.readLine() != null) { //file has contents
					Log.wtf("train()", "YES - " + fileName + " has content");
					//train it
					Boolean exception = false;
					int x = 10; //why 10? i think we are trying to read 10 lines from the file
					while(!exception && x > 0) {
						Log.wtf("train()", "entering while loop");
						try {
							OpdfMultiGaussianFactory initFactoryPunch = new OpdfMultiGaussianFactory(3); //3 dimensions because x y z
							Reader learnReaderPunch = new FileReader(new File (directoryPath, fileName));
							List<List<ObservationVector>> learnSequencesPunch = ObservationSequencesReader.readSequences(new ObservationVectorReader(), learnReaderPunch);
							learnReaderPunch.close();
							KMeansLearner<ObservationVector> kMeansLearnerPunch = new KMeansLearner<ObservationVector>(x, initFactoryPunch, learnSequencesPunch);
							// Create an estimation of the HMM (initHmm) using one iteration of the
							// k-Means algorithm
							Hmm<ObservationVector> initHmmPunch = kMeansLearnerPunch.iterate();
							// Use BaumWelchLearner to create the HMM (learntHmm) from initHmm
							BaumWelchLearner baumWelchLearnerPunch = new BaumWelchLearner();
							learnMap.put(character, baumWelchLearnerPunch.learn(initHmmPunch, learnSequencesPunch));
							exception=true;
							//System.out.println(x);
						} catch(Exception ex) {
							x--; //what happens in this exception block? Is the file missing lines?
							Log.wtf("exception", ex.getMessage());
						}
					}
				} else { 
					Log.wtf("train()", "NO - " + fileName + " does not have content");
				}
			} catch (IOException ex) {
				//this is prevented by file creation in splash screen
			}
		}
		TextView tv = (TextView)findViewById(R.id.lblMainX);
		tv.setText("trained");
	}
	
	private void test() {
		
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.wtf("system.out", "MainActivity loaded");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		train();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// inflate menu items for use in action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_activity_actions, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// handle presses on action bar items
		switch (item.getItemId()) {
		case R.id.action_graph:
			openGraph();
			return true;
		case R.id.action_edit:
			// do nothing - already here
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

	private void openGraph() {
		Intent intent = new Intent(MainActivity.this, HBaseRowActivity.class);
		startActivity(intent);
	}

	private void openStorage() {
		Intent intent = new Intent(MainActivity.this,
				DataTrainingActivity.class);
		startActivity(intent);
	}
}

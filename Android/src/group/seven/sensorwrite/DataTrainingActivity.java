package group.seven.sensorwrite;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class DataTrainingActivity extends Activity {
	private ConnectionServiceReceiver receiver;
	private StringBuilder receivedData;
	
	//buttons
	Button btnSave, btnDiscard;
	
	//labels
	TextView lblAccelerometerX, lblAccelerometerY, lblAccelerometerZ;
	TextView lblA, lblB, lblC, lblD, lblE, lblF, lblG, lblH, lblI, lblJ, lblK, lblL, lblM, 
		lblN, lblO, lblP, lblQ, lblR, lblS, lblT, lblU, lblV, lblW, lblX, lblY, lblZ,
		lblExclamationPoint, lblPeriod, lblComma, lblQuestionMark;
	
	//reference the current and previous characters
	TextView selectedCharacter;
	TextView previousCharacter;
	
	//for testing with fake data (click 3 times to trigger)
	int questionMarkClickCount = 0;
	
	/**
	 * BROADCAST RECEIVER
	 * sender: ConnectionService.class
	 */
	public class ConnectionServiceReceiver extends BroadcastReceiver {
		public static final String PROCESS_RESPONSE = "group.seven.sensorwrite.intent.action.PROCESS_RESPONSE";
		@Override
		public void onReceive(Context context, Intent intent) {
			String X = intent.getStringExtra(ConnectionService.X);
			String Y = intent.getStringExtra(ConnectionService.Y);
			String Z = intent.getStringExtra(ConnectionService.Z);
			long timestamp = intent.getLongExtra("TIMESTAMP", System.currentTimeMillis());
			lblAccelerometerX.setText(X);
			lblAccelerometerY.setText(Y);
			lblAccelerometerZ.setText(Z);
			receivedData.append(timestamp + "\t" + X + "\t" + Y + "\t" + Z + "\n");
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.wtf("system.out", "DataTrainingActivity loaded");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_datatraining);
		
		receivedData = new StringBuilder();
		registerUI();
		disableButtons();

		/*
		Intent intent = new Intent(DataTrainingActivity.this, ConnectionService.class);
		IntentFilter filter = new IntentFilter(ConnectionServiceReceiver.PROCESS_RESPONSE);
		filter.addCategory(Intent.CATEGORY_DEFAULT);
		receiver = new ConnectionServiceReceiver();
		registerReceiver(receiver, filter);
		startService(intent);
		*/
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
				openGraph();
				return true;
			case R.id.action_edit:
				openWrite();
				return true;
			case R.id.action_storage:
				//do nothing - already here
				return true;
			case R.id.action_settings:
				//not implemented
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	private void openGraph() {
		Intent intent = new Intent(DataTrainingActivity.this, HBaseRowActivity.class);
	    startActivity(intent);
	}
	private void openWrite() {
		Intent intent = new Intent(DataTrainingActivity.this, MainActivity.class);
	    startActivity(intent);
	}
	
	/**
	 * WRITE A SEQUENCE FILE
	 * @param file
	 * @param value
	 */
	private void writeSequence(File file, String value) {
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
				contents += "[ " + x + "\t" + y + "\t" + z + " ]  ; ";
		    }
		    filewriter.write(contents + "\n");
		    filewriter.close();
		} catch (IOException exception) {
			//do nothing?
		}
	}
	
	/**
	 * REGISTER UI
	 * 
	 * Creates UI objects and register their listeners.
	 */
	private void registerUI() {
		//buttons
		btnSave = (Button)findViewById(R.id.btnSave);
		btnDiscard = (Button)findViewById(R.id.btnDiscard);
		btnSave.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//if data is captured, try to store it
				if(receivedData.toString().length() > 0) {
					Context context = DataTrainingActivity.this;
					String method = "post";
					String table = "characters";
					String row = getResources().getString(R.string.user);
					String family;
					String qualifier = selectedCharacter.getText().toString();
					String value = receivedData.toString();
					//set family
					char c = qualifier.charAt(0);
					if(Character.isUpperCase(c)) {
						family = "capital";
					} else if (Character.isLowerCase(c)) {
						family = "lowercase";
					} else if (Character.isDigit(c)) {
						family = "numeric";
					} else {
						family = "punctuation";
					}
					writeSequence(new File(qualifier + ".seq"), value);
					String url = new RestfulGestureData(context, method, table, row, family, qualifier, value).toRestfulUrl();
					//new HttpAsyncTask(context, url).execute(method, value);
					Toast.makeText(DataTrainingActivity.this, "file saved", Toast.LENGTH_SHORT).show();;
					Log.wtf("url", url);
				}
			}
		});
		btnDiscard.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				resetState();
			}
		});
		
		//labels
		lblAccelerometerX = (TextView)findViewById(R.id.lblAccelerationX);
		lblAccelerometerY = (TextView)findViewById(R.id.lblAccelerationY);
		lblAccelerometerZ = (TextView)findViewById(R.id.lblAccelerationZ);
		lblA = (TextView)findViewById(R.id.lblA);
		lblB = (TextView)findViewById(R.id.lblB);
		lblC = (TextView)findViewById(R.id.lblC);
		lblD = (TextView)findViewById(R.id.lblD);
		lblE = (TextView)findViewById(R.id.lblE);
		lblF = (TextView)findViewById(R.id.lblF);
		lblG = (TextView)findViewById(R.id.lblG);
		lblH = (TextView)findViewById(R.id.lblH);
		lblI = (TextView)findViewById(R.id.lblI);
		lblJ = (TextView)findViewById(R.id.lblJ);
		lblK = (TextView)findViewById(R.id.lblK);
		lblL = (TextView)findViewById(R.id.lblL);
		lblM = (TextView)findViewById(R.id.lblM);
		lblN = (TextView)findViewById(R.id.lblN);
		lblO = (TextView)findViewById(R.id.lblO);
		lblP = (TextView)findViewById(R.id.lblP);
		lblQ = (TextView)findViewById(R.id.lblQ);
		lblR = (TextView)findViewById(R.id.lblR);
		lblS = (TextView)findViewById(R.id.lblS);
		lblT = (TextView)findViewById(R.id.lblT);
		lblU = (TextView)findViewById(R.id.lblU);
		lblV = (TextView)findViewById(R.id.lblV);
		lblW = (TextView)findViewById(R.id.lblW);
		lblX = (TextView)findViewById(R.id.lblX);
		lblY = (TextView)findViewById(R.id.lblY);
		lblZ = (TextView)findViewById(R.id.lblZ);
		lblExclamationPoint = (TextView)findViewById(R.id.lblExclamationPoint);
		lblPeriod = (TextView)findViewById(R.id.lblPeriod);
		lblComma = (TextView)findViewById(R.id.lblComma);
		lblQuestionMark = (TextView)findViewById(R.id.lblQuestionMark);	
		lblA.setOnClickListener(lblClickListener);
		lblB.setOnClickListener(lblClickListener);
		lblC.setOnClickListener(lblClickListener);
		lblD.setOnClickListener(lblClickListener);
		lblE.setOnClickListener(lblClickListener);
		lblF.setOnClickListener(lblClickListener);
		lblG.setOnClickListener(lblClickListener);
		lblH.setOnClickListener(lblClickListener);
		lblI.setOnClickListener(lblClickListener);
		lblJ.setOnClickListener(lblClickListener);
		lblK.setOnClickListener(lblClickListener);
		lblL.setOnClickListener(lblClickListener);
		lblM.setOnClickListener(lblClickListener);
		lblN.setOnClickListener(lblClickListener);
		lblO.setOnClickListener(lblClickListener);
		lblP.setOnClickListener(lblClickListener);
		lblQ.setOnClickListener(lblClickListener);
		lblR.setOnClickListener(lblClickListener);
		lblS.setOnClickListener(lblClickListener);
		lblT.setOnClickListener(lblClickListener);
		lblU.setOnClickListener(lblClickListener);
		lblV.setOnClickListener(lblClickListener);
		lblW.setOnClickListener(lblClickListener);
		lblX.setOnClickListener(lblClickListener);
		lblY.setOnClickListener(lblClickListener);
		lblZ.setOnClickListener(lblClickListener);
		lblExclamationPoint.setOnClickListener(lblClickListener);
		lblPeriod.setOnClickListener(lblClickListener);
		lblComma.setOnClickListener(lblClickListener);
		lblQuestionMark.setOnClickListener(questionMarkClickListener);
	}
	
	OnClickListener lblClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			goToAndSelect(v);
			questionMarkClickCount = 0;
		}
	};
	OnClickListener questionMarkClickListener = new OnClickListener() {
		// Three clicks on the ? character calls fake testing data
		@Override
		public void onClick(View v) {
			goToAndSelect(v);
			if(questionMarkClickCount < 3) {
				questionMarkClickCount++;
				if(!selectedCharacter.getText().toString().equals("?")) {
					goToAndSelect(v);
				}
			} else {
				makeFakeData();
			}
		}
	};
	/**
	 * Set current character, set previous character, set background colors
	 * @param v
	 */
	private void goToAndSelect(View v) {
		enableButtons();
		if(previousCharacter != null) {
			previousCharacter.setBackgroundColor(Color.TRANSPARENT);
		}
		selectedCharacter = (TextView)v;
		selectedCharacter.setBackgroundColor(Color.GRAY);
		previousCharacter = selectedCharacter;
	}
	/**
	 * Generates fake testing data. Click ? character three times to trigger.
	 * @return
	 */
	private void makeFakeData() {
		receivedData = new StringBuilder();
		String x = "";
		String y = "";
		String z = "";
		long timestamp = System.currentTimeMillis();
		for(int i = 0; i < 40; i++) {
			x = Float.toString(new Random().nextFloat());
			y = Float.toString(new Random().nextFloat());
			z = Float.toString(new Random().nextFloat());
			timestamp += 20; //store 20 ms difference each time (makes better graphs)
			receivedData.append(timestamp);
			receivedData.append("\t" + x);
			receivedData.append("\t" + y);
			receivedData.append("\t" + z);
			receivedData.append("\n");
		}
		lblAccelerometerX.setText(x);
		lblAccelerometerY.setText(y);
		lblAccelerometerZ.setText(z);
		enableButtons();
	}
	public void disableButtons() {
		receivedData = new StringBuilder();
		btnSave.setEnabled(false);
		btnDiscard.setEnabled(false);
	}
	public void enableButtons() {
		if(receivedData.toString().length() > 0) {
			btnSave.setEnabled(true);
			btnDiscard.setEnabled(true);
		}
	}
	private void resetState() {
		disableButtons();
		showMessage("Hold left simple key to record");
		receivedData = new StringBuilder();
	}
	private void showMessage(String message) {
		lblAccelerometerX.setText(message);
		lblAccelerometerY.setText("");
		lblAccelerometerZ.setText("");
	}
}

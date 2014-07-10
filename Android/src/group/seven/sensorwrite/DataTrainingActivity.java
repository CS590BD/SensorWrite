package group.seven.sensorwrite;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class DataTrainingActivity extends Activity {
	
	private static final String GLASSFISH_IP = "10.205.1.232";
	
	private ConnectionServiceReceiver receiver;
	TextView lblAccelerometerX, lblAccelerometerY, lblAccelerometerZ;
	TextView lblA, lblB, lblC, lblD, lblE, lblF, lblG, lblH, lblI, lblJ, lblK, lblL, lblM, 
		lblN, lblO, lblP, lblQ, lblR, lblS, lblT, lblU, lblV, lblW, lblX, lblY, lblZ,
		lblExclamationPoint, lblPeriod, lblComma, lblQuestionMark;
	Button btnSave, btnDiscard;
	
	// used to toggle the selected character view
	TextView selectedCharacter;
	TextView previousCharacter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.wtf("system.out", "DataTrainingActivity loaded");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_datatraining);
		
		registerUI();

		Intent intent = new Intent(DataTrainingActivity.this, ConnectionService.class);
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
			long timestamp = intent.getLongExtra("TIMESTAMP", System.currentTimeMillis());
			lblAccelerometerX.setText(X);
			lblAccelerometerY.setText(Y);
			lblAccelerometerZ.setText(Z);
		}
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
			case R.id.action_storage:
				return true;
			case R.id.action_settings:
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	private void registerUI() {
		//buttons
		btnSave = (Button)findViewById(R.id.btnSave);
		btnDiscard = (Button)findViewById(R.id.btnDiscard);
		
		//button handlers
		btnSave.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.wtf("system.out", "save clicked - implement your own data post!");
				/*
				String tableName = "characters";
				String row = selectedCharacter.getText().toString();
				familY?
				qualifier?
				String url = "http://" + GLASSFISH_IP + ":8080/group.seven/rest/hbase/insert/tablename/row/family/qualifier";
				try {
					//HTTP.post(url, message)
					lblAccelerometerX.setText("data saved");
				} catch (Exception ex) {
					lblAccelerometerX.setText("Exception: " + ex.getMessage());
				}
				*/
			}
		});
		btnDiscard.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				lblAccelerometerX.setText("Hold left simple key to record");
				lblAccelerometerY.setText("");
				lblAccelerometerZ.setText("");
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
		lblQuestionMark.setOnClickListener(lblClickListener);

	}
	OnClickListener lblClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if(previousCharacter != null) {
				previousCharacter.setBackgroundColor(Color.TRANSPARENT);
			}
			selectedCharacter = (TextView)v;
			selectedCharacter.setBackgroundColor(Color.GRAY);
			previousCharacter = selectedCharacter;
		}
	};
}

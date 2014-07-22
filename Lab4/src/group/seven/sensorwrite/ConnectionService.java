package group.seven.sensorwrite;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.IntentService;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;
import android.util.SparseArray;


public class ConnectionService extends IntentService implements BluetoothAdapter.LeScanCallback{
	
	public static final String X = "X";
	public static final String Y = "Y";
	public static final String Z = "Z";
	public static final long TIMESTAMP = System.currentTimeMillis();

	private static final String GLASSFISH_IP = "10.10.89.140";
    private static final String DEVICE_NAME = "SensorTag";

    /* Acceleromter configuration servcie */
    private static final UUID ACCELEROMETER_SERVICE = UUID.fromString("f000aa10-0451-4000-b000-000000000000");
    private static final UUID ACCELEROMETER_DATA_CHAR = UUID.fromString("f000aa11-0451-4000-b000-000000000000");
    private static final UUID ACCELEROMETER_CONFIG_CHAR = UUID.fromString("f000aa12-0451-4000-b000-000000000000");
    private static final UUID ACCELEROMETER_PERIOD_CHAR = UUID.fromString("f000aa13-0451-4000-b000-000000000000");
    
    /* Client Configuration Descriptor */
    private static final UUID CONFIG_DESCRIPTOR = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

    private BluetoothAdapter mBluetoothAdapter;
    private SparseArray<BluetoothDevice> mDevices;
    private BluetoothGatt mConnectedGatt;
	
	public ConnectionService() {
		super("ConnectionService");
	}
	
	String growingData = "";

	@Override
		public void onCreate() {
			super.onCreate();
			BluetoothManager manager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
	        mBluetoothAdapter = manager.getAdapter();
	        mDevices = new SparseArray<BluetoothDevice>();
		}

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.wtf("invoke method", "onHandleIntent");
		startScan();
	}

	@Override
	public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
		Log.wtf("invoke method", "onLeScan");
		Log.wtf("device found", device.getName() + " @ " + rssi);
        if (DEVICE_NAME.equals(device.getName())) {
            mDevices.put(device.hashCode(), device);
            mConnectedGatt = device.connectGatt(this, false, mGattCallback);
        }
	}

    private void startScan() {
    	Log.wtf("invoke method", "startScan");
        mBluetoothAdapter.startLeScan(this);
    }
	/*
     * In this callback, we've created a bit of a state machine to enforce that only
     * one characteristic be read or written at a time until all of our sensors
     * are enabled and we are registered to get notifications.
     */
    private BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
    	
        /* State Machine Tracking */
        private int mState = 0;
        private void reset() { mState = 0; }
        private void advance() { mState++; }

        /*
         * Send an enable command to each sensor by writing a configuration
         * characteristic.  This is specific to the SensorTag to keep power
         * low by disabling sensors you aren't using.
         */
        private void enableNextSensor(BluetoothGatt gatt) {
        	Log.wtf("invoke method", "enableNextSensor");
            BluetoothGattCharacteristic characteristic;
            switch (mState) {
                case 0:
                    Log.wtf("enable accelerometer","ACCELEROMETER_CONFIG_CHAR");
                    characteristic= gatt.getService(ACCELEROMETER_SERVICE).getCharacteristic(ACCELEROMETER_CONFIG_CHAR);
                    characteristic.setValue(new byte[]{0x01});
                    break;
                case 1:
                    Log.wtf("enable accelerometer","ACCELEROMETER_PERIOD_CHAR");
                    characteristic= gatt.getService(ACCELEROMETER_SERVICE).getCharacteristic(ACCELEROMETER_PERIOD_CHAR);
                    characteristic.setValue(new byte[]{(byte)20});
                    break;
                default:
                    Log.wtf("sensors enabled", "mState = " + mState);
                    return;
            }
            gatt.writeCharacteristic(characteristic);
        }
        
        private void readNextSensor(BluetoothGatt gatt) {
        	Log.wtf("invoke method", "readNextSensor");
            BluetoothGattCharacteristic characteristic;
            switch (mState) {
                case 0:
                    Log.wtf("read accelerometer", "ACCELEROMETER_DATA_CHAR");
                    characteristic = gatt.getService(ACCELEROMETER_SERVICE).getCharacteristic(ACCELEROMETER_DATA_CHAR);
                    break;

                default:
                    Log.wtf("sensors read", "mState = " + mState);
                    return;
            }
            gatt.readCharacteristic(characteristic);
        }
        
       /* * Enable notification of changes on the data characteristic for each sensor
        * by writing the ENABLE_NOTIFICATION_VALUE flag to that characteristic's
        * configuration descriptor.
        */
       private void setNotifyNextSensor(BluetoothGatt gatt) {
    	   Log.wtf("invoke method", "setNotifyNextSensor");
           BluetoothGattCharacteristic characteristic;
           switch (mState) {
               case 0:
                   Log.wtf("notify accelerometer","ACCELEROMETER_DATA_CHAR");
                   characteristic = gatt.getService(ACCELEROMETER_SERVICE).getCharacteristic(ACCELEROMETER_DATA_CHAR);
                   break;
               default:
                   Log.wtf("sensors notified", "mState = " + mState);
                   return;
           }

           //Enable local notifications
           gatt.setCharacteristicNotification(characteristic, true);
           //Enabled remote notifications
           BluetoothGattDescriptor desc = characteristic.getDescriptor(CONFIG_DESCRIPTOR);
           desc.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
           gatt.writeDescriptor(desc);
       }
       
       @Override
       public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
           Log.wtf("invoke method", "onConnectionStateChange");
           Log.wtf("connection state change", status+" -> "+connectionState(newState));
           if (status == BluetoothGatt.GATT_SUCCESS && newState == BluetoothProfile.STATE_CONNECTED) {
               //Once successfully connected, we must next discover all the services on the
               //device before we can read and write their characteristics.
               gatt.discoverServices();
               //mHandler.sendMessage(Message.obtain(null, MSG_PROGRESS, "Discovering Services..."));
           } else if (status == BluetoothGatt.GATT_SUCCESS && newState == BluetoothProfile.STATE_DISCONNECTED) {
        	   try {
        		   String url = "http://" + GLASSFISH_IP + ":8080/group.seven/rest/hbase/insert/a0_accelerometer/letters/E/E1";
        		   HTTP.post(url, growingData);
        	   } catch (Exception ex) {
        		   Log.wtf("data failed to send", ex.getMessage());
        	   }
        	   
        	   // If at any point we disconnect, send a message to clear the weather values out of the UI
        	   // mHandler.sendEmptyMessage(MSG_CLEAR);
           } else if (status != BluetoothGatt.GATT_SUCCESS) {
               //If there is a failure at any stage, simply disconnect
               gatt.disconnect();
           }
       }

       @Override
       public void onServicesDiscovered(BluetoothGatt gatt, int status) {
           Log.wtf("invoke method", "onServicesDiscovered");
          // mHandler.sendMessage(Message.obtain(null, MSG_PROGRESS, "Enabling Sensors..."));
           /*
            * With services discovered, we are going to reset our state machine and start
            * working through the sensors we need to enable
            */
           reset();
           enableNextSensor(gatt);
       }

       @Override
       public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
    	   Log.wtf("invoke method", "onCharacteristicRead");
           //For each read, pass the data up to the UI thread to update the display
           if (ACCELEROMETER_DATA_CHAR.equals(characteristic.getUuid())) {
        	   Log.wtf("update the ui", "update the ui");
               //mHandler.sendMessage(Message.obtain(null, MSG_ACCELEROMETER, characteristic));
           }
           //After reading the initial value, next we enable notifications
           setNotifyNextSensor(gatt);
       }

       @Override
       public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
    	   Log.wtf("invoke method", "onCharacteristicWrite");
           //After writing the enable flag, next we read the initial value
           readNextSensor(gatt);
       }

       @Override
       public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
    	   Log.wtf("invoke method", "onCharacteristicChanged");
           /*
            * After notifications are enabled, all updates from the device on characteristic
            * value changes will be posted here.  Similar to read, we hand these up to the
            * UI thread to update the display.
            */
    	   if (ACCELEROMETER_DATA_CHAR.equals(characteristic.getUuid())) {
              updateAccelerometerCals(characteristic);
           }
       }

       @Override
       public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
    	   Log.wtf("invoke method", "onDescriptorWrite");
    	   //Once notifications are enabled, we move to the next sensor and start over with enable
           advance();
           enableNextSensor(gatt);
       }

       @Override
       public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
    	   Log.wtf("invoke method", "onReadRemoteRssi");
           Log.wtf("remote rssi", Integer.toString(rssi));
       }

       private String connectionState(int status) {
    	   Log.wtf("invoke method", "connectionState");
           switch (status) {
               case BluetoothProfile.STATE_CONNECTED:
                   return "Connected";
               case BluetoothProfile.STATE_DISCONNECTED:
                   return "Disconnected";
               case BluetoothProfile.STATE_CONNECTING:
                   return "Connecting";
               case BluetoothProfile.STATE_DISCONNECTING:
                   return "Disconnecting";
               default:
                   return String.valueOf(status);
           }
       }
   };
 
   private void updateAccelerometerCals(BluetoothGattCharacteristic characteristic) {
       Float[] values = SensorTagData.extractAccelerometerReading(characteristic, 0);
       Log.wtf("values", "x="+values[0].toString() + ", y=" + values[1].toString() + ", z=" + values[2].toString());
       Date d = new Date();
       String string = "\n"+d.toString() 
    		   + "\t" + String.valueOf(values[0]) 
    		   + "\t" + String.valueOf(values[1]) 
    		   +"\t" + String.valueOf(values[2]);
       SaveData(string);
   }
   
   private void SaveData(String string) {
	   Log.wtf("invoke method", "SaveData");
	   growingData += string;
    }
}

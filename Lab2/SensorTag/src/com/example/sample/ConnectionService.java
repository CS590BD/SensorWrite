package com.example.sample;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.UUID;

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
import android.util.SparseArray;


public class ConnectionService extends IntentService implements BluetoothAdapter.LeScanCallback {
	
    private BluetoothAdapter mBluetoothAdapter;
    private SparseArray<BluetoothDevice> mDevices;
	private BluetoothGatt mConnectedGatt;
	
	private static final String TAG = "BluetoothGattActivity";
    private static final String DEVICE_NAME = "SensorTag";

    /* Humidity Sensor */
    private static final UUID HUMIDITY_SERVICE = UUID.fromString("f000aa20-0451-4000-b000-000000000000");
    private static final UUID HUMIDITY_DATA = UUID.fromString("f000aa21-0451-4000-b000-000000000000");
    private static final UUID HUMIDITY_CONF = UUID.fromString("f000aa22-0451-4000-b000-000000000000");
    private static final UUID HUMIDITY_PERIOD = UUID.fromString("f000aa23-0451-4000-b000-000000000000");
    
    /* IR Temperature Sensor */
    private static final UUID IR_TEMPERATURE_SERVICE = UUID.fromString("f000aa00-0451-4000-b000-000000000000");
    private static final UUID IR_TEMPERATURE_DATA = UUID.fromString("f000aa01-0451-4000-b000-000000000000");
    private static final UUID IR_TEMPERATURE_CONF = UUID.fromString("f000aa02-0451-4000-b000-000000000000");
    private static final UUID IR_TEMPERATURE_PERIOD = UUID.fromString("f000aa03-0451-4000-b000-000000000000");
    
    /* Accelerometer Sensor */
    private static final UUID ACCELEROMETER_SERVICE = UUID.fromString("f000aa10-0451-4000-b000-000000000000");
    private static final UUID ACCELEROMETER_DATA = UUID.fromString("f000aa11-0451-4000-b000-000000000000");
    private static final UUID ACCELEROMETER_CONF = UUID.fromString("f000aa12-0451-4000-b000-000000000000");
    private static final UUID ACCELEROMETER_PERIOD = UUID.fromString("f000aa13-0451-4000-b000-000000000000");
    
    /* Gyroscope Configuration service */
    private static final UUID GYROSCOPE_SERVICE = UUID.fromString("f000aa50-0451-4000-b000-000000000000");
    private static final UUID GYROSCOPE_DATA = UUID.fromString("f000aa51-0451-4000-b000-000000000000");
    private static final UUID GYROSCOPE_CONF = UUID.fromString("f000aa52-0451-4000-b000-000000000000");
    private static final UUID GYROSCOPE_PERIOD = UUID.fromString("f000aa53-0451-4000-b000-000000000000");
    
    /* Client Configuration Descriptor */
    private static final UUID CONFIG_DESCRIPTOR = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
   
	
	public ConnectionService()
	{
		super("ConnectionService");
	}

	
	@Override
	public void onCreate()
	{
		super.onCreate();
		BluetoothManager manager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        mBluetoothAdapter = manager.getAdapter();
        mDevices = new SparseArray<BluetoothDevice>();
	}
	
	
	@Override
	protected void onHandleIntent(Intent intent)
	{
		mBluetoothAdapter.startLeScan(this);
	}

	@Override
	public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord)
	{
        /*
         * We are looking for SensorTag devices only, so validate the name
         * that each device reports before adding it to our collection
         */
        if (DEVICE_NAME.equals(device.getName()))
        {
            mDevices.put(device.hashCode(), device);
            mConnectedGatt = device.connectGatt(this, false, mGattCallback);
        }
	}

    
    private BluetoothGattCallback mGattCallback = new BluetoothGattCallback()
    {
    	/* State Machine Tracking */
        private int mState = 0;
        private void reset() { mState = 0; }
        private void advance() { mState++; }
        
        private void setNextSensorCharacteristic(BluetoothGatt gatt)
        {
        	BluetoothGattCharacteristic characteristic;
        	
        	switch (mState)
        	{
	        	case 0:
	        		// enable Humidity Sensor
	            	characteristic = gatt.getService(HUMIDITY_SERVICE)
	        			.getCharacteristic(HUMIDITY_CONF);
	            	characteristic.setValue(new byte[] { 0x01 });
	            	break;
	        	case 1:
	        		// enable IR Temperature Sensor
	            	characteristic = gatt.getService(IR_TEMPERATURE_SERVICE)
	            		.getCharacteristic(IR_TEMPERATURE_CONF);
	            	characteristic.setValue(new byte[] { 0x01 });
	        		break;
	        	case 2:
	        		// enable Accelerometer Sensor
	            	characteristic = gatt.getService(ACCELEROMETER_SERVICE)
	        			.getCharacteristic(ACCELEROMETER_CONF);
	            	characteristic.setValue(new byte[] { 0x01 });
	        		break;
	        	case 3:
	            	// enable Gyroscope Sensor (configured for X:Y:Z)
	            	characteristic = gatt.getService(GYROSCOPE_SERVICE)
	        			.getCharacteristic(GYROSCOPE_CONF);
	            	characteristic.setValue(new byte[] { 0x07 });
	        		break;
	        	case 4:
	        		// set Humidity Sensor Period to 500ms
	            	characteristic = gatt.getService(HUMIDITY_SERVICE)
	        			.getCharacteristic(HUMIDITY_PERIOD);
	            	characteristic.setValue(new byte[] { 0x32 });
	        		break;
	        	case 5:
	        		// set IR Temperature Sensor Period to 500ms
	            	characteristic = gatt.getService(IR_TEMPERATURE_SERVICE)
	        			.getCharacteristic(IR_TEMPERATURE_PERIOD);
	            	characteristic.setValue(new byte[] { 0x32 });
	        		break;
	        	case 6:
	        		// set Accelerometer Sensor Period to 500ms
	            	characteristic = gatt.getService(ACCELEROMETER_SERVICE)
	        			.getCharacteristic(ACCELEROMETER_PERIOD);
	            	characteristic.setValue(new byte[] { 0x32 });
	        		break;
	        	case 7:
	        		// set Gyroscope Sensor Period to 500ms
	            	characteristic = gatt.getService(GYROSCOPE_SERVICE)
	        			.getCharacteristic(GYROSCOPE_PERIOD);
	            	characteristic.setValue(new byte[] { 0x32 });
	        		break;
            	default:
            		return;
        	}
        	gatt.writeCharacteristic(characteristic);
        }
   
        
       /* * Enable notification of changes on the data characteristic for each sensor
        * by writing the ENABLE_NOTIFICATION_VALUE flag to that characteristic's
        * configuration descriptor.
        */
       private void enableNextSensorNotification(BluetoothGatt gatt)
       {
    	   BluetoothGattCharacteristic characteristic;
    	   
    	   switch (mState) 
    	   {
	    	   case 0:
	    		   characteristic = gatt.getService(HUMIDITY_SERVICE)
	    		   		.getCharacteristic(HUMIDITY_DATA);
	    		   break;
	    	   case 1:
	    		   characteristic = gatt.getService(IR_TEMPERATURE_SERVICE)
   		   				.getCharacteristic(IR_TEMPERATURE_DATA);
	    		   break;
	    	   case 2:
	    		   characteristic = gatt.getService(ACCELEROMETER_SERVICE)
   		   				.getCharacteristic(ACCELEROMETER_DATA);
	    		   break;
	    	   case 3:
	    		   characteristic = gatt.getService(GYROSCOPE_SERVICE)
   		   				.getCharacteristic(GYROSCOPE_DATA);
	    		   break;
	    	   default:
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
       public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState)
       {
           if (status == BluetoothGatt.GATT_SUCCESS && newState == BluetoothProfile.STATE_CONNECTED)
           {
               /*
                * Once successfully connected, we must next discover all the services on the
                * device before we can read and write their characteristics.
                */
               gatt.discoverServices();
           }
           else if (status != BluetoothGatt.GATT_SUCCESS)
           {
               /*
                * If there is a failure at any stage, simply disconnect
                */
               gatt.disconnect();
           }
       }
       

       @Override
       public void onServicesDiscovered(BluetoothGatt gatt, int status)
       {
    	   reset();
           setNextSensorCharacteristic(gatt);
       }
       

       @Override
       public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status)
       {
       }
       

       @Override
       public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status)
       {
           //After writing to a sensor characteristic, we move on to enable data notification on the sensor
    	   enableNextSensorNotification(gatt);
       }
       
       
       @Override
       public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status)
       {
    	   advance();
    	   setNextSensorCharacteristic(gatt);
       }
       

       @Override
       public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic)
       {
           /*
            * After notifications are enabled, all updates from the device on characteristic
            * value changes will be posted here.
            */
    	   if (HUMIDITY_DATA.equals(characteristic.getUuid()))
    	   {
    		   double humidity = SensorTagData.extractRelativeHumidity(characteristic);
    		   Date d = new Date();
    		   SaveData(d.toString() + ", Relative Humidity (%RH): " + String.valueOf(humidity) + "\n");
    	   }
    	   if (IR_TEMPERATURE_DATA.equals(characteristic.getUuid()))
    	   {
    		   double ambient = SensorTagData.extractAmbientTemperature(characteristic);
    		   double target = SensorTagData.extractTargetTemperature(characteristic, ambient);
    		   Date d = new Date();
    		   SaveData(d.toString() + ", Ambient Temperature (°C): " + String.valueOf(ambient) + "\n");
    		   SaveData(d.toString() + ", Object Temperature (°C): " + String.valueOf(target) + "\n");
    	   }
    	   if (ACCELEROMETER_DATA.equals(characteristic.getUuid()))
    	   {
    		   double[] acceleration = SensorTagData.extractAccelerometerXYZ(characteristic);
    		   Date d = new Date();
    		   SaveData(d.toString() + ", Acceleration, X:Y:Z (g): " + String.valueOf(acceleration[0]) 
    				   + ", " + String.valueOf(acceleration[1]) + ", " + String.valueOf(acceleration[2]) + "\n");
    	   }
    	   if (GYROSCOPE_DATA.equals(characteristic.getUuid()))
    	   {
    		   float[] orientation = SensorTagData.extractGyroscopeXYZ(characteristic);
    		   Date d = new Date();
    		   SaveData(d.toString() + ", Orientation, X:Y:Z (deg/s): " + String.valueOf(orientation[0]) 
    				   + ", " + String.valueOf(orientation[1]) + ", " + String.valueOf(orientation[2]) + "\n");
    	   }
       }
   };
   
   
   private void SaveData(String string)
   {
        File sdCard = Environment.getExternalStorageDirectory();
        File directory = new File (sdCard.getAbsolutePath() + "/Data");
        if (!directory.exists())
        {
        	directory.mkdirs();
        }
        String fname = "sensor.txt";
        File file = new File (directory, fname);
        
        try
        {
	        if(!file.exists())
	        {
	            file.createNewFile();
	        }
	       FileOutputStream out = new FileOutputStream(file,true);
	       out.write(string.getBytes());
	       out.flush();
	       out.close();
        }
        catch (Exception e)
        {
               e.printStackTrace();
        }
    }
}





package group.seven.sensorwrite;

import group.seven.sensorwrite.DataTrainingActivity.ConnectionServiceReceiver;
import group.seven.sensorwrite.SensorTagData.SimpleKeysStatus;

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
import android.util.Log;
import android.util.SparseArray;

public class ConnectionService extends IntentService implements
		BluetoothAdapter.LeScanCallback {

	//intent extras
	public static final String X = "X";
	public static final String Y = "Y";
	public static final String Z = "Z";
	public static final long TIMESTAMP = System.currentTimeMillis();
	public static final String DELETE_COMMAND = "DELETE_COMMAND";
	public static final String TEST_COMMAND = "TEST_COMMAND";

	private BluetoothAdapter mBluetoothAdapter;
	private SparseArray<BluetoothDevice> mDevices;
	private BluetoothGatt mConnectedGatt;
	private static final String DEVICE_NAME = "SensorTag";

	/* Accelerometer Sensor */
	private static final UUID ACCELEROMETER_SERVICE = UUID.fromString("f000aa10-0451-4000-b000-000000000000");
	private static final UUID ACCELEROMETER_DATA = UUID.fromString("f000aa11-0451-4000-b000-000000000000");
	private static final UUID ACCELEROMETER_CONF = UUID.fromString("f000aa12-0451-4000-b000-000000000000");
	private static final UUID ACCELEROMETER_PERIOD = UUID.fromString("f000aa13-0451-4000-b000-000000000000");

	/* Simple Keys */
	private static final UUID SIMPLE_KEYS_SERVICE = UUID.fromString("0000ffe0-0000-1000-8000-00805f9b34fb");
	private static final UUID SIMPLE_KEYS_DATA = UUID.fromString("0000ffe1-0000-1000-8000-00805f9b34fb");

	/* Client Configuration Descriptor */
	private static final UUID CONFIG_DESCRIPTOR = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

	private SimpleKeysStatus state;
	protected Object log;

	public ConnectionService() {
		super("ConnectionService");
	}

	@Override
	public void onCreate() {
		Log.wtf("system.out", "ConnectionService loaded");
		super.onCreate();
		BluetoothManager manager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
		mBluetoothAdapter = manager.getAdapter();
		mDevices = new SparseArray<BluetoothDevice>();
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		mBluetoothAdapter.startLeScan(this);
	}

	@Override
	public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
		/*
		 * We are looking for SensorTag devices only, so validate the name that
		 * each device reports before adding it to our collection
		 */
		if (DEVICE_NAME.equals(device.getName())) {
			mDevices.put(device.hashCode(), device);
			mConnectedGatt = device.connectGatt(this, false, mGattCallback);
		}
	}

	private BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
		/* State Machine Tracking */
		private int mState = 0;
		private void reset() { mState = 0; }
		private void advance() { mState++; }

		private void setNextSensorCharacteristic(BluetoothGatt gatt) {
			BluetoothGattCharacteristic characteristic;

			switch (mState) {
			case 0:
				// enable Accelerometer Sensor
				characteristic = gatt.getService(ACCELEROMETER_SERVICE).getCharacteristic(ACCELEROMETER_CONF);
				characteristic.setValue(new byte[] { 0x01 });
				break;
			case 1:
				characteristic = gatt.getService(ACCELEROMETER_SERVICE).getCharacteristic(ACCELEROMETER_PERIOD);
				characteristic.setValue(new byte[] { (byte) 10 });
				//above, 10 seems close or same as 20, and both seem faster than 5, but 40 slows down more like 5
				break;
			default:
				return;
			}
			gatt.writeCharacteristic(characteristic);
		}

		/*
		 *  * Enable notification of changes on the data characteristic for each
		 * sensor by writing the ENABLE_NOTIFICATION_VALUE flag to that
		 * characteristic's configuration descriptor.
		 */
		private void enableNextSensorNotification(BluetoothGatt gatt) {
			BluetoothGattCharacteristic characteristic;

			switch (mState) {
			case 1:
				// subscribe to accelerometer data notifications
				characteristic = gatt.getService(ACCELEROMETER_SERVICE).getCharacteristic(ACCELEROMETER_DATA);
				break;
			case 0:
				// subscribe to simple keys data notifications
				characteristic = gatt.getService(SIMPLE_KEYS_SERVICE).getCharacteristic(SIMPLE_KEYS_DATA);
				break;
			default:
				return;
			}

			// Enable local notifications
			gatt.setCharacteristicNotification(characteristic, true);
			// Enabled remote notifications
			BluetoothGattDescriptor desc = characteristic.getDescriptor(CONFIG_DESCRIPTOR);
			desc.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
			gatt.writeDescriptor(desc);
		}

		@Override
		public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
			if (status == BluetoothGatt.GATT_SUCCESS && newState == BluetoothProfile.STATE_CONNECTED) {
				// discover all services on device before we can read and write their characteristics
				gatt.discoverServices();
			} else if (status != BluetoothGatt.GATT_SUCCESS) {
				// if there is a failure at any stage, simply disconnect
				gatt.disconnect();
			}
		}

		@Override
		public void onServicesDiscovered(BluetoothGatt gatt, int status) {
			reset();
			state = SimpleKeysStatus.OFF_OFF;
			setNextSensorCharacteristic(gatt);
		}

		@Override
		public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
			//do nothing?
		}

		@Override
		public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
			// After writing to a sensor characteristic, we move on to enable data notification on the sensor
			enableNextSensorNotification(gatt);
		}

		@Override
		public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
			advance();
			setNextSensorCharacteristic(gatt);
		}

		@Override
		public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
			// After notifications are enabled, all updates from the device on characteristic value changes will be posted here.
			if (ACCELEROMETER_DATA.equals(characteristic.getUuid())) {
				double[] acceleration = SensorTagData.extractAccelerometerXYZ(characteristic);
				String x = Double.toString(acceleration[0]);
				String y = Double.toString(acceleration[1]);
				String z = Double.toString(acceleration[2]);
				long timestamp = System.currentTimeMillis();
				
				//broadcast only during periods where left-click is held down
				if(state.equals(SimpleKeysStatus.ON_OFF)) {
					Intent broadcastIntent = new Intent();
					broadcastIntent.setAction(ConnectionServiceReceiver.PROCESS_RESPONSE);
					broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
					broadcastIntent.putExtra(X, x);
					broadcastIntent.putExtra(Y, y);
					broadcastIntent.putExtra(Z, z);
					broadcastIntent.putExtra("TIMESTAMP", timestamp);
					sendBroadcast(broadcastIntent);
				} else if (state.equals(SimpleKeysStatus.OFF_ON)) { //backspace
					Intent broadcastIntent = new Intent();
					broadcastIntent.setAction(ConnectionServiceReceiver.PROCESS_RESPONSE);
					broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
					broadcastIntent.putExtra(ConnectionService.DELETE_COMMAND, "DELETE_COMMAND");
					sendBroadcast(broadcastIntent);
				} else if (state.equals(SimpleKeysStatus.OFF_OFF)) { //call test() on ui
					Intent broadcastIntent = new Intent();
					broadcastIntent.setAction(ConnectionServiceReceiver.PROCESS_RESPONSE);
					broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
					broadcastIntent.putExtra(ConnectionService.TEST_COMMAND, "TEST_COMMAND");
					sendBroadcast(broadcastIntent);
				}
			}
			if (SIMPLE_KEYS_DATA.equals(characteristic.getUuid())) {
				state = SensorTagData.extractSimpleKeysValue(characteristic);
			}
		}
	};
}

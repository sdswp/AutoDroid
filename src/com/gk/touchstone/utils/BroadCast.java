package com.gk.touchstone.utils;

import com.gk.touchstone.R;
import com.gk.touchstone.db.DBManager;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.telephony.TelephonyManager;

public class BroadCast {
	private Context context;
	private String pathString = Environment.getExternalStorageDirectory()
			.getPath();
	public static String wifiStateStr = "";
	public static String btStateStr = "";
	public static String mobileNetStateStr = "";
	public static String gpsStateStr = "";
	public static String airplaneStateStr = "";
	public static String sdmountStateStr = "";
	public static String phoneStateStr = "Idle";
	public static String phoneNumber = "";
	IntentFilter airIntentFilter;
	private int airState;
	boolean state = false;

	private MobileNeNetworkStateReceiver mnStateReceiver = new MobileNeNetworkStateReceiver();
	private WifiStateReceiver mWifiStateReceiver = new WifiStateReceiver();
	private BluetoothStateReceiver blueStateReceiver = new BluetoothStateReceiver();
	private GpsStateReceiver gpsStateReceiver = new GpsStateReceiver();
	private AirplaneStateReceiver airplaneStateReceiver = new AirplaneStateReceiver();
	private SdcardMountReceiver sdcardMountReceiver = new SdcardMountReceiver();
	public PhoneBroadcastReceiver phoneReceiver = new PhoneBroadcastReceiver();
	//private receiveTask taskBroadCast = new receiveTask();

	public BroadCast(Context context) {
		this.context = context;

		// tbc=new TaskBroadCast();
	}

	// private enum plantype {serialRandom(1), serial,parallel};

//	public boolean registerReceiverTask() {
//		String broadcastAction = context.getResources().getString(
//				R.string.taskBroadCastAction);
//		try {
//			context.registerReceiver(taskBroadCast, new IntentFilter(
//					broadcastAction));
//		} catch (Exception e) {
//			return false;
//		}
//		return true;
//	}

//	public void registerTask() {
//		String broadcastAction = context.getResources().getString(
//				R.string.taskBroadCastAction);
//		context.registerReceiver(taskBroadCast, new IntentFilter(
//				broadcastAction));
//	}

	public void registerWifi() {
		// WifiStateReceiver mWifiStateReceiver = new WifiStateReceiver();
		context.registerReceiver(mWifiStateReceiver, new IntentFilter(
				WifiManager.WIFI_STATE_CHANGED_ACTION));
	}

	public void registerBluetooth() {
		// BluetoothStateReceiver blueStateReceiver = new
		// BluetoothStateReceiver();
		context.registerReceiver(blueStateReceiver, new IntentFilter(
				BluetoothAdapter.ACTION_STATE_CHANGED));
	}

	public void registerMoblieNetwork() {
		// MobileNeNetworkStateReceiver mnStateReceiver = new
		// MobileNeNetworkStateReceiver();
		context.registerReceiver(mnStateReceiver, new IntentFilter(
				ConnectivityManager.CONNECTIVITY_ACTION));
	}

	public void registerGps() {
		// GpsStateReceiver gpsStateReceiver = new GpsStateReceiver();
		context.registerReceiver(gpsStateReceiver, new IntentFilter(
				LocationManager.PROVIDERS_CHANGED_ACTION));
	}

	public void registerAirplane() {
		// AirplaneStateReceiver airplaneStateReceiver = new
		// AirplaneStateReceiver();
		context.registerReceiver(airplaneStateReceiver, new IntentFilter(
				Intent.ACTION_AIRPLANE_MODE_CHANGED));
	}

	public void registerSdcard() {
		// SdcardMountReceiver sdcardMountReceiver = new SdcardMountReceiver();
		context.registerReceiver(sdcardMountReceiver, new IntentFilter(
				Intent.ACTION_MEDIA_MOUNTED));
	}

	public void registerPhone() {
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(TelephonyManager.ACTION_PHONE_STATE_CHANGED);
		intentFilter.setPriority(Integer.MAX_VALUE);
		context.registerReceiver(phoneReceiver, intentFilter);
		// context.registerReceiver(phoneReceiver, new
		// IntentFilter(TelephonyManager.ACTION_PHONE_STATE_CHANGED));
	}

	private class WifiStateReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
			switch (wifiState) {
			case WifiManager.WIFI_STATE_DISABLED:
				wifiStateStr = "DISABLED";
				break;
			case WifiManager.WIFI_STATE_ENABLED:
				wifiStateStr = "ENABLED";
				break;
			case WifiManager.WIFI_STATE_UNKNOWN:
				wifiStateStr = "UNKNOWN";
				break;
			}
		}
	};

	private class BluetoothStateReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			int bluetoothState = intent.getIntExtra(
					BluetoothAdapter.EXTRA_STATE, 10);
			switch (bluetoothState) {
			case BluetoothAdapter.STATE_OFF:
				btStateStr = "DISABLED";
				break;
			case BluetoothAdapter.STATE_ON:
				btStateStr = "ENABLED";
				break;
			}
		}
	};

	private class MobileNeNetworkStateReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			ConnectivityManager cm = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);

			if (cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
					.isConnected()) {
				mobileNetStateStr = "ENABLED";
			} else if (cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
					.isFailover()) {
				mobileNetStateStr = "IsFailover";
			} else {
				mobileNetStateStr = "DISABLED";
			}
		}
	};

	private class GpsStateReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			LocationManager lm = (LocationManager) context
					.getSystemService(Context.LOCATION_SERVICE);
			if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
				gpsStateStr = "ENABLED";
			} else {
				gpsStateStr = "DISABLED";
			}
		}
	};

	private class AirplaneStateReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			// 1为开启，0为关闭
			String airplaneState = Settings.System.getString(
					context.getContentResolver(),
					Settings.System.AIRPLANE_MODE_ON);
			if (airplaneState.trim().equals("1")) {
				airplaneStateStr = "ENABLED";
			} else {
				airplaneStateStr = "DISABLED";
			}
		}
	};

	private class SdcardMountReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (Intent.ACTION_MEDIA_MOUNTED.equals(action)
					|| Intent.ACTION_MEDIA_SCANNER_STARTED.equals(action)
					|| Intent.ACTION_MEDIA_SCANNER_FINISHED.equals(action)) {// SD卡成功挂载
				sdmountStateStr = "Mounted";
			} else if (Intent.ACTION_MEDIA_REMOVED.equals(action)
					|| Intent.ACTION_MEDIA_UNMOUNTED.equals(action)
					|| Intent.ACTION_MEDIA_BAD_REMOVAL.equals(action)) { // SD卡挂载失败
				sdmountStateStr = "unMounted";
			}
		}
	};

	public class PhoneBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String callNumber = intent
					.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
			TelephonyManager telephony = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			int state = telephony.getCallState();
			switch (state) {
			case TelephonyManager.CALL_STATE_RINGING:
				phoneStateStr = "Ring";
				phoneNumber = callNumber;
				break;
			case TelephonyManager.CALL_STATE_IDLE:
				phoneStateStr = "Idle";
				// Log.i(TAG, "[Broadcast]电话挂断=" + phoneNumber);
				break;
			case TelephonyManager.CALL_STATE_OFFHOOK:
				phoneStateStr = "Offhook";
				// Log.i(TAG, "[Broadcast]通话中=" + phoneNumber);
				break;
			}
		}
	};

//	/**
//	 * 自定义任务广播，监控任务开始、完成、和结束。 注：接收自com.gk.touchstone.task包所发送任务状态信息
//	 * 
//	 * @author guohai@Live.com
//	 * 
//	 */
//	private class receiveTask extends BroadcastReceiver {
//
//		@Override
//		public void onReceive(Context arg0, Intent arg1) {
//			String broadcastAction = context.getResources().getString(
//					R.string.taskBroadCastAction);
//			String broadcastName = context.getResources().getString(
//					R.string.taskBroadCastName);
//			tu = new TaskUtils(arg0);
//			dbm = new DBManager(arg0);
//			us = new Utils(arg0);
//			if (arg1.getAction().equals(broadcastAction)) {
//
////				Bundle bundle = arg1.getExtras();
////				if (bundle != null) {
//
//					// int broadcastValue = bundle.getInt(broadcastName);
//					// TaskValue.setRunState(broadcastValue);
//					// Toast.makeText(context, String.valueOf(broadcastValue) ,
//					// 2000).show ();
//					int[] receiveVal = arg1.getIntArrayExtra(broadcastName);
//					int pid = receiveVal[0];
//					int pstate = receiveVal[1];
//
//					dbm = new DBManager(arg0);
//					Plan plan = dbm.queryPlan(pid);
//
//					switch (plan.getPlanType()) {
//					case TaskValue.serial:
//						serialRandomTask(pid, pstate);
//						break;
//					case TaskValue.parallel:
//						parallelTask(pid, pstate);
//						break;
//					default:
//						us.myToast(plan.getPlanName(),
//								R.string.select_planError);
//						break;
//					}
//				}
//			//}
//			dbm.closedb();
//		}
//	}
//
//	// private enum plantype {serialRandom(1), serial,parallel};
//
//	private void serialRandomTask(int pid, int state) {
//		List<Task> taskNoRunList = dbm.queryTaskNoRun(pid);
//		int k = (int) (Math.random() * taskNoRunList.size());
//		Task task = taskNoRunList.get(k);
//
//		switch (state) {
//		case TaskValue.start:
//			break;
//		case TaskValue.finish:
//			startTask(task);
//			break;
//		case TaskValue.stop:
//			break;
//		default:
//			break;
//		}
//	}
//
//	private void parallelTask(int pid, int state) {
//		List<Task> taskNoRunList = dbm.queryTaskNoRun(pid);
//		Task task = taskNoRunList.get(0);
//		switch (state) {
//		case TaskValue.start:
//			startTask(task);
//			break;
//		case TaskValue.finish:
//			updateTaskState(task);
//			break;
//		case TaskValue.stop:
//			break;
//		default:
//			break;
//		}
//	}
//
//	private void startTask(Task task) {
//		// 启动任务后，任务数减一
//		Reflects ref = new Reflects(context);
//
//		int c = task.getTaskCount();
//		if (c <= 0) {
//			task.setRunState(0);
//			task.setTaskCount(0);
//			dbm.updateTask(task);
//		} else {
//			task.setRunState(3);
//			task.setTaskCount(c - 1);
//			dbm.updateTask(task);
//			ref.startTask(task);
//		}
//	}
//
//	private void updateTaskState(Task task) {
//		if (task.getTaskCount() > 0) {
//			task.setRunState(1);
//			dbm.updateTask(task);
//		}
//	}

	// runtype 0 1 2
	// runstate 0 1 2

	public void sendTaskBroadcast(int planid, int state) {
		String broadcastAction = context.getResources().getString(
				R.string.taskBroadCastAction);
		String broadcastName = context.getResources().getString(
				R.string.taskBroadCastName);

		Intent intent = new Intent();
		Bundle bundle = new Bundle();

		int[] stateArray = new int[] { planid, state };
		// 计划类型，任务状态
		bundle.putIntArray(broadcastName, stateArray);
		// bundle.putInt(broadcastName, state);
		intent.putExtras(bundle);

		intent.setAction(broadcastAction);
		context.sendBroadcast(intent);
	}

	public void unregisterWifi() {
		context.unregisterReceiver(mWifiStateReceiver);
	}

	public void unregisterBluetooth() {
		context.unregisterReceiver(blueStateReceiver);
	}

	public void unregisterMoblieNetwork() {
		context.unregisterReceiver(mnStateReceiver);
	}

	public void unregisterGps() {
		context.unregisterReceiver(gpsStateReceiver);
	}

	public void unregisterAirplane() {
		context.unregisterReceiver(airplaneStateReceiver);
	}

	public void unregisterSdcard() {
		context.unregisterReceiver(sdcardMountReceiver);
	}

	public void unregisterPhone() {
		if (phoneReceiver != null) {
			try {
				context.unregisterReceiver(phoneReceiver);
			} catch (IllegalArgumentException e) {
				if (e.getMessage().contains("Receiver not registered")) {
				} else {
					throw e;
				}
			}
		}
		//context.unregisterReceiver(phoneReceiver);
	}

//	public void unregisterTask() {
//		context.unregisterReceiver(taskBroadCast);
//	}
}

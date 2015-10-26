package com.gk.touchstone.testcasebak;

import android.content.Context;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import com.gk.touchstone.core.TestCase;
import com.gk.touchstone.core.Constants;
import com.gk.touchstone.entity.Task;

public class GPSSwitchTask extends TestCase {
	private final static int MSG_LISTEN_GPS_STATE = 11;
	private final static int MSG_CHANGE_GPS_STATE = 12;
	private final static int MSG_GPS_TEST_END = 13;
	//因为开启和关闭算两次，这两次都需要记录在测试结果中，所以使用StingBuilder来进行存储
	private StringBuilder SBResult;
	private StringBuilder SBReason;

	private int TimeOut;

	private Handler testHandler;
	
	public GPSSwitchTask(Context context, Task task) {
		super(context, task);
			
		testHandler = new myHandler();

		initData();
	}
	
	private void initData(){
		timeCount = getIntValue("gpsSwitchTimes");
		TimeOut = getIntValue("gpsStatusTime");
		
		listenState();
		SBResult = new StringBuilder();
		SBReason = new StringBuilder();
	}
	
	@Override
	public void Start() {
		initData();
		
		isRunning=true;
		if (mThread == null) {
			mThread = new testThread();
			mThread.start();
		}
		Thread listenThread = new listenThread();
		listenThread.start();
	}

	@Override
	public void Finish() {
		// 运行完毕后进行关闭
		if (listenState()) {
			changeGpsState();
		}
		sendBroadcast();
		
		return;
	}

	@Override
	public void Stop() {
		if (listenState()) {
			changeGpsState();
		}
		stopTask();
	}

	class testThread extends Thread {
		@Override
		public void run() {
			super.run();
			try {
				while (true) {
					if (isRunning) {
						if (timeCount > 0) {
							testHandler.sendEmptyMessage(MSG_CHANGE_GPS_STATE);
						} else {
							testHandler.sendEmptyMessage(MSG_GPS_TEST_END);
						}
						sleep(TimeOut * 1000);
					}
					sleep(500);
				}

			} catch (Exception e) {
				Log.e("TouchStone", "gps test  Thread Error!");
				e.printStackTrace();
			}
		}
	}

	class listenThread extends Thread {
		@Override
		public void run() {
			super.run();
			try {
				do {
					testHandler.sendEmptyMessage(MSG_LISTEN_GPS_STATE);
					sleep(1000);
				} while (isRunning);
			} catch (Exception e) {
				Log.e("David", "gps test  Thread Error!");
				e.printStackTrace();
			}
		}
	}
	
	private class myHandler extends Handler{
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what){
			case MSG_LISTEN_GPS_STATE: {
				listenState();
				break;
			}
			case MSG_CHANGE_GPS_STATE: {
				if (!listenState()){
					try{
						changeGpsState();
						SBResult.append(Constants.PASS+",");
						SBReason.append("");
					}catch (Exception e){
						SBResult.append(Constants.FAIL+",");
						SBReason.append(e.getMessage());
					}
				}else{
					timeCount--;
					try{
						changeGpsState();
						SBResult.append(Constants.PASS+",");
						SBReason.append("");
					}catch (Exception e){
						SBResult.append(Constants.FAIL+",");
						SBReason.append(e.getMessage());
					}
					ResultWrite();
				}
				break;
			}
			case MSG_GPS_TEST_END: {
				Finish();
				break;
			}
			default:
				break;
			}
		}
	}
	
	private boolean listenState() {
		boolean state = Settings.Secure.isLocationProviderEnabled(context.getContentResolver(), LocationManager.GPS_PROVIDER);
		if (state){
			state = true;
		}
		else if (!state){
			state = false;
		}
		else{
			SBResult.append(Constants.FAIL+",");
			SBReason.append("GPS Status Unknown");
		}
		return state;
	}

	private void changeGpsState() {
		boolean gpsEnabled = Settings.Secure.isLocationProviderEnabled(context.getContentResolver(), LocationManager.GPS_PROVIDER);
		if (gpsEnabled) {
			// turn off GPS
			Settings.Secure.setLocationProviderEnabled(context.getContentResolver(), LocationManager.GPS_PROVIDER, false);
		} else {
			// turn on GPS
			Settings.Secure.setLocationProviderEnabled(context.getContentResolver(), LocationManager.GPS_PROVIDER, true);
		}

	}	
	
	private void ResultWrite(){
		writeResult(timeCount,SBResult.toString(),SBReason.toString());
		SBResult = new StringBuilder();
		SBReason = new StringBuilder();
	}
	
}

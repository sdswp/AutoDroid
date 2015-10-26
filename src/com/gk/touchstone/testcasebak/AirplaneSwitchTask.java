package com.gk.touchstone.testcasebak;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;

import com.gk.touchstone.core.Constants;
import com.gk.touchstone.core.TestCase;
import com.gk.touchstone.entity.Task;

import static android.provider.Settings.System.AIRPLANE_MODE_ON;

public class AirplaneSwitchTask extends TestCase{

	private final static int MSG_LISTEN_AIRPLANE_STATE = 15;
	private final static int MSG_CHANGE_AIRPLANE_STATE = 16;
	private final static int MSG_AIRPLANE_TEST_END = 17;
	
	private int internalTime;
	
	private StringBuilder SBResult;
	private StringBuilder SBReason;
	
	private Handler testHandler;
	
	public AirplaneSwitchTask(Context context, Task task) {
		super(context, task);
		// TODO Auto-generated constructor stub
		
		testHandler = new myHandler();
		
		initData();
	}

	private void initData(){
		timeCount = getIntValue("apsTimes");
		internalTime = getIntValue("apsInternalTime");
		
		listenState(context);
		SBResult = new StringBuilder();
		SBReason = new StringBuilder();
	}
	
	@Override
	public void Start() {
		// TODO Auto-generated method stu
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
		// TODO Auto-generated method stub
		if (listenState(context)){
			changeAirplaneState();
		}
		sendBroadcast();
		return;
	}

	@Override
	public void Stop() {
		// TODO Auto-generated method stub
		if (listenState(context)){
			changeAirplaneState();
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
							testHandler.sendEmptyMessage(MSG_CHANGE_AIRPLANE_STATE);
						} else {
							testHandler.sendEmptyMessage(MSG_AIRPLANE_TEST_END);
						}
						sleep(internalTime * 1000);
					}
					sleep(500);
				}

			} catch (Exception e) {
				Log.e("TouchStone", "airplanemode test  Thread Error!");
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
					testHandler.sendEmptyMessage(MSG_LISTEN_AIRPLANE_STATE);
					sleep(1000);
				} while (isRunning);
			} catch (Exception e) {
				Log.e("David", "airplanemode test  Thread Error!");
				e.printStackTrace();
			}
		}
	}
	
	private class myHandler extends Handler{
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what){
			case MSG_LISTEN_AIRPLANE_STATE: {
				listenState(context);
				break;
			}
			case MSG_CHANGE_AIRPLANE_STATE: {
				if (!listenState(context)){
					try{
						changeAirplaneState();
						SBResult.append(Constants.PASS+",");
						SBReason.append("");
					}catch (Exception e){
						SBResult.append(Constants.FAIL+",");
						SBReason.append(e.getMessage());
					}
				}else{
					timeCount--;
					try{
						changeAirplaneState();
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
			case MSG_AIRPLANE_TEST_END: {
				Finish();
				break;
			}
			default:
				break;
			}
		}
	}
	
	/*private boolean listenState() {
		//1 is on, 0 is off
		boolean airplaneState;
		String state = Settings.System.getString(context.getContentResolver(),Settings.System.AIRPLANE_MODE_ON);
		if (state.equals("1")){
			airplaneState = true;
		} else{
			airplaneState = false;
		}
		return airplaneState;
	}*/
	
	static boolean listenState(Context context) {
		  ContentResolver contentResolver = context.getContentResolver();
		  return Settings.System.getInt(contentResolver, AIRPLANE_MODE_ON, 0) != 0;
		}

	private void changeAirplaneState() {
		String state = Settings.System.getString(context.getContentResolver(),Settings.System.AIRPLANE_MODE_ON);
		if (state.equals("1")) {
			// turn off Airplanemode
			setAirplaneMode(context,false);
		} else {
			// turn on Airplanemode
			setAirplaneMode(context,true);
		}
	}	
	
	private void ResultWrite(){
		writeResult(timeCount,SBResult.toString(),SBReason.toString());
		SBResult = new StringBuilder();
		SBReason = new StringBuilder();
	}
	
	private void setAirplaneMode(Context context, boolean enabling){
		Settings.System.putInt(context.getContentResolver(), Settings.System.AIRPLANE_MODE_ON, enabling ? 1 : 0);
		Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra("state", enabling);
		context.sendBroadcast(intent);
	}
	
}
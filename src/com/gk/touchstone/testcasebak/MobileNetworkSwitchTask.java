package com.gk.touchstone.testcasebak;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.gk.touchstone.core.Constants;
import com.gk.touchstone.utils.MobileNetwork;
import com.gk.touchstone.core.TestCase;
import com.gk.touchstone.entity.Task;

public class MobileNetworkSwitchTask extends TestCase{
	private final static int MSG_LISTEN_MOBILE_STATE = 0;
	private final static int MSG_CHANGE_MOBILE_STATE = 1;
	private final static int MSG_MOBILE_TEST_END = 2;

	private MobileNetwork mn;
	private WifiManager wifiManager;
	
	private StringBuilder SBResult;
	private StringBuilder SBReason;
	private int TimeOut;

	private Handler testHandler;

	public MobileNetworkSwitchTask(Context context, Task task) {
		super(context, task);

		wifiManager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);

		testHandler = new myHandler();
		mn = new MobileNetwork(context);
		
		initData();
	}
	
	private void initData(){
		if (wifiManager.isWifiEnabled()){
			wifiManager.setWifiEnabled(false);
		}
		
		timeCount = getIntValue("mobileNetSwitchTimes");
		TimeOut = getIntValue("mobileNetStatusTime");
		listenState();
		SBResult = new StringBuilder();
		SBReason = new StringBuilder();
	}
	
	@Override
	public void Start() {
		initData();
		isRunning = true;
		if (mThread == null){
			mThread = new testThread();
			mThread.start();
		}
		Thread listenThread = new listenThread();
		listenThread.start();
	}

	@Override
	public void Finish() {
		// 每次结束都确保移动网络已经关掉，避免影响其他的测试或者耗费大量流量
		if (listenState()) {
			changeMobilenetState();
		}
		sendBroadcast();
		return;
	}

	@Override
	public void Stop() {
		if (listenState()){
			changeMobilenetState();
		}
		stopTask();
	}

	class testThread extends Thread {
		@Override
		public void run() {
			super.run();
			while (true) {
				try {
					if (isRunning) {
						if (timeCount > 0) {
							testHandler.sendEmptyMessage(MSG_CHANGE_MOBILE_STATE);
						} else {
							testHandler.sendEmptyMessage(MSG_MOBILE_TEST_END);
						}
						sleep(TimeOut * 1000);
					}
					sleep(500);
				}catch (Exception e) { 
					Log.e("TouchStone", "mobilenet test Thread Error!");
					e.printStackTrace();
				}
			}
		}
	}

	class listenThread extends Thread {
		@Override
		public void run() {
			super.run();
			try {
				do {
					testHandler.sendEmptyMessage(MSG_LISTEN_MOBILE_STATE);
					sleep(1000);
				} while (isRunning);
			} catch (Exception e) {
				Log.e("David", "mobilenet test Thread Error!");
				e.printStackTrace();
			}
		}
	}
	
	private class myHandler extends Handler{
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what){
			case MSG_LISTEN_MOBILE_STATE: {
				listenState();
				break;
			}
			case MSG_CHANGE_MOBILE_STATE: {
				if (!listenState()){
					try{
						changeMobilenetState();
						SBResult.append(Constants.PASS+",");
						SBReason.append("");
					}catch (Exception e){
						SBResult.append(Constants.FAIL+",");
						SBReason.append(e.getMessage());
					}
				}else{
					timeCount--;
					
					try{
						changeMobilenetState();
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
			case MSG_MOBILE_TEST_END: {
				Finish();
				break;
			}
			default:
				break;
			}
		}
	}	
	
	private boolean listenState() {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		boolean state = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnected();
		if (!cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isAvailable()){
			SBResult.append(Constants.FAIL+",");
			SBReason.append("MobileNet Work is not available");
		}
		else if(cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isFailover()){
			SBResult.append(Constants.FAIL+",");
			SBReason.append("A Failover attempt");
		}
		else{
			return state;
		}
		return state;
	}

	private void changeMobilenetState() {
		
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		boolean mobileEnabled = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnected();
		if (mobileEnabled) {
			// turn off mobilenet
			mn.toggleMobileData(context, false);
		} else {
			// turn on mobilenet
			mn.toggleMobileData(context, true);
		}
	}	
	
	private void ResultWrite(){
		writeResult(timeCount,SBResult.toString(),SBReason.toString());
		SBResult = new StringBuilder();
		SBReason = new StringBuilder();
	}
}

package com.gk.touchstone.testcase;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.gk.touchstone.core.Constants;
import com.gk.touchstone.core.TestCase;
import com.gk.touchstone.entity.Task;

public class WifiSwitchTask extends TestCase {
	private WifiManager wifiManager;
	private int timeOut;

	private final static int MSG_LISTEN_WIFI_STATE = 8;
	private final static int MSG_CHANGE_WIFI_STATE = 9;
	private final static int MSG_WIFI_TEST_END = 10;

	private StringBuilder SBResult;
	private StringBuilder SBReason;

	private Handler testHandler;

	public WifiSwitchTask(Context context, Task task) {
		super(context, task);
		// TODO Auto-generated constructor stub
		task.setPackageName(this.getClass().getSimpleName());
	}

	// private void initData() {
	// timeCount = getIntValue("wifiSwitchCount");
	// timeOut = getIntValue("wifiSwitchInterval");
	//
	// //listenState();
	// SBResult = new StringBuilder();
	// SBReason = new StringBuilder();
	// }

	@Override
	public void setUp() {
		super.setUp();

		wifiManager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		testHandler = new myHandler();

		timeCount = getIntValue("wifiSwitchCount");
		timeOut = getIntValue("wifiSwitchInterval");

		// listenState();
		SBResult = new StringBuilder();
		SBReason = new StringBuilder();
	}


	@Override
	public void Start() {
		// 进入下一轮任务时，重新初始化数据
		setUp();

		isRunning = true;
		if (mThread == null) {
			mThread = new switchWiFi();
			mThread.start();
		}
		Thread listenThread = new listenThread();
		listenThread.start();
	}

	@Override
	public void Finish() {
		sendBroadcast();
		return;
	}

	@Override
	public void Stop() {
		stopTask();
	}

	class switchWiFi extends Thread {
		@Override
		public void run() {
			super.run();
			while (true) {
				try {
					if (isRunning) {
						if (timeCount > 0) {
							testHandler.sendEmptyMessage(MSG_CHANGE_WIFI_STATE);
						} else {
							testHandler.sendEmptyMessage(MSG_WIFI_TEST_END);
						}
						sleep(timeOut * 1000);
					}
					sleep(500);
				} catch (Exception e) {
					writeResult(timeCount, Constants.FAIL,
							"WiFi test Thread Error: " + e.getMessage());
					Log.e("TouchStone", "WiFi test Thread Error!");
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
					testHandler.sendEmptyMessage(MSG_LISTEN_WIFI_STATE);
					sleep(1000);
				} while (isRunning);
			} catch (Exception e) {
				writeResult(timeCount, Constants.FAIL,
						"WiFi test Thread Error: " + e.getMessage());
				Log.e("TouchStone", "WiFi test Thread Error!");
				e.printStackTrace();
			}
		}
	}

	private class myHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_LISTEN_WIFI_STATE: {
				listenState();
				break;
			}
			case MSG_CHANGE_WIFI_STATE: {
				if (!listenState()) {
					try {
						changeBtState();
						SBResult.append(Constants.PASS + ",");
						SBReason.append("");
					} catch (Exception e) {
						SBResult.append(Constants.FAIL + ",");
						SBReason.append(e.getMessage());
					}
				} else {
					timeCount--;
					try {
						changeBtState();
						SBResult.append(Constants.PASS + ",");
						SBReason.append("");
					} catch (Exception e) {
						SBResult.append(Constants.FAIL + ",");
						SBReason.append(e.getMessage());
					}
					ResultWrite();
				}
				break;
			}
			case MSG_WIFI_TEST_END: {
				Finish();
				break;
			}
			default:
				break;
			}
		}
	}

	private boolean listenState() {
		wifiManager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		boolean state = wifiManager.isWifiEnabled();
		switch (wifiManager.getWifiState()) {
		case WifiManager.WIFI_STATE_DISABLING:
			break;
		case WifiManager.WIFI_STATE_DISABLED:
			state = false;
			break;
		case WifiManager.WIFI_STATE_ENABLING:
			break;
		case WifiManager.WIFI_STATE_ENABLED:
			state = true;
			break;
		case WifiManager.WIFI_STATE_UNKNOWN:
			SBResult.append(Constants.FAIL + ",");
			SBReason.append("WIFI Status Unknown");
			break;
		default:
			SBResult.append(Constants.FAIL + ",");
			SBReason.append("WIFI Status Unknown");
			break;
		}
		return state;
	}

	private void changeBtState() {
		wifiManager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		boolean wifiEnabled = wifiManager.isWifiEnabled();
		if (wifiEnabled) {
			// turn off wifi
			wifiManager.setWifiEnabled(false);
		} else {
			// turn on wifi
			wifiManager.setWifiEnabled(true);
		}
	}

	private void ResultWrite() {
		// 开关WiFi完毕后，会出现两个结果，但报告记录只能算一次
		writeResult(timeCount, SBResult.toString(), SBReason.toString());
		SBResult = new StringBuilder();
		SBReason = new StringBuilder();
	}

	/*
	 * class myThread extends Thread {
	 * 
	 * @Override public void run() { while (true) { try {
	 * Thread.currentThread(); Thread.sleep(1000);
	 * 
	 * if (isRunning) { Message msg = new Message(); if (timeCount > 0) {
	 * handler.sendMessage(msg); } else { Finish(); } } } catch
	 * (InterruptedException e) { us.writeSDFile(e.getMessage(),
	 * "threadException.log"); throw new RuntimeException(); } } } }
	 */

	/*
	 * private class myHandler extends Handler {
	 * 
	 * @Override public void handleMessage(Message msg) {
	 * super.handleMessage(msg);
	 * 
	 * // 判断WIFI是否打开 if (wifiManager.isWifiEnabled()) { switch
	 * (wifiManager.getWifiState()) { case WifiManager.WIFI_STATE_DISABLING:
	 * break; case WifiManager.WIFI_STATE_DISABLED: break; case
	 * WifiManager.WIFI_STATE_ENABLING: break; case
	 * WifiManager.WIFI_STATE_ENABLED: switchAction(Constants.PASS, ""); break;
	 * default: switchAction(Constants.FAIL, "wifi unknow"); break; } } else {
	 * switchAction(Constants.FAIL, "规定时间未开启"); } } };
	 * 
	 * private void switchAction(String state, String reason) { if (st <= 0) {
	 * st = spaceTime; timeCount--;
	 * 
	 * resultStr = state; reasonStr = reason; writeResult(timeCount, resultStr,
	 * reasonStr);
	 * 
	 * wifiManager.setWifiEnabled(false); return; }
	 * 
	 * wifiManager.setWifiEnabled(true); st--; }
	 */

}

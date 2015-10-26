package com.gk.touchstone.testcasebak;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.gk.touchstone.core.TestCase;
import com.gk.touchstone.core.Constants;
import com.gk.touchstone.entity.Task;

public class BTSwitchTask extends TestCase {
	private int TimeOut;
	private BluetoothAdapter bluetoothAdapter;

	private final static int MSG_LISTEN_BT_STATE = 5;
	private final static int MSG_CHANGE_BT_STATE = 6;
	private final static int MSG_BT_TEST_END = 7;

	private StringBuilder SBResult;
	private StringBuilder SBReason;

	private Handler testHandler;

	public BTSwitchTask(Context context, Task task) {
		super(context, task);

		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		testHandler = new myHandler();
		
		initData();
	}

	private void initData() {
		timeCount = getIntValue("btSwitchTimes");
		TimeOut = getIntValue("btStatusTime");

		//listenState();
		SBResult = new StringBuilder();
		SBReason = new StringBuilder();
	}

	@Override
	public void Start() {
		initData();

		isRunning = true;
		if (mThread == null) {
			mThread = new switchBT();
			mThread.start();
		}
		Thread listenThread = new listenThread();
		listenThread.start();
	}

	@Override
	public void Finish() {
		if (listenState()) {
			changeBtState();
		}
		sendBroadcast();
		return;
	}

	@Override
	public void Stop() {
		if (listenState()) {
			changeBtState();
		}
		stopTask();
	}

	class switchBT extends Thread {
		@Override
		public void run() {
			super.run();
			while (true) {
				try {
					if (isRunning) {
						if (timeCount > 0) {
							testHandler.sendEmptyMessage(MSG_CHANGE_BT_STATE);
						} else {
							testHandler.sendEmptyMessage(MSG_BT_TEST_END);
						}
						sleep(TimeOut * 1000);
					}
					sleep(500);
				} catch (Exception e) {
					writeResult(timeCount, Constants.FAIL,
							"bt test Thread Error: " + e.getMessage());
					Log.e("TouchStone", "BT test Thread Error!");
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
					testHandler.sendEmptyMessage(MSG_LISTEN_BT_STATE);
					sleep(1000);
				} while (isRunning);
			} catch (Exception e) {
				writeResult(timeCount, Constants.FAIL, "bt test Thread Error: "
						+ e.getMessage());
				Log.e("David", "BT test Thread Error!");
				e.printStackTrace();
			}
		}
	}

	private class myHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_LISTEN_BT_STATE: {
				listenState();
				break;
			}
			case MSG_CHANGE_BT_STATE: {
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
			case MSG_BT_TEST_END: {
				Finish();
				break;
			}
			default:
				break;
			}
		}
	}

	private boolean listenState() {
		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		boolean state = bluetoothAdapter.isEnabled();

		switch (bluetoothAdapter.getState()) {
		case BluetoothAdapter.STATE_OFF:
			state = false;
			break;
		case BluetoothAdapter.STATE_ON:
			state = true;
			break;
		case BluetoothAdapter.STATE_TURNING_OFF:
			break;
		case BluetoothAdapter.STATE_TURNING_ON:
			break;
		case BluetoothAdapter.ERROR:
			SBResult.append(Constants.FAIL + ",");
			SBReason.append("BT Status Unknown");
		default:
			SBResult.append(Constants.FAIL + ",");
			SBReason.append("BT Status Unknown");
			break;
		}
		return state;
	}

	private void changeBtState() {
		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		boolean btEnabled = bluetoothAdapter.isEnabled();
		if (btEnabled) {
			// turn off bt
			bluetoothAdapter.disable();
		} else {
			// turn on bt
			bluetoothAdapter.enable();
		}
	}

	private void ResultWrite() {
		// 开关蓝牙完毕后，会出现两个结果，但报告记录只能算一次
		writeResult(timeCount, SBResult.toString(), SBReason.toString());
		SBResult = new StringBuilder();
		SBReason = new StringBuilder();
	}

	/*
	 * Handler handler = new Handler() {
	 * 
	 * @Override public void handleMessage(Message msg) {
	 * super.handleMessage(msg);
	 * 
	 * if (bluetoothAdapter.isEnabled()) { switch (bluetoothAdapter.getState())
	 * { case BluetoothAdapter.STATE_OFF: break; case BluetoothAdapter.STATE_ON:
	 * switchAction(Constants.PASS, ""); break; case
	 * BluetoothAdapter.STATE_TURNING_OFF: break; case
	 * BluetoothAdapter.STATE_TURNING_ON: break; default:
	 * switchAction(Constants.FAIL, "bluetooth unknow"); break; } } else {
	 * switchAction(Constants.FAIL, "规定时间未开启"); } } };
	 * 
	 * class switchBT extends Thread {
	 * 
	 * @Override public void run() { while (true) { try {
	 * Thread.currentThread(); Thread.sleep(1000); if (isRunning) { Message msg
	 * = new Message(); if (timeCount > 0) { handler.sendMessage(msg); } else {
	 * Finish(); } } } catch (Exception e) { e.printStackTrace(); } } } }
	 */

	/*
	 * private void switchAction(String state, String reason) { if (to <= 0) {
	 * to = TimeOut; timeCount--;
	 * 
	 * resultStr = state; reasonStr = reason; writeResult(timeCount, resultStr,
	 * reasonStr);
	 * 
	 * bluetoothAdapter.disable(); return; }
	 * 
	 * bluetoothAdapter.enable(); to--; }
	 */

}

package com.gk.touchstone.testcasebak;

import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import com.gk.touchstone.core.TestCase;
import com.gk.touchstone.core.Constants;
import com.gk.touchstone.entity.Task;

public class WifiAPQuantityTask extends TestCase {
	private Thread mThread = null;
	private WifiManager wifiManager;
	private int openTime;
	private int scanTime;
	private int spaceTime;

	private int opent;
	private int scant;

	private List<ScanResult> wifiList;
	private WifiReceiver wifiReceiver = null;

	private List<String> scanSize = new ArrayList<String>();

	public WifiAPQuantityTask(Context context, Task task) {
		super(context, task);

		wifiManager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);

		initData();
	}

	private void initData() {
		timeCount = getIntValue("gatherNum");
		openTime = getIntValue("gatherOpenTime");
		scanTime = getIntValue("wifiScanTime");
		spaceTime = getIntValue("wifiScanTimeInterval");

		opent = openTime;
		scant = scanTime;
	}

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			// 判断WIFI是否打开
			if (wifiManager.isWifiEnabled()) {
				switch (wifiManager.getWifiState()) {
				case WifiManager.WIFI_STATE_DISABLING:
					break;
				case WifiManager.WIFI_STATE_DISABLED:
					break;
				case WifiManager.WIFI_STATE_ENABLING:
					break;
				case WifiManager.WIFI_STATE_ENABLED:
					wifiManager.startScan();
					switchAction(Constants.PASS, scanSize.toString());
					break;
				default:
					switchAction(Constants.FAIL, "wifi unknow");
					break;
				}
			} else {
				switchAction(Constants.FAIL, "规定时间未开启");
			}
		}
	};

	private void switchAction(String state, String reason) {
		if (opent > 0) {
			opent--;
			return;
		}

		if (scant <= 0) {
			opent = openTime;
			scant = scanTime;
			timeCount--;

			writeResult(timeCount, state, reason);
			if (scanSize != null) {
				scanSize = null;
				scanSize = new ArrayList<String>();
			}

			wifiManager.setWifiEnabled(false);
			return;
		}

		wifiManager.setWifiEnabled(true);

		if (scant % spaceTime == 0 && wifiList != null) {
			scanSize.add(String.valueOf(wifiList.size()));
		}

		scant--;
	}

	private final class WifiReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			wifiList = wifiManager.getScanResults();
		}
	}

	class myThread extends Thread {
		@Override
		public void run() {
			while (true) {
				try {
					Thread.currentThread();
					Thread.sleep(1000);

					if (isRunning) {
						Message msg = new Message();
						if (timeCount > 0) {
							handler.sendMessage(msg);
						} else {
							Finish();
						}
					}
				} catch (InterruptedException e) {
					throw new RuntimeException();
				}
			}
		}
	}

	@Override
	public void Start() {
		initData();

		wifiReceiver = new WifiReceiver();
		context.registerReceiver(wifiReceiver, new IntentFilter(
				WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

		isRunning = true;
		if (mThread == null) {
			mThread = new myThread();
			mThread.start();
		}
	}

	@Override
	public void Finish() {
		unRegReceiver();
		sendBroadcast();
		return;
	}

	@Override
	public void Stop() {
		unRegReceiver();

		stopTask();
	}

	private void unRegReceiver() {
		if (wifiReceiver != null) {
			try {
				context.unregisterReceiver(wifiReceiver);
			} catch (IllegalArgumentException e) {
				if (e.getMessage().contains("Receiver not registered")) {
				} else {
					throw e;
				}
			}
		}
	}

}

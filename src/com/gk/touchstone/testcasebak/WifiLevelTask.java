package com.gk.touchstone.testcasebak;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.gk.touchstone.entity.Task;
import com.gk.touchstone.entity.Wifi;
import com.gk.touchstone.core.TestCase;
import com.gk.touchstone.core.Constants;
import com.gk.touchstone.utils.Utils;
import com.gk.touchstone.utils.WifiAdmin;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class WifiLevelTask extends TestCase {
	private static final String Tag = null;
	private WifiAdmin wa;
	private Gson gson;
	private Thread mThread = null;
	private String taskname;
	private int testCount;
	private int spaceTime;
	private List<Wifi> aplist = new ArrayList<Wifi>();

	private String wifiSwitchResult = "";

	private int st;

	public WifiLevelTask(Context context, Task task) {
		super(context, task);

		gson = new Gson();
		wa = new WifiAdmin(context);

		initData();
	}

	private void initData() {
		timeCount = getIntValue("wifiSwitchCount");
		// timeOut = convertInt(formValue, "wifiSwitchInterval");

	}

	@Override
	public void Start() {
		testCount = getIntValue("wifiLevelCheckCount");
		spaceTime = getIntValue("wifiLevelApList");

		st = spaceTime;

		isRunning = true;
		if (mThread == null) {
			mThread = new myThread();
			mThread.start();
		}
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

	class myThread extends Thread {
		@Override
		public void run() {
			while (isRunning) {
				try {
					Thread.currentThread();
					Thread.sleep(1000);
					Message msg = new Message();
					msg.what = 0;
					if (isRunning) {
						if (testCount > 0) {
							handler.sendMessage(msg);
						} else {
							Finish();
						}
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:

				// 判断WIFI是否打开
				if (wa.checkNetCardState() == 1) {
					wa.wifiEnabled();
					wifiSwitchResult = Constants.PASS;
				} else if (wa.checkNetCardState() == 3) {
					switchAction();
				} else if (wa.checkNetCardState() == 0) {

				} else if (wa.checkNetCardState() == 2) {

				} else {
					loge("wifi坏掉了...");
					wifiSwitchResult = Constants.FAIL;
					Finish();
				}

				break;
			}
		}
	};

	private void switchAction() {
		if (st <= 0) {
			st = spaceTime;
			testCount--;

			writeResult(testCount, wifiSwitchResult, "");

			wa.wifiDisabled();
			return;
		}

		st--;
	}

	// 随机AP
	private WifiConfiguration getWifiConfig() {
		WifiConfiguration wcg = null;
		if (aplist.size() <= 0) {
			String json = "";
			Utils us = new Utils(context);
			try {
				String apResource = "";
				json = us.fromFile(apResource);
			} catch (IOException e) {
				e.printStackTrace();
			}

			aplist = gson.fromJson(json, new TypeToken<List<Wifi>>() {
			}.getType());

		} else {
			int k = (int) (Math.random() * aplist.size());
			String ssid = aplist.get(k).getSsid().toString().trim();
			String psk = aplist.get(k).getPsk().toString().trim();
			Log.d(Tag, "Connect to " + ssid);
			// wa.addNetwork(wa.CreateWifiInfo(ssid, psk, 3));
			wcg = wa.CreateWifiInfo(ssid, psk, 3);

			aplist.remove(aplist.get(k));
		}
		return wcg;
	}

}

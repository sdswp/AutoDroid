package com.gk.touchstone.testcasebak;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.gk.touchstone.entity.Task;
import com.gk.touchstone.entity.Wifi;
import com.gk.touchstone.core.TestCase;
import com.gk.touchstone.utils.Utils;
import com.gk.touchstone.utils.WifiUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class WifiAPSwitchTask extends TestCase {
	private static final String Tag = null;
	private Map<String, Object> formValue;
	private int keepTime;
	private int outTime;
	private int switchCount;
	private String apResource;
	private WifiUtils wifi;

	private int kt;
	private int ot;
	private List<Wifi> aplist = new ArrayList<Wifi>();

	public WifiAPSwitchTask(Context context, Task task) {
		super(context, task);

		wifi = new WifiUtils(context);

		initData();
	}

	private void initData() {
		keepTime = getIntValue("apKeepTime");
		outTime = getIntValue("apSwitchOutTime");
		switchCount = getIntValue("apSwitchCount");
		apResource = getStrValue("apResource");

		kt = keepTime;
		ot = outTime;
	}

	class myThread extends Thread {
		@Override
		public void run() {
			// Thread.currentThread().setName("hello");
			while (true) {
				try {
					Thread.currentThread();
					Thread.sleep(1000);

					if (isRunning) {
						Message msg = new Message();
						if (switchCount > 0) {
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

	@Override
	public void Start() {
		initData();

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

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// 判断WIFI是否打开
//			if (wa.checkNetCardState() == 1) {
//				wa.wifiEnabled();
//			} else if (wa.checkNetCardState() == 3) {
//				switchAP();
//			}
			if (wifi.isWifiEnabled()) {
				switch (wifi.getWifiStates()) {
				case 0:
					break;
				case 1:
					break;
				case 2:
					break;
				case 3:
					// switchAction(Constants.PASS, "");
					//switchAP();
					break;
				default:
					// switchAction(Constants.FAIL, "wifi unknow");
					break;
				}
			} else {
				// switchAction(Constants.FAIL, "规定时间未开启");
			}

		}
	};

/*	private void switchAction(String state, String reason) {
		if (st <= 0) {
			st = spaceTime;
			timeCount--;

			resultStr = state;
			reasonStr = reason;
			writeResult(timeCount, resultStr, reasonStr);

			wifiManager.setWifiEnabled(false);
			return;
		}

		wifiManager.setWifiEnabled(true);
		st--;
	}*/


/*	private void switchAP() {
		if (wa.getWifiConnect().equals("CONNECTED")) {
			if (kt <= 0) {
				wifi.addNetwork(getWifiConfig());
				switchCount--;
				kt = keepTime;// 重置保持时间
			} else {
				kt--;
				System.out.println("连接成功，保持时间：" + kt);
				// 保持时间内，每隔n秒记录一次信息。
				if (kt % (keepTime / 2) == 0) {

				}
			}
		} else {
			if (ot <= 0) {
				wu.disconnectWifi();
				wu.addNetwork(getWifiConfig());
				switchCount--;
				ot = outTime;// 重置超时
			} else {
				ot--;
				System.out.println("连接不成功，超时时间：" + ot);
				// resultState = "连接超时";
				if (wu.getWifiStates() == WifiManager.WIFI_STATE_UNKNOWN) {
					// resultState = "状态未知";
				}
			}
		}
	}*/

	// 随机AP
	private WifiConfiguration getWifiConfig() {
		WifiConfiguration wcg = null;
		if (aplist.size() <= 0) {
			String json = "";
			Utils us=new Utils(context);
			try {
				json = us.fromFile(apResource);
			} catch (IOException e) {
				e.printStackTrace();
			}

			Gson gson=new Gson();
			aplist = gson.fromJson(json, new TypeToken<List<Wifi>>() {
			}.getType());

		} else {
			int k = (int) (Math.random() * aplist.size());
			String ssid = aplist.get(k).getSsid().toString().trim();
			String psk = aplist.get(k).getPsk().toString().trim();
			Log.d(Tag, "Connect to " + ssid);
			// wa.addNetwork(wa.CreateWifiInfo(ssid, psk, 3));
			//wcg = wa.CreateWifiInfo(ssid, psk, 3);

			aplist.remove(aplist.get(k));
		}
		return wcg;
	}
	


}

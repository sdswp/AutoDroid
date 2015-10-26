package com.gk.touchstone.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

public class WifiUtils {
	private WifiManager wifiManager;
	private WifiInfo wifiInfo;
	private Context context;
	private int DISABLING = 0;
	private int DISABLED = 1;
	private int ENABLING = 2;
	private int ENABLED = 3;
	private int UNKNOWN=4;
	

	public WifiUtils(Context context) {
		this.context = context;
		wifiManager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		wifiInfo = wifiManager.getConnectionInfo();
	}
	
	// 添加一个网络并连接
	public boolean addNetwork(WifiConfiguration wcg) {
		int wcgID = wifiManager.addNetwork(wcg);
		boolean b = wifiManager.enableNetwork(wcgID, true);
		return b;
	}

	public void disconnectWifi() {
		int netId = getNetworkId();
		wifiManager.disableNetwork(netId);
		wifiManager.disconnect();
		wifiInfo = null;
	}

	public int getNetworkId() {
		return (wifiInfo == null) ? 0 : wifiInfo.getNetworkId();
	}
	
	/**
	 * 判断wifi是否打开
	 * @return
	 */
	public boolean isWifiEnabled() {
		if (!wifiManager.isWifiEnabled()) {
			return false;
		}
		return true;
	}
	
	public int getWifiStates() {
		int wifistate = 0;
		if (wifiManager.getWifiState() == DISABLING) {
			wifistate = 0;
		} else if (wifiManager.getWifiState() == DISABLED) {
			wifistate = 1;
		} else if (wifiManager.getWifiState() == ENABLING) {
			wifistate = 2;
		} else if (wifiManager.getWifiState() == ENABLED) {
			wifistate = 3;
		} else if (wifiManager.getWifiState() == UNKNOWN) {
			wifistate = 4;
		}
		return wifistate;
	}
	
	public String getWifiConnect() {
		String state = "";
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		// return
		// connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		NetworkInfo ni = connectivityManager
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		State st = connectivityManager.getNetworkInfo(
				ConnectivityManager.TYPE_WIFI).getState();

		// return ni;

		switch (st) {
		case CONNECTED:
			state = "CONNECTED";
			break;
		case CONNECTING:
			state = "CONNECTING";
			break;
		case DISCONNECTED:
			state = "DISCONNECTED";
			break;
		default:
			break;
		}
		return state;

	}
	
}

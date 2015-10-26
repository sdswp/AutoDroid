package com.gk.touchstone.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.util.Log;

public class WifiAdmin {
	private final static String TAG = "WifiAdmin";
	private StringBuffer mStringBuffer = new StringBuffer();
	private List<ScanResult> listResult;
	private ScanResult mScanResult;
	// 定义WifiManager对象
	private WifiManager mWifiManager;
	// 定义WifiInfo对象
	private WifiInfo mWifiInfo;
	// 网络连接列表
	private List<WifiConfiguration> mWifiConfiguration;
	// 定义一个WifiLock
	WifiLock mWifiLock;
	// private ArrayList<HashMap<String, Object>> listmapResult = new
	// ArrayList<HashMap<String, Object>>();
	private Context context;

	/**
	 * 构造方法
	 */
	public WifiAdmin(Context context) {
		this.context = context;
		mWifiManager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		mWifiInfo = mWifiManager.getConnectionInfo();
	}

	/**
	 * 检查Wifi状态
	 */
	public boolean isWifiEnabled() {
		if (!mWifiManager.isWifiEnabled()) {
			return false;
		}
		return true;
	}

	/**
	 * 打开Wifi网卡
	 */
	public void wifiEnabled() {
		if (!mWifiManager.isWifiEnabled()) {
			mWifiManager.setWifiEnabled(true);
		}
	}

	public WifiManager getWifiManager() {

		return mWifiManager;

	}

	/**
	 * 关闭Wifi网卡
	 */
	public void wifiDisabled() {
		if (mWifiManager.isWifiEnabled()) {
			mWifiManager.setWifiEnabled(false);
		}
	}

	/**
	 * 检查当前Wifi网卡状态
	 */
	public int checkNetCardState() {
		int wifistate = 0;
		if (mWifiManager.getWifiState() == 0) {
			// Log.i(TAG, "网卡正在关闭");
			wifistate = 0;
		} else if (mWifiManager.getWifiState() == 1) {
			// Log.i(TAG, "网卡已经关闭");
			wifistate = 1;
		} else if (mWifiManager.getWifiState() == 2) {
			// Log.i(TAG, "网卡正在打开");
			wifistate = 2;
		} else if (mWifiManager.getWifiState() == 3) {
			// Log.i(TAG, "网卡已经打开");
			wifistate = 3;
		} else {
			// Log.i(TAG, "没有获取到状态");
		}
		return wifistate;
	}

	/**
	 * 扫描周边网络
	 */
	public void scan() {
		mWifiManager.startScan();
		listResult = mWifiManager.getScanResults();
		if (listResult != null) {
			//Log.i(TAG, "当前区域存在无线网络，请查看扫描结果");
		} else {
			//Log.i(TAG, "当前区域没有无线网络");
		}
	}

	/**
	 * 得到扫描结果
	 */
	public List<Map<String, Object>> getScanResult() {
		List<Map<String, Object>> scanlist = new ArrayList<Map<String, Object>>();
		// 每次点击扫描之前清空上一次的扫描结果
		if (mStringBuffer != null) {
			mStringBuffer = new StringBuffer();
		}
		// 开始扫描网络
		//scan();
		mWifiManager.startScan();
		listResult = mWifiManager.getScanResults();
		if (listResult != null) {
			for (int i = 0; i < listResult.size(); i++) {
				Map<String, Object> map = new HashMap<String, Object>();
				mScanResult = listResult.get(i);
				// mStringBuffer = mStringBuffer.append("NO.").append(i + 1)
				// .append(" :").append(mScanResult.SSID).append("->")
				// .append(mScanResult.BSSID).append("->")
				// .append(mScanResult.capabilities).append("->")
				// .append(mScanResult.frequency).append("->")
				// .append(mScanResult.level).append("->")
				// .append(mScanResult.describeContents()).append("\n\n");
				// map.put("bssid",mScanResult.BSSID);
				// map.put("capabilities",mScanResult.capabilities);
				// map.put("frequency",mScanResult.frequency);
				// map.put("level",mScanResult.level);
				// map.put("describeContents",mScanResult.describeContents());
				map.put("ssid", mScanResult.SSID);
				map.put("bssid", mScanResult.BSSID);
				scanlist.add(map);
			}
		}
		// Log.i(TAG, mStringBuffer.toString());
		return scanlist;
	}

	/**
	 * 连接指定网络
	 */
	public void connect() {
		mWifiInfo = mWifiManager.getConnectionInfo();

	}

	/**
	 * 断开当前连接的网络
	 */
	public void disconnectWifi() {
		int netId = getNetworkId();
		mWifiManager.disableNetwork(netId);
		mWifiManager.disconnect();
		mWifiInfo = null;

	}

	/**
	 * 检查当前网络状态
	 * 
	 * @return Boolean
	 */
	public Boolean checkNetWorkState() {
		Boolean minfo;
		if (mWifiInfo != null) {
			minfo = true;
		} else {
			minfo = false;
		}
		return minfo;
	}

	/**
	 * 判断wifi已经连接上网络
	 * 
	 * @return Boolean
	 */
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

	/**
	 * 得到连接的ID
	 */
	public int getNetworkId() {
		return (mWifiInfo == null) ? 0 : mWifiInfo.getNetworkId();
	}

	/**
	 * 得到IP地址
	 */
	public int getIPAddress() {
		return (mWifiInfo == null) ? 0 : mWifiInfo.getIpAddress();
	}

	public int getRssi() {
		return (mWifiInfo == null) ? 0 : mWifiInfo.getRssi();
	}

	/**
	 * 得到连接速度
	 */
	public int getLinkSpeed() {
		return (mWifiInfo == null) ? 0 : mWifiInfo.getLinkSpeed();
	}

	// 锁定WifiLock
	public void acquireWifiLock() {
		mWifiLock.acquire();
	}

	// 解锁WifiLock
	public void releaseWifiLock() {
		// 判断时候锁定
		if (mWifiLock.isHeld()) {
			mWifiLock.acquire();
		}
	}

	// 创建一个WifiLock
	public void creatWifiLock() {
		mWifiLock = mWifiManager.createWifiLock("Test");
	}

	// 得到配置好的网络
	public List<WifiConfiguration> getConfiguration() {
		return mWifiConfiguration;
	}

	// 指定配置好的网络进行连接
	public void connectConfiguration(int index) {
		// 索引大于配置好的网络索引返回
		if (index >= mWifiConfiguration.size()) {
			return;
		}
		// 连接配置好的指定ID的网络
		mWifiManager.enableNetwork(mWifiConfiguration.get(index).networkId,
				true);
	}

	// 得到MAC地址
	public String getMacAddress() {
		return (mWifiInfo == null) ? "NULL" : mWifiInfo.getMacAddress();
	}

	// 得到接入点的BSSID
	public String getBSSID() {
		return (mWifiInfo == null) ? "NULL" : mWifiInfo.getBSSID();
	}

	// 得到接入点的BSSID
	public String getSSID() {
		return (mWifiInfo == null) ? "NULL" : mWifiInfo.getSSID();
	}

	// 得到WifiInfo的所有信息包
	public String getWifiInfo() {
		return (mWifiInfo == null) ? "NULL" : mWifiInfo.toString();
	}

	private WifiConfiguration IsExsits(String SSID) {
		List<WifiConfiguration> existingConfigs = mWifiManager
				.getConfiguredNetworks();
		for (WifiConfiguration existingConfig : existingConfigs) {
			if (existingConfig.SSID.equals("\"" + SSID + "\"")) {
				return existingConfig;
			}
		}
		return null;
	}

	public WifiConfiguration CreateWifiInfo(String SSID, String Password,
			int Type) {
		WifiConfiguration config = new WifiConfiguration();
		config.allowedAuthAlgorithms.clear();
		config.allowedGroupCiphers.clear();
		config.allowedKeyManagement.clear();
		config.allowedPairwiseCiphers.clear();
		config.allowedProtocols.clear();
		config.SSID = "\"" + SSID + "\"";

		WifiConfiguration tempConfig = this.IsExsits(SSID);
		if (tempConfig != null) {
			mWifiManager.removeNetwork(tempConfig.networkId);
		}

		if (Type == 1) // WIFICIPHER_NOPASS
		{
			config.wepKeys[0] = "";
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
			config.wepTxKeyIndex = 0;
		}
		if (Type == 2) // WIFICIPHER_WEP
		{
			config.hiddenSSID = true;
			config.wepKeys[0] = "\"" + Password + "\"";
			config.allowedAuthAlgorithms
					.set(WifiConfiguration.AuthAlgorithm.SHARED);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
			config.allowedGroupCiphers
					.set(WifiConfiguration.GroupCipher.WEP104);
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
			config.wepTxKeyIndex = 0;
		}
		if (Type == 3) // WIFICIPHER_WPA
		{
			config.preSharedKey = "\"" + Password + "\"";
			config.hiddenSSID = true;
			config.allowedAuthAlgorithms
					.set(WifiConfiguration.AuthAlgorithm.OPEN);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
			config.allowedPairwiseCiphers
					.set(WifiConfiguration.PairwiseCipher.TKIP);
			// config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
			config.allowedPairwiseCiphers
					.set(WifiConfiguration.PairwiseCipher.CCMP);
			config.status = WifiConfiguration.Status.ENABLED;
		}
		return config;
	}

	// 添加一个网络并连接
	public void addNetwork(WifiConfiguration wcg) {
		int wcgID = mWifiManager.addNetwork(wcg);
		boolean b = mWifiManager.enableNetwork(wcgID, true);
		// System.out.println("a--" + wcgID);
		// System.out.println("b--" + b);
	}

	// 添加一个网络并连接
	// public int addNetwork(WifiConfiguration wcg) {
	// int wcgID = mWifiManager.addNetwork(mWifiConfiguration.get(3));
	// mWifiManager.enableNetwork(wcgID, true);
	// return wcgID;
	// }
}

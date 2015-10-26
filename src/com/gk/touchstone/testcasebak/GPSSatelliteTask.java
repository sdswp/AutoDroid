package com.gk.touchstone.testcasebak;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle; 
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;

import com.gk.touchstone.GkApplication;
import com.gk.touchstone.core.Constants;
import com.gk.touchstone.core.TestCase;
import com.gk.touchstone.entity.Task;
import com.gk.touchstone.utils.Utils;
import com.google.gson.Gson;

public class GPSSatelliteTask extends TestCase {

	private int internalTime;
	private LocationManager locationManager;
	private StringBuffer sb;
	private StringBuffer getInformationSb;

	public GPSSatelliteTask(Context context, Task task) {
		super(context, task);
		// TODO Auto-generated constructor stub

		sb = new StringBuffer();
		getInformationSb = new StringBuffer();

		initData();
	}

	private void initData() {
		if(!listenState()){
			changeGpsState();
		}
		
		timeCount = getIntValue("gpsUsingTimes");
		internalTime = getIntValue("gpsUsingInterval");
	}

	@Override
	public void Start() {
		// TODO Auto-generated method stub
		initData();
		
		isRunning=true;
		if (mThread == null){
			mThread = new loadingGps();
			mThread.start();
		}
	}

	@Override
	public void Finish() {
		// TODO Auto-generated method stub
		if(listenState()){
			changeGpsState();
		}
		sendBroadcast();
		return;
	}

	@Override
	public void Stop() {
		// TODO Auto-generated method stub
		if(listenState()){
			changeGpsState();
		}
		stopTask();
	}

	private StringBuffer getGpsInformation() {
		getInformationSb = null;
		// 获取系统LocationManager服务
		locationManager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		// 从GPS获取最近的定位信息
		Location location = locationManager
				.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		// 将location里的位置信息显示在EditText中
		// updateView(location);
		// 设置每2秒获取一次GPS的定位信息
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				2000, 8, new LocationListener() {

					@Override
					public void onLocationChanged(Location location) {
						// 当GPS定位信息发生改变时，更新位置
						sb.append("实时的位置信息：\n经度：");
						getInformationSb.append(location.getLongitude());
						sb.append("\n纬度：");  
						getInformationSb.append(location.getLatitude());
						sb.append("\n高度：");  
			            sb.append(location.getAltitude());
			            sb.append("\n方向："); 
						getInformationSb.append(location.getBearing());
						sb.append("\n速度：");
						getInformationSb.append(location.getSpeed());
						sb.append("\n精度："); 
						getInformationSb.append(location.getAccuracy());
					}

					@Override
					public void onProviderDisabled(String provider) {
						//updateView(null);
						//getInformationSb.append("");
					}

					@Override
					public void onProviderEnabled(String provider) {
						// 当GPS LocationProvider可用时，更新位置
						//getInformationSb.append(locationManager.getLastKnownLocation(provider));
					}

					@Override
					public void onStatusChanged(String provider, int status,
							Bundle extras) {
					}
				});
		return getInformationSb;
	}
	
	Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
			case 0:
				//String url = context.getResources().getString(R.string.networkUsing);
				timeCount--;
				if (getGpsInformation()!=null){
					writeResult(timeCount, Constants.PASS, "");
				}
				else{
					writeResult(timeCount,Constants.FAIL,"No satellite found");
				}
				break;
			case 1:
				Finish();
				break;
			}
		}
	};
	
	private class loadingGps extends Thread{
	@Override
	public void run() {
		while (true){
			try{
				Thread.currentThread();
				Thread.sleep(1000*internalTime);
				Message msg = new Message();
				if (isRunning){
					if (timeCount > 0){
						msg.what = 0;
						handler.sendMessage(msg);
					}
					else{
						msg.what = 1;
						handler.sendMessage(msg);
					}
				}
				
				}catch (Exception e){
					e.printStackTrace();
				}
			}
		}
	}
	
	private boolean listenState() {
		boolean state = Settings.Secure.isLocationProviderEnabled(context.getContentResolver(), LocationManager.GPS_PROVIDER);
		/*if (state) {
			String thisOut = "gps state on!";
		} else {
			String thisOut = "gps state off!";
		}*/
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
	
}

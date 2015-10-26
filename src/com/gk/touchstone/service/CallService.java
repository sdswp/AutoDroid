package com.gk.touchstone.service;

import com.gk.touchstone.utils.BroadCast;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
//just for test
public class CallService extends Service{
	private Handler callHandler;
	private BroadCast bc;
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
	}
	
	@Override
	public void onStart(Intent intent, int startId) {
		Log.i("callservice", "start");
		//register phone receive
		bc = new BroadCast(CallService.this);
		bc.registerPhone();
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		this.stopSelf();
	}
	
	
}

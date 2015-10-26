package com.gk.touchstone.testcasebak;

import android.content.Context;
import android.os.Handler;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.SystemClock;

import com.gk.touchstone.core.TestCase;
import com.gk.touchstone.entity.Task;

public class ScreenTestTask extends TestCase{
	private PowerManager powerManager;
	private WakeLock wakeLock;
	private WakeLock mScreenonWakeLock = null;
	private int internalTime;
	private Handler testHandler;
	
	public ScreenTestTask(Context context, Task task) {
		super(context, task);
		
		powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
		
		initData();
	}

	private void initData(){
		timeCount = getIntValue("screenTestTimes");
		internalTime = getIntValue("screenTestInterval");
	}
	
	@Override
	public void Start() {
		// TODO Auto-generated method stub
		initData();
		powerManager.goToSleep(SystemClock.uptimeMillis());
		//mScreenonWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "ScreenTest1");
		//mScreenonWakeLock.acquire();
	
	}

	@Override
	public void Finish() {
		// TODO Auto-generated method stub
		if (wakeLock != null) {
			wakeLock.release();
			wakeLock = null;
		}
		sendBroadcast();
		return;
	}

	@Override
	public void Stop() {
		// TODO Auto-generated method stub
		if (wakeLock != null) {
			wakeLock.release();
			wakeLock = null;
		}
		stopTask();
	}
}

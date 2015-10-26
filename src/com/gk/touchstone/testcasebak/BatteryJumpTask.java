package com.gk.touchstone.testcasebak;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.gk.touchstone.core.TestCase;
import com.gk.touchstone.entity.Task;

public class BatteryJumpTask extends TestCase {
	private BroadcastReceiver batteryReceiver = null;
	private SharedPreferences sp;
	private Editor editor;
	private int changeNum = 0;

	public BatteryJumpTask(Context context, Task task) {
		super(context, task);

		sp = context.getSharedPreferences("battery", Context.MODE_PRIVATE);
		editor = sp.edit();
	}

	@Override
	public void Start() {
		isRunning = true;
		registerBattery();
	}

	@Override
	public void Finish() {
		sendBroadcast();
		return;
	}

	@Override
	public void Stop() {
		if (batteryReceiver != null) {
			context.unregisterReceiver(batteryReceiver);
		}

		editor.remove("value");
		editor.commit();

		stopTask();
	}

	private void registerBattery() {
		editor.putInt("value", 0);
		editor.commit();

		batteryReceiver = new BatteryReceiver();
		context.registerReceiver(batteryReceiver, new IntentFilter(
				Intent.ACTION_BATTERY_CHANGED));
	}

	private boolean diffBattery(int b) {
		int val = sp.getInt("value", 0);

		// 先提取上一次的电量，再写入本次的电量
		editor.putInt("value", b);
		editor.commit();

		if ((val - b) >= changeNum) {
			return true;
		}
		return false;
	}

	private class BatteryReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			int currentBattery = intent.getExtras().getInt("level");// 获得当前电量
			int total = intent.getExtras().getInt("scale");// 获得总电量
			int percent = currentBattery * 100 / total;

			if (sp.getInt("value", 0) == 0 || diffBattery(percent)) {
				writeResult(0, String.valueOf(percent), "跳变率"+changeNum);
			}

		}
	}

}

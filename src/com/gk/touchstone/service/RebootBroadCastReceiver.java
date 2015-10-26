package com.gk.touchstone.service;

import com.gk.touchstone.R;
import com.gk.touchstone.activity.Splash;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class RebootBroadCastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// 系统重启或者开机启动程序
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		boolean isKeepRunning = prefs.getBoolean(context.getResources()
				.getString(R.string.autoStart_key), true);
		
		if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)
				&& isKeepRunning) {
			Intent startIntent = new Intent(context, Splash.class);
			startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(startIntent);
		}
	}
}

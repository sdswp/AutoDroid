package com.gk.touchstone.activity;

import com.gk.touchstone.R;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class MySettings extends PreferenceActivity implements Preference.OnPreferenceChangeListener{
	
	private CheckBoxPreference cbp_AutoStart;
	private String str_AutoStartKey;
	private CheckBoxPreference cbp_WakeLock;
	private String str_WakeLockKey;
	private CheckBoxPreference cbp_KeepRunning;
	private String str_KeepRunningKey;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.layout.mysettings);
		//this.getWindow().setBackgroundDrawableResource(R.drawable.bg);
		
		str_AutoStartKey = getResources().getString(R.string.autoStart_key);
		cbp_AutoStart = (CheckBoxPreference) findPreference(str_AutoStartKey);
		str_WakeLockKey = getResources().getString(R.string.wakeLock_key);
		cbp_WakeLock = (CheckBoxPreference) findPreference(str_WakeLockKey);
		str_KeepRunningKey = getResources().getString(R.string.keepRunning_key);
		cbp_KeepRunning = (CheckBoxPreference) findPreference(str_KeepRunningKey);
		
		cbp_AutoStart.setSummaryOff("不开启");
		cbp_AutoStart.setSummaryOn("已开启");
		cbp_AutoStart.setOnPreferenceChangeListener(this);
		
		cbp_WakeLock.setSummaryOff("不开启");
		cbp_WakeLock.setSummaryOn("已开启 "); 
		cbp_WakeLock.setOnPreferenceChangeListener(this);
		
		cbp_KeepRunning.setSummaryOff("不开启");
		cbp_KeepRunning.setSummaryOn("已开启");
		cbp_KeepRunning.setOnPreferenceChangeListener(this);

	}
	
	@Override
	protected void onResume() {
		super.onResume();
		/*http://my.unix-center.net/~Simon_fu/?p=652*/
		setMySettingsStatics();
	}
	
	private void setMySettingsStatics(){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		
		Boolean autoStart = prefs.getBoolean(str_AutoStartKey, true);//默认关闭开机启动服务
		cbp_AutoStart.setChecked(autoStart);
		
		Boolean wakeLock = prefs.getBoolean(str_WakeLockKey, true);//默认设置中不开启WakeLock
		cbp_WakeLock.setChecked(wakeLock);
		
		Boolean keeyRunning = prefs.getBoolean(str_KeepRunningKey, true);//默认设置中开启遇到重启后仍然继续运行
		cbp_KeepRunning.setChecked(keeyRunning);
	}
	
	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
    	return true;
	}
	
}

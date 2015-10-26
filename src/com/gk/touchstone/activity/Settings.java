package com.gk.touchstone.activity;

import java.io.File;

import com.gk.touchstone.R;
import com.gk.touchstone.network.AsyncUpload;
import com.gk.touchstone.core.Constants;
import com.gk.touchstone.utils.Utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

public class Settings extends Fragment {
	private TextView txtTitle;
	private View mySettings, deviceInfo, checkUpdate, getAdvice, getAbout,
			initRes, initData;
	private Utils us;
	private AsyncUpload uploadfile;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View parentView = inflater.inflate(R.layout.setting, container, false);
		txtTitle = (TextView) parentView.findViewById(R.id.txt_title);

		mySettings = (View) parentView.findViewById(R.id.mysettings);
		deviceInfo = (View) parentView.findViewById(R.id.deviceinfo);
		checkUpdate = (View) parentView.findViewById(R.id.checkupdate);
		getAdvice = (View) parentView.findViewById(R.id.get_advice);
		getAbout = (View) parentView.findViewById(R.id.get_about);
		initRes = (View) parentView.findViewById(R.id.initRes);
		initData = (View) parentView.findViewById(R.id.initData);

		mySettings.setOnClickListener(new mySettings());
		deviceInfo.setOnClickListener(new deviceInfo());
		checkUpdate.setOnClickListener(new checkUpdate());
		getAdvice.setOnClickListener(new getAdvice());
		getAbout.setOnClickListener(new getAbout());
		initRes.setOnClickListener(new startInitResources());
		initData.setOnClickListener(new startInitData());

		initView();

		return parentView;

	}

	private void initView() {
		us = new Utils(getActivity());
		txtTitle.setText(R.string.tab_4);
	}

	private class startInitData implements OnClickListener {
		@Override
		public void onClick(View v) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle("提示");
			builder.setMessage("确认后数据库、测试结果、配置的测试资源都将被清空，确认要初始化吗?")
					.setCancelable(false)
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									// TODO 清空数据库,清空data/data files,删除配置的测试资源
									initAllData();
								}
							})
					.setNegativeButton("取消",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.cancel();
								}
							});
			AlertDialog alert = builder.create();
			alert.show();
		}
	};

	private void initAllData() {
		String appName = getActivity().getResources().getString(
				R.string.app_name);
		if (getActivity().deleteDatabase("touchstone.db") && delDataFile()
				&& clearPrefs()) {
			us.myToast("", "请重启" + appName + "后，应用更改！");
		}

	}

	private boolean clearPrefs() {
		boolean clear = false;
		
		//清除TestCase 和 Module
		SharedPreferences sp1 = getActivity().getSharedPreferences(
				Constants.PREFS_TESTCASE_MODULE, Context.MODE_PRIVATE);
		Editor editor1 = sp1.edit();
		editor1.putBoolean(Constants.KEY_SAVE_DATA, false);
		clear = editor1.commit();
		
		//清除稳定性
		SharedPreferences sp2 = getActivity().getSharedPreferences(
				Constants.PREFS_STABILITY, Context.MODE_PRIVATE);
		Editor editor2 = sp2.edit();
		editor2.putInt(Constants.KEY_STABILITY_PID, 0);
		editor2.putInt(Constants.KEY_STRESS_PID, 0);
		clear = editor2.commit();
		
		return clear;
	}

	private boolean delDataFile() {
		boolean del = false;
		String path = getActivity().getFilesDir().getAbsolutePath();

		File delfile = new File(path);
		File[] files = delfile.listFiles();
		for (int i = 0; i < files.length; i++) {
			if (!files[i].isDirectory()) {
				del = files[i].delete();
			}
		}
		return del;
	}

	private class startInitResources implements OnClickListener {
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(getActivity(), ResConfig.class);
			startActivity(intent);
		}
	};

	private class mySettings implements OnClickListener {
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(getActivity(), MySettings.class);
			startActivity(intent);
		}
	};

	private class deviceInfo implements OnClickListener {
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(getActivity(), DeviceInfo.class);
			startActivity(intent);
		}
	};

	private class checkUpdate implements OnClickListener {
		@Override
		public void onClick(View v) {
			// Intent intent = new Intent(getActivity(), TestCaseList.class);
			// startActivity(intent);
		}
	};
	
	private class getAdvice implements OnClickListener {
		@Override
		public void onClick(View v) {
			
		}
	};

	private class getAbout implements OnClickListener {
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(getActivity(), About.class);
			startActivity(intent);

		}
	};

	private class updateApp implements OnClickListener {
		@Override
		public void onClick(View v) {

		}
	}

	/*
	 * private String showPhoneInfo(){ String info=""; StrUtils su=new
	 * StrUtils(Settings.this);
	 * 
	 * DisplayMetrics metric = new DisplayMetrics();
	 * getWindowManager().getDefaultDisplay().getMetrics(metric);
	 * 
	 * String[] args1 = { "/system/bin/cat",
	 * "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq" }; String[] args2
	 * = { "/system/bin/cat",
	 * "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_min_freq" };
	 * 
	 * //info=" IMEI:"+imei+"\r\n 分辨率："+width+"×"+height+"\r\n density:"+density+
	 * "\r\n densityDpi:"+densityDpi+"\r\n";
	 * 
	 * info = "Product: " + android.os.Build.PRODUCT+"\r\n"; info += "IMEI: " +
	 * su.getImei()+"\r\n"; info += "Pixels: " +
	 * metric.widthPixels+"×"+metric.heightPixels+"\r\n"; info += "Density: " +
	 * metric.density+"\r\n"; info += "DensityDpi: " + metric.densityDpi+"\r\n";
	 * info += "CPU_ABI: " + android.os.Build.CPU_ABI+"\r\n"; info +=
	 * "CPU Frequency: " + getCpufreq(args2)+"~"+getCpufreq(args1)+"\r\n"; info
	 * += "TAGS: " + android.os.Build.TAGS+"\r\n"; info +=
	 * "VERSION_CODES.BASE: " + android.os.Build.VERSION_CODES.BASE+"\r\n"; info
	 * += "MODEL: " + android.os.Build.MODEL+"\r\n"; info += "SDK: " +
	 * android.os.Build.VERSION.SDK+"\r\n"; info += "VERSION.RELEASE: " +
	 * android.os.Build.VERSION.RELEASE+"\r\n"; info += "DEVICE: " +
	 * android.os.Build.DEVICE+"\r\n"; info += "DISPLAY: " +
	 * android.os.Build.DISPLAY+"\r\n"; info += "BRAND: " +
	 * android.os.Build.BRAND+"\r\n"; info += "BOARD: " +
	 * android.os.Build.BOARD+"\r\n"; info += "FINGERPRINT: " +
	 * android.os.Build.FINGERPRINT+"\r\n"; info += "ID: " +
	 * android.os.Build.ID+"\r\n"; info += "MANUFACTURER: " +
	 * android.os.Build.MANUFACTURER+"\r\n"; info += "USER: " +
	 * android.os.Build.USER+"\r\n";
	 * 
	 * return info; }
	 */

}

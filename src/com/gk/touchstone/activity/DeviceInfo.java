package com.gk.touchstone.activity;

import java.io.IOException;
import java.io.InputStream;

import com.gk.touchstone.R;
import com.gk.touchstone.utils.Utils;
import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.TextView;

public class DeviceInfo extends Activity {
	private TextView info;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.device_info);
		initView();
	}

	private void initView() {
		info = (TextView) findViewById(R.id.txt_info);
		info.setText(showPhoneInfo());
	}

	private String showPhoneInfo() {
		String info = "";
		Utils us = new Utils(this);

		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);

		String[] args1 = { "/system/bin/cat",
				"/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq" };
		String[] args2 = { "/system/bin/cat",
				"/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_min_freq" };

		// info=" IMEI:"+imei+"\r\n 分辨率："+width+"×"+height+"\r\n density:"+density+"\r\n densityDpi:"+densityDpi+"\r\n";

		info = "Product: " + android.os.Build.PRODUCT + "\r\n";
		info += "IMEI: " + us.getDeviceId() + "\r\n";
		info += "Pixels: " + metric.widthPixels + "×" + metric.heightPixels
				+ "\r\n";
		info += "Density: " + metric.density + "\r\n";
		info += "DensityDpi: " + metric.densityDpi + "\r\n";
		info += "CPU_ABI: " + android.os.Build.CPU_ABI + "\r\n";
		info += "CPU Frequency: " + getCpufreq(args2) + "~" + getCpufreq(args1)
				+ "\r\n";
		info += "TAGS: " + android.os.Build.TAGS + "\r\n";
		info += "VERSION_CODES.BASE: " + android.os.Build.VERSION_CODES.BASE
				+ "\r\n";
		info += "MODEL: " + android.os.Build.MODEL + "\r\n";
		info += "SDK: " + android.os.Build.VERSION.SDK + "\r\n";
		info += "VERSION.RELEASE: " + android.os.Build.VERSION.RELEASE + "\r\n";
		info += "DEVICE: " + android.os.Build.DEVICE + "\r\n";
		info += "DISPLAY: " + android.os.Build.DISPLAY + "\r\n";
		info += "BRAND: " + android.os.Build.BRAND + "\r\n";
		info += "BOARD: " + android.os.Build.BOARD + "\r\n";
		info += "FINGERPRINT: " + android.os.Build.FINGERPRINT + "\r\n";
		info += "ID: " + android.os.Build.ID + "\r\n";
		info += "MANUFACTURER: " + android.os.Build.MANUFACTURER + "\r\n";
		info += "USER: " + android.os.Build.USER + "\r\n";

		return info;
	}

	// 获取cpu频率
	private String getCpufreq(String[] args) {
		String result = "";
		ProcessBuilder cmd;
		try {
			cmd = new ProcessBuilder(args);
			Process process = cmd.start();
			InputStream in = process.getInputStream();
			byte[] re = new byte[24];
			while (in.read(re) != -1) {
				result = result + new String(re);
			}
			in.close();
		} catch (IOException ex) {
			ex.printStackTrace();
			result = "N/A";
		}
		return result.trim();
	}
}

package com.gk.touchstone.activity;

import com.gk.touchstone.R;
import com.gk.touchstone.core.Constants;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class About extends Activity {
	private TextView appname, version, powerby,titlebar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
		initView();
	}

	private void initView() {
		titlebar = (TextView) findViewById(R.id.txt_title);
		titlebar.setText("关于测试工具");
		
		appname = (TextView) findViewById(R.id.txt_app);
		appname.setText(R.string.app_name);

		version = (TextView) findViewById(R.id.txt_version);
		version.setText("version:"+getVerName());

		powerby = (TextView) findViewById(R.id.txt_powerby);
		powerby.setText(R.string.powerby);

	}

	private String getVerName() {
		String versionName = "";
		try {
			versionName = getPackageManager().getPackageInfo(
					Constants.PACKAGE_NAME, 0).versionName;
		} catch (NameNotFoundException e) {
		}
		return versionName;
	}
}

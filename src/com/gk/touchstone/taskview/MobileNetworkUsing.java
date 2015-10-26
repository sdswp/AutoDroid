package com.gk.touchstone.taskview;

import com.gk.touchstone.R;
import com.gk.touchstone.core.BaseActivity;

import android.os.Bundle;
import android.os.StrictMode;
import android.view.WindowManager;

public class MobileNetworkUsing extends BaseActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setBaseContentView();//(R.layout.mobilenetusing);
		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	}
}

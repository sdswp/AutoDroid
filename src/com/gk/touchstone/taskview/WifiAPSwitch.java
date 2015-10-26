package com.gk.touchstone.taskview;

import com.gk.touchstone.R;
import com.gk.touchstone.activity.APList;
import com.gk.touchstone.core.BaseActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class WifiAPSwitch extends BaseActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setBaseContentView();//(R.layout.wifi_ap_switch);

		//initValue();
		
		initView();
	}
	
	private void initView() {
		Button btnSet=(Button) findViewById(R.id.btn_set);
		btnSet.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent=new Intent(WifiAPSwitch.this,APList.class);
				startActivity(intent);
			}
		});
	}


}

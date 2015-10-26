package com.gk.touchstone.taskview;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.gk.touchstone.R;
import com.gk.touchstone.activity.APList;
import com.gk.touchstone.core.BaseActivity;

public class WifiLevel extends BaseActivity {
	
	//private WifiLevelTask taskAction;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setBaseContentView();//(R.layout.wifi_level);
		
		//initValue();
		initView();
	}

	private void initView() {
		Button btnSet=(Button) findViewById(R.id.btn_set);
		btnSet.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent=new Intent(WifiLevel.this,APList.class);
				startActivity(intent);
			}
		});
	}



}

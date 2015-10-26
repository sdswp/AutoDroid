package com.gk.touchstone.taskview;

import com.gk.touchstone.R;
import com.gk.touchstone.core.BaseActivity;

import android.os.Bundle;

public class WifiAPQuantity extends BaseActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setBaseContentView();//(R.layout.wifi_ap_quantity);
		//initValue();
	}
}

//	protected static final String TAG = null;
//	private TextView TitleView,startTips;
//	private EditText openTime,gatherNum;
//	private RadioButton radioNum,radioAll;
//	private Button btnStop, btnStart, btnSave;
//	private Utils us;
//	private XmlParser xp;
//	private String titleBarName;
//	private String className = this.getClass().getSimpleName();
//	private WifiAPQuantityTask taskAction;
//	
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.wifi_ap_quantity);
//		initView();
//	}
//
//	private void initView() {
//		getWindow().setSoftInputMode(
//				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
//		xp = new XmlParser(this);
//		xp.initValue();
//		us = new Utils(this);
//
//		titleBarName = xp.allActivityMap().get(className).get("text")
//				.toString();
//		TextView TitleView = (TextView) findViewById(R.id.txt_title);
//		TitleView.setText(titleBarName);
//		
//		Button btnSave = (Button) findViewById(R.id.title_rbtn);
//		btnSave.setText(R.string.save);
//		btnSave.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				if (xp.saveValue()) {
//					us.myToast(titleBarName, R.string.save_data);
//					WifiAPQuantity.this.finish();
//				}
//			}
//		});
//		btnStart = (Button) findViewById(R.id.btn_start);
//		btnStop = (Button) findViewById(R.id.btn_stop);
//
//		btnStart.setOnClickListener(new startTask());
//		btnStop.setOnClickListener(new stopTask());
//
//	}
//	
//	private class startTask implements OnClickListener {
//		@Override
//		public void onClick(View v) {
//			if (xp.saveValue()) {
//				Task task = us.newTask(className);
//				taskAction = new WifiAPQuantityTask(WifiAPQuantity.this, task);
//				taskAction.Start();
//			}
//		}
//	}
//
//	private class stopTask implements OnClickListener {
//		@Override
//		public void onClick(View v) {
//			taskAction.Stop();
//		}
//	}
//
//
//}
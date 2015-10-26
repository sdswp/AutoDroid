package com.gk.touchstone.taskview;

import com.gk.touchstone.R;
import com.gk.touchstone.core.BaseActivity;

import android.os.Bundle;

public class AutoCall extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setBaseContentView();//(R.layout.phone_call);

	}
	
	
	@Override
	protected void onDestroy() {
		//TaskBase taskbase=tm.getCameraTask("AutoCallTask");
		//AutoCallTask callTask=(AutoCallTask) taskbase;
		//callTask.bc.unregisterPhone();
		super.onDestroy();
	}

	/*private void prepare() {
		// 初始化表单数据
		xp = new XmlParser(this);
		xp.initValue();
		us = new Utils(this);

		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		titleBarName = xp.allActivityMap().get(className).get("text")
				.toString();
		TextView TitleView = (TextView) findViewById(R.id.txt_title);
		TitleView.setText(titleBarName);

		Button btnSave = (Button) findViewById(R.id.title_rbtn);
		btnSave.setText(R.string.save);
		btnSave.setOnClickListener(new saveFormValue());

		Edt_callNumber = (EditText) findViewById(R.id.callNumber);
		Edt_callTimes = (EditText) findViewById(R.id.callTimes);
		Edt_internalTime = (EditText) findViewById(R.id.internalTime);
		//tvleftTimes = (TextView) findViewById(R.id.leftTimes);
		Btn_start = (Button) findViewById(R.id.btn_callstart);
		Btn_cancel = (Button) findViewById(R.id.btn_callcancel);

		Btn_start.setOnClickListener(new startRun());
		Btn_cancel.setOnClickListener(new cancelRun());
		Btn_cancel.setEnabled(false);
		
	}

	private class saveFormValue implements OnClickListener {
		@Override
		public void onClick(View v) {
			if (xp.saveValue()) {
				us.myToast(titleBarName, R.string.save_data);
				AutoCall.this.finish();
			}
		}
	}

	private class startRun implements OnClickListener {
		@Override
		public void onClick(View v) {
			if (xp.saveValue()) {
				Task task = us.newTask(className);
				taskAction = new AutoCallTask(AutoCall.this, task);
				taskAction.Start();

				Edt_callNumber.setEnabled(false);
				Edt_callTimes.setEnabled(false);
				Edt_internalTime.setEnabled(false);
				Btn_start.setEnabled(false);
				Btn_cancel.setEnabled(true);
			}
		}
	}

	private class cancelRun implements OnClickListener {
		@Override
		public void onClick(View v) {
			taskAction.Finish();
			Btn_start.setEnabled(true);
			Edt_callNumber.setEnabled(true);
			Edt_callTimes.setEnabled(true);
			Edt_internalTime.setEnabled(true);
			Btn_cancel.setEnabled(false);
		}
	}*/
}

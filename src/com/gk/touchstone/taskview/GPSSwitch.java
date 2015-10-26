package com.gk.touchstone.taskview;

import com.gk.touchstone.R;
import com.gk.touchstone.core.BaseActivity;

import android.os.Bundle;

public class GPSSwitch extends BaseActivity {
	/*private Button btnStart;
	private Button btnCancel;
	private EditText edtgpsswitchTimes;
	private EditText edtgpsstatusTime;
	private Utils us;
	private XmlParser xp;
	private String titleBarName;
	private String className = this.getClass().getSimpleName();
	private GPSSwitchTask taskAction;
*/
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setBaseContentView();//(R.layout.gpsswitch);
		//initValue();
		//prepareView();
	}

	/*private void prepareView() {
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		xp = new XmlParser(this);
		xp.initValue();
		us = new Utils(this);

		titleBarName = xp.allActivityMap().get(className).get("text")
				.toString();
		TextView TitleView = (TextView) findViewById(R.id.txt_title);
		TitleView.setText(titleBarName);

		Button btnSave = (Button) findViewById(R.id.title_rbtn);
		btnSave.setText(R.string.save);
		btnSave.setOnClickListener(new saveFormValue());

		btnStart = (Button) findViewById(R.id.btn_gpsstart);
		btnCancel = (Button) findViewById(R.id.btn_gpscancel);
		edtgpsswitchTimes = (EditText) findViewById(R.id.gpsSwitchTimes);
		edtgpsstatusTime = (EditText) findViewById(R.id.gpsStatusTime);
		//tvleftTimes = (TextView) findViewById(R.id.leftTimes);
		
		btnStart.setOnClickListener(new startRun());
		btnCancel.setOnClickListener(new cancelRun());
		btnCancel.setEnabled(false);
	}

	private class saveFormValue implements OnClickListener {
		@Override
		public void onClick(View v) {
			if (xp.saveValue()) {
				us.myToast(titleBarName, R.string.save_data);
				GPSSwitch.this.finish();
			}
		}
	}

	private class startRun implements OnClickListener {
		@Override
		public void onClick(View v) {
			if (xp.saveValue()) {
				Task task = us.newTask(className);
				taskAction = new GPSSwitchTask(GPSSwitch.this, task);
				taskAction.Start();
				btnStart.setEnabled(false);
				btnCancel.setEnabled(true);
				edtgpsstatusTime.setEnabled(false);
				edtgpsswitchTimes.setEnabled(false);
				
			}
		}
	}

	private class cancelRun implements OnClickListener {
		@Override
		public void onClick(View v) {
			taskAction.Finish();
			btnStart.setEnabled(true);
			btnCancel.setEnabled(false);
			edtgpsstatusTime.setEnabled(true);
			edtgpsswitchTimes.setEnabled(true);
		}
	}*/
}

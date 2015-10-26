package com.gk.touchstone.activity;

import java.util.List;

import com.gk.touchstone.GkApplication;
import com.gk.touchstone.R;
import com.gk.touchstone.db.DBManager;
import com.gk.touchstone.entity.Plan;
import com.gk.touchstone.entity.Task;
import com.gk.touchstone.core.BaseStability;
import com.gk.touchstone.core.PlanRunner;
import com.gk.touchstone.core.TestRunner;
import com.gk.touchstone.core.TaskManager;
import com.gk.touchstone.core.Constants;
import com.gk.touchstone.utils.DateTimes;
import com.gk.touchstone.utils.Utils;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class StabilityTest extends BaseStability {
	private GkApplication app;
	private Button btnStart, btnCancel;
	private EditText edInternalTime;
	private TextView txtLeftTime;
	private View lnProgress;
	private Utils us;
	private SharedPreferences sp;
	private Editor editor;
	private TaskManager tm;
	private String pidKey = Constants.KEY_STABILITY_PID;
	private ReceiveTaskBroadcast receiveTaskBroadcast;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setBaseContentView(R.layout.statibilitytest);

		initView();
	}

	private void initView() {
		us = new Utils(this);
		tm = new TaskManager(this);
		app = (GkApplication) getApplication();

		receiveTaskBroadcast = new ReceiveTaskBroadcast();
		registerReceiver(receiveTaskBroadcast, new IntentFilter(
				Constants.TASKSTATE_ACTION));

		sp = getSharedPreferences(Constants.PREFS_STABILITY, MODE_PRIVATE);
		editor = sp.edit();

		Intent intent = getIntent();
		String titleName = intent.getStringExtra("titlebar");

		TextView title = (TextView) findViewById(R.id.txt_title);
		title.setText(titleName);

		TextView tips = (TextView) findViewById(R.id.txt_planTips);
		tips.setText(R.string.plan_StabilityTest_desc);

		lnProgress = (View) findViewById(R.id.ln_progress);
		txtLeftTime = (TextView) findViewById(R.id.txt_leftTime);
		btnStart = (Button) findViewById(R.id.btnStart);
		btnCancel = (Button) findViewById(R.id.btnStop);
		edInternalTime = (EditText) findViewById(R.id.internalTime);

		btnStart.setOnClickListener(new startRun());
		btnCancel.setOnClickListener(new stopRun());
		

		if (!planIdExist(sp, pidKey)) {
			String json = us.getRawFile(R.raw.stability);
			int[] planids = tm.readServerJsonData(json);

			editor.putInt(pidKey, planids[0]);
			editor.commit();
		}
	}

	public void setBtnStart(boolean state) {
		if (edInternalTime != null) {
			edInternalTime.setEnabled(state);
		}
		if (btnStart != null) {
			btnStart.setEnabled(state);
		}

		if (lnProgress != null) {
			if (state) {
				lnProgress.setVisibility(View.GONE);
			} else {
				lnProgress.setVisibility(View.VISIBLE);
			}
		}
		if (btnCancel != null) {
			btnCancel.setEnabled(!state);
		}
	}

	@Override
	protected void onDestroy() {
		if (receiveTaskBroadcast != null) {
			unregisterReceiver(receiveTaskBroadcast);
		}
		super.onDestroy();
	}

	private class startRun implements OnClickListener {
		@Override
		public void onClick(View v) {
			float internalTime = 0;
			if (edInternalTime.getText().toString().trim().equals("")) {
				internalTime = Float.parseFloat(edInternalTime.getHint().toString()
						.trim());
			} else {
				internalTime = Float.parseFloat(edInternalTime.getText().toString()
						.trim());
			}

			updateDataAndRun(sp,pidKey,internalTime);
				//long t=(long) (internalTime*60*60*1000);
				//tm.startPlan(plan, false);

				// System.out.println(app.planEndTime);
			
		}
	}

	private class stopRun implements OnClickListener {
		@Override
		public void onClick(View v) {

			AlertDialog.Builder builder = new AlertDialog.Builder(
					StabilityTest.this);
			builder.setTitle("提示");
			builder.setMessage("确认要停止当前任务吗?")
					.setCancelable(false)
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									setBtnStart(true);
									//tm.stopPlan();
									runner.stop();

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
	}

	private class ReceiveTaskBroadcast extends BroadcastReceiver {

		@Override
		public void onReceive(Context arg0, Intent arg1) {

			if (arg1.getAction().equals(Constants.TASKSTATE_ACTION)) {
				Bundle bundle = arg1.getExtras();
				if (bundle != null) {
					int receiveValue = bundle.getInt(Constants.TASKSTATE_KEY);
					if (receiveValue == 0) {
						txtLeftTime.setText("");
						setBtnStart(true);
					} else {
						long leftTime = app.getPlanEndDateTime()
								- System.currentTimeMillis();
						txtLeftTime.setText("剩余时间："
								+ String.valueOf(DateTimes
										.getLeftTime(leftTime)));
					}
				}
			}
		}
	}

}

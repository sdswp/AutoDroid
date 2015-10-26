package com.gk.touchstone.core;

import java.util.List;

import com.gk.touchstone.GkApplication;
import com.gk.touchstone.R;
import com.gk.touchstone.activity.StabilityTest;
import com.gk.touchstone.db.DBManager;
import com.gk.touchstone.entity.Plan;
import com.gk.touchstone.entity.Task;
import com.gk.touchstone.entity.WifiAP;
import com.gk.touchstone.utils.DateTimes;
import com.gk.touchstone.utils.Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class BaseStability extends Activity {
//	private GkApplication app;
//	private Button btnStart, btnCancel;
//	private EditText edInternalTime;
//	private TextView txtLeftTime;
//	private View lnProgress;
	private Utils us;
//	private SharedPreferences sp;
//	private Editor editor;
//	private TaskManager tm;
//	private ReceiveTaskBroadcast receiveTaskBroadcast;
//	private String pidKey = Constants.KEY_STRESS_PID;
	protected PlanRunner runner;
//	private List<Task> tasks;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.base_statibility);

		// 设置窗体始终点亮
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		// 不打开软键盘
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		runner = new TestRunner(this);
		us = new Utils(this);
	}
	
	public void setBaseContentView(int layoutResId) {
		LinearLayout llContent = (LinearLayout) findViewById(R.id.view_content);
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = inflater.inflate(layoutResId, null);
		llContent.addView(v);
	}

//	private void initView() {
//		us = new Utils(this);
//		tm = new TaskManager(this);
//		app = (GkApplication) getApplication();
//
//		receiveTaskBroadcast = new ReceiveTaskBroadcast();
//		registerReceiver(receiveTaskBroadcast, new IntentFilter(
//				Constants.TASKSTATE_ACTION));
//
//		sp = getSharedPreferences(Constants.PREFS_STABILITY, MODE_PRIVATE);
//		editor = sp.edit();
//
//		Intent intent = getIntent();
//		String titleName = intent.getStringExtra("titlebar");
//
//		TextView title = (TextView) findViewById(R.id.txt_title);
//		title.setText(titleName);
//
//		lnProgress = (View) findViewById(R.id.ln_progress);
//		txtLeftTime = (TextView) findViewById(R.id.txt_leftTime);
//		btnStart = (Button) findViewById(R.id.btnStart);
//		btnCancel = (Button) findViewById(R.id.btnStop);
//		edInternalTime = (EditText) findViewById(R.id.internalTime);
//
//		btnStart.setOnClickListener(new startRun());
//		btnCancel.setOnClickListener(new stopRun());
//
//		runner = new RunNormalPlan(this);
//
////		if (!us.planIdExist(sp, pidKey)) {
////			String json = us.getRawFile(R.raw.stability);
////			int[] planids = tm.readServerJsonData(json, 1);
////
////			editor.putInt(pidKey, planids[0]);
////			editor.commit();
////		}
//	}
//
//	public void setBtnStart(boolean state) {
//		if (edInternalTime != null) {
//			edInternalTime.setEnabled(state);
//		}
//		if (btnStart != null) {
//			btnStart.setEnabled(state);
//		}
//
//		if (lnProgress != null) {
//			if (state) {
//				lnProgress.setVisibility(View.GONE);
//			} else {
//				lnProgress.setVisibility(View.VISIBLE);
//			}
//		}
//		if (btnCancel != null) {
//			btnCancel.setEnabled(!state);
//		}
//	}
//
//	@Override
//	protected void onDestroy() {
//		if (receiveTaskBroadcast != null) {
//			unregisterReceiver(receiveTaskBroadcast);
//		}
//		super.onDestroy();
//	}
//
//	private class startRun implements OnClickListener {
//		@Override
//		public void onClick(View v) {
//			float internalTime = 0;
//			if (edInternalTime.getText().toString().trim().equals("")) {
//				internalTime = Float.parseFloat(edInternalTime.getHint()
//						.toString().trim());
//			} else {
//				internalTime = Float.parseFloat(edInternalTime.getText()
//						.toString().trim());
//			}
//
//			if (us.planIdExist(sp, pidKey)) {
//				DBManager dbm = new DBManager(BaseStability.this);
//				int pid = sp.getInt(pidKey, 0);
//				Plan plan = dbm.queryPlan(sp.getInt(pidKey, 0));
//				tasks = dbm.queryTaskByPlanId(pid);
//
//				plan.setDuration(internalTime);
//				plan.setUpdateTime(us.getCurrentTime());
//				dbm.updatePlan(plan);
//
//				dbm.closedb();
//
//				if (isHaveTaskName(tasks, "AutoCall")) {
//					showCallNumberDialog(plan);
//				} else {
//					runner.start(plan);
//					setBtnStart(false);
//				}
//
//				// long t=(long) (internalTime*60*60*1000);
//				// tm.startPlan(plan, false);
//
//				// System.out.println(app.planEndTime);
//			}
//		}
//	}

	protected boolean isHaveTaskName(List<Task> tasklist, String tname) {
		for (Task t : tasklist) {
			if (t.getTaskName().contains(tname.trim())) {
				return true;
			}
		}
		return false;
	}
	
	protected boolean planIdExist(SharedPreferences sp, String keyStr) {
		if (sp.getInt(keyStr, 0) > 0) {
			return true;
		}
		return false;
	}
	
	protected void updateDataAndRun(SharedPreferences sp, String pidKey,
			float internalTime) {
		if (planIdExist(sp, pidKey)) {
			DBManager dbm = new DBManager(BaseStability.this);
			int pid = sp.getInt(pidKey, 0);

			Plan plan = dbm.queryPlan(pid);
			List<Task> tasks = dbm.queryTaskByPlanId(pid);

			plan.setDuration(internalTime);
			plan.setUpdateTime(us.getCurrentTime());
			dbm.updatePlan(plan);

			dbm.closedb();

			if (isHaveTaskName(tasks, "AutoCall")) {
				showCallNumberDialog(plan);
			} else {
				runner.start(plan);
				// setBtnStart(false);
			}
		}
	}

//	private class stopRun implements OnClickListener {
//		@Override
//		public void onClick(View v) {
//
//			AlertDialog.Builder builder = new AlertDialog.Builder(
//					BaseStability.this);
//			builder.setTitle("提示");
//			builder.setMessage("确认要停止当前任务吗?")
//					.setCancelable(false)
//					.setPositiveButton("确定",
//							new DialogInterface.OnClickListener() {
//								public void onClick(DialogInterface dialog,
//										int id) {
//									setBtnStart(true);
//									// tm.stopPlan();
//									runner.stop();
//
//								}
//							})
//					.setNegativeButton("取消",
//							new DialogInterface.OnClickListener() {
//								public void onClick(DialogInterface dialog,
//										int id) {
//									dialog.cancel();
//								}
//							});
//			AlertDialog alert = builder.create();
//			alert.show();
//		}
//	}

	protected void showCallNumberDialog(final Plan plan) {
		LayoutInflater factory = LayoutInflater.from(this);
		final View textEntryView = factory.inflate(
				R.layout.stability_edit_dialog, null);
		final EditText etCallNumber = (EditText) textEntryView
				.findViewById(R.id.et_callNumber);
		AlertDialog.Builder ad1 = new AlertDialog.Builder(this);
		ad1.setTitle("编辑手机号码");
		ad1.setIcon(android.R.drawable.ic_dialog_info);

		etCallNumber.setText("");

		ad1.setView(textEntryView);
		ad1.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int i) {
				String ssid = etCallNumber.getText().toString();

//				WifiAP wifiap = new WifiAP();
//				wifiap.setSsid(ssid);

				runner.start(plan);
				//setBtnStart(false);
			}
		});
		ad1.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int i) {
				dialog.cancel();
			}
		});
		ad1.show();// 显示对话框

	}

//	private class ReceiveTaskBroadcast extends BroadcastReceiver {
//
//		@Override
//		public void onReceive(Context arg0, Intent arg1) {
//
//			if (arg1.getAction().equals(Constants.TASKSTATE_ACTION)) {
//				Bundle bundle = arg1.getExtras();
//				if (bundle != null) {
//					int receiveValue = bundle.getInt(Constants.TASKSTATE_KEY);
//					if (receiveValue == 0) {
//						txtLeftTime.setText("");
//						setBtnStart(true);
//					} else {
//						long leftTime = app.getPlanEndDateTime()
//								- System.currentTimeMillis();
//						txtLeftTime.setText("剩余时间："
//								+ String.valueOf(DateTimes
//										.getLeftTime(leftTime)));
//					}
//				}
//			}
//		}
//	}

}

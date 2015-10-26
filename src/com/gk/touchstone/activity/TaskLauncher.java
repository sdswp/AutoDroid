package com.gk.touchstone.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.gk.touchstone.GkApplication;
import com.gk.touchstone.R;
import com.gk.touchstone.adapter.LauncherAdapter;
import com.gk.touchstone.db.DBManager;
import com.gk.touchstone.entity.Plan;
import com.gk.touchstone.entity.Task;
import com.gk.touchstone.core.PlanRunner;
import com.gk.touchstone.core.TestRunner;
import com.gk.touchstone.core.TestCase;
import com.gk.touchstone.core.TaskManager;
import com.gk.touchstone.core.Constants;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class TaskLauncher extends Activity {
	private ListView listview;
	private Button btnRun, btnStop;
	private TextView TitleView, TitleState;
	private LauncherAdapter adapter;
	private List<Task> tasklist;
	private Plan plan;
	private int resultOk;
	private TaskManager tm;
	private DBManager dbm;
	private boolean isNetTask;
	private GkApplication app;
	private ReceiveTaskBroadcast receiveTaskBroadcast;
	private PlanRunner runner;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// 设置窗体始终点亮
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		setContentView(R.layout.testcase_container);
		initView();
	}

	private void initView() {
		tm = new TaskManager(this);
		app = (GkApplication) getApplication();

		receiveTaskBroadcast = new ReceiveTaskBroadcast();
		registerReceiver(receiveTaskBroadcast, new IntentFilter(
				Constants.TASKSTATE_ACTION));

		Intent intent = getIntent();
		String planName = intent.getStringExtra("planName");
		int planId = intent.getIntExtra("planId", 0);
		resultOk = intent.getIntExtra("RESULT_OK", 0);

		if (intent.getIntExtra("planIdByServer", 0) != 0) {
			isNetTask = true;
		}

		if (isNetTask) {
			planId = intent.getIntExtra("planIdByServer", 0);
			// plan=app.getTempPlans()intent;
			tasklist = new ArrayList<Task>();
			for (Plan p : app.getTempPlans()) {
				if (p.id == planId) {
					plan = p;

					for (Task task : app.getTempTasks()) {
						if (task.getPlanId() == p.id) {
							tasklist.add(task);
						}
					}

					break;
				}
			}
		} else {
			dbm = new DBManager(this);
			tasklist = dbm.queryTaskByPlanId(planId);
			plan = dbm.queryPlan(planId);
			dbm.closedb();
		}

		listview = (ListView) findViewById(R.id.lv_tasklist);
		TitleView = (TextView) findViewById(R.id.txt_title);
		TitleState = (TextView) findViewById(R.id.txt_state);
		TitleView.setText(planName);

		btnRun = (Button) findViewById(R.id.title_rbtn1);
		btnStop = (Button) findViewById(R.id.title_rbtn2);

		btnRun.setText(R.string.plan_run);
		btnStop.setText(R.string.plan_stop);

		adapter = new LauncherAdapter(this, tasklist, R.layout.testcase_item,
				getRunState());
		listview.setAdapter(adapter);
		
		runner=new TestRunner(this);

		btnRun.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				btnRun.setVisibility(View.GONE);
				btnStop.setVisibility(View.VISIBLE);
				if (isNetTask) {
					tm.startNetPlan(plan, 0, 0);
				} else {
					runner.start(plan);
					//tm.startPlan(plan, false);
				}

				adapter.setState(getRunState());
				adapter.notifyDataSetChanged();
			}
		});

		btnStop.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				AlertDialog.Builder builder = new AlertDialog.Builder(
						TaskLauncher.this);
				builder.setTitle("提示");
				builder.setMessage("确认要停止当前任务吗?")
						.setCancelable(false)
						.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {

										btnRun.setVisibility(View.VISIBLE);
										btnStop.setVisibility(View.GONE);

										//tm.stopPlan(app.getTaskBases());
										runner.stop();

										adapter.setState(getRunState());
										adapter.notifyDataSetChanged();
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
		});

	}

	private List<Map<String, Object>> getRunState() {
		List<Map<String, Object>> maplist = null;

		List<TestCase> taskbases = app.getTaskBases();

		if (taskbases != null && taskbases.size() > 0) {
			maplist = new ArrayList<Map<String, Object>>();

			for (TestCase tb : taskbases) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("taskid", tb.taskid);
				map.put("taskCount", tb.originTaskCount - tb.taskCount);
				map.put("timeCount", "[" + tb.taskCount + ", " + tb.timeCount
						+ "]");
				maplist.add(map);
			}

			btnRun.setVisibility(View.GONE);
			btnStop.setVisibility(View.VISIBLE);

			TitleState.setText("Running");
		} else {
			btnRun.setVisibility(View.VISIBLE);
			btnStop.setVisibility(View.GONE);

			TitleState.setText("");
		}
		return maplist;
	}

	private class ReceiveTaskBroadcast extends BroadcastReceiver {

		@Override
		public void onReceive(Context arg0, Intent arg1) {

			if (arg1.getAction().equals(Constants.TASKSTATE_ACTION)) {
				Bundle bundle = arg1.getExtras();
				if (bundle != null) {
					int receiveValue = bundle.getInt(Constants.TASKSTATE_KEY);
					if (receiveValue == 0) {
						adapter.setState(null);
						btnRun.setVisibility(View.VISIBLE);
						btnStop.setVisibility(View.GONE);
						TitleState.setText("");
					} else {
						adapter.setState(getRunState());
					}
					adapter.notifyDataSetChanged();
				}
			}
		}
	}

	@Override
	protected void onDestroy() {
		if (receiveTaskBroadcast != null) {
			unregisterReceiver(receiveTaskBroadcast);
		}
		super.onDestroy();
	}

	@Override
	public void onBackPressed() {
		Intent data = new Intent();
		// data.putExtra("selectFile", fname);
		setResult(resultOk, data);
		finish();

	}

}

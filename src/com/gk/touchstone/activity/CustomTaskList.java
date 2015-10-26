package com.gk.touchstone.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.gk.touchstone.GkApplication;
import com.gk.touchstone.R;
import com.gk.touchstone.adapter.PlanAdapter;
import com.gk.touchstone.db.DBManager;
import com.gk.touchstone.entity.Plan;
import com.gk.touchstone.core.PlanRunner;
import com.gk.touchstone.core.TestRunner;
import com.gk.touchstone.core.TaskManager;
import com.gk.touchstone.core.Constants;
import com.gk.touchstone.core.TestCase;

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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class CustomTaskList extends Activity {
	private ListView listviewFunc;
	private PlanAdapter adapter;
	private Button btnRun, btnStop;
	private List<Plan> planlist;
	private TaskManager tm;
	private ReceiveTaskBroadcast receiveTaskBroadcast;
	private DBManager dbm;
	private GkApplication app;
	private int RESULT_OK = 1;
	private PlanRunner runner;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 设置窗体始终点亮
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.custom_plan);

		initView();
	}

	private void initView() {
		app = (GkApplication) getApplication();

		tm = new TaskManager(this);

		receiveTaskBroadcast = new ReceiveTaskBroadcast();
		registerReceiver(receiveTaskBroadcast, new IntentFilter(
				Constants.TASKSTATE_ACTION));

		Intent intent = getIntent();
		String titlebar = intent.getStringExtra("titlebar");

		// planTips = (TextView) findViewById(R.id.txt_planTips);
		listviewFunc = (ListView) findViewById(R.id.lv_customPlan);
		TextView TitleView = (TextView) findViewById(R.id.txt_title);
		TitleView.setText(titlebar);

		btnRun = (Button) findViewById(R.id.title_rbtn1);
		btnStop = (Button) findViewById(R.id.title_rbtn2);

		btnRun.setText(R.string.plan_run);
		btnStop.setText(R.string.plan_stop);

		dbm = new DBManager(this);
		planlist = dbm.queryPlansByCreateWay(Constants.MODE_PERFORMANCE);
		dbm.closedb();

		adapter = new PlanAdapter(this, planlist, getRunState());
		listviewFunc.setAdapter(adapter);
		
		runner=new TestRunner(this);

		btnRun.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				List<Plan> selectPlanList = new ArrayList<Plan>();
				HashMap<Integer, Boolean> cbState = adapter.state;
				for (int j = 0; j < adapter.getCount(); j++) {
					if (cbState.get(j) != null) {
						Plan plan = (Plan) adapter.getItem(j);
						selectPlanList.add(plan);
					}
				}

				if (selectPlanList.size() <= 0) {
					Toast.makeText(CustomTaskList.this, "请选择至少一个测试计划", 3000)
							.show();
					return;
				}
				// TODO: 扩展支持多个Plan串行。
				
				//tm.startPlan(selectPlanList.get(0), false);
				runner.start(selectPlanList.get(0));

				adapter.setState(getRunState());
				adapter.notifyDataSetChanged();
			}
		});

		btnStop.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				AlertDialog.Builder builder = new AlertDialog.Builder(
						CustomTaskList.this);
				builder.setTitle("提示");
				builder.setMessage("确认要停止当前任务吗?")
						.setCancelable(false)
						.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {

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

		listviewFunc.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				int pid = planlist.get(arg2).getId();
				Intent intent = new Intent(CustomTaskList.this,
						TaskLauncher.class);
				Bundle bundle = new Bundle();
				bundle.putInt("RESULT_OK", RESULT_OK);

				intent.putExtra("planName", planlist.get(arg2).getPlanName());
				intent.putExtra("planId", pid);

				intent.putExtras(bundle);
				startActivityForResult(intent, 0);
				// startActivity(intent);
			}
		});
	}

	private Map<String, Object> getRunState() {
		Map<String, Object> mapState = null;

		List<TestCase> taskbases = app.getTaskBases();
		if (taskbases != null && taskbases.size() > 0) {
			mapState = new HashMap<String, Object>();
			int pid = taskbases.get(0).planid;

			mapState.put("pid", pid);
			mapState.put("state", "Running");

			btnRun.setVisibility(View.GONE);
			btnStop.setVisibility(View.VISIBLE);
		} else {
			btnRun.setVisibility(View.VISIBLE);
			btnStop.setVisibility(View.GONE);
		}
		return mapState;
	}

	private class ReceiveTaskBroadcast extends BroadcastReceiver {

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			// String bcAction = getResources().getString(
			// R.string.customPlanAction);
			// String bcName = getResources().getString(
			// R.string.customPlanBroadCastName);

			if (arg1.getAction().equals(Constants.TASKSTATE_ACTION)) {
				Bundle bundle = arg1.getExtras();
				if (bundle != null) {
					int receiveValue = bundle.getInt(Constants.TASKSTATE_KEY);
					if (receiveValue == 0) {
						adapter.setState(null);
						btnRun.setVisibility(View.VISIBLE);
						btnStop.setVisibility(View.GONE);
					} else {
						adapter.setState(getRunState());
					}
					adapter.notifyDataSetChanged();

				}
			}
		}
	}

	// public int threadNum() {
	// ThreadGroup threadGroup = Thread.currentThread().getThreadGroup()
	// .getParent();
	//
	// Thread[] list = new Thread[threadGroup.activeCount()];
	// threadGroup.enumerate(list, true);
	//
	// return list.length;
	// }
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode == RESULT_OK) {
			// String mFile = data.getStringExtra("selectFile");
			// fileSelect.setText(mFile);
			adapter.setState(getRunState());
			adapter.notifyDataSetChanged();

		}
	}

	@Override
	protected void onDestroy() {
		if (receiveTaskBroadcast != null) {
			unregisterReceiver(receiveTaskBroadcast);
		}
		super.onDestroy();
	}

}

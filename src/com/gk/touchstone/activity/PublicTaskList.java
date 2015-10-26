package com.gk.touchstone.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.gk.touchstone.GkApplication;
import com.gk.touchstone.R;
import com.gk.touchstone.adapter.PlanAdapter;
import com.gk.touchstone.network.AsyncGetJson;
import com.gk.touchstone.network.HttpUtils;
import com.gk.touchstone.core.TestCase;
import com.gk.touchstone.core.TaskManager;
import com.gk.touchstone.core.Constants;
import com.gk.touchstone.entity.Plan;
import com.gk.touchstone.utils.Utils;

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

public class PublicTaskList extends Activity {
	public static int loading_process;
	private ListView netPlanView;
	private Button btnRun, btnStop;
	private Utils us;
	private GkApplication app;
	private TaskManager tm;
	private PlanAdapter adapter;
	private List<Plan> planlist;
	private HttpUtils hs;
	private ReceiveTaskBroadcast receiveTaskBroadcast;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 设置窗体始终点亮
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.net_report);

		initView();
	}

	private void initView() {
		us = new Utils(this);
		tm = new TaskManager(this);
		hs = new HttpUtils(this);

		app = (GkApplication) getApplication();
		receiveTaskBroadcast = new ReceiveTaskBroadcast();
		registerReceiver(receiveTaskBroadcast, new IntentFilter(
				Constants.TASKSTATE_ACTION));

		Intent intent = getIntent();
		TextView TitleView = (TextView) findViewById(R.id.txt_title);
		TitleView.setText(intent.getStringExtra("titlebar"));

		btnRun = (Button) findViewById(R.id.title_rbtn1);
		btnStop = (Button) findViewById(R.id.title_rbtn2);

		btnRun.setText(R.string.plan_run);
		btnRun.setOnClickListener(new startRun());
		btnStop.setText(R.string.plan_stop);
		btnStop.setOnClickListener(new stopTask());

		netPlanView = (ListView) findViewById(R.id.lv_planlist);

		planlist = new ArrayList<Plan>();
		adapter = new PlanAdapter(this, planlist, getRunState());

		netPlanView.setAdapter(adapter);

		if (hs.isConnect()) {
			AsyncGetJson async = new AsyncGetJson(this, adapter);
			async.execute(us.getServerUrl("sharetask.aspx"));
		}

		netPlanView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Plan plan = app.getTempPlans().get(arg2);
				int pid = plan.getId();

				Intent intent = new Intent(PublicTaskList.this,
						TaskLauncher.class);
				Bundle bundle = new Bundle();
				bundle.putInt("RESULT_OK", RESULT_OK);

				intent.putExtra("planName", plan.getPlanName());
				intent.putExtra("planId", String.valueOf(pid));
				intent.putExtra("planIdByServer", pid);

				intent.putExtras(bundle);
				startActivityForResult(intent, 0);
			}
		});
	}

	private class startRun implements OnClickListener {
		@Override
		public void onClick(View v) {
			btnRun.setVisibility(View.GONE);
			btnStop.setVisibility(View.VISIBLE);

			List<Plan> selectPlanList = new ArrayList<Plan>();
			HashMap<Integer, Boolean> cbState = adapter.state;
			for (int j = 0; j < adapter.getCount(); j++) {
				if (cbState.get(j) != null) {
					Plan plan = (Plan) adapter.getItem(j);
					selectPlanList.add(plan);
				}
			}

			tm.startNetPlan(selectPlanList.get(0), 0, 0);

			adapter.setState(getRunState());
			adapter.notifyDataSetChanged();
		}
	};

	private class stopTask implements OnClickListener {
		@Override
		public void onClick(View v) {
			AlertDialog.Builder builder = new AlertDialog.Builder(
					PublicTaskList.this);
			builder.setTitle("提示");
			builder.setMessage("确认要停止当前任务吗?")
					.setCancelable(false)
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {

									btnRun.setVisibility(View.VISIBLE);
									btnStop.setVisibility(View.GONE);

									tm.stopPlan();

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
	};

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

	@Override
	protected void onDestroy() {
		if (receiveTaskBroadcast != null) {
			unregisterReceiver(receiveTaskBroadcast);
		}
		super.onDestroy();
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode == RESULT_OK) {
			adapter.setState(getRunState());
			adapter.notifyDataSetChanged();

		}
	}

}

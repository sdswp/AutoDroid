package com.gk.touchstone.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import com.gk.touchstone.R;
import com.gk.touchstone.adapter.ReportPlanAdapter;
import com.gk.touchstone.db.DBManager;
import com.gk.touchstone.entity.Plan;
import com.gk.touchstone.entity.Task;
import com.gk.touchstone.network.HttpUtils;
import com.gk.touchstone.utils.Utils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class ReportPlans extends Activity {
	private ListView listv;
	private Button btnClean, btnManage, btnUpload;
	private View lnManage, lnDesc;
	private ReportPlanAdapter adapter;
	private Utils us;
	private List<Plan> planlist;
	private HttpUtils hs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// 设置窗体始终点亮
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.test_report);

		initView();
	}

	private void initView() {
		us = new Utils(this);
		hs = new HttpUtils(this);

		Intent intent = getIntent();
		TextView TitleView = (TextView) findViewById(R.id.txt_title);
		TitleView.setText(intent.getStringExtra("titlebar"));

		btnManage = (Button) findViewById(R.id.title_rbtn);
		btnManage.setText("管理");

		lnManage = (View) findViewById(R.id.ln_manage);
		lnDesc = (View) findViewById(R.id.ln_desc);
		btnUpload = (Button) findViewById(R.id.btn_upload);
		btnClean = (Button) findViewById(R.id.btn_delReport);

		listv = (ListView) findViewById(R.id.lv_report);

		DBManager dbm = new DBManager(this);
		planlist = getPlanLogExist(dbm.queryAllPlan());
		dbm.closedb();

		List<Task> tasks = newTasksFromTaskLog(planlist);
		for (Task t : tasks) {
			System.out.println(t.getPlanId());
		}

		// List<Plan> newplans = new ArrayList<Plan>();
		// List<Task> newTask = new ArrayList<Task>();
		// int rf = 0;
		// for (Plan plan : planlist) {
		// List<Task> tasks = dbm.queryTaskByPlanId(plan.getId());
		//
		// Plan p = plan;
		// int pId = us.getRandomInt();
		// p.setId(pId);
		// newplans.add(p);
		//
		// for (Task t : tasks) {
		// String[] resultFiles = t.getResultFile().split(".log");
		//
		// for (String s : resultFiles) {
		// rf++;
		// // Task task = new Task();
		// // int tId = us.getRandomInt();
		// // task.setId(tId);
		// // task.setPlanId(pId);
		// // task.setResultFile(s + ".log");
		// // newTask.add(task);
		// }
		// }
		//
		// }
		//
		// System.out.println(newplans.size());
		// System.out.println(newTask.size());

		adapter = new ReportPlanAdapter(this, planlist);
		listv.setAdapter(adapter);

		listv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent intent = null;

				int pid = planlist.get(arg2).getId();
				String pname = planlist.get(arg2).getPlanName();
				intent = new Intent(ReportPlans.this, ReportTasks.class);
				intent.putExtra("planId", String.valueOf(pid));
				intent.putExtra("planName", pname);

				startActivity(intent);
			}

		});

		btnManage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (lnManage.getVisibility() == View.GONE) {
					lnManage.setVisibility(View.VISIBLE);
					lnDesc.setVisibility(View.GONE);
				} else {
					lnManage.setVisibility(View.GONE);
					lnDesc.setVisibility(View.VISIBLE);
				}
			}
		});

		btnUpload.setOnClickListener(new startUploadLog());
		btnClean.setOnClickListener(new startCleanLog());
	}

	// private List<Plan> newPlansFromTaskLog(List<Plan> plans){
	// List<Plan> newplans = new ArrayList<Plan>();
	//
	// for (Plan plan : plans) {
	// List<Task> tasks = dbm.queryTaskByPlanId(plan.getId());
	//
	// for (Task t : tasks) {
	// String[] resultFiles = t.getResultFile().split(".log");
	// for (String s : resultFiles) {
	// Plan p = plan;
	// int pId = us.getRandomInt();
	// p.setId(pId);
	// newplans.add(p);
	// }
	// break;
	// }
	//
	// }
	// return newplans;
	// }

	private List<Task> newTasksFromTaskLog(List<Plan> plans) {
		DBManager dbm = new DBManager(this);
		List<Task> newTask = new ArrayList<Task>();

		for (Plan plan : plans) {
			List<Task> tasks = dbm.queryTaskByPlanId(plan.getId());

			for (Task t : tasks) {
				String[] resultFiles = t.getResultFile().split(".log");

				for (String s : resultFiles) {
					Task task = new Task();
					// int tId = us.getRandomInt();
					// task.setId(tId);

					int ranID = us.getRandomInt();
					String idd = String.valueOf(plan.getId())
							+ String.valueOf(ranID);
					int newid = Integer.parseInt(idd);

					task.setPlanId(newid);
					task.setResultFile(s + ".log");
					newTask.add(task);
				}
			}

		}
		dbm.closedb();

		return newTask;
	}

	private class startUploadLog implements OnClickListener {
		@Override
		public void onClick(View v) {
			if (hs.isConnect()) {
				if (getSelectTasks() != null) {
					us.uploadLog(getSelectTasks());
				}
			} else {
				us.myToast("", "网络未连接！");
			}
		}
	}

	private class startCleanLog implements OnClickListener {
		@Override
		public void onClick(View v) {
			if (getSelectTasks() != null) {
				if (us.deleteLogs(getSelectTasks())) {
					adapter.setItemList(getPlanLogExist(getSelectPlans()));
					adapter.notifyDataSetChanged();
				}
			}
		}
	}

	private List<Plan> getSelectPlans() {
		HashMap<Integer, Boolean> cbState = adapter.state;

		if (adapter.getCount() == 0) {
			us.myToast("", "请至少选择一项测试报告！");
			return null;
		}
		List<Plan> selectPlans = new ArrayList<Plan>();
		for (int j = 0; j < adapter.getCount(); j++) {
			if (cbState.get(j) != null) {
				Plan plan = (Plan) adapter.getItem(j);
				selectPlans.add(plan);
			}
		}
		return selectPlans;
	}

	private List<Task> getSelectTasks() {
		DBManager dbm = new DBManager(this);
		List<Plan> plans = getSelectPlans();
		if (plans == null) {
			return null;
		}
		List<Task> tasks = new ArrayList<Task>();
		for (Plan p : plans) {
			List<Task> queryTasks = dbm.queryTaskByPlanId(p.getId());
			tasks.addAll(queryTasks);
		}
		dbm.closedb();
		return tasks;
	}

	private List<Plan> getPlanLogExist(List<Plan> plans) {
		DBManager dbm = new DBManager(this);
		List<Plan> saveplan = new ArrayList<Plan>();
		for (Plan plan : plans) {
			List<Task> tasklist = dbm.queryTaskByPlanId(plan.getId());
			for (Task task : tasklist) {
				String lastLog = us.getLastLog(task.getResultFile());
				File fis = new File(getFilesDir().getPath() + "//" + lastLog);
				if (fis.length() > 0) {
					saveplan.add(plan);
					break;
				}
				// // 运行任务时，如果leftCount=0，则
				// String[] files = task.getResultFile().split(".log");
				// for (String s : files) {
				// File fis = new File(getFilesDir().getPath() + "//" + s
				// + ".log");
				// if (fis.exists() && fis.length() > 0) {
				// saveplan.add(plan);
				// break;
				// }
				// }
			}
		}
		dbm.closedb();
		return saveplan;
	}

	@Override
	protected void onDestroy() {

		super.onDestroy();
	}

}

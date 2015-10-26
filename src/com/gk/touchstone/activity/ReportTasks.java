package com.gk.touchstone.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.gk.touchstone.GkApplication;
import com.gk.touchstone.R;
import com.gk.touchstone.adapter.TaskAdapter;
import com.gk.touchstone.db.DBManager;
import com.gk.touchstone.entity.Plan;
import com.gk.touchstone.entity.Report;
import com.gk.touchstone.entity.Task;
import com.gk.touchstone.core.Constants;
import com.gk.touchstone.core.ResultFileParse;
import com.gk.touchstone.utils.Utils;

public class ReportTasks extends Activity {
	private DBManager dbm;
	private TextView TitleView, tips;
	private List<Task> tasks;
	private Button btnClean;
	private RadioButton rbResult, rbParams;
	private Utils us;
	private TaskAdapter adapter;
	private List<Report> reportList;
	private ResultFileParse prl;
	private GkApplication app;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// 设置窗体始终点亮
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.report_tasks);

		initView();
	}

	private void initView() {
		us = new Utils(this);
		prl = new ResultFileParse(this);
		app = (GkApplication) this.getApplication();

		TitleView = (TextView) findViewById(R.id.txt_title);
		tips = (TextView) findViewById(R.id.txt_planTips);

		ListView listv = (ListView) findViewById(R.id.lv_tasks);
		btnClean = (Button) findViewById(R.id.title_rbtn);
		btnClean.setText("清空");

		// rbResult = (RadioButton) findViewById(R.id.rb_result);
		// rbParams = (RadioButton) findViewById(R.id.rb_params);

		adapter = diffTaskMode();
		listv.setAdapter(adapter);

		listv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Task task = (Task) adapter.getItem(arg2);
				reportView(task, arg2);
			}

		});

	}

	private void reportView(Task task, int arg2) {

		List<Report> reports = prl.getReportByTask(reportList, task,
				Constants.FAIL);

		if (reports == null) {
			us.myToast(task.getDisplayName(), "测试结果异常！");
			app.setTaskRepots(null);
		} else if (reports.size() > 0) {
			app.setTaskRepots(reports);
			Intent intent = new Intent(ReportTasks.this, ReportTaskDetail.class);
			// intent.putExtra("resultFile", task.getResultFile());
			intent.putExtra("displayName", task.getDisplayName());
			startActivity(intent);
		} else {
			us.myToast(task.getDisplayName(), "测试结果全部PASS！");
			app.setTaskRepots(null);
		}
	}

	// private void paramsView(Task task, int arg2) {
	// Class<?> clazz = null;
	// try {
	// clazz = Class.forName(Constants.packageActivity
	// + task.getTaskName());
	// } catch (Exception e) {
	//
	// }
	//
	// Intent intent = new Intent(this, clazz);
	// intent.putExtra("reportTaskValue", task.getFormValue());
	//
	// startActivity(intent);
	// }

	// 计划任务和单任务公用这个界面做的一些区分。
	private TaskAdapter diffTaskMode() {
		dbm = new DBManager(this);
		Intent intent = getIntent();
		Plan plan = null;

		ResultFileParse prl = new ResultFileParse(this);

		if (intent.getStringExtra("planId") != null) {
			int pid = Integer.parseInt(intent.getStringExtra("planId"));
			tasks = dbm.queryTaskByPlanId(pid);
			plan = dbm.queryPlan(pid);
			reportList = prl.getReportFromTasks(tasks);
			// Collections.reverse(reportList);

			adapter = new TaskAdapter(this, tasks, reportList,
					R.layout.report_tasks_item, false);

			btnClean.setOnClickListener(new cleanReports());
		} else {
			tasks = dbm.queryTaskByPlanId(0);
			reportList = prl.getReportFromTasks(tasks);

			adapter = new TaskAdapter(this, tasks, reportList,
					R.layout.report_tasks_item, true);
			btnClean.setOnClickListener(new cleanSingleReport());
		}

		// 标题栏区分计划和单任务读取
		if (intent.getStringExtra("planName") != null) {
			TitleView.setText(intent.getStringExtra("planName"));

			String tipStr = plan.getExceptions().trim();
			if (!tipStr.equals("")) {
				tips.setText("发生异常时间：" + tipStr);
			}
		} else {
			TitleView.setText(intent.getStringExtra("titlebar"));
		}

		dbm.closedb();
		return adapter;
	}

	private class cleanSingleReport implements OnClickListener {
		@Override
		public void onClick(View v) {
			HashMap<Integer, Boolean> cbState = adapter.state;
			List<Task> selectTasks = new ArrayList<Task>();
			for (int j = 0; j < adapter.getCount(); j++) {
				if (cbState.get(j) != null) {
					Task task = (Task) adapter.getItem(j);
					selectTasks.add(task);
				}
			}

			if (us.deleteLogs(selectTasks)) {
				adapter.setItemList(getTaskLogExist(selectTasks));
				adapter.notifyDataSetChanged();
			}
		}
	};

	private class cleanReports implements OnClickListener {
		@Override
		public void onClick(View v) {
			if (us.deleteLogs(tasks)) {
				adapter.setItemList(getTaskLogExist(tasks));
				adapter.notifyDataSetChanged();
			}
		}
	};

	private List<Task> getTaskLogExist(List<Task> tasks) {
		List<Task> savetask = new ArrayList<Task>();

		for (Task task : tasks) {
			File fis = new File(getFilesDir().getPath() + "//"
					+ task.getResultFile());
			if (fis.length() > 0) {
				savetask.add(task);
				break;
			}

		}
		return savetask;
	}

}
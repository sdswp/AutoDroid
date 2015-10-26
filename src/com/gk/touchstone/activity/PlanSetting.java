package com.gk.touchstone.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.gk.touchstone.GkApplication;
import com.gk.touchstone.R;
import com.gk.touchstone.adapter.TaskSelectAdapter;
import com.gk.touchstone.entity.Plan;
import com.gk.touchstone.entity.PlanType;
import com.gk.touchstone.entity.Task;
import com.gk.touchstone.core.TaskManager;
import com.gk.touchstone.core.Constants;
import com.gk.touchstone.utils.Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

public class PlanSetting extends Activity {
	private GkApplication app;
	private ListView lvTimeRatio, lvRunNum;
	private Spinner spinner;
	private Spinner spinnerType;
	private ArrayAdapter<PlanType> planTypeAdapter;
	private EditText etTaskNum;
	private EditText etPlanName;
	private EditText etplanNumber;
	private TaskSelectAdapter taskAdapter;
	private List<Task> tasklist;
	private Task task;
	private Utils us;
	private TaskManager tm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.plan_setting);
		initView();
	}

	private void initView() {
		ActivityManager.getInstance().addActivity(this);

		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		us = new Utils(this);
		app = (GkApplication) this.getApplication();
		tm = new TaskManager(this);

		tasklist = app.getSelectTasks();

		etPlanName = (EditText) findViewById(R.id.planName);
		UUID uuid = UUID.randomUUID();
		CharSequence cs = "Plan" + uuid.toString().split("-")[0];
		etPlanName.setHint(cs);

		etplanNumber = (EditText) findViewById(R.id.planNumber);

		lvTimeRatio = (ListView) findViewById(R.id.lv_timeRatio);
		// lvRunNum=(ListView) findViewById(R.id.lv_runNum);
		TextView txtTitle = (TextView) findViewById(R.id.txt_title);
		txtTitle.setText("保存测试计划");

		spinnerType = (Spinner) findViewById(R.id.spinner_type);

		planTypeAdapter = new ArrayAdapter<PlanType>(this,
				android.R.layout.simple_spinner_item, getPlanTypes());
		planTypeAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerType.setAdapter(planTypeAdapter);

		taskAdapter = new TaskSelectAdapter(this, tasklist,
				R.layout.select_task_item);

		lvTimeRatio.setAdapter(taskAdapter);

		lvTimeRatio.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				task = (Task) arg0.getItemAtPosition(arg2);

				LayoutInflater factory = LayoutInflater.from(PlanSetting.this);
				View textEntryView = factory.inflate(R.layout.task_dialog1,
						null);
				etTaskNum = (EditText) textEntryView
						.findViewById(R.id.et_taskNum);
				etTaskNum.setHint(String.valueOf(task.getTaskCount()));

				AlertDialog.Builder ad1 = new AlertDialog.Builder(
						PlanSetting.this);
				ad1.setTitle("设置 " + task.getDisplayName() + " 任务次数");
				ad1.setIcon(android.R.drawable.ic_dialog_info);
				ad1.setView(textEntryView);

				ad1.setPositiveButton("确定",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int i) {
								int tnum = 0;
								if (etTaskNum.getText().toString().equals("")) {
									tnum = Integer.parseInt(etTaskNum.getHint()
											.toString());
								} else {
									tnum = Integer.parseInt(etTaskNum.getText()
											.toString());
								}
								task.setTaskCount(tnum);
								task.setOriginCount(tnum);

								taskAdapter.setItemList(tasklist);
								taskAdapter.notifyDataSetChanged();
							}
						});
				ad1.setNegativeButton("取消",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int i) {
							}
						});
				ad1.show();

			}
		});

		Button btnSave = (Button) findViewById(R.id.title_rbtn);
		btnSave.setText("保存");

		btnSave.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Plan plan = getPlan(etPlanName, spinnerType);
				tm.addPlanAndTask(plan, tasklist);

				Intent intent = new Intent(PlanSetting.this,
						CustomTaskList.class);
				intent.putExtra("titlebar", "当前自定义测试计划");
				startActivity(intent);
				// overridePendingTransition(R.anim.animnone,R.anim.animnone);

				// made by David start
				// Intent intent = new Intent();
				// intent.setClass(PlanSetting.this, CustomPlan.class);
				// startActivity(intent);
				// made by David end
				// finish();
				// Intent intent = new Intent();
				// intent.setClass(PlanSetting.this, CustomTaskList.class);
				// startActivity(intent);
				// app.mTabHost.setCurrentTab(0);
				app.setSelectTasks(null);

				ActivityManager.getInstance().clearActivity();

			}
		});

	}

	// 新建计划
	private Plan getPlan(EditText etName, Spinner sType) {
		Plan plan = new Plan();
		int pn = 0;
		if (!etName.getText().toString().equals("")) {
			plan.setPlanName(etName.getText().toString());
		} else {
			plan.setPlanName(etName.getHint().toString());
		}

		PlanType p = (PlanType) sType.getSelectedItem();
		plan.setPlanType(p.getId());
		plan.setCreateWay(Constants.MODE_PERFORMANCE);

		// 设定计划次数
		// if (!etNumber.getText().toString().equals("")) {
		// pn = Integer.parseInt(etNumber.getText().toString().trim());
		// } else {
		// pn = Integer.parseInt(etNumber.getHint().toString().trim());
		// }
		plan.setExceptions("");
		plan.setPlanNumber(1);
		plan.setIsFinish(0);
		plan.setIsFinish(0);
		plan.setCreateTime(us.getCurrentTime());
		plan.setUpdateTime(us.getCurrentTime());

		return plan;
	}

	private List<PlanType> getPlanTypes() {
		String[] planTypes = { "串行", "并行" };
		List<PlanType> ptlist = new ArrayList<PlanType>();
		for (int n = 0; n < planTypes.length; n++) {
			PlanType pt = new PlanType(n, planTypes[n]);
			ptlist.add(pt);
		}

		return ptlist;
	}

}

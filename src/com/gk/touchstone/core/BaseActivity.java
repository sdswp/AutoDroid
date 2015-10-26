package com.gk.touchstone.core;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.gk.touchstone.GkApplication;
import com.gk.touchstone.R;
import com.gk.touchstone.db.DBManager;
import com.gk.touchstone.entity.Form;
import com.gk.touchstone.entity.Task;
import com.gk.touchstone.entity.Case;
import com.gk.touchstone.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class BaseActivity extends Activity {
	private Button btnStart, btnCancel, btnSave;
	private TextView titleName;
	private Utils us;
	private String titleBarName = "";
	private String className = this.getClass().getSimpleName();
	private ReceiveTaskByActivity receiveTask;
	protected GkApplication app;
	private DBManager dbm;
	private TestCase taskbase = null;
	private TaskManager tm;
	private String testCaseValue;
	private String testCaseName;
	private int testCaseId;
	private int resultOk;
	private Gson gson;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// 设置窗体始终点亮
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.base);

		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		us = new Utils(this);
		tm = new TaskManager(this);
		app = (GkApplication) getApplication();
		gson = new Gson();

		Intent intent = getIntent();
		// testCase = (TestCase) intent.getSerializableExtra("testCase");
		testCaseValue = intent.getStringExtra("testCaseValue");
		testCaseName = intent.getStringExtra("testCaseName");
		testCaseId = intent.getIntExtra("testCaseId", 0);

		resultOk = intent.getIntExtra("RESULT_OK", 0);
		// testCaseId=intent.getIntExtra("testCaseId", 0);

		if (testCaseName != null) {
			titleBarName = testCaseName;
		}

		titleName = (TextView) findViewById(R.id.txt_title);
		titleName.setText(titleBarName);

		btnSave = (Button) findViewById(R.id.title_rbtn);
		btnSave.setText(R.string.save);
		btnSave.setOnClickListener(new saveFormValue());

		btnStart = (Button) findViewById(R.id.btn_start);
		btnCancel = (Button) findViewById(R.id.btn_stop);
		btnStart.setOnClickListener(new startTask());
		btnCancel.setOnClickListener(new cancelRun());

		receiveTask = new ReceiveTaskByActivity();
		registerReceiver(receiveTask, new IntentFilter(
				Constants.TASKSTATE_ACTION));
	}
	
	public View createView(int WidgetNumber,int id, String value) {
		if (WidgetNumber == 0) {
			EditText et = new EditText(this);
			et.setText(value);
			et.setId(id);
			return et;
		} else if (WidgetNumber == 1) {
			TextView tv = new TextView(this);
			tv.setText(value);
			tv.setId(id);
			return tv;
		} else if (WidgetNumber == 2) {
			Button btn = new Button(this);
			btn.setText(value);
			btn.setId(id);
			return btn;
		} else {
			return null;
		}
	}

	public void setBaseContentView() {//(int layoutResId) {
		LinearLayout layout = (LinearLayout) findViewById(R.id.view_content);
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		//View v = inflater.inflate(layoutResId, null);
		//layout.addView(v);

		if (testCaseValue != null && !testCaseValue.equals("")) {
/*			Map<String, Object> tcMap = gson.fromJson(testCaseValue,
					new TypeToken<Map<String, Object>>() {
					}.getType());

			// 遍历从原TestCase获取的value转换成的map，获取map string，反射查找EditText ID
			Set<String> keys = tcMap.keySet();
			for (Iterator<String> it = keys.iterator(); it.hasNext();) {
				String str = (String) it.next();
				String vals = us.objToStr(tcMap.get(str));

				EditText et = (EditText) v.findViewById(getResources()
						.getIdentifier(str, "id", getPackageName()));
				et.setText(vals);

			}*/
						
			List<Form> forms = gson.fromJson(testCaseValue, new TypeToken<List<Form>>(){}.getType());
			for(Form fs :forms){
				TextView tv=new TextView(this);
				tv.setText(fs.getName());
				layout.addView(tv);
				
				View view = createView(fs.getType(),1, fs.getValue());
				
				layout.addView(view);
			}
		}
		// //ReportTask查看测试报告配置参数
		// Intent intent = getIntent();
		// String reportTaskValue=intent.getStringExtra("reportTaskValue");
		// if(reportTaskValue!=null && !reportTaskValue.equals("")){
		// JsonValidator jv=new JsonValidator();
		// if(jv.validate(reportTaskValue)){
		// getReportFormValue(reportTaskValue,v);
		// }
		// btnStart.setVisibility(View.GONE);
		// btnCancel.setVisibility(View.GONE);
		// btnSave.setVisibility(View.GONE);
		// }

	}

	// 从报告页面打开
	private void getReportFormValue(String jsonStr, View v) {
		Map<String, Object> tcMap = gson.fromJson(jsonStr,
				new TypeToken<Map<String, Object>>() {
				}.getType());

		// 遍历从原TestCase获取的value转换成的map，获取map string，反射查找EditText ID
		Set<String> keys = tcMap.keySet();
		for (Iterator<String> it = keys.iterator(); it.hasNext();) {
			String str = (String) it.next();
			String vals = us.objToStr(tcMap.get(str));

			int rid = getResources().getIdentifier(str, "id", getPackageName());

			EditText et = (EditText) v.findViewById(rid);

			et.setText(vals);
		}
	}

	private class startTask implements OnClickListener {
		@Override
		public void onClick(View v) {
			// if (saveValue()) {
			if (app.getTaskBases() != null && app.getTaskBases().size() > 0) {
				us.myToast(titleBarName, R.string.plan_error1);
			} else {
				taskbase = initViewTask();
				if (tm.isHardwareExist(taskbase)) {
					taskbase.Start();
					setButtonEnabled(false);
				} else {
					us.myToast(taskbase.displayName, "场景，设备不支持！");
					app.setTaskBases(null);
				}
			}
			// }

		}
	}

	private class cancelRun implements OnClickListener {
		@Override
		public void onClick(View v) {
			if (taskbase != null) {
				taskbase.Stop();

				tm.updateTaskBases(taskbase.taskid);
				setButtonEnabled(true);

				// unRegisterTaskBroadCast(receiveTask);
			}
		}
	}

	private class saveFormValue implements OnClickListener {
		@Override
		public void onClick(View v) {
			Intent data = new Intent();
			data.putExtra("saveClassName", className);
			data.putExtra("testCaseValue", saveTestCaseValue(testCaseValue));

			setResult(resultOk, data);

			us.myToast(titleBarName, R.string.save_data);
			finish();
		}
	}

	private String saveTestCaseValue(String testCaseVal) {
		if (testCaseVal == null || testCaseVal.equals("")) {
			return "";
		}
		Map<String, Object> tcMap = gson.fromJson(testCaseVal,
				new TypeToken<Map<String, Object>>() {
				}.getType());

		Map<String, Object> savemap = new HashMap<String, Object>();

		// 遍历从原TestCase获取的value转换成的map，获取map string，反射查找EditText ID
		Set<String> keys = tcMap.keySet();
		for (Iterator<String> it = keys.iterator(); it.hasNext();) {
			String str = (String) it.next();
			Object obj = tcMap.get(str);

			EditText et = (EditText) findViewById(getResources().getIdentifier(
					str, "id", getPackageName()));

			String etStr = removeDotZero(et.getText().toString().trim());

			// 判断原先的数据类型
			if (obj instanceof Double) {// 未知原因，Object数据总是带.0
				int vals = (int) Math.ceil(Double.parseDouble(etStr));
				savemap.put(str, vals);// Double.parseDouble(etStr));
			} else if (obj instanceof Integer) {
				savemap.put(str, Integer.parseInt(etStr));
			} else if (obj instanceof Boolean) {
				savemap.put(str, Boolean.valueOf(etStr).booleanValue());
			} else {
				savemap.put(str, etStr);
			}

		}

		String json = gson.toJson(savemap,
				new TypeToken<HashMap<String, Object>>() {
				}.getType());
		// testCase.setValue(json);

		return json;
		// app.setTestCaseSettings(testCase);
	}

	/**
	 * 使用java正则表达式去掉多余的.与0
	 * @param s
	 * @return
	 */
	private String removeDotZero(String str) {
		if (str.indexOf(".") > 0) {
			str = str.replaceAll("0+?$", "");// 去掉多余的0
			str = str.replaceAll("[.]$", "");// 如最后一位是.则去掉
		}
		return str;
	}

	@Override
	protected void onDestroy() {
		unRegisterTaskBroadCast(receiveTask);

		if (taskbase != null) {
			app.setTaskBases(null);
			taskbase.Stop();
		}

		super.onDestroy();
	}

	private void unRegisterTaskBroadCast(ReceiveTaskByActivity receiveTask) {
		if (receiveTask != null) {
			try {
				unregisterReceiver(receiveTask);
			} catch (IllegalArgumentException e) {
				if (e.getMessage().contains("Receiver not registered")) {
				} else {
					throw e;
				}
			}
		}
	}

	/**
	 * Activity上单独执行任务初始化。
	 * 
	 * @param task
	 */
	private TestCase initViewTask() {
		Task task = newSingleTask();

		TestCase taskbase = null;
		try {
			String cname = task.getTaskAction();
			Class<?> cls = Class.forName(Constants.PACKAGE_TASK + cname);
			Constructor<?> constructor = cls.getConstructor(new Class[] {
					Context.class, Task.class });
			taskbase = (TestCase) constructor.newInstance(new Object[] { this,
					task });

		} catch (Exception e) {
			e.printStackTrace();
		}

		// TaskBase taskbase = reflectTask(task);
		List<TestCase> taskbases = new ArrayList<TestCase>();
		taskbases.add(taskbase);
		app.setTaskBases(taskbases);

		return taskbase;
	}

	// 新建任务
	private Task newSingleTask() {
		dbm = new DBManager(this);
		Case testcase = dbm.queryTestCaseById(testCaseId);

		Task task = new Task();
		task.setPlanId(0);
		task.setTaskName(testcase.getName());
		task.setDisplayName(testcase.getDisplayName());
		task.setTaskAction(testcase.getAction());

		String formValue = saveTestCaseValue(testcase.getValue());
		task.setFormValue(formValue);

		task.setOriginCount(0);
		task.setTaskCount(0);

		UUID uuid = UUID.randomUUID();
		String resultFileName = uuid.toString() + ".log";
		task.setResultFile(resultFileName);
		task.setResultJson("");
		task.setCreateTime(us.getCurrentTime());
		task.setUpdateTime(us.getCurrentTime());

		dbm.insertTask(task);
		dbm.closedb();

		return task;
	}

	// private TaskBase reflectTask(Task task) {
	// TaskBase taskbase = null;
	// try {
	// String cname = task.getTaskAction();
	// Class<?> cls = Class.forName(Constants.PACKAGE_TASK + cname);
	// Constructor<?> constructor = cls.getConstructor(new Class[] {
	// Context.class, Task.class });
	// taskbase = (TaskBase) constructor.newInstance(new Object[] { this,
	// task });
	//
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	//
	// return taskbase;
	// }

	protected class ReceiveTaskByActivity extends BroadcastReceiver {

		@Override
		public void onReceive(Context arg0, Intent arg1) {

			if (arg1.getAction().equals(Constants.TASKSTATE_ACTION)) {
				Bundle bundle = arg1.getExtras();
				if (bundle != null) {
					int receiveValue = bundle.getInt(Constants.TASKSTATE_KEY);
					if (receiveValue == 0) {
						setButtonEnabled(true);
						titleName.setText(titleBarName);
						app.setTaskBases(null);
					} else {
						setButtonEnabled(false);
						if (taskbase != null) {
							String tname = titleBarName + ":"
									+ taskbase.timeCount;
							titleName.setText(tname);
						}
					}
				}
			}
		}
	}

	/**
	 * 切换：开始、取消、保存按钮
	 * 
	 * @param state
	 */
	public void setButtonEnabled(boolean state) {
		if (!state) {
			titleName.setText(titleBarName + ":" + taskbase.timeCount);
		} else {
			titleName.setText(titleBarName);
		}

		if (btnSave != null) {
			btnSave.setEnabled(state);
		}
		if (btnStart != null) {
			btnStart.setEnabled(state);
		}
		if (btnCancel != null) {
			btnCancel.setEnabled(!state);
		}

	}

}

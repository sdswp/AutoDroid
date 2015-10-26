package com.gk.touchstone.core;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.gk.touchstone.GkApplication;
import com.gk.touchstone.db.DBManager;
import com.gk.touchstone.entity.Form;
import com.gk.touchstone.entity.Task;
import com.gk.touchstone.entity.Result;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public abstract class TestCase implements Test{
	protected Context context;
	protected Task task;
	public Map<String, Object> formValue;
	private List<Form> forms;
	public boolean isRunning = false;
	public String taskName;
	public String resultFile;
	public String displayName;
	public int taskid;
	public int planid;
	public Thread mThread = null;
	public int timeCount = 0;
	public int taskCount = 0;// 递减的TaskCount;
	public int originTaskCount = 0;// 初始的TaskCount;
	private GkApplication app;
	private TaskManager tm;
	private List<Result> ResultList = new ArrayList<Result>();

	public TestCase(Context context, Task task) {
		this.context = context;
		this.task = task;

		formValue = getViewValue(task);
		forms = getForms(task);

		taskName = task.getTaskName();
		displayName = task.getDisplayName();
		taskid = task.getId();
		planid = task.getPlanId();
		taskCount = task.getTaskCount();
		originTaskCount = task.getTaskCount();
		resultFile = task.getResultFile();
		app = (GkApplication) context.getApplicationContext();

		tm = new TaskManager(context);
	}
	
//	public abstract void Start();
//	public abstract void Finish();
//	public abstract void Stop();

	public void setUp() {
	}

	public void tearDown() {
	}

	
	/**
	 * 发送任务状态广播
	 * 
	 * @param taskstate
	 */
	public void sendBroadcast() {
		isRunning = false;

		// 写入完成后，再发送广播..
		updateTaskResult();

		// 更新数据库剩余taskCount
		DBManager dbm = new DBManager(context);
		task.setTaskCount(taskCount);
		task.setUpdateTime(currentTime());
		dbm.updateTask(task);
		dbm.closedb();
		//tm.updateTaskCount(taskid, taskCount);

		tm.sendPlanBroadcast(0, task.getPlanId());
	}


	private void updateTaskResult() {
		if (ResultList == null || ResultList.size() <= 0) {
			return;
		}

		// tm.decreaseTaskCount记录的任务开始时间
//		SharedPreferences sp = context.getSharedPreferences(taskName,
//				Context.MODE_PRIVATE);
//		String startTime = sp.getString(Constants.TASK_START_TIME, "");
		
		String startTime = app.getMapStartTime().get(taskName);
		Gson gson = new Gson();
		String json = "{\"startTime\":\"" + startTime + "\",\"endTime\":\""
				+ currentTime() + "\",\"taskCount\":" + taskCount
				+ ",\"resultList\":" + gson.toJson(ResultList) + "},";

		if (writeLog(task.getResultFile(), json, Context.MODE_APPEND)) {
			ResultList.clear();
		}
	}

	public boolean writeLog(String logFileName, String content, int type) {
		// logFileName task.getresultFile 准备包含多个log文件名，存放历史log，最前一个为最新
		String[] strs = logFileName.split(".log");
		String lastLog = strs[strs.length - 1] + ".log";

		try {
			FileOutputStream fos = context.openFileOutput(lastLog, type);

			fos.write(content.getBytes());
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	// 获取系统当前时间
	@SuppressLint("SimpleDateFormat")
	private String currentTime() {
		Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(curDate);
	}

	public void stopTask() {
		isRunning = false;
		// app.setTaskBases(null);
	}

	/**
	 * 规定时间结束，停止任务
	 * 
	 * @return
	 */
	private boolean isRunningTime() {
		if (!app.getReckonByTime()) {
			return true;
		}

		if (app.getPlanEndDateTime() - System.currentTimeMillis() > 0) {
			return true;
		} else {
			tm.sendTaskBroadcast(0);
			tm.stopPlan();
			return false;
		}
	}

	public void writeResult(int timeCount, String result, String reason) {
		Result tr = new Result();
		tr.setResultData(timeCount, result, reason, currentTime());
		ResultList.add(tr);

		if (!isRunningTime()) {
			return;
		}

		// 广播通知任务状态
		if (app.getTaskBases() != null) {
			if (app.getTaskBases().size() == 1 && taskCount == 0
					&& timeCount == 0) {
				tm.sendTaskBroadcast(0);// 任务完成
			} else {
				tm.sendTaskBroadcast(1);
			}
		}

		logd("开始记录" + task.getTaskName() + ":[" + taskCount + "-" + timeCount
				+ "]" + result + reason);
	}

	/**
	 * task.getFormValue()的值 转为Map<String, Object>
	 * 
	 * @param val
	 * @return
	 */
	private Map<String, Object> getViewValue(Task t) {
		Gson gson = new Gson();
		String val = t.getFormValue();
		if (val != null && !val.equals("")) {
			return gson.fromJson(val, new TypeToken<Map<String, Object>>() {
			}.getType());

		}
		return null;
	}
	
	
	private List<Form> getForms(Task task){
		Gson gson = new Gson();
		String val = task.getFormValue();
		if (val != null && !val.equals("")) {
			//List<Form> ps = gson.fromJson(val, new TypeToken<List<Form>>(){}.getType());
			return gson.fromJson(val, new TypeToken<List<Form>>() {
			}.getType());

		}
		return null;
	}
	
	/**
	 * 根据id名获取value值，并转换为int
	 * example  "[{\"name\":\"abc\",\"id\":\"switchNum\",\"type\":0,\"value\":\"10\"}]",
	 * @param id
	 * @return
	 */
	public int getIntValue(String id) {
		for (Form fm : forms) {
			if (fm.getId().equals(id)) {
				Object o = fm.getValue();
				String str = removeDotZero(o.toString());

				int vals = 0;
				if (o instanceof Double) {
					// vals = Integer.parseInt(str);
					vals = (int) Math.ceil(Double.parseDouble(str));
				} else if (o instanceof Integer) {
					vals = Integer.parseInt(str);
				} else if (o instanceof String) {
					vals = Integer.parseInt(str);
				}

				return vals;
			}
		}
		return 0;
	}
	
	/**
	 * 根据id名获取value值，并转换为String
	 * @param id
	 * @return
	 */
	public String getStrValue(String id) {
		for (Form fm : forms) {
			if (fm.getId().equals(id)) {
				return fm.getValue();
			}
		}
		return null;
	}

	/**
	 * Map.get(Object) 转换为int类型
	 * 
	 * @param map
	 * @param keyName
	 * @return
	 */
	public int convertInt(Map<String, Object> map, String keyName) {
		Object o = map.get(keyName.trim());
		String str = removeDotZero(o.toString());

		int vals = 0;
		if (o instanceof Double) {
			// vals = Integer.parseInt(str);
			vals = (int) Math.ceil(Double.parseDouble(str));
		} else if (o instanceof Integer) {
			vals = Integer.parseInt(str);
		} else if (o instanceof String) {
			vals = Integer.parseInt(str);
		}
		return vals;
	}

	/**
	 * Map.get(Object) 转换为String类型
	 * 
	 * @param map
	 * @param keyName
	 * @return
	 */
	public String convertStr(Map<String, Object> map, String keyName) {
		Object o = map.get(keyName.trim());
		return o.toString();
	}

	// 使用java正则表达式去掉多余的.与0
	private String removeDotZero(String str) {
		if (str.indexOf(".") > 0) {
			str = str.replaceAll("0+?$", "");// 去掉多余的0
			str = str.replaceAll("[.]$", "");// 如最后一位是.则去掉
		}
		return str;
	}

	public void logv(String tips) {
		Log.v(context.getClass().getSimpleName(), tips);
	}

	public void logd(String tips) {
		Log.d(context.getClass().getSimpleName(), tips);
	}

	public void loge(String tips) {
		Log.e(context.getClass().getSimpleName(), tips);
	}

	public void logi(String tips) {
		Log.i(context.getClass().getSimpleName(), tips);
	}

//	public enum Widgets
//	{
//	    TextView(1, "TextView"), EditText(2, "EditText"), Button(3, "Button");
//
//	    private int _value;
//	    private String _name;
//
//	    private Widgets(int value, String name)
//	    {
//	        _value = value;
//	        _name = name;
//	    }
//
//	    public int value()
//	    {
//	        return _value;
//	    }
//
//	    public String getName()
//	    {
//	        return _name;
//	    }
//	}


}

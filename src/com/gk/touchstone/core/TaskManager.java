package com.gk.touchstone.core;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.gk.touchstone.GkApplication;
import com.gk.touchstone.R;
import com.gk.touchstone.db.DBManager;
import com.gk.touchstone.entity.Plan;
import com.gk.touchstone.entity.Task;
import com.gk.touchstone.entity.TaskJson;
import com.gk.touchstone.entity.Case;
import com.gk.touchstone.utils.DateTimes;
import com.gk.touchstone.utils.JsonValidator;
import com.gk.touchstone.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import dalvik.system.BaseDexClassLoader;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import java.util.UUID;

public class TaskManager {
	private Context context;
	private Utils us;
	private final int serial = 0;
	private final int parallel = 1;

	public static int plantype = -1;
	private GkApplication app;

	public TaskManager(Context context) {
		this.context = context;
		app = (GkApplication) context.getApplicationContext();
		us = new Utils(context);
	}

	/**
	 * 新增计划和任务
	 * 
	 * @param plan
	 * @param tasks
	 */
	public void addPlanAndTask(Plan plan, List<Task> tasks) {
		DBManager dbm = new DBManager(context);
		int pid = dbm.insertPlan(plan);
		if (pid != -1) {
			dbm.insertTasks(tasks, pid);
		} else {
			us.myToast(plan.getPlanName(), R.string.plan_exist);
		}
		dbm.closedb();
	}

//	/**
//	 * 启动任务计划
//	 * 
//	 * @param plan
//	 * @param byTime
//	 * @param duration
//	 *            持续时间
//	 * @param reboot
//	 *            重启后继续任务
//	 */
//	public void startPlan(Plan plan, boolean reboot) {
//		DBManager dbm = new DBManager(context);
//		final List<Task> tasks = dbm.queryTaskByPlanId(plan.getId());
//		final long duration = (long) plan.getDuration() * 60 * 60 * 1000;
//		dbm.closedb();
//
//		// 有未执行完成的任务
//		if (findUnfinished(plan, tasks).size() > 0) {
//			// 区分重启后继续任务没有对话框，手动开启任务的有对话框
//			if (reboot) {
//				List<Task> undones = findUnfinished(plan, tasks);
//				runTasks(plan, undones, duration);
//			} else {
//				showPlanStateDialog(plan, tasks, duration);
//			}
//		} else {
//			DBManager dbm2 = new DBManager(context);
//			dbm2.updateTasks(tasks, plan.getId());
//			dbm2.closedb();
//			runTasks(plan, tasks, duration);
//		}
//
//	}

	// 开始运行任务
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public void runTasks(Plan plan, List<Task> tasks, long duration) {
		if (tasks.size() > 0) {
			List<TestCase> taskbases = reflectTaskList(tasks);
			app.setTaskBases(taskbases);
			app.setPlan(plan);

			// 初始化各任务次数,存入SharedPreferences
			setTasksSharePrefs(tasks);

			int pid = tasks.get(0).getPlanId();
			String pidStr = pid + ",";// List<Plan> id
			// updatePlanInfo(pidStr, 0, byTime, duration);
			SharedPreferences splan = context.getSharedPreferences(
					Constants.PLAN_STATE, Context.MODE_MULTI_PROCESS);
			SharedPreferences.Editor editor = splan.edit();
			editor.putString("id", pidStr);

			Date dateTime = DateTimes.getCurrentAddDate(duration);
			editor.putLong("byTime", dateTime.getTime());

			editor.commit();

			app.setPlanEndDateTime(dateTime.getTime());
			if (duration > 0) {
				app.setReckonByTime(true);
			} else {
				app.setReckonByTime(false);
			}

			sendPlanBroadcast(1, plan.getId());
		}
	}

	private void showPlanStateDialog(final Plan plan, final List<Task> tasks,
			final long duration) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage("有任务未执行完成，是否继续?")
				.setCancelable(false)
				.setPositiveButton("继续", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						runTasks(plan, findUnfinished(plan, tasks), duration);
					}
				})
				.setNeutralButton("重新开始",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// 重新开始，删除之前的log，初始化任务次数
								for (Task t : tasks) {
									context.deleteFile(us.getLastLog(t
											.getResultFile()));
									t.setTaskCount(t.getOriginCount());
									t.setUpdateTime(us.getCurrentTime());
								}
								DBManager dbm1 = new DBManager(context);
								dbm1.updateTasks(tasks, plan.getId());
								dbm1.closedb();
								runTasks(plan, tasks, duration);
							}
						})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
		builder.create().show();
	}

	// 是否有任务未完成
	public List<Task> findUnfinished(Plan plan, List<Task> tasks) {
		UUID uuid = UUID.randomUUID();
		String resultFileName = uuid.toString() + ".log";

		List<Task> undoneTask = new ArrayList<Task>();

		for (Task t : tasks) {
			if (t.getTaskCount() == 0) {
				// 如果任务数已全部执行完毕，新建log文件
				t.setTaskCount(t.getOriginCount());
				t.setResultFile(t.getResultFile() + resultFileName);
				t.setUpdateTime(us.getCurrentTime());
			} else {
				String filePath = context.getFilesDir().getAbsoluteFile()
						+ File.separator + us.getLastLog(t.getResultFile());
				File f = new File(filePath);

				// 如果文件存在并且taskCount 不为0，则有任务未执行完成。
				if (f.exists()) {
					undoneTask.add(t);
				}
			}
		}

		return undoneTask;
	}

	// 暂无追加log
	public void startNetPlan(Plan plan, int byTime, long duration) {
		List<Task> tasks = new ArrayList<Task>();
		for (Task t : app.getTempTasks()) {
			if (t.getPlanId() == plan.id) {
				tasks.add(t);
			}
		}

		DBManager dbm = new DBManager(context);
		int pid = dbm.insertPlan(plan);
		if (pid != -1) {
			dbm.insertTasks(tasks, pid);
		}

		if (tasks.size() > 0) {
			// 重新查找数据库，获取自动生成的result.log,重置tasks
			plan = dbm.queryPlan(pid);
			tasks = dbm.queryTaskByPlanId(pid);

			List<TestCase> taskbases = reflectTaskList(tasks);
			app.setTaskBases(taskbases);
			app.setPlan(plan);

			setTasksSharePrefs(tasks);
			String pidStr = pid + ",";// 以后List<Plan> id 都用逗号间隔
			updatePlanInfo(pidStr, 0, duration);

			Date dateTime = DateTimes.getCurrentAddDate(duration);
			app.setPlanEndDateTime(dateTime.getTime());
			if (byTime == 0) {
				app.setReckonByTime(false);
			} else {
				app.setReckonByTime(true);
			}

			sendPlanBroadcast(1, plan.getId());
		}
		dbm.closedb();
	}

	/*
	 * switch (plan.getPlanType()) { case serial: serialRandomTask(taskbases);
	 * break; case parallel: // 首次，全部开启任务 if (receiveVal == 1) { for (TaskBase
	 * taskbase : taskbases) { taskbase.Start(); } } else {
	 * parallelTask(taskbases); } break; }
	 */

	/**
	 * 用反射批量获取本次计划任务到app.getTaskBases
	 * 
	 * @param tasklist
	 * @return
	 */
	public List<TestCase> reflectTaskList(List<Task> tasklist) {
		List<TestCase> taskbases = new ArrayList<TestCase>();
		for (Task task : tasklist) {
			try {
				String action = task.getTaskAction();
				Class<?> cls = Class.forName(Constants.PACKAGE_TASK + action);
				Constructor<?> constructor = cls.getConstructor(new Class[] {
						Context.class, Task.class });
				TestCase taskbase = (TestCase) constructor
						.newInstance(new Object[] { context, task });

				if (isHardwareExist(taskbase)) {
					taskbases.add(taskbase);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return taskbases;
	}
	
	/**
	 * 加载SD卡内的jar用例包
	 * @param tasklist
	 * @param casePackageName 存放在SD卡内的用例jar包名
	 * @return
	 */
	public List<TestCase> reflectTaskList(List<Task> tasklist, String casePackageName) {
		final File optimizedDexOutputPath = new File(Environment
				.getExternalStorageDirectory().toString()
				+ File.separator
				+ casePackageName);
		// + "testss.jar");

		BaseDexClassLoader cl = new BaseDexClassLoader(Environment
				.getExternalStorageDirectory().toString(),
				optimizedDexOutputPath,
				optimizedDexOutputPath.getAbsolutePath(),
				context.getClassLoader());

		List<TestCase> taskbases = new ArrayList<TestCase>();
		for (Task task : tasklist) {
			try {
				String action = task.getTaskAction();
				Class<?> cls = cl.loadClass("com.gk.touchstone.testcase."
						+ action);

				Constructor<?> constructor = cls.getConstructor(new Class[] {
						Context.class, Task.class });
				TestCase taskbase = (TestCase) constructor
						.newInstance(new Object[] { context, task });

				if (isHardwareExist(taskbase)) {
					taskbases.add(taskbase);
				}

			} catch (Exception exception) {
				exception.printStackTrace();
			}
		}
		return taskbases;
	}

	/**
	 * 暂时用分辨率判断手表，屏蔽硬件模块不支持的场景。
	 * 
	 * @param taskbase
	 * @return
	 */
	public boolean isHardwareExist(TestCase taskbase) {
		DisplayMetrics metric = new DisplayMetrics();
		WindowManager windowMgr = (WindowManager) context
				.getApplicationContext().getSystemService(
						Context.WINDOW_SERVICE);
		windowMgr.getDefaultDisplay().getMetrics(metric);

		int screenHeight = metric.heightPixels;

		String[] hardwares = new String[] { "Camera", "Mobile", "Sms",
				"AutoCall" };

		for (String s : hardwares) {
			if (taskbase.taskName.contains(s) && screenHeight < 250) {
				return false;
			}
		}

		return true;
	}

	// /**
	// * 启动暂停的任务计划
	// *
	// * @param plan
	// */
	// public void startPausePlan(Plan plan) {
	// dbm = new DBManager(context);
	// List<Task> tasks = dbm.queryTaskByPlanId(plan.getId());
	// dbm.closedb();
	//
	// if (tasks.size() > 0) {
	// List<TaskBase> taskbases = rf.getTaskList(tasks);
	// app.setTaskBases(taskbases);
	//
	// app.setPlanType(plan.getPlanType());
	//
	// sendPlanBroadcast(plan.getId());
	// }
	// }

	/**
	 * 更新单个TaskBase
	 * 
	 * @param task
	 */
	public void updateTaskBases(int taskid) {
		List<TestCase> taskbases = app.getTaskBases();

		if (taskbases != null && taskbases.size() > 0) {
			Iterator<TestCase> itr = taskbases.iterator();
			while (itr.hasNext()) {
				TestCase t = itr.next();
				if (t.taskid == taskid) {
					itr.remove();
				}
			}

			app.setTaskBases(taskbases);
		}
	}

	/**
	 * 启动任务执行的广播，同时存储所有任务的执行次数到SharedPreferences
	 * 
	 * @param plan
	 */
	public void sendPlanBroadcast(int isFirst, int pid) {
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		// 0为非首次广播
		// int[] intr = new int[] { 0, task.getPlanId() };
		// bundle.putIntArray(app.TASK_KEY, intr);
		// intent.putExtras(bundle);
		//
		// intent.setAction(app.TASK_ACTION);
		// context.sendBroadcast(intent);

		// 1为首次开启
		int[] intr = new int[] { isFirst, pid };
		bundle.putIntArray(Constants.TASK_KEY, intr);

		intent.putExtras(bundle);

		intent.setAction(Constants.TASK_ACTION);
		context.sendBroadcast(intent);
	}

	/**
	 * 每次结束一个任务时，发送任务状态的自定义广播
	 * 
	 * @param pid
	 */
	public void sendTaskBroadcast(int state) {
		Intent intent = new Intent();
		Bundle bundle = new Bundle();

		// int[] intr = new int[] { planid, taskid, state };
		bundle.putInt(Constants.TASKSTATE_KEY, state);
		intent.putExtras(bundle);

		intent.setAction(Constants.TASKSTATE_ACTION);
		context.sendBroadcast(intent);

	}
	
	/**
	 * 获取运行中的任务
	 * @return
	 */
	public List<TestCase> getRunningTask(){
		if(isTaskRunning(app.getTaskBases())){
			return app.getTaskBases();
		}
		return null;
	}

	/**
	 * 停止所有运行中的任务
	 * 
	 * @param taskBases
	 * @param tv
	 */
	public void stopPlan() {
		List<TestCase> taskbases = getRunningTask();
		if (taskbases != null) {
			for (TestCase t : taskbases) {
				t.Stop();
			}
		}

		app.setPlanEndDateTime(0);
		app.setTaskBases(null);
		app.setPlan(null);
	}

	/**
	 * 计划开始前，初始化待运行的
	 * 
	 * @param tasks
	 */
	public void setTasksSharePrefs(List<Task> tasks) {
		for (Task t : tasks) {
			SharedPreferences spTask = context.getSharedPreferences(
					t.getTaskName(), Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = spTask.edit();
			editor.putInt("count", t.getTaskCount() - 1);// 计数从n~0
			editor.commit();

		}
	}

	/**
	 * 执行串行任务
	 * 
	 * @param taskbases
	 */
	public void serialRandomTask(List<TestCase> taskbases) {
		if (updateFinishTask(taskbases) > 0) {
			int k = (int) (Math.random() * taskbases.size());
			TestCase tb = taskbases.get(k);

			// 开始任务前更新任务数量
			if (tb.taskCount > 0) {
				tb.Start();
				// updateNewTaskInfo(tb);
				tb.taskCount = tb.taskCount - 1;
				app.setMapStartTime(tb.taskName, us.getCurrentTime());
			}
		} else {
			updatePlanInfo("", 1, 0);
			app.setPlanEndDateTime(0);
			app.setTaskBases(null);
			app.setPlan(null);
		}
	}

	/**
	 * 执行并行任务
	 * 
	 * @param taskbases
	 */
	public void parallelTask(List<TestCase> taskbases) {
		if (updateFinishTask(taskbases) > 0) {
			for (TestCase tb : taskbases) {
				if (!tb.isRunning && tb.taskCount > 0) {
					tb.Start();
					// updateNewTaskInfo(tb);
					tb.taskCount = tb.taskCount - 1;
					app.setMapStartTime(tb.taskName, us.getCurrentTime());
					break;
				}
			}
		} else {
			updatePlanInfo("", 1, 0);

			app.setPlanEndDateTime(0);
			app.setTaskBases(null);
			app.setPlan(null);
		}
	}

	// /**
	// * 开启一个新的场景时，记录startTime、leftCount
	// *
	// * @param taskbase
	// */
	// public void updateNewTaskInfo(TaskBase taskbase) {
	// int newCount = taskbase.taskCount - 1;
	//
	// // SharedPreferences sp = context.getSharedPreferences(taskbase.taskName,
	// // Context.MODE_PRIVATE);
	// // SharedPreferences.Editor editor = sp.edit();
	// // editor.putString(Constants.TASK_START_TIME, us.getCurrentTime());
	// // //editor.putInt(Constants.TASK_LEFT_COUNT, newCount);
	// // editor.commit();
	//
	// app.setMapStartTime(taskbase.taskName, us.getCurrentTime());
	//
	// taskbase.taskCount = newCount;
	// }
	//
	// /**
	// * 计划运行时，更新TaskCount 到数据库
	// *
	// * @param task
	// */
	// public void updateTaskCount(int taskid, int taskCount) {
	// DBManager dbm = new DBManager(context);
	// Task task = dbm.queryTaskByTaskId(taskid);
	//
	// // SharedPreferences sp =
	// // context.getSharedPreferences(task.getTaskName(),
	// // Context.MODE_PRIVATE);
	// // int leftCount = sp.getInt("leftCount", 0);
	//
	// task.setTaskCount(taskCount);
	// task.setUpdateTime(us.getCurrentTime());
	// dbm.updateTask(task);
	// dbm.closedb();
	// }

	// /**
	// * 停止计划时，剩余TaskCount 批量更新到数据库
	// *
	// * @param task
	// */
	// public void updateTasksLeftCount(int planid) {
	// dbm = new DBManager(context);
	// List<Task> tasks = dbm.queryTaskByPlanId(planid);
	//
	// for (Task task : tasks) {
	// SharedPreferences sp = context.getSharedPreferences(
	// task.getTaskName(), Context.MODE_PRIVATE);
	// int leftCount = sp.getInt("leftCount", 0);
	// task.setLeftCount(leftCount);
	// if (leftCount > 0) {// 为0 则任务已经运行完成，已经由任务运行中更新updateTime
	// task.setUpdateTime(us.getCurrentTime());
	// }
	// }
	// dbm.updateTasks(tasks, planid);
	// dbm.closedb();
	// }
	//
	// public void decreaseTaskCount(Task task) {
	// int newCount = task.getTaskCount() - 1;
	// task.setTaskCount(newCount);
	// SharedPreferences sp = context.getSharedPreferences(task.getTaskName(),
	// Context.MODE_PRIVATE);
	// SharedPreferences.Editor editor = sp.edit();
	// editor.putInt("leftCount", newCount);
	// editor.commit();
	//
	// // taskbase.taskCount = newCount;
	// }

	/**
	 * 检查全局app.getTaskBases 是否为null，或size=0
	 * 
	 * @param taskbases
	 * @return
	 */
	public boolean isTaskRunning(List<TestCase> taskbases) {
		if (taskbases != null && taskbases.size() > 0) {
			return true;
		}
		return false;
	}

	/**
	 * 由BroadCast驱动任务
	 * 
	 * @param receiveVal
	 * @param planType
	 * @param taskbases
	 */
	public void dispatchTask(int receiveVal) {
		// 在开始任务的时候，有任务添加判断，taskbase一定是不为null，切数量大于0，暂时不需要判断
		// if (!checkTaskBase(taskbases)) {
		// return;
		// }
		List<TestCase> taskbases = app.getTaskBases();

		Plan plan = app.getPlan();
		if (plan == null) {
			return;
		}

		switch (plan.getPlanType()) {
		case serial:
			serialRandomTask(taskbases);
			break;
		case parallel:
			// 首次，全部开启任务
			if (receiveVal == 1) {
				for (TestCase taskbase : taskbases) {
					taskbase.Start();
				}
			} else {
				parallelTask(taskbases);
			}
			break;
		}
	}

	/**
	 * 更新任务记录
	 * 
	 * @param taskbases
	 * @return
	 */
	private int updateFinishTask(List<TestCase> taskbases) {
		int n = 0;
		if (taskbases != null && taskbases.size() > 0) {
			Iterator<TestCase> itr = taskbases.iterator();
			while (itr.hasNext()) {
				TestCase t = itr.next();
				if (t.taskCount <= 0) {
					itr.remove();
					// updateTaskLeftCount(t.taskid,t.taskCount);
				}
			}

			n = taskbases.size();
			app.setTaskBases(taskbases);
		}
		return n;
	}

	// /**
	// * 获取当前任务和场景的总数
	// *
	// * @param taskbases
	// * @return
	// */
	// public int getTaskTotalCount(List<TaskBase> taskbases) {
	// int t = 0;
	// if (taskbases != null && taskbases.size() > 0) {
	// for (TaskBase tb : taskbases) {
	// int taskCount = tb.taskCount;
	// int timeCount = tb.timeCount;
	// if (taskCount == 0 && timeCount >= 0) {
	// t += timeCount;
	// } else {
	// t += taskCount * timeCount;
	// }
	// }
	// }
	// return t;
	// }

	/**
	 * 记录计划状态
	 * 
	 * @param pid
	 * @param isUnfinish
	 *            计划是否运行完成： true完成，false未完成
	 * @param bytime
	 * @param duration
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public void updatePlanInfo(String pid, int isFinish, long duration) {

		String[] pidArray = null;
		if (!pid.equals("") && pid.contains(",")) {
			// TODO 根据id 数组查询到 List<Plan>
			pidArray = pid.split(",");

			DBManager dbm = new DBManager(context);
			Plan plan = dbm.queryPlan(Integer.parseInt(pidArray[0]));
			plan.setIsFinish(isFinish);

			dbm.updatePlan(plan);
			dbm.closedb();
		}

		SharedPreferences splan = context.getSharedPreferences(
				Constants.PLAN_STATE, Context.MODE_MULTI_PROCESS);
		SharedPreferences.Editor editor = splan.edit();
		editor.putString("id", pid);
		// editor.putBoolean("state", isFinish);
		// editor.putBoolean("byTime", bytime);

		Date dateTime = DateTimes.getCurrentAddDate(duration);
		editor.putLong("byTime", dateTime.getTime());

		editor.commit();
	}

	// /**
	// * 获取任务状态SharedPreferences
	// *
	// * @return
	// */
	// public Map<String, Object> getPrefsPlan() {
	// SharedPreferences planPrefs = context.getSharedPreferences("planState",
	// Context.MODE_PRIVATE);
	//
	// Map<String, Object> map = new HashMap<String, Object>();
	// int id = planPrefs.getInt("id", 0);
	// boolean state = planPrefs.getBoolean("state", false);
	// map.put("id", id);
	// map.put("state", state);
	// return map;
	//
	// }

	// /**
	// * 更新运行中的任务列表
	// *
	// * @param taskbases
	// * @return
	// */
	// public List<Task> getRunTasks(List<TaskBase> taskbases) {
	// List<Task> tasks=null;
	// if (taskbases != null && taskbases.size() > 0) {
	// tasks = new ArrayList<Task>();
	// for (TaskBase tb : taskbases) {
	// Task task = new Task();
	// task.setTaskName(tb.taskName);
	// task.setPlanId(tb.planid);
	// task.setDisplayName(tb.displayName + "," + tb.timeCount);
	// task.setTaskCount(tb.taskCount);
	// tasks.add(task);
	// }
	// }
	// return tasks;
	// }

	// /**
	// * 获取任务数SharedPreferences
	// *
	// * @param prefsName
	// * @return
	// */
	// public int getPrefsTaskCount(String prefsName) {
	// SharedPreferences sp = context.getSharedPreferences(prefsName,
	// Context.MODE_PRIVATE);
	// return sp.getInt("count", 0);
	// }

	// /**
	// * 修改任务数SharedPreferences
	// *
	// * @param prefsName
	// * @param value
	// */
	// public void updatePrefsTaskCount(String prefsName, int value) {
	// SharedPreferences sp = context.getSharedPreferences(prefsName,
	// Context.MODE_PRIVATE);
	// SharedPreferences.Editor editor = sp.edit();
	// editor.putInt("count", value);
	// editor.commit();
	// }

	public TestCase getCameraTask(String className) {
		// int taskcount=0;
		TestCase taskbase = null;
		List<TestCase> taskbases = app.getTaskBases();
		if (taskbases != null && taskbases.size() > 0) {
			for (TestCase t : taskbases) {
				if (t.taskName.equals(className)) {
					taskbase = t;
					break;
				}

			}
		}

		return taskbase;
	}

	/**
	 * 读取WebServer生成的json数据
	 * 
	 * @param jsonData
	 */
	public int[] readServerJsonData(String jsonData) {
		List<Plan> plans = new ArrayList<Plan>();
		Gson gson = new Gson();
		List<TaskJson> taskjsons = gson.fromJson(jsonData.trim(),
				new TypeToken<List<TaskJson>>() {
				}.getType());

		int[] planids = new int[taskjsons.size()];
		int i = 0;
		DBManager dbm = new DBManager(context);
		for (TaskJson taskjson : taskjsons) {
			String planName = taskjson.getTaskName();
			int playType = taskjson.getTaskType();
			int planCount = taskjson.getTaskCount();

			Plan plan = new Plan();
			plan.setPlanName(planName);
			plan.setPlanType(playType);
			plan.setPlanNumber(planCount);
			plan.setCreateWay(Constants.MODE_STABILITY);
			plan.setExceptions("");
			plan.setIsFinish(0);
			plan.setDuration(0);
			plan.setCreateTime(us.getCurrentTime());
			plan.setUpdateTime(us.getCurrentTime());
			plans.add(plan);

			List<Map<String, Object>> maps = taskjson.getTestCases();

			List<Task> tasks = new ArrayList<Task>();
			for (Map<String, Object> map : maps) {
				String caseid = map.get("testCaseId").toString();
				String testCaseCount = map.get("testCaseCount").toString();
				int taskCount = Integer.parseInt(delZero(testCaseCount));

				Case testcase = dbm.queryTestCaseByServerId(caseid);

				Task task = new Task();
				task.setTaskName(testcase.getName());
				task.setDisplayName(testcase.getDisplayName());
				task.setTaskAction(testcase.getAction());
				task.setFormValue(testcase.getValue());
				task.setTaskCount(taskCount);
				task.setOriginCount(taskCount);
				
				UUID uuid = UUID.randomUUID();
				String resultFileName = uuid.toString() + ".log";
				task.setResultFile(resultFileName);
				task.setResultJson("");
				task.setCreateTime(us.getCurrentTime());
				task.setUpdateTime(us.getCurrentTime());

				tasks.add(task);
			}

			int pid = dbm.insertPlan(plan);
			// planid 收集到数组中
			planids[i] = pid;
			i++;

			if (pid != -1) {
				dbm.insertTasks(tasks, pid);
			} else {
				us.myToast(plan.getPlanName(), R.string.plan_exist);
			}
		}

		dbm.closedb();

		return planids;
	}

	// /**
	// * 读取WebServer生成的json数据
	// *
	// * @param jsonData
	// */
	// public List<Plan> getNetTask(String jsonData) {
	// List<Plan> plans = new ArrayList<Plan>();
	// Gson gson = new Gson();
	// List<TaskJson> taskjsons = gson.fromJson(jsonData.trim(),
	// new TypeToken<List<TaskJson>>() {
	// }.getType());
	// for (TaskJson taskjson : taskjsons) {
	// String planName = taskjson.getTaskName();
	// int playType = taskjson.getTaskType();
	// int planCount = taskjson.getTaskCount();
	//
	// Plan plan = new Plan();
	// plan.setPlanName(planName);
	// plan.setPlanType(playType);
	// plan.setPlanNumber(planCount);
	// plan.setCreateWay(Constants.MODE_STABILITY);
	// plan.setExceptions("");
	// plan.setIsFinish(0);
	// plan.setCreateTime(us.getCurrentTime());
	// plan.setUpdateTime(us.getCurrentTime());
	// plans.add(plan);
	//
	// }
	// return plans;
	// }

	/**
	 * 获取在线配置计划。
	 * 
	 * @param jsonData
	 * @return
	 */
	public List<Plan> getSingleNetPlan(String jsonData) {
		JsonValidator jv = new JsonValidator();

		if (jsonData == "" || !jv.validate(jsonData)) {
			return null;
		}

		List<Plan> plans = new ArrayList<Plan>();
		List<Task> tasks = new ArrayList<Task>();
		Gson gson = new Gson();
		List<TaskJson> taskjsons = gson.fromJson(jsonData.trim(),
				new TypeToken<List<TaskJson>>() {
				}.getType());

		DBManager dbm = new DBManager(context);
		List<Plan> planlist = dbm.queryAllPlan();

		Iterator<TaskJson> itr = taskjsons.iterator();
		while (itr.hasNext()) {
			TaskJson t = itr.next();
			for (Plan p : planlist) {
				if (p.getCreateWay().equals(t.getTaskId())) {
					itr.remove();
				}
			}
		}

		// dbm = new DBManager(context);
		for (TaskJson taskjson : taskjsons) {
			String planName = taskjson.getTaskName();
			int playType = taskjson.getTaskType();
			int planCount = taskjson.getTaskCount();

			int pid = us.getRandomInt();// 临时随机生成一个id
			Plan plan = new Plan();
			plan.setId(pid);
			plan.setPlanName(planName);
			plan.setPlanType(playType);
			plan.setPlanNumber(planCount);
			// TODO: CreateWay准备用服务器的guid
			// plan.setCreateWay(Constants.Ways_Server);
			plan.setExceptions("");
			plan.setIsFinish(0);
			plan.setCreateWay(taskjson.getTaskId());
			plan.setCreateTime(us.getCurrentTime());
			plan.setUpdateTime(us.getCurrentTime());
			plans.add(plan);
			// int pid=dbm.insertPlan(plan);

			List<Map<String, Object>> maps = taskjson.getTestCases();

			for (Map<String, Object> map : maps) {
				String caseid = map.get("caseId").toString();
				String name = map.get("name").toString();
				String displayName = map.get("displayName").toString();
				String action = map.get("action").toString();
				String testCaseCount = map.get("caseCount").toString();
				String resultValue = map.get("values").toString();

				int taskCount = Integer.parseInt(delZero(testCaseCount));

				Task task = new Task();
				task.setPlanId(pid);
				task.setTaskName(name);
				task.setDisplayName(displayName);
				task.setTaskAction(action);
				task.setFormValue(resultValue);
				task.setResultJson("");
				task.setTaskCount(taskCount);
				task.setCreateTime(us.getCurrentTime());
				task.setUpdateTime(us.getCurrentTime());

				tasks.add(task);
			}
			// dbm.insertTasks(tasks, pid);
		}
		// dbm.closedb();

		app.setTempPlans(plans);
		app.setTempTasks(tasks);

		return plans;
	}

	// public List<Task> getSingleNetPlan(String jsonData, int pos) {
	// List<Plan> plans = new ArrayList<Plan>();
	// Gson gson = new Gson();
	// List<TaskJson> taskjsons = gson.fromJson(jsonData.trim(),
	// new TypeToken<List<TaskJson>>() {
	// }.getType());
	// dbm = new DBManager(context);
	//
	// TaskJson taskjson = taskjsons.get(pos);
	//
	// String planName = taskjson.getTaskName();
	// int playType = taskjson.getTaskType();
	// int planCount = taskjson.getTaskCount();
	//
	// Plan plan = new Plan();
	// plan.setPlanName(planName);
	// plan.setPlanType(playType);
	// plan.setPlanNumber(planCount);
	// plan.setCreateWay(Constants.Ways_Server);
	// plan.setCreateTime(us.getCurrentTime());
	// plan.setUpdateTime(us.getCurrentTime());
	// plans.add(plan);
	// int pid = dbm.insertPlan(plan);
	//
	// List<Map<String, Object>> maps = taskjson.getTestCases();
	//
	// List<Task> tasks = new ArrayList<Task>();
	// for (Map<String, Object> map : maps) {
	// String caseid = map.get("testCaseId").toString();
	// String testCaseCount = map.get("testCaseCount").toString();
	// String resultValue = map.get("testCaseValue").toString();
	//
	// int taskCount = Integer.parseInt(delZero(testCaseCount));
	//
	// TestCase testcase = dbm.queryTestCaseByServerId(caseid);
	//
	// Task task = new Task();
	// task.setTaskName(testcase.getName());
	// task.setDisplayName(testcase.getDisplayName());
	// task.setTaskAction(testcase.getAction());
	// task.setFormValue(resultValue);
	// task.setResultJson("");
	// task.setTaskCount(taskCount);
	// task.setCreateTime(us.getCurrentTime());
	// task.setUpdateTime(us.getCurrentTime());
	//
	// tasks.add(task);
	// }
	// dbm.insertTasks(tasks, pid);
	//
	// dbm.closedb();
	//
	// return tasks;
	// }

	public static String delZero(String s) {
		if (s.indexOf(".") > 0) {
			s = s.replaceAll("0+?$", "");// 去掉多余的0
			s = s.replaceAll("[.]$", "");// 如最后一位是.则去掉
		}
		return s;
	}

	public void unRegisterTaskReceiver(BroadcastReceiver bc) {
		// BroadcastReceiver receiver = (BroadcastReceiver) getTaskObject(name);

		if (bc != null) {
			try {
				context.unregisterReceiver(bc);
			} catch (IllegalArgumentException e) {
				if (e.getMessage().contains("Receiver not registered")) {
				} else {
					throw e;
				}
			}
		}
	}

	// public Object getTaskObject(String className) {
	// Object obj = null;
	// try {
	// Class<?> cls = Class.forName(className);
	//
	// Constructor<?> constructor = cls.getConstructor();
	// obj = (Object) constructor.newInstance();
	//
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	//
	// return obj;
	// }

	// public void savePlanEndTime(int duration) {
	// String str = us.getCurrentTime();
	// SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	// try {
	// Date myDate = formatter.parse(str);
	// Calendar c = Calendar.getInstance();
	// c.setTime(myDate);
	// c.add(Calendar.HOUR, duration);
	// // c.add(Calendar.MONTH, 8);
	// myDate = c.getTime();
	//
	// if (duration == 0) {
	// app.setPlanEndDate(null);
	// } else {
	// //app.planEndTime = formatter.format(myDate);
	// }
	//
	// } catch (ParseException e1) {
	// e1.printStackTrace();
	// }
	//
	// }

}

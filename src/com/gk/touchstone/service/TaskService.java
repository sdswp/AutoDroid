package com.gk.touchstone.service;

import java.io.File;

import com.gk.touchstone.R;
import com.gk.touchstone.core.TaskManager;
import com.gk.touchstone.db.DBManager;
import com.gk.touchstone.entity.Plan;
import com.gk.touchstone.core.Constants;
import com.gk.touchstone.core.RunRebootPlan;
import com.gk.touchstone.core.PlanRunner;
import com.gk.touchstone.utils.Utils;

import android.app.ActivityManager;
import android.app.Service;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

public class TaskService extends Service {
	private static String TAG = "TaskSerice";
	private receiveTask taskBroadCast;
	private TaskManager tm;
	private static Thread checkFileSize = null;
	private boolean isChecking = false;
	private Utils us;
	private SharedPreferences prefs;
	private PlanRunner runner;

	@Override
	public void onCreate() {
		Log.e(TAG, "TaskSerice onCreate()");

		taskBroadCast = new receiveTask();
		tm = new TaskManager(this);
		us = new Utils(this);
		runner = new RunRebootPlan(this);

		isChecking = true;
		checkSDCardFiles();

		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		Log.e(TAG, "TaskSerice onStart()");
		
		registerReceiver(taskBroadCast, new IntentFilter(Constants.TASK_ACTION));
		
		SharedPreferences planState = getSharedPreferences(
				Constants.PLAN_STATE, MODE_MULTI_PROCESS);

		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		if (prefs.getBoolean(getResources().getString(R.string.keepRunning_key),
				true)) {
			
			String ids = planState.getString("id", "").trim();
			//long byTime = planState.getLong("byTime", 0);
			String[] pidArray=null;
			if (!ids.equals("") && ids.contains(",")) {
				// TODO 根据id 数组查询到 List<Plan>
				pidArray = ids.split(",");
				
				DBManager dbm = new DBManager(this);
				Plan plan = dbm.queryPlan(Integer.parseInt(pidArray[0]));

				if (plan.getIsFinish() == 0) {//未运行完成
					// 记录重启异常
					String ex = plan.getExceptions();
					plan.setExceptions(ex + us.getCurrentTime() + "|");
					dbm.updatePlan(plan);
					//tm.startPlan(plan, true);
					
					runner.start(plan);
				}
				
				dbm.closedb();
			}
			
			// tm.startPlan(plan, false, 0, true);
		}

		

		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		Log.e(TAG, "TaskSerice onDestroy()");
		unregisterReceiver(taskBroadCast);
		//tm.stopPlan();
		runner.stop();

		if (checkFileSize != null) {
			isChecking = false;
			checkFileSize = null;
		}

		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		Log.e(TAG, "TaskSerice onBind()");
		return null;
	}

	@Override
	public boolean onUnbind(Intent intent) {
		Log.e(TAG, "TaskSerice onUnbind()");
		return super.onUnbind(intent);
	}

	public static boolean checkServiceStatus(Context context) {
		boolean isServiceRunning = false;
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		for (RunningServiceInfo service : am
				.getRunningServices(Integer.MAX_VALUE)) {
			if (TaskService.class.getName().equals(
					service.service.getClassName())) {
				isServiceRunning = true;
				break;
			}
		}
		return isServiceRunning;
	}

	private class receiveTask extends BroadcastReceiver {

		@Override
		public void onReceive(Context arg0, Intent arg1) {

			if (arg1.getAction().equals(Constants.TASK_ACTION)) {
				Bundle bundle = arg1.getExtras();
				if (bundle != null) {
					int[] receiveValue = bundle.getIntArray(Constants.TASK_KEY);
					Log.i("TaskService", "TaskService已经收到任务结束的广播，开始执行下一个任务...");

					tm.dispatchTask(receiveValue[0]);// 暂时不取pid
				}
			}
		}
	}
	
	// 控制Camera产生的文件
	private void checkSDCardFiles() {
		if (checkFileSize == null) {
			checkFileSize = new Thread() {
				public void run() {
					try {
						while (isChecking) {
							Thread.sleep(50000);
							String dirs = us.getAppSDCardPath()
									+ File.separator + Constants.FOLDER_CAMERA;
							File root = new File(dirs);

							if (root.exists()) {
								File[] files = root.listFiles();
								if (getSDEnoughSize(files)) {
									for (File file : files) {
										file.delete();
									}
								}
							}
						}

					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			};
			checkFileSize.start();
		}
	}

	// 是否超出SD卡200M剩余空间
	private boolean getSDEnoughSize(File[] files) {
		if (files == null || files.length == 0) {
			return false;
		}

		long filesize = 0;

		for (File file : files) {
			try {
				filesize += us.getFileSizes(file);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if ((us.getSDLeftSize() - filesize) < Constants.FILES_LEFT_SIZE) {
			return false;
		}

		return true;
	}

}

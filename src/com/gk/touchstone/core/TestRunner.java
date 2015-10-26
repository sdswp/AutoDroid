package com.gk.touchstone.core;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.gk.touchstone.db.DBManager;
import com.gk.touchstone.entity.Plan;
import com.gk.touchstone.entity.Task;

public class TestRunner implements PlanRunner {
	private Context context;
	private TaskManager tm;

	public TestRunner(Context context) {
		this.context = context;
		tm = new TaskManager(context);
	}

	@Override
	public void start(Plan plan) {
		DBManager dbm = new DBManager(context);
		List<Task> tasks = dbm.queryTaskByPlanId(plan.getId());
		long duration = (long) plan.getDuration() * 60 * 60 * 1000;
		dbm.closedb();

		// 有未执行完成的任务
		if (tm.findUnfinished(plan, tasks).size() > 0) {
			showPlanStateDialog(plan, tasks, duration);
		} else {
			DBManager dbm2 = new DBManager(context);
			dbm2.updateTasks(tasks, plan.getId());
			dbm2.closedb();
			tm.runTasks(plan, tasks, duration);
		}

	}

	@Override
	public void stop() {
		tm.stopPlan();
	}
	
	private void showPlanStateDialog(final Plan plan, final List<Task> tasks,
			final long duration) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage("有任务未执行完成，是否继续?")
				.setCancelable(false)
				.setPositiveButton("继续", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						tm.runTasks(plan, tm.findUnfinished(plan, tasks), duration);
					}
				})
				.setNeutralButton("重新开始",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// 重新开始，删除之前的log，初始化任务次数
								for (Task t : tasks) {
									String[] strs = t.getResultFile().split(".log");
									String logname= strs[strs.length - 1] + ".log";
									context.deleteFile(logname);
									t.setTaskCount(t.getOriginCount());
									t.setUpdateTime(currentTime());
								}
								DBManager dbm1 = new DBManager(context);
								dbm1.updateTasks(tasks, plan.getId());
								dbm1.closedb();
								tm.runTasks(plan, tasks, duration);
							}
						})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
		builder.create().show();
	}
	
	// 获取系统当前时间
	@SuppressLint("SimpleDateFormat")
	private String currentTime() {
		Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(curDate);
	}

}

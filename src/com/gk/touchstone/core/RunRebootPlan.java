package com.gk.touchstone.core;

import java.util.List;

import android.content.Context;
import com.gk.touchstone.db.DBManager;
import com.gk.touchstone.entity.Plan;
import com.gk.touchstone.entity.Task;

public class RunRebootPlan implements PlanRunner {
	private Context context;
	private TaskManager tm;

	public RunRebootPlan(Context context) {
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
			List<Task> undones = tm.findUnfinished(plan, tasks);
			tm.runTasks(plan, undones, duration);
		} 
//		else {
//			DBManager dbm2 = new DBManager(context);
//			dbm2.updateTasks(tasks, plan.getId());
//			dbm2.closedb();
//			tm.runTasks(plan, tasks, duration);
//		}

	}

	@Override
	public void stop() {
		tm.stopPlan();
	}

}

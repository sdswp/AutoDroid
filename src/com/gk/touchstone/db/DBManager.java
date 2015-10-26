package com.gk.touchstone.db;

import java.util.ArrayList;
import java.util.List;

import com.gk.touchstone.core.Stability;
import com.gk.touchstone.entity.Module;
import com.gk.touchstone.entity.Plan;
import com.gk.touchstone.entity.Task;
import com.gk.touchstone.entity.Case;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DBManager {
	private DBhelper helper;
	private SQLiteDatabase db;

	// 构造函数
	public DBManager(Context context) {
		helper = new DBhelper(context);

		db = helper.getWritableDatabase();
	}

	// 如果在一定的时间内需要重复的操作数据库，那么不要调用close()方法，
	// 关闭游标就可以了。在Activity注销或者真正不再需要的时候调用数据库的colse()方法.

	public Cursor queryTaskCursor() {
		Cursor c = db.rawQuery("SELECT * FROM Task", null);
		return c;
	}

	public Cursor queryPlanCursor() {
		Cursor c = db.rawQuery("SELECT * FROM Plan", null);
		return c;
	}

	public void closedb() {
		db.close();
	}

	/**
	 * 新建计划
	 * 
	 * @param task
	 */
	public int insertPlan(Plan plan) {
		// db.beginTransaction();
		int pid = 0;
		// try {
		if (!existPlan(plan.getPlanName())) {
			ContentValues cv = new ContentValues();
			cv.putNull("id");
			cv.put("planName", plan.getPlanName());
			cv.put("planType", plan.getPlanType());
			cv.put("createWay", plan.getCreateWay());
			cv.put("planNumber", plan.getPlanNumber());
			cv.put("exceptions", plan.getExceptions());
			cv.put("isFinish", plan.getIsFinish());
			cv.put("duration", plan.getDuration());
			cv.put("createTime", plan.getCreateTime());
			cv.put("updateTime", plan.getUpdateTime());

			db.insert("Plan", null, cv);

			Cursor c = db
					.rawQuery("SELECT last_insert_rowid() from Plan", null);
			if (c.moveToFirst()) {
				pid = c.getInt(0);
			}
			c.close();
			// } finally {
			// //db.endTransaction();
			// //db.close();
			// }
		} else {
			pid = -1;
		}
		return pid;
	}
	
	
	/**
	 * 修改Plan
	 * 
	 * @param plan
	 */
	public void updatePlan(Plan plan) {
		ContentValues cv = new ContentValues();
		cv.put("planName", plan.getPlanName());
		cv.put("planType", plan.getPlanType());
		cv.put("createWay", plan.getCreateWay());
		cv.put("planNumber", plan.getPlanNumber());
		cv.put("exceptions", plan.getExceptions());
		cv.put("isFinish", plan.getIsFinish());
		cv.put("duration", plan.getDuration());
		cv.put("createTime", plan.getCreateTime());
		cv.put("updateTime", plan.getUpdateTime());

		db.update("Plan", cv, "id = ?",
				new String[] { String.valueOf(plan.getId()) });
	}

	/**
	 * 根据planId查找所属的任务
	 * 
	 * @param pid
	 * @return
	 */
	public List<Task> queryTaskByPlanId(int pid) {
		List<Task> tasklist = new ArrayList<Task>();
		Cursor c = db.rawQuery("SELECT * FROM Task where planId=?",
				new String[] { String.valueOf(pid) });

		while (c.moveToNext()) {
			Task task = new Task();
			task.setId(c.getInt(c.getColumnIndex("id")));
			task.setPlanId(c.getInt(c.getColumnIndex("planId")));
			task.setTaskName(c.getString(c.getColumnIndex("taskName")));
			task.setDisplayName(c.getString(c.getColumnIndex("displayName")));
			task.setTaskAction(c.getString(c.getColumnIndex("taskAction")));
			task.setFormValue(c.getString(c.getColumnIndex("formValue")));
			task.setTaskCount(c.getInt(c.getColumnIndex("taskCount")));
			task.setOriginCount(c.getInt(c.getColumnIndex("originCount")));
			task.setResultJson(c.getString(c.getColumnIndex("resultJson")));
			task.setResultFile(c.getString(c.getColumnIndex("resultFile")));
			task.setCreateTime(c.getString(c.getColumnIndex("createTime")));
			task.setUpdateTime(c.getString(c.getColumnIndex("updateTime")));
			tasklist.add(task);
		}

		c.close();
		return tasklist;
	}
	
	/**
	 * 通过ID查找Task
	 * @param taskid
	 * @return
	 */
	public Task queryTaskByTaskId(int taskid) {
		Cursor c = db.rawQuery("SELECT * FROM Task where id=?",
				new String[] { String.valueOf(taskid) });
		Task task = new Task();
		while (c.moveToNext()) {
			task.setId(c.getInt(c.getColumnIndex("id")));
			task.setPlanId(c.getInt(c.getColumnIndex("planId")));
			task.setTaskName(c.getString(c.getColumnIndex("taskName")));
			task.setDisplayName(c.getString(c.getColumnIndex("displayName")));
			task.setTaskAction(c.getString(c.getColumnIndex("taskAction")));
			task.setFormValue(c.getString(c.getColumnIndex("formValue")));
			task.setTaskCount(c.getInt(c.getColumnIndex("taskCount")));
			task.setOriginCount(c.getInt(c.getColumnIndex("originCount")));
			task.setResultJson(c.getString(c.getColumnIndex("resultJson")));
			task.setResultFile(c.getString(c.getColumnIndex("resultFile")));
			task.setCreateTime(c.getString(c.getColumnIndex("createTime")));
			task.setUpdateTime(c.getString(c.getColumnIndex("updateTime")));

		}
		c.close();
		return task;
	}


	/**
	 * 查找所有plan
	 * 
	 * @return
	 */
	public List<Plan> queryAllPlan() {
		List<Plan> planlist = new ArrayList<Plan>();
		Cursor c = db.rawQuery("SELECT * from Plan", null);
		while (c.moveToNext()) {
			Plan plan = new Plan();
			plan.setId(c.getInt(c.getColumnIndex("id")));
			plan.setPlanName(c.getString(c.getColumnIndex("planName")));
			plan.setPlanType(c.getInt(c.getColumnIndex("planType")));
			plan.setCreateWay(c.getString(c.getColumnIndex("createWay")));
			plan.setPlanNumber(c.getInt(c.getColumnIndex("planNumber")));
			plan.setExceptions(c.getString(c.getColumnIndex("exceptions")));
			plan.setIsFinish(c.getInt(c.getColumnIndex("isFinish")));
			plan.setDuration(c.getInt(c.getColumnIndex("duration")));
			plan.setCreateTime(c.getString(c.getColumnIndex("createTime")));
			plan.setUpdateTime(c.getString(c.getColumnIndex("updateTime")));
			planlist.add(plan);
		}
		c.close();
		return planlist;

	}

	public List<Plan> queryPlansByCreateWay(String createWayStr) {
		List<Plan> planlist = new ArrayList<Plan>();
		Cursor c = db.rawQuery("SELECT * from Plan where createWay=?",
				new String[] { createWayStr });
		while (c.moveToNext()) {
			Plan plan = new Plan();
			plan.setId(c.getInt(c.getColumnIndex("id")));
			plan.setPlanName(c.getString(c.getColumnIndex("planName")));
			plan.setPlanType(c.getInt(c.getColumnIndex("planType")));
			plan.setCreateWay(c.getString(c.getColumnIndex("createWay")));
			plan.setPlanNumber(c.getInt(c.getColumnIndex("planNumber")));
			plan.setExceptions(c.getString(c.getColumnIndex("exceptions")));
			plan.setIsFinish(c.getInt(c.getColumnIndex("isFinish")));
			plan.setDuration(c.getInt(c.getColumnIndex("duration")));
			plan.setCreateTime(c.getString(c.getColumnIndex("createTime")));
			plan.setUpdateTime(c.getString(c.getColumnIndex("updateTime")));
			planlist.add(plan);
		}
		c.close();
		return planlist;

	}

	public Plan queryPlan(int pid) {
		Cursor c = db.rawQuery("SELECT * from Plan where id=?",
				new String[] { String.valueOf(pid) });
		Plan plan = new Plan();
		while (c.moveToNext()) {
			plan.setId(c.getInt(c.getColumnIndex("id")));
			plan.setPlanName(c.getString(c.getColumnIndex("planName")));
			plan.setPlanType(c.getInt(c.getColumnIndex("planType")));
			plan.setCreateWay(c.getString(c.getColumnIndex("createWay")));
			plan.setPlanNumber(c.getInt(c.getColumnIndex("planNumber")));
			plan.setExceptions(c.getString(c.getColumnIndex("exceptions")));
			plan.setIsFinish(c.getInt(c.getColumnIndex("isFinish")));
			plan.setDuration(c.getInt(c.getColumnIndex("duration")));
			plan.setCreateTime(c.getString(c.getColumnIndex("createTime")));
			plan.setUpdateTime(c.getString(c.getColumnIndex("updateTime")));
		}
		c.close();
		return plan;

	}

	/**
	 * 新建任务集
	 * 
	 * @param task
	 * @param planname
	 */
	public void insertTasks(List<Task> tasks, int pid) {
		db.beginTransaction();
		try {
			for (Task t : tasks) {
				ContentValues cv = new ContentValues();
				cv.putNull("id");
				cv.put("planId", pid);
				cv.put("taskName", t.getTaskName());
				cv.put("displayName", t.getDisplayName());
				cv.put("taskAction", t.getTaskAction());
				cv.put("formValue", t.getFormValue());
				cv.put("taskCount", t.getTaskCount());
				cv.put("originCount", t.getOriginCount());
				cv.put("resultJson", t.getResultJson());
				cv.put("resultFile", t.getResultFile());
				cv.put("createTime", t.getCreateTime());
				cv.put("updateTime", t.getUpdateTime());

				db.insert("Task", null, cv);
			}
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
			// db.close();
		}
	}

	/**
	 * 更新Task
	 * @param t
	 */
	public void updateTask(Task t) {
		ContentValues cv = new ContentValues();
		cv.put("taskName", t.getTaskName());
		cv.put("displayName", t.getDisplayName());
		cv.put("taskAction", t.getTaskAction());
		cv.put("formValue", t.getFormValue());
		cv.put("taskCount", t.getTaskCount());
		cv.put("originCount", t.getOriginCount());
		cv.put("resultJson", t.getResultJson());
		cv.put("resultFile", t.getResultFile());
		cv.put("createTime", t.getCreateTime());
		cv.put("updateTime", t.getUpdateTime());
		db.update("Task", cv, "id = ?",
				new String[] { String.valueOf(t.getId()) });
	}

	/**
	 * 批量更新Task
	 * @param tasks
	 * @param pid
	 */
	public void updateTasks(List<Task> tasks, int pid) {
		db.beginTransaction();
		try {
			for (Task t : tasks) {
				ContentValues cv = new ContentValues();
				//cv.putNull("id");
				cv.put("planId", pid);
				cv.put("taskName", t.getTaskName());
				cv.put("displayName", t.getDisplayName());
				cv.put("taskAction", t.getTaskAction());
				cv.put("formValue", t.getFormValue());
				cv.put("taskCount", t.getTaskCount());
				cv.put("originCount", t.getOriginCount());
				cv.put("resultJson", t.getResultJson());
				cv.put("resultFile", t.getResultFile());
				cv.put("createTime", t.getCreateTime());
				cv.put("updateTime", t.getUpdateTime());

				db.update("Task", cv, "id = ?",
						new String[] { String.valueOf(t.getId()) });
			}
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
	}

	/**
	 * 新建任务
	 * 
	 * @param task
	 * @param pid
	 */
	public void insertTask(Task t) {
		ContentValues cv = new ContentValues();
		cv.putNull("id");
		cv.put("planId", 0);
		cv.put("taskName", t.getTaskName());
		cv.put("displayName", t.getDisplayName());
		cv.put("taskAction", t.getTaskAction());
		cv.put("formValue", t.getFormValue());
		cv.put("taskCount", t.getTaskCount());
		cv.put("originCount", t.getOriginCount());
		cv.put("resultJson", t.getResultJson());
		cv.put("resultFile", t.getResultFile());
		cv.put("createTime", t.getCreateTime());
		cv.put("updateTime", t.getUpdateTime());

		db.insert("Task", null, cv);

	}

	/**
	 * 查找计划是否存在
	 * 
	 * @param pname
	 * @return
	 */
	public boolean existPlan(String pname) {
		boolean exist = false;
		Cursor c = db.rawQuery("SELECT * FROM Plan where planName=?",
				new String[] { pname });
		if (c.moveToFirst()) {
			exist = true;
		}
		c.close();
		return exist;
	}

	/**
	 * 新建Modules
	 * 
	 * @param modules
	 */
	public void insertModules(List<Module> modules) {
		db.beginTransaction();
		try {
			for (Module m : modules) {
				ContentValues cv = new ContentValues();
				cv.putNull("id");
				cv.put("name", m.getName());
				cv.put("displayName", m.getDisplayName());
				cv.put("desc", m.getDesc());
				cv.put("createTime", m.getCreateTime());

				db.insert("Module", null, cv);
			}
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
			// db.close();
		}
	}

	/**
	 * 新建Module
	 * 
	 * @param modules
	 */
	public int insertModule(Module module) {
		int rid = 0;
		ContentValues cv = new ContentValues();
		cv.putNull("id");
		cv.put("name", module.getName());
		cv.put("displayName", module.getDisplayName());
		cv.put("desc", module.getDesc());
		cv.put("createTime", module.getCreateTime());

		db.insert("Module", null, cv);

		Cursor c = db.rawQuery("SELECT last_insert_rowid() from Module", null);
		if (c.moveToFirst()) {
			rid = c.getInt(0);
		}
		c.close();

		return rid;
	}

	/**
	 * 批量新建TestCase
	 * 
	 * @param modules
	 */
	public void insertTestCases(List<Case> testcases) {
		db.beginTransaction();
		try {
			for (Case m : testcases) {
				ContentValues cv = new ContentValues();
				cv.putNull("id");
				cv.put("serverId", m.getServerId());
				cv.put("moduleId", m.getModuleId());
				cv.put("name", m.getName());
				cv.put("displayName", m.getDisplayName());
				cv.put("value", m.getValue());
				cv.put("action", m.getAction());
				cv.put("entity", m.getEntity());
				cv.put("aloneUse",m.getAloneUse());
				cv.put("isJoin",m.getIsJoin());
				cv.put("count", m.getCount());
				cv.put("desc", m.getDesc());
				cv.put("createTime", m.getCreateTime());
				cv.put("updateTime", m.getUpdateTime());

				db.insert("TestCase", null, cv);
			}
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
			// db.close();
		}
	}

	/**
	 * 新建TestCase
	 * 
	 * @param testcase
	 * @return
	 */
	public int insertTestCase(Case testcase) {
		int testid = 0;

		ContentValues cv = new ContentValues();
		cv.putNull("id");
		cv.put("serverId", testcase.getServerId());
		cv.put("moduleId", testcase.getModuleId());
		cv.put("name", testcase.getName());
		cv.put("displayName", testcase.getDisplayName());
		cv.put("value", testcase.getValue());
		cv.put("action", testcase.getAction());
		cv.put("entity", testcase.getEntity());
		cv.put("aloneUse",testcase.getAloneUse());
		cv.put("isJoin",testcase.getIsJoin());
		cv.put("count", testcase.getCount());
		cv.put("desc", testcase.getDesc());
		cv.put("createTime", testcase.getCreateTime());
		cv.put("updateTime", testcase.getUpdateTime());

		db.insert("TestCase", null, cv);

		Cursor c = db
				.rawQuery("SELECT last_insert_rowid() from TestCase", null);
		if (c.moveToFirst()) {
			testid = c.getInt(0);
		}
		c.close();

		return testid;

	}

	/**
	 * 新建TestCase
	 * 
	 * @param testcase
	 * @return
	 */
	public int insertStability(Stability stability) {
		int testid = 0;

		ContentValues cv = new ContentValues();
		cv.putNull("id");
		cv.put("taskCount", stability.getTaskCount());
		cv.put("testcaseId", stability.getTestCaseId());
		cv.put("desc", stability.getDesc());
		cv.put("createTime", stability.getCreateTime());
		cv.put("updateTime", stability.getUpdateTime());

		db.insert("Stability", null, cv);

		return testid;

	}

	public void insertStabilitys(List<Stability> stabilitys) {
		db.beginTransaction();
		try {
			for (Stability m : stabilitys) {
				ContentValues cv = new ContentValues();
				cv.putNull("id");
				cv.put("taskCount", m.getTaskCount());
				cv.put("testcaseId", m.getTestCaseId());
				cv.put("desc", m.getDesc());
				cv.put("createTime", m.getCreateTime());
				cv.put("updateTime", m.getUpdateTime());

				db.insert("Stability", null, cv);
			}
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
			// db.close();
		}
	}

	/**
	 * 查找所有Module
	 * 
	 * @return
	 */
	public List<Module> queryAllModule() {
		List<Module> modulelist = new ArrayList<Module>();
		Cursor c = db.rawQuery("SELECT * from Module", null);
		while (c.moveToNext()) {
			Module module = new Module();
			module.setId(c.getInt(c.getColumnIndex("id")));
			module.setName(c.getString(c.getColumnIndex("name")));
			module.setDisplayName(c.getString(c.getColumnIndex("displayName")));
			module.setDesc(c.getString(c.getColumnIndex("desc")));
			module.setCreateTime(c.getString(c.getColumnIndex("createTime")));
			modulelist.add(module);
		}
		c.close();
		return modulelist;

	}

	/**
	 * 查找所有TestCase
	 * 
	 * @return
	 */
	public List<Case> queryAllTestCase() {
		List<Case> mclist = new ArrayList<Case>();
		Cursor c = db.rawQuery("SELECT * from TestCase", null);
		while (c.moveToNext()) {
			Case mc = new Case();
			mc.setId(c.getInt(c.getColumnIndex("id")));
			mc.setServerId(c.getString(c.getColumnIndex("serverId")));
			mc.setModuleId(c.getInt(c.getColumnIndex("moduleId")));
			mc.setName(c.getString(c.getColumnIndex("name")));
			mc.setDisplayName(c.getString(c.getColumnIndex("displayName")));
			mc.setValue(c.getString(c.getColumnIndex("value")));
			mc.setAction(c.getString(c.getColumnIndex("action")));
			mc.setEntity(c.getString(c.getColumnIndex("entity")));
			mc.setAloneUse(c.getString(c.getColumnIndex("aloneUse")));
			mc.setIsJoin(c.getInt(c.getColumnIndex("isJoin")));
			mc.setCount(c.getInt(c.getColumnIndex("count")));
			mc.setDesc(c.getString(c.getColumnIndex("desc")));
			mc.setCreateTime(c.getString(c.getColumnIndex("createTime")));
			mc.setUpdateTime(c.getString(c.getColumnIndex("updateTime")));
			mclist.add(mc);
		}
		c.close();
		return mclist;

	}

	public Case queryTestCaseById(int caseid) {
		Cursor c = db.rawQuery("SELECT * from TestCase where id=?",
				new String[] { String.valueOf(caseid) });
		Case mc = new Case();
		while (c.moveToNext()) {
			mc.setId(c.getInt(c.getColumnIndex("id")));
			mc.setServerId(c.getString(c.getColumnIndex("serverId")));
			mc.setModuleId(c.getInt(c.getColumnIndex("moduleId")));
			mc.setName(c.getString(c.getColumnIndex("name")));
			mc.setDisplayName(c.getString(c.getColumnIndex("displayName")));
			mc.setValue(c.getString(c.getColumnIndex("value")));
			mc.setAction(c.getString(c.getColumnIndex("action")));
			mc.setEntity(c.getString(c.getColumnIndex("entity")));
			mc.setAloneUse(c.getString(c.getColumnIndex("aloneUse")));
			mc.setIsJoin(c.getInt(c.getColumnIndex("isJoin")));
			mc.setCount(c.getInt(c.getColumnIndex("count")));
			mc.setDesc(c.getString(c.getColumnIndex("desc")));
			mc.setCreateTime(c.getString(c.getColumnIndex("createTime")));
			mc.setUpdateTime(c.getString(c.getColumnIndex("updateTime")));
		}
		c.close();
		return mc;

	}

	public Case queryTestCaseByServerId(String serverId) {
		Cursor c = db.rawQuery("SELECT * from TestCase where serverId=?",
				new String[] { serverId });
		Case mc = new Case();
		while (c.moveToNext()) {
			mc.setId(c.getInt(c.getColumnIndex("id")));
			mc.setServerId(c.getString(c.getColumnIndex("serverId")));
			mc.setModuleId(c.getInt(c.getColumnIndex("moduleId")));
			mc.setName(c.getString(c.getColumnIndex("name")));
			mc.setDisplayName(c.getString(c.getColumnIndex("displayName")));
			mc.setValue(c.getString(c.getColumnIndex("value")));
			mc.setAction(c.getString(c.getColumnIndex("action")));
			mc.setEntity(c.getString(c.getColumnIndex("entity")));
			mc.setAloneUse(c.getString(c.getColumnIndex("aloneUse")));
			mc.setCount(c.getInt(c.getColumnIndex("count")));
			mc.setIsJoin(c.getInt(c.getColumnIndex("isJoin")));
			mc.setDesc(c.getString(c.getColumnIndex("desc")));
			mc.setCreateTime(c.getString(c.getColumnIndex("createTime")));
			mc.setUpdateTime(c.getString(c.getColumnIndex("updateTime")));
		}
		c.close();
		return mc;

	}

}

package com.gk.touchstone;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.gk.touchstone.core.TestCase;
import com.gk.touchstone.entity.Plan;
import com.gk.touchstone.entity.Report;
import com.gk.touchstone.entity.Task;
import com.gk.touchstone.entity.Case;
import com.gk.touchstone.service.ServiceBroadcastReceiver;

import android.app.Application;
import android.content.Intent;
import android.content.IntentFilter;

public class GkApplication extends Application {
	private List<TestCase> taskbases;
	private List<Task> selectTasks;
	private List<Case> testCases;
	private int planType;
	private long planEndDateTime;
	private boolean reckonByTime;
	public int cameraTimes = 0;
	private String mobileUsingError;
	private Plan plan;
	private String netJson;
	private List<Task> tempTasks;
	private List<Plan> tempPlans;
	private List<Report> reports;
	private Map<String, String> mapStartTime = new HashMap<String, String>();

	public Plan getPlan() {
		return plan;
	}

	public void setPlan(Plan plan) {
		this.plan = plan;
	}

	public String getNetJson() {
		return netJson;
	}

	public void setNetJson(String netJson) {
		this.netJson = netJson;
	}

	public boolean getReckonByTime() {
		return reckonByTime;
	}

	public void setReckonByTime(boolean reckonByTime) {
		this.reckonByTime = reckonByTime;
	}

	/**
	 * 读取计划结束时间
	 * 
	 * @return
	 */
	public long getPlanEndDateTime() {
		return planEndDateTime;
	}

	/**
	 * 写入计划结束时间
	 * 
	 * @return
	 */
	public void setPlanEndDateTime(long planEndDateTime) {
		this.planEndDateTime = planEndDateTime;
	}

	/**
	 * 获取串并行计划类型
	 * 
	 * @return
	 */
	public int getPlanType() {
		return planType;
	}

	/**
	 * 新建计划任务时，存储串并行计划
	 * 
	 * @param planType
	 */
	public void setPlanType(int planType) {
		this.planType = planType;
	}

	/**
	 * 获取任务基类集合
	 * 
	 * @return
	 */
	public List<TestCase> getTaskBases() {
		return taskbases;
	}

	/**
	 * 存储任务基类集合
	 * 
	 * @param taskbases
	 */
	public void setTaskBases(List<TestCase> taskbases) {
		this.taskbases = taskbases;
	}

	/**
	 * PlanSetting Task传递
	 * 
	 * @return
	 */
	public List<Task> getSelectTasks() {
		return selectTasks;
	}

	public void setSelectTasks(List<Task> selectTasks) {
		this.selectTasks = selectTasks;
	}

	public List<Case> getTestCaseSettings() {
		return testCases;
	}

	public void setTestCaseSettings(List<Case> testCases) {
		this.testCases = testCases;
	}

	// json 临时存储
	public List<Task> getTempTasks() {
		return tempTasks;
	}

	public void setTempTasks(List<Task> tempTasks) {
		this.tempTasks = tempTasks;
	}

	public List<Plan> getTempPlans() {
		return tempPlans;
	}

	public void setTempPlans(List<Plan> tempPlans) {
		this.tempPlans = tempPlans;
	}

	public List<Report> getTaskRepots() {
		return reports;
	}

	public void setTaskRepots(List<Report> reports) {
		this.reports = reports;
	}

	public String getMobileUsingError() {
		return mobileUsingError;
	}

	public void setMobileUsingError(String mobileUsingError) {
		this.mobileUsingError = mobileUsingError;
	}

	public Map<String, String> getMapStartTime() {
		return mapStartTime;
	}

	/**
	 * 存储每个任务的开始时间
	 * 
	 * @param key
	 * @param value
	 */
	public void setMapStartTime(String key, String value) {
		this.mapStartTime.put(key, value);
	}

	// TODO 重启应用，需要预先写入一个SharedPreferences

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();

		// 每分钟发送一次广播，判断service状态，是否重新启动(需动态注册)
		IntentFilter intentFilter = new IntentFilter(Intent.ACTION_TIME_TICK);
		ServiceBroadcastReceiver receiver = new ServiceBroadcastReceiver();
		registerReceiver(receiver, intentFilter);

	}

}

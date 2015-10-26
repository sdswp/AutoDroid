package com.gk.touchstone.entity;

import java.util.List;
import java.util.Map;

public class TaskJson {
	private String taskId;
	private String taskName;
	private int taskType;
	private int taskCount;
	private List<Map<String, Object>> testCases;

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public int getTaskType() {
		return taskType;
	}

	public void setTaskType(int taskType) {
		this.taskType = taskType;
	}

	public int getTaskCount() {
		return taskCount;
	}

	public void setTaskCount(int taskCount) {
		this.taskCount = taskCount;
	}

	public List<Map<String, Object>> getTestCases() {
		return testCases;
	}

	public void setTestCases(List<Map<String, Object>> testCases) {
		this.testCases = testCases;
	}

}

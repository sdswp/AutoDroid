package com.gk.touchstone.entity;

import java.util.List;


public class TestResultJson {
	private int taskcount;
	private List<Result> task;
	
	public int getTaskCount() {
		return taskcount;
	}

	public void setTaskCount(int taskcount) {
		this.taskcount = taskcount;
	}
	
	public List<Result> getTask() {
		return task;
	}

	public void setTask(List<Result> task) {
		this.task = task;
	}
}

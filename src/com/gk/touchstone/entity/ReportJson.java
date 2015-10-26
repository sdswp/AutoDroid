package com.gk.touchstone.entity;

import java.util.List;

public class ReportJson {
	private int taskCount;
	private String startTime;
	private String endTime;
	private List<ResultListJson> resultList;

	public int getTaskCount() {
		return taskCount;
	}

	public void setTaskCount(int taskCount) {
		this.taskCount = taskCount;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public List<ResultListJson> getResultList() {
		return resultList;
	}

	public void setResultList(List<ResultListJson> resultList) {
		this.resultList = resultList;
	}

}

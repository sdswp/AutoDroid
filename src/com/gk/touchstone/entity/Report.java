package com.gk.touchstone.entity;

public class Report implements Comparable<Report> {
	private int resultId;
	private String state;
	private String reason;
	private int taskCount;
	private int resultLists;
	private String failPosition;// Fail产生的第[5,6]
	private String writeTime;
	private String startTime;
	private String endTime;
	private String date;
	private int times;

	public int getResultId() {
		return resultId;
	}

	public void setResultId(int resultId) {
		this.resultId = resultId;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public int getTaskCount() {
		return taskCount;
	}

	public void setTaskCount(int taskCount) {
		this.taskCount = taskCount;
	}

	public int getResultLists() {
		return resultLists;
	}

	public void setResultLists(int resultLists) {
		this.resultLists = resultLists;
	}

	public String getFailPosition() {
		return failPosition;
	}

	public void setFailPosition(String failPosition) {
		this.failPosition = failPosition;
	}

	public String getWriteTime() {
		return writeTime;
	}

	public void setWriteTime(String writeTime) {
		this.writeTime = writeTime;
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

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public int getTimes() {
		return times;
	}

	public void setTimes(int times) {
		this.times = times;
	}

	@Override
	public int compareTo(Report another) {
		String resultid = String.valueOf(this.getResultId());
		String anotherid = String.valueOf(another.getResultId());
		return resultid.compareTo(anotherid);
	}

}

package com.gk.touchstone.entity;

/**
 * log json文件内的task
 * 
 * @author Administrator
 * 
 */
public class ResultListJson {
	private String date;
	private String reason;
	private String result;
	private int times;

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public int getTimes() {
		return times;
	}

	public void setTimes(int times) {
		this.times = times;
	}

}

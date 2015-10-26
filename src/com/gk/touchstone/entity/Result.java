package com.gk.touchstone.entity;

public class Result {
	private int times;
	private String result;
	private String reason;
	private String date;

	/**
	 * 获取次序
	 * 
	 * @return
	 */
	public int getTimes() {
		return times;
	}

	/**
	 * 存储次序
	 * 
	 * @param count
	 */
	public void setTimes(int times) {
		this.times = times;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public void setResultData(int times, String result, String reason,
			String date) {
		this.times = times;
		this.result = result;
		this.reason = reason;
		this.date = date;
	}

	// public void setResultData(boolean state, int times, String date) {
	// this.times = times;
	// this.state = state;
	// this.date = date;
	// }
	//
	// public void setResultData(String reason, int times, String date) {
	// this.times = times;
	// this.reason = reason;
	// this.date = date;
	// }
	//
	// public void setResultData(int count, int times, String date) {
	// this.times = times;
	// this.count = count;
	// this.date = date;
	// }

}

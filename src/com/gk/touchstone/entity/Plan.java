package com.gk.touchstone.entity;

public class Plan {
	public int id;
	private String planName;
	private int planType;
	private String createWay;
	private int planNumber;
	private String exceptions;
	private int isFinish;
	private float duration;
	private String createTime;
	private String updateTime;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getPlanName() {
		return planName;
	}

	public void setPlanName(String planName) {
		this.planName = planName;
	}

	public int getPlanType() {
		return planType;
	}

	public void setPlanType(int planType) {
		this.planType = planType;
	}

	public String getCreateWay() {
		return createWay;
	}

	public void setCreateWay(String createWay) {
		this.createWay = createWay;
	}

	public int getPlanNumber() {
		return planNumber;
	}

	public void setPlanNumber(int planNumber) {
		this.planNumber = planNumber;
	}

	public String getExceptions() {
		return exceptions;
	}

	public void setExceptions(String exceptions) {
		this.exceptions = exceptions;
	}

	public int getIsFinish() {
		return isFinish;
	}

	public void setIsFinish(int isFinish) {
		this.isFinish = isFinish;
	}

	public float getDuration() {
		return duration;
	}

	public void setDuration(float duration) {
		this.duration = duration;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

}
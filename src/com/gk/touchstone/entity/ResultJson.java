package com.gk.touchstone.entity;

public class ResultJson {
	private String TaskId;
	private String TestCaseId;
	private String DeviceId;
	private String TestResult;
	private String TestResultFile;

	public String getTaskId() {
		return TaskId;
	}

	public void setTaskId(String taskId) {
		TaskId = taskId;
	}

	public String getTestCaseId() {
		return TestCaseId;
	}

	public void setTestCaseId(String testCaseId) {
		TestCaseId = testCaseId;
	}

	public String getDeviceId() {
		return DeviceId;
	}

	public void setDeviceId(String deviceId) {
		DeviceId = deviceId;
	}

	public String getTestResult() {
		return TestResult;
	}

	public void setTestResult(String testResult) {
		TestResult = testResult;
	}

	public String getTestResultFile() {
		return TestResultFile;
	}

	public void setTestResultFile(String testResultFile) {
		TestResultFile = testResultFile;
	}
}

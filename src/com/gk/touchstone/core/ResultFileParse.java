package com.gk.touchstone.core;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;

import com.gk.touchstone.entity.Report;
import com.gk.touchstone.entity.ReportJson;
import com.gk.touchstone.entity.ResultListJson;
import com.gk.touchstone.entity.Task;
import com.gk.touchstone.utils.JsonValidator;
import com.google.gson.Gson;

public class ResultFileParse {
	private Context context;
	private Gson gson;

	public ResultFileParse(Context context) {
		this.context = context;
		gson = new Gson();
	}

	/**
	 * report 通过task分类。
	 * 
	 * @param reports
	 * @param task
	 * @return
	 */
	public List<Report> getReportByTask(List<Report> reports, Task task) {

		List<Report> filter = new ArrayList<Report>();

		for (Report r : reports) {
			if (r.getResultId() == task.getId()) {
				filter.add(r);
			}
		}
		return filter;
	}

	/**
	 * report 通过task分类,只取fail的内容
	 * 
	 * @param reports
	 * @param task
	 * @return
	 */
	public List<Report> getReportByTask(List<Report> reports, Task task,
			String state) {
		if (reports == null || reports.size() == 0) {
			return null;
		}

		List<Report> filter = new ArrayList<Report>();

		for (Report r : reports) {
			if (r.getResultId() == task.getId() && r.getState().equals(state)) {
				filter.add(r);
			}
		}
		return filter;
	}

	/**
	 * 从已分类的report中比较最大的时间值
	 * 
	 * @param reports
	 * @param task
	 * @return
	 */
	public String getMaxTime(List<Report> reports) {
		if (reports == null || reports.size() == 0) {
			return "0000 00:00:00~0000 00:00:00";
		}

		String startTime = reports.get(0).getStartTime();
		String endTime = reports.get(0).getEndTime();
		for (Report report : reports) {
			// 开始时间取最小值，结束时间取最大值
			String newST = report.getStartTime();
			if (getIntervalTimes(startTime, newST) > 0) {
				startTime = newST;
			}

			String newET = report.getEndTime();
			if (getIntervalTimes(endTime, newET) < 0) {
				endTime = newET;
			}
		}

		return "日期:" + simpleDateStr(startTime) + "~" + simpleDateStr(endTime);

	}

	// 精简日期格式
	private String simpleDateStr(String timeStr) {
		if (timeStr.equals("")) {
			return "";
		}
		return timeStr.replace("-", "").substring(2);
	}

	/**
	 * 从已分类的report中取Fail数
	 * 
	 * @return
	 */
	public int getFailCount(List<Report> reports) {
		if (reports == null || reports.size() == 0) {
			return 0;
		}

		int count = 0;
		for (Report report : reports) {
			if (report.getState().equals(Constants.FAIL)) {
				count++;
			}
		}
		return count;

	}

	/**
	 * 获取测试一条计划内所有的log
	 * 
	 * @param tasks
	 * @return
	 */
	public List<Report> getReportFromTasks(List<Task> tasks) {
		List<Report> reports = new ArrayList<Report>();

		for (Task t : tasks) {
			List<Report> singleTaskReorts = getReportFromTask(t);
			if (singleTaskReorts != null && singleTaskReorts.size() > 0) {
				reports.addAll(singleTaskReorts);
			}
		}

		return reports;
	}

	/**
	 * 获取指定场景的log
	 * 
	 * @param task
	 * @return
	 */
	public List<Report> getReportFromTask(Task task) {// getResultFile.split(".log")
		List<Report> reports = new ArrayList<Report>();

		// String log = readLog(task.getResultFile());
		String log = readLog(getLastLog(task.getResultFile()));

		List<ReportJson> reportJsons = getReportJson(log);
		if (reportJsons == null || reportJsons.size() <= 0) {
			return null;
		}

		for (ReportJson reportjson : reportJsons) {
			List<ResultListJson> ResultListJsons = reportjson.getResultList();

			if (ResultListJsons != null && ResultListJsons.size() > 0) {

				for (ResultListJson result : ResultListJsons) {
					Report report = new Report();
					report.setResultId(task.getId());
					report.setReason(result.getReason());

					String resultStr = "";
					if (result.getResult().contains(Constants.FAIL)) {
						resultStr = Constants.FAIL;
					} else {
						resultStr = Constants.PASS;
					}
					report.setState(resultStr);// PASS FAIL
					report.setTaskCount(reportjson.getTaskCount());
					report.setResultLists(ResultListJsons.size());

					int startT = task.getTaskCount() - report.getTaskCount();
					// 判断单次任务Count=0时的计数
					if (task.getTaskCount() == 0) {
						startT = 1;
					} else {
						startT = task.getTaskCount() - report.getTaskCount();
					}

					int endT = ResultListJsons.size() - result.getTimes();
					report.setFailPosition(String.valueOf(startT) + ","
							+ String.valueOf(endT));
					report.setWriteTime(result.getDate());
					report.setStartTime(reportjson.getStartTime());
					report.setEndTime(reportjson.getEndTime());
					report.setTimes(result.getTimes());
					report.setDate(result.getDate());
					reports.add(report);
				}
			}
		}
		return reports;
	}

	public List<ReportJson> getReportJson(String logStr) {
		String splitStr = "\\}\\]\\}\\,";
		if (logStr == null || logStr == "" || !logStr.contains("}]},")) {
			return null;
		}
		// TODO test 空内容log split
		String[] jsons = logStr.split(splitStr);
		if (jsons == null) {
			return null;
		}

		JsonValidator jv = new JsonValidator();

		List<ReportJson> reportJsons = new ArrayList<ReportJson>();

		// 验证每个split后的json字符串，放入List<ReportJson> rj
		for (String s : jsons) {
			String singleJson = s + "}]}";

			if (jv.validate(singleJson)) {
				// System.out.println(singleJson);
				ReportJson rj = gson.fromJson(singleJson, ReportJson.class);
				if (rj != null) {
					reportJsons.add(rj);
				}
			}
		}

		return reportJsons;
	}

	/**
	 * 读取data/data/<包名>/files 下的log
	 * 
	 * @param logFileName
	 * @return
	 */
	public String readLog(String logFileName) {
		String content = null;
		String dir = context.getFilesDir() + File.separator + logFileName;
		File file = new File(dir);
		if (!file.exists()) {
			return null;
		}
		try {
			FileInputStream fis = context.openFileInput(logFileName);

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int len = 0;
			while ((len = fis.read(buffer)) != -1) {
				baos.write(buffer, 0, len);
			}
			content = baos.toString();
			fis.close();
			baos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return content;
	}

	/**
	 * 任务多次测试结果，log文件追加到resultfile，先临时获取最后一个log
	 * 
	 * @param logFileName
	 * @return
	 */
	public String getLastLog(String logFileName) {
		String[] strs = logFileName.split(".log");
		return strs[strs.length - 1] + ".log";
	}
	
	public static Date strToDate(String dateTime) {
		if (dateTime.equals("")) {
			return null;
		}
		DateFormat fmt = new SimpleDateFormat(Constants.DATE_YMDHMS);
		Date date = null;
		try {
			date = fmt.parse(dateTime);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return date;
		// java.sql.Date sqlDate = java.sql.Date.valueOf(dateTime);
		// return sqlDate;
	}

	public static long getIntervalTimes(String startDate, String endDate) {
		// Date resDate = sdf.parse(dateStr);
		if (strToDate(startDate) == null || strToDate(endDate) == null) {
			return 0;
		}

		long decreaseTimes = strToDate(startDate).getTime()
				- strToDate(endDate).getTime();
		return decreaseTimes;
	}

}

package com.gk.touchstone.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.gk.touchstone.core.Constants;

import android.annotation.SuppressLint;

public class DateTimes {

	@SuppressLint("SimpleDateFormat")
	public static String formatDateTime(Date date, String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(date);
	}

	// 获取系统当前时间
	public static String getCurrentTime(String format) {
		Date curDate = new Date(System.currentTimeMillis());
		return formatDateTime(curDate, format);
	}

	/**
	 * 当前日期与小时(h)相加
	 * 
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public static Date getCurrentAddDate(long ms) {
		Date curDate = new Date(System.currentTimeMillis() + ms);
		return curDate;
	}

	/**
	 * 小时转毫秒
	 * 
	 * @param hours
	 * @return
	 */
	public static long hourToMs(int hours) {
		return hours * 60 * 60 * 1000;
	}

	public static long msToHour(long ms) {
		return ms / (60 * 60 * 1000);
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

	public static long getIntervalTimes(Date startDate, Date endDate) {
		// Date resDate = sdf.parse(dateStr);
		long decreaseTimes = endDate.getTime() - startDate.getTime();
		return decreaseTimes;
	}

	public static long getDiff(String startTime, String endTime) {
		long diff = 0;
		SimpleDateFormat ft = new SimpleDateFormat(Constants.DATE_YMDHMS);

		try {
			Date startDate = ft.parse(startTime);
			Date endDate = ft.parse(endTime);
			diff = startDate.getTime() - endDate.getTime();
			diff = diff / 1000;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return diff;
	}

	public static String getLeftTime(long ms) {

		long hour = ms / 60 / 60 / 1000;
		long minute = (ms - hour * 60 * 60 * 1000) / 60 / 1000;
		long sec = ((ms - hour * 60 * 60 * 1000) - minute * 60 * 1000) / 1000;

		return hour + "小时" + minute + "分钟" + sec + "秒";

	}

}

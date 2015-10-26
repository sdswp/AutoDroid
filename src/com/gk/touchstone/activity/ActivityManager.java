package com.gk.touchstone.activity;

import java.util.LinkedList;
import java.util.List;

import android.app.Activity;

/**
 * 退出程序时退出所有的Activity
 * Activity的oncreat方法里面加上ActivityManager.getInstance().addActivity(this);
 * 退出时调用：ActivityManager.getInstance().exit();
 * 
 */

public class ActivityManager {
	private List<Activity> activitys = new LinkedList<Activity>();
	private static ActivityManager instance;

	private ActivityManager() {
	}

	// 单例模式中获取唯一的MyApplication实例
	public static ActivityManager getInstance() {
		if (null == instance) {
			instance = new ActivityManager();
		}
		return instance;
	}

	// 添加Activity到容器中
	public void addActivity(Activity activity) {
		activitys.add(activity);
	}

	public void clearActivity() {
		for (Activity a : activitys) {
			a.finish();
		}
	}

	// 遍历所有Activity并finish
	public void exit() {
		for (Activity activity : activitys) {
			activity.finish();
		}
		System.exit(0);
	}
}
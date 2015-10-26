package com.gk.touchstone.core;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import android.content.Context;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.gk.touchstone.entity.Task;

import dalvik.system.BaseDexClassLoader;

public class TestSuite implements Test {
	private Context context;
	private Vector<Test> tests = new Vector<Test>();// Vector支持线程的同步，某一时刻只有一个线程能够写Vector，避免多线程同时写而引起的不一致性
	private String jarName;

	public TestSuite(Context context, Task task) {
		this.context = context;
		addTestsFromTestCase(task);
	}
	
	public TestSuite(Context context, Task task, String jarName) {
		this.context = context;
		this.jarName = jarName;
		addTestsFromJar(task);
	}

	public void addTestsFromTestCase(Task task) {
		try {
			String action = task.getTaskAction();
			Class<?> cls = Class.forName(Constants.PACKAGE_TASK + action);
			Constructor<?> constructor = cls.getConstructor(new Class[] {
					Context.class, Task.class });
			TestCase testcase = (TestCase) constructor
					.newInstance(new Object[] { context, task });

			if (isHardwareExist(testcase)) {
				tests.add(testcase);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void addTestsFromJar(Task task) {
		final File optimizedDexOutputPath = new File(Environment
				.getExternalStorageDirectory().toString()
				+ File.separator
				+ jarName);
		// + "testss.jar");

		BaseDexClassLoader cl = new BaseDexClassLoader(Environment
				.getExternalStorageDirectory().toString(),
				optimizedDexOutputPath,
				optimizedDexOutputPath.getAbsolutePath(),
				context.getClassLoader());
		
		try {
			String action = task.getTaskAction();
			Class<?> cls = cl.loadClass("com.gk.touchstone.testcase."
					+ action);

			Constructor<?> constructor = cls.getConstructor(new Class[] {
					Context.class, Task.class });
			TestCase testcase = (TestCase) constructor
					.newInstance(new Object[] { context, task });

			if (isHardwareExist(testcase)) {
				tests.add(testcase);
			}

		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}
	


//	/**
//	 * 用反射批量获取本次计划任务到app.getTaskBases
//	 * 
//	 * @param tasklist
//	 * @return
//	 */
//	public List<TestCase> reflectTaskList(List<Task> tasklist) {
//		List<TestCase> taskbases = new ArrayList<TestCase>();
//		for (Task task : tasklist) {
//			try {
//				String action = task.getTaskAction();
//				Class<?> cls = Class.forName(Constants.PACKAGE_TASK + action);
//				Constructor<?> constructor = cls.getConstructor(new Class[] {
//						Context.class, Task.class });
//				TestCase taskbase = (TestCase) constructor
//						.newInstance(new Object[] { context, task });
//
//				if (isHardwareExist(taskbase)) {
//					taskbases.add(taskbase);
//				}
//
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//		return taskbases;
//	}
//
//	/**
//	 * 加载SD卡内的jar用例包
//	 * 
//	 * @param tasklist
//	 * @param casePackageName
//	 *            存放在SD卡内的用例jar包名
//	 * @return
//	 */
//	public List<TestCase> reflectTaskList(List<Task> tasklist,
//			String casePackageName) {
//		final File optimizedDexOutputPath = new File(Environment
//				.getExternalStorageDirectory().toString()
//				+ File.separator
//				+ casePackageName);
//		// + "testss.jar");
//
//		BaseDexClassLoader cl = new BaseDexClassLoader(Environment
//				.getExternalStorageDirectory().toString(),
//				optimizedDexOutputPath,
//				optimizedDexOutputPath.getAbsolutePath(),
//				context.getClassLoader());
//
//		List<TestCase> taskbases = new ArrayList<TestCase>();
//		for (Task task : tasklist) {
//			try {
//				String action = task.getTaskAction();
//				Class<?> cls = cl.loadClass("com.gk.touchstone.testcase."
//						+ action);
//
//				Constructor<?> constructor = cls.getConstructor(new Class[] {
//						Context.class, Task.class });
//				TestCase taskbase = (TestCase) constructor
//						.newInstance(new Object[] { context, task });
//
//				if (isHardwareExist(taskbase)) {
//					taskbases.add(taskbase);
//				}
//
//			} catch (Exception exception) {
//				exception.printStackTrace();
//			}
//		}
//		return taskbases;
//	}

	/**
	 * 暂时用分辨率判断手表，屏蔽硬件模块不支持的场景。
	 * 
	 * @param taskbase
	 * @return
	 */
	public boolean isHardwareExist(TestCase taskbase) {
		DisplayMetrics metric = new DisplayMetrics();
		WindowManager windowMgr = (WindowManager) context
				.getApplicationContext().getSystemService(
						Context.WINDOW_SERVICE);
		windowMgr.getDefaultDisplay().getMetrics(metric);

		int screenHeight = metric.heightPixels;

		String[] hardwares = new String[] { "Camera", "Mobile", "Sms",
				"AutoCall" };

		for (String s : hardwares) {
			if (taskbase.taskName.contains(s) && screenHeight < 250) {
				return false;
			}
		}

		return true;
	}

	@Override
	public void Start() {
		// TODO Auto-generated method stub

	}

	@Override
	public void Finish() {
		// TODO Auto-generated method stub

	}

	@Override
	public void Stop() {
		// TODO Auto-generated method stub

	}

}

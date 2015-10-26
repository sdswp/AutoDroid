package com.gk.touchstone.taskview;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.gk.touchstone.GkApplication;
import com.gk.touchstone.R;
import com.gk.touchstone.core.TestCase;
import com.gk.touchstone.core.TaskManager;
import com.gk.touchstone.utils.CameraView;
import com.gk.touchstone.core.Constants;
import com.gk.touchstone.entity.Result;
import com.gk.touchstone.utils.Utils;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

public class CameraFrontPreviewGet extends Activity {
	/** 图片媒体类型 **/
	public static final int MEDIA_TYPE_IMAGE = 1;
	/** 摄像头类的对象 **/
	private Camera mCamera;
	/** SurfaceView对象 **/
	private CameraView mPreview;

	private File dir = null;

	private Utils us;
	private int i = 0;
	private Handler testHandler;
	private int internalTime;
	private Intent intent;
	protected List<Result> ResultList;
	private TaskManager tm;
	private GkApplication app;
	private String className = "";
	private int newTimeCount = 0;
	private CameraInfo cameraInfo;
	private Thread testThread=null;

	// 存储任务计数
	/**
	 * 每次任务结束时发广播更新
	 */
	// public int taskCount = 0;
	// private Task task;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		us = new Utils(this);
		ResultList = new ArrayList<Result>();
		app = (GkApplication) getApplication();
		tm = new TaskManager(this);
		testHandler = new myHandler();
		Window window = getWindow();// 得到窗口
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 没有标题
		window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏
		window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);// 保持屏幕亮

		setContentView(R.layout.getcamerapreview);

		// 获取Camera对象的实例
		mCamera = getCameraInstance();
		// 初始化SurfaceView
		mPreview = new CameraView(this, mCamera);
		FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
		// 将SurfaceView添加到FrameLayout中
		preview.addView(mPreview);
		// 设置相机的属性
		Camera.Parameters params = mCamera.getParameters();
		// JPEG质量设置到最好
		params.setJpegQuality(100);
		// 散光灯模式设置为自动调节
		params.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
		mCamera.setParameters(params);

		intent = this.getIntent();
		className = intent.getStringExtra("className");
		internalTime = intent.getIntExtra("internalTime", 1);
		// timeCount = intent.getIntExtra("TestTimes", 1);

		testThread = new testThread();
		testThread.start();
	}

	// PictureCallback回调函数实现
	private PictureCallback mPicture = new PictureCallback() {
		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
			if (pictureFile == null) {
				return;
			}
			// 将照片数据data写入指定的文件
			try {
				FileOutputStream fos = new FileOutputStream(pictureFile);
				fos.write(data);
				fos.close();

			} catch (FileNotFoundException e) {
				tm.getCameraTask(className).writeResult(tm.getCameraTask(className).timeCount, Constants.FAIL,
						e.getMessage());
				e.printStackTrace();
			} catch (IOException e) {
				tm.getCameraTask(className).writeResult(tm.getCameraTask(className).timeCount, Constants.FAIL,
						e.getMessage());
				e.printStackTrace();
			}
		}
	};
	// 快门的回调函数实现
	private ShutterCallback mShutter = new ShutterCallback() {
		@Override
		public void onShutter() {

		}
	};

	// 释放Camera对象（务必实现）
	private void releaseCamera() {
		if (mCamera != null) {
			mCamera.release();
			mCamera = null;
		}
	}

	private Camera getCameraInstance() {
		Camera c = null;
		cameraInfo = new CameraInfo();
		try {
			switch (cameraInfo.facing) {
			case Camera.CameraInfo.CAMERA_FACING_BACK:
				c = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
				break;
			case Camera.CameraInfo.CAMERA_FACING_FRONT:
				c = Camera.open();
				break;
			default:
				break;
			}
		} catch (Exception e) {
			tm.getCameraTask(className).writeResult(tm.getCameraTask(className).timeCount, Constants.FAIL,
					e.getMessage());
			e.printStackTrace();
		}
		return c;
	}

	/**
	 * 在指定路径创建照片文件
	 * 
	 * @param type
	 * @return
	 */
	private File getOutputMediaFile(int type) {
		// 指定照片存放的目录，在SD根目录下的一个文件夹中

		dir = Environment.getExternalStorageDirectory();
		File mediaStorageDir = new File(us.getAppSDCardPath() + File.separator
				+ Constants.FOLDER_CAMERA);
		// Environment
		// .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
		// "CameraUseApp");

		// 文件夹不存在，则创建该文件夹
		if (!mediaStorageDir.exists()) {
			mediaStorageDir.mkdirs();
			if (!mediaStorageDir.mkdirs()) {
				Log.d("CameraUse", "failed to create directory");
				return null;
			}
		}
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
				.format(new Date());
		// 创建照片文件
		File mediaFile;
		if (type == MEDIA_TYPE_IMAGE) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator
					+ "IMG_" + timeStamp + ".jpg");
		} else {
			return null;
		}
		return mediaFile;
	}

	@Override
	protected void onPause() {
		super.onPause();
		releaseCamera();
	}

	class testThread extends Thread {
		@Override
		public void run() {
			super.run();
			try {
				// 相机启动时间
				sleep(4000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				tm.getCameraTask(className).writeResult(tm.getCameraTask(className).timeCount, Constants.FAIL,
						e1.getMessage());
				e1.printStackTrace();
			}
			
			newTimeCount = tm.getCameraTask(className).timeCount;
			while (newTimeCount > 0) {
				try {
					sleep(1000 * internalTime);
					mCamera.takePicture(mShutter, null, mPicture);
					sleep(3000);
					mCamera.startPreview();
					newTimeCount--;
					tm.getCameraTask(className).timeCount = newTimeCount;
					testHandler.sendEmptyMessage(0);
				} catch (Exception e) {
					tm.getCameraTask(className).writeResult(newTimeCount, Constants.FAIL,
							e.getMessage());
					e.printStackTrace();
				}
			}
			testHandler.sendEmptyMessage(1);
		}
	}
	
	private void writeResultOnActivity(int count, String resultStr,
			String reasonStr) {
		if (app.getTaskBases() != null) {
			TestCase taskbase = tm.getCameraTask(className);
			taskbase.writeResult(count, resultStr, reasonStr);
		}
	}
	
	@Override
	public void onBackPressed() {
		us.myToast(tm.getCameraTask(className).displayName, "未完成，请等待...");
	}
	
	@Override
	protected void onDestroy() {
		newTimeCount=0;
		super.onDestroy();
	}

	private class myHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				writeResultOnActivity(newTimeCount, Constants.PASS, "");
				break;
			case 1:
				tm.getCameraTask(className).Finish();
				finish();
				// simulateKey(KeyEvent.KEYCODE_BACK);
				break;
			default:
				break;
			}
		}
	}

	/*
	 * private TaskBase getCameraTask() { // int taskcount=0; TaskBase taskbase
	 * = null; List<TaskBase> taskbases = app.getTaskBases(); if (taskbases !=
	 * null && taskbases.size() > 0) { for (TaskBase t : taskbases) { if
	 * (t.taskName.equals(className)) { taskbase = t; break; }
	 * 
	 * } }
	 * 
	 * return taskbase; }
	 */

	public static void simulateKey(final int KeyCode) {
		new Thread() {
			public void run() {
				try {
					Instrumentation inst = new Instrumentation();
					inst.sendKeyDownUpSync(KeyCode);
				} catch (Exception e) {
					Log.e("Exception when sendKeyDownUpSync", e.toString());
				}
			}
		}.start();
	}

	/*
	 * @SuppressLint("SimpleDateFormat") private String currentTime() { String
	 * startdate = ""; SimpleDateFormat formatter = new
	 * SimpleDateFormat("MM-dd HH:mm:ss"); Date curDate = new
	 * Date(System.currentTimeMillis());// 获取当前时间 startdate =
	 * formatter.format(curDate); return startdate; }
	 * 
	 * public void logd(String tips) { Log.d(this.getClass().getSimpleName(),
	 * tips); }
	 * 
	 * private void writeResult(int timeCount, String result, String reason) {
	 * TestResult tr = new TestResult(); tr.setResultData(timeCount, result,
	 * reason, currentTime()); ResultList.add(tr);
	 * 
	 * //String prefsName = task.getTaskName(); //int taskcountPrefs =
	 * tm.getTaskSharePrefs(className);
	 * 
	 * logd("开始记录" + className + ":[" + getCameraTask().taskCount + "-" +
	 * timeCount + "]" + result + reason);
	 * 
	 * if (app.getTaskBases().size() == 1 && getCameraTask().taskCount == 0 &&
	 * timeCount == 0) { tm.sendTaskBroadcast(0); } else {
	 * tm.sendTaskBroadcast(1); } }
	 */

}

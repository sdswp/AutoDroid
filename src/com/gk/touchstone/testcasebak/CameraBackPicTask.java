package com.gk.touchstone.testcasebak;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;

import com.gk.touchstone.taskview.CameraPreviewGet;
import com.gk.touchstone.core.TestCase;
import com.gk.touchstone.entity.Task;
import com.gk.touchstone.utils.CameraView;

public class CameraBackPicTask extends TestCase{
	//private Camera camera;
	Bundle bundle = null;// 声明一个Bundle对象，用来存储数据
	//private Camera.Parameters parameters = null;
	private int internalTime;
	CameraView view;
	private int cameraCount = 0;

	public CameraBackPicTask(Context context, Task task) {
		super(context, task);
		
		initData();
	}
	
	private void initData() {
		timeCount = getIntValue("backCameraPicTimes");
		internalTime = getIntValue("backCameraPicInterval");
	}

	@Override
	public void Start() {
		initData();
		
		Intent intent = new Intent();
		intent.putExtra("className", task.getTaskName());
		//intent.putExtra("TestTimes", timeCount);
		intent.putExtra("internalTime", internalTime);
		intent.setClass(context, CameraPreviewGet.class);
		//For run in Service without Crash
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		cameraCount = Camera.getNumberOfCameras();
		//Toast.makeText(context, String.valueOf(cameraCount), 3000).show();
		context.startActivity(intent);
		/*isRunning = true;
		if (mThread == null) {
			mThread = new cameraBackThread();
			mThread.start();
		}*/
	}

	@Override
	public void Finish() {
		// TODO Auto-generated method stub
		TimerTask task = new TimerTask(){
			public void run(){
				sendBroadcast();
			}
		};
		
		Timer timer  = new Timer();
		timer.schedule(task, 3000);
		
		return;
	}

	@Override
	public void Stop() {
		// TODO Auto-generated method stub
		TimerTask task = new TimerTask(){
			public void run(){
				stopTask();
			}
		};
		
		Timer timer  = new Timer();
		timer.schedule(task, 3000);
	}
}

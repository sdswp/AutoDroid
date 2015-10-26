package com.gk.touchstone.testcasebak;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.gk.touchstone.taskview.CameraVideoPreviewGet;
import com.gk.touchstone.core.TestCase;
import com.gk.touchstone.entity.Task;
import com.gk.touchstone.utils.CameraView;

public class CameraBackVideoTask extends TestCase{
	
	Bundle bundle = null;// 声明一个Bundle对象，用来存储数据
	private int internalTime;
	CameraView view;

	public CameraBackVideoTask(Context context, Task task) {
		super(context, task);

		initData();
	}

	private void initData() {
		timeCount = getIntValue("backCameraVideoTimes");
		internalTime = getIntValue("backCameraVideoInterval");
	}
	
	@Override
	public void Start() {
		// TODO Auto-generated method stub
		initData();
		
		Intent intent = new Intent();
		intent.putExtra("className", task.getTaskName());
		//intent.putExtra("TestTimes", timeCount);
		intent.putExtra("internalTime", internalTime);
		intent.setClass(context, CameraVideoPreviewGet.class);
		//For run in Service without Crash
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}

	@Override
	public void Finish() {
		// TODO Auto-generated method stub
		TimerTask task = new TimerTask() {
			public void run() {
				sendBroadcast();
			}
		};

		Timer timer = new Timer();
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

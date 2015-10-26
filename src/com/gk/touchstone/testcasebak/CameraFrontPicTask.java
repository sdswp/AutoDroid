package com.gk.touchstone.testcasebak;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.gk.touchstone.taskview.CameraFrontPreviewGet;
import com.gk.touchstone.utils.CameraView;
import com.gk.touchstone.core.TestCase;
import com.gk.touchstone.entity.Task;

public class CameraFrontPicTask extends TestCase{
	//private Camera camera;
	Bundle bundle = null;// 声明一个Bundle对象，用来存储数据
	//private Camera.Parameters parameters = null;
	private int internalTime;
	CameraView view;
	
	public CameraFrontPicTask(Context context, Task task) {
		super(context, task);
		
		initData();
	}
	
	private void initData() {
		timeCount = getIntValue("frontCameraPicTimes");
		internalTime = getIntValue("frontCameraPicInterval");
	}

	@Override
	public void Start() {
		// TODO Auto-generated method stub
		initData();
		
		Intent intent = new Intent();
		intent.putExtra("className", task.getTaskName());
		//intent.putExtra("TestTimes", timeCount);
		intent.putExtra("internalTime", internalTime);
		intent.setClass(context, CameraFrontPreviewGet.class);
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

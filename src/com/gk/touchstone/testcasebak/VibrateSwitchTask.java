package com.gk.touchstone.testcasebak;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;

import com.gk.touchstone.core.TestCase;
import com.gk.touchstone.core.Constants;
import com.gk.touchstone.entity.Task;

public class VibrateSwitchTask extends TestCase{

	private Vibrator vibrator = null;
	private int vibrateIneternalTime;
	private int internalTime;
	
	public VibrateSwitchTask(Context context, Task task) {
		super(context, task);
		// TODO Auto-generated constructor stub
		vibrator = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);

		initData();
	}
	
	private void initData(){
		timeCount = getIntValue("vibrateTimes");
		internalTime = getIntValue("vibrateInterval");
		vibrateIneternalTime = getIntValue("vibrateTimeSpan");
	}
	
	@Override
	public void Start() {
		// TODO Auto-generated method stub
		initData();
		isRunning = true;
		if (mThread == null) {
			mThread = new startVibrate();
			mThread.start();
		}
	}

	@Override
	public void Finish() {
		// TODO Auto-generated method stub
		vibrator.cancel();
		sendBroadcast();
		return;
	}

	@Override
	public void Stop() {
		// TODO Auto-generated method stub
		vibrator.cancel();
		stopTask();
	}
	
	private void vibrateStart(int vibratorInternalTime){
		vibrator.vibrate(vibratorInternalTime*1000);
	}
	
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what){
			case 0:
				try {
					vibrateStart(vibrateIneternalTime);
					writeResult(timeCount, Constants.PASS, "");
				} catch (Exception e) {
					writeResult(timeCount, Constants.FAIL, e.getMessage());
					e.printStackTrace();
				}
				break;
			case 1:
				Finish();
				break;
			}
		}
	};
	
	class startVibrate extends Thread {
		@Override
		public void run() {
			while (true) {
				try {
					Thread.currentThread();
					if (isRunning){
						Message msg = new Message();
						if (timeCount > 0) {
							timeCount--;
							msg.what = 0;
							handler.sendMessage(msg);
							Thread.sleep(1000 * vibrateIneternalTime);
							vibrator.cancel();
						} else {
							msg.what = 1;
							handler.sendMessage(msg);
						}	
						Thread.sleep(1000 * internalTime);
					}
				} catch (Exception e) {
					Log.e("david",e.getMessage());
					e.printStackTrace();
				}
			}
		}
	}
}

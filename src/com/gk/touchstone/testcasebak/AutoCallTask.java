package com.gk.touchstone.testcasebak;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.gk.touchstone.core.TestCase;
import com.gk.touchstone.utils.BroadCast;
import com.gk.touchstone.core.Constants;
import com.gk.touchstone.entity.Task;

public class AutoCallTask extends TestCase {
	private BroadCast bc;
	private int internalTime;
	String callNumber;
	
	public AutoCallTask(Context context, Task task) {
		super(context, task);

		initData();
	}
	
	private void initData(){
		timeCount = getIntValue("autoCallTimes");
		internalTime = getIntValue("autoCallInterval");
		callNumber = getStrValue("autoCallNumber");
	}
	
	@Override
	public void Start() {
		initData();
		
		bc = new BroadCast(context);
		bc.registerPhone();
		
		isRunning = true;
		if (mThread == null) {
			mThread = new startCall();
			mThread.start();
		}
	}

	@Override
	public void Finish() {
		unregisterPhone();
		sendBroadcast();
		return;
	}

	@Override
	public void Stop() {
		unregisterPhone();
		stopTask();
	}

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what){
			case 0:
				try {
					call();
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					writeResult(timeCount, Constants.FAIL, e.getMessage());
					e.printStackTrace();
				}
				try {
					if (BroadCast.phoneStateStr == "Offhook") {
						writeResult(timeCount, Constants.PASS, "");
					}
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

	class startCall extends Thread {
		@Override
		public void run() {
			while (true) {
				try {
					Thread.currentThread();
					if (isRunning){
						if (BroadCast.phoneStateStr == "Idle") {
							Thread.sleep(1000 * internalTime);
							Message msg = new Message();
							if (timeCount > 0) {
								timeCount--;
								msg.what = 0;
								handler.sendMessage(msg);
							} else {
								msg.what = 1;
								handler.sendMessage(msg);
							}
						}
					}
				} catch (Exception e) {
					Log.e("david",e.getMessage());
					e.printStackTrace();
				}
			}
		}
	}
	
	private void call(){
		if (BroadCast.phoneStateStr == "Idle") {
			Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"
					+ String.valueOf(callNumber))); 
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);
		} else {
			return;
		}
	}
	
	public void unregisterPhone() {
		if (bc.phoneReceiver != null) {
			try {
				context.unregisterReceiver(bc.phoneReceiver);
			} catch (IllegalArgumentException e) {
				if (e.getMessage().contains("Receiver not registered")) {
				} else {
					throw e;
				}
			}
		}
		//context.unregisterReceiver(phoneReceiver);
	}
}

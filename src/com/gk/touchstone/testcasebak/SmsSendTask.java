package com.gk.touchstone.testcasebak;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.telephony.SmsManager;
import com.gk.touchstone.core.TestCase;
import com.gk.touchstone.core.Constants;
import com.gk.touchstone.entity.Task;

public class SmsSendTask extends TestCase{
	private String receiveNumber;
	private int internalTime;
	private String sendContent;
	
	public SmsSendTask(Context context, Task task) {
		super(context, task);
		
		initData();
	}
	
	private void initData(){
		receiveNumber = getStrValue("smsSendNumber");
		timeCount = getIntValue("smsSendTimes");
		internalTime = getIntValue("smsSendInterval");
		sendContent = getStrValue("smsSendContent");
	}

	@Override
	public void Start() {
		// TODO Auto-generated method stub
		initData();
		
		isRunning = true;
		if (mThread == null) {
			mThread = new startSendSms();
			mThread.start();
		}
	}

	@Override
	public void Finish() {
		/*if (leftTimes != null){
			leftTimes.setText("剩余测试次数："+String.valueOf(0));
		}*/
		sendBroadcast();
		return;
	}

	@Override
	public void Stop() {
		stopTask();
	}

	public void sendSMS(String phoneNumber,String message){
		//send an SMS message to another device
		SmsManager sms = SmsManager.getDefault();
		//Create the sentIntent parameter
		Intent sentIntent = new Intent("SENT_SMS_ACTION");
		//在Service中调用intent，需要加入FLAG_ACTIVITY_NEW_TASK
		sentIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		PendingIntent sentPI = PendingIntent.getBroadcast(context, 0, sentIntent, 0);
		sms.sendTextMessage(phoneNumber, null, message, sentPI, null);
	}
	
	Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg){
			if (timeCount > 0){
				sendSMS(receiveNumber,sendContent); 
				timeCount--;
				writeResult(timeCount, Constants.PASS,"");
				/*if (leftTimes != null){
					leftTimes.setText("剩余测试次数："+String.valueOf(timeCount));
				}*/
			}else{
				/*if (leftTimes != null 
						&& btnstart != null 
						& btncancel != null 
						&& edtTimeCount != null 
						&& edtInternalTime != null
						&& edtReceiver != null
						&& edtSendContent != null){
					leftTimes.setText("剩余测试次数："+String.valueOf(0));
					btnstart.setEnabled(true);
					btncancel.setEnabled(false);
					edtTimeCount.setEnabled(true);
					edtInternalTime.setEnabled(true);
					edtReceiver.setEnabled(true);
					edtSendContent.setEnabled(true);
				}*/
				Finish();
			}
		}
	};
	
	class startSendSms extends Thread{
		@Override
		public void run() {
			while (true){
				try{
					if (isRunning){
						Thread.currentThread();
						Thread.sleep(1000*internalTime);
						Message msg = new Message();
						handler.sendMessage(msg);
					}
				}catch (Exception e){
					e.printStackTrace();
					writeResult(timeCount, Constants.FAIL,e.getMessage());
					}
				}
			}
		}
}

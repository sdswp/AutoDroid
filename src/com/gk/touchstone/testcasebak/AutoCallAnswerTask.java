package com.gk.touchstone.testcasebak;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;

import com.android.internal.telephony.ITelephony;
import com.gk.touchstone.core.TestCase;
import com.gk.touchstone.utils.BroadCast;
import com.gk.touchstone.core.Constants;
import com.gk.touchstone.entity.Task;
import com.gk.touchstone.utils.PhoneUtils;

public class AutoCallAnswerTask extends TestCase{
	private BroadCast bc;
	private int keepTime;
	private TelephonyManager teleManager;
	private String callNumber;
	private int answerCount = 0;
	
	public AutoCallAnswerTask(Context context, Task task) {
		super(context, task);
		
		initData();
	}

	private void initData(){
		keepTime = getIntValue("answerHookTime");
		callNumber = getStrValue("answerNumnber");
		
		answerCount = 0;
	}
	
	@Override
	public void Start() {
		initData();
		
		bc = new BroadCast(context);
		bc.registerPhone();
		isRunning = true;
		if (mThread == null) {
			mThread = new answerCall();
			mThread.start();
		}
	}

	@Override
	public void Finish() {
		unregisterPhone();
		sendBroadcast();
//		if (leftTimes != null){
//			leftTimes.setText("已接通次数："+String.valueOf(0));
//		}
		answerCount = 0;
		return;
	}

	@Override
	public void Stop() {
		unregisterPhone();
		/*if (leftTimes != null){
			leftTimes.setText("已接通次数："+String.valueOf(0));
		}*/
		answerCount = 0;
		stopTask();
	}
	
	Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg){
			if(BroadCast.phoneStateStr == "Offhook"){
				endCall(context);
				answerCount++;
				writeResult(answerCount, Constants.PASS, "");
//				if (leftTimes != null){
//					leftTimes.setText("已接通次数："+String.valueOf(answerCount));
//				}
			}else{
				return;
			}
		}
	};
	
	class answerCall extends Thread{
		@Override
		public void run() {
			while (true){
				try{
					Thread.currentThread();
					if (isRunning){
						if (BroadCast.phoneStateStr == "Ring"){
							if (BroadCast.phoneNumber.equals(callNumber)){
								answerRingingCalls(context);
								Thread.sleep(keepTime * 1000);
								Message msg = new Message();
								handler.sendMessage(msg);
							}
						}
					}
				}catch (Exception e){
					writeResult(answerCount, Constants.FAIL, e.getMessage());
					e.printStackTrace();
					}
				}
			}
		}
	
	public void endCall(Context context){
		teleManager = (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);
		try {
			ITelephony iTelephony = PhoneUtils.getITelephony(teleManager);
			// end call
			iTelephony.endCall();
		} catch (IllegalArgumentException e) {
			Log.e("david", "IllegalArgumentException: " + e.getMessage());
			writeResult(answerCount, Constants.FAIL, e.getMessage());
		} catch (IllegalAccessException e) {
			Log.e("david", "IllegalAccessException: " + e.getMessage());
			writeResult(answerCount, Constants.FAIL, e.getMessage());
		} catch (InvocationTargetException e) {
			Log.e("david", "InvocationTargetException: " + e.getMessage());
			writeResult(answerCount, Constants.FAIL, e.getMessage());
		} catch (RemoteException e) {
			Log.e("david", "RemoteException: " + e.getMessage());
			writeResult(answerCount, Constants.FAIL, e.getMessage());
		} catch (Exception e) {
			Log.e("david", "Exception: " + e.getMessage());
			e.printStackTrace();
			writeResult(answerCount, Constants.FAIL, e.getMessage());
		}
	}
	
	public void answerRingingCalls(Context context) {
		try {
	           if (android.os.Build.VERSION.SDK_INT >= 16) {
	                Intent meidaButtonIntent = new Intent(
	                    Intent.ACTION_MEDIA_BUTTON);
	                KeyEvent keyEvent = new KeyEvent(KeyEvent.ACTION_UP,
	                    KeyEvent.KEYCODE_HEADSETHOOK);
	                meidaButtonIntent.putExtra(Intent.EXTRA_KEY_EVENT, keyEvent);
	                context.sendOrderedBroadcast(meidaButtonIntent,
	                    "android.permission.CALL_PRIVILEGED");
	            } else {
	                Intent localIntent1 = new Intent(Intent.ACTION_HEADSET_PLUG);	
	                localIntent1.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
	                localIntent1.putExtra("state", 1);
	                localIntent1.putExtra("microphone", 1);
	                localIntent1.putExtra("name", "Headset");
	                context.sendOrderedBroadcast(localIntent1,
	                    "android.permission.CALL_PRIVILEGED");
	                Intent localIntent2 = new Intent(Intent.ACTION_MEDIA_BUTTON);
	                KeyEvent localKeyEvent1 = new KeyEvent(KeyEvent.ACTION_DOWN,
	                    KeyEvent.KEYCODE_HEADSETHOOK);
	                localIntent2.putExtra("android.intent.extra.KEY_EVENT",
	                    localKeyEvent1);
	                context.sendOrderedBroadcast(localIntent2,
	                    "android.permission.CALL_PRIVILEGED");
	                Intent localIntent3 = new Intent(Intent.ACTION_MEDIA_BUTTON);
	                KeyEvent localKeyEvent2 = new KeyEvent(KeyEvent.ACTION_UP,
	                    KeyEvent.KEYCODE_HEADSETHOOK);
	                localIntent3.putExtra("android.intent.extra.KEY_EVENT",
	                    localKeyEvent2);
	                context.sendOrderedBroadcast(localIntent3,
	                    "android.permission.CALL_PRIVILEGED");
	                Intent localIntent4 = new Intent(Intent.ACTION_HEADSET_PLUG);
	                localIntent4.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
	                localIntent4.putExtra("state", 0);
	                localIntent4.putExtra("microphone", 1);
	                localIntent4.putExtra("name", "Headset");
	                context.sendOrderedBroadcast(localIntent4,
	                    "android.permission.CALL_PRIVILEGED");
	            }

		} catch (Exception e) {
			writeResult(answerCount, Constants.FAIL, e.getMessage());
			e.printStackTrace();
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

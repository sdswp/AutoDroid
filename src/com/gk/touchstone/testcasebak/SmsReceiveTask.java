package com.gk.touchstone.testcasebak;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.telephony.SmsMessage;
import com.gk.touchstone.taskview.SmsReceive;
import com.gk.touchstone.core.TestCase;
import com.gk.touchstone.core.Constants;
import com.gk.touchstone.entity.Task;

public class SmsReceiveTask extends TestCase{
	private SMSBroadcastReceiver smsreceiver = new SMSBroadcastReceiver();
	private String senderNumber;
	//private TextView leftTimes;
	/*private Button btnStart,btnCancel;
	private EditText sendNumber;*/
	private int receiveTimes = 0;

	public SmsReceiveTask(Context context, Task task) {
		super(context, task);
		
		initData();
	}
	
	private void initData(){
		senderNumber = getStrValue("smsReceiveNumber");
		receiveTimes = 0;
	}
	
	@Override
	public void Start() {
		initData();
		
		isRunning=true;
		registerSms();
		Intent intent = new Intent();
		intent.setAction("android.provider.Telephony.SMS_RECEIVED");
		context.sendBroadcast(intent);
		
	}

	@Override
	public void Finish() {
		/*if (leftTimes != null){
			leftTimes.setText("已接收短信条数："+String.valueOf(0));
		}*/
		receiveTimes = 0;
		unregisterSms();
		sendBroadcast();
		return;
	}

	@Override
	public void Stop() {
		/*if (leftTimes != null){
			leftTimes.setText("已接收短信条数："+String.valueOf(0));
		}*/
		receiveTimes = 0;
		unregisterSms();
		stopTask();
	}
	
	public void registerSms(){
		context.registerReceiver(smsreceiver, new IntentFilter("android.provider.Telephony.SMS_RECEIVED"));
	}
	
	public class SMSBroadcastReceiver extends BroadcastReceiver {  
		private static final String strACT = "android.provider.Telephony.SMS_RECEIVED";
	    @Override  
	    public void onReceive(Context context, Intent intent) {
	    	if (intent.getAction().equals(strACT)) {
	    		StringBuilder sb = new StringBuilder();
	    		Bundle bundle = intent.getExtras();
	    		if (bundle != null) {
	    			Object[] pdus = (Object[]) bundle.get("pdus");
	    			SmsMessage[] msg = new SmsMessage[pdus.length];
	    			/*for (int i = 0; i < pdus.length; i++) {
	    				msg[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
	    					//to handle message处理接收到的短信内容
	    				if (msg[i].getMessageBody().contains(messagebody)){
	    					//to do 
	    					System.out.println("get message content and to do ");
	    				}
	    			}*/
	    		for (SmsMessage currMsg : msg) {
	    			//可过滤接收者的号码
	    			String sender = currMsg.getOriginatingAddress();
	    			if (senderNumber.equals(sender)) {
	    				receiveTimes++;
	    				writeResult(receiveTimes, Constants.PASS,"");
//	    				if (leftTimes != null){
//	    					leftTimes.setText("已接收短信条数："+String.valueOf(receiveTimes));
//	    				}
	    			}
	    			sb.append("From:");
	    			sb.append(currMsg.getDisplayOriginatingAddress());
	    			sb.append("\nMessage:");
	    			sb.append(currMsg.getDisplayMessageBody());
	    			}
	    		}
	    		Intent i = new Intent(context, SmsReceive.class);
	    		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    		context.startActivity(i);
	    	}
	    }
	}
	
	public void unregisterSms(){
	 	 context.unregisterReceiver(smsreceiver);
	}
	
}

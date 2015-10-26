package com.gk.touchstone.testcasebak;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.gk.touchstone.GkApplication;
import com.gk.touchstone.R;
import com.gk.touchstone.core.Constants;
import com.gk.touchstone.utils.MobileNetwork;
import com.gk.touchstone.core.TestCase;
import com.gk.touchstone.entity.Task;

public class MobileNetworkUsingTask extends TestCase{
	private int TimeOut;
	private HttpPost httpRequest=null;
	private HttpResponse httpResponse=null;
	private MobileNetwork mn;
	private WifiManager wifiManager;
	private GkApplication app;
	/*private Button btnstart,btncancel;
	private TextView leftTimes;
	private EditText edtTimeCount,edtInternalTime;*/
	
	public MobileNetworkUsingTask(Context context, Task task) {
		super(context, task);

		wifiManager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);

		app = (GkApplication) context.getApplicationContext();
		
		mn = new MobileNetwork(context);
		initData();
	}
	
	private void initData(){
		timeCount = getIntValue("mobileUsingCount");
		TimeOut = getIntValue("mobileUsingOutTime");
		if (wifiManager.isWifiEnabled()){
			wifiManager.setWifiEnabled(false);
		}
		if(!listenState()){
			changeMobilenetState();
		}		
	}
	
	@Override
	public void Start() {
		// TODO Auto-generated method stub
		//if edtText is not null ,need to send parameter here
		initData();
		
		isRunning=true;
		if (mThread == null){
			mThread = new loadingMobileNetwork();
			mThread.start();
		}
	}

	@Override
	public void Finish() {
		if(listenState()){
			changeMobilenetState();
		}
		sendBroadcast();
		/*if(leftTimes != null){
			leftTimes.setText("剩余测试次数："+String.valueOf(0));
		}*/
		return;
	}

	@Override
	public void Stop() {
		if(listenState()){
			changeMobilenetState();
		}
		stopTask();
	}
	
	Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
			case 0:
				String url = context.getResources().getString(R.string.networkUsing);
				timeCount--;
				if (loadUrlapi(url)){
					writeResult(timeCount, Constants.PASS, "");
				}
				else{
					writeResult(timeCount,Constants.FAIL,app.getMobileUsingError());
				}
				
				/*if (leftTimes != null){
					leftTimes.setText("剩余测试次数："+String.valueOf(timeCount));
				}*/
				break;
			case 1:
				/*if (btnstart != null && btncancel != null && leftTimes != null && edtTimeCount != null && edtInternalTime != null){
					leftTimes.setText("剩余测试次数："+String.valueOf(0));
					btnstart.setEnabled(true);
					btncancel.setEnabled(false);
					edtTimeCount.setEnabled(true);
					edtInternalTime.setEnabled(true);
					}*/
				Finish();
				break;
			}
		}
	};
	
	private class loadingMobileNetwork extends Thread{
	@Override
	public void run() {
		while (true){
			try{
				Thread.currentThread();
				Thread.sleep(1000*TimeOut);
				Message msg = new Message();
				if (isRunning){
					if (timeCount > 0){
						msg.what = 0;
						handler.sendMessage(msg);
					}
					else{
						msg.what = 1;
						handler.sendMessage(msg);
					}
				}
				
				}catch (Exception e){
					e.printStackTrace();
				}
			}
		}
	}
	
	//get方法获取
/*	private boolean loadUrl(String url){
		HttpGet httpRequest = new HttpGet(url);
		try {

			HttpResponse httpResponse = new DefaultHttpClient()
					.execute(httpRequest);

			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				String strResult = EntityUtils.toString(httpResponse.getEntity());
				Log.d("david","Pass");
				return true;
				String strResult = EntityUtils.toString(httpResponse
						.getEntity());
			} else {
				Log.d("david","Fail");
				return false;
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			Log.d("david","Fail");
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			Log.d("david","Fail");
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			Log.d("david","Fail");
			return false;
		}
	}*/	
	
	//post方法获取
	public boolean loadUrlapi(String url){
		if(httpRequest==null){
			httpRequest = new HttpPost(url);
		}
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("str", "post string"));
		
		try {
			httpRequest.setEntity(new UrlEncodedFormEntity(params,HTTP.UTF_8));
			if (httpResponse==null){
				httpResponse = new DefaultHttpClient().execute(httpRequest);
			}
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				//使用2G网络时，运行总是出现ANR,将httpResponse和httpRequest进行释放后,赋值为null，就不会出现ANR了
				//String strResult = EntityUtils.toString(httpResponse.getEntity());
				return true;
			} else {
				app.setMobileUsingError("Error Code is "+String.valueOf(httpResponse.getStatusLine().getStatusCode()));
				return false;
			}
		}  catch (Exception e) {
			e.printStackTrace();
			app.setMobileUsingError(e.getMessage());
			Log.d("david","ExceptionFail");
			return false;
		}
	}
	
	//读取移动网络的状态
	private boolean listenState() {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		boolean state = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnected();
		/*if (state) {
			String thisOut = "mobilenet state on!";
		} else {
			String thisOut = "mobilenet state off!";
		}*/
		return state;
	}

	//更改移动网络状态
	private void changeMobilenetState() {
		
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		boolean mobileEnabled = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnected();
		if (mobileEnabled) {
			// turn off mobilenet
			mn.toggleMobileData(context, false);
		} else {
			// turn on mobilenet
			mn.toggleMobileData(context, true);
		}
	}
}

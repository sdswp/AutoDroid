package com.gk.touchstone.activity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import com.gk.touchstone.R;
import com.gk.touchstone.entity.ChartInfo;
import com.gk.touchstone.network.SocketClient;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.provider.CalendarContract.Instances;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class LogonActivity extends Activity {
	// private EditText edtIp;
	private EditText edtPort;

	private Button btnLogon;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.logon);
		
		if(android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode. setThreadPolicy(policy);
        }

		this.initViews();
	}

	private void initViews() {
		this.btnLogon = (Button) this.findViewById(R.id.btnLogon);
		this.btnLogon.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ChartInfo chartInfo = ChartInfo.getInstance();
				String edtip = getResources().getString(R.string.server_addr);
				chartInfo.setIp(edtip);
				chartInfo.setPort(1234);
				SocketClient proxy = SocketClient.getInstance();

				if (proxy == null) {
					showDialog("没有网络");
					setWireless();
					return;
				}

				proxy.putHandler("Logon", new Handler() {
					@Override
					public void handleMessage(Message msg) {

						SocketClient proxy = SocketClient.getInstance();
						proxy.removeHandler("Logon");
						Log.d("initSocket", "handleMessage");
						if (msg == null || msg.obj == null) {
							return;
						}

						JSONObject json = (JSONObject) msg.obj;
						try {
							String userName = json.getString("UserName");
							Log.d("initSocket", "userName:" + userName);
							ChartInfo.getInstance().setUserName(userName);
							Intent itt = new Intent();
							itt.setClass(LogonActivity.this,
									SocketActivity.class);
							LogonActivity.this.startActivity(itt);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});

				TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
				String deviceid = tm.getDeviceId();
				proxy.logon(deviceid);
			}
		});
	}

	private void setWireless() {
		Intent mIntent = new Intent("/");
		ComponentName comp = new ComponentName("com.android.settings",
				"com.android.settings.WirelessSettings");
		mIntent.setComponent(comp);
		mIntent.setAction("android.intent.action.VIEW");
		startActivityForResult(mIntent, 0);
	}

	private void showDialog(String mess) {
		new AlertDialog.Builder(this).setTitle("信息").setMessage(mess)
				.setNegativeButton("确定", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
					}
				}).show();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {

			AlertDialog alertDialog = new AlertDialog.Builder(
					LogonActivity.this)
					.setTitle("退出程序")
					.setMessage("是否退出程序")
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface dialog,
										int which) {
									LogonActivity.this.finish();
								}

							}).setNegativeButton("取消",

					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							return;
						}
					}).create(); // 创建对话框

			alertDialog.show(); // 显示对话框

			return false;

		}

		return false;
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		SocketClient proxy = SocketClient.getInstance();
		if (proxy != null) {
			proxy.close();
		}
		
		
		
	}
	

}

package com.gk.touchstone.activity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.gk.touchstone.GkApplication;
import com.gk.touchstone.R;
import com.gk.touchstone.activity.HomeActivity.OnBackListener;
import com.gk.touchstone.core.MyBase;
import com.gk.touchstone.core.TestCase;
import com.gk.touchstone.db.DBManager;
import com.gk.touchstone.entity.Task;
import com.gk.touchstone.utils.Utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.KeyguardManager;
import android.app.AlertDialog.Builder;
import android.app.KeyguardManager.KeyguardLock;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TabHost.TabSpec;

import dalvik.system.BaseDexClassLoader;
import dalvik.system.DexClassLoader;

public class MainActivity extends FragmentActivity implements OnBackListener {
	private FragmentTabHost mTabHost;
	private RadioGroup mTabRg;

	private int currentV = 0, newV = 0;
	private JSONObject jo_v;
	private String newVContent = "";

	private ProgressBar pb;
	private TextView tv;
	public static int loading_process;
	// private Intent servIntent;
	private GkApplication app;
	private Utils us;
	private DBManager dbm;

	// PhoneReference.class,选机参考
	private final Class[] fragments = { HomeActivity.class,
			StabilityMain.class, PerformanceMain.class, Settings.class };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 设置窗体始终点亮
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		setContentView(R.layout.activity_main);
		initView();
		loadCase();
		// initUpdate();
		// startService(new Intent("com.gk.touchstone.SettingsService"));
	}

	private void loadCase() {
		final File optimizedDexOutputPath = new File(Environment
				.getExternalStorageDirectory().toString()
				+ File.separator
				+ "testss.jar");

		BaseDexClassLoader cl = new BaseDexClassLoader(Environment
				.getExternalStorageDirectory().toString(),
				optimizedDexOutputPath,
				optimizedDexOutputPath.getAbsolutePath(), getClassLoader());

		try {
			Class<?> clazz = cl
					.loadClass("com.gk.touchstone.testcase.LoadTestTask");

//			Constructor<?> constructor = clazz.getConstructor(new Class[] {
//					Context.class, Task.class });
//			TaskBase taskbase = (TaskBase) constructor
//					.newInstance(new Object[] { this, task });
//			taskbase.Start();

			MyBase tb = (MyBase) clazz.newInstance();
			tb.Start();

		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	private void initView() {
		app = (GkApplication) getApplication();
		us = new Utils(this);

		// // 启动任务监听Servie
		// servIntent = new Intent(this, TaskService.class);
		// startService(servIntent);

		mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
		mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);

		int count = fragments.length;
		for (int i = 0; i < count; i++) {
			// 设置Tab的按钮图标和文字
			TabSpec tabSpec = mTabHost.newTabSpec(i + "").setIndicator(i + "");

			mTabHost.addTab(tabSpec, fragments[i], null);
		}

		mTabRg = (RadioGroup) findViewById(R.id.tab_rg_menu);
		mTabRg.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.tab_rb_1:
					mTabHost.setCurrentTab(0);
					break;
				case R.id.tab_rb_2:
					if (!isTaskRunning()) {
						mTabHost.setCurrentTab(1);
					}
					break;
				case R.id.tab_rb_3:
					if (!isTaskRunning()) {
						mTabHost.setCurrentTab(2);
					}
					break;
				case R.id.tab_rb_4:
					if (!isTaskRunning()) {
						mTabHost.setCurrentTab(3);
					}
					break;

				default:
					break;
				}
			}
		});

		mTabHost.setCurrentTab(0);
	}

	private boolean isTaskRunning() {
		if (app.getTaskBases() != null && app.getTaskBases().size() > 0) {
			mTabHost.setCurrentTab(0);
			mTabRg.check(R.id.tab_rb_1);

			int pid = app.getTaskBases().get(0).planid;
			dbm = new DBManager(this);
			String planName = dbm.queryPlan(pid).getPlanName();
			dbm.closedb();

			us.myToast(planName, R.string.plan_error1);
			return true;
		}
		return false;
	}

	private void initUpdate() {
		loading_process = 0;
		if (isConnect(this)) {
			currentV = getVerCode(this, "com.gk.touchstone");
			new Thread() {
				public void run() {
					String serv = "http://"
							+ getResources().getString(R.string.server_addr)
									.trim();
					String port = getResources()
							.getString(R.string.server_port).trim();
					if (!port.equals("")) {
						port = ":" + port + "/";
					}

					String apkdir = getResources().getString(R.string.apkdir)
							.trim();
					jo_v = getJsonObject(serv + port + apkdir
							+ "/updatets.html");
					if (jo_v != null && jo_v.has("version"))
						try {
							newV = jo_v.getInt("version");
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					if (newV > currentV) {
						Message msg = BroadcastHandler.obtainMessage();
						BroadcastHandler.sendMessage(msg);
					}
				}
			}.start();
		}
	}

	@Override
	public void onBackPressed() {
		exitDialog();

	}

	@Override
	public void backEvent() {
		Toast.makeText(this, "back", Toast.LENGTH_SHORT).show();
	}

	private void exitDialog() {
		AlertDialog.Builder dlg = new AlertDialog.Builder(this);
		dlg.setIcon(android.R.drawable.ic_dialog_info);
		dlg.setTitle("退出提示");
		String nameStr = getResources().getString(R.string.app_name);
		dlg.setMessage("确定要退出" + nameStr + "吗？");

		dlg.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// stopService(servIntent);
				// stopService(new Intent(
				// "com.gk.touchstone.service.Settings_SERVICE"));
				MainActivity.this.finish();
			}
		});
		DisplayMetrics metric = new DisplayMetrics();

		getWindowManager().getDefaultDisplay().getMetrics(metric);
		int height = metric.heightPixels;
		if (height < 250) {
			// setNeutralButton
			dlg.setNegativeButton("HOME",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							Intent intent = new Intent(Intent.ACTION_MAIN);
							intent.addCategory(Intent.CATEGORY_HOME);
							intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
									| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
							startActivity(intent);
						}
					});
		} else {
			dlg.setNegativeButton("取消", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub

				}
			});
		}
		dlg.create().show();
	}

	/*
	 * 升级部分
	 */
	private Handler BroadcastHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (jo_v.has("content")) {
				JSONArray items;
				try {
					items = jo_v.getJSONArray("content");
					for (int i = 0; i < items.length(); i++) {
						JSONObject d = items.getJSONObject(i);
						newVContent = newVContent + (i + 1) + "."
								+ d.getString("text") + "\n";
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			Dialog dialog = new AlertDialog.Builder(MainActivity.this)
					.setTitle("新版本更新：")
					.setMessage(newVContent)
					.setPositiveButton("更新",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									Beginning();
									dialog.dismiss();
								}
							})
					.setNegativeButton("取消",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									dialog.dismiss();
								}
							}).create();
			dialog.show();
		}
	};

	public void Beginning() {
		LinearLayout ll = (LinearLayout) LayoutInflater.from(MainActivity.this)
				.inflate(R.layout.layout_loadapk, null);
		pb = (ProgressBar) ll.findViewById(R.id.down_pb);
		tv = (TextView) ll.findViewById(R.id.tv);
		Builder builder = new Builder(MainActivity.this);
		builder.setView(ll);
		builder.setTitle("版本更新进度");
		// builder.setNegativeButton("后台下载",
		// new DialogInterface.OnClickListener() {
		// @Override
		// public void onClick(DialogInterface dialog, int which) {
		// Intent intent=new Intent(MainActivity.this, VersionService.class);
		// startService(intent);
		// dialog.dismiss();
		// }
		// });

		builder.show();
		new Thread() {
			public void run() {
				String serv = getResources().getString(R.string.server_addr)
						.trim();
				String apkdir = getResources().getString(R.string.apkdir)
						.trim();
				loadFile(serv + apkdir + "TouchStone.apk");
			}
		}.start();
	}

	public void loadFile(String url) {
		HttpClient client = new DefaultHttpClient();
		HttpGet get = new HttpGet(url);
		HttpResponse response;
		try {
			response = client.execute(get);

			HttpEntity entity = response.getEntity();
			float length = entity.getContentLength();

			InputStream is = entity.getContent();
			FileOutputStream fileOutputStream = null;
			if (is != null) {
				File file = new File(Environment.getExternalStorageDirectory(),
						"TouchStone.apk");
				fileOutputStream = new FileOutputStream(file);
				byte[] buf = new byte[1024];
				int ch = -1;
				float count = 0;
				while ((ch = is.read(buf)) != -1) {
					fileOutputStream.write(buf, 0, ch);
					count += ch;
					sendMsg(1, (int) (count * 100 / length));
				}
			}
			sendMsg(2, 0);
			fileOutputStream.flush();
			if (fileOutputStream != null) {
				fileOutputStream.close();
			}
		} catch (Exception e) {
			sendMsg(-1, 0);
		}
	}

	private void sendMsg(int flag, int c) {
		Message msg = new Message();
		msg.what = flag;
		msg.arg1 = c;
		handler.sendMessage(msg);
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {// 定义一个Handler，用于处理下载线程与UI间通讯
			if (!Thread.currentThread().isInterrupted()) {
				switch (msg.what) {
				case 1:
					pb.setProgress(msg.arg1);
					loading_process = msg.arg1;
					tv.setText("已加载：" + loading_process + "%");
					break;
				case 2:
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.setDataAndType(Uri.fromFile(new File(Environment
							.getExternalStorageDirectory(), "TouchStone.apk")),
							"application/vnd.android.package-archive");
					startActivity(intent);
					break;
				case -1:
					String error = msg.getData().getString("error");
					Toast.makeText(MainActivity.this, error, 1).show();
					break;
				}
			}
			super.handleMessage(msg);
		}
	};

	public JSONObject getJsonObject(String Url) {
		HttpClient client = new DefaultHttpClient();
		StringBuilder sb = new StringBuilder();
		String js = null;
		JSONObject son = null;
		HttpGet myget = new HttpGet(Url);
		try {
			HttpParams params = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(params, 8000);
			HttpResponse response = client.execute(myget);
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent()));
			for (String s = reader.readLine(); s != null; s = reader.readLine()) {
				sb.append(s);
			}
			js = sb.toString();
			son = new JSONObject(js);
		} catch (Exception e) {
			return null;
		}
		return son;
	}

	public boolean isConnect(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity != null) {
			NetworkInfo info = connectivity.getActiveNetworkInfo();
			if (info != null && info.isConnected()) {
				if (info.getState() == NetworkInfo.State.CONNECTED) {
					return true;
				}
			}
		}
		return false;
	}

	public int getVerCode(Context _context, String _package) {
		int verCode = -1;
		try {
			verCode = _context.getPackageManager().getPackageInfo(_package, 0).versionCode;
		} catch (NameNotFoundException e) {
		}
		return verCode;
	}

}

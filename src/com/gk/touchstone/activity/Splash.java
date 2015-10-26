package com.gk.touchstone.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.gk.touchstone.R;
import com.gk.touchstone.db.DBManager;
import com.gk.touchstone.core.Constants;
import com.gk.touchstone.entity.Module;
import com.gk.touchstone.entity.ModuleJson;
import com.gk.touchstone.entity.Case;
import com.gk.touchstone.entity.TestCaseJson;
import com.gk.touchstone.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.view.WindowManager;

public class Splash extends Activity {
	private final int SPLASH_DISPLAY_LENGHT = 2000; // 延迟三秒
	private DBManager dbm;
	private Utils us;
	private boolean isSaved = false;
	private SharedPreferences sp;
	private Editor editor;
	private Gson gson;
	private WakeLock mWakelock;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);

		// 设置窗体始终点亮
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		us = new Utils(this);
		sp = getSharedPreferences(Constants.PREFS_TESTCASE_MODULE, MODE_PRIVATE);

		isSaved = sp.getBoolean(Constants.KEY_SAVE_DATA, false);

		if (!isSaved) {
			String APJson = us.getRawFile(R.raw.aplist);
			String durl = us.getRawFile(R.raw.download_url);
			if (addModuleCase()
					&& us.writeDataFile("aplist.json", APJson,
							Context.MODE_PRIVATE)
					&& us.writeDataFile("downloadurl.json", durl,
							Context.MODE_PRIVATE)) {
				editor = sp.edit();
				editor.putBoolean("SaveData", true);
				editor.commit();
				isSaved = true;
			}
		}

		us.createFolder();

		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				File file = new File(us.getAppSDCardPath());
				if (file.exists() && isSaved) {
					Intent intent = new Intent(Splash.this, MainActivity.class);
					startActivity(intent);
					Splash.this.finish();
				}
			}

		}, SPLASH_DISPLAY_LENGHT);

	}

	private boolean addModuleCase() {
		String moduleJson = us.getRawFile(R.raw.module);
		String testcaseJson = us.getRawFile(R.raw.testcase);

		gson = new Gson();
		dbm = new DBManager(this);

		List<ModuleJson> modules = gson.fromJson(moduleJson.trim(),
				new TypeToken<List<ModuleJson>>() {
				}.getType());

		// 重新排序
		Collections.sort(modules, new Comparator<ModuleJson>() {
			public int compare(ModuleJson arg0, ModuleJson arg1) {
				return arg0.getName().compareTo(arg1.getName());
			}
		});

		List<TestCaseJson> testcases = gson.fromJson(testcaseJson.trim(),
				new TypeToken<List<TestCaseJson>>() {
				}.getType());

		List<Case> testCaseList = new ArrayList<Case>();
		// List<Stability> staList = new ArrayList<Stability>();
		try {
			for (ModuleJson mj : modules) {
				Module module = new Module();
				module.setName(mj.getName());
				module.setDisplayName(mj.getDisplayName());
				module.setCreateTime(us.getCurrentTime());
				module.setUpdateTime(us.getCurrentTime());
				module.setDesc(mj.getDesc());
				int mid = dbm.insertModule(module);
				String mjid = mj.getId();

				for (TestCaseJson tcj : testcases) {
					if (mjid.equals(tcj.getModuleId())) {
						Case testcase = new Case();
						testcase.setServerId(tcj.getTestCaseId());
						testcase.setName(tcj.getTestCaseName());
						testcase.setDisplayName(tcj.getDisplayName());
						testcase.setModuleId(mid);
						testcase.setValue(tcj.getValue());
						testcase.setAloneUse(tcj.getAloneUse());

						// SQLite中没有定义布尔类型，而是以Integer 存储布尔值，0(false), 1(true)
						if (tcj.getIsJoin()) {
							testcase.setIsJoin(1);
						} else {
							testcase.setIsJoin(0);
						}

						testcase.setCount(tcj.getCount());
						testcase.setAction(tcj.getAction());
						testcase.setEntity(tcj.getEntity());
						testcase.setDesc(tcj.getDesc());
						testcase.setCreateTime(us.getCurrentTime());
						testcase.setUpdateTime(us.getCurrentTime());
						testCaseList.add(testcase);
					}

				}
			}
			dbm.insertTestCases(testCaseList);
			dbm.closedb();
			
			

		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
//	public boolean writeJAR(String content) {
//		try {
//			FileOutputStream fos = this.openFileOutput("testss.jar",
//					Context.MODE_WORLD_READABLE);
//
//			fos.write(content.getBytes());
//			fos.close();
//		} catch (Exception e) {
//			e.printStackTrace();
//			return false;
//		}
//		return true;
//	}

	@Override
	protected void onResume() {
		super.onResume();

		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		mWakelock = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP
				| PowerManager.SCREEN_DIM_WAKE_LOCK, "SimpleTimer");
		mWakelock.acquire();

		KeyguardManager km = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
		km.newKeyguardLock("Tag For Debug").disableKeyguard();

	}

	@Override
	protected void onPause() {
		super.onPause();
		mWakelock.release();
	}


}
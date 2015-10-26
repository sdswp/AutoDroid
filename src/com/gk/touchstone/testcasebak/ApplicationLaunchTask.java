package com.gk.touchstone.testcasebak;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Handler;
import android.os.Message;

import com.gk.touchstone.core.TestCase;
import com.gk.touchstone.entity.Task;

public class ApplicationLaunchTask extends TestCase{
	
	public ApplicationLaunchTask(Context context, Task task) {
		super(context, task);
		// TODO Auto-generated constructor stub
	}

	private static boolean isRunning = false;
	private PackageManager pm;
	
	@Override
	public void Start() {
		// TODO Auto-generated method stub
		isRunning = true;
		new startAppLaunch().start();
	}

	@Override
	public void Stop() {
		// TODO Auto-generated method stub
		stopTask();
	}

	@Override
	public void Finish() {
		// TODO Auto-generated method stub
		sendBroadcast();
		return;
	}
	
	private List<AppInfo> queryFilterAppInfo() {
		   pm = context.getPackageManager();
		   List<ApplicationInfo> listAppcations = pm
					.getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
			Collections.sort(listAppcations,
					new ApplicationInfo.DisplayNameComparator(pm));// 排序
			List<AppInfo> appInfos = new ArrayList<AppInfo>(); // 保存过滤查到的AppInfo
			for(ApplicationInfo app: listAppcations){
				if ((app.flags & ApplicationInfo.FLAG_SYSTEM) <= 0){
					appInfos.add(getAppInfo(app));
				}
			}
			
			return appInfos;
	}
	   
	// 构造一个AppInfo对象 ，并赋值
	private AppInfo getAppInfo(ApplicationInfo app) {
		AppInfo appInfo = new AppInfo();
		appInfo.setPkgName(app.packageName);
		return appInfo;
	}
		
	public String getLaunchActivity(String packagName){
		String activityName = "";
		Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
		mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		List<ResolveInfo> resolveInfos = pm
				.queryIntentActivities(mainIntent, PackageManager.GET_UNINSTALLED_PACKAGES);
		Collections.sort(resolveInfos,
				new ResolveInfo.DisplayNameComparator(pm));
			
		for (ResolveInfo reInfo : resolveInfos){
			String thisPackageNmae = reInfo.activityInfo.packageName;
			if (thisPackageNmae.equals(packagName)){ 
				activityName = reInfo.activityInfo.name;
				}
			}
			return activityName;
		}
	
	private class AppInfo {
		//private String appLabel;    //应用程序标签
		//private Drawable appIcon;  //应用程序图像
		//private Intent intent ;     //启动应用程序的Intent ，一般是Action为Main和Category为Launcher的Activity
		private String pkgName ;    //应用程序所对应的包名
			
		public AppInfo(){}
			
		/*public String getAppLabel() {
			return appLabel;
		}
		public void setAppLabel(String appName) {
			this.appLabel = appName;
		}
		public Drawable getAppIcon() {
			return appIcon;
		}
		public void setAppIcon(Drawable appIcon) {
			this.appIcon = appIcon;
		}
		public Intent getIntent() {
			return intent;
		}
		public void setIntent(Intent intent) {
			this.intent = intent;
		}*/
		public String getPkgName(){
			return pkgName ;
		}
		public void setPkgName(String pkgName){
			this.pkgName=pkgName ;
		}
	}
	
	int i = 0;
	Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			List<AppInfo> pkgList = queryFilterAppInfo();
			if (i < pkgList.size()){
				String pkgName = pkgList.get(i).getPkgName();
				String activityName = getLaunchActivity(pkgName);
				Intent launchIntent = new Intent();
				launchIntent.setComponent(new ComponentName(pkgName,
					activityName));
				context.startActivity(launchIntent);
				i++;
			}else{
				isRunning = false;
			}
		}
	};
	
	class startAppLaunch extends Thread{
	@Override
	public void run() {
		try{
			while (true){
				if (isRunning){
					Thread.currentThread();
					Thread.sleep(5000);
					Message msg = new Message();
					handler.sendMessage(msg);
					}
				}
			}catch (Exception e){
				e.printStackTrace();
			}
		}
	}
		
}

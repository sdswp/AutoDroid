package com.gk.touchstone.utils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.content.Context;
import android.net.ConnectivityManager;

public class MobileNetwork {

	private Context context;
	private ConnectivityManager mobileNetwork;
	
	public MobileNetwork(Context context){
		this.context = context;
		mobileNetwork = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
				
	}
	
	public void toggleMobileData(Context context, boolean enabled) {
		Class<?> conMgrClass = null; // ConnectivityManager类
		Field iConMgrField = null; // ConnectivityManager类中的字段     
		Object iConMgr = null; // IConnectivityManager类的引用     
		Class<?> iConMgrClass = null; // IConnectivityManager类     
		Method setMobileDataEnabledMethod = null; // setMobileDataEnabled方法 
		try {
			// 取得ConnectivityManager类      
			conMgrClass = Class.forName(mobileNetwork.getClass().getName());     
			// 取得ConnectivityManager类中的对象mService      
			iConMgrField = conMgrClass.getDeclaredField("mService");     
			// 设置mService可访问     
			iConMgrField.setAccessible(true);
			// 取得mService的实例化类IConnectivityManager
			iConMgr = iConMgrField.get(mobileNetwork);
			// 取得IConnectivityManager类      
			iConMgrClass = Class.forName(iConMgr.getClass().getName());     
			// 取得IConnectivityManager类中的setMobileDataEnabled(boolean)方法      
			setMobileDataEnabledMethod = iConMgrClass.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);     
			// 设置setMobileDataEnabled方法可访问
			setMobileDataEnabledMethod.setAccessible(true);
			// 调用setMobileDataEnabled方法
			setMobileDataEnabledMethod.invoke(iConMgr, enabled);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (NoSuchFieldException e) {     
				e.printStackTrace();
			} catch (SecurityException e) {     
				e.printStackTrace();
			} catch (NoSuchMethodException e) {     
				e.printStackTrace();
			} catch (IllegalArgumentException e) {     
				e.printStackTrace();
			} catch (IllegalAccessException e) {     
				e.printStackTrace();
			} catch (InvocationTargetException e) {     
				e.printStackTrace();
			}
		}
	
	public void UsingMobilenetwork(String address){
		String cmd = "am start -a android.intent.action.VIEW -d http://"+address;
		Runtime runtime = Runtime.getRuntime();
		try {
			Process proc = runtime.exec(cmd);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

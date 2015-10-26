package com.gk.touchstone.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.MathContext;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.gk.touchstone.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;


public class UpdateManager {
	/* 下载中 */
	private static final int DOWNLOAD = 1;
	/* 下载结束 */
	private static final int DOWNLOAD_FINISH = 2;
	/* 保存解析的XML信息 */
	HashMap<String, String> mHashMap;
	/* 下载保存路径 */
	private String mSavePath;
	/* 记录进度条数量 */
	private int progress;
	/* 是否取消更新 */
	private boolean cancelUpdate = false;

	private Context mContext;
	/* 更新进度条 */
	private ProgressBar mProgress;
	private Dialog mDownloadDialog;
	private String urlAddr;
	private URL url;

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			// 正在下载
			case DOWNLOAD:
				// 设置进度条位置
				mProgress.setProgress(progress);
				break;
			case DOWNLOAD_FINISH:
				// 安装文件
				installApk();
				break;
			default:
				break;
			}
		};
	};

	public UpdateManager(Context context) {
		this.mContext = context;
	}

	/**
	 * 检测软件更新
	 */
	public void checkUpdate() {
		if (isUpdate() == 1) {
			// 显示提示对话框
			showNoticeDialog();
		} else if (isUpdate() == 0) {
			Toast.makeText(mContext, "已经是最新版本", Toast.LENGTH_LONG)
					.show();
		} else {
			Toast.makeText(mContext, "服务器连接失败", Toast.LENGTH_LONG).show();
		}
	}

	/**
	 * 检查软件是否有更新版本
	 * 
	 * @return
	 */
	private int isUpdate() {
		int netver = 0;// 0没有更新，1有更新，-1服务器连接失败
		// 获取当前软件版本
		int versionCode = getVersionCode(mContext);
		String serverAddr = mContext.getResources().getString(
				R.string.server_addr);
		String urls = serverAddr + "/Data/apkinfo.html";

		mHashMap = new HashMap<String, String>();

		try {
			HttpClient httpClient = new DefaultHttpClient();
			HttpGet httpRequest = new HttpGet(urls);
			HttpResponse httpResponse = httpClient.execute(httpRequest);
			String strResult = "";
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				strResult = EntityUtils.toString(httpResponse.getEntity(),
						"UTF-8");
				try {
					JSONObject jsonObject = new JSONObject(strResult);
					mHashMap.put("name", jsonObject.getString("Name"));
					mHashMap.put("version", jsonObject.getString("Version"));
					mHashMap.put("url", jsonObject.getString("URL"));
					mHashMap.put("updatedesc",
							jsonObject.getString("UpdateDesc"));

					String getVer = mHashMap.get("version").trim()
							.replace(".", "").replace("．", "");
					int serviceCode = Integer.valueOf(getVer).intValue();
					if (serviceCode > versionCode) {
						netver = 1;
					} else {
						netver = 0;
					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} else {
				netver = -1;
			}
		} catch (ClientProtocolException e) {
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// if (mHashMap.size()>0)
		// {
		//
		// //int sc = Integer.valueOf(mHashMap.get("version")).intValue();
		//
		// BigDecimal serviceCode=new BigDecimal(mHashMap.get("version"));
		// // 版本判断
		// //if (serviceCode > versionCode)
		// if (serviceCode.compareTo(versionCode)==1)
		// {
		// netver=1;
		// }
		// else{
		// netver=0;
		// }
		// }
		// return false;
		return netver;
	}

	private InputStream HttpDownLoadURL(String urlstr) {
		StringBuffer sb = new StringBuffer();
		BufferedReader buffer;
		System.out.println("--------------->" + urlstr);
		InputStream inStream = null;
		// 创建一个HTTP连接
		HttpURLConnection urlConn;
		try {
			url = new URL(urlstr);
			urlConn = (HttpURLConnection) url.openConnection();
			// 使用IO进行流数据的读取
			inStream = urlConn.getInputStream();
			buffer = new BufferedReader(new InputStreamReader(
					urlConn.getInputStream()), 1000);
			while ((buffer.readLine()) != null) {
				sb.append(buffer);
			}
		} catch (Exception e) {
			System.out.println("err....... " + e);
			// e.printStackTrace();
		} finally {

		}
		return inStream;
	}

	/**
	 * 获取软件版本号
	 * 
	 * @param context
	 * @return
	 */
	private int getVersionCode(Context context) {
		int versionCode = 0;
		// int vc = 0;
		// BigDecimal versionCode = null;
		try {
			// 获取软件版本号，对应AndroidManifest.xml下android:versionCode
			PackageInfo info = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0);
			versionCode = info.versionCode;

			// vc =
			// context.getPackageManager().getPackageInfo("com.woodpecker.activity",
			// 0).versionCode;
			// versionCode=new BigDecimal(vc);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return versionCode;
	}

	/**
	 * 显示软件更新对话框
	 */
	private void showNoticeDialog() {
		// 构造对话框
		AlertDialog.Builder builder = new Builder(mContext);
		builder.setTitle("发现新版本: v" + mHashMap.get("version"));
		// builder.setMessage(R.string.soft_update_info);
		builder.setMessage(mHashMap.get("updatedesc"));
		// 更新
		builder.setPositiveButton("更新",
				new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						// 显示下载对话框
						showDownloadDialog();
					}
				});
		// 稍后更新
		builder.setNegativeButton("稍后更新",
				new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		Dialog noticeDialog = builder.create();
		noticeDialog.show();
	}

	/**
	 * 显示软件下载对话框
	 */
	private void showDownloadDialog() {
		// 构造软件下载对话框
		AlertDialog.Builder builder = new Builder(mContext);
		builder.setTitle("正在更新");
		// 给下载对话框增加进度条
		final LayoutInflater inflater = LayoutInflater.from(mContext);
		View v = inflater.inflate(R.layout.softupdate_progress, null);
		mProgress = (ProgressBar) v.findViewById(R.id.update_progress);
		builder.setView(v);
		// 取消更新
		builder.setNegativeButton("取消",
				new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						// 设置取消状态
						cancelUpdate = true;
					}
				});
		mDownloadDialog = builder.create();
		mDownloadDialog.show();
		// 现在文件
		downloadApk();
	}

	/**
	 * 下载apk文件
	 */
	private void downloadApk() {
		// 启动新线程下载软件
		new downloadApkThread().start();
	}

	/**
	 * 下载文件线程
	 * 
	 */
	private class downloadApkThread extends Thread {
		@Override
		public void run() {
			try {
				// 判断SD卡是否存在，并且是否具有读写权限
				if (Environment.getExternalStorageState().equals(
						Environment.MEDIA_MOUNTED)) {
					// 获得存储卡的路径
					String sdpath = Environment.getExternalStorageDirectory()
							+ "/";
					mSavePath = sdpath + "WoodpeckerData";
					System.out.println(mHashMap.get("url"));
					URL url = new URL(mHashMap.get("url"));
					// 创建连接
					HttpURLConnection conn = (HttpURLConnection) url
							.openConnection();
					conn.connect();
					// 获取文件大小
					int length = conn.getContentLength();
					// 创建输入流
					InputStream is = conn.getInputStream();

					File file = new File(mSavePath);
					// 判断文件目录是否存在
					if (!file.exists()) {
						file.mkdir();
					}
					File apkFile = new File(mSavePath, mHashMap.get("name"));
					FileOutputStream fos = new FileOutputStream(apkFile);
					int count = 0;
					// 缓存
					byte buf[] = new byte[1024];
					// 写入到文件中
					do {
						int numread = is.read(buf);
						count += numread;
						// 计算进度条位置
						progress = (int) (((float) count / length) * 100);
						// 更新进度
						mHandler.sendEmptyMessage(DOWNLOAD);
						if (numread <= 0) {
							// 下载完成
							mHandler.sendEmptyMessage(DOWNLOAD_FINISH);
							break;
						}
						// 写入文件
						fos.write(buf, 0, numread);
					} while (!cancelUpdate);// 点击取消就停止下载.
					fos.close();
					is.close();
				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			// 取消下载对话框显示
			mDownloadDialog.dismiss();
		}
	};

	/**
	 * 安装APK文件
	 */
	private void installApk() {
		File apkfile = new File(mSavePath, mHashMap.get("name"));
		if (!apkfile.exists()) {
			return;
		}
		// 通过Intent安装APK文件
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setDataAndType(Uri.parse("file://" + apkfile.toString()),
				"application/vnd.android.package-archive");
		mContext.startActivity(i);
	}
}

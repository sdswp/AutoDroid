package com.gk.touchstone.testcasebak;

import java.io.File;
import java.text.DecimalFormat;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.gk.touchstone.R;
import com.gk.touchstone.core.TestCase;
import com.gk.touchstone.core.Constants;
import com.gk.touchstone.entity.Task;
import com.gk.touchstone.utils.Utils;

public class WifiDownloadTask extends TestCase {
	private String folderName;
	private String downloadFileName;

	private TextView downloadSize, downloadPrecent;
	private final String KEY_NAME_DOWNLOAD_ID = "downloadId";
	private String prefsName = "WifiDownloadId";
	public final Uri CONTENT_URI = Uri
			.parse("content://downloads/my_downloads");

	private DownloadManager downloadManager;
	// private DownloadManagerPro downloadManagerPro;
	private long downloadId = 0;
	private boolean isChecking = false;
	private Thread checkThread = null;

	private MyHandler handler;

	public DownloadChangeObserver downloadObserver;
	public CompleteReceiver completeReceiver;

	private Utils us;
	private int spaceTime;
	private String url;

	private String resultStr = "", reasonStr = "";
	private int st;
	private Activity activity;

	public WifiDownloadTask(Context context, Task task) {
		super(context, task);

		activity = (Activity) context;
		us = new Utils(context);
		handler = new MyHandler();
		downloadManager = (DownloadManager) context
				.getSystemService(Context.DOWNLOAD_SERVICE);
		// downloadManagerPro = new DownloadManagerPro(downloadManager);

		if (activity != null) {
			Intent intent = activity.getIntent();
			if (intent != null) {
				Uri data = intent.getData();
				if (data != null) {
					Toast.makeText(context, data.toString(), Toast.LENGTH_LONG)
							.show();
				}
			}
		}

		initContentView();
		initDownloadData();

		downloadObserver = new DownloadChangeObserver();
		completeReceiver = new CompleteReceiver();

		context.registerReceiver(completeReceiver, new IntentFilter(
				DownloadManager.ACTION_DOWNLOAD_COMPLETE));

		initData();
	}

	private void initData() {
		//String wifiDownloadUrl;
		timeCount = convertInt(formValue,"wifiDownloadCount");
		spaceTime = convertInt(formValue,"wifiDownloadInterval");

		st = spaceTime;
	}

	private void initContentView() {

		if (checkThread == null) {
			checkThread = new myThread();
			checkThread.start();
		}

		folderName = context.getResources().getString(R.string.apk_name);
	}

	private void initDownloadData() {
		downloadId = getLong(context, KEY_NAME_DOWNLOAD_ID);
		updateView();
	}

	class DownloadChangeObserver extends ContentObserver {

		public DownloadChangeObserver() {
			super(handler);
		}

		@Override
		public void onChange(boolean selfChange) {
			updateView();
		}

	}

	class CompleteReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {

			long completeDownloadId = intent.getLongExtra(
					DownloadManager.EXTRA_DOWNLOAD_ID, -1);
			if (completeDownloadId == downloadId) {
				initDownloadData();
				updateView();
				// if download successful, install apk
				int getStatusById = getInt(downloadId,
						DownloadManager.COLUMN_STATUS);
				if (getStatusById == DownloadManager.STATUS_SUCCESSFUL) {
					String apkFilePath = new StringBuilder(Environment
							.getExternalStorageDirectory().getAbsolutePath())
							.append(File.separator).append(folderName)
							.append(File.separator).append(downloadFileName)
							.toString();
					// install(context, apkFilePath);
					isChecking = true;
				}
			}
		}
	};

	public void updateView() {
		int[] bytesAndStatus = getBytesAndStatus(downloadId);
		handler.sendMessage(handler.obtainMessage(0, bytesAndStatus[0],
				bytesAndStatus[1], bytesAndStatus[2]));
	}

	public int[] getBytesAndStatus(long downloadId) {
		int[] bytesAndStatus = new int[] { -1, -1, 0 };
		DownloadManager.Query query = new DownloadManager.Query()
				.setFilterById(downloadId);
		Cursor c = null;
		try {
			c = downloadManager.query(query);
			if (c != null && c.moveToFirst()) {
				bytesAndStatus[0] = c
						.getInt(c
								.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
				bytesAndStatus[1] = c
						.getInt(c
								.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
				bytesAndStatus[2] = c.getInt(c
						.getColumnIndex(DownloadManager.COLUMN_STATUS));
			}
		} finally {
			if (c != null) {
				c.close();
			}
		}
		return bytesAndStatus;
	}

	private class myThread extends Thread {
		@Override
		public void run() {
			while (true) {
				try {
					Thread.currentThread();
					Thread.sleep(1000);
					// handler.sendMessage(msg);
					if (isChecking) {
						if (removeDownloadData()) {
							isChecking = false;
						}
					}

				} catch (InterruptedException e) {
					throw new RuntimeException();
				}
			}
		}
	}

	private class MyHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			switch (msg.what) {
			case 0:
				int status = (Integer) msg.obj;
				if (isDownloading(status)) {
					if (activity != null) {
						if (msg.arg2 < 0) {
							downloadPrecent.setText("(0%)");
							downloadSize.setText("0M/0M");
						} else {
							downloadPrecent.setText(getNotiPercent(msg.arg1,
									msg.arg2));
							downloadSize.setText(getAppSize(msg.arg1) + "/"
									+ getAppSize(msg.arg2));
						}
					}
				} else {
					if (status == DownloadManager.STATUS_FAILED) {
						resultStr = Constants.FAIL;
						reasonStr = "STATUS_FAILED";
					} else if (status == DownloadManager.STATUS_SUCCESSFUL) {
						resultStr = Constants.FAIL;
						reasonStr = "STATUS_SUCCESSFUL";
					} else {

					}
				}
				break;
			}
		}
	}

	static final DecimalFormat DOUBLE_DECIMAL_FORMAT = new DecimalFormat("0.##");

	public static final int MB_2_BYTE = 1024 * 1024;
	public static final int KB_2_BYTE = 1024;

	/**
	 * @param size
	 * @return
	 */
	public static CharSequence getAppSize(long size) {
		if (size <= 0) {
			return "0M";
		}

		if (size >= MB_2_BYTE) {
			return new StringBuilder(16).append(
					DOUBLE_DECIMAL_FORMAT.format((double) size / MB_2_BYTE))
					.append("M");
		} else if (size >= KB_2_BYTE) {
			return new StringBuilder(16).append(
					DOUBLE_DECIMAL_FORMAT.format((double) size / KB_2_BYTE))
					.append("K");
		} else {
			return size + "B";
		}
	}

	public static String getNotiPercent(long progress, long max) {
		int rate = 0;
		if (progress <= 0 || max <= 0) {
			rate = 0;
		} else if (progress > max) {
			rate = 100;
		} else {
			rate = (int) ((double) progress / max * 100);
		}

		String percentStr = new StringBuilder(16).append(rate).append("%)")
				.toString();

		String downloadRate = "(" + percentStr;

		return downloadRate;
	}

	public static boolean isDownloading(int downloadManagerStatus) {
		return downloadManagerStatus == DownloadManager.STATUS_RUNNING
				|| downloadManagerStatus == DownloadManager.STATUS_PAUSED
				|| downloadManagerStatus == DownloadManager.STATUS_PENDING;
	}

	private boolean removeDownloadData() {
		boolean delFinish = false;
		if (downloadManager.remove(downloadId) == 0) {
			// downloadId = 0;
			// PreferencesUtils.putLong(this, KEY_NAME_DOWNLOAD_ID, downloadId);
			File file = new File(us.getAppSDCardPath() + downloadFileName);
			if (file.exists()) {
				delFinish = file.delete();
			}
		}
		return delFinish;
	}

	private boolean putLong(Context context, String key, long value) {
		SharedPreferences settings = context.getSharedPreferences(prefsName,
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		editor.putLong(key, value);
		return editor.commit();
	}

	private long getLong(Context context, String key) {
		return getLong(context, key, -1);
	}

	private long getLong(Context context, String key, long defaultValue) {
		SharedPreferences settings = context.getSharedPreferences(prefsName,
				Context.MODE_PRIVATE);
		return settings.getLong(key, defaultValue);
	}

	private int getInt(long downloadId, String columnName) {
		DownloadManager.Query query = new DownloadManager.Query()
				.setFilterById(downloadId);
		int result = -1;
		Cursor c = null;
		try {
			c = downloadManager.query(query);
			if (c != null && c.moveToFirst()) {
				result = c.getInt(c.getColumnIndex(columnName));
			}
		} finally {
			if (c != null) {
				c.close();
			}
		}
		return result;
	}

	@Override
	public void Start() {
		File folder = new File(folderName);
		if (!folder.exists() || !folder.isDirectory()) {
			folder.mkdirs();
		}

		MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
		String mimeString = mimeTypeMap.getMimeTypeFromExtension(MimeTypeMap
				.getFileExtensionFromUrl(url));

		downloadFileName = "download" + "." + mimeString.split("\\/")[1];

		DownloadManager.Request request = new DownloadManager.Request(
				Uri.parse(url));
		request.setDestinationInExternalPublicDir(folderName, downloadFileName);
		request.setTitle(downloadFileName);
		// request.setDescription("desc");
		// request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
		request.setVisibleInDownloadsUi(false);
		// request.allowScanningByMediaScanner();
		// request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
		// request.setShowRunningNotification(false);
		// request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);

		request.setMimeType(mimeString);

		downloadId = downloadManager.enqueue(request);

		putLong(context, KEY_NAME_DOWNLOAD_ID, downloadId);
		updateView();
	}

	@Override
	public void Finish() {
		downloadManager.remove(downloadId);
		updateView();
	}

	@Override
	public void Stop() {
		// TODO Auto-generated method stub

	}
}

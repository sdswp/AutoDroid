package com.gk.touchstone.taskview;

import com.gk.touchstone.GkApplication;
import com.gk.touchstone.R;
import com.gk.touchstone.activity.DownloadAddr;
import com.gk.touchstone.core.BaseActivity;
import com.gk.touchstone.core.TaskManager;
import com.gk.touchstone.testcasebak.WifiDownloadTask;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class WifiDownload extends BaseActivity {

	private int RESULT_OK = 1;
	private String[] downloadInfo = null;
	private TextView downloadSize, downloadPrecent;
	private EditText txtDownloadUrl;
	private String downloadFileName;
	private WifiDownloadTask wifiDownloadTask;
	private TaskManager tm;

	private GkApplication app;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setBaseContentView();//(R.layout.wifi_download);
		//initValue();

		initView();
	}

	private void initView() {
//		tm = new TaskManager(this);
//		app = (GkApplication) getApplication();
//		if (!tm.isTaskRunning(app.getTaskBases())) {
//			return;
//		}
//		//wifiDownloadTask = (WifiDownloadTask) app.getTaskBases().get(0);
//		Button btnSet = (Button) findViewById(R.id.btn_selectFile);
//		btnSet.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				System.out.println("ffefwfwe");
//				Intent intent = new Intent(WifiDownload.this,
//						DownloadAddr.class);
//				Bundle bundle = new Bundle();
//				bundle.putInt("RESULT_OK", RESULT_OK);
//				intent.putExtras(bundle);
//				startActivityForResult(intent, 0);
//			}
//		});
//
//		downloadSize = (TextView) findViewById(R.id.download_size);
//		downloadPrecent = (TextView) findViewById(R.id.download_precent);
	}

	// @Override
	// protected void onResume() {
	// super.onResume();
	//
	// getContentResolver().registerContentObserver(
	// wifiDownloadTask.CONTENT_URI, true,
	// wifiDownloadTask.downloadObserver);
	// wifiDownloadTask.updateView();
	// }

	@Override
	protected void onPause() {
		super.onPause();
		getContentResolver().unregisterContentObserver(
				wifiDownloadTask.downloadObserver);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// unregisterReceiver(completeReceiver);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			downloadInfo = data.getStringArrayExtra("selectFile");
			// downloadFileName = downloadInfo[0];
			// url = downloadInfo[1];
			txtDownloadUrl.setText(downloadInfo[1]);

		}
	}

}

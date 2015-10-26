package com.gk.touchstone.activity;

import com.gk.touchstone.GkApplication;
import com.gk.touchstone.R;
import com.gk.touchstone.adapter.ReportAdapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.TextView;

public class ReportTaskDetail extends Activity {
	private ListView listview;
	private ReportAdapter adapter;
	private GkApplication app;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// 设置窗体始终点亮
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.report_task_detail);

		initView();
	}

	private void initView() {
		app = (GkApplication) this.getApplication();

		listview = (ListView) findViewById(R.id.lv_task_detail);

		Intent intent = getIntent();
		String tname = intent.getStringExtra("displayName");

		TextView TitleView = (TextView) findViewById(R.id.txt_title);
		TitleView.setText(tname);

		adapter = new ReportAdapter(this, app.getTaskRepots());
		listview.setAdapter(adapter);
	}
}

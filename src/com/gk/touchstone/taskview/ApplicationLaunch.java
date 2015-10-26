package com.gk.touchstone.taskview;

import com.gk.touchstone.R;
import com.gk.touchstone.core.BaseActivity;
import com.gk.touchstone.testcasebak.ApplicationLaunchTask;
import com.gk.touchstone.utils.Utils;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;

public class ApplicationLaunch extends Activity {
	
	private Button btnStart,btnStop;
	private String className = this.getClass().getSimpleName();
	private Utils us;
	private ApplicationLaunchTask taskAction;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.compatibilitytest);
		//不打开软键盘
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		initView();
	}
	
	private void initView(){
		us = new Utils(this);
		btnStart = (Button) findViewById(R.id.btnStart);
		btnStop = (Button) findViewById(R.id.btnStop);
		btnStart.setOnClickListener(new startRun());
		btnStop.setOnClickListener(new stopRun());
	}
	
	private class startRun implements OnClickListener {
		@Override
		public void onClick(View v){
//			//guohai@live.com new task方法已经更改，newTask已经注释掉
//			Task task = us.newTask(className);
//			taskAction = new ApplicationLaunchTask(ApplicationLaunch.this, task);
//			taskAction.Start();
//			btnStart.setEnabled(false);
//			btnStop.setEnabled(true);
		}
	}
	
	private class stopRun implements OnClickListener {
		@Override
		public void onClick(View v) {
			taskAction.Finish();
			btnStart.setEnabled(true);
			btnStop.setEnabled(false);
		}
	}
}

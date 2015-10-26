package com.gk.touchstone.taskview;

import com.gk.touchstone.R;
import com.gk.touchstone.core.BaseActivity;

import android.os.Bundle;

public class AutoCallAnswer extends BaseActivity{
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setBaseContentView();//(R.layout.phone_answer);
		//initValue();
	}
	
	/*private void initView(){
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		xp = new XmlParser(this);
		xp.initValue();
		us = new Utils(this);

		titleBarName = xp.allActivityMap().get(className).get("text")
				.toString();
		TextView TitleView = (TextView) findViewById(R.id.txt_title);
		TitleView.setText(titleBarName);
		
		Button btnSave = (Button) findViewById(R.id.title_rbtn);
		btnSave.setText(R.string.save);
		btnSave.setOnClickListener(new saveFormValue());
		
		Edt_answerNumber = (EditText) findViewById(R.id.answerNumnber);
		Edt_hookTime = (EditText) findViewById(R.id.hookTime);
		//tvleftTimes = (TextView) findViewById(R.id.leftTimes);
		Btn_startWait = (Button) findViewById(R.id.btn_waitstart);
		Btn_endWait = (Button) findViewById(R.id.btn_waitcancel);
		
		Btn_startWait.setOnClickListener(new startRun());
		Btn_endWait.setOnClickListener(new cancelRun());
		Btn_endWait.setEnabled(false);
	}
	
	private class saveFormValue implements OnClickListener {
		@Override
		public void onClick(View v) {
			if (xp.saveValue()) {
				us.myToast(titleBarName, R.string.save_data);
				AutoCallAnswer.this.finish();
			}
		}
	}
	
	private class startRun implements OnClickListener{
		@Override
		public void onClick(View v){
			if (xp.saveValue()) {
				Task task = us.newTask(className);
				taskAction = new AutoCallAnswerTask(AutoCallAnswer.this, task);
				taskAction.Start();
			
			Edt_answerNumber.setEnabled(false);
			Edt_hookTime.setEnabled(false);
			Btn_startWait.setEnabled(false);
			Btn_endWait.setEnabled(true);
			}
		}
	}
	
	private class cancelRun implements OnClickListener{
		@Override
		public void onClick(View v){
			taskAction.Finish();
			Edt_answerNumber.setEnabled(true);
			Edt_hookTime.setEnabled(true);
			Btn_startWait.setEnabled(true);
			Btn_endWait.setEnabled(false);
		}
	}*/
}

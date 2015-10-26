package com.gk.touchstone.taskview;

import java.util.List;
import java.util.Map;

import com.gk.touchstone.R;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

public class TouchScreenGesture extends Activity {
	private TextView txtTitle;
	private Button btnStart;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.touch_screen);

		initView();
	}
	
	private void initView() {
		txtTitle=(TextView)findViewById(R.id.txt_title);
		txtTitle.setText("触屏测试_手势识别");
		
	
		btnStart=(Button) findViewById(R.id.btn_start);
		
	}
}

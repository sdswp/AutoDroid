package com.gk.touchstone.taskview;

import java.util.List;
import java.util.Map;

import com.gk.touchstone.R;
import com.gk.touchstone.adapter.HardwareModuleAdapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class TouchScreenMain extends Activity {
	private TextView txtTitle;
	private List<Map<String, Object>> listitem = null;
	private ListView listview;
	private Button btnStart;
	private HardwareModuleAdapter hdAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.hardware_main);
		initView();
	}
	
	private void initView() {
		txtTitle = (TextView) findViewById(R.id.txt_title);
		listview = (ListView) findViewById(R.id.lv_sublist);
		btnStart = (Button) findViewById(R.id.btn_start);
		
		Intent intent=getIntent();
		String barValue=intent.getStringExtra("titlebar");
		txtTitle.setText(barValue);
		
//		xp = new XmlParser(this);
//		listitem = xp.getHardwareList(R.xml.hardware_module_bak,"touchscreen");
//		
//
//		//String[] devices={"定位精度","滑屏流畅度","触屏响应时间","手势识别","多点触摸：定位精度","多点触摸：滑屏流畅度","多点触摸：响应时间","多点触摸：手势识别","抗电磁干扰","抗水性"};
//		hdAdapter = new HardwareModuleAdapter(this, listitem,
//				R.layout.cbox_txtview_item);
//		listview.setAdapter(hdAdapter);
//		
//		listview.setOnItemClickListener(new OnItemClickListener() {
//			@Override
//			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
//					long arg3) {
////				Reflects ref=new Reflects(TouchScreenMain.this);
////				ref.startActivityByMap(listitem,arg2);
//			}
//		});
		
	}
		
}

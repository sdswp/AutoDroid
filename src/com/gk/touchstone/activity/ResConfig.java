package com.gk.touchstone.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.gk.touchstone.GkApplication;
import com.gk.touchstone.R;
import com.gk.touchstone.core.Constants;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class ResConfig extends Activity{
	private List<Map<String,Object>> maplist=null;
	private GkApplication app;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.res_config);
		
		initView();
	}
	
	
	private void initView(){
		app = (GkApplication) getApplication();
		
		TextView title=(TextView) findViewById(R.id.txt_title);
		title.setText("测试资源配置");
		
		String[] res=new String[]{"APList|AP列表","DownloadAddr|下载地址"};
		
		maplist=new ArrayList<Map<String,Object>>();
		for(String s: res){
			String[] resItem=s.split("\\|");
			Map<String,Object> map=new HashMap<String, Object>();
			map.put("name", resItem[0]);
			map.put("displayName", resItem[1]);
			maplist.add(map);
		}
		
		String[] from = {"displayName"};
        int[] to = { R.id.txt_resName};
		
		SimpleAdapter adapter =new SimpleAdapter(this, maplist, R.layout.res_config_item, from, to);
		
		ListView reslistView=(ListView) findViewById(R.id.lv_res);
		reslistView.setAdapter(adapter);
		
		reslistView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				String act=maplist.get(arg2).get("name").toString();
				Class<?> cls = null;
				try {
					cls = Class.forName(Constants.PACKAGE_ACTIVITY + act);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				//Class<?> clazz= rf.getActivityClass(maplist.get(arg2).get("name").toString());
				Intent intent=new Intent(ResConfig.this,cls);
				startActivity(intent);
				
			}
		});
		
		
		
	}
	
}

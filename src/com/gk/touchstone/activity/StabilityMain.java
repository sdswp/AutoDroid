package com.gk.touchstone.activity;

import java.util.List;
import java.util.Map;

import com.gk.touchstone.GkApplication;
import com.gk.touchstone.R;
import com.gk.touchstone.core.Constants;
import com.gk.touchstone.utils.Utils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

public class StabilityMain extends Fragment {
	private List<Map<String, Object>> listitem = null;
	private ListView listviewFunc;
	private TextView txtTitle;
	private Button btnTitleR;
	private ViewGroup linerlayoutstability;
	private GkApplication app;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View parentView = inflater.inflate(R.layout.stability_main, container, false);

		listviewFunc = (ListView) parentView.findViewById(R.id.lv_funs);
		txtTitle = (TextView) parentView.findViewById(R.id.txt_title);
		linerlayoutstability = (ViewGroup) parentView.findViewById(R.id.ll_stabilitytitle);
		DisplayMetrics metric = new DisplayMetrics();
		Activity act = (Activity) getActivity();
		act.getWindowManager().getDefaultDisplay().getMetrics(metric);
		int width = metric.widthPixels;  // 屏幕宽度（像素）
		int height = metric.heightPixels;  // 屏幕高度（像素）
		float density = metric.density;  // 屏幕密度（0.75 / 1.0 / 1.5）
		int densityDpi = metric.densityDpi;  // 屏幕密度DPI（120 / 160 / 240）
		
		if (width == 240 && height == 240){
			linerlayoutstability.setVisibility(View.GONE);
		}
		//btnTitleR = (Button) parentView.findViewById(R.id.title_rbtn);

		initView();

		return parentView;
	}

	private void initView() {
		txtTitle.setText(R.string.tab_2);
		app = (GkApplication) getActivity().getApplication();
		//btnTitleR.setOnClickListener(new openSetting());
		Utils us = new Utils(getActivity());
		
		listitem = us.getHomeView(R.xml.home_listvalue,"stability");

		SimpleAdapter adapter = new SimpleAdapter(getActivity(), listitem,
				R.layout.func_item, new String[] { "icon", "text" },
				new int[] { R.id.img_icon, R.id.txt_func });

		listviewFunc.setAdapter(adapter);

		listviewFunc.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				//if (listitem.get(arg2).get("name").equals("stabilityTest")) {
					startActivityByMap(listitem,arg2);
					/*Intent intent = new Intent(getActivity(),
							StabilityTest.class);
					startActivity(intent);*/
				//}
			}
		});
	}

	private class openSetting implements OnClickListener {
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(getActivity(), Settings.class);
			startActivity(intent);
		}
	}
	
	private void startActivityByMap(List<Map<String, Object>> listitem, int arg) {
		String getName = listitem.get(arg).get("name").toString();
		String getValue = listitem.get(arg).get("text").toString();
		String getAction = "";
		if (listitem.get(arg).get("action") != null) {
			getAction = listitem.get(arg).get("action").toString();
		}

		Class<?> clazz = null;
		try {
			clazz = Class.forName(Constants.PACKAGE_ACTIVITY + getName);
		} catch (Exception e) {

		}

		Intent intent = new Intent(getActivity(), clazz);
		intent.putExtra("titlebar", getValue);
		intent.putExtra("taskAction", getAction);
		intent.putExtra("className", getName);
		startActivity(intent);
	}
	
}

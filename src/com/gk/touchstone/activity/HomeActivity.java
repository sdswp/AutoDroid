package com.gk.touchstone.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.gk.touchstone.GkApplication;
import com.gk.touchstone.R;
import com.gk.touchstone.core.TaskManager;
import com.gk.touchstone.core.Constants;
import com.gk.touchstone.utils.Utils;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class HomeActivity extends Fragment {
	private List<Map<String, Object>> listitem = null;
	private Button btnInfo;
	private ListView listviewFunc;
	private TextView txtTitle;
	private ViewGroup linerlayouthome;
	private GkApplication app;
	private TaskManager tm;
	private Utils us;

	OnBackListener mListener;

	public interface OnBackListener {
		public void backEvent();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (OnBackListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnArticleSelectedListener");
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View parentView = inflater.inflate(R.layout.home, container, false);

		btnInfo = (Button) parentView.findViewById(R.id.title_leftBtn);
		listviewFunc = (ListView) parentView.findViewById(R.id.lv_funs);
		txtTitle = (TextView) parentView.findViewById(R.id.txt_title);
		txtTitle.setText(R.string.app_name);

		linerlayouthome = (ViewGroup) parentView
				.findViewById(R.id.ll_hometitle);
		DisplayMetrics metric = new DisplayMetrics();
		Activity act = (Activity) getActivity();
		act.getWindowManager().getDefaultDisplay().getMetrics(metric);
		int width = metric.widthPixels; // 屏幕宽度（像素）
		int height = metric.heightPixels; // 屏幕高度（像素）
		float density = metric.density; // 屏幕密度（0.75 / 1.0 / 1.5）
		int densityDpi = metric.densityDpi; // 屏幕密度DPI（120 / 160 / 240）
		if (width == 240 && height == 240) {
			linerlayouthome.setVisibility(View.GONE);
		}
		// btnTitleR = (Button) parentView.findViewById(R.id.title_rbtn);
		initView();
		return parentView;
	}

	private void initView() {
		app = (GkApplication) getActivity().getApplication();
		tm = new TaskManager(getActivity());
		us = new Utils(getActivity());

		listitem = us.getHomeView(R.xml.home_listvalue, "home");

		SimpleAdapter adapter = new SimpleAdapter(getActivity(), listitem,
				R.layout.func_item, new String[] { "icon", "text" }, new int[] {
						R.id.img_icon, R.id.txt_func });

		listviewFunc.setAdapter(adapter);

		listviewFunc.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if (tm.isTaskRunning(app.getTaskBases())) {
					if (arg2 == 0) {
						startActivityByMap(listitem, arg2);
					}
					us.myToast("计划", R.string.plan_error1);
				} else {
					startActivityByMap(listitem, arg2);
				}
			}
		});
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

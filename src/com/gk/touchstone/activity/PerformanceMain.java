package com.gk.touchstone.activity;

import java.util.List;
import java.util.Map;

import com.gk.touchstone.R;
import com.gk.touchstone.adapter.ModuleHomeAdapter;
import com.gk.touchstone.db.DBManager;
import com.gk.touchstone.entity.Module;
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
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

public class PerformanceMain extends Fragment {
	private List<Map<String, Object>> listitem = null;
	private GridView gridview;
	private TextView txtTitle;
	private Utils tu;
	private ViewGroup linerlayoutperformance;
	private DBManager dbm;
	private List<Module> modules;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View parentView = inflater.inflate(R.layout.performance_main, container, false);

		gridview = (GridView) parentView.findViewById(R.id.gv_diy);
		txtTitle = (TextView) parentView.findViewById(R.id.txt_title);
		//btnTitleR = (Button) parentView.findViewById(R.id.title_rbtn);
		
		linerlayoutperformance = (ViewGroup) parentView.findViewById(R.id.ll_performancetitle);
		DisplayMetrics metric = new DisplayMetrics();
		Activity act = (Activity) getActivity();
		act.getWindowManager().getDefaultDisplay().getMetrics(metric);
		int width = metric.widthPixels;  // 屏幕宽度（像素）
		int height = metric.heightPixels;  // 屏幕高度（像素）
		float density = metric.density;  // 屏幕密度（0.75 / 1.0 / 1.5）
		int densityDpi = metric.densityDpi;  // 屏幕密度DPI（120 / 160 / 240）
		
		if (width == 240 && height == 240){
			linerlayoutperformance.setVisibility(View.GONE);
		}
		
		initView();

		return parentView;
	}

	private void initView() {
		dbm=new DBManager(getActivity());
		modules=dbm.queryAllModule();
		dbm.closedb();
		
		ModuleHomeAdapter adapter =new ModuleHomeAdapter(getActivity(),modules,R.layout.pfm_grid_item);
		gridview.setAdapter(adapter);
		
		txtTitle.setText(R.string.tab_3);

//		tu=new Utils(getActivity());
//		XmlParser xp = new XmlParser(getActivity());
//		listitem = xp.getHomeView(R.xml.home_listvalue,"performance");
//		
//		TaskValue.setHwModuleInitMap(xp.getHardwareMap(R.xml.hardware_module));
//		
//		SimpleAdapter adapter = new SimpleAdapter(getActivity(), listitem,
//				R.layout.pfm_grid_item, new String[] { "icon", "text" },
//				new int[] { R.id.img_icon, R.id.txt_diy_name });
//
//		gridview.setAdapter(adapter);

		gridview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				//String displayName=modules.get(arg2).getDisplayName();
				Intent intent = new Intent(getActivity(),TestCaseSelector.class);
				intent.putExtra("pos", arg2);
				//intent.putExtra("moduleName", listitem.get(arg2).get(""));
				//intent.putExtra("displayname", displayName);
				startActivity(intent);
				
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

}

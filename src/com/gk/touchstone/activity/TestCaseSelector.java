package com.gk.touchstone.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import com.gk.touchstone.GkApplication;
import com.gk.touchstone.R;
import com.gk.touchstone.adapter.ModuleViewAdapter;
import com.gk.touchstone.adapter.TestCaseAdapter;
import com.gk.touchstone.db.DBManager;
import com.gk.touchstone.entity.Module;
import com.gk.touchstone.entity.Task;
import com.gk.touchstone.entity.Case;
import com.gk.touchstone.core.Constants;
import com.gk.touchstone.utils.Utils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class TestCaseSelector extends Activity {
	private TestCaseAdapter adapter;
	private ViewPager viewPager;
	private PagerTabStrip pagerTabStrip;
	private List<Case> testCases = null;

	private List<Module> modules = null;
	private List<TestCaseAdapter> adapters = null;
	private List<View> views;
	private List<String> titles;
	private DBManager dbm;
	private GkApplication app;
	private Utils us;
	private List<Task> tasklist;
	private int openActivityIndex = 0;
	private View currentView;
	private int arg2 = 0;
	private int selectModuleId = 0;
	private int selectCaseId = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// 设置窗体始终点亮
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		setContentView(R.layout.pfm_viewpager);
		initView();
	}

	private void initView() {
		us = new Utils(this);
		dbm = new DBManager(this);
		app = (GkApplication) getApplication();
		app.setTestCaseSettings(null);// 清空设置

		testCases = dbm.queryAllTestCase();
		modules = dbm.queryAllModule();
		dbm.closedb();

		// 从main传过来的值
		Intent intent = getIntent();
		int itemPos = intent.getIntExtra("pos", 0);
		// viewPager.setCurrentItem(itemPos);

		TextView txtTitle = (TextView) findViewById(R.id.txt_title);
		txtTitle.setText("选择测试项");
		Button btnSave = (Button) findViewById(R.id.title_rbtn);
		btnSave.setText("添加");

		btnSave.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				tasklist = getSelectedItem();
				if (tasklist.size() > 0) {
					Intent intent = new Intent(TestCaseSelector.this,
							PlanSetting.class);
					app.setSelectTasks(tasklist);
					startActivity(intent);
					// TODO setResult后 app.setSelectTasks(null);
				} else {
					us.myToast("提醒：", R.string.select_test);
				}
			}
		});

		adapter = new TestCaseAdapter(this, testCases, R.layout.cbtv_arrow_item);
		views = new ArrayList<View>();// 将要分页显示的View装入数组中
		titles = new ArrayList<String>();// 每个页面的Title数据

		adapters = new ArrayList<TestCaseAdapter>();

		for (Module m : modules) {
			int mid = m.getId();
			String moduleName = m.getName();
			String moduleDisplayName = m.getDisplayName();

			lvAdapter(mid, moduleName, moduleDisplayName);
		}

		viewPager = (ViewPager) findViewById(R.id.viewpager);
		pagerTabStrip = (PagerTabStrip) findViewById(R.id.pagertab);
		pagerTabStrip.setTabIndicatorColor(getResources().getColor(
				R.color.pinkishred));
		pagerTabStrip.setDrawFullUnderline(false);
		pagerTabStrip.setBackgroundColor(getResources().getColor(
				R.color.gray_light2));
		pagerTabStrip.setTextSpacing(20);

		ModuleViewAdapter mvAdapter = new ModuleViewAdapter(views, titles);
		viewPager.setAdapter(mvAdapter);
		viewPager.setCurrentItem(itemPos);

	}

	// 每个listview 包含的TestCase集合
	private List<Case> viewTestCase(int moduleId) {
		final List<Case> currentViewCase = new ArrayList<Case>();
		for (Case t : testCases) {
			if (t.getModuleId() == moduleId) {
				if(t.getIsJoin()==0){
					t.setDisplayName(t.getDisplayName()+" (不允许加入串并行)");
				}
				currentViewCase.add(t);
			}
		}
		return currentViewCase;
	}

	private void lvAdapter(final int moduleId, String moduleName,
			String moduleDisplayName) {

		final List<Case> selectors = viewTestCase(moduleId);

		if (selectors.size() > 0) {
//			int pid = getResources().getIdentifier("pfm_" + moduleName,
//					"layout", getPackageName());
//			int rid = getResources().getIdentifier("lv_" + moduleName, "id",
//					getPackageName());

			View view = getLayoutInflater().inflate(R.layout.pfm_listv, null);
			views.add(view);
			titles.add(moduleDisplayName);

			// 单个Listview
			ListView lv = (ListView) view.findViewById(R.id.pfms);

			final TestCaseAdapter adapter = new TestCaseAdapter(this, selectors,
					R.layout.cbtv_arrow_item);
			lv.setAdapter(adapter);

			lv.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					openActivityIndex = arg2;
					currentView = arg1;
					Case tc= (Case) adapter.getItem(arg2);
					// selectModuleId = moduleId;
					selectCaseId = tc.getId();

					String act = tc.getName();

					Class<?> cls = null;
					try {
						cls = Class.forName(Constants.TASK_VIEWS + act);
					} catch (Exception e) {
						e.printStackTrace();
					}

					Intent intent = new Intent(TestCaseSelector.this, cls);
					intent.putExtra("testCaseName", selectors.get(arg2)
							.getDisplayName());
					intent.putExtra("testCaseValue", selectors.get(arg2)
							.getValue());
					intent.putExtra("testCaseId", selectCaseId);

					Bundle bundle = new Bundle();
					bundle.putInt("RESULT_OK", RESULT_OK);

					intent.putExtras(bundle);
					startActivityForResult(intent, 0);

				}
			});

			adapters.add(adapter);
		}
	}

	public List<Task> getSelectedItem() {
		List<Task> tasklist = new ArrayList<Task>();
		for (TestCaseAdapter hdaa : adapters) {
			HashMap<Integer, Boolean> cbState = hdaa.state;

			for (int j = 0; j < hdaa.getCount(); j++) {
				if (cbState.get(j) != null) {
					Case testcase = (Case) hdaa.getItem(j);
					// TODO
					// 保存过的TestCase是存到app.getTestCaseSettings，还是序列化替换ListView
					// 的TestCase

					Task t = selectNewTask(testcase);
					tasklist.add(t);
				}
			}
		}
		return tasklist;
	}

	// 新建任务
	private Task selectNewTask(Case testcase) {
		Task task = new Task();
		task.setTaskName(testcase.getName());
		task.setDisplayName(testcase.getDisplayName());
		task.setTaskAction(testcase.getAction());
		task.setFormValue(testcase.getValue());

		// 默认10次
		task.setOriginCount(10);
		task.setTaskCount(10);

		UUID uuid = UUID.randomUUID();
		String resultFileName = uuid.toString() + ".log";
		task.setResultFile(resultFileName);
		task.setResultJson("");
		task.setCreateTime(us.getCurrentTime());
		task.setUpdateTime(us.getCurrentTime());

		return task;
	}

	// 保存后Checkbox 选中，TestCase修改
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode == RESULT_OK) {
			CheckBox cb = (CheckBox) currentView.findViewById(R.id.cb_testcase);

			String formValue = data.getStringExtra("testCaseValue");
			// int arg2 = data.getIntExtra("arg2", 0);

			for (Case t : testCases) {
				if (t.getId() == selectCaseId) {
					t.setValue(formValue);
					//为0，不允许加入到串并行任务
					if (t.getIsJoin() == 1) {
						cb.setChecked(true);
					}
					
					break;
				}
			}

			// selectors.set(arg2, testcase);
		}
	}

}

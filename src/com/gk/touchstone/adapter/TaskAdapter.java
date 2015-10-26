package com.gk.touchstone.adapter;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.gk.touchstone.R;
import com.gk.touchstone.entity.Report;
import com.gk.touchstone.entity.Task;
import com.gk.touchstone.core.Constants;
import com.gk.touchstone.core.ResultFileParse;
import com.gk.touchstone.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class TaskAdapter extends BaseAdapter {
	private Context context;
	private List<Task> tasklist;
	private List<Report> reportlist;
	private LayoutInflater listContainer;// 视图容器
	private int itemViewResource;// 自定义项视图源
	private boolean switchCheckbox;
	private Utils us;
	private ResultFileParse prl;
	private Gson gson;

	public HashMap<Integer, Boolean> state = new HashMap<Integer, Boolean>();

	public TaskAdapter(Context context, List<Task> tasklist,
			List<Report> reportlist, int rid, boolean switchCheckbox) {
		this.context = context;
		us = new Utils(context);
		gson = new Gson();

		this.tasklist = tasklist;
		this.reportlist = reportlist;

		this.listContainer = LayoutInflater.from(context);// 创建视图容器并设置上下文
		this.itemViewResource = rid;

		this.switchCheckbox = switchCheckbox;
		prl = new ResultFileParse(context);

	}

	public void setItemList(List<Task> list) {
		tasklist = list;
	}

	@Override
	public int getCount() {
		return tasklist.size();
	}

	@Override
	public Object getItem(int position) {
		return tasklist.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = listContainer.inflate(this.itemViewResource, null);
		}
		TextView txtCaseName = (TextView) convertView
				.findViewById(R.id.txt_taskname);
		TextView txtState = (TextView) convertView.findViewById(R.id.txt_state);
		TextView txtDateTime = (TextView) convertView
				.findViewById(R.id.txt_datetime);
		TextView txtStCount = (TextView) convertView
				.findViewById(R.id.txt_stCount);
		TextView txtParams = (TextView) convertView
				.findViewById(R.id.txt_params);

		Task task = tasklist.get(position);
		txtCaseName.setText(task.getDisplayName());

		List<Report> reports = prl.getReportByTask(reportlist, task);

		String timeStr = prl.getMaxTime(reports);

		if (reports != null && reports.size() > 0) {
			int failCount = prl.getFailCount(reports);
			int results = reports.get(0).getResultLists();// 获取该任务的场景数

			if (failCount > 0) {
				txtState.setText(us.setResultColor(Constants.FAIL));
			} else {
				txtState.setText(us.setResultColor(Constants.PASS));
			}
			String allCount = "总次数:" + task.getTaskCount() * results + ", 执行:"
					+ reports.size() + ", 失败:" + failCount;
			txtStCount.setText(allCount);
		} else {
			String allCount = "<font color='#FF0000'>log文件异常！</font>";
			txtStCount.setText(Html.fromHtml(allCount));
		}

		txtDateTime.setText(timeStr);

		StringBuilder sb = new StringBuilder();

		Map<String, Object> tcMap = gson.fromJson(task.getFormValue(),
				new TypeToken<Map<String, Object>>() {
				}.getType());
		Set<String> keys = tcMap.keySet();
		for (Iterator<String> it = keys.iterator(); it.hasNext();) {
			String str = (String) it.next();
			String val = us.objToStr(tcMap.get(str));
			
			int rid = context.getResources().getIdentifier(str, "string",
					context.getPackageName());
			String displayName = context.getResources().getString(rid);
			
			sb.append(displayName + ":" + val + ", ");
		}
		String paramstr=sb.toString();
		txtParams.setText("参数:" + paramstr.substring(0, paramstr.length()-2));

		CheckBox check = (CheckBox) convertView.findViewById(R.id.cb_testcase);

		if (switchCheckbox) {
			check.setVisibility(View.VISIBLE);
		} else {
			check.setVisibility(View.GONE);
		}

		check.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {
					state.put(position, isChecked);
				} else {
					state.remove(position);
				}
			}
		});
		check.setChecked((state.get(position) == null ? false : true));
		return convertView;
	}
}
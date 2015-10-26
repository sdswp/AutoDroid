package com.gk.touchstone.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.gk.touchstone.R;
import com.gk.touchstone.core.TaskManager;
import com.gk.touchstone.entity.Plan;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class TestReportAdapter extends BaseAdapter {
	private Context context;
	private List<Map<String, Object>> planlist;

	public HashMap<Integer, Boolean> state = new HashMap<Integer, Boolean>();

	public TestReportAdapter(Context context, List<Map<String, Object>> planlist) {
		this.context = context;
		this.planlist = planlist;

	}
	
	public String getType(int position) {
		// TODO Auto-generated method stub
		return planlist.get(position).get("type").toString();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return planlist.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return planlist.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	// 重写View
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub

		LayoutInflater mInflater = LayoutInflater.from(context);
		convertView = mInflater.inflate(R.layout.test_result_item, null);

		TextView txtPlanItem = (TextView) convertView
				.findViewById(R.id.txt_planItem);
		TextView txtFileSize = (TextView) convertView
				.findViewById(R.id.txt_fileSize);
		TextView txtUpdatetime = (TextView) convertView
				.findViewById(R.id.txt_updatetime);

		String planname = planlist.get(position).get("plan").toString();
		txtPlanItem.setText(planname);

		String filesize = planlist.get(position).get("filesize").toString();
		txtFileSize.setText(filesize);

		String updatetime = planlist.get(position).get("time").toString();
		txtUpdatetime.setText(updatetime);

		CheckBox check = (CheckBox) convertView.findViewById(R.id.cb_planItem);
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
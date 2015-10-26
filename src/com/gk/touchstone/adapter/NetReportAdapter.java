package com.gk.touchstone.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.gk.touchstone.R;
import com.gk.touchstone.entity.Plan;
import com.gk.touchstone.entity.Task;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class NetReportAdapter extends BaseAdapter {
	private Context context;
	private List<Task> tasklist;
	public HashMap<Integer, Boolean> state = new HashMap<Integer, Boolean>();

	public NetReportAdapter(Context context, List<Task> tasklist) {
		this.context = context;
		this.tasklist = tasklist;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return tasklist.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return tasklist.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub

		LayoutInflater mInflater = LayoutInflater.from(context);
		convertView = mInflater.inflate(R.layout.ckb_task_item, null);

		TextView txtPlanItem = (TextView) convertView
				.findViewById(R.id.txt_taskItem);
		TextView txtState = (TextView) convertView.findViewById(R.id.txt_state);

		txtPlanItem.setText(tasklist.get(position).getDisplayName());
		txtState.setText("");

		CheckBox check = (CheckBox) convertView.findViewById(R.id.cb_taskItem);
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
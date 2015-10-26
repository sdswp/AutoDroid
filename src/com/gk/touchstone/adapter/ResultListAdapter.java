package com.gk.touchstone.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.gk.touchstone.R;
import com.gk.touchstone.entity.Plan;
import com.gk.touchstone.utils.Utils;

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

public class ResultListAdapter extends BaseTaskAdapter<Plan> {
	private Context context;
	private List<Map<String, Object>> maplist;
	private Utils us;

	// public HashMap<Integer, Boolean> state = new HashMap<Integer, Boolean>();

	public ResultListAdapter(Context context, List<Map<String, Object>> maplist) {
		this.context = context;
		this.maplist = maplist;
		us = new Utils(context);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return maplist.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return maplist.get(position);
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
		convertView = mInflater.inflate(R.layout.task_result_item, null);

		TextView txtName = (TextView) convertView
				.findViewById(R.id.txt_taskName);
		TextView txtResult = (TextView) convertView
				.findViewById(R.id.txt_result);
		TextView txtReason = (TextView) convertView
				.findViewById(R.id.txt_reason);
		TextView txtTime = (TextView) convertView.findViewById(R.id.txt_time);

		String times = "[" + maplist.get(position).get("taskCount").toString()
				+ "-" + maplist.get(position).get("timeCount").toString() + "]";
		String resultStr = maplist.get(position).get("result").toString();
		txtName.setText(times);
		txtResult.setText(us.setResultColor(resultStr));
		txtReason.setText("Reason: "+maplist.get(position).get("reason").toString());
		txtTime.setText(maplist.get(position).get("date").toString());

		// CheckBox check = (CheckBox)
		// convertView.findViewById(R.id.cb_planItem);
		// check.setOnCheckedChangeListener(new OnCheckedChangeListener() {
		// @Override
		// public void onCheckedChanged(CompoundButton buttonView,
		// boolean isChecked) {
		// // TODO Auto-generated method stub
		// if (isChecked) {
		// state.put(position, isChecked);
		// } else {
		// state.remove(position);
		// }
		// }
		// });
		// check.setChecked((state.get(position) == null ? false : true));
		return convertView;
	}
}
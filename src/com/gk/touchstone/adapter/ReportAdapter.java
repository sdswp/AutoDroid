package com.gk.touchstone.adapter;

import java.util.List;

import com.gk.touchstone.R;
import com.gk.touchstone.entity.Plan;
import com.gk.touchstone.entity.Report;
import com.gk.touchstone.utils.Utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ReportAdapter extends BaseTaskAdapter<Plan> {
	private Context context;
	private List<Report> reportList;
	private Utils us;

	// public HashMap<Integer, Boolean> state = new HashMap<Integer, Boolean>();

	public ReportAdapter(Context context, List<Report> reportList) {
		this.context = context;
		this.reportList = reportList;
		us = new Utils(context);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return reportList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return reportList.get(position);
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

		Report report = reportList.get(position);

		String times = "[" + report.getFailPosition() + "]";
		String resultStr = report.getState();
		txtName.setText(times);
		txtResult.setText(us.setResultColor(resultStr));
		txtReason.setText("Reason: " + report.getReason());
		txtTime.setText(report.getDate());

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
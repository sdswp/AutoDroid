package com.gk.touchstone.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.gk.touchstone.R;
import com.gk.touchstone.core.Constants;
import com.gk.touchstone.entity.Plan;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class PlanAdapter extends BaseAdapter {
	private Context context;
	private List<Plan> planlist;
	private final int serial = 0;
	private final int parallel = 1;
	private Map<String, Object> mapState;

	public HashMap<Integer, Boolean> state = new HashMap<Integer, Boolean>();

	public PlanAdapter(Context context, List<Plan> planlist,
			Map<String, Object> mapState) {
		this.context = context;
		this.planlist = planlist;
		this.mapState = mapState;
	}

	public void setState(Map<String, Object> mapState) {
		this.mapState = mapState;
		// new myAsyncTask().execute(null);
	}


	public void setItemList(List<Plan> list) {
		planlist = list;
		// new myAsyncTask().execute(null);
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
		convertView = mInflater.inflate(R.layout.ckb_plan_item, null);

		TextView txtPlanItem = (TextView) convertView
				.findViewById(R.id.txt_planItem);
		TextView txtState = (TextView) convertView.findViewById(R.id.txt_state);

		int plantype = planlist.get(position).getPlanType();
		String pname = "";
		switch (plantype) {
		case serial:
			pname = Constants.PLAN_SERIAL;
			break;
		case parallel:
			pname = Constants.PLAN_PARALLEL;
			break;
		}

		String[] ts = planlist.get(position).getPlanName().split("\\,");
		txtPlanItem.setText(pname + "：" + ts[0]);

		if (mapState != null) {
			int pid = (Integer) mapState.get("pid");
			String st = mapState.get("state").toString();

			if (planlist.get(position).getId() == pid) {
				txtState.setText(st);
			}
		}
		else{
			txtState.setText("");
		}

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
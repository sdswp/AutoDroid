package com.gk.touchstone.adapter;

import java.util.HashMap;
import java.util.List;

import com.gk.touchstone.R;
import com.gk.touchstone.entity.Case;
import com.gk.touchstone.utils.Utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class TestCaseAdapter extends BaseAdapter {
	private Context context;
	private List<Case> testcases;
	private LayoutInflater listContainer;//视图容器
	private int itemViewResource;//自定义项视图源 
	private Utils us;

	public HashMap<Integer, Boolean> state = new HashMap<Integer, Boolean>();

	public TestCaseAdapter(Context context, List<Case> testcases, int rid) {
		this.context = context;
		this.testcases = testcases;
		us=new Utils(context);

		this.listContainer = LayoutInflater.from(context);//创建视图容器并设置上下文
		this.itemViewResource = rid;
	}

	public void setItemList(List<Case> list) {
		testcases = list;
	}

	@Override
	public int getCount() {
		return testcases.size();
	}

	@Override
	public Object getItem(int position) {
		return testcases.get(position);
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
		TextView displayname = (TextView) convertView
				.findViewById(R.id.txt_displayname);
		TextView txtState = (TextView) convertView
				.findViewById(R.id.txt_state);

		displayname.setText(testcases.get(position).getDisplayName());
		
		CheckBox check = (CheckBox) convertView.findViewById(R.id.cb_testcase);
		
		if (testcases.get(position).getIsJoin() == 0) {
			check.setEnabled(false);
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
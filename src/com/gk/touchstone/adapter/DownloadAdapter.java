package com.gk.touchstone.adapter;

import java.util.HashMap;
import java.util.List;

import com.gk.touchstone.R;
import com.gk.touchstone.entity.Download;
import com.gk.touchstone.entity.WifiAP;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class DownloadAdapter extends BaseAdapter {
	Context context;
	List<Download> listData;
	// 记录checkbox的状态
	public HashMap<Integer, Boolean> state = new HashMap<Integer, Boolean>();

	// 构造函数
	public DownloadAdapter(Context context, List<Download> listData) {
		this.context = context;
		this.listData = listData;
	}

	public void setItemList(List<Download> list) {
		listData = list;
		// new myAsyncTask().execute(null);
	}

	@Override
	public int getCount() {
		return listData.size();
	}

	@Override
	public Object getItem(int position) {
		return listData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	// 重写View
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		LayoutInflater mInflater = LayoutInflater.from(context);
		convertView = mInflater.inflate(R.layout.res_list_item, null);

		TextView ssid = (TextView) convertView.findViewById(R.id.txt_name);
		ssid.setText((String) listData.get(position).getName());
		TextView filesize = (TextView) convertView
				.findViewById(R.id.txt_state);
		filesize.setText((String) listData.get(position).getSize());

		CheckBox check = (CheckBox) convertView.findViewById(R.id.check_aplist);
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
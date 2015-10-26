package com.gk.touchstone.adapter;

import java.util.HashMap;
import java.util.List;

import com.gk.touchstone.R;
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

public class APListAdapter extends BaseAdapter {
	Context context;
	List<WifiAP> listData;	
	public HashMap<Integer, Boolean> state = new HashMap<Integer, Boolean>();		

	public APListAdapter(Context context,List<WifiAP> listData) {
		this.context = context;
		this.listData = listData;
	}
	
	public void setItemList(List<WifiAP> list) {
		listData = list;
		// new myAsyncTask().execute(null);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return listData.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return listData.get(position);
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
		convertView = mInflater.inflate(R.layout.res_list_item, null);
		//ImageView image = (ImageView) convertView.findViewById(R.id.friend_image);
		//image.setBackgroundResource((Integer) listData.get(position).get("friend_image"));
		TextView ssid = (TextView) convertView.findViewById(R.id.txt_name);
		ssid.setText((String) listData.get(position).getSsid());
		TextView txtinfo = (TextView) convertView.findViewById(R.id.txt_info);
		txtinfo.setText((String) listData.get(position).getKeymgmt());
		
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
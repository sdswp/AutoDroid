package com.gk.touchstone.adapter;

import java.util.HashMap;
import java.util.List;
import com.gk.touchstone.R;
import com.gk.touchstone.entity.Task;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

/**
 * 实例化Adapter
 * 
 * @author guohai@live.com
 * 
 */
public class TaskSelectAdapter extends BaseAdapter {
	private Context context;
	private List<Task> tasklist;
	private int itemViewResource;// 自定义项视图源

	public TaskSelectAdapter(Context context, List<Task> tasklist, int resource) {
		this.context = context;
		this.tasklist = tasklist;
		this.itemViewResource = resource;
	}
	
	public void setItemList(List<Task> list) {
		tasklist = list;
		//new myAsyncTask().execute(null);
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
		LayoutInflater mInflater = LayoutInflater.from(context);
		convertView = mInflater.inflate(itemViewResource, null);

		TextView txtTaskItem = (TextView) convertView
				.findViewById(R.id.txt_taskitem);
		TextView txtTaskNum = (TextView) convertView
				.findViewById(R.id.txt_tasknum);
		//EditText etNum = (EditText) convertView.findViewById(R.id.et_taskNum);

		txtTaskItem.setText(tasklist.get(position).getDisplayName());
		int tnum=tasklist.get(position).getTaskCount();
		txtTaskNum.setText(String.valueOf(tnum));
		//etNum.setText(String.valueOf(tasklist.get(position).getTaskCount()));

		return convertView;
	}

}
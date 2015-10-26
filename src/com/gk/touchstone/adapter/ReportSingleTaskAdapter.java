package com.gk.touchstone.adapter;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import com.gk.touchstone.R;
import com.gk.touchstone.entity.Task;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class ReportSingleTaskAdapter extends BaseAdapter {
	private Context context;
	private List<Task> tasklist;
	private LayoutInflater listContainer;// 视图容器
	private int itemViewResource;// 自定义项视图源

	public HashMap<Integer, Boolean> state = new HashMap<Integer, Boolean>();

	public ReportSingleTaskAdapter(Context context, List<Task> tasklist, int rid) {
		this.context = context;
		this.tasklist = tasklist;

		this.listContainer = LayoutInflater.from(context);// 创建视图容器并设置上下文
		this.itemViewResource = rid;
	}

	public long setFileSize(List<Task> list, int position) {
		String filename = list.get(position).getResultFile();
		File fis = new File(context.getFilesDir().getPath() + "//" + filename);
		long fileSize = fis.length();
		return fileSize;
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
		TextView taskName = (TextView) convertView
				.findViewById(R.id.txt_taskName);
		TextView info = (TextView) convertView.findViewById(R.id.txt_info);
		TextView desc = (TextView) convertView.findViewById(R.id.txt_desc);

		taskName.setText(tasklist.get(position).getDisplayName());
		long filesize = setFileSize(tasklist, position);
		info.setText(String.valueOf(filesize));
		desc.setText(tasklist.get(position).getCreateTime());
		// txtState.setText(String.valueOf(tasklist.get(position).getTaskCount()));

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
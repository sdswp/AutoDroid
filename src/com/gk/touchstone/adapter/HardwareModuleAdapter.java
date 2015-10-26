package com.gk.touchstone.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.gk.touchstone.R;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class HardwareModuleAdapter extends BaseAdapter {
	private List<Map<String, Object>> tasklist;
	public HashMap<Integer, Boolean> checkboxState = new HashMap<Integer, Boolean>();
	private Context context;
	private LayoutInflater listContainer;// 视图容器
	private int itemViewResource;// 自定义项视图源

	static class ListItemView { // 自定义控件集合
		public CheckBox cb;
		public TextView taskitem;
	}

	/**
	 * 实例化Adapter
	 * 
	 * @param context
	 * @param data
	 * @param resource
	 */
	public HardwareModuleAdapter(Context context,
			List<Map<String, Object>> data, int resource) {
		this.context = context;
		this.listContainer = LayoutInflater.from(context); // 创建视图容器并设置上下文
		this.itemViewResource = resource;
		this.tasklist = data;
	}

	@Override
	public int getCount() {
		return tasklist.size();
	}

	@Override
	public Object getItem(int position) {
		// return postion;
		return tasklist.get(position);
	}

	@Override
	public long getItemId(int postion) {
		// TODO Auto-generated method stub
		return postion;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ListItemView listItemView = null;

		if (convertView == null) {
			// 获取list_item布局文件的视图
			convertView = listContainer.inflate(this.itemViewResource, null);

			listItemView = new ListItemView();
			// 获取控件对象
			listItemView.cb = (CheckBox) convertView
					.findViewById(R.id.cb_taskItem);
			listItemView.taskitem = (TextView) convertView
					.findViewById(R.id.txt_taskItem);

			listItemView.cb
					.setOnCheckedChangeListener(new OnCheckedChangeListener() {
						@Override
						public void onCheckedChanged(CompoundButton buttonView,
								boolean isChecked) {
							// TODO Auto-generated method stub
							if (isChecked) {
								checkboxState.put(position, isChecked);
							} else {
								checkboxState.remove(position);
							}
						}
					});
			listItemView.cb.setChecked((checkboxState.get(position) == null ? false
					: true));

			// listItemView.flag=
			// (ImageView)convertView.findViewById(R.id.img_ResultType);

			// 设置控件集到convertView
			convertView.setTag(listItemView);
		} else {
			listItemView = (ListItemView) convertView.getTag();
		}

		// 设置文字和图片
		Map<String, Object> task = tasklist.get(position);

		listItemView.taskitem.setText(task.get("text").toString());

		return convertView;
	}

}

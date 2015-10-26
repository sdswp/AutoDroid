package com.gk.touchstone.adapter;

import java.util.HashMap;
import java.util.List;

import com.gk.touchstone.R;
import com.gk.touchstone.entity.Module;
import com.gk.touchstone.entity.Case;

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

public class ModuleHomeAdapter extends BaseAdapter {
	private Context context;
	private List<Module> modules;
	private LayoutInflater listContainer;//视图容器
	private int itemViewResource;//自定义项视图源 

	public HashMap<Integer, Boolean> state = new HashMap<Integer, Boolean>();

	public ModuleHomeAdapter(Context context, List<Module> modules, int rid) {
		this.context = context;
		this.modules = modules;

		this.listContainer = LayoutInflater.from(context);//创建视图容器并设置上下文
		this.itemViewResource = rid;
	}

	public void setItemList(List<Module> list) {
		modules = list;
	}

	@Override
	public int getCount() {
		return modules.size();
	}

	@Override
	public Object getItem(int position) {
		return modules.get(position);
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
				.findViewById(R.id.txt_diy_name);
		ImageView icon = (ImageView) convertView
				.findViewById(R.id.img_icon);

		displayname.setText(modules.get(position).getDisplayName());
		String moduleName="cat_"+modules.get(position).getName();
		int resId = context.getResources().getIdentifier(moduleName, "drawable",
				context.getPackageName());
		icon.setImageResource(resId);

//		CheckBox check = (CheckBox) convertView.findViewById(R.id.cb_testcase);
//		check.setOnCheckedChangeListener(new OnCheckedChangeListener() {
//			@Override
//			public void onCheckedChanged(CompoundButton buttonView,
//					boolean isChecked) {
//				// TODO Auto-generated method stub
//				if (isChecked) {
//					state.put(position, isChecked);
//				} else {
//					state.remove(position);
//				}
//			}
//		});
//		check.setChecked((state.get(position) == null ? false : true));
		return convertView;
	}
}
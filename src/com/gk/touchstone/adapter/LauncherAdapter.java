package com.gk.touchstone.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.gk.touchstone.R;
import com.gk.touchstone.entity.Plan;
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

public class LauncherAdapter extends BaseTaskAdapter<Task> {
	private Context context;
	private List<Task> tasklist;
	private LayoutInflater listContainer;// 视图容器
	private int itemViewResource;// 自定义项视图源
	private List<Map<String, Object>> maplist;

	public HashMap<Integer, Boolean> state = new HashMap<Integer, Boolean>();

	public LauncherAdapter(Context context, List<Task> tasklist, int rid,List<Map<String, Object>> maplist) {
		this.context = context;
		this.tasklist = tasklist;
		this.maplist = maplist;
		this.listContainer = LayoutInflater.from(context);// 创建视图容器并设置上下文
		this.itemViewResource = rid;
	}
	
	public void setState(List<Map<String, Object>> maplist) {
		this.maplist = maplist;
		// new myAsyncTask().execute(null);
	}
	
	@Override
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
		TextView txtPlanItem = (TextView) convertView
				.findViewById(R.id.txt_taskItem);
		TextView txtTimeCount = (TextView) convertView
				.findViewById(R.id.txt_timeCount);
		TextView txtState = (TextView) convertView
				.findViewById(R.id.txt_taskCount);
		

		if(maplist!=null){
			
			for(Map<String, Object> map: maplist){
				int taskid=(Integer) map.get("taskid");
				String taskCount=map.get("taskCount").toString();
				String timeCount=map.get("timeCount").toString();
				if (tasklist.get(position).getId() == taskid) {
					txtState.setText(taskCount);
					txtTimeCount.setText(timeCount);
				}
			}
		}
		else{
			txtState.setText("");
			txtTimeCount.setText("");
		}

		txtPlanItem.setText(tasklist.get(position).getDisplayName());

		if (convertView.findViewById(R.id.cb_taskItem) != null) {
			CheckBox check = (CheckBox) convertView
					.findViewById(R.id.cb_taskItem);
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
		}
		return convertView;
	}
}
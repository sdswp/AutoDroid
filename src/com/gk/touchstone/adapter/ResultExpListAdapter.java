package com.gk.touchstone.adapter;

import java.util.List;
import java.util.Map;

import com.gk.touchstone.core.Constants;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ResultExpListAdapter extends BaseExpandableListAdapter {
	private List<Map<String, Object>> tasknames;
	private Context context;
	private List<List<String[]>> resultContainer;

	public ResultExpListAdapter(List<Map<String, Object>> tasknames,
			List<List<String[]>> resultContainer, Context context) {
		this.tasknames = tasknames;
		this.resultContainer = resultContainer;
		this.context = context;
	}

	@Override
	public int getGroupCount() {
		return tasknames.size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return tasknames.get(groupPosition);
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return resultContainer.get(groupPosition).size();
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return resultContainer.get(groupPosition).get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		LinearLayout ll = new LinearLayout(context);
		ll.setOrientation(0);
		
//		Map<String, Object> maps = (Map<String, Object>) getGroup(groupPosition);
//
//		String htmstr = maps.get("tname").toString() + "<font color='#ff0000'>"
//				+ maps.get("state").toString() + "</font>";

		TextView textView = newTextView();
		textView.setText(Html.fromHtml(diffState(getGroup(groupPosition))));
		textView.setPadding(50, 0, 0, 0);
		ll.addView(textView);

		return ll;
	}
	
	private String diffState(Object obj){
		@SuppressWarnings("unchecked")
		Map<String, Object> maps = (Map<String, Object>) obj;
		String htmstr="";
		String state=maps.get("state").toString();
		if(state.contains(Constants.PASS)){
			htmstr = maps.get("tname").toString() + "<font color='#00FF00'>"
					+ state + "</font>";
		}
		else{
			htmstr = maps.get("tname").toString() + "<font color='#FF0000'>"
					+ state + "</font>";
		}
		return htmstr;
	}

	@SuppressLint("InlinedApi")
	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		LinearLayout ll = new LinearLayout(context);
		ll.setOrientation(0);
		String[] args = (String[]) getChild(groupPosition, childPosition);

		for (int i = 0; i < args.length; i++) {
			TextView tv = newTextView();
			tv.setText(args[i]);
			ll.addView(tv);
			switch (i) {
			case 0:
				tv.setTextColor(Color.rgb(80, 80, 80));
				break;
			case 1:
				tv.setTextColor(Color.rgb(240, 30, 70));
				break;
			case 2:
				tv.setTextColor(Color.rgb(150, 150, 150));
				break;
			}
		}

		return ll;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	private TextView newTextView() {
		AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT, 55);
		TextView textView = new TextView(context);
		textView.setLayoutParams(lp);
		textView.setGravity(Gravity.CENTER_VERTICAL);
		textView.setPadding(10, 0, 0, 0);
		// textView.setText(str);
		textView.setTextSize(15);
		textView.setTextColor(Color.BLACK);
		return textView;
	}

}

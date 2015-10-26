package com.gk.touchstone.adapter;

import java.util.List;

import com.gk.touchstone.entity.Task;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

public class ModuleViewAdapter extends PagerAdapter {
	private List<View> viewList;
	private List<String> titleList;
	
	public ModuleViewAdapter(List<View> listv,List<String> titleList) {
		this.viewList = listv;
		this.titleList=titleList;
	}
	
	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

	@Override
	public int getCount() {
		return viewList.size();
	}

	@Override
	public void destroyItem(ViewGroup container, int position,
			Object object) {
		container.removeView(viewList.get(position));
	}

	@Override
	public int getItemPosition(Object object) {

		return super.getItemPosition(object);
	}

	@Override
	public CharSequence getPageTitle(int position) {

		return titleList.get(position);
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		container.addView(viewList.get(position));
		return viewList.get(position);
	}

}

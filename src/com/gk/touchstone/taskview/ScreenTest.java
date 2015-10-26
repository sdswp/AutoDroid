package com.gk.touchstone.taskview;

import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;

import com.gk.touchstone.R;
import com.gk.touchstone.core.BaseActivity;

public class ScreenTest extends BaseActivity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setBaseContentView();//(R.layout.screentest);
		try {
			((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS); 
		} catch (Exception e) {
//			Log.e(Utils.TEST_SCREEN_TAG, "try to hide keyboard");
		}
		//initValue(); 
	}
}

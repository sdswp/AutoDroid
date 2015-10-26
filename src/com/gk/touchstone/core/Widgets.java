package com.gk.touchstone.core;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Widgets {
	// private int WidgetNumber = 0;
	private Context context;

	public Widgets(Context context) {
		// this.WidgetNumber = WidgetNumber;
		this.context = context;
	}

	public View getView(int WidgetNumber, String value) {
		if (WidgetNumber == 0) {
			EditText et = new EditText(context);
			et.setText(value);
			return et;
		} else if (WidgetNumber == 1) {
			TextView tv = new TextView(context);
			tv.setText(value);
			return tv;
		} else if (WidgetNumber == 2) {
			Button btn = new Button(context);
			btn.setText(value);
			return btn;
		} else {
			return null;
		}
	}

}

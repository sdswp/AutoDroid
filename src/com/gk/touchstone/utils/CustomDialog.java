package com.gk.touchstone.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.gk.touchstone.R;
import com.gk.touchstone.entity.WifiAP;

public class CustomDialog {
	private Context context;

	public CustomDialog(Context context) {
		this.context = context;
	}

	public void showDialog() {
		LayoutInflater factory = LayoutInflater.from(context);
		final View textEntryView = factory.inflate(R.layout.stability_edit_dialog,
				null);
		final EditText etCallNumber = (EditText) textEntryView
				.findViewById(R.id.et_callNumber);
		AlertDialog.Builder ad1 = new AlertDialog.Builder(context);
		ad1.setTitle("编辑AP");
		ad1.setIcon(android.R.drawable.ic_dialog_info);

		etCallNumber.setText("");
		
		ad1.setView(textEntryView);
		ad1.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int i) {
				String ssid = etCallNumber.getText().toString();

				WifiAP wifiap = new WifiAP();
				wifiap.setSsid(ssid);
			}
		});
		ad1.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int i) {
				dialog.cancel();
			}
		});
		ad1.show();// 显示对话框

	}
}

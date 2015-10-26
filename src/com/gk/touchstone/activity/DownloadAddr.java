package com.gk.touchstone.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.gk.touchstone.R;
import com.gk.touchstone.adapter.DownloadAdapter;
import com.gk.touchstone.entity.Download;
import com.gk.touchstone.entity.Download;
import com.gk.touchstone.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class DownloadAddr extends Activity {
	private TextView TitleView, txtFileExist;
	private View manageAP;
	private Button btnok, btnAdd, btnDel, btnMatch, btnDownload, btnSelect;
	private ListView resList;
	private Gson gson;
	private Utils us;
	private DownloadAdapter adapter;
	private List<Download> downloadList = null;
	private String fileName = "downloadurl.json";
	private final int addAP = -1;
	private int resultOk;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.res_list);

		initView();
	}

	private void initView() {
		gson = new Gson();
		us = new Utils(this);

		Intent intent = getIntent();
		resultOk = intent.getIntExtra("RESULT_OK", 0);

		TitleView = (TextView) findViewById(R.id.txt_title);
		txtFileExist = (TextView) findViewById(R.id.txt_fileExist);
		btnok = (Button) findViewById(R.id.title_rbtn);
		btnAdd = (Button) findViewById(R.id.btn_ap_add);
		btnDel = (Button) findViewById(R.id.btn_ap_del);
		manageAP = (View) findViewById(R.id.ln_manageAP);
		btnMatch = (Button) findViewById(R.id.btn_ap_match);
		btnDownload = (Button) findViewById(R.id.btn_ap_download);

		btnSelect = (Button) findViewById(R.id.btn_select);

		TitleView.setText("下载地址列表");
		btnok.setText(" 管理 ");

		resList = (ListView) findViewById(R.id.list_res);
		downloadList = getDownloadUrl(fileName);

		adapter = new DownloadAdapter(this, downloadList);
		resList.setAdapter(adapter);

		btnok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (manageAP.getVisibility() == 8) {
					manageAP.setVisibility(View.VISIBLE);
				} else {
					manageAP.setVisibility(View.GONE);
				}
			}
		});

		btnAdd.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				showAddDialog(addAP);
			}
		});

		btnSelect.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				String dname = getSelectedItem().get(0).getName();
				String durl = getSelectedItem().get(0).getUrl();
				String[] downloadInfo = new String[] { dname, durl };

				Intent data = new Intent();
				data.putExtra("selectFile", downloadInfo);
				setResult(resultOk, data);
				System.out.println("resultOk" + resultOk);
				finish();
			}
		});

		btnDel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// downloadList
				Iterator<Download> itr = downloadList.iterator();
				while (itr.hasNext()) {
					Download t = itr.next();
					// getSelectedItem;
					String tssid = t.getName();
					for (Download ap : getSelectedItem()) {
						if (ap.getName().equals(tssid)) {
							itr.remove();
							break;
						}
					}
				}
				String tojson = gson.toJson(downloadList);

				if (us.writeDataFile(fileName, tojson, Context.MODE_PRIVATE)) {
					downloadList = getDownloadUrl(fileName);
				}
				adapter.setItemList(downloadList);
				adapter.notifyDataSetChanged();

			}
		});

	}

	private List<Download> getDownloadUrl(String filename) {
		String jsonAP = us.readDataFile(filename);
		List<Download> downloads = gson.fromJson(jsonAP,
				new TypeToken<List<Download>>() {
				}.getType());

		return downloads;
	}

	private List<Download> getSelectedItem() {
		List<Download> newDownloadList = new ArrayList<Download>();
		HashMap<Integer, Boolean> cbState = adapter.state;

		for (int j = 0; j < adapter.getCount(); j++) {
			if (cbState.get(j) != null) {
				@SuppressWarnings("unchecked")
				Download download = (Download) adapter.getItem(j);

				newDownloadList.add(download);
			}

		}
		return newDownloadList;
	}

	protected void showAddDialog(final int pos) {
		LayoutInflater factory = LayoutInflater.from(this);
		final View textEntryView = factory.inflate(R.layout.ap_edit_dialog,
				null);
		final EditText etSSID = (EditText) textEntryView
				.findViewById(R.id.etSSID);
		final EditText etPSK = (EditText) textEntryView
				.findViewById(R.id.etPSK);
		final EditText etKeymgmt = (EditText) textEntryView
				.findViewById(R.id.etKeymgmt);
		AlertDialog.Builder ad1 = new AlertDialog.Builder(this);
		ad1.setTitle("编辑AP");
		ad1.setIcon(android.R.drawable.ic_dialog_info);
		if (pos == -1) {
			etSSID.setText("");
			etPSK.setText("");
			etKeymgmt.setText("");
		} else {
			etSSID.setText(downloadList.get(pos).getName().toString());
			etPSK.setText(downloadList.get(pos).getUrl().toString());
			etKeymgmt.setText(downloadList.get(pos).getSize().toString());
		}
		ad1.setView(textEntryView);
		ad1.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int i) {
				String ssid = etSSID.getText().toString();
				String psk = etPSK.getText().toString();
				String keymgmt = etKeymgmt.getText().toString();
				if (pos == addAP) {
					Download download = new Download();
					download.setName(ssid);
					download.setUrl(psk);
					download.setSize(keymgmt);
					downloadList.add(download);
				} else {
					downloadList.get(pos).setName(ssid);
					downloadList.get(pos).setUrl(psk);
					downloadList.get(pos).setSize(keymgmt);
				}

				String tojson = gson.toJson(downloadList);

				if (us.writeDataFile(fileName, tojson, Context.MODE_PRIVATE)) {
					downloadList = getDownloadUrl(fileName);
				}
				adapter.setItemList(downloadList);
				adapter.notifyDataSetChanged();

			}
		});
		ad1.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int i) {

			}
		});
		ad1.show();// 显示对话框

	}

}

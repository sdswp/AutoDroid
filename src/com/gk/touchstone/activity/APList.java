package com.gk.touchstone.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.gk.touchstone.R;
import com.gk.touchstone.adapter.APListAdapter;
import com.gk.touchstone.entity.WifiAP;
import com.gk.touchstone.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.wifi.WifiManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class APList extends Activity {
	private TextView TitleView, txtFileExist;
	private View manageAP;
	private Button btnok, btnAdd, btnDel, btnMatch, btnDownload;
	private ListView aplv;
	private List<WifiAP> APList = null;
//	private List<WifiAP> APListNew = new ArrayList<WifiAP>();
//	private ArrayList<HashMap<String, Object>> wifiScanResult = new ArrayList<HashMap<String, Object>>();
//	private ArrayList<HashMap<String, Object>> sameArr = new ArrayList<HashMap<String, Object>>();

	private APListAdapter adapter;
	private Utils us;
	private Gson gson;
	private final int addAP = -1;

	private WifiManager wifiManager = null;
	private String fileName = "aplist.json";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.res_list);
		initView();
	}

	private void initView() {
		us = new Utils(this);
		gson = new Gson();
		
		//Intent intent=getIntent();
		
		APList = getAPList(fileName);

		TitleView = (TextView) findViewById(R.id.txt_title);
		txtFileExist = (TextView) findViewById(R.id.txt_fileExist);
		btnok = (Button) findViewById(R.id.title_rbtn);
		btnAdd = (Button) findViewById(R.id.btn_ap_add);
		btnDel = (Button) findViewById(R.id.btn_ap_del);
		manageAP = (View) findViewById(R.id.ln_manageAP);
		btnMatch = (Button) findViewById(R.id.btn_ap_match);
		btnDownload = (Button) findViewById(R.id.btn_ap_download);
		TitleView.setText("AP列表");
		btnok.setText(" 管理 ");
		aplv = (ListView) findViewById(R.id.list_res);

		wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
		wifiManager.setWifiEnabled(true);

		showlist(getAPList(fileName));

		aplv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				showAddDialog(arg2);
			}

		});

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

		btnDel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// APList
				Iterator<WifiAP> itr = APList.iterator();
				while (itr.hasNext()) {
					WifiAP t = itr.next();
					// getSelectedItem;
					String tssid = t.getSsid();
					for (WifiAP ap : getSelectedItem()) {
						if (ap.getSsid().equals(tssid)) {
							itr.remove();
							break;
						}
					}
				}
				String tojson = gson.toJson(APList);

				if (us.writeDataFile(fileName, tojson, Context.MODE_PRIVATE)) {
					APList = getAPList(fileName);
				}
				adapter.setItemList(APList);
				adapter.notifyDataSetChanged();

			}
		});

		btnDownload.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
			
		});

		btnMatch.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
//				if (wifiScanResult != null) {
//					wifiScanResult = new ArrayList<HashMap<String, Object>>();
//				}
//				if (APListNew != null) {
//					APListNew = new ArrayList<WifiAP>();
//				}
//
//				List<ScanResult> scanResults = wifiManager.getScanResults();
//				if (scanResults != null) {
//					for (ScanResult scanResult : scanResults) {
//						HashMap<String, Object> map = new HashMap<String, Object>();
//						if (scanResult.level != 0) {
//							map.put("SSID", scanResult.SSID);
//							map.put("level", scanResult.level);
//							map.put("calculateSignalLevel", WifiManager
//									.calculateSignalLevel(scanResult.level, 4));
//							map.put("BSSID", scanResult.BSSID);
//							wifiScanResult.add(map);
//						}
//					}
//				}
//
//				System.out.println("APListNew" + APListNew.size()
//						+ "   wifiScanResult" + wifiScanResult.size());
//
//				// aplist.xml和扫描列表匹配
//				for (WifiAP wifiap : APList) {
//					for (int i = 0; i < wifiScanResult.size(); i++) {
//						// if (wifiScanResult.get(i).containsValue(wifiap.ssid))
//						// {
//						// APListNew.add(wifiap);
//						// break;
//						// }
//					}
//				}
//
//				showlist(APListNew);
//				// writeAPlist(APListNew, newFilename);

			}
		});

	}

	private List<WifiAP> getSelectedItem() {
		List<WifiAP> newaplist = new ArrayList<WifiAP>();
		HashMap<Integer, Boolean> cbState = adapter.state;

		for (int j = 0; j < adapter.getCount(); j++) {
			if (cbState.get(j) != null) {
				WifiAP wifiap = (WifiAP) adapter.getItem(j);
				newaplist.add(wifiap);
			}

		}
		return newaplist;
	}

	private void showlist(List<WifiAP> aps) {
		if (aps.size() > 0) {
			adapter = new APListAdapter(this, aps);
			aplv.setAdapter(adapter);
			txtFileExist.setVisibility(View.GONE);
		} else {
			txtFileExist.setVisibility(View.VISIBLE);
		}
	}


	@Override
	protected void onDestroy() {
		super.onDestroy();
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
			etSSID.setText(APList.get(pos).getSsid().toString());
			etPSK.setText(APList.get(pos).getPsk().toString());
			etKeymgmt.setText(APList.get(pos).getKeymgmt().toString());
		}
		ad1.setView(textEntryView);
		ad1.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int i) {
				String ssid = etSSID.getText().toString();
				String psk = etPSK.getText().toString();
				String keymgmt = etKeymgmt.getText().toString();
				if (pos == addAP) {
					WifiAP wifiap = new WifiAP();
					wifiap.setSsid(ssid);
					wifiap.setPsk(psk);
					wifiap.setKeymgmt(keymgmt);
					APList.add(wifiap);
				} else {
					APList.get(pos).setSsid(ssid);
					APList.get(pos).setPsk(psk);
					APList.get(pos).setKeymgmt(keymgmt);
				}

				String tojson = gson.toJson(APList);

				if (us.writeDataFile(fileName, tojson, Context.MODE_PRIVATE)) {
					APList = getAPList(fileName);
				}
				adapter.setItemList(APList);
				adapter.notifyDataSetChanged();

			}
		});
		ad1.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int i) {

			}
		});
		ad1.show();// 显示对话框

	}

	private List<WifiAP> getAPList(String filename) {
		String jsonAP = us.readDataFile(filename);
		List<WifiAP> APList = gson.fromJson(jsonAP,
				new TypeToken<List<WifiAP>>() {
				}.getType());

		return APList;
	}

}

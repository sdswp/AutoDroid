package com.gk.touchstone.activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.gk.touchstone.R;
import com.gk.touchstone.utils.Utils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class SDCardExplorer extends Activity {

	protected static final String TAG = null;
	private TextView tvpath;
	private ListView lvFiles;
	private Button btnParent, titlebarBtnRight;
	private ArrayList<HashMap<String, Object>> SDCardAPList = new ArrayList<HashMap<String, Object>>();
	private int resultOk;
	private Utils us;
	// 记录当前的父文件夹
	File currentParent;

	// 记录当前路径下的所有文件夹的文件数组
	File[] currentFiles;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sdcard);

		us = new Utils(this);

		Intent intent = getIntent();
		resultOk = intent.getIntExtra("RESULT_OK", 0);

		TextView TitleView = (TextView) findViewById(R.id.txt_title);
		TitleView.setText("文件浏览");
		titlebarBtnRight = (Button) findViewById(R.id.title_rbtn);
		titlebarBtnRight.setText("关闭");

		lvFiles = (ListView) this.findViewById(R.id.files);

		tvpath = (TextView) this.findViewById(R.id.tvpath);
		btnParent = (Button) this.findViewById(R.id.btnParent);

		// 获取系统的SDCard的目录
		String dirs = Environment.getExternalStorageDirectory()
				.getAbsolutePath();
		File root = new File(dirs);
		// 如果SD卡存在的话
		if (root.exists()) {

			currentParent = root;
			currentFiles = root.listFiles();
			inflateListView(currentFiles);

		}

		lvFiles.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view,
					int position, long id) {

				// 获取用户点击的文件夹 下的所有文件
				File[] tem = currentFiles[position].listFiles();

				if (tem == null || tem.length == 0) {
					// if (tem == null || tem.length == 0) {
					if (currentFiles[position].isFile()) {
						if (SDCardAPList != null) {
							SDCardAPList = new ArrayList<HashMap<String, Object>>();
						}
						try {
							String fname = currentParent.getCanonicalPath()
									+ "/" + currentFiles[position].getName();
							// String[] strs = new String[] {
							// currentParent.getCanonicalPath(),
							// currentFiles[position].getName() };
							Intent data = new Intent();
							data.putExtra("selectFile", fname);
							setResult(resultOk, data);
							finish();
						} catch (Exception e) {
							Log.e(TAG, e.getMessage());
						}

					} else {
						Toast.makeText(SDCardExplorer.this,
								"当前路径不可访问或者该路径下没有文件", Toast.LENGTH_LONG).show();
					}
				} else {
					// 获取用户单击的列表项对应的文件夹，设为当前的父文件夹
					currentParent = currentFiles[position];
					// 保存当前的父文件夹内的全部文件和文件夹
					currentFiles = tem;
					// 再次更新ListView
					inflateListView(currentFiles);
				}

			}
		});

		// 获取上一级目录
		btnParent.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					if (!currentParent.getCanonicalPath().equals(us.getSDCardPath())) {
						// 获取上一级目录
						currentParent = currentParent.getParentFile();
						// 列出当前目录下的所有文件
						currentFiles = currentParent.listFiles();
						// 再次更新ListView
						inflateListView(currentFiles);
					}
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		});

	}

	/**
	 * 根据文件夹填充ListView
	 * 
	 * @param files
	 */
	private void inflateListView(File[] files) {

		List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < files.length; i++) {
			Map<String, Object> listItem = new HashMap<String, Object>();
			if (files[i].isDirectory()) {
				// 如果是文件夹就显示的图片为文件夹的图片
				listItem.put("icon", R.drawable.folder);
			} else {
				listItem.put("icon", R.drawable.file);
			}
			// 添加一个文件名称
			listItem.put("filename", files[i].getName());

			listItems.add(listItem);
		}

		// 定义一个SimpleAdapter
		SimpleAdapter adapter = new SimpleAdapter(SDCardExplorer.this,
				listItems, R.layout.sdcard_item, new String[] { "filename",
						"icon", "modify" }, new int[] { R.id.file_name,
						R.id.icon });

		// 填充数据集
		lvFiles.setAdapter(adapter);

		try {
			tvpath.setText("sd卡路径：" + currentParent.getCanonicalPath());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
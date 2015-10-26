package com.gk.touchstone.taskview;

import com.gk.touchstone.R;
import com.gk.touchstone.activity.SDCardExplorer;
import com.gk.touchstone.core.BaseActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class SdcardCopy extends BaseActivity {
	private EditText fileSelect;
	private int RESULT_OK = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setBaseContentView();//(R.layout.storage_copyinsd);

		initView();
	}

	private void initView() {
//		fileSelect = (EditText) findViewById(R.id.sdcardCopyFile);
//		Button openfile = (Button) findViewById(R.id.btn_fileBrowser);
//		openfile.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				Intent intent = new Intent(SdcardCopy.this,
//						SDCardExplorer.class);
//				Bundle bundle = new Bundle();
//				bundle.putInt("RESULT_OK", RESULT_OK);
//				intent.putExtras(bundle);
//				startActivityForResult(intent, 0);
//			}
//		});
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			String mFile = data.getStringExtra("selectFile");
			fileSelect.setText(mFile);
		}
	}

}

package com.gk.touchstone.network;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.gk.touchstone.GkApplication;
import com.gk.touchstone.R;
import com.gk.touchstone.adapter.PlanAdapter;
import com.gk.touchstone.core.TaskManager;
import com.gk.touchstone.entity.Plan;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

public class AsyncGetJson extends AsyncTask<String, Integer, String> {
	private TextView mTvProgress;
	private ProgressBar mPgBar;
	private AlertDialog alert;
	private PlanAdapter adapter;
	private TaskManager tm;
	private GkApplication app;
	
	public AsyncGetJson(Context context, PlanAdapter adapter) {
		this.adapter = adapter;
		tm=new TaskManager(context);
		app = (GkApplication) context.getApplicationContext();
		
		LayoutInflater mInflater = LayoutInflater.from(context);
		View convertView = mInflater.inflate(R.layout.downloading,
				null);

		mTvProgress = (TextView) convertView
				.findViewById(R.id.tv_filebrowser_uploading);
		mPgBar = (ProgressBar) convertView
				.findViewById(R.id.pb_filebrowser_uploading);

		AlertDialog.Builder builder = new AlertDialog.Builder(context).setView(convertView);
		alert = builder.create();
		alert.show();
	}

	@Override
	protected void onPostExecute(String result) {
		// mTvProgress.setText(result);
		// txtv.setText(result);
		List<Plan> list = tm.getSingleNetPlan(result);
		if (list != null) {
			adapter.setItemList(list);
			adapter.notifyDataSetChanged();

			app.setNetJson(result);
		}
		alert.cancel();
	}

	@Override
	protected void onPreExecute() {
		mTvProgress.setText("loading...");
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		mPgBar.setProgress(values[0]);
		mTvProgress.setText("loading..." + values[0] + "%");
	}

	@Override
	protected String doInBackground(String... params) {

		try {
			HttpClient client = new DefaultHttpClient();
			// params[0] 代表连接的url
			HttpGet get = new HttpGet(params[0]);
			HttpResponse response = client.execute(get);
			HttpEntity entity = response.getEntity();
			long length = entity.getContentLength();
			InputStream is = entity.getContent();
			String s = null;
			if (is != null) {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				byte[] buf = new byte[128];
				int ch = -1;
				int count = 0;
				while ((ch = is.read(buf)) != -1) {
					baos.write(buf, 0, ch);
					count += ch;
					if (length > 0) {
						publishProgress((int) ((count / (float) length) * 100));
					}
				}
				byte data[] = baos.toByteArray(); 
				s = new String(data,"utf-8"); 
				
			}
			return s;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
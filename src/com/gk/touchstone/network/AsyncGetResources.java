package com.gk.touchstone.network;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.gk.touchstone.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;

public class AsyncGetResources extends AsyncTask<String, Integer, String> {
	private ProgressDialog pdialog;
	private Context context;
	private String[] jsonstr;
	private Dialog alertDialog;

	public AsyncGetResources(Context context) {
		this.context = context;
		pdialog = new ProgressDialog(context, R.style.ProgressDialogCustom);
		pdialog.setMax(100);
		pdialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pdialog.setMessage("正在获取数据...");
		pdialog.show();
	}

	@Override
	protected String doInBackground(String... params) {
		try {
			HttpClient client = new DefaultHttpClient();
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
				while ((ch = is.read(buf)) != -1) {
					baos.write(buf, 0, ch);
					Thread.sleep(100);
				}
				s = new String(baos.toByteArray());
			}
			return s;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected void onPostExecute(String result) {
		try {
			if (!result.equals("")) {

				JSONArray resultArray = new JSONArray(result);
				// resultArray.optJSONObject(0).getString("Name");
				jsonstr = new String[resultArray.length()];
				for (int i = 0; i < resultArray.length(); i++) {
					JSONObject resultObj = resultArray.optJSONObject(i);

					jsonstr[i] = resultObj.getString("Name");
				}
			}
			alertDialog = null;
			alertDialog = new AlertDialog.Builder(context)
					.setTitle("选择下载文件")
					.setItems(jsonstr, new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							//EtDownloadurl.setText(jsonstr[which]);
						}
					})
					.setNegativeButton("取消",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
								}
							}).create();
			alertDialog.show();

		} catch (JSONException e) {
			e.printStackTrace();
		}

		pdialog.dismiss();
	}

}

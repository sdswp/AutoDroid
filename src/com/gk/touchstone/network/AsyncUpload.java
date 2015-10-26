package com.gk.touchstone.network;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import org.apache.http.protocol.HTTP;

import com.gk.touchstone.R;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

public class AsyncUpload extends AsyncTask<String, Integer, String> {
	private TextView mTvProgress;
	private ProgressBar mPgBar;
	private AlertDialog alert;

	public AsyncUpload(Context context) {
		
		LayoutInflater mInflater = LayoutInflater.from(context);
		View convertView = mInflater.inflate(R.layout.filebrowser_uploading,
				null);

		mTvProgress = (TextView) convertView
				.findViewById(R.id.tv_filebrowser_uploading);
		mPgBar = (ProgressBar) convertView
				.findViewById(R.id.pb_filebrowser_uploading);

		AlertDialog.Builder builder = new AlertDialog.Builder(context)
				.setTitle("上传进度").setView(convertView);
		alert = builder.create();
		alert.show();
	}

	@Override
	protected void onPostExecute(String result) {
		mTvProgress.setText(result);
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
		String filePath = params[0];
		String uploadUrl = params[1];
		String end = "\r\n";
		String twoHyphens = "--";
		String boundary = "******";
		try {
			URL url = new URL(uploadUrl);
			HttpURLConnection httpURLConnection = (HttpURLConnection) url
					.openConnection();
			httpURLConnection.setDoInput(true);
			httpURLConnection.setDoOutput(true);
			httpURLConnection.setUseCaches(false);
			httpURLConnection.setRequestMethod("POST");
			httpURLConnection.setConnectTimeout(6 * 1000);
			httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
			httpURLConnection.setRequestProperty("Charset", HTTP.UTF_8);
			httpURLConnection.setRequestProperty("Content-Type",
					"multipart/form-data;boundary=" + boundary);

			DataOutputStream dos = new DataOutputStream(
					httpURLConnection.getOutputStream());
			dos.writeBytes(twoHyphens + boundary + end);
			dos.writeBytes("Content-Disposition: form-data; name=\"file\"; filename=\""
					+ filePath.substring(filePath.lastIndexOf("/") + 1)
					+ "\""
					+ end);
			dos.writeBytes(end);

			FileInputStream fis = new FileInputStream(filePath);
			long total = fis.available();
			//String totalstr = String.valueOf(total);
			//Log.d("文件大小", totalstr);
			byte[] buffer = new byte[8192]; // 8k
			int count = 0;
			int length = 0;
			while ((count = fis.read(buffer)) != -1) {
				dos.write(buffer, 0, count);
				length += count;
				publishProgress((int) ((length / (float) total) * 100));
			}
			fis.close();
			dos.writeBytes(end);
			dos.writeBytes(twoHyphens + boundary + twoHyphens + end);
			dos.flush();
			
			int responseCode = httpURLConnection.getResponseCode();  
		    InputStream is = null;  
		    if (responseCode == 200) {  
		        is = new BufferedInputStream(httpURLConnection.getInputStream());  
		    } else {  
		    	is = new BufferedInputStream(httpURLConnection.getErrorStream());  
		    }
		    String result = readInStream(is); 
//			InputStream is = new BufferedInputStream(httpURLConnection.getInputStream());
//			InputStream is = httpURLConnection.getInputStream();
//			InputStreamReader isr = new InputStreamReader(is, HTTP.ASCII);
//			BufferedReader br = new BufferedReader(isr);
//			@SuppressWarnings("unused")
//			String result = br.readLine();
			dos.close();
			is.close();

			return "上传成功";
		} catch (Exception e) {
			e.printStackTrace();
			return "上传失败";
		}
	}
	
	private String readInStream(InputStream in) {  
	    Scanner scanner = new Scanner(in).useDelimiter("\\A");  
	    return scanner.hasNext() ? scanner.next() : "";  
	} 

}
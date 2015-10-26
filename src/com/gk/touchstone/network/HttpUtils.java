package com.gk.touchstone.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.gk.touchstone.R;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Message;

public class HttpUtils {
	private Context context;

	public HttpUtils(Context context) {
		this.context = context;
	}

	/**
	 * 判断网络连接
	 * 
	 * @return
	 */
	public boolean isConnect() {
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity != null) {
			NetworkInfo info = connectivity.getActiveNetworkInfo();
			if (info != null && info.isConnected()) {
				if (info.getState() == NetworkInfo.State.CONNECTED) {
					return true;
				}
			}
		}
		return false;
	}

	public String JSONTokener(String in) {
		// consume an optional byte order mark (BOM) if it exists
		if (in != null && in.startsWith("\ufeff")) {
			in = in.substring(1);
		}
		return in;
	}

	public JSONObject getJsonObject(String Url) {
		HttpClient client = new DefaultHttpClient();
		StringBuilder sb = new StringBuilder();
		String js = null;
		JSONObject son = null;
		HttpGet myget = new HttpGet(Url);
		try {
			HttpParams params = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(params, 8000);
			HttpResponse response = client.execute(myget);
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent()));
			for (String s = reader.readLine(); s != null; s = reader.readLine()) {
				sb.append(s);
			}
			js = sb.toString();
			son = new JSONObject(js);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return null;
		}
		return son;
	}

	public void postJson(final String uri,final Map<String, String> params, String jsonStr) {
		new Thread() {
			public void run() {
				byte[] reqData = getRequestData(params).toString().getBytes();
				try {
					HttpClient httpclient = new DefaultHttpClient();
					HttpPost httppost = new HttpPost(uri);
					// 添加http头信息
					// httppost.addHeader("Authorization", "your token"); //
					// 认证token
					httppost.addHeader("Content-Type", "application/json");
					httppost.addHeader("User-Agent", "imgfornote");
					// http post的json数据格式： {"name": "your name","parentId":
					// "id_of_parent"}
					JSONObject obj = new JSONObject();
					obj.put("name", "your name");
					obj.put("parentId", "your parentid");
					httppost.setEntity(new StringEntity(obj.toString()));
					HttpResponse response;
					response = httpclient.execute(httppost);
					// 检验状态码，如果成功接收数据
					int code = response.getStatusLine().getStatusCode();
					if (code == 200) {
						String rev = EntityUtils.toString(response.getEntity());// 返回json格式：
																				// {"id":
																				// "27JpL~j4vsL0LX00E00005","version":
																				// "abc"}
						obj = new JSONObject(rev);
						String id = obj.getString("id");
						String version = obj.getString("version");
					}
				} catch (ClientProtocolException e) {
				} catch (IOException e) {
				} catch (Exception e) {
				}

			}
		}.start();
	}
	

	public static StringBuffer getRequestData(Map<String, String> params) {
		StringBuffer stringBuffer = new StringBuffer(); // 存储封装好的请求体信息
		try {
			for (Map.Entry<String, String> entry : params.entrySet()) {
				stringBuffer.append(entry.getKey()).append("=")
						.append(URLEncoder.encode(entry.getValue(), "utf-8"))
						.append("&");
			}
			stringBuffer.deleteCharAt(stringBuffer.length() - 1); // 删除最后的一个"&"
		} catch (Exception e) {
			e.printStackTrace();
		}
		return stringBuffer;
	}

}

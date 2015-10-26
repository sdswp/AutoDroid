package com.gk.touchstone.utils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.gk.touchstone.R;
import com.gk.touchstone.core.Constants;
import com.gk.touchstone.entity.Task;
import com.gk.touchstone.network.AsyncUpload;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class Utils {
	public Context context;

	public Utils(Context context) {
		this.context = context;
	}

	@SuppressLint("SimpleDateFormat")
	public static String formatDateTime(Date date, String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(date);
	}

	/**
	 * 获取系统当前时间
	 * 
	 * @return
	 */
	public String getCurrentTime() {
		Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
		return formatDateTime(curDate, "yyyy-MM-dd HH:mm:ss");
	}

	/**
	 * 从UUID中提取一个随机数
	 * 
	 * @return
	 */
	public int getRandomInt() {
		UUID uuid = UUID.randomUUID();
		String result = "";

		Pattern p = Pattern.compile("[0-9]");
		Matcher m = p.matcher(uuid.toString());

		while (m.find()) {
			result += m.group();
		}
		String s = result.substring(0, 4);
		return Integer.parseInt(s);
	}

	/**
	 * 判断String字符串是否为Null或空
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isNullOrEmpty(String str) {
		return (str == null || str.trim().length() == 0);
	}

	/**
	 * 拼接一个web地址
	 * 
	 * @param sub
	 * @return
	 */
	public String getServerUrl(String sub) {
		String subStr = "";
		if (!isNullOrEmpty(sub)) {
			subStr = sub;
		}
		String serv = "http://"
				+ context.getResources().getString(R.string.server_addr).trim()
				+ "/" + subStr;
		return serv;
	}

	private void initToast(String sname, String value) {
		Activity activity = (Activity) context;
		View toastRoot = activity.getLayoutInflater().inflate(R.layout.toast,
				null);
		TextView msg = (TextView) toastRoot.findViewById(R.id.msg_cname);
		TextView defaultStr = (TextView) toastRoot
				.findViewById(R.id.msg_default);
		msg.setText(sname);

		defaultStr.setText(value);

		Toast toastStart = new Toast(activity);
		toastStart.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
		toastStart.setDuration(Toast.LENGTH_LONG);
		toastStart.setView(toastRoot);
		toastStart.show();
	}

	/**
	 * 自定义Toast，描述文字来自ResourcesID
	 * 
	 * @param sname
	 * @param tips
	 */
	public void myToast(String sname, int tips) {
		String tipsStr = context.getResources().getString(tips);
		initToast(sname, tipsStr);
	}

	/**
	 * 自定义Toast
	 * 
	 * @param sname
	 * @param tips
	 */
	public void myToast(String sname, String tips) {
		initToast(sname, tips);
	}

	/**
	 * 获取设备ID
	 * 
	 * @return
	 */
	public String getDeviceId() {
		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		String DEVICE_ID = tm.getDeviceId();
		return DEVICE_ID;
	}

	/**
	 * 取得文件大小
	 * 
	 * @param file
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("resource")
	public long getFileSizes(File file) throws Exception {
		long s = 0;
		if (file.exists()) {
			FileInputStream fis = new FileInputStream(file);
			s = fis.available();
		} else {
			System.out.println("文件不存在");
		}
		return s;
	}

	/**
	 * 文件尺寸转换
	 * 
	 * @param fileS
	 * @return
	 */
	public String FormetFileSize(long fileS) {
		DecimalFormat df = new DecimalFormat("#.00");
		String fileSizeString = "";
		if (fileS < 1024) {
			fileSizeString = df.format((double) fileS) + "B";
		} else if (fileS < 1048576) {
			fileSizeString = df.format((double) fileS / 1024) + "K";
		} else if (fileS < 1073741824) {
			fileSizeString = df.format((double) fileS / 1048576) + "M";
		} else {
			fileSizeString = df.format((double) fileS / 1073741824) + "G";
		}
		return fileSizeString;
	}

	/**
	 * 获取SD卡路径
	 * 
	 * @return
	 */
	public static String getSDCardPath() {
		String dirs = "";
		String state = Environment.getExternalStorageState();
		boolean sdCardExist = state
				.equals(android.os.Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
		if (sdCardExist) {
			dirs = Environment.getExternalStorageDirectory().getAbsolutePath();
		} else {
			Log.i("SDCard",
					"SD Card Doesn't exist on the phone or is not correctly mounted or without write access!");
			return null;
		}

		return dirs;
	}

	// public String getSDCardFolderPath() {
	// String apkname = "/"
	// + context.getResources().getString(R.string.apk_name) + "/";
	// String dirs = getSDCardPath() + apkname;
	// return dirs;
	// }

	/**
	 * 获取项目自动生成的文件夹路径，末尾带斜杠
	 * 
	 * @return
	 */
	public String getAppSDCardPath() {
		String apkname = context.getResources().getString(R.string.apk_name);
		String dirs = getSDCardPath() + File.separator + apkname
				+ File.separator;
		return dirs;
	}

	public void createFolder() {
		File file = new File(getAppSDCardPath());
		if (!file.exists()) {
			try {
				// 按照指定的路径创建文件夹
				file.mkdirs();
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}

	public String fromFile(String filename) throws IOException {
		File f = new File(getAppSDCardPath() + filename);
		InputStream is = new FileInputStream(f);
		byte[] bs = new byte[is.available()];
		is.read(bs);
		is.close();
		return new String(bs);
	}

	public String getRawFile(int rawid) {
		String line = "";
		String Result = "";
		try {
			InputStream is = context.getResources().openRawResource(rawid);
			InputStreamReader isr = new InputStreamReader(is, "utf8");
			BufferedReader bufReader = new BufferedReader(isr);

			while ((line = bufReader.readLine()) != null) {
				Result += line;
			}
			isr.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Result;
	}

	public String getFileFromAssets(String fileName) {
		String line = "";
		String Result = "";
		try {
			InputStreamReader isr = new InputStreamReader(context
					.getResources().getAssets().open(fileName));

			BufferedReader bufReader = new BufferedReader(isr);

			while ((line = bufReader.readLine()) != null) {
				Result += line;
			}
			isr.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Result;
	}

	public boolean getRawFileToSD(String filename, int rawid) {
		String line = "";
		String Result = "";
		try {
			InputStream is = context.getResources().openRawResource(rawid);
			InputStreamReader isr = new InputStreamReader(is, "utf8");
			BufferedReader bufReader = new BufferedReader(isr);

			while ((line = bufReader.readLine()) != null) {
				Result += line;
			}
			isr.close();
			saveFileToSDCard(filename, Result);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;

	}

	public void saveFileToSDCard(String filename, String content)
			throws Exception {
		// Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
		File file = new File(getAppSDCardPath() + filename);
		// File file = Environment.getExternalStorageDirectory();
		FileOutputStream fos = new FileOutputStream(file);
		fos.write(content.getBytes());
		fos.close();
	}

	public void saveRawToSDCard(String filename, int rid) throws IOException {
		try {
			InputStream is = context.getResources().openRawResource(rid);
			FileOutputStream fos = new FileOutputStream(getAppSDCardPath()
					+ filename);
			byte[] buffer = new byte[8192];
			int count = 0;
			while ((count = is.read(buffer)) > 0) {
				fos.write(buffer, 0, count);
			}
			fos.close();
			is.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 验证 正则表达式
	 * 
	 * @author 正则表达式 value 所属字符串
	 * @return boolean
	 */
	public boolean regex(String regex, String value) {
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(value);
		return m.find();
	}

	public String intentExtra(String extraStr) {
		Activity act = (Activity) context;
		Intent intent = act.getIntent();
		String infoStr = intent.getStringExtra(extraStr);
		return infoStr;
	}

	public String objToStr(Object o) {
		String vals = "";
		if (o instanceof Double) {
			Double d = (Double) o;
			int c = (int) Math.ceil(d);
			vals = String.valueOf(c);
		} else if (o instanceof Integer) {
			vals = String.valueOf(o.toString());
		} else {
			vals = o.toString();
		}
		return vals;
	}

	// public int convertInt(Map<String, Object> map,String keyName) {
	// Object o= map.get(keyName.trim());
	// String str = removeDotZero(o.toString());
	//
	// int vals = 0;
	// if (o instanceof Double) {
	// //vals = Integer.parseInt(str);
	// vals = (int) Math.ceil(Double.parseDouble(str));
	// } else if (o instanceof Integer) {
	// vals = Integer.parseInt(str);
	// } else if (o instanceof String) {
	// vals = Integer.parseInt(str);
	// }
	// return vals;
	// }

	public void writeSDFile(String contentStr, String fileName) {

		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {

			String foldername = getAppSDCardPath();
			File folder = new File(foldername);

			if (folder == null || !folder.exists()) {
				folder.mkdir();
			}

			File targetFile = new File(foldername + fileName);
			OutputStreamWriter osw;

			try {
				if (!targetFile.exists()) {
					targetFile.createNewFile();
					osw = new OutputStreamWriter(new FileOutputStream(
							targetFile), "utf-8");
					osw.write(contentStr);
					osw.close();
				} else {
					osw = new OutputStreamWriter(new FileOutputStream(
							targetFile, true), "utf-8");
					osw.write("\n" + contentStr);
					osw.flush();
					osw.close();
				}
			} catch (Exception e) {
				// Toast.makeText(context, e.toString(),
				// Toast.LENGTH_LONG).show();
			}
		} else {
			// Toast.makeText(context, "未发现SD卡！", Toast.LENGTH_LONG).show();
		}

	}

	/**
	 * 任务多次测试结果，log文件追加到resultfile，先临时获取最后一个log
	 * 
	 * @param logFileName
	 * @return
	 */
	public String getLastLog(String logFileName) {
		String[] strs = logFileName.split(".log");
		return strs[strs.length - 1] + ".log";
	}

	/**
	 * 写入测试结果到data/data/com.gk.touchstone/files
	 * 
	 * @param logFileName
	 *            文件名
	 */
	public boolean writeDataFile(String logFileName, String content, int type) {
		// logFileName task.getresultFile 准备包含多个log文件名，存放历史log，最前一个为最新
		try {
			FileOutputStream fos = context.openFileOutput(
					getLastLog(logFileName), type);

			fos.write(content.getBytes());
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public boolean isFilesExistNoEmpty(Task task) {
		String fileName = task.getResultFile();
		String filePath = context.getFilesDir().getAbsolutePath();
		File f = new File(filePath + File.separator + fileName);
		if (!f.exists() || readDataFile(fileName) == null) {
			return false;
		}

		return true;
	}

	/**
	 * 读取data/data下的File内的测试结果
	 * 
	 * @param logFileName
	 * @return
	 */
	public String readDataFile(String logFileName) {
		String content = null;
		try {
			FileInputStream fis = context.openFileInput(logFileName);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int len = 0;
			while ((len = fis.read(buffer)) != -1) {
				baos.write(buffer, 0, len);
			}
			content = baos.toString();
			fis.close();
			baos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return content;
	}

	/**
	 * 删除测试结果
	 * 
	 * @param logFileName
	 */
	public void deleteLog(String logFileName) {
		try {
			context.deleteFile(logFileName);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean deleteLogs(List<Task> tasks) {
		try {
			for (Task t : tasks) {
				context.deleteFile(t.getResultFile());
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public void uploadLog(List<Task> tasks) {
		File file = context.getFilesDir();

		Collection<File> resFileList = new ArrayList<File>();

		UUID uuid = UUID.randomUUID();
		String zipName = uuid.toString() + ".zip";

		String zipFilePath = getAppSDCardPath() + zipName;
		File zipFile = new File(zipFilePath);
		for (Task t : tasks) {
			// Task t =tasks.get(0);
			String filename = t.getResultFile();
			String filePath = file.getAbsolutePath() + File.separator
					+ filename;

			File f = new File(filePath);
			resFileList.add(f);
		}

		ZipCompress zip = new ZipCompress();
		zip.compress(resFileList, zipFile);
		uploadFile(zipName, zipFilePath);

	}

	public void uploadFile(String filename, String path) {
		AsyncUpload uploadfile = new AsyncUpload(context);

		String serverUrl = context.getResources().getString(
				R.string.server_addr);

		String username = "postuser";
		String password = "touchstone";

		String url = "http://" + serverUrl + "/ReceiveUpload.aspx?name="
				+ username + "&pwd=" + password;

		uploadfile.execute(path, url);

	}

	public boolean installAPK(Context context, String filePath) {
		Intent i = new Intent(Intent.ACTION_VIEW);
		File file = new File(filePath);
		if (file != null && file.length() > 0 && file.exists() && file.isFile()) {
			i.setDataAndType(Uri.parse("file://" + filePath),
					"application/vnd.android.package-archive");
			i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(i);
			return true;
		}
		return false;
	}

	public TextView findTextView(int rid) {
		Activity act = (Activity) context;
		TextView txt = null;
		if (act != null) {
			txt = (TextView) act.findViewById(rid);
		}
		return txt;
	}

	/**
	 * 文件名过滤
	 * 
	 * @param startStr
	 * @param endStr
	 * @return
	 */
	public static FilenameFilter filterFile(String startStr, String endStr) {
		final String _startStr = startStr;
		final String _endStr = endStr;
		return new FilenameFilter() {
			public boolean accept(File file, String name) {
				boolean ret = name.startsWith(_startStr)
						&& name.endsWith(_endStr);
				return ret;
			}
		};
	}

	/**
	 * 测试结果PASS FAIL着色
	 * 
	 * @param stateStr
	 * @return
	 */
	public Spanned setResultColor(String stateStr) {
		String htmstr = "";
		// context.getResources().getColor(R.color.red);
		if (stateStr.contains(Constants.PASS)) {
			htmstr = "<font color='#008200'>" + stateStr + "</font>";
		} else if (stateStr.contains(Constants.FAIL)) {
			htmstr = "<font color='#FF0000'>" + stateStr + "</font>";
		}
		return Html.fromHtml(htmstr);
	}

	/**
	 * 获取SD卡剩余容量
	 * 
	 * @return
	 */
	public long getSDLeftSize() {
		// 取得SDCard当前的状态
		String sDcString = android.os.Environment.getExternalStorageState();

		if (sDcString.equals(android.os.Environment.MEDIA_MOUNTED)) {

			File pathFile = android.os.Environment
					.getExternalStorageDirectory();

			android.os.StatFs statfs = new android.os.StatFs(pathFile.getPath());

			long nBlocSize = statfs.getBlockSize();
			long nAvailaBlock = statfs.getAvailableBlocks();
			return nAvailaBlock * nBlocSize;
		} else {
			// SDCard不存在;
		}
		return 0;
	}

	/**
	 * XML文件解析
	 * 
	 * @param rid
	 * @param tab
	 * @return
	 */
	public List<Map<String, Object>> getHomeView(int rid, String tab) {
		XmlPullParser xpp = context.getResources().getXml(rid);
		List<Map<String, Object>> itemlist = new ArrayList<Map<String, Object>>();
		// Map<String, Object> maps = null;
		while (true) {
			try {
				int eventType = xpp.getEventType();
				if ((eventType == XmlPullParser.END_TAG && xpp.getName()
						.equals("ListItems"))
						|| eventType == XmlPullParser.END_DOCUMENT)
					break;
				if (eventType == XmlPullParser.START_TAG) {
					if (xpp.getName().equals("item")) {

						String catName = "";
						String catValue = "";

						Map<String, Object> mapValue = new HashMap<String, Object>();
						for (int i = 0; i < xpp.getAttributeCount(); i++) {
							String xppName = xpp.getAttributeName(i).trim();
							String xppValue = xpp.getAttributeValue(i).trim();
							if (xppName.equals("name")) {
								mapValue.put("name", xppValue);
							} else if (xppName.equals("text")) {
								mapValue.put("text", xppValue);
							} else if (xppName.equals("category")) {
								mapValue.put("category", xppValue);
							} else if (xpp.getAttributeName(i).equals("icon")) {
								if (!xppValue.equals("")) {
									int resID = context.getResources()
											.getIdentifier(xppValue,
													"drawable",
													context.getPackageName());
									mapValue.put("icon", resID);
								}
							} else if (xppName.equals("tab")) {
								mapValue.put("tab", xppValue);
								catName = "tab";
								catValue = xppValue;
							}
						}
						if (catName.equals("tab") && catValue.equals(tab)) {
							itemlist.add(mapValue);
						}
					}
				}

			} catch (XmlPullParserException e) {
				e.printStackTrace();
			}
			try {
				xpp.next();
			} catch (XmlPullParserException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return itemlist;
	}

}

package com.gk.touchstone.testcasebak;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import android.content.Context;
import com.gk.touchstone.core.TestCase;
import com.gk.touchstone.core.Constants;
import com.gk.touchstone.entity.Task;
import com.gk.touchstone.utils.Utils;

public class SdcardCopyTask extends TestCase {
	private Thread mThread = null;
	private String copyFile;
	private int spaceTime;
	private int st;
	private long fileSize;
	private String fileName;
	private boolean copyFinish = false;
	private boolean isCopying = false;
	private String startCopyTime;
	private String endCopyTime;
	private Utils us;

	private String resultStr = "", reasonStr = "";

	public SdcardCopyTask(Context context, Task task) {
		super(context, task);

		us = new Utils(context);

		initData();
	}

	private void initData() {
		timeCount = getIntValue("sdcardCopyNum");
		spaceTime = getIntValue("sdcardCopyInterval");
		copyFile = getStrValue("sdcardCopyFile");

		st = spaceTime;

		if (copyFile.equals("")) {
			copyFile = getfile(Utils.getSDCardPath());
		}
	}

	@Override
	public void Start() {
		initData();

		isRunning = true;
		if (mThread == null) {
			mThread = new myThread();
			mThread.start();
		}
	}

	@Override
	public void Finish() {
		sendBroadcast();
		return;
	}

	@Override
	public void Stop() {
		stopTask();
	}

	class myThread extends Thread {
		@Override
		public void run() {
			while (true) {
				try {
					Thread.currentThread();
					Thread.sleep(1000);
					if (!isRunning) {
						break;
					}
					if (timeCount > 0) {
						if (st > 0) {
							st--;
						} else {// 等待拷贝间隔时间完成
							if (!copyFinish && !isCopying) {
								startCopyFile();
							} else if (copyFinish) {
								copyFinish = false;

								st = spaceTime;
								timeCount--;
								writeResult(timeCount, resultStr, startCopyTime
										+ "-" + endCopyTime);// reasonStr);
							}
						}
					} else {
						Finish();
					}
				} catch (InterruptedException e) {
					throw new RuntimeException();
				}
			}
		}
	}

	// private boolean copyOver(String filename){
	// String copyFolder = us.getAppSDCardPath();
	// File f = new File(copyFolder + File.separator + filename);
	// if (f.exists() && f.length() == fileSize) {
	// copyFinish = true;
	// isCopying = false;
	//
	// resultStr = Constants.PASS;
	// reasonStr = "";
	// return true;
	// }
	// return false;
	// }

	private void startCopyFile() {
		new Thread() {
			public void run() {
				isCopying = true;
				try {
					String copyFolder = us.getAppSDCardPath();
					copyFile(copyFile, copyFolder);
				} catch (IOException e) {
					resultStr = Constants.FAIL;
					reasonStr = e.getMessage();
					// e.printStackTrace();
				}
			}
		}.start();
	}

	public void copyFile(String resFilePath, String distFolder)
			throws IOException {
		File resFile = new File(resFilePath);
		fileSize = resFile.length();
		fileName = resFile.getName();

		File distFile = new File(distFolder);
		if (resFile.isDirectory()) {
			FileUtils.copyDirectoryToDirectory(resFile, distFile);
		} else if (resFile.isFile()) {
			FileUtils.copyFileToDirectory(resFile, distFile, true);
		}
	}

	private String getfile(String fromFile) {
		String filepath = "";
		File[] currentFiles;
		File root = new File(fromFile);

		if (!root.exists()) {
			filepath = "";
		}

		currentFiles = root.listFiles();

		for (int i = 0; i < currentFiles.length; i++) {
			if (currentFiles[i].isFile() && currentFiles[i].canRead()
					&& !currentFiles[i].getName().startsWith(".")) {
				try {
					filepath = currentFiles[i].getCanonicalPath();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				break;
			}
		}

		return filepath;
	}

	// private int copy(String fromFile, String toFile) {
	// // 要复制的文件目录
	// File[] currentFiles;
	// File root = new File(fromFile);
	// // 如同判断SD卡是否存在或者文件是否存在
	// // 如果不存在则 return出去
	// if (!root.exists()) {
	// return -1;
	// }
	// // 如果存在则获取当前目录下的全部文件 填充数组
	// currentFiles = root.listFiles();
	//
	// // 目标目录
	// File targetDir = new File(toFile);
	// // 创建目录
	// if (!targetDir.exists()) {
	// targetDir.mkdirs();
	// }
	// // 遍历要复制该目录下的全部文件
	// for (int i = 0; i < currentFiles.length; i++) {
	// if (currentFiles[i].isDirectory())// 如果当前项为子目录 进行递归
	// {
	// copy(currentFiles[i].getPath() + "/",
	// toFile + currentFiles[i].getName() + "/");
	//
	// } else// 如果当前项为文件则进行文件拷贝
	// {
	// CopySdcardFile(currentFiles[i].getPath(), toFile
	// + currentFiles[i].getName());
	// }
	// }
	// return 0;
	// }

}

package com.gk.touchstone.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Collection;
import java.util.zip.CRC32;
import java.util.zip.CheckedOutputStream;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;

public class ZipCompress {
	static final int BUFFER = 8192;

	public ZipCompress() {

	}

	public void compress(Collection<File> files, File zipFile) {
		try {
			FileOutputStream fileOutputStream = new FileOutputStream(zipFile);
			CheckedOutputStream cos = new CheckedOutputStream(fileOutputStream,
					new CRC32());
			ZipOutputStream out = new ZipOutputStream(cos);
			// String basedir = "";
			compressFiles(files, out);
			out.close();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void compressFiles(Collection<File> files, ZipOutputStream out) {
		for (File f : files) {
			compressFile(f, out);
		}
	}

	/** 压缩一个文件 */
	private void compressFile(File file, ZipOutputStream out) {
		if (!file.exists()) {
			return;
		}
		try {
			BufferedInputStream bis = new BufferedInputStream(
					new FileInputStream(file));
			ZipEntry entry = new ZipEntry(file.getName());
			out.putNextEntry(entry);
			int count;
			byte data[] = new byte[BUFFER];
			while ((count = bis.read(data, 0, BUFFER)) != -1) {
				out.write(data, 0, count);
			}
			bis.close();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
package com.cttic.cell.phone.signal.utils.zip;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPOutputStream;

public class FileUtil {
	/**
	 * 
	 * @Title: compress
	 * @Description: 将文件用tar压缩
	 * @param source
	 *            �?要压缩的文件
	 * @return File 返回压缩后的文件
	 * @throws
	 */
	public static File compress(File source) {
		File target = new File(source.getAbsoluteFile() + ".gz");
		FileInputStream in = null;
		GZIPOutputStream out = null;
		try {
			in = new FileInputStream(source);
			out = new GZIPOutputStream(new FileOutputStream(target));
			byte[] array = new byte[1024];
			int number = -1;
			while ((number = in.read(array, 0, array.length)) != -1) {
				out.write(array, 0, number);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
					return null;
				}
			}

			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
					return null;
				}
			}
		}
		return target;
	}

	/***
	 * 指定文件夹下的所有文件
	 * @param path
	 * @return
	 */
	public static List<File> getFiles(String path) {
		File root = new File(path);
		List<File> files = new ArrayList<File>();
		if (!root.isDirectory()) {
			files.add(root);
		} else {
			File[] subFiles = root.listFiles();
			for (File f : subFiles) {
				files.addAll(getFiles(f.getAbsolutePath()));
			}
		}
		return files;
	}

	public static boolean rename(File ori, File dest) {
		File destF = dest;
		if (dest.exists()) {
			destF = new File(dest.getPath() + "." + System.currentTimeMillis());
		}
		return ori.renameTo(destF);
	}
}

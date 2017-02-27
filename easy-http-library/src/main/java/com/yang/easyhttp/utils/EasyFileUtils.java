package com.yang.easyhttp.utils;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.yang.easyhttp.manager.EasyHttpClientManager;

import java.io.File;

/**
 * Created by yangy on 2017/2/16.
 */
public class EasyFileUtils {
	public static String getFileNameFromUrl(String url) {
		if (!TextUtils.isEmpty(url)) {
			return url.substring(url.lastIndexOf("/") + 1);
		}

		return System.currentTimeMillis() + "";
	}

	public static String getDefaultFilePath(Context context) {
		String filePath = Environment.getExternalStorageDirectory().getAbsolutePath()
				+ "/"
				+ context.getPackageName()
				+ "/download/";
		File file = new File(filePath);
		if (!file.exists()) {
			boolean createDir = file.mkdirs();
			if (createDir) {
				if (EasyHttpClientManager.getInstance().isDebug()) {
					Log.d("EasyDownloadTask", "create file dir success");;
				}
			}
		}
		return filePath;
	}
}

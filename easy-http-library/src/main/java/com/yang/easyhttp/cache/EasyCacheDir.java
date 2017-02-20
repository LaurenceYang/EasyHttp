package com.yang.easyhttp.cache;

import android.os.Environment;

/**
 * Created by yangy on 2017/2/16.
 */
public class EasyCacheDir {
	public static final String SD_PATH = Environment.getExternalStorageDirectory().getPath();
	public static final String CACHE_SHORT_DIR = SD_PATH + "/BookReader/cache/http.cache.1"; //TODO：
	public static final String CACHE_MID_DIR = SD_PATH + "/BookReader/cache/http.cache.2"; //TODO：
	public static final String CACHE_LONG_DIR = SD_PATH + "/BookReader/cache/http.cache.3"; //TODO：

}

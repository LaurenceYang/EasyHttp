package com.yang.easyhttp;

import android.content.Context;

import com.yang.easyhttp.cache.EasyCacheType;
import com.yang.easyhttp.callback.EasyCallback;
import com.yang.easyhttp.config.EasyHttpConfig;
import com.yang.easyhttp.download.EasyDownloadManager;
import com.yang.easyhttp.manager.EasyHttpClientManager;
import com.yang.easyhttp.request.EasyRequestParams;

/**
 * Created by yangy on 2017/2/15.
 */
public class EasyHttpClient {
	/**
	 * EasyHttpClient init.
	 * @param context
	 */
	public static void init(Context context) {
		EasyHttpClientManager.getInstance().init(context);
	}

	public static void init(Context context, EasyHttpConfig config) {
		EasyHttpClientManager.getInstance().init(context, config);
	}

	/**
	 * Download environment init.
	 * Make sure the init function is only called once.
	 */
	public static void initDownloadEnvironment() {
		EasyDownloadManager.getInstance().init(EasyHttpClientManager.getInstance().getContext());
	}

	public static void initDownloadEnvironment(int threadCount) {
		EasyDownloadManager.getInstance().init(EasyHttpClientManager.getInstance().getContext(), threadCount);
	}

	/**
	 * set the debug mode.
	 * @param debug
	 */
	public static void setDebug(boolean debug) {
		EasyHttpClientManager.getInstance().setDebug(debug);
	}

	/**
	 * get request.
	 * @param url
	 * @param callback
	 * @param <T>
	 */
	public static <T> void get(String url, EasyCallback<T> callback) {
		get(url, null, callback);
	}

	public static <T> void get(String url, EasyRequestParams easyRequestParams, EasyCallback<T> callBack) {
		EasyHttpClientManager.getInstance().get(url, easyRequestParams, EasyCacheType.CACHE_TYPE_NO_SETTING, callBack);
	}

	public static <T> void get(String url, int cacheType, EasyCallback<T> callback) {
		get(url, null, cacheType, callback);
	}

	public static <T> void get(String url, EasyRequestParams easyRequestParams, int cacheType, EasyCallback<T> callback) {
		EasyHttpClientManager.getInstance().get(url, easyRequestParams, cacheType, callback);
	}

	/**
	 * post request.
	 * @param url
	 * @param easyRequestParams
	 * @param callback
	 * @param <T>
	 */
	public static <T> void post(String url, EasyRequestParams easyRequestParams, EasyCallback<T> callback) {
		EasyHttpClientManager.getInstance().postOrDeleteOrPut(url, easyRequestParams, callback, EasyHttpClientManager.REQUEST_POST);
	}

	/**
	 * delete request.
	 * @param url
	 * @param easyRequestParams
	 * @param callback
	 * @param <T>
	 */
	public static <T> void delete(String url, EasyRequestParams easyRequestParams, EasyCallback<T> callback) {
		EasyHttpClientManager.getInstance().postOrDeleteOrPut(url, easyRequestParams, callback, EasyHttpClientManager.REQUEST_DELETE);
	}

	/**
	 * put request
	 * @param url
	 * @param easyRequestParams
	 * @param callback
	 * @param <T>
	 */
	public static <T> void put(String url, EasyRequestParams easyRequestParams, EasyCallback<T> callback) {
		EasyHttpClientManager.getInstance().postOrDeleteOrPut(url, easyRequestParams, callback, EasyHttpClientManager.REQUEST_PUT);
	}

	/**
	 * upload file.
	 * @param url
	 * @param filePath
	 * @param callback
     * @param <T>
     */
	public static <T> void uploadFile(String url, String filePath, EasyCallback<T> callback) {
		EasyHttpClientManager.getInstance().uploadFile(url, filePath, callback);
	}

	/**
	 * cancel request.
	 * @param cacheType
     */
	public static void cancel(int cacheType) {
		EasyHttpClientManager.getInstance().cancelRequest(cacheType);
	}
}

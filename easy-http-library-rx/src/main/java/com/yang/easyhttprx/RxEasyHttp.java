package com.yang.easyhttprx;

import com.yang.easyhttp.cache.EasyCacheType;
import com.yang.easyhttp.request.EasyRequestParams;
import com.yang.easyhttprx.manager.RxEasyHttpManager;

import io.reactivex.Flowable;
import okhttp3.Response;

/**
 * Created by yangy on 2017/3/14.
 */

public class RxEasyHttp {
	/**
	 * Get请求RxJava形式
	 * @param url
	 * @return
	 */
	public static Flowable<Response> get(String url) {
		return get(url, null);
	}

	public static Flowable<Response> get(String url, EasyRequestParams easyRequestParams) {
		return RxEasyHttpManager.getInstance().get(url, easyRequestParams, EasyCacheType.CACHE_TYPE_NO_SETTING);
	}

	public static Flowable<Response> get(String url, int cacheType) {
		return get(url, null, cacheType);
	}

	public static Flowable<Response> get(String url, EasyRequestParams easyRequestParams, int cacheType) {
		return RxEasyHttpManager.getInstance().get(url, easyRequestParams, cacheType);
	}

	/**
	 * Post请求RxJava形式
	 * @param url
	 * @return
	 */
	public static Flowable<Response> post(String url, EasyRequestParams easyRequestParams) {
		return RxEasyHttpManager.getInstance().post(url, easyRequestParams);
	}

	/**
	 * post file RxJava形式
	 * @param url
	 * @param filePath
	 * @return
	 */
	public static Flowable<Response> uploadFile(String url, String filePath) {
		return RxEasyHttpManager.getInstance().uploadFile(url, filePath);
	}
}

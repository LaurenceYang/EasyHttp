package com.yang.easyhttprx;

import com.yang.easyhttp.cache.EasyCacheType;
import com.yang.easyhttp.request.EasyRequestParams;
import com.yang.easyhttprx.converter.RxEasyConverter;
import com.yang.easyhttprx.manager.RxEasyHttpManager;

import io.reactivex.Flowable;

/**
 * Created by yangy on 2017/3/14.
 */

public class RxEasyHttp {
	/**
	 * Get请求RxJava形式
	 * @param url
	 * @return
	 */
	public static <T> Flowable<T> get(String url, RxEasyConverter<T> converter) {
		return get(url, null, converter);
	}

	public static <T> Flowable<T> get(String url, EasyRequestParams easyRequestParams, RxEasyConverter<T> converter) {
		return RxEasyHttpManager.getInstance().get(url, easyRequestParams, EasyCacheType.CACHE_TYPE_NO_SETTING, converter);
	}

	public static <T> Flowable<T> get(String url, int cacheType, RxEasyConverter<T> converter) {
		return get(url, null, cacheType, converter);
	}

	public static <T> Flowable<T> get(String url, EasyRequestParams easyRequestParams, int cacheType, RxEasyConverter<T> converter) {
		return RxEasyHttpManager.getInstance().get(url, easyRequestParams, cacheType, converter);
	}

	/**
	 * Post请求RxJava形式
	 * @param url
	 * @return
	 */
	public static <T> Flowable<T> post(String url, EasyRequestParams easyRequestParams, RxEasyConverter<T> converter) {
		return RxEasyHttpManager.getInstance().post(url, easyRequestParams, converter);
	}

	/**
	 * post file RxJava形式
	 * @param url
	 * @param filePath
	 * @return
	 */
	public static <T> Flowable<T> uploadFile(String url, String filePath, RxEasyConverter<T> converter) {
		return RxEasyHttpManager.getInstance().uploadFile(url, filePath, converter);
	}
}

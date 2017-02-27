package com.yang.easyhttp.callback;

/**
 * Created by yangy on 2016/10/14.
 */

public interface EasyCallback<T> {
	// UI线程.
	void onStart();

	// UI线程.
	void onFinish();

	// UI线程.
	void onSuccess(T content);

	// UI线程.
	void onFailure(Throwable error, String content);

	// 子线程.
	T convert(String body) throws Exception;
}

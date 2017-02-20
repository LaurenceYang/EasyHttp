package com.yang.easyhttp.callback;

import com.google.gson.Gson;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by yangy on 2017/2/15.
 */
public abstract class EasyJsonCallback<T> implements EasyCallback<T> {
	@Override
	public void onStart() {

	}

	@Override
	public void onFinish() {

	}

	@Override
	public T convert(String body) {
		Type t = getClass().getGenericSuperclass();
		Type tArg = ((ParameterizedType) t).getActualTypeArguments()[0];
		Gson gson = new Gson(); //TODO:
		return gson.fromJson(body, tArg);
	}
}

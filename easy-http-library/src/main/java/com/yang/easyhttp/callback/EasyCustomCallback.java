package com.yang.easyhttp.callback;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.yang.easyhttp.manager.EasyHttpClientManager;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by yangy on 2017/2/15.
 */
public abstract class EasyCustomCallback<T> implements EasyCallback<T> {
	@Override
	public void onStart() {

	}

	@Override
	public void onFinish() {

	}

	@Override
	public T convert(String body) throws JsonSyntaxException {
		Type superClassType = getClass().getGenericSuperclass();
		Type tArg = ((ParameterizedType) superClassType).getActualTypeArguments()[0];
		Gson gson = EasyHttpClientManager.getInstance().getGson();
		return gson.fromJson(body, tArg);
	}
}

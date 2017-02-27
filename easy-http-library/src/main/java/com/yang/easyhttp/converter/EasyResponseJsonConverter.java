package com.yang.easyhttp.converter;

import com.google.gson.Gson;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by yangy on 2017/2/15.
 */
public class EasyResponseJsonConverter<T> implements EasyResponseConverter<T> {

	@Override
	public T convert(String body) {
		Gson gson = new Gson(); //TODO:
		Type t = getClass().getGenericSuperclass();
		Type targ = ((ParameterizedType) t).getActualTypeArguments()[0];
		return gson.fromJson(body, targ);
	}
}

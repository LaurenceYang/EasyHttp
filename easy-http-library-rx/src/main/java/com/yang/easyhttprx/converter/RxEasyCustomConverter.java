package com.yang.easyhttprx.converter;

import com.google.gson.Gson;
import com.yang.easyhttp.manager.EasyHttpClientManager;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by yangyang on 2017/3/15.
 */
public abstract class RxEasyCustomConverter<T> implements RxEasyConverter<T> {

    @Override
    public T convert(String body) throws Exception {
        Class clazz = this.getClass();
        Type superClassType = clazz.getGenericSuperclass();
        Type tArg = ((ParameterizedType) superClassType).getActualTypeArguments()[0];
        Gson gson = EasyHttpClientManager.getInstance().getGson();
        return gson.fromJson(body, tArg);
    }
}

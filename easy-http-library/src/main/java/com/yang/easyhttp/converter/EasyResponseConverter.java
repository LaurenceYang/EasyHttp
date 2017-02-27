package com.yang.easyhttp.converter;

/**
 * Created by yangy on 2017/2/15.
 */
public interface EasyResponseConverter<T> {
	T convert(String body);
}

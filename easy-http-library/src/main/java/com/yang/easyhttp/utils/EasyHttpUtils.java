package com.yang.easyhttp.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import okhttp3.Headers;
import okhttp3.Request;

/**
 * Created by yangy on 2017/3/14.
 */

public class EasyHttpUtils {
	public static Request.Builder appendHeaders(Map<String, String> headers) {
		Request.Builder requestBuilder = new Request.Builder();

		if (headers == null || headers.size() == 0) {
			return requestBuilder;
		}

		Headers.Builder headerBuilder = new Headers.Builder();
		for (Map.Entry<String, String> entry : headers.entrySet()) {
//			String headerValue = URLEncoder.encode(entry.getValue(), "UTF-8");
			headerBuilder.add(entry.getKey(), entry.getValue());
		}
		requestBuilder.headers(headerBuilder.build());
		return requestBuilder;
	}

	public static String createUrlFromParams(String url, Map<String, String> params) {
		if (params == null || params.size() == 0) {
			return url;
		}

		try {
			StringBuilder sb = new StringBuilder();
			sb.append(url);
			if (url.indexOf('&') > 0 || url.indexOf('?') > 0) {
				sb.append("&");
			} else {
				sb.append("?");
			}
			for (Map.Entry<String, String> urlParam : params.entrySet()) {
				//对参数进行 utf-8 编码,防止头信息传中文
				String urlValue = URLEncoder.encode(urlParam.getValue(), "UTF-8");
				sb.append(urlParam.getKey())
						.append("=")
						.append(urlValue)
						.append("&");
			}
			sb.deleteCharAt(sb.length() - 1);
			return sb.toString();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return url;
	}
}

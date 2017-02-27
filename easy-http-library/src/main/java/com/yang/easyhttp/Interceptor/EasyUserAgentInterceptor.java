package com.yang.easyhttp.Interceptor;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by yangy on 2017/2/22.
 */
public class EasyUserAgentInterceptor implements Interceptor {
	private static final String USER_AGENT_HEADER_NAME = "User-Agent";
	private String mUserAgent;

	public EasyUserAgentInterceptor(String userAgent) {
		mUserAgent = userAgent;
	}

	@Override
	public Response intercept(Chain chain) throws IOException {
		final Request originalRequest = chain.request();
		final Request requestWithUserAgent = originalRequest.newBuilder()
				.removeHeader(USER_AGENT_HEADER_NAME)
				.addHeader(USER_AGENT_HEADER_NAME, mUserAgent)
				.build();

		return chain.proceed(requestWithUserAgent);
	}
}

package com.yang.easyhttp.Interceptor;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by yangyang on 2017/6/14.
 */

public class EasyHeaderInterceptor implements Interceptor {
    ConcurrentHashMap<String, String> mHeaders;

    public EasyHeaderInterceptor(ConcurrentHashMap<String, String> headers) {
        this.mHeaders = headers;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        final Request originalRequest = chain.request();

        Request.Builder builder = originalRequest.newBuilder();

        if (mHeaders != null) {
            for (ConcurrentHashMap.Entry<String, String> entry : mHeaders.entrySet()) {
                builder.addHeader(entry.getKey(), entry.getValue());
            }
        }

        return chain.proceed(builder.build());
    }
}

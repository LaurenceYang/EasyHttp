package com.yang.easyhttp.request;

import java.util.concurrent.ConcurrentHashMap;

public class EasyRequestParams {
    private ConcurrentHashMap<String, String> mRequestParams = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, String> mRequestHeaders = new ConcurrentHashMap<>();


    public void put(String key, String value) {
        if (key != null && value != null) {
            mRequestParams.put(key, value);
        }
    }

    public ConcurrentHashMap<String, String> getRequestParams() {
        return mRequestParams;
    }

    public void addHeader(String key, String value) {
        if (key != null && value != null) {
            mRequestHeaders.put(key, value);
        }
    }

    public ConcurrentHashMap<String, String> getRequestHeaders() {
        return mRequestHeaders;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (ConcurrentHashMap.Entry<String, String> entry : mRequestParams.entrySet()) {
            if (result.length() > 0)
                result.append("&");

            result.append(entry.getKey());
            result.append("=");
            result.append(entry.getValue());
        }
        return result.toString();
    }
}

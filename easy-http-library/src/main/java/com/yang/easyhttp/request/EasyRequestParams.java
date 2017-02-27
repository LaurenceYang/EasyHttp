package com.yang.easyhttp.request;

import java.util.concurrent.ConcurrentHashMap;

public class EasyRequestParams {
    protected ConcurrentHashMap<String, String> urlParams = new ConcurrentHashMap<>();

    public void put(String key, String value) {
        if (key != null && value != null) {
            urlParams.put(key, value);
        }
    }

    public ConcurrentHashMap<String, String> getUrlParams() {
        return urlParams;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (ConcurrentHashMap.Entry<String, String> entry : urlParams.entrySet()) {
            if (result.length() > 0)
                result.append("&");

            result.append(entry.getKey());
            result.append("=");
            result.append(entry.getValue());
        }
        return result.toString();
    }
}

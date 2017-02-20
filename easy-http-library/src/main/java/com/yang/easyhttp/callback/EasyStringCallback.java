package com.yang.easyhttp.callback;

/**
 * Created by yangyang on 2017/2/20.
 */
public abstract class EasyStringCallback implements EasyCallback<String> {
    @Override
    public void onStart() {

    }

    @Override
    public void onFinish() {

    }

    @Override
    public String convert(String body) {
        return body;
    }
}

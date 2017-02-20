package com.yang.demo;

import android.app.Application;

import com.yang.easyhttp.EasyHttpClient;

/**
 * Created by yangyang on 2017/2/17.
 */
public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // 初始化HttpClient.
        EasyHttpClient.init(this);

        // 初始化下载环境.
        EasyHttpClient.initDownloadEnvironment(2);

    }
}

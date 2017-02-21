# EasyHttp

## 特性
* 支持Get请求
* 支持Post请求
* 支持文件下载及下载管理
* 支持文件下载的断点续传
* 支持文件上传
* 支持Get请求全局缓存设置
* 支持Get请求不同请求不同缓存策略
* 支持返回JSON对象，可自动映射为Java对象
* 支持返回String字符，可自定义返回值转换扩展
* 使用简单；轻量级代码；结构清晰
* 基于okhttp3
* 下载管理数据库使用greendao



## 最新版本

* v0.5.0 - 2017.02.21 - 初版发布，支持Get、Post、下载、上传、断点续传、不同缓存策略等



## ScreenShot



## 使用指南

### With Gradle

在全局`build.gradle`文件的`repositories`添加如下所示配置：

```java
jcenter()
```
jcenter上传目前审核中，不能访问的情况下在`repositories`添加以下配置：
```java
maven {
    url  "http://dl.bintray.com/laurenceyanger/maven"
}
```
在module的`build.gradle`文件的`dependencies`区域内添加如下所示配置：

```java
compile 'com.yang.easyhttp:easyhttp:0.5.0'
```

## 初始化

在Applicaiton里进行初始化。

```java
public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // 初始化HttpClient.
        EasyHttpClient.init(this);

        // 初始化下载环境.optional.
        EasyHttpClient.initDownloadEnvironment(2);

    }
}
```

### Get 请求

EasyHttp提供`EasyHttpClient.get`接口用来异步请求网络数据。</br>eg:

```java
EasyHttpClient.get(url, new EasyJsonCallback<Entity>() {
  @Override
  public void onStart() {//ui thread.
      dialog.show();
  }

  @Override
  public void onFinish() {//ui thread.
      dialog.cancel();
  }

  @Override
  public void onSuccess(Entity content) {//ui thread.
      // ui operation using content object.
  }

  @Override
  public void onFailure(Throwable error, String content) {//ui thread.
      body.setText(content + "\n" +error.toString());
  }
});
```

Get请求接口列表如下：

```java
public static <T> void get(String url, EasyCallback<T> callback);
public static <T> void get(String url, EasyRequestParams easyRequestParams, EasyCallback<T> callBack);
public static <T> void get(String url, int cacheType, EasyCallback<T> callback);
public static <T> void get(String url, EasyRequestParams easyRequestParams, int cacheType, EasyCallback<T> callback);
```

返回的是Json数据，可以使用`EasyJsonCallback`类来直接完成字符串对Json对象的转换。</br>

返回的是普通的字符串，可以使用`EasyStringCallback`作为回调。</br>

`EasyRequestParams`用来设定请求的参数。</br>

`EasyCacheType`用来设定每个请求的缓存类型。</br>

### Post请求

EasyHttp提供`EasyHttpClient.post`接口用来异步post数据。

```java
EasyRequestParams params = new EasyRequestParams();
params.put("key1", "value1");
params.put("key2", "value2");

EasyHttpClient.post(postUrl,
        params,
        new EasyStringCallback() {
            @Override
            public void onStart() {
                dialog.show();
            }

            @Override
            public void onFinish() {
                dialog.cancel();
            }

            @Override
            public void onSuccess(String content) {
                Toast.makeText(PostActivity.this, "提交成功", Toast.LENGTH_LONG);
                result.setText(content);
            }

            @Override
            public void onFailure(Throwable error, String content) {
                Toast.makeText(PostActivity.this, "提交失败", Toast.LENGTH_LONG);
                result.setText(content + "\n" + error.getMessage());
            }
        }
);
```

Post请求接口列表如下：

```java
public static <T> void post(String url, EasyRequestParams easyRequestParams, EasyCallback<T> callback)
```

同Get请求类似，返回的是Json数据，可以使用`EasyJsonCallback`类来直接完成字符串对Json对象的转换。</br>

返回的是普通的字符串，可以使用`EasyStringCallback`作为回调。</br>

`EasyRequestParams`用来设定post的键值对。</br>

### Download File

初始化时可通过`initDownloadEnvironment(int threadCount)`来设定同时下载的数量。</br>

跟下载相关的类包括：

* EasyDownloadManager 负责管理下载任务
* EasyDownloadTask 下载任务
* EasyDownloadTaskListener 下载回调
* EasyTaskEntity 任务的实体
* EasyTaskStatus 下载的状态

关于下载的管理，可以参考sampe中的`DownloadAdapter`文件，实现了文件的下载，断点续传，进度展示等通用功能。

### Upload File

EasyHttp提供`EasyHttpClient.uploadFile`接口用来上传文件。接口如下：

```
public static <T> void uploadFile(String url, String filePath, EasyCallback<T> callback) 
```

## TODO list


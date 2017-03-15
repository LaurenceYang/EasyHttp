# EasyHttp
[![License](https://img.shields.io/badge/license-Apache%202-green.svg)](https://www.apache.org/licenses/LICENSE-2.0)
[![Download](https://api.bintray.com/packages/laurenceyanger/maven/easyhttp-laurenceyanger/images/download.svg) ](https://bintray.com/laurenceyanger/maven/easyhttp-laurenceyanger/_latestVersion)
    Support RxJava[![Download](https://api.bintray.com/packages/laurenceyanger/maven/rxeasyhttp/images/download.svg) ](https://bintray.com/laurenceyanger/maven/rxeasyhttp/_latestVersion)
## 特性
* 支持Get/Post请求
* Get/Post请求完美支持String，Json，自定义对象返回
* Get支持全局缓存设置及不同请求不同缓存设置
* 支持Callback和RxJava2两种形式回调
* 支持文件下载及下载管理
* 支持文件下载的断点续传
* 支持文件上传
* 使用简单；轻量级代码；结构清晰
* 基于okhttp3
* 下载管理数据库使用greendao


## 最新版本
* v0.7.0 - 2017.03.15 - Get/Post请求支持RxJava2
* v0.6.0 - 2017.02.27 - Get/Post请求完美支持String，Json，自定义对象返回；下载模块添加异常检测及容错处理
* v0.5.0 - 2017.02.21 - 初版发布，支持Get、Post、下载、上传、断点续传、不同缓存策略等


## ScreenShot
![main](https://github.com/LaurenceYang/EasyHttp/blob/master/assert/GIF.gif) 

## 使用指南

### With Gradle

在全局`build.gradle`文件的`repositories`添加如下所示配置：

```java
jcenter()
```
在module的`build.gradle`文件的`dependencies`区域内添加如下所示配置：

```java
compile 'com.yang.easyhttp:easyhttp:0.7.0'
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

EasyHttp提供`EasyHttpClient.get`接口用来异步请求网络数据。</br>返回自定义对象eg:

```java
EasyHttpClient.get(url, new EasyCustomCallback<Entity>() {
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

返回的是Json对象时，可以使用`EasyJsonCallback`作为回调。</br>

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

同Get请求类似，返回的是Json数据，可以使用`EasyJsonCallback`类来作为回调。</br>

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

### 支持RxJava
EasyHttp提供RxJava2的扩展，如需使用RxJava2回调方式，
在module的`build.gradle`文件的`dependencies`区域内如下所示进行配置：
                                     
```java
compile 'com.yang.easyhttp:easyhttp:0.7.0'
compile 'com.yang.rxeasyhttp:rxeasyhttp:0.7.0'

```
### Get请求的RxJava形式
```
 RxEasyHttp.get(url.toString(), new RxEasyStringConverter())
     .doOnSubscribe(new Consumer<Subscription>() {
         @Override
         public void accept(@NonNull Subscription subscription) throws Exception {
             dialog.show();
             body.setText("");
         }
     })
     .observeOn(AndroidSchedulers.mainThread())
     .subscribe(new FlowableSubscriber<String>() {
         @Override
         public void onSubscribe(Subscription s) {
             s.request(Long.MAX_VALUE);
             dialog.show();
             body.setText("");
         }

         @Override
         public void onNext(String response) {
             body.setText(response);
         }

         @Override
         public void onError(Throwable t) {
             body.setText(t.toString());
         }

         @Override
         public void onComplete() {
             dialog.cancel();
         }
     });
```
### Post请求的RxJava形式
```
RxEasyHttp.post(url, params, new RxEasyCustomConverter<PostEntity>() {
        @Override
        public void doNothing() {}
    })
    .observeOn(AndroidSchedulers.mainThread())
    .subscribe(new FlowableSubscriber<PostEntity>() {

        @Override
        public void onSubscribe(Subscription s) {
            s.request(Long.MAX_VALUE);
            dialog.show();
        }

        @Override
        public void onNext(PostEntity entity) {
            Toast.makeText(RxPostActivity.this, "提交成功", Toast.LENGTH_LONG).show();
            result.setText("status : " + entity.getStatus() + "\n" +
                    "message : " + entity.getMessage());

        }

        @Override
        public void onError(Throwable t) {
            Toast.makeText(RxPostActivity.this, "提交失败", Toast.LENGTH_LONG).show();
            result.setText(t.getMessage());
            dialog.cancel();
        }

        @Override
        public void onComplete() {
            dialog.cancel();
        }
    });
```
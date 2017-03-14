package com.yang.easyhttp.manager;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.yang.easyhttp.Interceptor.EasyCacheInterceptor;
import com.yang.easyhttp.Interceptor.EasyLoggingInterceptor;
import com.yang.easyhttp.Interceptor.EasyUserAgentInterceptor;
import com.yang.easyhttp.cache.EasyCacheDir;
import com.yang.easyhttp.cache.EasyCacheTime;
import com.yang.easyhttp.cache.EasyCacheType;
import com.yang.easyhttp.callback.EasyCallback;
import com.yang.easyhttp.config.EasyHttpConfig;
import com.yang.easyhttp.request.EasyRequestParams;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by yangy on 2016/9/14.
 */
public class EasyHttpClientManager {
	private final static String TAG = "EasyHttpClientManager";
	private static EasyHttpClientManager mInstance = null;

	private OkHttpClient mNoCacheOKHttpClient;
	private OkHttpClient mCacheShortOKHttpClient;
	private OkHttpClient mCacheMidOKHttpClient;
	private OkHttpClient mCacheLongOKHttpClient;

	private Context mContext;
	private boolean mDebug;
	private Handler mHandler;
	private Gson mGson;
	private EasyHttpConfig mConfig;

	private static final ReentrantLock LOCK = new ReentrantLock();

	/**
	 * constructor.
	 */
	private EasyHttpClientManager() {
		mDebug = false;
		mGson = new Gson();
	}

	/**
	 * EasyHttpClientManager getInstance.
	 *
	 * @return
	 */
	public static EasyHttpClientManager getInstance() {
		try {
			LOCK.lock();
			if (null == mInstance) {
				mInstance = new EasyHttpClientManager();
			}
		} finally {
			LOCK.unlock();
		}

		return mInstance;
	}

	/**
	 * 初始化上下文.
	 * @param context
	 */
	public void init(Context context) {
		mContext = context;
		mHandler = new Handler(Looper.getMainLooper());
		// default http config.
		mConfig = new EasyHttpConfig.Builder()
				.setCacheDir(EasyCacheDir.SD_PATH + mContext.getPackageName())
				.setCacheTime(EasyCacheTime.CACHE_TIME_SHORT, EasyCacheTime.CACHE_TIME_MID, EasyCacheTime.CACHE_TIME_LONG)
				.setCacheMaxSize(5 * 1024 * 1024, 5 * 1024 * 1024, 20 * 1024 * 1024)
				.setGlobalCacheType(EasyCacheType.CACHE_TYPE_NO_SETTING)
				.build();
	}

	/**
	 * 初始化上下文，带配置.
	 * @param context
	 * @param config
	 */
	public void init(Context context, EasyHttpConfig config) {
		init(context);
		if (!TextUtils.isEmpty(config.getCacheDir())) {
			mConfig.setCacheDir(config.getCacheDir());
		}
		if (config.getCacheShortTime() > 0) {
			mConfig.setCacheShortTime(config.getCacheShortTime());
		}
		if (config.getCacheMidTime() > 0) {
			mConfig.setCacheMidTime(config.getCacheMidTime());
		}
		if (config.getCacheLongTime() > 0) {
			mConfig.setCacheLongTime(config.getCacheLongTime());
		}
		if (config.getCacheShortMaxSize() > 0) {
			mConfig.setCacheShortMaxSize(config.getCacheShortMaxSize());
		}
		if (config.getCacheMidMaxSize() > 0) {
			mConfig.setCacheMidMaxSize(config.getCacheMidMaxSize());
		}
		if (config.getCacheLongMaxSize() > 0) {
			mConfig.setCacheLongMaxSize(config.getCacheLongMaxSize());
		}
		if (config.getGlobalCacheType() > -1) {
			mConfig.setGlobalCacheType(config.getGlobalCacheType());
		}
		if (!TextUtils.isEmpty(config.getUserAgent())) {
			mConfig.setUserAgent(config.getUserAgent());
		}
	}

	/**
	 * 获取上下文.
	 * @return
	 */
	public Context getContext() {
		return mContext;
	}

	/**
	 * 设定debug类型.
	 * @param debug
	 */
	public void setDebug(boolean debug) {
		mDebug = debug;
	}

	/**
	 * 返回是否debug.
	 * @return
	 */
	public boolean isDebug() {
		return mDebug;
	}

	/**
	 * 获取Gson对象.
	 * @return
	 */
	public Gson getGson() {
		return mGson;
	}

	/**
	 * 获取配置.
	 * @return
	 */
	public EasyHttpConfig getConfig() {
		return mConfig;
	}

	/**
	 * getOkHttpClient.
	 * @param cacheType
	 * @return
	 */
	public OkHttpClient getOkHttpClient(int cacheType) {
		OkHttpClient okHttpClient;

		if (cacheType == EasyCacheType.CACHE_TYPE_DEFAULT) {
			okHttpClient = getNoCacheOkHttpClient();
		} else if (cacheType == EasyCacheType.CACHE_TYPE_SHORT) {
			okHttpClient = getCacheShortOkHttpClient();
		} else if (cacheType == EasyCacheType.CACHE_TYPE_MID) {
			okHttpClient = getCacheMidOkHttpClient();
		} else if (cacheType == EasyCacheType.CACHE_TYPE_LONG) {
			okHttpClient = getCacheLongOkHttpClient();
		} else {
			okHttpClient = getNoCacheOkHttpClient();
		}

		return okHttpClient;
	}

	/**
	 * 获取无缓存OkHttpClient.
	 *
	 * @return
	 */
	private synchronized OkHttpClient getNoCacheOkHttpClient() {
		if (mNoCacheOKHttpClient == null) {
			EasyCacheInterceptor interceptor = new EasyCacheInterceptor(EasyCacheType.CACHE_TYPE_DEFAULT);

			OkHttpClient.Builder builder = new OkHttpClient()
					.newBuilder()
					.addNetworkInterceptor(interceptor);
			if (!TextUtils.isEmpty(mConfig.getUserAgent())) {
				builder.addNetworkInterceptor(new EasyUserAgentInterceptor(mConfig.getUserAgent()));
			}
			if (mDebug) {
				builder.addNetworkInterceptor(new EasyLoggingInterceptor());
			}

			mNoCacheOKHttpClient = builder.build();
		}

		return mNoCacheOKHttpClient;
	}

	/**
	 * 获取短期缓存OkHttpClient.
	 *
	 * @return
	 */
	private synchronized OkHttpClient getCacheShortOkHttpClient() {
		if (mCacheShortOKHttpClient == null) {
			File httpCacheDirectory = new File(mConfig.getCacheDir() + EasyCacheDir.CACHE_SHORT_DIR);
			if (!httpCacheDirectory.exists()) {
				httpCacheDirectory.mkdirs();
			}
			Cache cache = new Cache(httpCacheDirectory, mConfig.getCacheShortMaxSize());
			EasyCacheInterceptor interceptor = new EasyCacheInterceptor(EasyCacheType.CACHE_TYPE_SHORT);

			OkHttpClient.Builder builder = new OkHttpClient().newBuilder()
					.addNetworkInterceptor(interceptor)
					.cache(cache);
			if (!TextUtils.isEmpty(mConfig.getUserAgent())) {
				builder.addNetworkInterceptor(new EasyUserAgentInterceptor(mConfig.getUserAgent()));
			}
			if (mDebug) {
				builder.addNetworkInterceptor(new EasyLoggingInterceptor());
			}

			mCacheShortOKHttpClient = builder.build();
		}

		return mCacheShortOKHttpClient;
	}

	/**
	 * 获取中期缓存OkHttpClient.
	 *
	 * @return
	 */
	private synchronized OkHttpClient getCacheMidOkHttpClient() {
		if (mCacheMidOKHttpClient == null) {
			File httpCacheDirectory = new File(mConfig.getCacheDir() + EasyCacheDir.CACHE_MID_DIR);
			if (!httpCacheDirectory.exists()) {
				httpCacheDirectory.mkdirs();
			}
			Cache cache = new Cache(httpCacheDirectory, mConfig.getCacheMidMaxSize());
			EasyCacheInterceptor interceptor = new EasyCacheInterceptor(EasyCacheType.CACHE_TYPE_MID);

			OkHttpClient.Builder builder = new OkHttpClient().newBuilder()
					.addNetworkInterceptor(interceptor)
					.cache(cache);
			if (!TextUtils.isEmpty(mConfig.getUserAgent())) {
				builder.addNetworkInterceptor(new EasyUserAgentInterceptor(mConfig.getUserAgent()));
			}
			if (mDebug) {
				builder.addNetworkInterceptor(new EasyLoggingInterceptor());
			}

			mCacheMidOKHttpClient = builder.build();
		}

		return mCacheMidOKHttpClient;
	}

	/**
	 * 获取长期缓存OkHttpClient.
	 *
	 * @return
	 */
	private synchronized OkHttpClient getCacheLongOkHttpClient() {
		if (mCacheLongOKHttpClient == null) {
			File httpCacheDirectory = new File(mConfig.getCacheDir() + EasyCacheDir.CACHE_LONG_DIR);
			if (!httpCacheDirectory.exists()) {
				httpCacheDirectory.mkdirs();
			}
			Cache cache = new Cache(httpCacheDirectory, mConfig.getCacheLongMaxSize());

			EasyCacheInterceptor interceptor = new EasyCacheInterceptor(EasyCacheType.CACHE_TYPE_LONG);

			OkHttpClient.Builder builder = new OkHttpClient().newBuilder()
					.addNetworkInterceptor(interceptor)
					.cache(cache);
			if (!TextUtils.isEmpty(mConfig.getUserAgent())) {
				builder.addNetworkInterceptor(new EasyUserAgentInterceptor(mConfig.getUserAgent()));
			}
			if (mDebug) {
				builder.addNetworkInterceptor(new EasyLoggingInterceptor());
			}

			mCacheLongOKHttpClient = builder.build();
		}

		return mCacheLongOKHttpClient;
	}

	/**
	 * 拼接url
	 * @param url
	 * @param params
	 * @return
	 */
	public String buildUrl(String url, EasyRequestParams params) {
		if (params != null) {
			return url + "&" + params.toString();
		} else {
			return url;
		}
	}

	/**
	 * get异步请求
	 * @param url
	 * @param requestParams
	 * @param cacheType
	 * @param callBack
	 * @param <T>
	 */
	public <T> void get(String url, EasyRequestParams requestParams, int cacheType, final EasyCallback<T> callBack) {
		final Request request = new Request.Builder().url(buildUrl(url, requestParams)).build();
		if (callBack != null) {
			// UI线程.
			callBack.onStart();
		}

		// 接口没有单独设定缓存类型，使用全局缓存类型.
		if (cacheType == EasyCacheType.CACHE_TYPE_NO_SETTING) {
			cacheType = mConfig.getGlobalCacheType();
		}

		// 异步.
		getOkHttpClient(cacheType).newCall(request).enqueue(new Callback() {
			@Override
			public void onFailure(Call call, final IOException e) {
				// 子线程
				if (callBack == null) {
					return;
				}

				mHandler.post(new Runnable() {
					@Override
					public void run() {
						callBack.onFailure(e,
								"the request could not be executed due to cancellation, a connectivity problem or timeout");
						callBack.onFinish();
					}
				});
			}

			@Override
			public void onResponse(Call call, final Response response) throws IOException {
				// 子线程
				if (callBack == null) {
					return;
				}

				if (!response.isSuccessful()) {
					final IOException e = new IOException("Unexpected code " + response);
					mHandler.post(new Runnable() {
						@Override
						public void run() {
							callBack.onFailure(e, "the HTTP response was not successfully");
							callBack.onFinish();
						}
					});
					return;
				}

				try {
					final String content = response.body().string();
					response.body().close();
					final T data = callBack.convert(content);

					mHandler.post(new Runnable() {
						@Override
						public void run() {
							callBack.onSuccess(data);
							callBack.onFinish();
						}
					});
				} catch (final Exception e) {
					mHandler.post(new Runnable() {
						@Override
						public void run() {
							callBack.onFailure(e, "Exception");
							callBack.onFinish();
						}
					});
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * post异步请求，
	 * @param url
	 * @param requestParams
	 * @param callBack
	 * @param <T>
	 */
	public <T> void post(String url, EasyRequestParams requestParams, final EasyCallback<T> callBack) {
		FormBody.Builder builder = new FormBody.Builder();
		ConcurrentHashMap<String, String> paramsMap = requestParams.getUrlParams();
		for (ConcurrentHashMap.Entry<String, String> entry : paramsMap.entrySet()) {
			builder.add(entry.getKey(), entry.getValue());
		}

		RequestBody requestBody = builder.build();
		final Request request = new Request.Builder()
				.url(url)
				.post(requestBody)
				.build();

		if (callBack != null) {
			// UI线程.
			callBack.onStart();
		}

		// 异步.
		getNoCacheOkHttpClient().newCall(request).enqueue(new Callback() {
			@Override
			public void onFailure(Call call, final IOException e) {
				// 子线程
				if (callBack == null) {
					return;
				}

				mHandler.post(new Runnable() {
					@Override
					public void run() {
						callBack.onFailure(e,
								"the request could not be executed due to cancellation, a connectivity problem or timeout");
						callBack.onFinish();
					}
				});
			}

			@Override
			public void onResponse(Call call, final Response response) throws IOException {
				// 子线程
				if (callBack == null) {
					return;
				}

				if (!response.isSuccessful()) {
					final IOException e = new IOException("Unexpected code " + response);
					mHandler.post(new Runnable() {
						@Override
						public void run() {
							callBack.onFailure(e, "the HTTP response was not successfully");
							callBack.onFinish();
						}
					});
					return;
				}

				try {
					final String content = response.body().string();
					response.body().close();
					final T data = callBack.convert(content);

					mHandler.post(new Runnable() {
						@Override
						public void run() {
							callBack.onSuccess(data);
							callBack.onFinish();
						}
					});
				} catch (final Exception e) {
					mHandler.post(new Runnable() {
						@Override
						public void run() {
							callBack.onFailure(e, "Exception");
							callBack.onFinish();
						}
					});
					e.printStackTrace();
				}
			}
		});
	}

	public <T> void uploadFile(String url, String filePath, final EasyCallback<T> callBack) {
		File file = new File(filePath);
		RequestBody requestBody = RequestBody.create(MediaType.parse("application/octet-stream"), file);

		final Request request = new Request.Builder()
				.url(url)
				.post(requestBody)
				.build();

		if (callBack != null) {
			// UI线程.
			callBack.onStart();
		}

		// 异步.
		getNoCacheOkHttpClient().newCall(request).enqueue(new Callback() {
			@Override
			public void onFailure(Call call, final IOException e) {
				// 子线程
				if (callBack == null) {
					return;
				}

				mHandler.post(new Runnable() {
					@Override
					public void run() {
						callBack.onFailure(e,
								"the request could not be executed due to cancellation, a connectivity problem or timeout");
						callBack.onFinish();
					}
				});
			}

			@Override
			public void onResponse(Call call, final Response response) throws IOException {
				// 子线程
				if (callBack == null) {
					return;
				}

				if (!response.isSuccessful()) {
					final IOException e = new IOException("Unexpected code " + response);
					mHandler.post(new Runnable() {
						@Override
						public void run() {
							callBack.onFailure(e, "the HTTP response was not successfully");
							callBack.onFinish();
						}
					});
					return;
				}

				try {
					final String content = response.body().string();
					response.body().close();
					final T data = callBack.convert(content);

					mHandler.post(new Runnable() {
						@Override
						public void run() {
							callBack.onSuccess(data);
							callBack.onFinish();
						}
					});
				} catch (final Exception e) {
					mHandler.post(new Runnable() {
						@Override
						public void run() {
							callBack.onFailure(e, "Exception");
							callBack.onFinish();
						}
					});
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * 取消请求.
	 * @param type
     */
	public void cancelRequest(int type) {
		getOkHttpClient(type).dispatcher().cancelAll();
	}
}

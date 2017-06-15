package com.yang.easyhttprx.manager;

import com.yang.easyhttp.cache.EasyCacheType;
import com.yang.easyhttp.manager.EasyHttpClientManager;
import com.yang.easyhttp.request.EasyRequestParams;
import com.yang.easyhttprx.converter.RxEasyConverter;

import java.io.File;
import java.util.concurrent.ConcurrentHashMap;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by yangy on 2017/3/14.
 */

public class RxEasyHttpManager {
	private static RxEasyHttpManager mInstance = null;

	public static synchronized RxEasyHttpManager getInstance() {
		if (null == mInstance) {
			mInstance = new RxEasyHttpManager();
		}

		return mInstance;
	}

	/**
	 * Get请求的Rxjava方式.
	 * @param url
	 * @param requestParams
	 * @param cacheType
	 * @return
	 */
	public <T> Flowable<T> get(String url, EasyRequestParams requestParams, int cacheType, RxEasyConverter<T> rxEasyConverter) {
		final Request request = new Request.Builder().url(EasyHttpClientManager.getInstance().buildUrl(url, requestParams)).build();
		// 接口没有单独设定缓存类型，使用全局缓存类型.
		if (cacheType == EasyCacheType.CACHE_TYPE_NO_SETTING) {
			cacheType = EasyHttpClientManager.getInstance().getConfig().getGlobalCacheType();
		}

		Call call = EasyHttpClientManager.getInstance().getOkHttpClient(cacheType).newCall(request);

		return Flowable.create(new CallFlowableOnSubscribe(call, rxEasyConverter), BackpressureStrategy.BUFFER)
				.subscribeOn(Schedulers.io());
	}

	/**
	 * Post请求的Rxjava方式.
	 * @param url
	 * @param requestParams
	 * @return
	 */
	public <T> Flowable<T> post(String url, EasyRequestParams requestParams, RxEasyConverter<T> rxEasyConverter) {
		FormBody.Builder builder = new FormBody.Builder();
		ConcurrentHashMap<String, String> paramsMap = requestParams.getRequestParams();
		for (ConcurrentHashMap.Entry<String, String> entry : paramsMap.entrySet()) {
			builder.add(entry.getKey(), entry.getValue());
		}

		RequestBody requestBody = builder.build();
		final Request request = new Request.Builder()
				.url(url)
				.post(requestBody)
				.build();

		Call call = EasyHttpClientManager.getInstance().getOkHttpClient(EasyCacheType.CACHE_TYPE_DEFAULT).newCall(request);

		return Flowable.create(new CallFlowableOnSubscribe(call,rxEasyConverter), BackpressureStrategy.BUFFER)
				.subscribeOn(Schedulers.io());
	}

	/**
	 * upload file.
	 * @param url
	 * @param filePath
	 * @return
	 */
	public <T> Flowable<T> uploadFile(String url, String filePath, RxEasyConverter<T> rxEasyConverter) {
		File file = new File(filePath);
		RequestBody requestBody = RequestBody.create(MediaType.parse("application/octet-stream"), file);

		final Request request = new Request.Builder()
				.url(url)
				.post(requestBody)
				.build();

		Call call = EasyHttpClientManager.getInstance().getOkHttpClient(EasyCacheType.CACHE_TYPE_DEFAULT).newCall(request);

		return Flowable.create(new CallFlowableOnSubscribe(call, rxEasyConverter), BackpressureStrategy.BUFFER)
				.subscribeOn(Schedulers.io());
	}

	class CallFlowableOnSubscribe<T> implements FlowableOnSubscribe<T> {
		private Call call;
		private RxEasyConverter<T> rxEasyConverter;

		public CallFlowableOnSubscribe(Call call, RxEasyConverter<T> rxEasyConverter) {
			this.call = call;
			this.rxEasyConverter = rxEasyConverter;
		}

		@Override
		public void subscribe(FlowableEmitter<T> e) throws Exception {
			try {
				Response response = call.execute();

				if (!e.isCancelled()) {
					if (response.isSuccessful()) {
						e.onNext(rxEasyConverter.convert(response.body().string()));
					} else {
						e.onError(new Throwable("response is unsuccessful"));
					}
				}
			} catch (Throwable t) {
				if (!e.isCancelled()) {
					e.onError(t);
				}
			} finally {
				e.onComplete();
			}
		}
	}
}

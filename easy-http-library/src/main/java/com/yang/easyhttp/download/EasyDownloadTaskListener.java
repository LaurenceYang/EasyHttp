package com.yang.easyhttp.download;

/**
 * Created by yangy on 2017/2/16.
 */
public interface EasyDownloadTaskListener {
	void onQueue(EasyDownloadTask downloadTask);
	/**
	 * connecting
	 */
	void onConnecting(EasyDownloadTask downloadTask);

	/**
	 * download start
	 */
	void onDownloadStart(EasyDownloadTask downloadTask);

	/**
	 * downloading
	 */
	void onDownloading(EasyDownloadTask downloadTask);

	/**
	 * pauseTask
	 */
	void onPause(EasyDownloadTask downloadTask);

	/**
	 * cancel
	 */
	void onCancel(EasyDownloadTask downloadTask);

	/**
	 * success
	 */
	void onFinish(EasyDownloadTask downloadTask);

	/**
	 * failure
	 */
	void onError(EasyDownloadTask downloadTask, int code);
}

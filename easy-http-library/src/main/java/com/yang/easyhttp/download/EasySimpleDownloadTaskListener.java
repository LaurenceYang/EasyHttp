package com.yang.easyhttp.download;

/**
 * Created by yangy on 2017/2/16.
 */
public interface EasySimpleDownloadTaskListener {
	/**
	 * queue
	 * @param downloadTask
	 */
	void onQueue(EasySimpleDownloadTask downloadTask);

	/**
	 * connecting
	 * @param downloadTask
     */
	void onConnecting(EasySimpleDownloadTask downloadTask);

	/**
	 * downloading
	 * @param downloadTask
	 */
	void onDownloading(EasySimpleDownloadTask downloadTask);

	/**
	 * pauseTask
	 * @param downloadTask
	 */
	void onPause(EasySimpleDownloadTask downloadTask);

	/**
	 * cancel
	 * @param downloadTask
	 */
	void onCancel(EasySimpleDownloadTask downloadTask);

	/**
	 * success
	 * @param downloadTask
	 */
	void onFinish(EasySimpleDownloadTask downloadTask);

	/**
	 * failure
	 * @param downloadTask
	 */
	void onError(EasySimpleDownloadTask downloadTask, int code);
}

package com.yang.easyhttp.download;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.yang.easyhttp.cache.EasyCacheType;
import com.yang.easyhttp.manager.EasyHttpClientManager;
import com.yang.easyhttp.utils.EasyFileUtils;
import com.yang.easyhttp.utils.EasyIOUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.SocketTimeoutException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by yangy on 2017/2/16.
 */
public class EasySimpleDownloadTask implements Runnable {
	private OkHttpClient mClient;
	private EasySimpleDownloadTaskListener mSimpleDownloadTaskListener;
	private EasyTaskEntity mTaskEntity;
	private Handler mHandler = new Handler(Looper.getMainLooper()) {
		@Override
		public void handleMessage(Message msg) {
			if (mSimpleDownloadTaskListener == null) {
				return;
			}
			int code = msg.what;
			switch (code) {
				case EasyTaskStatus.TASK_STATUS_INIT:
					break;
				case EasyTaskStatus.TASK_STATUS_QUEUE:
					mSimpleDownloadTaskListener.onQueue(EasySimpleDownloadTask.this);
					break;
				case EasyTaskStatus.TASK_STATUS_CONNECTING:
					mSimpleDownloadTaskListener.onConnecting(EasySimpleDownloadTask.this);
					break;
				case EasyTaskStatus.TASK_STATUS_DOWNLOADING:
					mSimpleDownloadTaskListener.onDownloading(EasySimpleDownloadTask.this);
					break;
				case EasyTaskStatus.TASK_STATUS_PAUSE:
					mSimpleDownloadTaskListener.onPause(EasySimpleDownloadTask.this);
					break;
				case EasyTaskStatus.TASK_STATUS_CANCEL:
					mSimpleDownloadTaskListener.onCancel(EasySimpleDownloadTask.this);
					break;
				case EasyTaskStatus.TASK_STATUS_LINK_FAILURE_ERROR:
					mSimpleDownloadTaskListener.onError(EasySimpleDownloadTask.this, EasyTaskStatus.TASK_STATUS_LINK_FAILURE_ERROR);
					break;
				case EasyTaskStatus.TASK_STATUS_REQUEST_ERROR:
					mSimpleDownloadTaskListener.onError(EasySimpleDownloadTask.this, EasyTaskStatus.TASK_STATUS_REQUEST_ERROR);
					break;
				case EasyTaskStatus.TASK_STATUS_STORAGE_ERROR:
					mSimpleDownloadTaskListener.onError(EasySimpleDownloadTask.this, EasyTaskStatus.TASK_STATUS_STORAGE_ERROR);
					break;
				case EasyTaskStatus.TASK_STATUS_FINISH:
					mSimpleDownloadTaskListener.onFinish(EasySimpleDownloadTask.this);
					break;
				default:
					break;
			}
		}
	};

	public EasySimpleDownloadTask(EasyTaskEntity taskEntity) {
		mClient = EasyHttpClientManager.getInstance().getOkHttpClient(EasyCacheType.CACHE_TYPE_DEFAULT);
		mTaskEntity = taskEntity;
	}

	public void setClient(OkHttpClient client) {
		this.mClient = client;
	}

	public void setSimpleDownloadTaskListener(EasySimpleDownloadTaskListener simpleDownloadTaskListener) {
		this.mSimpleDownloadTaskListener = simpleDownloadTaskListener;
	}

	public EasyTaskEntity getTaskEntity() {
		return mTaskEntity;
	}

	@Override
	public void run() {
		InputStream inputStream = null;
		BufferedInputStream bufferedInputStream = null;
		RandomAccessFile randomAccessFile = null;

		try {
			String saveDirPath = mTaskEntity.getSaveDirPath();
			if (TextUtils.isEmpty(saveDirPath)) {
				saveDirPath = EasyFileUtils.getDefaultFilePath(EasyHttpClientManager.getInstance().getContext());
				mTaskEntity.setSaveDirPath(saveDirPath);
			}

			File file = new File(saveDirPath);
			if (!file.exists()) {
				boolean created = file.mkdirs();
				if (!created) {
					throw new FileNotFoundException("save dir path not created");
				}
			}

			String saveFileName = mTaskEntity.getSaveFileName();
			if (TextUtils.isEmpty(saveFileName)) {
				saveFileName = EasyFileUtils.getFileNameFromUrl(mTaskEntity.getDownloadUrl());
				mTaskEntity.setSaveFileName(saveFileName);
			}

			File saveFile = new File(saveDirPath, saveFileName);
			File saveTempFile = new File(saveDirPath, saveFileName + ".tmp");
			randomAccessFile = new RandomAccessFile(saveTempFile, "rwd");

			if (mTaskEntity.getTaskStatus() == EasyTaskStatus.TASK_STATUS_CANCEL ||
					mTaskEntity.getTaskStatus() == EasyTaskStatus.TASK_STATUS_PAUSE) {
				// 任何步骤都可能插入暂停操作.
				EasyIOUtils.close(bufferedInputStream, inputStream, randomAccessFile);
				return;
			} else {
				mTaskEntity.setTaskStatus(EasyTaskStatus.TASK_STATUS_CONNECTING);
				mHandler.sendEmptyMessage(EasyTaskStatus.TASK_STATUS_CONNECTING);
			}

			// 异常检测及下载错误校正.
			long completedSize = mTaskEntity.getCompletedSize();
			long fileLength = randomAccessFile.length();
//			Log.d("EasyDownload", "taskId[" + mTaskEntity.getTaskId() + "],fileLength[" + fileLength + "],completedSize[" + completedSize + "]");

			if (completedSize > 0 && fileLength != completedSize) {
				// 比较激进的容错方案.
				completedSize = fileLength;
				mTaskEntity.setCompletedSize(fileLength);
			}

			Request request = new Request.Builder()
					.url(mTaskEntity.getDownloadUrl())
					.header("RANGE", "bytes=" + completedSize + "-")
					.build();

			randomAccessFile.seek(completedSize);

			Response response = mClient.newCall(request).execute();

			if (response.isSuccessful()) {
				ResponseBody responseBody = response.body();
				if (responseBody != null) {
					if (mTaskEntity.getTaskStatus() == EasyTaskStatus.TASK_STATUS_CANCEL ||
							mTaskEntity.getTaskStatus() == EasyTaskStatus.TASK_STATUS_PAUSE) {
						// 任何步骤都可能插入暂停操作.
						EasyIOUtils.close(bufferedInputStream, inputStream, randomAccessFile);
						return;
					} else {
						mTaskEntity.setTaskStatus(EasyTaskStatus.TASK_STATUS_DOWNLOADING);
					}
					long contentLength = responseBody.contentLength();
					if (contentLength == -1) {
						// 下载链接存在问题.
						mTaskEntity.setTaskStatus(EasyTaskStatus.TASK_STATUS_LINK_FAILURE_ERROR);
						mHandler.sendEmptyMessage(EasyTaskStatus.TASK_STATUS_LINK_FAILURE_ERROR);
						return;
					}
//					Log.d("EasyDownload", "taskId[" + mTaskEntity.getTaskId() + "],contentLength[" + contentLength + "]");
					mTaskEntity.setTotalSize(contentLength + completedSize);

					long fileTotalSize = mTaskEntity.getTotalSize();

					double updateSize = fileTotalSize / 100;
					inputStream = responseBody.byteStream();
					bufferedInputStream = new BufferedInputStream(inputStream);
					byte[] buffer = new byte[1024];
					int length;
					int buffOffset = 0;

					while ((length = bufferedInputStream.read(buffer)) > 0
							&& mTaskEntity.getTaskStatus() != EasyTaskStatus.TASK_STATUS_CANCEL
							&& mTaskEntity.getTaskStatus() != EasyTaskStatus.TASK_STATUS_PAUSE) {
						randomAccessFile.write(buffer, 0, length);
						completedSize += length;
						buffOffset += length;

						mTaskEntity.setCompletedSize(completedSize);
						//避免一直调用数据库.
						if (buffOffset >= updateSize) {
							buffOffset = 0;
							mHandler.sendEmptyMessage(EasyTaskStatus.TASK_STATUS_DOWNLOADING);
//							Log.d("EasyDownload", "taskId[" + mTaskEntity.getTaskId() + "],totalSize[" + fileTotalSize + "],completedSize[" + completedSize + "]");
						}
						if (completedSize == fileTotalSize) {
//							Log.d("EasyDownload", "taskId[" + mTaskEntity.getTaskId() + "],totalSize[" + fileTotalSize + "],completedSize[" + completedSize + "]");
							mHandler.sendEmptyMessage(EasyTaskStatus.TASK_STATUS_DOWNLOADING);
							saveTempFile.renameTo(saveFile);
							mTaskEntity.setTaskStatus(EasyTaskStatus.TASK_STATUS_FINISH);
							mHandler.sendEmptyMessage(EasyTaskStatus.TASK_STATUS_FINISH);
						}
					}


//					Log.d("EasyDownload", "taskId[" + mTaskEntity.getTaskId() + "],exception：totalSize[" + fileTotalSize + "],completedSize[" + completedSize + "]");
				}
			} else if (response.code() == 403 || response.code() == 404) {
//				Log.d("EasyDownload", "taskId[" + mTaskEntity.getTaskId() + "],response.code()[" + response.code() + "]");
				mTaskEntity.setTaskStatus(EasyTaskStatus.TASK_STATUS_LINK_FAILURE_ERROR);
				mHandler.sendEmptyMessage(EasyTaskStatus.TASK_STATUS_LINK_FAILURE_ERROR);
			} else {
//				Log.d("EasyDownload", "taskId[" + mTaskEntity.getTaskId() + "],response.code()[" + response.code() + "]");
				mTaskEntity.setTaskStatus(EasyTaskStatus.TASK_STATUS_REQUEST_ERROR);
				mHandler.sendEmptyMessage(EasyTaskStatus.TASK_STATUS_REQUEST_ERROR);
			}

		} catch (FileNotFoundException e) {
//			Log.d("EasyDownload", "taskId[" + mTaskEntity.getTaskId() + "]FileNotFoundException");
			mTaskEntity.setTaskStatus(EasyTaskStatus.TASK_STATUS_STORAGE_ERROR);
			mHandler.sendEmptyMessage(EasyTaskStatus.TASK_STATUS_STORAGE_ERROR);
			e.printStackTrace();
		} catch (SocketTimeoutException e) {
//			Log.d("EasyDownload", "taskId[" + mTaskEntity.getTaskId() + "]SocketTimeoutException");
			mTaskEntity.setTaskStatus(EasyTaskStatus.TASK_STATUS_REQUEST_ERROR);
			mHandler.sendEmptyMessage(EasyTaskStatus.TASK_STATUS_REQUEST_ERROR);
			e.printStackTrace();
		}  catch (IOException e) {
//			Log.d("EasyDownload", "taskId[" + mTaskEntity.getTaskId() + "]IOException");
			mTaskEntity.setTaskStatus(EasyTaskStatus.TASK_STATUS_REQUEST_ERROR);
			mHandler.sendEmptyMessage(EasyTaskStatus.TASK_STATUS_REQUEST_ERROR);
			e.printStackTrace();
		} finally {
			EasyIOUtils.close(bufferedInputStream, inputStream, randomAccessFile);
		}
	}

	public void pause() {
		mTaskEntity.setTaskStatus(EasyTaskStatus.TASK_STATUS_PAUSE);
		mHandler.sendEmptyMessage(EasyTaskStatus.TASK_STATUS_PAUSE);
	}

	public void init() {
		mTaskEntity.setTaskStatus(EasyTaskStatus.TASK_STATUS_INIT);
		mHandler.sendEmptyMessage(EasyTaskStatus.TASK_STATUS_INIT);
	}

	public void queue() {
		mTaskEntity.setTaskStatus(EasyTaskStatus.TASK_STATUS_QUEUE);
		mHandler.sendEmptyMessage(EasyTaskStatus.TASK_STATUS_QUEUE);
	}

	public void cancel() {
		mTaskEntity.setTaskStatus(EasyTaskStatus.TASK_STATUS_CANCEL);
		mHandler.sendEmptyMessage(EasyTaskStatus.TASK_STATUS_CANCEL);
	}
}
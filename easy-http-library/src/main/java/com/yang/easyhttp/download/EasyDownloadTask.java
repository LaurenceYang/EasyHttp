package com.yang.easyhttp.download;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import com.yang.easyhttp.cache.EasyCacheType;
import com.yang.easyhttp.db.EasyDaoManager;
import com.yang.easyhttp.manager.EasyHttpClientManager;
import com.yang.easyhttp.utils.EasyIOUtils;
import com.yang.easyhttp.utils.EasyFileUtils;

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
public class EasyDownloadTask implements Runnable {
	private OkHttpClient mClient;
	private EasyDownloadTaskListener mDownloadTaskListener;
	private EasyTaskEntity mTaskEntity;
	private Handler mHandler = new Handler(Looper.getMainLooper()) {
		@Override
		public void handleMessage(Message msg) {
			if (mDownloadTaskListener == null) {
				return;
			}
			int code = msg.what;
			switch (code) {
				case EasyTaskStatus.TASK_STATUS_INIT:
					break;
				case EasyTaskStatus.TASK_STATUS_QUEUE:
					mDownloadTaskListener.onQueue(EasyDownloadTask.this);
					break;
				case EasyTaskStatus.TASK_STATUS_CONNECTING:
					mDownloadTaskListener.onConnecting(EasyDownloadTask.this);
					break;
				case EasyTaskStatus.TASK_STATUS_DOWNLOAD_START:
					mDownloadTaskListener.onDownloadStart(EasyDownloadTask.this);
					break;
				case EasyTaskStatus.TASK_STATUS_DOWNLOADING:
					mDownloadTaskListener.onDownloading(EasyDownloadTask.this);
					break;
				case EasyTaskStatus.TASK_STATUS_PAUSE:
					mDownloadTaskListener.onPause(EasyDownloadTask.this);
					break;
				case EasyTaskStatus.TASK_STATUS_CANCEL:
					mDownloadTaskListener.onCancel(EasyDownloadTask.this);
					break;
				case EasyTaskStatus.TASK_STATUS_REQUEST_ERROR:
					mDownloadTaskListener.onError(EasyDownloadTask.this, EasyTaskStatus.TASK_STATUS_REQUEST_ERROR);
					break;
				case EasyTaskStatus.TASK_STATUS_STORAGE_ERROR:
					mDownloadTaskListener.onError(EasyDownloadTask.this, EasyTaskStatus.TASK_STATUS_STORAGE_ERROR);
					break;
				case EasyTaskStatus.TASK_STATUS_FINISH:
					mDownloadTaskListener.onFinish(EasyDownloadTask.this);
					break;
				default:
					break;
			}
		}
	};

	public EasyDownloadTask(EasyTaskEntity taskEntity) {
		mClient = EasyHttpClientManager.getInstance().getOkHttpClient(EasyCacheType.CACHE_TYPE_DEFAULT);
		mTaskEntity = taskEntity;
	}

	public void setClient(OkHttpClient client) {
		this.mClient = client;
	}

	public void setDownloadTaskListener(EasyDownloadTaskListener downloadTaskListener) {
		this.mDownloadTaskListener = downloadTaskListener;
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
			}
			mTaskEntity.setSaveDirPath(saveDirPath);

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
			}
			mTaskEntity.setSaveFileName(saveFileName);

			File saveFile = new File(saveDirPath, saveFileName);
			randomAccessFile = new RandomAccessFile(saveFile, "rwd");

			mTaskEntity.setTaskStatus(EasyTaskStatus.TASK_STATUS_CONNECTING);
			mHandler.sendEmptyMessage(EasyTaskStatus.TASK_STATUS_CONNECTING);

			// 异常检测及下载错误校正.
			long completedSize = mTaskEntity.getCompletedSize();
			long totalSize = mTaskEntity.getTotalSize();
			if (randomAccessFile.length() == 0
					|| completedSize > totalSize) {
				completedSize = 0;
				mTaskEntity.setTotalSize(0);
				mTaskEntity.setCompletedSize(0);
				if (EasyDaoManager.instance().queryWithId(mTaskEntity.getTaskId()) != null) {
					EasyDaoManager.instance().delete(mTaskEntity);
				}
			}

			if (EasyDaoManager.instance().queryWithId(mTaskEntity.getTaskId()) != null) {
				EasyDaoManager.instance().update(mTaskEntity);
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
					mTaskEntity.setTaskStatus(EasyTaskStatus.TASK_STATUS_DOWNLOAD_START);
					if (EasyDaoManager.instance().queryWithId(mTaskEntity.getTaskId()) == null) {
						EasyDaoManager.instance().insertOrReplace(mTaskEntity);
						mTaskEntity.setTotalSize(responseBody.contentLength());
					}

					double updateSize = mTaskEntity.getTotalSize() / 100;
					inputStream = responseBody.byteStream();
					bufferedInputStream = new BufferedInputStream(inputStream);
					byte[] buffer = new byte[1024];
					int length;
					int buffOffset = 0;

					while ( (length = bufferedInputStream.read(buffer)) > 0
							&& mTaskEntity.getTaskStatus() != EasyTaskStatus.TASK_STATUS_CANCEL
							&& mTaskEntity.getTaskStatus() != EasyTaskStatus.TASK_STATUS_PAUSE) {
						randomAccessFile.write(buffer, 0, length);
						completedSize += length;
						buffOffset += length;

						mTaskEntity.setCompletedSize(completedSize);
						//避免一直调用数据库.
						if (buffOffset >= updateSize) {
							buffOffset = 0;
							EasyDaoManager.instance().update(mTaskEntity);
							mHandler.sendEmptyMessage(EasyTaskStatus.TASK_STATUS_DOWNLOADING);
						}

						if (completedSize == mTaskEntity.getTotalSize()) {
							mHandler.sendEmptyMessage(EasyTaskStatus.TASK_STATUS_DOWNLOADING);
							mTaskEntity.setTaskStatus(EasyTaskStatus.TASK_STATUS_FINISH);
							mHandler.sendEmptyMessage(EasyTaskStatus.TASK_STATUS_FINISH);
							EasyDaoManager.instance().update(mTaskEntity);
						}
					}

//					if (mTaskEntity.getTotalSize() == -1 && completedSize > 0) {
//						// 返回文件大小未知情况下，直接设为下载完成.
//						mHandler.sendEmptyMessage(EasyTaskStatus.TASK_STATUS_DOWNLOADING);
//						mTaskEntity.setTaskStatus(EasyTaskStatus.TASK_STATUS_FINISH);
//						mHandler.sendEmptyMessage(EasyTaskStatus.TASK_STATUS_FINISH);
//						EasyDaoManager.instance().update(mTaskEntity);
//					}

				}
			} else {
				mTaskEntity.setTaskStatus(EasyTaskStatus.TASK_STATUS_REQUEST_ERROR);
				mHandler.sendEmptyMessage(EasyTaskStatus.TASK_STATUS_REQUEST_ERROR);
			}

		} catch (FileNotFoundException e) {
			mTaskEntity.setTaskStatus(EasyTaskStatus.TASK_STATUS_STORAGE_ERROR);
			mHandler.sendEmptyMessage(EasyTaskStatus.TASK_STATUS_STORAGE_ERROR);
			e.printStackTrace();
		} catch (SocketTimeoutException e) {
			mTaskEntity.setTaskStatus(EasyTaskStatus.TASK_STATUS_REQUEST_ERROR);
			mHandler.sendEmptyMessage(EasyTaskStatus.TASK_STATUS_REQUEST_ERROR);
			e.printStackTrace();
		}  catch (IOException e) {
			e.printStackTrace();
		} finally {
			EasyIOUtils.close(bufferedInputStream, inputStream, randomAccessFile);
		}
	}

	public void pause() {
		mTaskEntity.setTaskStatus(EasyTaskStatus.TASK_STATUS_PAUSE);
		if (EasyDaoManager.instance().queryWithId(mTaskEntity.getTaskId()) == null) {
			EasyDaoManager.instance().insertOrReplace(mTaskEntity);
		} else {
			EasyDaoManager.instance().update(mTaskEntity);
		}
		mHandler.sendEmptyMessage(EasyTaskStatus.TASK_STATUS_PAUSE);
	}

	public void queue() {
		mTaskEntity.setTaskStatus(EasyTaskStatus.TASK_STATUS_QUEUE);
		mHandler.sendEmptyMessage(EasyTaskStatus.TASK_STATUS_QUEUE);
	}

	public void cancel() {
		mTaskEntity.setTaskStatus(EasyTaskStatus.TASK_STATUS_CANCEL);
		if (EasyDaoManager.instance().queryWithId(mTaskEntity.getTaskId()) != null) {
			EasyDaoManager.instance().delete(mTaskEntity);
		}
		mHandler.sendEmptyMessage(EasyTaskStatus.TASK_STATUS_CANCEL);
	}
}
package com.yang.easyhttp.download;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.yang.easyhttp.BuildConfig;
import com.yang.easyhttp.cache.EasyCacheType;
import com.yang.easyhttp.db.EasyDaoManager;
import com.yang.easyhttp.manager.EasyHttpClientManager;
import com.yang.easyhttp.utils.EasyConstants;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * Created by yangy on 2017/2/16.
 */
public class EasyDownloadManager {
	private static EasyDownloadManager mInstance;

	// ok http client
	private OkHttpClient mClient;
	// the thread count
	private int mThreadCount = 1;
	// greenDao seesion
	private DaoSession mDaoSession;
	// ThreadPoolExecutor
	private ThreadPoolExecutor mExecutor;
	// task list
	private Map<String, EasyDownloadTask> mCurrentTaskList;
	// queue
	private LinkedBlockingQueue<Runnable> mQueue;

	private EasyDownloadManager() {

	}

	public static synchronized EasyDownloadManager getInstance() {
		if (mInstance == null) {
			mInstance = new EasyDownloadManager();
		}

		return mInstance;
	}

	public void init(Context context) {
		init(context, getAppropriateThreadCount());
	}

	public void init(Context context, int threadCount) {
		init(context, threadCount, getOkHttpClient());
	}

	public void init(Context context, int threadCount, OkHttpClient client) {
		setupDatabase(context);
		recoveryTaskState();

		mClient = client;
		mThreadCount = threadCount < 1 ? 1 : threadCount <= EasyConstants.MAX_THREAD_COUNT ? threadCount : EasyConstants.MAX_THREAD_COUNT;
		mExecutor = new ThreadPoolExecutor(
				mThreadCount,
				mThreadCount,
				20,
				TimeUnit.MILLISECONDS,
				new LinkedBlockingQueue<Runnable>()
				);
		mCurrentTaskList = new HashMap<>();
		mQueue = (LinkedBlockingQueue<Runnable>) mExecutor.getQueue();
	}

	private void setupDatabase(Context context) {
		DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, "download.db", null);
		SQLiteDatabase db = helper.getWritableDatabase();
		DaoMaster master = new DaoMaster(db);
		mDaoSession = master.newSession();
	}

	public DaoSession getDaoSession() {
		return mDaoSession;
	}

	/**
	 * generate default client
	 */
	private OkHttpClient getOkHttpClient() {
		return EasyHttpClientManager.getInstance().getOkHttpClient(EasyCacheType.CACHE_TYPE_DEFAULT);
	}

	/**
	 * @return generate the appropriate thread count.
	 */
	private int getAppropriateThreadCount() {
		return Runtime.getRuntime().availableProcessors() * 2 + 1;
	}

	public void addTask(EasyDownloadTask task) {
		EasyTaskEntity taskEntity = task.getTaskEntity();

		if (taskEntity != null
				&& taskEntity.getTaskStatus() != EasyTaskStatus.TASK_STATUS_DOWNLOADING
				&& taskEntity.getTaskStatus() != EasyTaskStatus.TASK_STATUS_DOWNLOAD_START) {
			task.setClient(mClient);
			mCurrentTaskList.put(taskEntity.getTaskId(), task);

			if (!mQueue.contains(task)) {
				mExecutor.execute(task);
			}

			if (mExecutor.getTaskCount() > mThreadCount) {
				task.queue();
			}
		}
	}

	public void pauseTask(EasyDownloadTask task) {
		if (mQueue.contains(task)) {
			mQueue.remove(task);
		}

		task.pause();
	}

	public void resumeTask(EasyDownloadTask task) {
		addTask(task);
	}

	public void cancelTask(EasyDownloadTask task) {
		if(task == null) {
			return;
		}

		EasyTaskEntity taskEntity = task.getTaskEntity();
		if (taskEntity != null) {
			if (taskEntity.getTaskStatus() == EasyTaskStatus.TASK_STATUS_DOWNLOADING ||
					taskEntity.getTaskStatus() == EasyTaskStatus.TASK_STATUS_DOWNLOAD_START) {
				pauseTask(task);
				mExecutor.remove(task);
			}

			if (mQueue.contains(task)) {
				mQueue.remove(task);
			}

			mCurrentTaskList.remove(taskEntity.getTaskId());

			task.cancel();

			if (!TextUtils.isEmpty(taskEntity.getSaveDirPath())
					&& !TextUtils.isEmpty(taskEntity.getSaveFileName())) {
				File temp = new File(taskEntity.getSaveDirPath(), taskEntity.getSaveFileName());
				if (temp.exists()) {
					if (temp.delete()) {
						if (BuildConfig.DEBUG)
							Log.d("DownloadManager", "delete temp file!");
					}
				}
			}
		}
	}

	public EasyDownloadTask getTask(String taskId) {
		EasyDownloadTask currTask = mCurrentTaskList.get(taskId);
		if (currTask == null) {
			EasyTaskEntity entity = EasyDaoManager.instance().queryWithId(taskId);
			if (entity != null) {
				int status = entity.getTaskStatus();
				currTask = new EasyDownloadTask(entity);
				if (status != EasyTaskStatus.TASK_STATUS_FINISH) {
					mCurrentTaskList.put(taskId, currTask);
				}
			}
		}

		// 如果下载完成,判断本地是否被删除.
		if (currTask != null) {
			EasyTaskEntity entity = currTask.getTaskEntity();
			int status = entity.getTaskStatus();
			if (status == EasyTaskStatus.TASK_STATUS_FINISH) {
				String saveDirPath = entity.getSaveDirPath();
				String saveFileName = entity.getSaveFileName();
				if (TextUtils.isEmpty(saveDirPath) || TextUtils.isEmpty(saveFileName)) {
					return currTask;
				}

				File file = new File(saveDirPath, saveFileName);
				if (!file.exists()) {
					mCurrentTaskList.remove(taskId);
					EasyDaoManager.instance().delete(entity);
				}
			}
		}

		return currTask;
	}

	public boolean isPauseTask(String id) {
		EasyTaskEntity entity = EasyDaoManager.instance().queryWithId(id);
		if (entity != null) {
			File file = new File(entity.getSaveDirPath(), entity.getSaveFileName());
			if (file.exists()) {
				long totalSize = entity.getTotalSize();
				return totalSize > 0 && file.length() < totalSize;
			}
		}
		return false;
	}

	public boolean isFinishTask(String id) {
		EasyTaskEntity entity = EasyDaoManager.instance().queryWithId(id);
		if (entity != null) {
			File file = new File(entity.getSaveDirPath(), entity.getSaveFileName());
			if (file.exists()) {
				return file.length() == entity.getTotalSize();
			}
		}

		return false;
	}

	private void recoveryTaskState() {
		List<EasyTaskEntity> entities = EasyDaoManager.instance().queryAll();

		for (EasyTaskEntity entity : entities) {
			long completedSize = entity.getCompletedSize();
			long totalSize = entity.getTotalSize();
			if (completedSize > 0 &&
					completedSize < totalSize &&
					entity.getTaskStatus() != EasyTaskStatus.TASK_STATUS_PAUSE) {
				entity.setTaskStatus(EasyTaskStatus.TASK_STATUS_PAUSE);
				EasyDaoManager.instance().update(entity);
			}
		}
	}
 }

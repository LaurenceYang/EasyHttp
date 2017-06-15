package com.yang.easyhttp.download;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.yang.easyhttp.BuildConfig;
import com.yang.easyhttp.cache.EasyCacheType;
import com.yang.easyhttp.manager.EasyHttpClientManager;
import com.yang.easyhttp.utils.EasyConstants;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * Created by yangy on 2017/2/16.
 */
public class EasySimpleDownloadManager {
    private static EasySimpleDownloadManager mInstance;

    // ok http client
    private OkHttpClient mClient;
    // the thread count
    private int mThreadCount = 1;
    // ThreadPoolExecutor
    private ThreadPoolExecutor mExecutor;
    // task list
    private Map<String, EasySimpleDownloadTask> mCurrentTaskList;
    // queue
    private LinkedBlockingQueue<Runnable> mQueue;

    private EasySimpleDownloadManager() {

    }

    public static synchronized EasySimpleDownloadManager getInstance() {
        if (mInstance == null) {
            mInstance = new EasySimpleDownloadManager();
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

    public void addTask(EasySimpleDownloadTask task) {

        synchronized (this) {
            EasyTaskEntity taskEntity = task.getTaskEntity();

            if (taskEntity != null
                    && taskEntity.getTaskStatus() != EasyTaskStatus.TASK_STATUS_DOWNLOADING) {
                task.init();
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
    }

    public void pauseTask(EasySimpleDownloadTask task) {
        if (mQueue.contains(task)) {
            mQueue.remove(task);
        }

        task.pause();
    }

    public void resumeTask(EasySimpleDownloadTask task) {
        addTask(task);
    }

    public void cancelTask(EasySimpleDownloadTask task) {
        if (task == null) {
            return;
        }

        EasyTaskEntity taskEntity = task.getTaskEntity();
        if (taskEntity != null) {
            if (taskEntity.getTaskStatus() == EasyTaskStatus.TASK_STATUS_DOWNLOADING) {
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

    public void deleteTaskInMemory(EasySimpleDownloadTask task) {
        synchronized (this) {
            if (task == null) {
                return;
            }

            final EasyTaskEntity taskEntity = task.getTaskEntity();
            if (taskEntity != null) {
                if (taskEntity.getTaskStatus() == EasyTaskStatus.TASK_STATUS_DOWNLOADING) {
                    pauseTask(task);
                    mExecutor.remove(task);
                }

                if (mQueue.contains(task)) {
                    mQueue.remove(task);
                }

                mCurrentTaskList.remove(taskEntity.getTaskId());
                task.cancel();
            }
        }
    }

    public EasySimpleDownloadTask getTask(String taskId) {
        synchronized (this) {
            EasySimpleDownloadTask currTask = mCurrentTaskList.get(taskId);
            return currTask;
        }
    }

    public void cancelAllTask() {
        Iterator iterator = mCurrentTaskList.keySet().iterator();

        while (iterator.hasNext()) {
            String key = (String) iterator.next();
            EasySimpleDownloadTask task = mCurrentTaskList.get(key);
            EasyTaskEntity taskEntity = task.getTaskEntity();
            if (taskEntity != null) {
                if (taskEntity.getTaskStatus() == EasyTaskStatus.TASK_STATUS_DOWNLOADING) {
                    pauseTask(task);
                    mExecutor.remove(task);
                }

                if (mQueue.contains(task)) {
                    mQueue.remove(task);
                }

                task.cancel();
            }
            iterator.remove();
            mCurrentTaskList.remove(taskEntity.getTaskId());
        }
    }
}

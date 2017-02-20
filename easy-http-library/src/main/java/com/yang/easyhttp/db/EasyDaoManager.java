package com.yang.easyhttp.db;

import com.yang.easyhttp.download.EasyDownloadManager;
import com.yang.easyhttp.download.EasyTaskEntity;
import com.yang.easyhttp.download.EasyTaskEntityDao;

import java.util.List;

/**
 * Created by yangy on 2017/2/16.
 */
public class EasyDaoManager {
	private static EasyDaoManager mInstance;

	private EasyDaoManager() {
	}

	public static EasyDaoManager instance() {
		synchronized (EasyDaoManager.class) {
			if (mInstance == null) {
				mInstance = new EasyDaoManager();
			}
		}
		return mInstance;
	}

	public void insertOrReplace(EasyTaskEntity entity) {
		EasyDownloadManager.getInstance().getDaoSession().insertOrReplace(entity);
	}

	public EasyTaskEntity queryWithId(String taskId) {
		return EasyDownloadManager
				.getInstance()
				.getDaoSession()
				.getEasyTaskEntityDao()
				.queryBuilder()
				.where(EasyTaskEntityDao.Properties.TaskId.eq(taskId))
				.unique();
	}

	public List<EasyTaskEntity> queryAll() {
		return EasyDownloadManager
				.getInstance()
				.getDaoSession()
				.getEasyTaskEntityDao()
				.loadAll();
	}

	public void update(EasyTaskEntity entity) {
		EasyDownloadManager
			.getInstance()
			.getDaoSession()
			.getEasyTaskEntityDao()
			.update(entity);
	}

	public void delete(EasyTaskEntity entity) {
		EasyDownloadManager
			.getInstance()
			.getDaoSession()
			.getEasyTaskEntityDao()
			.delete(entity);
	}
}

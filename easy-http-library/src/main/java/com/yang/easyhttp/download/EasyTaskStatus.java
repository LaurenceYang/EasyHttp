package com.yang.easyhttp.download;

/**
 * Created by yangy on 2017/2/16.
 */
public class EasyTaskStatus {
	public static final int TASK_STATUS_INIT = 0;
	public static final int TASK_STATUS_QUEUE = TASK_STATUS_INIT + 1;
	public static final int TASK_STATUS_CONNECTING = TASK_STATUS_QUEUE + 1;
	public static final int TASK_STATUS_DOWNLOADING = TASK_STATUS_CONNECTING + 1;
	public static final int TASK_STATUS_PAUSE = TASK_STATUS_DOWNLOADING + 1;
	public static final int TASK_STATUS_CANCEL = TASK_STATUS_PAUSE + 1;
	public static final int TASK_STATUS_REQUEST_ERROR = TASK_STATUS_CANCEL + 1;
	public static final int TASK_STATUS_LINK_FAILURE_ERROR = TASK_STATUS_REQUEST_ERROR + 1;
	public static final int TASK_STATUS_STORAGE_ERROR = TASK_STATUS_LINK_FAILURE_ERROR + 1;
	public static final int TASK_STATUS_FINISH = TASK_STATUS_STORAGE_ERROR + 1;
	public static final int TASK_STATUS_RESET_STATUS = TASK_STATUS_FINISH + 1;
}

package com.yang.easyhttp.download;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Property;

/**
 * Created by yangy on 2017/2/16.
 */
@Entity(nameInDb = "download_status")
public class EasyTaskEntity {
	@Id(autoincrement = true)
	private Long id;
	@Property
	@NotNull
	private String taskId;
	@Property
	private long totalSize;
	@Property
	private long completedSize;
	@Property
	@NotNull
	private String downloadUrl;
	@Property
	private String saveDirPath;
	@Property
	private String saveFileName;
	@Property
	private int taskStatus;

	public EasyTaskEntity(Builder builder) {
		this.taskId = builder.taskId;
		this.totalSize = builder.totalSize;
		this.completedSize = builder.completedSize;
		this.downloadUrl = builder.downloadUrl;
		this.saveDirPath = builder.saveDirPath;
		this.saveFileName = builder.saveFileName;
		this.taskStatus = builder.taskStatus;
	}

	@Generated(hash = 1668992877)
	public EasyTaskEntity(Long id, @NotNull String taskId, long totalSize, long completedSize,
									@NotNull String downloadUrl, String saveDirPath, String saveFileName,
									int taskStatus) {
					this.id = id;
					this.taskId = taskId;
					this.totalSize = totalSize;
					this.completedSize = completedSize;
					this.downloadUrl = downloadUrl;
					this.saveDirPath = saveDirPath;
					this.saveFileName = saveFileName;
					this.taskStatus = taskStatus;
	}

	@Generated(hash = 1657904667)
	public EasyTaskEntity() {
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public long getTotalSize() {
		return totalSize;
	}

	public void setTotalSize(long totalSize) {
		this.totalSize = totalSize;
	}

	public long getCompletedSize() {
		return completedSize;
	}

	public void setCompletedSize(long completedSize) {
		this.completedSize = completedSize;
	}

	public String getDownloadUrl() {
		return downloadUrl;
	}

	public void setDownloadUrl(String downloadUrl) {
		this.downloadUrl = downloadUrl;
	}

	public String getSaveDirPath() {
		return saveDirPath;
	}

	public void setSaveDirPath(String saveDirPath) {
		this.saveDirPath = saveDirPath;
	}

	public String getSaveFileName() {
		return saveFileName;
	}

	public void setSaveFileName(String saveFileName) {
		this.saveFileName = saveFileName;
	}

	public int getTaskStatus() {
		return taskStatus;
	}

	public void setTaskStatus(int taskStatus) {
		this.taskStatus = taskStatus;
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	public static class Builder {
		// file id
		@NotNull
		private String taskId;
		// file length
		private long totalSize;
		// file complete length
		private long completedSize;
		// file url
		@NotNull
		private String downloadUrl;
		// file save path
		private String saveDirPath;
		// file name
		private String saveFileName;
		// file download status
		private int taskStatus;

		public Builder taskId(String taskId) {
			this.taskId = taskId;
			return this;
		}

		public Builder totalSize(long totalSize) {
			this.totalSize = totalSize;
			return this;
		}

		public Builder completedSize(long completedSize) {
			this.completedSize = completedSize;
			return this;
		}

		public Builder downloadUrl(String downloadUrl) {
			this.downloadUrl = downloadUrl;
			return this;
		}

		public Builder saveDirPath(String saveDirPath) {
			this.saveDirPath = saveDirPath;
			return this;
		}

		public Builder saveFileName(String saveFileName) {
			this.saveFileName = saveFileName;
			return this;
		}

		public Builder taskStatus(int taskStatus) {
			this.taskStatus = taskStatus;
			return this;
		}

		public EasyTaskEntity build() {
			return new EasyTaskEntity(this);
		}
	}

	@Override
	public String toString() {
		return "EasyTaskEntity{" +
				"taskId='" + taskId + '\'' +
				", totalSize=" + totalSize +
				", completedSize=" + completedSize +
				", downloadUrl='" + downloadUrl + '\'' +
				", saveDirPath='" + saveDirPath + '\'' +
				", saveFileName='" + saveFileName + '\'' +
				", taskStatus=" + taskStatus +
				'}';
	}

}

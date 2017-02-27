package com.yang.easyhttp.config;

/**
 * Created by yangy on 2017/2/22.
 */
public class EasyHttpConfig {
	String cacheDir;

	int cacheShortTime;
	int cacheMidTime;
	int cacheLongTime;

	long cacheShortMaxSize;
	long cacheMidMaxSize;
	long cacheLongMaxSize;

	int globalCacheType;

	String userAgent;

	EasyHttpConfig(Builder builder) {
		this.cacheDir = builder.cacheDir;
		this.cacheShortTime = builder.cacheShortTime;
		this.cacheMidTime = builder.cacheMidTime;
		this.cacheLongTime = builder.cacheLongTime;
		this.cacheShortMaxSize = builder.cacheShortMaxSize;
		this.cacheMidMaxSize = builder.cacheMidMaxSize;
		this.cacheLongMaxSize = builder.cacheLongMaxSize;
		this.globalCacheType = builder.globalCacheType;
		this.userAgent = builder.userAgent;
	}

	public void setCacheDir(String cacheDir) {
		this.cacheDir = cacheDir;
	}

	public void setCacheShortTime(int cacheShortTime) {
		this.cacheShortTime = cacheShortTime;
	}

	public void setCacheMidTime(int cacheMidTime) {
		this.cacheMidTime = cacheMidTime;
	}

	public void setCacheLongTime(int cacheLongTime) {
		this.cacheLongTime = cacheLongTime;
	}

	public void setGlobalCacheType(int globalCacheType) {
		this.globalCacheType = globalCacheType;
	}

	public String getCacheDir() {
		return cacheDir;
	}

	public int getCacheShortTime() {
		return cacheShortTime;
	}

	public int getCacheMidTime() {
		return cacheMidTime;
	}

	public int getCacheLongTime() {
		return cacheLongTime;
	}

	public int getGlobalCacheType() {
		return globalCacheType;
	}

	public long getCacheShortMaxSize() {
		return cacheShortMaxSize;
	}

	public void setCacheShortMaxSize(long cacheShortMaxSize) {
		this.cacheShortMaxSize = cacheShortMaxSize;
	}

	public long getCacheMidMaxSize() {
		return cacheMidMaxSize;
	}

	public void setCacheMidMaxSize(long cacheMidMaxSize) {
		this.cacheMidMaxSize = cacheMidMaxSize;
	}

	public long getCacheLongMaxSize() {
		return cacheLongMaxSize;
	}

	public void setCacheLongMaxSize(long cacheLongMaxSize) {
		this.cacheLongMaxSize = cacheLongMaxSize;
	}

	public String getUserAgent() {
		return userAgent;
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

	public static class Builder {
		String cacheDir;

		int cacheShortTime = 0;
		int cacheMidTime = 0;
		int cacheLongTime = 0;

		long cacheShortMaxSize = 0;
		long cacheMidMaxSize = 0;
		long cacheLongMaxSize = 0;

		int globalCacheType = -1;

		String userAgent;

		public Builder setCacheDir(String cacheDir) {
			this.cacheDir = cacheDir;
			return this;
		}

		public Builder setCacheTime(int shortTime, int midTime, int longTime) {
			this.cacheShortTime = shortTime;
			this.cacheMidTime = midTime;
			this.cacheLongTime = longTime;

			return this;
		}

		public Builder setGlobalCacheType(int type) {
			this.globalCacheType = type;
			return this;
		}

		public Builder setCacheMaxSize(long cacheShortMaxSize, long cacheMidMaxSize, long cacheLongMaxSize) {
			this.cacheShortMaxSize = cacheShortMaxSize;
			this.cacheMidMaxSize = cacheMidMaxSize;
			this.cacheLongMaxSize = cacheLongMaxSize;
			return this;
		}

		public Builder setUserAgent(String userAgent) {
			this.userAgent = userAgent;
			return this;
		}

		public EasyHttpConfig build() {
			return new EasyHttpConfig(this);
		}
	}
}

package com.h3c.iclouds.utils;


public class PictureSingleton {
	private static PictureSingleton pictureSingleton = new PictureSingleton();
	private String server2Ove;
	private String cvm2Ove;
	private String storage2Ove;

	public String getServer2Ove() {
		return server2Ove;
	}

	public void setServer2Ove(String server2Ove) {
		this.server2Ove = server2Ove;
	}

	public String getCvm2Ove() {
		return cvm2Ove;
	}

	public void setCvm2Ove(String cvm2Ove) {
		this.cvm2Ove = cvm2Ove;
	}

	public String getStorage2Ove() {
		return storage2Ove;
	}

	public void setStorage2Ove(String storage2Ove) {
		this.storage2Ove = storage2Ove;
	}

	private PictureSingleton() {
		super();
	}

	public static PictureSingleton getInstance() {
		if (pictureSingleton == null) {
			pictureSingleton = new PictureSingleton();
		}
		return pictureSingleton;
	}

}

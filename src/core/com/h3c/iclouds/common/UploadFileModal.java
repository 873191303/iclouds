package com.h3c.iclouds.common;

import java.io.Serializable;
import java.util.Vector;

/**
 * 文件上传对应实体
 * @author zkf5485
 *
 */
public class UploadFileModal implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public UploadFileModal(String token) {
		this.token = token;
	}

	private Vector<String> fileNames = new Vector<String>();
	
	private String fileKey;
	
	private String token;
	
	public static UploadFileModal create(String token) {
		return new UploadFileModal(token);
	}

	public Vector<String> getFileNames() {
		return fileNames;
	}

	public void setFileNames(Vector<String> fileNames) {
		this.fileNames = fileNames;
	}
	
	public String getFileKey() {
		return fileKey;
	}

	public UploadFileModal addFileName(String fileName) {
		this.fileNames.add(fileName);
		return this;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
	
}

package com.h3c.iclouds.common;

import com.h3c.iclouds.operate.CloudosClient;
import org.activiti.engine.impl.pvm.process.ActivityImpl;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SimpleCache {

	/**
	 * 流程路线
	 */
	public final static Map<String, List<ActivityImpl>> FLOW_MAP = new ConcurrentHashMap<>();
	
	/**
	 * cloudos 连接
	 */
	public final static Map<String, CloudosClient> CLOUDOS_CLIENT_MAP = new ConcurrentHashMap<>();
	
	/**
	 * 上传文件对应文件名称
	 */
	public final static Map<String, UploadFileModal> UPLOAD_FILE_MAP = new ConcurrentHashMap<>();
	
	/**
	 * token与用户id对应
	 */
	public final static Map<String, String> TOKEN_TO_USER_MAP = new ConcurrentHashMap<>();
	
}

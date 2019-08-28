package com.h3c.iclouds.opt;

import com.alibaba.fastjson.JSONObject;
import com.h3c.iclouds.auth.CacheSingleton;
import com.h3c.iclouds.client.EisooClient;
import com.h3c.iclouds.client.EisooParams;
import com.h3c.iclouds.po.bean.EiDirBean;
import com.h3c.iclouds.utils.InvokeSetForm;

import java.util.HashMap;
import java.util.Map;

/**
 * 目录eisoo操作
 * Created by yKF7317 on 2017/5/2.
 *
 */
public class EiDirOpt {
	
	CacheSingleton singleton = CacheSingleton.getInstance();
	
	private EisooClient client;
	
	public EiDirOpt (EisooClient client) {
		this.client = client;
	}
	
	/**
	 * 创建目录
	 * @param eiDirBean
	 * @return
	 */
	public JSONObject create(EiDirBean eiDirBean) {
		eiDirBean.setPath(null);
		Map<String, Object> param = InvokeSetForm.tranClassToMap(eiDirBean);
		return query(param, EisooParams.EISOO_CREATE_DIR_METHOD);
	}
	
	/**
	 * 创建多级目录
	 * @param eiDirBean
	 * @return
	 */
	public JSONObject createMulti(EiDirBean eiDirBean) {
		eiDirBean.setOndup(null);
		eiDirBean.setName(null);
		Map<String, Object> param = InvokeSetForm.tranClassToMap(eiDirBean);
		return query(param, EisooParams.EISOO_CREATE_MULTI_DIR_METHOD);
	}
	
	/**
	 * 重命名目录
	 * @param eiDirBean
	 * @return
	 */
	public JSONObject rename(EiDirBean eiDirBean) {
		eiDirBean.setPath(null);
		Map<String, Object> param = InvokeSetForm.tranClassToMap(eiDirBean);
		return query(param, EisooParams.EISOO_RENAME_DIR_METHOD);
	}
	
	/**
	 * 移动目录
	 * @param eiDirBean
	 * @param destParent
	 * @return
	 */
	public JSONObject move(EiDirBean eiDirBean, String destParent) {
		eiDirBean.setPath(null);
		eiDirBean.setName(null);
		Map<String, Object> param = InvokeSetForm.tranClassToMap(eiDirBean);
		param.put("destparent", destParent);
		return query(param, EisooParams.EISOO_MOVE_DIR_METHOD);
	}
	
	/**
	 * 浏览目录
	 * @param docId
	 * @param queryMap
	 * @return
	 */
	public JSONObject list(String docId, Map<String, Object> queryMap) {
		queryMap.put("docid", docId);
		return query(queryMap, EisooParams.EISOO_LIST_DIR_METHOD);
	}
	
	/**
	 * 复制目录
	 * @param eiDirBean
	 * @param destParent
	 * @return
	 */
	public JSONObject copy(EiDirBean eiDirBean, String destParent) {
		eiDirBean.setPath(null);
		eiDirBean.setName(null);
		Map<String, Object> param = InvokeSetForm.tranClassToMap(eiDirBean);
		param.put("destparent", destParent);
		return query(param, EisooParams.EISOO_COPY_DIR_METHOD);
	}
	
	/**
	 * 查看复制进度
	 * @param id
	 * @return
	 */
	public JSONObject getCopyProgress(String id) {
		Map<String, Object> param = new HashMap<>();
		param.put("id", id);
		return query(param, EisooParams.EISOO_GET_DIR_COPY_PROGRESS_METHOD);
	}
	
	/**
	 * 获取目录属性
	 * @param docId
	 * @return
	 */
	public JSONObject getAttribute(String docId) {
		Map<String, Object> param = new HashMap<>();
		param.put("docid", docId);
		return query(param, EisooParams.EISOO_GET_DIR_ATTRIBUTE_METHOD);
	}
	
	/**
	 * 获取目录大小
	 * @param docId
	 * @param onlyRecycle
	 * @return
	 */
	public JSONObject getSize(String docId, boolean onlyRecycle) {
		Map<String, Object> param = new HashMap<>();
		param.put("docid", docId);
		param.put("onlyrecycle", onlyRecycle);
		return query(param, EisooParams.EISOO_GET_DIR_SIZE_METHOD);
	}
	
	/**
	 * 获取建议名称
	 * @param eiDirBean
	 * @return
	 */
	public JSONObject getSuggestName(EiDirBean eiDirBean) {
		eiDirBean.setPath(null);
		eiDirBean.setOndup(null);
		Map<String, Object> param = InvokeSetForm.tranClassToMap(eiDirBean);
		return query(param, EisooParams.EISOO_GET_DIR_SUGGEST_NAME_METHOD);
	}
	
	/**
	 * 设置文件密级
	 * @param docId
	 * @param level--[5,9]
	 * @return
	 */
	public JSONObject setCsfLevel(String docId, int level) {
		Map<String, Object> param = new HashMap<>();
		param.put("docid", docId);
		param.put("csflevel", level);
		return query(param, EisooParams.EISOO_SET_DIR_CSF_LEVEL_METHOD);
	}
	
	/**
	 * 删除文件
	 * @param docId
	 * @return
	 */
	public JSONObject delete(String docId) {
		Map<String, Object> param = new HashMap<>();
		param.put("docid", docId);
		return query(param, EisooParams.EISOO_DELETE_DIR_METHOD);
	}
	
	public JSONObject query(Map<String, Object> param, String method) {
		String url = client.tranUrl(singleton.getEisooApi(EisooParams.EISOO_API_DIR), method);
		return client.post(url, param);
	}
	
}

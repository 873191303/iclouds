package com.h3c.iclouds.opt;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.h3c.iclouds.auth.CacheSingleton;
import com.h3c.iclouds.po.bean.EiFileBean;
import com.h3c.iclouds.client.EisooClient;
import com.h3c.iclouds.client.EisooParams;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.exception.MessageException;
import com.h3c.iclouds.operate.CloudosParams;
import com.h3c.iclouds.utils.ResourceHandle;
import com.h3c.iclouds.utils.HttpUtils;
import com.h3c.iclouds.utils.InvokeSetForm;
import com.h3c.iclouds.utils.StrUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * 文件eisoo操作
 * Created by yKF7317 on 2017/5/2.
 *
 */
public class EiFileOpt {
	
	CacheSingleton singleton = CacheSingleton.getInstance();
	
	private EisooClient client;
	
	public EiFileOpt (EisooClient client) {
		this.client = client;
	}
	
	/**
	 * 上传文件
	 * @param eiFileBean
	 * @param filePath
	 * @return
	 */
	public ResultType upload(EiFileBean eiFileBean, String filePath){
		JSONObject jsonObject = beginUpload(eiFileBean, filePath);
		if (!ResourceHandle.judgeResponse(jsonObject)){
			throw new MessageException(HttpUtils.getError(jsonObject));
		}
		JSONObject record = jsonObject.getJSONObject("record");
		String rev = record.getString("rev");
		String docId = record.getString("docid");
		Map<String, Object> map = StrUtils.createMap(CloudosParams.INPUTSTREAM, filePath);
		JSONArray authRequest = record.getJSONArray("authrequest");
		String method = authRequest.getString(0);
		String url = authRequest.getString(1);
		Map<String, Object> headerMap = new HashMap<>();
		for (int i = 2; i < authRequest.size(); i++) {
			String headerJson = authRequest.getString(i);
			String headerKey = headerJson.substring(0, headerJson.indexOf(":"));
			String headerValue = headerJson.substring(headerJson.indexOf(":") + 2, headerJson.length());
			headerMap.put(headerKey, headerValue);
		}
		jsonObject = client.queryHttp(url, map, method.toLowerCase(), headerMap);
		if (!ResourceHandle.judgeResponse(jsonObject)) {
			throw new MessageException(HttpUtils.getError(jsonObject));
		}
		jsonObject = endUpload(docId, rev);
		if (!ResourceHandle.judgeResponse(jsonObject)) {
			throw new MessageException(HttpUtils.getError(jsonObject));
		}
		return ResultType.success;
	}
	
	/**
	 * 开始上传
	 * @param eiFileBean
	 * @param filePath
	 * @return
	 */
	public JSONObject beginUpload(EiFileBean eiFileBean, String filePath) {
		Map<String, Object> param = InvokeSetForm.tranClassToMap(eiFileBean);
		File file = new File(filePath);
		param.put("length", file.length());
		param.put("usehttps", singleton.getEisooApi(EisooParams.EISOO_USEHTTPS));
		param.put("reqhost", singleton.getEisooApi(EisooParams.EISOO_IP));
		return query(param, EisooParams.EISOO_BEGIN_UPLOAD_METHOD);
	}
	
	/**
	 * 结束上传
	 * @param docId
	 * @param rev
	 * @return
	 */
	public JSONObject endUpload(String docId, String rev) {
		Map<String, Object> param = new HashMap<>();
		param.put("docid", docId);
		param.put("rev", rev);
		return query(param, EisooParams.EISOO_END_UPLOAD_METHOD);
	}
	
	/**
	 * 删除文件
	 * @param docId
	 * @return
	 */
	public JSONObject delete(String docId) {
		Map<String, Object> param = new HashMap<>();
		param.put("docid", docId);
		return query(param, EisooParams.EISOO_DELETE_FILE_METHOD);
	}
	
	/**
	 * 重命名文件
	 * @param eiFileBean
	 * @return
	 */
	public JSONObject rename(EiFileBean eiFileBean) {
		Map<String, Object> param = InvokeSetForm.tranClassToMap(eiFileBean);
		return query(param, EisooParams.EISOO_RENAME_FILE_METHOD);
	}
	
	/**
	 * 移动文件
	 * @param eiFileBean
	 * @param destParent
	 * @return
	 */
	public JSONObject move(EiFileBean eiFileBean, String destParent) {
		eiFileBean.setName(null);
		Map<String, Object> param = InvokeSetForm.tranClassToMap(eiFileBean);
		param.put("destparent", destParent);
		return query(param, EisooParams.EISOO_MOVE_FILE_METHOD);
	}
	
	/**
	 * 复制文件
	 * @param eiFileBean
	 * @param destParent
	 * @return
	 */
	public JSONObject copy(EiFileBean eiFileBean, String destParent) {
		eiFileBean.setName(null);
		Map<String, Object> param = InvokeSetForm.tranClassToMap(eiFileBean);
		param.put("destparent", destParent);
		return query(param, EisooParams.EISOO_COPY_FILE_METHOD);
	}
	
	/**
	 * 获取上传文件的建议名称
	 * @param eiFileBean
	 * @return
	 */
	public JSONObject getSuggestName(EiFileBean eiFileBean) {
		eiFileBean.setOndup(null);
		Map<String, Object> param = InvokeSetForm.tranClassToMap(eiFileBean);
		return query(param, EisooParams.EISOO_GET_FILE_SUGGEST_NAME_METHOD);
	}
	
	/**
	 * 获取文件属性
	 * @param docId
	 * @return
	 */
	public JSONObject getAttribute(String docId) {
		Map<String, Object> param = new HashMap<>();
		param.put("docid", docId);
		return query(param, EisooParams.EISOO_GET_FILE_ATTRIBUTE_METHOD);
	}
	
	/**
	 * 设置文件密级
	 * @param docId
	 * @param level
	 * @return
	 */
	public JSONObject setCsfLevel(String docId, int level) {
		Map<String, Object> param = new HashMap<>();
		param.put("docid", docId);
		param.put("csflevel", level);
		return query(param, EisooParams.EISOO_SET_FILE_CSF_LEVEL_METHOD);
	}
	
	/**
	 * 查看历史版本
	 * @param docId
	 * @return
	 */
	public JSONObject revisions(String docId) {
		Map<String, Object> param = new HashMap<>();
		param.put("docid", docId);
		return query(param, EisooParams.EISOO_GET_FILE_HISTORY_METHOD);
	}
	
	/**
	 * 还原历史版本
	 * @param docId
	 * @param rev
	 * @return
	 */
	public JSONObject restore(String docId, String rev) {
		Map<String, Object> param = new HashMap<>();
		param.put("docid", docId);
		param.put("rev", rev);
		return query(param, EisooParams.EISOO_RESTORE_FILE_HISTORY_METHOD);
	}
	
	/**
	 * 获取文件元数据
	 * @param docId
	 * @param rev
	 * @return
	 */
	public JSONObject metadata(String docId, String rev) {
		Map<String, Object> param = new HashMap<>();
		param.put("docid", docId);
		if (StrUtils.checkParam(rev)) {
			param.put("rev", rev);
		}
		return query(param, EisooParams.EISOO_GET_FILE_METADATA_METHOD);
	}
	
	/**
	 * 新增文件标签
	 * @param docId
	 * @param tag
	 * @return
	 */
	public JSONObject addTag(String docId, String tag) {
		Map<String, Object> param = new HashMap<>();
		param.put("docid", docId);
		param.put("tag", tag);
		return query(param, EisooParams.EISOO_ADD_FILE_TAG_METHOD);
	}
	
	/**
	 * 删除文件标签
	 * @param docId
	 * @param tag
	 * @return
	 */
	public JSONObject deleteTag(String docId, String tag) {
		Map<String, Object> param = new HashMap<>();
		param.put("docid", docId);
		param.put("tag", tag);
		return query(param, EisooParams.EISOO_DELETE_FILE_TAG_METHOD);
	}
	
	public JSONObject query (Map<String, Object> param, String method) {
		String url = client.tranUrl(singleton.getEisooApi(EisooParams.EISOO_API_FILE), method);
		return client.post(url, param);
	}
}

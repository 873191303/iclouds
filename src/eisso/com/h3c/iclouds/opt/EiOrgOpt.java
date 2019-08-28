package com.h3c.iclouds.opt;

import com.alibaba.fastjson.JSONObject;
import com.h3c.iclouds.po.bean.EiOrgBean;
import com.h3c.iclouds.client.EisooClient;
import com.h3c.iclouds.client.EisooParams;
import com.h3c.iclouds.common.EisooAPIConst;
import com.h3c.iclouds.utils.InvokeSetForm;

import java.util.HashMap;
import java.util.Map;

/**
 * 组织eisoo操作
 * Created by yKF7317 on 2017/5/2.
 *
 */
public class EiOrgOpt {
	
	private EisooClient client;
	
	public EiOrgOpt (EisooClient client) {
		this.client = client;
	}
	
	/**
	 * 创建组织
	 * @throws Exception
	 */
	public JSONObject createOrg(EiOrgBean orgBean) throws Exception {
		Map<String, Object> param = InvokeSetForm.tranClassToMap(orgBean);
		return EisooAPIConst.getOptionThirdResult(param, EisooParams.EISOO_CREATE_ORG_METHOD, client);
	}
	
	/**
	 * 删除组织
	 * @throws Exception
	 */
	public JSONObject deleteOrg(String orgId) throws Exception {
		Map<String, Object> param = new HashMap<>();
		param.put("orgId", orgId);
		return EisooAPIConst.getOptionThirdResult(param, EisooParams.EISOO_DELETE_ORG_METHOD, client);
	}
	
	/**
	 * 修改组织
	 * @throws Exception
	 */
	public JSONObject updateOrg(EiOrgBean orgBean) throws Exception {
		Map<String, Object> param = InvokeSetForm.tranClassToMap(orgBean);
		return EisooAPIConst.getOptionThirdResult(param, EisooParams.EISOO_EDIT_ORG_METHOD, client);
	}
	
	/**
	 * 根据组织名获取组织信息
	 * @throws Exception
	 */
	public JSONObject getOrgByName(String orgName) throws Exception {
		Map<String, Object> param = new HashMap<>();
		param.put("orgName", orgName);
		return EisooAPIConst.getOptionThirdResult(param, EisooParams.EISOO_GET_ORG_BYNAME_METHOD, client);
	}
	
	/**
	 * 根据组织id获取组织信息
	 * @throws Exception
	 */
	public JSONObject getOrgById(String orgId) throws Exception {
		Map<String, Object> param = new HashMap<>();
		param.put("orgId", orgId);
		return EisooAPIConst.getOptionThirdResult(param, EisooParams.EISOO_GET_ORG_BYID_METHOD, client);
	}
	
	/**
	 * 获取所有组织信息
	 * @throws Exception
	 */
	public JSONObject getOrgs(EisooClient client) throws Exception {
		return EisooAPIConst.getOptionThirdResult(null, EisooParams.EISOO_GET_ORG_METHOD, client);
	}
	
	/**
	 * 获取组织下用户信息
	 * @throws Exception
	 */
	public JSONObject getUsersByOrgId(String orgId, int start, int limit) throws Exception {
		Map<String, Object> param = new HashMap<>();
		param.put("orgId", orgId);
		param.put("start", start);
		param.put("limit", limit);
		return EisooAPIConst.getOptionThirdResult(param, EisooParams.EISOO_GET_USER_UNDER_ORG_METHOD, client);
	}
}

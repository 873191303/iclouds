package com.h3c.iclouds.opt;

import com.alibaba.fastjson.JSONObject;
import com.h3c.iclouds.po.bean.EiUserBean;
import com.h3c.iclouds.client.EisooClient;
import com.h3c.iclouds.client.EisooParams;
import com.h3c.iclouds.common.EisooAPIConst;
import com.h3c.iclouds.utils.ResourceHandle;
import com.h3c.iclouds.utils.InvokeSetForm;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户eisoo操作
 * Created by yKF7317 on 2017/5/2.
 *
 */
public class EiUserOpt {
	
	private EisooClient client;
	
	public EiUserOpt (EisooClient client) {
		this.client = client;
	}
	
	/**
	 * 创建用户
	 * @param userBean
	 * @param client
	 * @return
	 * @throws Exception
	 */
	public JSONObject createUser(EiUserBean userBean) throws Exception {
		Map<String, Object> param = InvokeSetForm.tranClassToMap(userBean);
		return EisooAPIConst.getOptionThirdResult(param, EisooParams.EISOO_CREATE_USER_METHOD, client);
	}
	
	/**
	 * 修改用户
	 * @param userBean
	 * @param client
	 * @return
	 * @throws Exception
	 */
	public JSONObject updateUser(EiUserBean userBean) throws Exception {
		userBean.setLoginName(null);
		userBean.setType(null);
		userBean.setDepIds(null);
		userBean.setDepNames(null);
		Map<String, Object> param = InvokeSetForm.tranClassToMap(userBean);
		return EisooAPIConst.getOptionThirdResult(param, EisooParams.EISOO_EDIT_USER_METHOD, client);
	}
	
	/**
	 * 删除用户(先关闭个人文档)
	 * @param userId
	 * @param client
	 * @return
	 * @throws Exception
	 */
	public JSONObject deleteUser(String userId) throws Exception {
		Map<String, Object> param = new HashMap<>();
		param.put("userId", userId);
		JSONObject jsonObject = EisooAPIConst.getOptionThirdResult(param, EisooParams.EISOO_CLOSE_USER_METHOD, client);
		if (ResourceHandle.judgeResponse(jsonObject)) {
			jsonObject = EisooAPIConst.getOptionThirdResult(param, EisooParams.EISOO_DELETE_USER_METHOD, client);
		}
		return jsonObject;
	}
	
	/**
	 * 通过登录名获取用户信息
	 * @param loginName
	 * @return
	 * @throws Exception
	 */
	public JSONObject getUserByName(String loginName) throws Exception {
		Map<String, Object> param = new HashMap<>();
		param.put("loginName", loginName);
		return EisooAPIConst.getOptionThirdResult(param, EisooParams.EISOO_GET_USER_BYNAME_METHOD, client);
	}
	
	/**
	 * 根据用户id获取用户信息
	 * @param userId
	 * @return
	 */
	public JSONObject getUserById(String userId) throws Exception {
		Map<String, Object> param = new HashMap<>();
		param.put("userId", userId);
		return EisooAPIConst.getOptionThirdResult(param, EisooParams.EISOO_GET_USER_BYID_METHOD, client);
	}
	
	public JSONObject getPageUsers(int start, int limit) throws Exception {
		Map<String, Object> param = new HashMap<>();
		param.put("start", start);
		param.put("limit", limit);
		return EisooAPIConst.getOptionThirdResult(param, EisooParams.EISOO_GET_PAGE_USER_METHOD, client);
	}
	
	public JSONObject getUserCount() throws Exception {
		return EisooAPIConst.getOptionThirdResult(null, EisooParams.EISOO_GET_USER_COUNT_METHOD, client);
	}
	
}

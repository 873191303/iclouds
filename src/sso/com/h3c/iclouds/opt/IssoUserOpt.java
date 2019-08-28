package com.h3c.iclouds.opt;

import com.alibaba.fastjson.JSONObject;
import com.h3c.iclouds.auth.CacheSingleton;
import com.h3c.iclouds.po.bean.IssoUserBean;
import com.h3c.iclouds.utils.InvokeSetForm;

import java.util.Map;

/**
 * Created by yKF7317 on 2017/5/25.
 */
public class IssoUserOpt {
	
	private IssoClient client;
	
	public IssoUserOpt(IssoClient client) {
		this.client = client;
	}
	
	private static CacheSingleton singleton = CacheSingleton.getInstance();
	
	/**
	 * 检查登录名在isso是否重复
	 * @param loginName
	 * @return
	 */
	public JSONObject checkRepeat(String loginName) {
		String url = client.tranUrl(singleton.getIssoApi(IssoParams.ISSO_API_USER_CHECK_REPEAT), loginName);
		return client.get(url);
	}
	
	/**
	 * 保存用户
	 * @param authUserBean
	 * @return
	 */
	public JSONObject save(IssoUserBean authUserBean) {
		Map<String, Object> param = InvokeSetForm.tranClassToMap(authUserBean);
		String url = client.tranUrl(singleton.getIssoApi(IssoParams.ISSO_API_USER));
		return client.post(url, param);
	}
	
	/**
	 * 根据登录名获取用户
	 * @param loginName
	 * @return
	 */
	public JSONObject get(String loginName) {
		String url = client.tranUrl(singleton.getIssoApi(IssoParams.ISSO_API_GET_USER_BY_LOGINNAME), loginName);
		return client.get(url);
	}
	
	/**
	 * 修改用户
	 * @param authUserBean
	 * @return
	 */
	public JSONObject update(IssoUserBean authUserBean, String id) {
		Map<String, Object> param = InvokeSetForm.tranClassToMap(authUserBean);
		String url = client.tranUrl(singleton.getIssoApi(IssoParams.ISSO_API_USER_ACTION), id);
		return client.put(url, param);
	}
	
	/**
	 * 删除用户
	 * @param id
	 * @return
	 */
	public JSONObject delete(String id) {
		String url = client.tranUrl(singleton.getIssoApi(IssoParams.ISSO_API_USER_ACTION), id);
		return client.delete(url, null);
	}
}

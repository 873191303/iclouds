package com.h3c.iclouds.biz;

import com.alibaba.fastjson.JSONArray;
import com.h3c.iclouds.base.BaseBiz;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.po.QuotaUsed;

public interface QuotaUsedBiz extends BaseBiz<QuotaUsed> {
	
	void upate(JSONArray quotas);
	
	/**
	 * 修改配额
	 * @param classCode
	 * @param projectId
	 * @param flag
	 * @param count
	 */
	void change(String classCode, String projectId,boolean flag,int count);
	
	/**
	 * 获取租户某个配额属性的配额值
	 * @param projectId
	 * @param classCode
	 * @return
	 */
	int getClassValue(String projectId, String classCode);
	
	/**
	 * 检查租户资源的配额
	 * @param type
	 * @param projectId
	 * @param count
	 * @return
	 */
	ResultType checkQuota(String type, String projectId, int count);
}

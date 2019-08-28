package com.h3c.iclouds.biz.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.h3c.iclouds.client.EisooAbcParams;
import com.h3c.iclouds.base.BaseBizImpl;
import com.h3c.iclouds.biz.QuotaUsedBiz;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.dao.Project2QuotaDao;
import com.h3c.iclouds.dao.QuotaClassDao;
import com.h3c.iclouds.dao.QuotaUsedDao;
import com.h3c.iclouds.po.Project2Quota;
import com.h3c.iclouds.po.QuotaClass;
import com.h3c.iclouds.po.QuotaUsed;
import com.h3c.iclouds.utils.InvokeSetForm;
import com.h3c.iclouds.utils.StrUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("quotaUsedBiz")
public class QuotaUsedBizImpl extends BaseBizImpl<QuotaUsed> implements QuotaUsedBiz {

	@Resource
	private QuotaUsedDao quotaUsedDao;

	@Resource
	private QuotaClassDao quotaClassDao;
	
	@Resource
	private Project2QuotaDao project2QuotaDao;
	
	@Override
	public void upate(JSONArray quotas) {
		if (null != quotas && quotas.size() > 0) {
			// 解析数据
			for (Object quota : quotas) {
				JSONObject temp = (JSONObject) quota;
				String tenantId = temp.getString("TenantId");
				String usedSize = temp.getString("QuotaUsedSize");

				QuotaUsed entity = new QuotaUsed();
				entity.setClassId(EisooAbcParams.ABC_QUOTA_FLAG);
				entity.setClassCode(EisooAbcParams.ABC_SUCCESS_CODE);
				entity.setTenantId(tenantId);
				entity.setQuotaUsed(Integer.valueOf(usedSize));
				entity.setDeleted(0);

				// 找到相应的 使用配额
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("classId", EisooAbcParams.ABC_QUOTA_FLAG);
				map.put("tenantId", tenantId);
				map.put("deleted", 0);
				List<QuotaUsed> quotasUsed = quotaUsedDao.listByClass(QuotaUsed.class, map);

				if (null != quotasUsed && quotasUsed.size() > 0) {
					QuotaUsed quotaUsed = quotasUsed.get(0);

					// 更新本地DB
					InvokeSetForm.copyFormProperties(entity, quotaUsed);
					quotaUsed.updatedUser(this.getLoginUser());
					quotaUsed.setUpdatedDate(new Date());
					quotaUsedDao.update(quotaUsed);
				} else {
					// TODO 现在是假数据（因为外键），正常环境下只更新，不插入
					entity.createdUser(this.getLoginUser());
					entity.setCreatedDate(new Date());
					entity.setUpdatedDate(new Date());
					quotaUsedDao.add(entity);
				}
			}
		}
	}

	@Override
	public void change(String classCode, String projectId, boolean flag, int count) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("tenantId", projectId);
		params.put("classCode", classCode);
		params.put("deleted", 0);
		// 修改租户配额信息，验证放在前面
		QuotaUsed quotaUsed = singleByClass(QuotaUsed.class, params);
		if (!StrUtils.checkParam(quotaUsed)) {
			QuotaClass quotaClass = quotaClassDao.findByPropertyName(QuotaClass.class, "classCode", classCode).get(0);
			quotaUsed = new QuotaUsed();
			quotaUsed.setClassCode(classCode);
			if (flag) {
				quotaUsed.setQuotaUsed(count);
			} else {
				quotaUsed.setQuotaUsed(0);
			}
			quotaUsed.setTenantId(projectId);
			quotaUsed.setClassId(quotaClass.getId());
			quotaUsed.setCreatedAt(new Date());
			quotaUsed.setUpdatedAt(new Date());
			quotaUsed.setDeleted(0);
			quotaUsedDao.add(quotaUsed);
		} else {
			if (flag) {
				quotaUsed.setQuotaUsed(quotaUsed.getQuotaUsed() + count);
			} else {
				quotaUsed.setQuotaUsed(quotaUsed.getQuotaUsed() - count);
			}
			quotaUsedDao.update(quotaUsed);
		}
	}
	
	public int getClassValue(String projectId, String classCode) {
		Map<String, Object> queryMap = new HashMap<>();
		queryMap.put("classCode", classCode);
		queryMap.put("tenantId", projectId);
		Project2Quota project2Quotas = project2QuotaDao.singleByClass(Project2Quota.class, queryMap);
		if (StrUtils.checkParam(project2Quotas)) {
			return project2Quotas.getHardLimit();
		}
		return 0;
	}
	
	public ResultType checkQuota(String classCode, String projectId, int count) {
		Map<String, Object> queryMap = new HashMap<>();
		queryMap.put("classCode", classCode);
		queryMap.put("tenantId", projectId);
		Project2Quota project2Quota = project2QuotaDao.singleByClass(Project2Quota.class, queryMap);
		if (!StrUtils.checkParam(project2Quota)) {// 租户没有分配配额
			return ResultType.tenant_not_contain_quota;
		}
		int limit = project2Quota.getHardLimit();
		if (limit == 0) {// 租户配额为0
			return ResultType.tenant_not_contain_quota;
		}
		QuotaUsed quotaUsed = quotaUsedDao.singleByClass(QuotaUsed.class, queryMap);
		if (StrUtils.checkParam(quotaUsed)) {// 租户配额已使用数量是否达到配额限制
			Integer usedCount = quotaUsed.getQuotaUsed();
			if (usedCount + count > limit) {
				return ResultType.quota_reach_max;
			}
		}
		return ResultType.success;
	}
	
}
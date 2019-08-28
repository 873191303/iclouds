package com.h3c.iclouds.biz.impl;

import com.alibaba.fastjson.JSONObject;
import com.h3c.iclouds.base.BaseBizImpl;
import com.h3c.iclouds.base.BaseDAO;
import com.h3c.iclouds.base.SqlQueryBiz;
import com.h3c.iclouds.biz.Project2QuotaBiz;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.common.SqlQueryProperty;
import com.h3c.iclouds.dao.Project2QuotaDao;
import com.h3c.iclouds.dao.ProjectDao;
import com.h3c.iclouds.dao.QuotaClassDao;
import com.h3c.iclouds.dao.QuotaUsedDao;
import com.h3c.iclouds.exception.MessageException;
import com.h3c.iclouds.po.NovaFlavor;
import com.h3c.iclouds.po.Project;
import com.h3c.iclouds.po.Project2Quota;
import com.h3c.iclouds.po.QuotaClass;
import com.h3c.iclouds.po.QuotaUsed;
import com.h3c.iclouds.po.bean.inside.SaveNovaVmBean;
import com.h3c.iclouds.utils.StrUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("project2QuotaBiz")
public class Project2QuotaBizImpl extends BaseBizImpl<Project2Quota> implements Project2QuotaBiz {

	@Resource
	ProjectDao projectDao;

	@Resource
	Project2QuotaDao project2QuotaDao;

	@Resource
	QuotaUsedDao quotaUsedDao;

	@Resource(name = "baseDAO")
	private BaseDAO<NovaFlavor> novaFlavorDao;

	@Resource
	SqlQueryBiz sqlQueryBiz;

	@Resource
	private QuotaClassDao quotaClassDao;

	private static Map<String, String> classCodeMap = new HashMap<>();

	static {
		classCodeMap.put(ConfigProperty.RESOURCE_TYPE_FIREWALL, "firewall");//防火墙
		classCodeMap.put(ConfigProperty.RESOURCE_TYPE_ROUTE, "router");//路由器
		classCodeMap.put(ConfigProperty.RESOURCE_TYPE_NETWORK, "network");//网络
		classCodeMap.put(ConfigProperty.RESOURCE_TYPE_VLB, "loadbalancer");//负载均衡组
		classCodeMap.put(ConfigProperty.RESOURCE_TYPE_VLBPOOL, "listener");//负载均衡
		classCodeMap.put(ConfigProperty.RESOURCE_TYPE_PORT, "ips");//网卡
		classCodeMap.put(ConfigProperty.RESOURCE_TYPE_FLOATINGIP, "floatingip");//公网ip
		classCodeMap.put(ConfigProperty.RESOURCE_TYPE_VOLUME, "volumes");//硬盘
		classCodeMap.put(ConfigProperty.RESOURCE_TYPE_GIGABYTES, "gigabytes");//存储
		classCodeMap.put(ConfigProperty.RESOURCE_TYPE_RAM, "ram");//内存
		classCodeMap.put(ConfigProperty.RESOURCE_TYPE_INSTANCES, "instances");//主机
		classCodeMap.put(ConfigProperty.RESOURCE_TYPE_SNAPSHOTS, "snapshots");//快照
		classCodeMap.put(ConfigProperty.RESOURCE_TYPE_CORES, "cores");//CPU数量
		classCodeMap.put(ConfigProperty.RESOURCE_TYPE_IPSECPOLICY, "ipsecpolicy");//IPsec策略
		classCodeMap.put(ConfigProperty.RESOURCE_TYPE_SECURITYGROUP, "security_group");//安全组
		classCodeMap.put(ConfigProperty.RESOURCE_TYPE_SECURITYGROUPRULE, "security_group_rule");//安全组规则
		classCodeMap.put(ConfigProperty.RESOURCE_TYPE_VPNSERVICE, "vpnservice");//vpn
	}

	@Override
	public Project2Quota get(String projectId, String classCode) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("tenantId", projectId);
		map.put("classCode", classCode);
		map.put("deleted", 0);
		return singleByClass(Project2Quota.class, map);
	}

	/**
	 * 检验并得到配额范围
	 */
	@Override
	public Map<String, Integer> check(Project project, Project2Quota project2Quota) {
		Integer min = 0, max = 0;
		Map<String, Integer> range = new HashMap<>();
		if (StrUtils.checkParam(project.getParentId())) {
			//子租户范围查找
			QuotaUsed quotaUsed = quotaUsedDao.get(project, project2Quota);
			if (StrUtils.checkParam(quotaUsed)) {
				if (StrUtils.checkParam(quotaUsed.getQuotaUsed())) {
					min = quotaUsed.getQuotaUsed();
				} else {
					this.warn("租户配额使用值不存在");
					min = 0;
				}
			} else {
				// 租户没有使用记录
				this.warn("租户没有配额使用记录");
				min = 0;
			}
			if (!ConfigProperty.class_third_resource.contains(project2Quota.getClassCode())) {
				Map<String, Object> map = new HashMap<>();
				map.put("projectId", project.getParentId());
				map.put("classCode", project2Quota.getClassCode());
				// 通过父级租户查找子租户的配额使用量
				List<Map<String, Object>> result = sqlQueryBiz.queryByName(SqlQueryProperty.QUERY_QUOTA_SUM, map);
				Long sum = StrUtils.tranLong(result.get(0).get("sum"));
				//父级租户的最大配额
				Project2Quota project2Quota1 = get(project.getParentId(), project2Quota.getClassCode());
				if (!StrUtils.checkParam(project2Quota1)) {
					throw new MessageException(ResultType.tenant_quota_not_contain);
				}
				max = project2Quota1.getHardLimit();
				if (StrUtils.checkParam(sum)) {
					max = max - sum.intValue();
				}
			} else {
				max = 1000 * 1000 * 1000;
			}
			range.put("min", min);
			range.put("max", max);
			return range;
		} else {
			// 找到自己已使用量的配额数量
			QuotaUsed quotaUsed = quotaUsedDao.get(project, project2Quota);
			if (!ConfigProperty.class_third_resource.equals(project2Quota.getClassCode())) {
				// 查找当前根租户子租户配额之和条件
				Map<String, Object> map = new HashMap<>();
				map.put("projectId", project.getId());
				map.put("classCode", project2Quota.getClassCode());
				List<Map<String, Object>> result = sqlQueryBiz.queryByName(SqlQueryProperty.QUERY_QUOTA_SUM, map);
				Long sum = StrUtils.tranLong(result.get(0).get("sum"));
				min = sum.intValue() + quotaUsed.getQuotaUsed();
			} else {
				min = quotaUsed.getQuotaUsed();
			}
			range.put("min", min);
			range.put("max", 1000 * 1000 * 1000);
			return range;
		}

	}

	/**
	 * 检查租户资源的配额
	 * 
	 * @param classCode
	 * @param projectId
	 * @return
	 */
	@Override
	public void checkQuota(String classCode, String projectId) {
		Map<String, Object> queryMap = new HashMap<>();
		queryMap.put("classCode", classCode);
		queryMap.put("tenantId", projectId);
		queryMap.put("tenantId", 0);
		Project2Quota project2Quota = get(projectId, classCode);// 查询当前租户该资源的配额数据
		List<QuotaUsed> quotaUseds = quotaUsedDao.listByClass(QuotaUsed.class, queryMap);
		if (!StrUtils.checkParam(project2Quota)) {// 租户没有分配配额
			throw new MessageException(ResultType.tenant_not_contain_quota);
		}
		int limit = project2Quota.getHardLimit();// 获取配额上限
		if (limit == 0) {// 租户配额为0
			throw new MessageException(ResultType.tenant_not_contain_quota);
		}
		if (StrUtils.checkParam(quotaUseds)) {// 租户配额已使用数量是否达到配额限制
			QuotaUsed quotaUsed = quotaUseds.get(0);
			Integer usedCount = quotaUsed.getQuotaUsed();
			if (usedCount >= limit) {
				throw new MessageException(ResultType.quota_reach_max);
			}
		}
	}

	/**
	 * 检查租户资源的配额
	 *
	 * @param classCode
	 * @param projectId
	 * @param resourceCount
	 *            新增资源数量
	 * @return
	 */
	@Override
	public void checkQuota(String classCode, String projectId, int resourceCount,SaveNovaVmBean bean) {
		Map<String, String> queryMap = new HashMap<>();
		queryMap.put("classCode", classCode);
		queryMap.put("tenantId", projectId);
		Project2Quota project2Quota = get(projectId, classCode);
		List<QuotaUsed> quotaUseds = quotaUsedDao.findByMap(QuotaUsed.class, queryMap);
		JSONObject result=new JSONObject();
		if (!StrUtils.checkParam(project2Quota)) {// 租户没有分配配额
			throw new MessageException(ResultType.tenant_not_contain_quota);
		}
		int limit = project2Quota.getHardLimit();
		if (limit == 0) {// 租户配额为0
			throw new MessageException(ResultType.tenant_not_contain_quota);
		}
		if (StrUtils.checkParam(quotaUseds)) {// 租户配额已使用数量是否达到配额限制
			QuotaUsed quotaUsed = quotaUseds.get(0);
			Integer usedCount = quotaUsed.getQuotaUsed();
			if (usedCount + resourceCount > limit) {
				this.warn(classCode+"已超限");
				result.put("classCode", classCode);
				result.put("limit", limit);
				bean.setError(result);
				throw new MessageException(ResultType.file_quota_failure);
			}
		}
	}

	@Override
	public void novaQuota(NovaFlavor novaFlavor, String tenantId, int count) {
	}

	@Override
	public void novaQuota(SaveNovaVmBean bean) {
		String tenantId = getProjectId();
		Integer count = bean.getCount();
		// 查询规格是否存在
		NovaFlavor novaFlavor = novaFlavorDao.findById(NovaFlavor.class, bean.getFlavorId());
		if (!StrUtils.checkParam(novaFlavor)) {
			throw new MessageException(ResultType.flavor_not_exist);
		}
		checkQuota("instances", tenantId, count,bean);
		checkQuota("cores", tenantId, count * novaFlavor.getVcpus(),bean);
		checkQuota("ram", tenantId, count * novaFlavor.getRam() / 1024,bean);
		checkQuota("ips", tenantId, count,bean);

	}

	/**
	 * 新增租户资源配额
	 * @param projectId
	 * @param type
	 * @param value
	 */
	public void addQuota(String projectId, String type, Integer value){
		String classCode = classCodeMap.get(type);
		Map<String, Object> queryMap = new HashMap<>();
		queryMap.put("classCode", classCode);
		QuotaClass quotaClass = quotaClassDao.singleByClass(QuotaClass.class, queryMap);
		String classId = quotaClass.getId();
		Project2Quota project2Quota = new Project2Quota();
		project2Quota.setTenantId(projectId);
		project2Quota.setClassId(classId);
		project2Quota.setClassCode(classCode);
		project2Quota.setCreatedDate(new Date());
		project2Quota.setDeleted(0);
		if (!StrUtils.checkParam(value)){
			value = 0;
		}
		//初始化已使用配额为0
		project2Quota.setHardLimit(value);
		project2Quota.createdUser(this.getLoginUser());
		project2QuotaDao.add(project2Quota);
		QuotaUsed quotaUsed = new QuotaUsed();
		quotaUsed.setTenantId(projectId);
		quotaUsed.setClassId(classId);
		quotaUsed.setClassCode(classCode);
		quotaUsed.setQuotaUsed(0);
		quotaUsed.setDeleted(0);
		quotaUsed.setCreatedDate(new Date());
		quotaUsed.createdUser(this.getLoginUser());
		quotaUsedDao.add(quotaUsed);
	}
}

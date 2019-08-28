package com.h3c.iclouds.quartz;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.h3c.iclouds.base.SpringContextHolder;
import com.h3c.iclouds.biz.Project2QuotaBiz;
import com.h3c.iclouds.biz.ProjectBiz;
import com.h3c.iclouds.biz.QuotaUsedBiz;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.dao.QuotaClassDao;
import com.h3c.iclouds.operate.CloudosClient;
import com.h3c.iclouds.po.Project;
import com.h3c.iclouds.po.Project2Quota;
import com.h3c.iclouds.po.QuotaClass;
import com.h3c.iclouds.po.QuotaUsed;
import com.h3c.iclouds.utils.HttpUtils;
import com.h3c.iclouds.utils.ResourceHandle;
import com.h3c.iclouds.utils.StrUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ProjectQuotaQuartz {
//
//	@Resource
//	ProjectBiz projectBiz;
//
//	@Resource
//	Project2QuotaBiz project2QuotaBiz;
//
//	@Resource
//	QuotaUsedBiz quotaUsedBiz;
//
//	@Resource
//	QuotaClassDao quotaClassDao;

	public void syn() {
		QuotaClassDao quotaClassDao = SpringContextHolder.getBean("quotaClassDao");
		QuotaUsedBiz quotaUsedBiz = SpringContextHolder.getBean("quotaUsedBiz");
		Project2QuotaBiz project2QuotaBiz = SpringContextHolder.getBean("project2QuotaBiz");
		ProjectBiz projectBiz = SpringContextHolder.getBean("projectBiz");
		Map<String, String> cloudosQuotaKey = new HashMap<>();
		Map<String, String> cloudosMaxQuotaKey = new HashMap<>();
		cloudosQuotaKey.put("cores", "totalCoresUsed");
		cloudosMaxQuotaKey.put("cores", "maxTotalCores");
		cloudosQuotaKey.put("instances", "totalInstancesUsed");
		cloudosMaxQuotaKey.put("instances", "maxTotalInstances");
		cloudosQuotaKey.put("ram", "totalRAMUsed");
		cloudosMaxQuotaKey.put("ram", "maxTotalRAMSize");
		cloudosQuotaKey.put("gigabytes", "totalGigabytesUsed");
		cloudosMaxQuotaKey.put("gigabytes", "maxTotalVolumeGigabytes");
		cloudosQuotaKey.put("snapshots", "totalSnapshotsUsed");
		cloudosMaxQuotaKey.put("snapshots", "maxTotalSnapshots");
		cloudosQuotaKey.put("volumes", "totalVolumesUsed");
		cloudosMaxQuotaKey.put("volumes", "maxTotalVolumes");
		cloudosQuotaKey.put("router", "totalRoutersUsed");
		cloudosMaxQuotaKey.put("router", "maxRouters");
		cloudosQuotaKey.put("vpnservice", "totalVpnServicesUsed");
		cloudosMaxQuotaKey.put("vpnservice", "maxVpnServices");
		cloudosQuotaKey.put("network", "totalNetworksUsed");
		cloudosMaxQuotaKey.put("network", "maxNetworks");
		cloudosQuotaKey.put("firewall", "totalFirewallsUsed");
		cloudosMaxQuotaKey.put("firewall", "maxFirewalls");
		cloudosQuotaKey.put("ips", "totalVnicUsed");
		cloudosMaxQuotaKey.put("ips", "maxVnic");
		cloudosQuotaKey.put("loadbalancer", "totalLoadbalancersUsed");
		cloudosMaxQuotaKey.put("loadbalancer", "maxLoadbalancers");
		cloudosQuotaKey.put("floatingip", "totalFloatingIpsUsed");
		cloudosMaxQuotaKey.put("floatingip", "maxTotalFloatingIps");
		cloudosQuotaKey.put("listener", "totalListenersUsed");
		cloudosMaxQuotaKey.put("listener", "maxListeners");
		cloudosQuotaKey.put("security_group", "totalSecurityGroupsUsed");
		cloudosMaxQuotaKey.put("security_group", "maxSecurityGroups");
		cloudosQuotaKey.put("ipsecpolicy", "totalIpsecPoliciesUsed");
		cloudosMaxQuotaKey.put("ipsecpolicy", "maxIpsecPolicies");
		cloudosQuotaKey.put("security_group_rule", "totalSecurityGroupRulesUsed");
		cloudosMaxQuotaKey.put("security_group_rule", "maxSecurityGroupRules");
		Map<String, String> quota2ClassId = new HashMap<>();
		List<QuotaClass> quotaClasses = quotaClassDao.getAll(QuotaClass.class);
		for (QuotaClass quotaClass : quotaClasses) {
			quota2ClassId.put(quotaClass.getClassCode(), quotaClass.getId());
		}
		// projectBiz.getAll(Project.class);
		CloudosClient client = CloudosClient.createAdmin();
		for (int j = 0; j < 3; j++) {
			if (StrUtils.checkParam(client)) {
				String uri = "/keystone/all-limits";
				JSONObject result = client.get(uri);
				if (ResourceHandle.judgeResponse(result)) {
					JSONArray array = HttpUtils.getJSONArray(result, "all_limits");
					if (StrUtils.checkCollection(array)) {
						for (int i = 0; i < array.size(); i++) {
							JSONObject item = array.getJSONObject(i).getJSONObject("absolute");
							String projectId = item.getString("projectId");
							if (!ConfigProperty.STOP_SET.contains(projectId)) {
								Project project = projectBiz.findById(Project.class, projectId);
								if (StrUtils.checkParam(project)) {
									Map<String, Object> deleteWhere = new HashMap<>();
									deleteWhere.put("tenantId", projectId);
									project2QuotaBiz.delete(deleteWhere, Project2Quota.class);
									quotaUsedBiz.delete(deleteWhere, QuotaUsed.class);
									// 更新最大配额
									for (String storage : ConfigProperty.class_storage_resource) {
										// 已使用
										String key = cloudosQuotaKey.get(storage);
										Integer quotaUse = item.getInteger(key);
										QuotaUsed quotaUsed = new QuotaUsed();
										quotaUsed.createDate();
										quotaUsed.setTenantId(projectId);
										quotaUsed.setClassId(quota2ClassId.get(storage));
										quotaUsed.setClassCode(storage);
										quotaUsed.setQuotaUsed(quotaUse);
										quotaUsedBiz.add(quotaUsed);

										key = cloudosMaxQuotaKey.get(storage);
										Integer keyMax = item.getInteger(key);
										Project2Quota project2Quota = new Project2Quota();
										project2Quota.setId(UUID.randomUUID().toString());
										project2Quota.setTenantId(projectId);
										project2Quota.setClassCode(storage);
										project2Quota.setClassId(quota2ClassId.get(storage));
										project2Quota.setHardLimit(keyMax);
										project2Quota.createDate();
										project2QuotaBiz.add(project2Quota);

									}
									for (String storage : ConfigProperty.class_computer_resource) {
										// 已使用
										String key = cloudosQuotaKey.get(storage);
										Integer quotaUse = item.getInteger(key);
										QuotaUsed quotaUsed = new QuotaUsed();
										quotaUsed.createDate();
										quotaUsed.setTenantId(projectId);
										quotaUsed.setClassId(quota2ClassId.get(storage));
										quotaUsed.setClassCode(storage);
										quotaUsed.setQuotaUsed(quotaUse);
										quotaUsedBiz.add(quotaUsed);
										key = cloudosMaxQuotaKey.get(storage);
										Integer keyMax = item.getInteger(key);
										Project2Quota project2Quota = new Project2Quota();
										project2Quota.setId(UUID.randomUUID().toString());
										project2Quota.setTenantId(projectId);
										project2Quota.setClassCode(storage);
										project2Quota.setClassId(quota2ClassId.get(storage));
										project2Quota.setHardLimit(keyMax);
										project2Quota.createDate();
										project2QuotaBiz.add(project2Quota);

									}
									for (String storage : ConfigProperty.class_network_resource) {
										// 已使用
										String key = cloudosQuotaKey.get(storage);
										Integer quotaUse = item.getInteger(key);
										QuotaUsed quotaUsed = new QuotaUsed();
										quotaUsed.createDate();
										quotaUsed.setTenantId(projectId);
										quotaUsed.setClassId(quota2ClassId.get(storage));
										quotaUsed.setClassCode(storage);
										quotaUsed.setQuotaUsed(quotaUse);
										quotaUsedBiz.add(quotaUsed);
										key = cloudosMaxQuotaKey.get(storage);
										Integer keyMax = item.getInteger(key);
										Project2Quota project2Quota = new Project2Quota();
										project2Quota.setId(UUID.randomUUID().toString());
										project2Quota.setTenantId(projectId);
										project2Quota.setClassCode(storage);
										project2Quota.setClassId(quota2ClassId.get(storage));
										project2Quota.setHardLimit(keyMax);
										project2Quota.createDate();
										project2QuotaBiz.add(project2Quota);
									}
								}

							}
						}
					}
				} else {
					return;
				}
			}
		}
		System.out.println("==========>>>>Sync project Quota end");
	}
}

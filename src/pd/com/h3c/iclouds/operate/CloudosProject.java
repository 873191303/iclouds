package com.h3c.iclouds.operate;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.h3c.iclouds.auth.CacheSingleton;
import com.h3c.iclouds.base.SpringContextHolder;
import com.h3c.iclouds.biz.Project2NetworkBiz;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.dao.Network2SubnetDao;
import com.h3c.iclouds.dao.Project2NetworkDao;
import com.h3c.iclouds.dao.ProjectDao;
import com.h3c.iclouds.exception.MessageException;
import com.h3c.iclouds.po.Network2Subnet;
import com.h3c.iclouds.po.Project;
import com.h3c.iclouds.po.Project2Network;
import com.h3c.iclouds.utils.IpValidator;
import com.h3c.iclouds.utils.ResourceHandle;
import com.h3c.iclouds.utils.HttpUtils;
import com.h3c.iclouds.utils.LogUtils;
import com.h3c.iclouds.utils.StrUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CloudosProject extends CloudosBase {
	
	private Project2NetworkBiz project2NetworkBiz = SpringContextHolder.getBean("project2NetworkBiz");
	
	private Project2NetworkDao project2NetworkDao = SpringContextHolder.getBean("project2NetworkDao");

	private Network2SubnetDao network2SubnetDao = SpringContextHolder.getBean("network2SubnetDao");

	private ProjectDao projectDao = SpringContextHolder.getBean("projectDao");

	public CloudosProject(CloudosClient client) {
		this.client = client;
	}

	public JSONObject save(Project project) {
		Map<String, Object> paramsProject = projectParams(project);
		/// v3/projects
		String uri = CacheSingleton.getInstance().getCloudosApi(CloudosParams.CLOUDOS_API_PROJECTS);
//		client.setHeaderLocal("Content-Type", "application/json");
		JSONObject result = client.post(uri, paramsProject);// 创建组织
		return result;
	}
	
	public boolean checkName(Project project) {
		String uri = CacheSingleton.getInstance().getCloudosApi(CloudosParams.CLOUDOS_API_PROJECTS);
		uri=uri+"?name="+project.getName();
		JSONObject result = client.get(uri);
		JSONArray projects=HttpUtils.getJSONArray(result,"projects");
		if (StrUtils.checkCollection(projects)) {
			//租户名重复
			return true;
		}
		return false;
	}

	public JSONObject update(Project project) {
//		client.setHeaderLocal("Content-Type", "application/json");
		Map<String, Object> paramsProject = projectParams(project);
		String uri = CacheSingleton.getInstance().getCloudosApi(CloudosParams.CLOUDOS_API_PROJECTS_ACTION);
		uri = HttpUtils.tranUrl(uri, project.getId());
		JSONObject result = client.patch(uri, paramsProject);// 修改组织
		return result;
	}

	public JSONObject delete(Project project, String projectId) {
		String uri = CacheSingleton.getInstance().getCloudosApi(CloudosParams.CLOUDOS_API_PROJECTS_ACTION);
		uri = HttpUtils.tranUrl(uri, projectId);
//		client.setHeaderLocal("Content-Type", "application/json");
		JSONObject result = client.delete(uri);
		return result;
	}

	public JSONObject delete(String projectId) {
		String uri = CacheSingleton.getInstance().getCloudosApi(CloudosParams.CLOUDOS_API_PROJECTS_ACTION);
		uri = HttpUtils.tranUrl(uri, projectId);
		JSONObject result = client.delete(uri);
		return result;
	}

	public boolean isExist(String projectId) {
		String uri = CacheSingleton.getInstance().getCloudosApi(CloudosParams.CLOUDOS_API_PROJECTS_ACTION);
		// String uri = "/v3/projects/{project_id}";
		uri = HttpUtils.tranUrl(uri, projectId);
		JSONObject result = client.get(uri);
		if (ResourceHandle.judgeResponse(result)) {
			return true;
		} else {
			return false;
		}
	}

	public JSONObject saveQuotaNetwork(Project project, String projectId, String cidr, List<Map<String, String>> ipPoolList) {
		String uri = CacheSingleton.getInstance().getCloudosApi(CloudosParams.CLOUDOS_API_QUOTA);
		uri = HttpUtils.tranUrl(uri, projectId, project.getId());
		JSONObject result = client.get(uri);
		if (!ResourceHandle.judgeResponse(result)) {
			LogUtils.warn(Project.class, result.getString("record"));
			throw new MessageException(ResultType.cloudos_api_error);
		}
		result = result.getJSONObject("record");
		JSONObject network = result.getJSONObject("network");
		if (null == network) {
			LogUtils.warn(Project.class, "Get Cloudos Quota Empty");
			throw new MessageException(ResultType.cloudos_login_lose);
		}
		JSONArray networkAddresses = networkParams(cidr, ipPoolList, project);
		network.put("network_addresses", networkAddresses);
		result = client.put(uri, result);
		if (!ResourceHandle.judgeResponse(result)) {
			LogUtils.warn(Project.class, result.getString("record"));
			throw new MessageException(ResultType.cloudos_api_error);
		}
		return result;

	}

	private Map<String, Object> projectParams(Project bean) {
		Map<String, Object> project = new HashMap<>();
		Map<String, Object> params = new HashMap<>();
		params.put("description", bean.getDescription());
		params.put("domain_id", bean.getDomainId()); // 默认为default
		if (StrUtils.checkParam(bean.getParentId())) { // 不存在parentid则不设置
			params.put("parent_id", bean.getParentId());
		}
		params.put("name", bean.getName());
		//params.put("name", bean.getName()+UUID.randomUUID().toString());
		project.put("project", params);
		return project;

	}

	@SuppressWarnings("unchecked")
	private JSONArray networkParams(String cidr, List<Map<String, String>> pools, Project project) {
		// cloudos网络配额参数值段
		List<Object> networkAddresses = new ArrayList<>();
		Map<String, Object> networkAddress = new HashMap<>();
		networkAddress.put("cidr", cidr);
		// 增加的ipPool
		List<Map<String, String>> ipPool = new ArrayList<>();
		// 增加的校验方面的ip
		for (Map<String, String> map : pools) {
			Map<String, String> ip = new HashMap<>();
			ip.put("start_ip", map.get("start"));
			ip.put("end_ip", map.get("end"));
			ipPool.add(ip);
		}
		networkAddress.put("ip_pools", ipPool);
		networkAddresses.add(networkAddress);
		// 校验ip段是否在父级网络配额范围内
		if (null != project.getParentId()) {
			Project parentProject = projectDao.findById(Project.class, project.getParentId());
			project2NetworkBiz.isContainProjectIp(parentProject, pools);
		}
		networkAddresses = getProjectIps(project, pools, networkAddresses);
		return new JSONArray(networkAddresses);
	}
	
	private List<Object> getProjectIps(Project project, List<Map<String, String>> addPool, List<Object> networkAddresses) {
		// 校验ip
		List<Map<String, String>> checkIpools = new ArrayList<>();
		Map<String, Object> map = new HashMap<>();

		map.put("tenantId", project.getId());
		String hql = "from Project2Network p where p.deleted=0 and p.tenantId=:tenantId";
		List<Project2Network> project2Networks = project2NetworkDao.findByHql(hql, map);
		for (Project2Network project2Network : project2Networks) {
			// 辅助变量
			Map<String, Object> networkAddress = new HashMap<>();
			List<Map<String, String>> temp = new ArrayList<>();
			Map<String, Object> map1 = new HashMap<>();
			map1.put("networkId", project2Network.getId());
			hql = "from Network2Subnet pn where pn.deleted=0 and pn.networkId=:networkId";
			List<Network2Subnet> network2Subnets = network2SubnetDao.findByHql(hql, map1);
			for (Network2Subnet network2Subnet : network2Subnets) {
				// 数据库ip
				Map<String, String> map2 = new HashMap<>();
				map2.put("start", network2Subnet.getStartIp());
				map2.put("end", network2Subnet.getEndIp());
				checkIpools.add(map2);// 本地需要校验的ip
				Map<String, String> ip = new HashMap<>();
				ip.put("start_ip", network2Subnet.getStartIp());
				ip.put("end_ip", network2Subnet.getEndIp());
				temp.add(ip);

			}
			networkAddress.put("cidr", project2Network.getCidr());
			networkAddress.put("ip_pools", temp);
			networkAddresses.add(networkAddress);
		}
		//校验是否与该租户其它网络配额重叠
		checkIpools.addAll(addPool);
		if (!IpValidator.checkIpPoolRepeat(checkIpools)) {
			throw new MessageException(ResultType.cidr_contain_repeat);
		}
		return networkAddresses;
	}

}

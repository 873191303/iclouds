package com.h3c.iclouds.biz.impl;

import com.h3c.iclouds.base.BaseBizImpl;
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
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("project2NetworkBiz")
public class Project2NetworkBizImpl extends BaseBizImpl<Project2Network>implements Project2NetworkBiz{

	@Resource
	private Project2NetworkDao project2NetworkDao;
	
	@Resource
	private ProjectDao projectDao;
	
	@Resource
	private Network2SubnetDao network2SubnetDao;
	
	@Override
	public List<Project2Network> get(Project project) {
		List<Project2Network> project2Networks = new ArrayList<Project2Network>();
		//排除当前已被用了的
		Map<String, Object> map = new HashMap<>();
		map.put("tenantId", project.getParentId());
		map.put("deleted", 0);
		String hql="from Project2Network p where p.tenantId=:tenantId And p.deleted=:deleted";
		project2Networks.addAll(project2NetworkDao.findByHql(hql, map));
		map.put("tenantId", project.getId());
		project2Networks.removeAll(project2NetworkDao.findByHql(hql, map));
		return project2Networks;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Object check(String tenantId, String cidr, Map<String, Object> ips) {
		Project project=projectDao.getExistProject(tenantId);
		List<Map<String, String>> pools = (List<Map<String, String>>) ips.get("ipPool");
		// 增加的校验方面的ip
		if (project.getParentId() != null) {
			Project parentProject = projectDao.findById(Project.class, project.getParentId());
			isContainProjectIp(parentProject, pools);
		}
		check(project, pools);
		return null;
	}
	
	private void check(Project project, List<Map<String, String>> addPool) {
		List<Map<String, String>> checkIpools = new ArrayList<>();
		Map<String, Object> map = new HashMap<>();
		map.put("tenantId", project.getId());
		String hql = "from Project2Network p where p.deleted=0 and p.tenantId=:tenantId";
		List<Project2Network> project2Networks = project2NetworkDao.findByHql(hql, map);
		for (Project2Network project2Network : project2Networks) {
			List<Map<String, String>> temp = network2SubnetDao.get(project2Network);
			checkIpools.addAll(temp);
		}
		checkIpools.addAll(addPool);
		if (!IpValidator.checkIpPoolRepeat(checkIpools)) {
			throw new MessageException(ResultType.cidr_contain_repeat);
		}
	}

	public void isContainProjectIp(Project parentProject, List<Map<String, String>> addPool) {
		Map<String, Object> map = new HashMap<>();
		map.put("tenantId", parentProject.getId());
		String hql = "from Project2Network p where p.deleted=0 and p.tenantId=:tenantId";
		List<Project2Network> project2Networks = project2NetworkDao.findByHql(hql, map);
		//只要addPool在父级里的有重复
		for (Project2Network project2Network : project2Networks) {
			List<Map<String, String>> ips = network2SubnetDao.get(project2Network);
			if (IpValidator.checkIpPoolRepeat(addPool, ips)) {
				return;
			}
		}
		throw new MessageException(ResultType.cidr_over_parent_project);
	}

	@Override
	public Object get(Project project, String networkId) {
		List<Network2Subnet> subnets=network2SubnetDao.findByPropertyName(Network2Subnet.class, "networkId", networkId);
		return subnets;
	}
}

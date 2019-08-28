package com.h3c.iclouds.biz;

import java.util.List;
import java.util.Map;

import com.h3c.iclouds.base.BaseBiz;
import com.h3c.iclouds.po.Project;
import com.h3c.iclouds.po.Project2Network;

public interface Project2NetworkBiz extends BaseBiz<Project2Network> {

	List<Project2Network> get(Project project);

	Object check(String id, String cidr, Map<String, Object> ips);

	Object get(Project project, String networkId);
	
	void isContainProjectIp(Project parentProject, List<Map<String, String>> addPool);
}

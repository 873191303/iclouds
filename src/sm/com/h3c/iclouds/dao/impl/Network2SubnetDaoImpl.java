package com.h3c.iclouds.dao.impl;

import com.h3c.iclouds.base.BaseDAOImpl;
import com.h3c.iclouds.dao.Network2SubnetDao;
import com.h3c.iclouds.po.Network2Subnet;
import com.h3c.iclouds.po.Project2Network;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository("network2SubnetDao")
public class Network2SubnetDaoImpl extends BaseDAOImpl<Network2Subnet> implements Network2SubnetDao {
	
	@Override
	public List<Map<String, String>> get(Project2Network project2Network) {
		//获取网络下所有IP段
		List<Map<String, String>> ips = new ArrayList<>();
		Map<String, Object> map1 = new HashMap<>();
		map1.put("networkId", project2Network.getId());
		String hql = "from Network2Subnet pn where pn.deleted=0 and pn.networkId=:networkId";
		List<Network2Subnet> network2Subnets =findByHql(hql, map1);
		for (Network2Subnet network2Subnet : network2Subnets) {
			Map<String, String> map2 = new HashMap<>();
			map2.put("start", network2Subnet.getStartIp());
			map2.put("end", network2Subnet.getEndIp());
			ips.add(map2);// 本地需要校验的ip
		}
		return ips;
				
	}
}

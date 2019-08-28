package com.h3c.iclouds.po.bean.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.h3c.iclouds.po.Network2Subnet;
import com.h3c.iclouds.utils.InvokeSetForm;

public class NetworksBean implements Serializable {

	private static final long serialVersionUID = -4324337135530408904L;
	private Project2NetworkBean cidr;
	private List<Network2SubnetBean> subnets;
	
	public Project2NetworkBean getCidr() {
		return cidr;
	}

	public void setCidr(Project2NetworkBean cidr) {
		this.cidr = cidr;
	}

	public List<Network2SubnetBean> getSubnets() {
		return subnets;
	}

	public void setSubnets(List<Network2SubnetBean> subnets) {
		this.subnets = subnets;
	}

	public List<Network2SubnetBean> po2Bean(List<Network2Subnet> network2Subnets) {
		List<Network2SubnetBean> network2SubnetBeans = new ArrayList<Network2SubnetBean>(network2Subnets.size());
		for (Network2Subnet network2Subnet : network2Subnets) {
			Network2SubnetBean bean = new Network2SubnetBean();
			InvokeSetForm.copyFormProperties1(network2Subnet, bean);
			bean.setId(network2Subnet.getId());
			network2SubnetBeans.add(bean);
		}
		return network2SubnetBeans;

	}

}

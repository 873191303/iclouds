package com.h3c.iclouds.po.bean.inside;

import java.io.Serializable;
import java.util.List;

import com.h3c.iclouds.po.Network2Subnet;
import com.h3c.iclouds.po.Project2Network;

public class SaveNetworksBean implements Serializable {

	private static final long serialVersionUID = -9173292114836941436L;

	private Project2Network network;
	
	private List<Network2Subnet> subnets;
	
	public Project2Network getNetwork() {
		return network;
	}
	public void setNetwork(Project2Network network) {
		this.network = network;
	}
	public List<Network2Subnet> getSubnets() {
		return subnets;
	}
	public void setSubnets(List<Network2Subnet> subnets) {
		this.subnets = subnets;
	}
	
	
}

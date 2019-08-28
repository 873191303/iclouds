package com.h3c.iclouds.po.bean.model;

import java.io.Serializable;
import java.util.Map;

public class QuotaBean implements Serializable{

	private static final long serialVersionUID = -6939542663231268813L;
	
	private Storage block_storage;
	
	
	public static class Storage implements Serializable{

		private static final long serialVersionUID = 1006574387774258343L;
		
		private Map<String, Object> quota_set;

		public Map<String, Object> getQuota_set() {
			return quota_set;
		}

		public void setQuota_set(Map<String, Object> quota_set) {
			this.quota_set = quota_set;
		}
	}
	
	public static class Network implements Serializable{

		/**
		 * 
		 */
		private static final long serialVersionUID = -4427245998342709055L;

//		private String subnet;
//		private String network;
//		private String floatingip;
//		private String security_group_rule;
//		private String security_group;
//		private String router;
//		private String port;
//		private String ipsecpolicy;
		
//		"subnet":166,
//		"network":104,
//		"floatingip":501,
//		"security_group_rule":1001,
//		"security_group":101,
//		"router":105,
//		"port":502
//		“ipsecpolicy": 10,
//		"vpnservice": 10,
//		“firewall”,：10,
//		“listener”：10,
//		“loadbalancer”,：10,
//		“waf”:10,
//		“ips”:10,
//		“virus”:10,

		
		
	}

	public Storage getBlock_storage() {
		return block_storage;
	}

	public void setBlock_storage(Storage block_storage) {
		this.block_storage = block_storage;
	}
}

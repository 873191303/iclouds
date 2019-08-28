package com.h3c.iclouds.common;

public enum CompareEnum {
	
	TASK_DIFF_ID("1"),	// 同步任务id
	
	SYNCTYPE_COMPARE("与cloudos数据对比"),

    IN_CLOUDOS("cloudos中存在"),
    
    IN_IYUN("iyun中存在"),
    
    DATA_DIFF("两端的数据不一致"),
    
    OPE_DIFF("同步差异内容"),
    
    OPE_SYNC("同步缺少内容"),
    
    OPE_DEL("删除多余内容"),
    
    PROJECT("租户"),
    
    USER("用户"),
    RESTENANCY("资源租期"),
    
    FIREWALL("防火墙"),
    
    FIREWALL_POLICY("防火墙规则集"),
    
    FIREWALL_RULE("防火墙规则"),
    
    ROUTER("路由器"),
    
    ROUTER_LINK_FIREWALL("路由器连接防火墙"),
    
    NETWORK("网络"),
    
    NETWORK_LINK_ROUTER("网络连接路由器"),
    
    PUBLIC_NETWORK("公网池"),
    
    FLOATINGIP("公网IP"),
    
    FLOATINGIP_LINK_CLOUDHOST("公网IP连接云主机"),
    
    FLOATINGIP_LINK_MONITOR("公网IP连接负载均衡监视器"),
    
    VLB("负载均衡"),
    
    MONITOR("监视器"),
    
    MONITOR_MEMBER("负载均衡监视器成员"),
    
    MONITOR_LINK_NETWORK("监视器连接网络"),
    
    CLOUDHOST("云主机"),
    
    VOLUME("云硬盘"),
    
    VOLUME_LINK_CLOUDHOST("云硬盘挂载云主机"),
    
    PORT("虚拟网卡"),
	;
 
    private String opeValue;

    private CompareEnum(String opeValue) {
        this.opeValue = opeValue;
    }
 
    public String getOpeValue() {
    	return opeValue;
    }
    
}

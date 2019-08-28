package com.h3c.iclouds.common;

/**
 * 只允许枚举尾部追加，不允许插空
 * @author zkf5485
 *
 */
public enum OperateLogEnum {

	LOGIN("1", "用户登录"),
	
	USER_RETRIEVE("2", "用户管理-查询"),
	
	USER_CREATE("3", "用户管理-新增"),
	
	USER_UPDATE("4", "用户管理-修改"),
	
	USER_DELETE("5", "用户管理-删除"),

    FIREWALL_CREATE("6", "防火墙管理-新增"),

    FIREWALL_UPDATE("7", "防火墙管理-修改"),

    FIREWALL_DELETE("8", "防火墙管理-删除"),

    ROUTE_CREATE("9", "路由器管理-新增"),

    ROUTE_UPDATE("10", "路由器管理-修改"),

    ROUTE_DELETE("11", "路由器管理-删除"),

    ROUTE_LINK("12", "路由器管理-连接"),

    ROUTE_UNLINK("13", "路由器管理-断开连接"),

    NETWORK_CREATE("14", "网络管理-新增"),

    NETWORK_UPDATE("15", "网络管理-修改"),

    NETWORK_DELETE("16", "网络管理-删除"),

    NETWORK_LINK("17", "网络管理-连接"),

    NETWORK_UNLINK("18", "网络管理-断开连接"),

    VLBPOOL_CREATE("19", "负载均衡监听器管理-新增"),

    VLBPOOL_UPDATE("20", "负载均衡监听器管理-修改"),

    VLBPOOL_DELETE("21", "负载均衡监听器管理-删除"),

    VLB_CREATE("22", "负载均衡组管理-新增"),

    VLB_UPDATE("23", "负载均衡组管理-修改"),

    VLB_DELETE("24", "负载均衡组管理-删除"),

    VLBMEMBER_CREATE("25", "负载均衡成员管理-新增"),

    VLBMEMBER_UPDATE("26", "负载均衡成员管理-修改"),

    VLBMEMBER_DELETE("27", "负载均衡成员管理-删除"),

    POLICYRULE_CREATE("28", "防火墙规则管理-新增"),

    POLICIERULE_UPDATE("29", "防火墙规则管理-修改"),

    POLICIERULE_DELETE("30", "防火墙规则管理-删除"),

    VDCINFO_SAVE("31", "vdc视图-整体保存"),
    
//    VOLUME_("32", "云硬盘-查询"),
    
    VOLUME_CREATE("33", "云硬盘-新增"),
    
    VOLUME_UPDATE("34", "云硬盘-修改"),
    
    VOLUME_DELETE("35", "云硬盘-删除"),
    
    VOLUME_ATTACH("36", "云硬盘-挂载至主机"),

    VOLUME_DETTACH("37", "云硬盘-从主机卸载"),
    
    FLOATINGIP_("38", "floatingIp-查询"),
    
    FLOATINGIP_CREATE("39", "floatingIp-新增"),
    
    FLOATINGIP_UPDATE("40", "floatingIp-修改"),
    
    FLOATINGIP_DELETE("41", "floatingIp-删除"),
    
//    ABC_("42", "爱数备份-查询"),
    
    SERVER_START("43", "云主机-开机"),
    
    SERVER_STOP("44", "云主机-关机"),
    
    SERVER_REBOOT("45", "云主机-重启"),
    
    SERVER_CONSOLE("46", "云主机-打开控制台"),
    
    SERVER_CREATE("47", "云主机-创建"),
    
    SERVER_DELETE("48", "云主机-删除"),
    
    PROJECT_CREATE("49", "租户-创建"),
    
    PROJECT_UPDATE("50", "租户-修改(基本信息及配额)"),
    
    PROJECT_DELETE("51", "租户-删除"),
    
    RECYCLE_VOLUME_DELETE("52", "回收站-删除云硬盘"),
    
    RECYCLE_DELETE("53", "回收站-删除云主机"),
    
    RECYCLE_VOLUME_UPDATE("54", "回收站-恢复云硬盘"),
    
    RECYCLE_UPDATE("55", "回收站-恢复云主机"),
    
    SERVER_TOIMAGE("56", "云主机-转为镜像"),
    
    PORT_CREATE("57", "网卡-新增"),
    
    PORT_DELETE("58", "网卡-删除"),
    
    PORT_ATTACH("59", "网卡-挂载云主机"),
    
    PORT_DETTACH("60", "网卡-从主机卸载"),

    SERVER_INIT_MONITOR("61", "云主机-加入监控"),

    SERVER_REMOVE_MONITOR("62", "云主机-移除监控"),

    SERVER_CLONE("63", "云主机-克隆"),
	;
 
    private String logTypeId;
    
    private String logTypeValue;

    private OperateLogEnum(String logTypeId, String logTypeValue) {
        this.logTypeId = logTypeId;
        this.logTypeValue = logTypeValue;
    }
 
    public String getLogTypeId() {
        return logTypeId;
    }
    
    public String getLogTypeValue() {
    	return logTypeValue;
    }

}
package com.h3c.iclouds.client.zabbix;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ZabbixDefine {
	
	public static final Map<String, String> ITEM_QUERY_TYPE = new HashMap<String, String>();
	
	public static final Map<String, String> DB_TYPE = new HashMap<String, String>();
	
	public static final Set<String> DB_SET = new HashSet<String>();
	
	public static final Set<String> MIDDLEWARE_SET = new HashSet<String>();
	
	public static final Set<String> OS_SET = new HashSet<String>();
	
	static {
		ITEM_QUERY_TYPE.put("os", "OS");
		ITEM_QUERY_TYPE.put("http", "http");
		ITEM_QUERY_TYPE.put("middleware", "Middleware");
		ITEM_QUERY_TYPE.put("db", "DB");
		ITEM_QUERY_TYPE.put("telnet", "telnet");
		ITEM_QUERY_TYPE.put("ping", "ping");
		ITEM_QUERY_TYPE.put("log", "log");
		
		DB_TYPE.put("MySQL", "mysql");
		DB_TYPE.put("Oracle", "oracle");
		DB_TYPE.put("Postgres", "postgres");
		DB_TYPE.put("SQL Server", "sqlServer");
		
		MIDDLEWARE_SET.add("Glassfish");
		MIDDLEWARE_SET.add("IIS");
		MIDDLEWARE_SET.add("Tomcat");
		MIDDLEWARE_SET.add("Weblogic");
		MIDDLEWARE_SET.add("Websphere");
		
		DB_SET.add("Oracle");
		DB_SET.add("MySQL");
		DB_SET.add("SQL Server");
		DB_SET.add("Postgres");
		
		OS_SET.add("CBMS Server");
		OS_SET.add("OS");
		OS_SET.add("CBMS Proxy");
		
	}
	
	public static final String GET = "get";
	
	public static final String CREATE = "create";
	
	public static final String UPDATE = "update";
	
	public static final String DELETE = "delete";
	
	public static final String EXPORT = "export";
	
	public static final String IMPORT = "import";
	
	public static final String ACKNOWLEDGE = "acknowledge";
	
	public static final String ISREADABLE = "isreadable";
	
	public static final String ISWRITABLE = "iswritable";
	
	public static final String GETOBJECTS = "getobjects";
	
	public static final String MASSADD = "massadd";
	
	public static final String MASSREMOVE = "massremove";
	
	public static final String MASSUPDATE = "massupdate";
	
	public static final String ADDTIMES = "addtimes";
	
	public static final String REPLACEHOSTINTERFACES = "replacehostinterfaces";
	
	public static final String DELETETIMES = "deletetimes";
	
	public static final String DELETEDEPENDENCIES = "deletedependencies";
	
	public static final String ADDDEPENDENCIES = "adddependencies";
	
	public static final String COPY = "copy";
	
	public static final String EXECUTE = "execute";
	
	public static final String GETSCRIPTSBYHOSTS = "getscriptsbyhosts";
	
	public static final String DELETEMEDIA = "deletemedia";
	
	public static final String ADDMEDIA = "addmedia";
	
	public static final String UPDATEMEDIA = "updatemedia";
	
	public static final String CREATEGLOBAL = "createglobal";
	
	public static final String UPDATEGLOBAL = "updateglobal";
	
	public static final String DELETEGLOBAL = "deleteglobal";
	
	public static final String UPDATEPROFILE = "updateprofile";
	
	public static final String LOGIN = "login";
	
	public static final String LOGOUT = "logout";
	
	public static final String ACTION = "action.";
	
	public static final String ALERT = "alert.";
	
	public static final String APPLICATION = "application.";
	
	public static final String CONFIGURATION = "configuration.";
	
	public static final String DHOST = "dhost.";
	
	public static final String DSERVICE = "dservice.";
	
	public static final String DCHECK = "dcheck.";
	
	public static final String DRULE = "drule.";
	
	public static final String EVENT = "event.";
	
	public static final String GRAPH = "graph.";
	
	public static final String GRAPHITEM = "graphitem.";
	
	public static final String GRAPHPROTOTYPE = "graphprototype.";
	
	public static final String HISTORY = "history.";
	
	public static final String HOST = "host.";
	
	public static final String HOSTGROUP = "hostgroup.";
	
	public static final String HOSTINTERFACE = "hostinterface.";
	
	public static final String HOSTPROTOTYPE = "hostprototype.";
	
	public static final String ICONMAP = "iconmap.";
	
	public static final String IMAGE = "image.";
	
	public static final String ITEM = "item.";
	
	public static final String ITEMPROTOTYPE = "itemprototype.";
	
	public static final String SERVICE = "service.";
	
	public static final String DISCOVERYRULE = "discoveryrule.";
	
	public static final String MAINTENANCE = "maintenance.";
	
	public static final String USERMEDIA = "usermedia.";
	
	public static final String MEDIATYPE = "mediatype.";
	
	public static final String PROXY = "proxy.";
	
	public static final String SCREEN = "screen.";
	
	public static final String TEMPLATE = "template.";
	
	public static final String SCREENITEM = "screenitem.";
	
	public static final String TEMPLATESCREEN = "templatescreen.";
	
	public static final String SCRIPT = "script.";
	
	public static final String TRIGGERPROTOTYPE = "triggerprototype.";
	
	public static final String TRIGGER = "trigger.";
	
	public static final String TEMPLATESCREENITEM = "templatescreenitem.";
	
	public static final String USER = "user.";
	
	public static final String USERGROUP = "usergroup.";
	
	public static final String USERMACRO = "usermacro.";
	
	public static final String VALUEMAP = "valuemap.";
	
	public static final String HTTPTEST = "httptest.";
	
	// 基础平台模板
	public static final String BASECOMPUTE = "base_compute";
	
	// 基础服务模板
	public static final String BASESERVICE = "base_service";
	
	// 基础服务模板
	public static final String BASE = "base";
	
	// 自定义模板
	public static final String CUSTOM = "custom";
	
	public static final String[] RESOURCE_KEYS = {"item", "httptest", "trigger"};
	
}

package com.h3c.iclouds.biz.impl;

import com.h3c.iclouds.base.BaseBizImpl;
import com.h3c.iclouds.base.SqlQueryBiz;
import com.h3c.iclouds.biz.AppItemsBiz;
import com.h3c.iclouds.biz.AppViewsBiz;
import com.h3c.iclouds.biz.ApplicationMasterBiz;
import com.h3c.iclouds.biz.DatabaseMasterBiz;
import com.h3c.iclouds.biz.ServiceClusterBiz;
import com.h3c.iclouds.biz.ServiceMasterBiz;
import com.h3c.iclouds.common.SqlQueryProperty;
import com.h3c.iclouds.po.AppItems;
import com.h3c.iclouds.po.AppViews;
import com.h3c.iclouds.po.ApplicationMaster;
import com.h3c.iclouds.po.DatabaseMaster;
import com.h3c.iclouds.po.ServiceCluster;
import com.h3c.iclouds.po.ServiceMaster;
import com.h3c.iclouds.utils.StrUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("appViewsBiz")
public class AppViewsBizImpl extends BaseBizImpl<AppViews> implements AppViewsBiz {

	@Resource
	private ApplicationMasterBiz applicationMasterBiz;

	@Resource
	private AppItemsBiz appItemsBiz;

	@Resource
	private DatabaseMasterBiz databaseMasterBiz;
	
	@Resource
	private ServiceClusterBiz serviceClusterBiz;
	
	@Resource
	private ServiceMasterBiz serviceMasterBiz;
	
	@Resource
	private SqlQueryBiz sqlQueryBiz;
	
	@Override
	public Map<String, Object> getAppViewByAppId(String appId) {
		Map<String, String> paramMap=new HashMap<>();
		paramMap.put("appId", appId);
		//级联查询
		List<Map<String, Object>> result = sqlQueryBiz.findByQuery(SqlQueryProperty.QUERY_APP_VIEWS, paramMap);
		List<Map<String, Object>> app = new ArrayList<>();
		List<Map<String, Object>> culster_service = new ArrayList<>();
		List<Map<String, Object>> culster_db = new ArrayList<>();
		List<Map<String, Object>> service = new ArrayList<>();
		List<Map<String, Object>> db = new ArrayList<>();
		Map<String, Object> data = new HashMap<>();
		
		for (Map<String, Object> map : result) {
			String type=(String) map.get("itemtype");
			String pid=(String) map.get("pid");
			String temp[]=new String[]{};
			if (StrUtils.checkParam(pid)) {
				temp=pid.split(";");
			}
			map.put("pid", temp);
			switch (type) {
			case "0":
				
				app.add(map);
				
				break;
			case "12":
				culster_service.add(map);
				break;
			case "13":
				culster_db.add(map);
				break;
			case "2":
				service.add(map);
				break;
			case "3":
				db.add(map);
				break;

			default:
				break;
			}
		}
		data.put("app", app);
		data.put("culster_service", culster_service);
		data.put("culster_db", culster_db);
		data.put("service", service);
		data.put("db", db);
		return data;
	}
	
	@Override
	public Object get(String type,String id) {
		Object result=null;
		switch (type) {
		case "0":
			ApplicationMaster applicationMaster=applicationMasterBiz.findById(ApplicationMaster.class, id);
			result=applicationMaster;
			break;
		case "12":
			result=serviceClusterBiz.findById(ServiceCluster.class, id);
			break;
		case "13":
			result=serviceClusterBiz.findById(ServiceCluster.class, id);
			break;
		case "2":
			result=serviceMasterBiz.findById(ServiceMaster.class, id);
			break;
		case "3":
			result=databaseMasterBiz.findById(DatabaseMaster.class, id);
			break;

		default:
			break;
		}
		return result;
	}
	
	@Override
	public String getViews(String id) {
		AppItems appItems = appItemsBiz.findById(AppItems.class, id);
		return appItems.getViewId();
	}
	
	@Override
	public void clearLock(AppViews appViews) {
		appViews.setLock(false);
		appViews.setUserId(null);
		appViews.setVersion(null);
		appViews.setSessionId(null);
	    this.update(appViews);
	}

}

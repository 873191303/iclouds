package com.h3c.iclouds.quartz;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.h3c.iclouds.auth.CacheSingleton;
import com.h3c.iclouds.base.SpringContextHolder;
import com.h3c.iclouds.biz.DifferencesBiz;
import com.h3c.iclouds.biz.RenewalBiz;
import com.h3c.iclouds.biz.UserBiz;
import com.h3c.iclouds.common.CompareEnum;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.operate.CloudosClient;
import com.h3c.iclouds.operate.CloudosParams;
import com.h3c.iclouds.po.Renewal;
import com.h3c.iclouds.po.User;
import com.h3c.iclouds.utils.HttpUtils;
import com.h3c.iclouds.utils.LogUtils;
import com.h3c.iclouds.utils.ThreadContext;

public class RenewalQuartz {
	
	@Resource
	private RenewalBiz renewalBiz;
	
	
	private UserBiz userBiz;
	
	private DifferencesBiz differencesBiz;
	private static CacheSingleton singleton = CacheSingleton.getInstance();
	/**
     * 同步  资源租期数据
     */
    @SuppressWarnings("unused")
	public void synchronization(){
    	ThreadContext.clear();
    	ThreadContext.set(ConfigProperty.LOG_WRITE_TYPE, ConfigProperty.LOG_WRITE_TYPE_STOP); 
    	try {
    		// 获取CloudOS的用户
			CloudosClient client = this.getClient();
			
    		//同步资源租期
    		renewalBiz = SpringContextHolder.getBean("renewalBiz");
    		diffRenewal(renewalBiz,client);
    		
    		//同步配额 
    		differencesBiz = SpringContextHolder.getBean("differencesBiz");
    		//查询用户表
    		userBiz = SpringContextHolder.getBean("userBiz");
    		List<User> listUser = userBiz.getAll(User.class);
    		for(User dto : listUser) {
    			if(dto.getProjectId()!=null && !"".equals(dto.getProjectId()) ) {
    				differencesBiz.syncIyunQuota(dto.getProjectId(), client);
    			}
    			
    		}
    		
    		//同步 可用域基础数据 
    		synkyy();
    		
    	}catch (Exception e) {
            LogUtils.exception(this.getClass(), e);
        } finally { 
        	ThreadContext.clear();
        }
    }
    private void synkyy() {
//    	String zoneBaseUrl = CacheSingleton.getInstance()
//				.getCloudosApi(CloudosParams.CLOUDOS_API_AZONES_ACTION);
//    	zoneBaseUrl = HttpUtils.tranUrl(zoneBaseUrl, id);
//		JSONObject response = client.get(zoneBaseUrl);
    }
    private CloudosClient getClient() {
		CloudosClient client = CloudosClient.createAdmin();
		return client;
	}
    private int getRenewalRowHash(String uuid,String userUuid,String userName,String serUuid,String resUuid,String resName,String startTime,String endTime,boolean due,String tenantId,int tenancyDay,boolean deleted,String description) {
		return (uuid+userUuid+userName+serUuid+resUuid+resName+startTime+endTime+String.valueOf(due).toString()+String.valueOf(tenancyDay).toString()+description).hashCode();
	}
	//比对租期数据
	private void diffRenewal(RenewalBiz renewalBiz,CloudosClient client) {
		CompareEnum dataType = CompareEnum.RESTENANCY;	// 类型为用户
		
		
		String uri = singleton.getCloudosApi(CloudosParams.CLOUDOS_API_RESTENANCY);
		
		JSONArray restenancys = HttpUtils.getJSONArray(client.get(uri), "resourceTenancys");
		if(null == restenancys) { 
			LogUtils.info(this.getClass(), "获取CloudOS资源租期数据失败失败.");
			return;
		}
		
		 
		 
		List<Renewal> list = renewalBiz.getAll(Renewal.class);
		Map<String, Renewal> renewalOldMap = new HashMap<String, Renewal>();
		for(int i=0;i<list.size();i++) {
			Renewal vo = list.get(i);
			int oldhash = getRenewalRowHash(vo.getUuid(),vo.getUserUuid(),vo.getUserName(),vo.getServiceUuid(),vo.getResourceUuid(),vo.getResourceName(),String.valueOf(vo.getStartTime().getTime()),String.valueOf(vo.getEndTime().getTime()),vo.isIsdue(),vo.getTenantId(),vo.getTenancyDay(),vo.isDeleted(),vo.getDescription()); 
			vo.setMyhash(oldhash);
			renewalOldMap.put(vo.getUuid(), vo);
		}
		
		for (int i = 0; i < restenancys.size(); i++) {
			JSONObject renewalJson = restenancys.getJSONObject(i);
			String uuid = renewalJson.getString("uuid");
			String userUuid = renewalJson.getString("userUuid");
			String userName = renewalJson.getString("userName");
			String serUuid = renewalJson.getString("serUuid");
			String resUuid = renewalJson.getString("resUuid");
			String resName = renewalJson.getString("resName");
			String startTime = renewalJson.getString("startTime");
			String endTime = renewalJson.getString("endTime");
			boolean due = renewalJson.getBoolean("due");
			String tenantId = renewalJson.getString("tenantId");
			int tenancyDay = renewalJson.getInteger("tenancyDay");
			boolean deleted = renewalJson.getBoolean("deleted");
			String description = renewalJson.getString("description"); 
			
			int newHash = getRenewalRowHash(uuid, userUuid, userName, serUuid, resUuid, resName, startTime, endTime, due, tenantId, tenancyDay, deleted, description);
			Renewal newrenewal  =new Renewal();
			newrenewal.setUuid(uuid);
			newrenewal.setUserUuid(userUuid);
			newrenewal.setUserName(userName);
			newrenewal.setServiceUuid(serUuid);
			newrenewal.setResourceUuid(resUuid);
			newrenewal.setResourceName(resName);
			newrenewal.setStartTime(new Date(Long.valueOf(startTime)));
			newrenewal.setEndTime(new Date(Long.valueOf(endTime)));
			newrenewal.setIsdue(due);
			newrenewal.setTenancyDay(tenancyDay);
			newrenewal.setDeleted(deleted);
			newrenewal.setDescription(description);
			
			if(renewalOldMap.containsKey(uuid)) {
				if(newHash!=renewalOldMap.get(uuid).getMyhash()) {
					//this.differencesBiz.addDiff(resUuid, resName, "资源租期[" + resName + "][" + userName + "]数据不一致", CompareEnum.DATA_DIFF, dataType);
					this.renewalBiz.delete(renewalOldMap.get(uuid));
					this.renewalBiz.add(newrenewal);
				}else {
					renewalOldMap.remove(uuid);
				}
			
			}else {
				//this.differencesBiz.addDiff(resUuid, resName, "资源租期[" + resName + "][" + userName + "]在CloudOS中存在，在IYun中不存在", CompareEnum.IN_CLOUDOS, dataType);				
				this.renewalBiz.add(newrenewal);
			}
			 
		}
		
//		if(!renewalOldMap.isEmpty()) {
//			for (Renewal entity : renewalOldMap.values()) {
//				String resUuid = entity.getUuid();
//				String resName = entity.getResourceName();
//				String userName =  entity.getUserName();
//				this.differencesBiz.addDiff(resUuid, resName, "资源[" + resName + "][" + userName + "]在IYun中存在，在CloudOS中不存在", CompareEnum.IN_IYUN, dataType);
//			}
//		}
	}
}

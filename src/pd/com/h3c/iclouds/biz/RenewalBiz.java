package com.h3c.iclouds.biz;

import java.util.Date;
import java.util.List;

import com.h3c.iclouds.base.BaseBiz;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.operate.CloudosClient;
import com.h3c.iclouds.po.Renewal;

public interface RenewalBiz  extends BaseBiz<Renewal>{
	//查
	List<Renewal> selRenewalByAdmin(String userName);
	
	
	Object insertRenewal(String uuid,String userUuid,String userName,String email,String serviceUuid,
			String resourceUuid,String resourceName,Date startTime,Date endTime,boolean isdue,
			String tenantId,Integer tenancyDay,boolean deleted,String description,String dueAction);
	
	Object updateRenewal(String uuid,String userUuid,String userName,String email,String serviceUuid,
			String resourceUuid,String resourceName,Date startTime,Date endTime,boolean isdue,
			String tenantId,Integer tenancyDay,boolean deleted,String description,String dueAction);
	
	
	ResultType toReanewal(String time,String id,String ywlb);
	public String addRenewal(RenewalBiz renewalBiz,UserBiz userBiz,CloudosClient cloudosClient,String serUuid,String time,String projectid,String resUuid,
			String resName);

}

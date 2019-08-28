package com.h3c.iclouds.biz.impl;

import java.util.Date;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.h3c.iclouds.auth.CacheSingleton;
import com.h3c.iclouds.base.BaseBizImpl;
import com.h3c.iclouds.base.BaseRest;
import com.h3c.iclouds.biz.OpeLogBiz;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.po.OpeLog;
import com.h3c.iclouds.utils.StrUtils;

@Service("opeLogBiz")
public class OpeLogBizImpl extends BaseBizImpl<OpeLog> implements OpeLogBiz {
	
	@Override
	public void save(String uuid, String logTypeId, String params, String result, Date startTime) {
		OpeLog entity = new OpeLog();
		try {
			if(null != result) {
				JSONObject obj = JSONObject.parseObject(result);
				if(obj.containsKey(ConfigProperty.RESULT))
					entity.setResult(StrUtils.tranString(obj.get(ConfigProperty.RESULT)));
			}
		} catch (Exception e) {
			
		}
		entity.setId(StrUtils.checkParam(uuid) ? uuid : StrUtils.getUUID());
		entity.setLogTypeId(logTypeId);
		StringBuffer buffer = new StringBuffer("serverIP: ")
		.append(CacheSingleton.getInstance().getIpAddr())
		.append(" / ")
		.append(params);
		entity.setOpeParams(buffer.toString());
		entity.setRemark(result == null ? "" : result);
		entity.setOpeEndTime(new Date());
		entity.setOpeStartTime(startTime);
		if(this.getSessionBean() == null) {
			entity.setOpeUserId(ConfigProperty.SYSTEM_FLAG);
			entity.setOpeLoginName(ConfigProperty.SYSTEM_FLAG);
			entity.setOpeUserName(ConfigProperty.SYSTEM_FLAG);
		} else {
			entity.setOpeUserId(this.getLoginUser());
			entity.setOpeLoginName(this.getSessionBean().getLoginName());
			entity.setOpeUserName(this.getSessionBean().getUserName());
		}
		entity.setOpeIp(BaseRest.getIpAddress(this.request));
		entity.setOpeUrl(logTypeId);
		if(entity.getOpeParams().length() >499) {
			entity.setOpeParams(entity.getOpeParams().substring(0, 499));
		}
		if(entity.getLogTypeId().length() >99) {
			entity.setLogTypeId(entity.getLogTypeId().substring(0, 99));
		}
		// 记录到日志中
		this.info(JSONObject.toJSONString(entity));
		this.add(entity);
	}

}

package com.h3c.iclouds.biz.impl;

import com.h3c.iclouds.base.BaseBizImpl;
import com.h3c.iclouds.biz.AppItemsBiz;
import com.h3c.iclouds.biz.AppRelationsBiz;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.exception.MessageException;
import com.h3c.iclouds.po.AppItems;
import com.h3c.iclouds.po.AppRelations;
import com.h3c.iclouds.po.bean.ApplicationBean;
import com.h3c.iclouds.utils.InvokeSetForm;
import com.h3c.iclouds.utils.StrUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service("appRelationsBiz")
public class AppRelationsBizImpl extends BaseBizImpl<AppRelations> implements AppRelationsBiz {

	@Resource
	private AppItemsBiz appItemsBiz;

	@Override
	public void save(ApplicationBean bean, String viewId) {
		//查询当前视图下的同级设备
		Map<String, Object> queryMap = StrUtils.createMap("itemtype", bean.getType());
		queryMap.put("viewId", viewId);
		List<AppItems> appItemss = appItemsBiz.listByClass(AppItems.class, queryMap);
		
		String name;
		String type = bean.getType();
		if ("3".equals(type)) {
			name = StrUtils.tranString(bean.getData().get("dbName"));
		} else if (StrUtils.equals(type, "12", "13")) {
			name = StrUtils.tranString(bean.getData().get("cname"));
		} else {
			name = StrUtils.tranString(bean.getData().get("name"));
		}
		
		String itemId = bean.getId();
		List<String> pids = bean.getPid();
		// 保存关系
		AppItems item = new AppItems();
		item.setId(itemId);
		item.setUuid(itemId);
		item.setItemtype(type);
		item.setOption(bean.getOption());
		item.setName(name);
		appItemsBiz.add(item);
		
		//保存与上级的关系
		if (StrUtils.checkParam(pids)) {
			Set<String> ids = new HashSet<>();//使用set防止保存重复
			for (String pid : pids) {
				if (!StrUtils.checkCollection(appItemss)) {//如果没有同级设备则需要将新关联的父级底下的子集的关联关系删除
					List<AppRelations> relationss = this.listByClass(AppRelations.class, StrUtils.createMap
							("previous", pid));
					for (AppRelations relations : relationss) {
						ids.add(relations.getAppId());
						this.delete(relations);
					}
				}
				//保存自己与父级的关联关系
				AppRelations relations = new AppRelations();
				relations.setAppId(itemId);
				relations.setViewId(viewId);
				relations.setPrevious(pid);
				relations.setSequence(getSequence(bean.getType()));
				this.add(relations);
				this.info("当前子id"+itemId+"父级的id为"+pid);
			}
			for (String id : ids) {//将新关联的父级底下的子集关联到自己底下
				AppRelations relations = new AppRelations();
				relations.setAppId(id);
				relations.setViewId(viewId);
				relations.setPrevious(itemId);
				relations.setSequence(getSequence(bean.getType()));
				this.add(relations);
			}
			
		} else {
			throw new MessageException(ResultType.app_pid_not_point);
		}
		
		//查询其中一个同级设备的子集并与其子集建立上下级关系（多个同级设备的子集都一样）
		if (StrUtils.checkCollection(appItemss)) {
			List<AppRelations> appRelationss = this.listByClass(AppRelations.class, StrUtils.createMap("previous",
					appItemss.get(0).getId()));
			for (AppRelations appRelations : appRelationss) {
				AppRelations relations = new AppRelations();
				InvokeSetForm.copyFormProperties(appRelations, relations);
				relations.setPrevious(itemId);
				this.add(relations);
			}
		}
	}
	
	private Integer getSequence(String type) {
		Integer sequence=null;
		switch (type) {
		case "0":
			sequence=0;
			break;
		case "12":
			sequence=1;
			break;
		case "2":
			sequence=2;
			break;
		case "13":
			sequence=1;
			break;
		case "3":
			sequence=3;
			break;
		default:
			break;
		}
		return sequence;
	}

}

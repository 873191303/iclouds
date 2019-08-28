package com.h3c.iclouds.biz.impl;

import com.h3c.iclouds.base.BaseBizImpl;
import com.h3c.iclouds.biz.Specs2KeyBiz;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.dao.Specs2KeyDao;
import com.h3c.iclouds.dao.Specs2KeyValueDao;
import com.h3c.iclouds.po.business.Specs2Key;
import com.h3c.iclouds.po.business.Specs2KeyValue;
import com.h3c.iclouds.utils.StrUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Service("specs2KeyBiz")
public class Specs2KeyBizImpl extends BaseBizImpl<Specs2Key> implements Specs2KeyBiz {

	@Resource
	private Specs2KeyDao specs2KeyDao;

	@Resource
	private Specs2KeyValueDao specs2KeyValueDao;

	@Override
	public PageModel<Specs2Key> findForPage(PageEntity entity) {
		return specs2KeyDao.findForPage(entity);
	}

	public boolean checkUnit(String name, String unit){
		boolean result = true;
		if ("CPU".equals(name) || "cpu".equals(name) || "vcpus".equals(name)){
			if (!"核".equals(unit)){
				result = false;
			}
		}
		if ("ram".equals(name) || "systemDisk".equals(name) || "dataDisk".equals(name) || "diskBrain".equals(name)){
			if (!"GB".equals(unit)){
				result = false;
			}
		}
		if ("bandWidth".equals(name) || "loadBalance".equals(name) || "internetWidth".equals(name) ){
			if (!"MB".equals(unit)){
				result = false;
			}
		}
		return result;
	}

	public void delete(String id){
		Map<String, Object> deleteMap = new HashMap<>();
		deleteMap.put("key", id);
		specs2KeyValueDao.delete(deleteMap, Specs2KeyValue.class);
		specs2KeyDao.deleteById(Specs2Key.class, id);
	}
	
	public Specs2Key getKey(String keyName, String key, String classId, String createdUserId, String unit, String dataType, String validate) {
		Map<String, Object> queryMap = new HashMap<>();
		queryMap.put("key", key);
		queryMap.put("classId", classId);
		Specs2Key specs2Key = specs2KeyDao.singleByClass(Specs2Key.class, queryMap);
		if (!StrUtils.checkParam(specs2Key)) {
			specs2Key = new Specs2Key();
			specs2Key.setClassId(classId);
			specs2Key.setKeyName(keyName);
			specs2Key.setKey(key);
			specs2Key.createdUser(createdUserId);
			specs2Key.setUnit(unit);
			specs2Key.setDataType(dataType);
			specs2Key.setValidate(validate);
			specs2KeyDao.add(specs2Key);
		}
		return specs2Key;
	}
}

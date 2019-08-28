package com.h3c.iclouds.biz.impl;

import com.alibaba.fastjson.JSONObject;
import com.h3c.iclouds.auth.CacheSingleton;
import com.h3c.iclouds.base.BaseBizImpl;
import com.h3c.iclouds.biz.ListPriceBiz;
import com.h3c.iclouds.biz.Specs2KeyBiz;
import com.h3c.iclouds.biz.Specs2KeyValueBiz;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.dao.ListPriceDao;
import com.h3c.iclouds.po.NovaFlavor;
import com.h3c.iclouds.po.VolumeFlavor;
import com.h3c.iclouds.po.business.ListPrice;
import com.h3c.iclouds.po.business.Specs2Key;
import com.h3c.iclouds.po.business.Specs2KeyValue;
import com.h3c.iclouds.utils.StrUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Service("listPriceBiz")
public class ListPriceBizImpl extends BaseBizImpl<ListPrice> implements ListPriceBiz {

	@Resource
	private ListPriceDao listPriceDao;
	
	@Resource
	private ListPriceBiz listPriceBiz;
	
	@Resource
	private Specs2KeyBiz specs2KeyBiz;
	
	@Resource
	private Specs2KeyValueBiz specs2KeyValueBiz;
	
	@Override
	public PageModel<ListPrice> findForPage(PageEntity entity) {
		return listPriceDao.findForPage(entity);
	}
	
	public ListPrice saveByNovaFlavor(NovaFlavor flavor, String createdUserId, String azoneId) {
		String vmClassId = CacheSingleton.getInstance().getVmClassId();
		int cpu = flavor.getVcpus();
		int ram = flavor.getRam()/1024;
		int disk = flavor.getDisk();
		Map<String, String> keyValueMap = getSpecByNovaFlavor(flavor, createdUserId);
		Map<String, Object> queryMap = new HashMap<>();
		queryMap.put("classId", vmClassId);
		queryMap.put("flavorId", flavor.getId());
		queryMap.put("azoneId", azoneId);
		ListPrice listPrice = listPriceBiz.singleByClass(ListPrice.class, queryMap);
		if (!StrUtils.checkParam(listPrice)) {
			listPrice = new ListPrice();
			listPrice.setName(keyValueMap.get("name"));
			listPrice.setSpec(keyValueMap.get("spec"));
			listPrice.setAzoneId(azoneId);
			listPrice.setClassId(vmClassId);
			double price = 1.0 * cpu + 1.0 * ram + 0.3 * disk;
			listPrice.setListPrice(price);
			listPrice.setFlavorId(flavor.getId());
			listPrice.createdUser(createdUserId);
			listPrice.setDiscount(1.0);
			listPrice.setSpecPrice(price);
			listPrice.setMeasureType(ConfigProperty.BILL_TYPE_MONTH);
			listPrice.setUnit(ConfigProperty.BILL_TYPE_MONTH);
			listPriceBiz.add(listPrice);
		}
		return listPrice;
	}
	
	public Map<String, String> getSpecByNovaFlavor(NovaFlavor flavor, String createdUserId) {
		Map<String, Object> specMap = new HashMap<>();
		
		String vmClassId = CacheSingleton.getInstance().getVmClassId();
		
		int cpu = flavor.getVcpus();
		Specs2Key cpuSpecs2Key = specs2KeyBiz.getKey("CPU","vcpu", vmClassId, createdUserId, "核", ConfigProperty
				.ITEM_DATATYPE1_INT, null);
		Specs2KeyValue cpuSpecs2Keyvalue = specs2KeyValueBiz.getValueId(cpuSpecs2Key.getId(), StrUtils.tranString(cpu),
				createdUserId);
		specMap.put(cpuSpecs2Key.getId(), cpuSpecs2Keyvalue.getId());
		
		int ram = flavor.getRam()/1024;
		Specs2Key ramSpecs2Key = specs2KeyBiz.getKey("内存","ram", vmClassId, createdUserId, "GB", ConfigProperty.ITEM_DATATYPE1_INT, null);
		Specs2KeyValue ramSpecs2Keyvalue = specs2KeyValueBiz.getValueId(ramSpecs2Key.getId(), StrUtils.tranString(ram), createdUserId);
		specMap.put(ramSpecs2Key.getId(), ramSpecs2Keyvalue.getId());
		
		int disk = flavor.getDisk();
		Specs2Key diskSpecs2Key = specs2KeyBiz.getKey("系统盘","systemDisk", vmClassId, createdUserId, "GB", ConfigProperty.ITEM_DATATYPE1_INT, null);
		Specs2KeyValue diskSpecs2Keyvalue = specs2KeyValueBiz.getValueId(diskSpecs2Key.getId(), StrUtils.tranString(disk), createdUserId);
		specMap.put(diskSpecs2Key.getId(), diskSpecs2Keyvalue.getId());
		
//		String diskType = flavor.getDiskType();
//		Specs2Key typeSpecs2Key = specs2KeyBiz.getKey("存储类型","diskType", vmClassId, createdUserId, null,
//				ConfigProperty.ITEM_DATATYPE3_STRING, "contain(1,0)");
//		Specs2KeyValue typeSpecs2Keyvalue = specs2KeyValueBiz.getValueId(typeSpecs2Key.getId(), StrUtils.tranString(diskType), createdUserId);
		
		String name = cpuSpecs2Key.getKey() + ":" + cpuSpecs2Keyvalue.getValue() + "|"
				+ ramSpecs2Key.getKey() + ":" + ramSpecs2Keyvalue.getValue() + "|"
				+ diskSpecs2Key.getKey() + ":" + diskSpecs2Keyvalue.getValue();
		Map<String, String> result = new HashMap<>();
		result.put("name", name);
		result.put("spec", JSONObject.toJSONString(specMap));
		return result;
	}
	
	public ListPrice saveByVolumeFlavor(VolumeFlavor flavor, String createdUserId, String azoneId) {
		String storageClassId = singleton.getStorageClassId();
		int brain = flavor.getSize();
		Map<String, Object> queryMap = StrUtils.createMap("classId", storageClassId);
		queryMap.put("azoneId", azoneId);
		Map<String, String> keyValueMap = getSpecByVolumeFlavor(flavor, createdUserId);
		queryMap.put("spec", keyValueMap.get("spec"));
		ListPrice listPrice = listPriceBiz.singleByClass(ListPrice.class, queryMap);
		if (!StrUtils.checkParam(listPrice)) {
			listPrice = new ListPrice();
			listPrice.setName(keyValueMap.get("name"));
			listPrice.setSpec(keyValueMap.get("spec"));
			listPrice.setAzoneId(azoneId);
			listPrice.setClassId(storageClassId);
			double price = "1".equals(flavor.getVolumeType()) ? brain/10 : (brain/10 + 5);
			listPrice.setListPrice(price);
			if (brain >= 100 && brain <= 2000) {
				listPrice.setStepPrice(10.0);
			}
			listPrice.createdUser(createdUserId);
			listPrice.setDiscount(1.0);
			listPrice.setSpecPrice(price);
			listPrice.setMeasureType(ConfigProperty.BILL_TYPE_MONTH);
			listPrice.setUnit(ConfigProperty.BILL_TYPE_MONTH);
			listPriceBiz.add(listPrice);
		}
		return listPrice;
	}
	
	public Map<String, String> getSpecByVolumeFlavor(VolumeFlavor flavor, String createdUserId) {
		Map<String, String> specMap = new HashMap<>();
		String storageClassId = singleton.getStorageClassId();
		
		String type = flavor.getVolumeType();
		Specs2Key typeSpecs2Key = specs2KeyBiz.getKey("硬盘类型","diskType", storageClassId, createdUserId, null,
				ConfigProperty.ITEM_DATATYPE3_STRING, "contains(1,2)");
		Specs2KeyValue typeSpecs2KeyValue = specs2KeyValueBiz.getValueId(typeSpecs2Key.getId(), type, createdUserId);
		specMap.put(typeSpecs2Key.getId(), typeSpecs2KeyValue.getId());
		
		int brain = flavor.getSize();
		Specs2Key brainSpecs2Key = specs2KeyBiz.getKey("容量","diskBrain", storageClassId, createdUserId, "GB",
				ConfigProperty.ITEM_DATATYPE1_INT, null);
		Specs2KeyValue brainSpecs2KeyValue;
		if (brain < 100 || brain > 2000) {
			brainSpecs2KeyValue = specs2KeyValueBiz.getValueId(brainSpecs2Key.getId(), brain + "", createdUserId);
		} else {
			brainSpecs2KeyValue = specs2KeyValueBiz.getValueId(brainSpecs2Key.getId(), 100, 2000, 100, createdUserId);
		}
		specMap.put(brainSpecs2Key.getId(), brainSpecs2KeyValue.getId());
		
		Map<String, String> result = new HashMap<>();
		result.put("name", typeSpecs2Key.getKey() + ":" + typeSpecs2KeyValue.getValue() + "|" + brainSpecs2Key
				.getKey() + ":" + ((brain < 100 || brain > 2000) ? brain + "" : "100-2000"));
		result.put("spec", JSONObject.toJSON(specMap).toString());
		return result;
	}

}

package com.h3c.iclouds.biz.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.h3c.iclouds.base.BaseBizImpl;
import com.h3c.iclouds.base.BaseDAO;
import com.h3c.iclouds.biz.RequestItemsBiz;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.po.NovaFlavor;
import com.h3c.iclouds.po.business.Prd2Templates;
import com.h3c.iclouds.po.business.PrdClass;
import com.h3c.iclouds.po.business.RequestItems;
import com.h3c.iclouds.po.business.RequestMaster;
import com.h3c.iclouds.utils.PatternCheckUtils;
import com.h3c.iclouds.utils.StrUtils;

@SuppressWarnings("unchecked")
@Service("requestItemsBiz")
public class RequestItemsBizImpl extends BaseBizImpl<RequestItems> implements RequestItemsBiz {
	
	@Resource(name = "baseDAO")
	private BaseDAO<Prd2Templates> prd2TemplatesDao;
	
	@Resource(name = "baseDAO")
	private BaseDAO<PrdClass> prdClassDao;
	
	@Resource(name = "baseDAO")
	private BaseDAO<NovaFlavor> novaFlavorDao;
	
	private boolean validate(String validate, Object value, String dataType) {
		String[] array = validate.split(";");
		JSONArray valueArray = null;
		if(Integer.valueOf(dataType) > 3) {
			valueArray = (JSONArray) value;
		} else {
			valueArray = new JSONArray();
			valueArray.add(value);
		}
		for(int i = 0; i < valueArray.size(); i++) {
			String value_ = valueArray.getString(i);
			for (String validate_ : array) {
				if(validate_.contains("contain")) {
					String contain = validate_.replace("contain(", "").replace(")", "");
					String[] contain_ = contain.split(",");
					boolean containResult = false;
					for (String str : contain_) {
						if(str.trim().equals(value_)) {
							containResult = true;
							break;
						}
					}
					if(!containResult) {
						return false;
					}
				} else if(validate_.contains("IP")) {
					if(!PatternCheckUtils.checkIp(value_)) {
						return false;
					}
				}
			}
		}
		return true;
	}
	
	/**
	 * 验证条目提交的json数据是否正常
	 * @param entity
	 * @param classMap
	 * @return
	 */
	private JSONObject check(RequestItems entity, Map<String, PrdClass> classMap,RequestMaster master) {
		PrdClass prdClass = classMap.get(entity.getClassId());
		if(prdClass == null) {
			prdClass = this.prdClassDao.findById(PrdClass.class, entity.getClassId());
			if(prdClass == null) {
				return null;
			}
			List<Prd2Templates> templates = prd2TemplatesDao.findByPropertyName(Prd2Templates.class, "classId", entity.getClassId());
			prdClass.setTemplates(templates);
			classMap.put(entity.getClassId(), prdClass);
		}
		
		JSONObject resultJson = new JSONObject();
		try {
			JSONObject itemJson = JSONObject.parseObject(entity.getAjson());
			if(prdClass != null) {
				if(master.getReqtype() == 1) {
					List<Prd2Templates> templates = prdClass.getTemplates();
					for (Prd2Templates template : templates) {
						String key = template.getKey();
						Object value = itemJson.get(key);
						this.info("key: " + key + "  --- value: " + StrUtils.tranString(value));
						if(ConfigProperty.YES.equals(template.getIsMust())) {
							if(!StrUtils.checkParam(value)) {
								return null;
							}
						} else {
							if(!StrUtils.checkParam(value)) {
								continue;
							}
						}
						if(value != null) {
							String dataType = template.getDataType();
							// 转换是否异常
							if(ConfigProperty.ITEM_DATATYPE1_INT.equals(dataType)) {
								value = Integer.valueOf(StrUtils.tranString(value));
							} else if(ConfigProperty.ITEM_DATATYPE2_FLOAT.equals(dataType)) {
								value = Double.valueOf(StrUtils.tranString(value));
							} else if(ConfigProperty.ITEM_DATATYPE3_STRING.equals(dataType)) {
								value = String.valueOf(value);
							} else if(ConfigProperty.ITEM_DATATYPE4_INT_ARRAY.equals(dataType)) {
								JSONArray array = (JSONArray) value;
								for (int i = 0; i < array.size(); i++) {
									Integer.valueOf(StrUtils.tranString(array.get(i)));
								}
							} else if(ConfigProperty.ITEM_DATATYPE5_FLOAT_ARRAY.equals(dataType)) {
								JSONArray array = (JSONArray) value;
								for (int i = 0; i < array.size(); i++) {
									Double.valueOf(StrUtils.tranString(array.get(i)));
								}
							} else if(ConfigProperty.ITEM_DATATYPE6_CHAR_ARRAY.equals(dataType)) {
								JSONArray array = (JSONArray) value;
								for (int i = 0; i < array.size(); i++) {
									StrUtils.tranString(array.get(i));
								}
							}
							if(template.getValidate() != null && !"".equals(template.getValidate())) {
								this.validate(template.getValidate(), value, dataType);
							}
						}
						resultJson.put(key, value);
					}
					 
				}else if(master.getReqtype() == 2) {
					resultJson = itemJson; 
				}
				entity.setAjson(resultJson.toJSONString());
				
			}
		} catch (Exception e) {
			this.exception(e, "Check item error, value:" + entity.getAjson());
			return null;
		}
		if(ConfigProperty.TEMPLATES_CLASS1_HOST.equals(entity.getClassId())&&master.getReqtype() == 1) {
			return checkHost(resultJson) ? resultJson : null;
		} else {
			return resultJson;
		}
		
	}
	
	/**
	 * 验证规格是否符合要求
	 * @param resultJson
	 * @return
	 */
	private boolean checkHost(JSONObject resultJson) {
		String flavorId = resultJson.getString("flavorId");
		//Flavor entity = flavorDao.findById(Flavor.class, flavorId);
		NovaFlavor entity = novaFlavorDao.findById(NovaFlavor.class, flavorId);
		if(ConfigProperty.YES.equals(resultJson.getString("needPublicIp"))) {
			String publicIp = StrUtils.tranString(resultJson.get("publicIp"));
			//JSONArray publicIp = resultJson.getJSONArray("publicIp");
			if(publicIp == null || publicIp.isEmpty()) {
				return false;
			}
		} else {
			resultJson.remove("publicIp");
		}
		if(entity != null) {
			int cpu = resultJson.getInteger("vcpu");
			int ram = resultJson.getInteger("ram");
			int systemDisk = resultJson.getInteger("systemDisk");
			if(entity.getVcpus().intValue() == cpu &&
					entity.getRam().intValue() == ram &&
					entity.getDisk().intValue() == systemDisk) {
				return true;
			}
			this.info("规格配置错误，提交规格为[cpu:" + cpu + "][ram:" + ram + "][systemDisk:" + systemDisk + "],"
					+ "正确规格为[cpu:" + entity.getVcpus().intValue() + "][ram:" + entity.getRam().intValue() + "][systemDisk:" + entity.getDisk().intValue() + "]");
		} else {
			this.info("未查询对应规格");
		}
		return false;
	}
	
	@Override
	public ResultType check(Set<RequestItems> set, RequestMaster master, RequestMaster oldEntity) {
		Set<RequestItems> oldItemSet = null;
		if(oldEntity != null) {
			oldItemSet = oldEntity.getItems();
		} else {
			oldItemSet = new HashSet<RequestItems>();
		}
		Map<String, Object> itemMap = new HashMap<String, Object>();
		for (RequestItems oldItem : oldItemSet) {
			itemMap.put(oldItem.getId(), oldItem);
		}
		
		Map<String, PrdClass> classMap = new HashMap<String, PrdClass>();
		for (RequestItems entity : set) {
			String reqType = entity.getReqType();
			String oid = entity.getOitemId();
			if(!ConfigProperty.ITEM_REQTYPE1_INSERT.equals(reqType)) {	// 非新增
				if(StrUtils.checkParam(oid)) {
					RequestItems tempItem = this.findById(RequestItems.class, oid);
					if(tempItem == null) {
						return ResultType.old_item_not_exist;
					}
					// 原条目不属于原申请单
					if(!tempItem.getReqId().equals(oldEntity.getId())) {
						return ResultType.item_parameter_error;
					}
					// 设置类型为原类型
					entity.setClassId(tempItem.getClassId());
					// 操作类型为删除或者不变
					if(!ConfigProperty.ITEM_REQTYPE2_UPDATE.equals(reqType)) {
						entity.setAjson(tempItem.getAjson());
					}
				} else {
					return ResultType.item_parameter_error;
				}
				itemMap.remove(oid);
			}
			
			// 新创建或者修改的部分需要做验证
			if(ConfigProperty.ITEM_REQTYPE1_INSERT.equals(reqType) || ConfigProperty.ITEM_REQTYPE2_UPDATE.equals(reqType)) {
				JSONObject itemObj = this.check(entity, classMap,master);
				if(itemObj == null || itemObj.isEmpty()) {
					return ResultType.parameter_error;
				}
				entity.setAjson(itemObj.toJSONString());
			}
			entity.setMaster(master);
			entity.createdUser(this.getSessionBean().getUserId());
		}
		if(itemMap.isEmpty()) {	// 确认所有非删除条目被已被覆盖
			// 验证条目格式是否正确
			return ResultType.success;
		}
		return ResultType.parameter_error;
	}
	
}

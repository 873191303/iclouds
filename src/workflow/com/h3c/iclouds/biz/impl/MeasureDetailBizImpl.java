package com.h3c.iclouds.biz.impl;

import com.alibaba.fastjson.JSONObject;
import com.h3c.iclouds.base.BaseBizImpl;
import com.h3c.iclouds.base.BaseDAO;
import com.h3c.iclouds.biz.BillBiz;
import com.h3c.iclouds.biz.ListPriceBiz;
import com.h3c.iclouds.biz.MeasureDetailBiz;
import com.h3c.iclouds.biz.NovaVmBiz;
import com.h3c.iclouds.biz.Specs2KeyBiz;
import com.h3c.iclouds.biz.Specs2KeyValueBiz;
import com.h3c.iclouds.biz.VolumeBiz;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.dao.MeasureDetailDao;
import com.h3c.iclouds.dao.NovaFlavorDao;
import com.h3c.iclouds.dao.ProjectDao;
import com.h3c.iclouds.po.NovaFlavor;
import com.h3c.iclouds.po.NovaVm;
import com.h3c.iclouds.po.Project;
import com.h3c.iclouds.po.Volume;
import com.h3c.iclouds.po.business.EventType;
import com.h3c.iclouds.po.business.FlavorParam;
import com.h3c.iclouds.po.business.Instance;
import com.h3c.iclouds.po.business.ListPrice;
import com.h3c.iclouds.po.business.ListPrice2Imag;
import com.h3c.iclouds.po.business.MeasureDetail;
import com.h3c.iclouds.po.business.Specs2Key;
import com.h3c.iclouds.po.business.Specs2KeyValue;
import com.h3c.iclouds.utils.StrUtils;
import com.h3c.iclouds.utils.ThreadContext;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("measureDetailBiz")
public class MeasureDetailBizImpl extends BaseBizImpl<MeasureDetail> implements MeasureDetailBiz {

	@Resource
	private MeasureDetailDao measureDetailDao;

	@Resource
	private ListPriceBiz listPriceBiz;

	@Resource(name = "baseDAO")
	private BaseDAO<ListPrice2Imag> listPrice2ImagDao;

	@Resource(name = "baseDAO")
	private BaseDAO<Instance> instanceDao;

	@Resource(name = "baseDAO")
	private BaseDAO<EventType> eventTypeDao;
	
	@Resource
	private Specs2KeyBiz specs2KeyBiz;

	@Resource
	private Specs2KeyValueBiz specs2KeyValueBiz;
	
	@Resource
	private NovaVmBiz novaVmBiz;
	
	@Resource
	private VolumeBiz volumeBiz;
	
	@Resource
	private BillBiz billBiz;
	
	@Resource
	private NovaFlavorDao flavorDao;

	@Resource
	private ProjectDao projectDao;
	
	@Override
	public PageModel<MeasureDetail> findForPage(PageEntity entity) {
		return measureDetailDao.findForPage(entity);
	}

	/**
	 * 创建账单信息(创建产品资源生成账单)
	 */
	@Override
	public ResultType save(FlavorParam flavorParam, String type) {
		ResultType rs = verify(flavorParam);
		if (!ResultType.success.equals(rs)) {
			return rs;
		}
		String classId = flavorParam.getClassId();
		ListPrice listPrice;
		ListPrice2Imag listPrice2Imag;
		String flavor = "";
		if (classId.equals(singleton.getVmClassId())) {
			Map<String, Object> queryMap = new HashMap<>();
			queryMap.put("azoneId", flavorParam.getAzoneId());
			queryMap.put("flavorId", flavorParam.getFlavorId());
			queryMap.put("classId", flavorParam.getClassId());
			listPrice = listPriceBiz.singleByClass(ListPrice.class, queryMap);
			if (null == listPrice) {
				NovaFlavor novaFlavor = flavorDao.findById(NovaFlavor.class, flavorParam.getFlavorId());
				listPrice = listPriceBiz.saveByNovaFlavor(novaFlavor, flavorParam.getCreatedBy(), flavorParam
						.getAzoneId());
			}
			flavor = listPrice.getName();
		} else {
			listPrice = listPriceBiz.findById(ListPrice.class, flavorParam.getSpecId());
			flavor = this.getFlavor(listPrice.getId(), flavorParam.getValue());
		}
		listPrice2Imag = this.getListPrice2Img(listPrice, flavorParam);
		String instanceId = this.getInstance(flavorParam, flavor).getId();
		String eventTypeId = this.getEventType(classId, type).getId();//获取事件类型(创建或修改)
		String id = this.saveMeasure(listPrice2Imag, instanceId, eventTypeId, flavorParam);
		// TODO: 2017/9/6 将创建的对象id保存，用于即时计算
		ThreadContext.set("measureId", id);
		this.warn("Create Measure, flavor:" + JSONObject.toJSONString(flavor) + ", resourceId:" + flavorParam.getResourceId());
		return ResultType.success;
	}

	/**
	 * 修改账单信息-停止上一个账单计费，创建新账单(修改产品资源规格)
	 */
	@Override
	public ResultType update(FlavorParam newFlavor) {
		stop(newFlavor.getResourceId(), newFlavor.getCreatedBy(), false);
		return save(newFlavor, "变更");
	}

	/**
	 * 停止账单计费(删除和停用资源时)
	 */
	@Override
	public void stop(String resourceId, String createdBy, boolean delete) {
		this.stop(resourceId, createdBy, delete, null);
	}

	@Override
	public void stop(String resourceId, String createdBy, boolean delete, Long num) {
		if (null == resourceId) {
			return;
		}
		Map<String, Object> query = new HashMap<>();
		query.put("instance", resourceId);
		Instance instance = instanceDao.singleByClass(Instance.class, query);
		if (StrUtils.checkParam(instance)){
			String instanceId = instance.getId();
			stopMeasure(instanceId, createdBy, delete, num);//停止账单计费
		}
		this.warn("Stop Charging, resourceId:" + resourceId);
	}

	/**
	 * 获取目录定价规格数据
	 * @param listPrice
	 * @return
	 */
	private ListPrice2Imag getListPrice2Img(ListPrice listPrice, FlavorParam flavorParam){
		ListPrice2Imag listPrice2Imag = listPrice2ImagDao.singleByClass(ListPrice2Imag.class, StrUtils.createMap
				("specId", listPrice.getId()));
		if (!StrUtils.checkParam(listPrice2Imag)){//如果定价规格镜像表里面不存在这条定价规格,则新增
			listPrice2Imag = new ListPrice2Imag(listPrice);
			listPrice2Imag.setMinValue(flavorParam.getMinValue());
			listPrice2Imag.setStep(flavorParam.getStep());
			listPrice2Imag.createdUser(flavorParam.getCreatedBy());
			listPrice2ImagDao.add(listPrice2Imag);
		}
		return listPrice2Imag;
	}

	/**
	 * 获取事件类型
	 * @param classId
	 * @param name
	 * @return
	 */
	private EventType getEventType(String classId, String name){
		Map<String, String> queryMap = new HashMap<>();
		queryMap.put("classId", classId);
		queryMap.put("name", name);
		EventType eventType = null;
		List<EventType> eventTypes = eventTypeDao.findByMap(EventType.class, queryMap);
		if (StrUtils.checkParam(eventTypes)){//如果当前产品的当前时间类型已存在则直接返回
			eventType = eventTypes.get(0);
		}else {//不存在则新建一条事件类型数据
			eventType = new EventType();
			eventType.setClassId(classId);
			eventType.setName(name);
			eventTypeDao.add(eventType);
		}
		return eventType;
	}

	/**
	 * 停止账单计费
	 * @param instanceId
	 * @param delete 是否删除标志 true-删除操作 删除时直接生成扣费流水账单
	 */
	private void stopMeasure(String instanceId, String createdBy, boolean delete, Long num){
		Map<String, Object> queryMap = new HashMap<>();
		queryMap.put("instanceId", instanceId);
		queryMap.put("isEffective", true);
		queryMap.put("flag", true);
		List<MeasureDetail> measureDetails = measureDetailDao.listByClass(MeasureDetail.class, queryMap);
		if (StrUtils.checkParam(measureDetails)){
			for (MeasureDetail measureDetail : measureDetails) {
				measureDetail.setEndDate(new Date());
				measureDetail.setFlag(false);
				measureDetail.setIsEffective(false);
				measureDetail.updatedUser(createdBy);
				measureDetailDao.update(measureDetail);
				if (delete) {
					billBiz.create(measureDetail, createdBy, ConfigProperty.BILL_TYPE_MANUAL, num);
				}
			}
		}
	}

	/**
	 * 保存账单
	 * @param instanceId
	 * @param eventTypeId
	 * @param flavorParam
	 */
	private String saveMeasure(ListPrice2Imag listPrice2Imag, String instanceId, String eventTypeId, FlavorParam flavorParam){
		MeasureDetail measureDetail = new MeasureDetail();
		measureDetail.setSpecId(listPrice2Imag.getId());
		measureDetail.setEventTypeId(eventTypeId);
		measureDetail.setInstanceId(instanceId);
		measureDetail.setDescription(listPrice2Imag.getName());
		measureDetail.setBegDate(new Date());
		measureDetail.createdUser(flavorParam.getCreatedBy());
		measureDetail.setUserId(flavorParam.getUserId());
		measureDetail.setTenantId(flavorParam.getTenantId());
		measureDetail.setNum(null == flavorParam.getValue() ? 1 : flavorParam.getValue());
		return measureDetailDao.add(measureDetail);
	}
	
	/**
	 * 校验参数
	 * @param flavorParam
	 * @return
	 */
	private ResultType verify(FlavorParam flavorParam) {
		if (!verifyFlavor(flavorParam)){//验证参数
			return ResultType.flavor_param_error;
		}
		ResultType rs = checkSpec(flavorParam);
		return rs;
	}
	
	/**
	 * 验证资源规格相关信息
	 * @param flavorParam
	 * @return
	 */
	private boolean verifyFlavor(FlavorParam flavorParam){
		String classId = flavorParam.getClassId();//产品id
		if (!StrUtils.checkParam(classId, flavorParam.getUserId(), flavorParam.getTenantId(), flavorParam.getResourceId())) {
			return false;
		}
		if (classId.equals(singleton.getVmClassId())){//资源为云主机时,必须有规格id和可用域id
			if (!StrUtils.checkParam(flavorParam.getAzoneId()) || !StrUtils.checkParam(flavorParam.getFlavorId())){
				return false;
			}
			flavorParam.setSpecId(null);
		} else {
			if (classId.equals(singleton.getStorageClassId())) {//资源为云硬盘时,必须有可用域id
				if (!StrUtils.checkParam(flavorParam.getAzoneId())) {
					return false;
				}
			}
			//资源为其它时,必须有定价目录id
			if (!StrUtils.checkParam(flavorParam.getSpecId())) {
				return false;
			}
			flavorParam.setFlavorId(null);
		}
		return true;
	}
	
	/**
	 * 检查产品规格是否正确
	 * @param flavorParam
	 * @return
	 */
	private ResultType checkSpec(FlavorParam flavorParam) {
		String specId = flavorParam.getSpecId();
		String classId = flavorParam.getClassId();
		Integer value = flavorParam.getValue();
		if (!StrUtils.checkParam(specId)) {
			return ResultType.success;
		}
		ListPrice listPrice = listPriceBiz.findById(ListPrice.class, specId);
		if (!StrUtils.checkParam(listPrice)) {
			return ResultType.listprice_not_exist;
		}
		String spec = listPrice.getSpec();
		Map<String, Object> keyValueMap = JSONObject.parseObject(spec);
		for (Map.Entry<String, Object> keyValue : keyValueMap.entrySet()) {
			String keyId = keyValue.getKey();
			Specs2Key specs2Key = specs2KeyBiz.findById(Specs2Key.class, keyId);
			if (!classId.equals(specs2Key.getClassId())) {
				return ResultType.specs2key_not_belong_to_class;
			}
			String valueId = StrUtils.tranString(keyValue.getValue());
			Specs2KeyValue specs2KeyValue = specs2KeyValueBiz.findById(Specs2KeyValue.class, valueId);
			if ("1".equals(specs2KeyValue.getValueType())) {
				if (!classId.equals(ConfigProperty.NETWORK_FLOW_CLASSID) && !StrUtils.checkParam(value)) {
					return ResultType.value_not_null;
				}
				if (StrUtils.checkParam(value) && (value < specs2KeyValue.getMinValue() || value > specs2KeyValue.getMaxValue())) {
					return ResultType.value_not_in_range;
				}
				flavorParam.setMinValue(specs2KeyValue.getMinValue());
				flavorParam.setStep(specs2KeyValue.getStep());
			}
		}
		return ResultType.success;
	}
	
	/**
	 * 获取产品资源实例
	 * @param flavor
	 * @param flavorParam
	 * @return
	 */
	private Instance getInstance(FlavorParam flavorParam, String flavor) {
		String classId = flavorParam.getClassId();
		String resourceId = flavorParam.getResourceId();
		String name = "";
		if (singleton.getVmClassId().equals(classId)) {
			NovaVm novaVm = novaVmBiz.singleByClass(NovaVm.class, StrUtils.createMap("uuid", resourceId));
			name = novaVm.getHostName();
		} else if (singleton.getStorageClassId().equals(classId)) {
			Volume volume = volumeBiz.findById(Volume.class, resourceId);
			name = volume.getName();
		} else if(ConfigProperty.NETWORK_FLOW_CLASSID.equals(classId)) {
			Project project = projectDao.findById(Project.class, flavorParam.getResourceId());
			name = project.getName();
		}
		Instance instance = new Instance();
		instance.setClassId(classId);
		instance.setTenantId(flavorParam.getTenantId());
		instance.setUserId(flavorParam.getUserId());
		instance.setInstance(resourceId);
		instance.setBegDate(new Date());
		instance.createdUser(flavorParam.getCreatedBy());
		instance.setFlavor(flavor);
		instance.setName(name);
		String id = instanceDao.add(instance);
		return instance;
	}
	
	/**
	 * 获取每个账单的规格名称
	 * @param specId
	 * @param value
	 * @return
	 */
	private String getFlavor(String specId, Integer value) {
		StringBuffer flavor = new StringBuffer();
		ListPrice listPrice = listPriceBiz.findById(ListPrice.class, specId);
		String spec = listPrice.getSpec();
		Map<String, Object> keyValueMap = JSONObject.parseObject(spec);
		for (Map.Entry<String, Object> keyValue : keyValueMap.entrySet()) {
			String keyId = keyValue.getKey();
			Specs2Key specs2Key = specs2KeyBiz.findById(Specs2Key.class, keyId);
			flavor.append(specs2Key.getKey() + ":");
			String valueId = StrUtils.tranString(keyValue.getValue());
			Specs2KeyValue specs2KeyValue = specs2KeyValueBiz.findById(Specs2KeyValue.class, valueId);
			if ("1".equals(specs2KeyValue.getValueType())) {
				flavor.append(value);
			} else {
				flavor.append(specs2KeyValue.getValue());
			}
			flavor.append("|");
		}
		return flavor.substring(0, flavor.length() - 1);
	}
	
}

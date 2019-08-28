package com.h3c.iclouds.biz.impl;

import com.h3c.iclouds.base.BaseBizImpl;
import com.h3c.iclouds.biz.Specs2KeyValueBiz;
import com.h3c.iclouds.common.ConfigProperty;
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
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Service("specs2KeyValueBiz")
public class Specs2KeyValueBizImpl extends BaseBizImpl<Specs2KeyValue> implements Specs2KeyValueBiz {

	@Resource
	private Specs2KeyValueDao specs2KeyValueDao;
	
	@Resource
	private Specs2KeyDao specs2KeyDao;

	@Override
	public PageModel<Specs2KeyValue> findForPage(PageEntity entity) {
		return specs2KeyValueDao.findForPage(entity);
	}
	/**
	 * 验证规格属性值类型
	 * 
	 * @param specs2Key
	 *            规格
	 * @param entity
	 * @return
	 */
	@Override
	public boolean checkValueType(Specs2Key specs2Key, Specs2KeyValue entity) {
		String dataType = specs2Key.getDataType();
		String value = entity.getValue();
		if (ConfigProperty.ITEM_DATATYPE1_INT.equals(dataType)) {
			return StrUtils.isInteger(entity.getValue());

		} else if (ConfigProperty.ITEM_DATATYPE2_FLOAT.equals(dataType)) {
			if(value.indexOf(".")!=value.lastIndexOf("."))
				return false;
			Pattern p = Pattern.compile("^[-\\+]?[.\\d]*$");
			return p.matcher(value).matches();
		}
//		else if (ConfigProperty.ITEM_DATATYPE4_INT_ARRAY.equals(dataType)) {
//			if (value.startsWith("[") && value.endsWith("]")) {
//				value = value.substring(1, value.length() - 1);
//				if (value.startsWith(",") || value.endsWith(","))
//					return false;
//				String[] sarr = value.split(",");
//				for (String s : sarr) {
//					if (!StrUtils.isInteger(s))
//						return false;
//				}
//			}
//
//		} else if (ConfigProperty.ITEM_DATATYPE5_FLOAT_ARRAY.equals(dataType)) {
//			if (value.startsWith("[") && value.endsWith("]")) {
//				value = value.substring(1, value.length() - 1);
//				if (value.startsWith(",") || value.endsWith(","))
//					return false;
//				String[] sarr = value.split(",");
//				Pattern p = Pattern.compile("^[-\\+]?[.\\d]*$");
//				for (String s : sarr) {
//					if(s.indexOf(".")!=s.lastIndexOf("."))
//						return false;
//					if (!p.matcher(s).matches())
//						return false;
//				}
//			}
//
//		} else if (ConfigProperty.ITEM_DATATYPE6_CHAR_ARRAY.equals(dataType)) {
//			if (value.startsWith("[") && value.endsWith("]")) {
//				value = value.substring(1, value.length() - 1);
//				if (value.startsWith(",") || value.endsWith(","))
//					return false;
//				String[] sarr = value.split(",");
//				for (String s : sarr) {
//					if (s.toCharArray().length > 1)
//						return false;
//				}
//			}
//		}

		return true;
	}

	/**
	 * 根据校验规则验证
	 * 
	 * @param specs2Key
	 *            校验规则
	 * @param entity
	 *            规格属性值
	 * @return
	 */
	@Override
	public boolean validate(Specs2Key specs2Key, Specs2KeyValue entity) {
		String validate = specs2Key.getValidate();
		if (validate != null) {
			if (validate.toLowerCase().indexOf("contain") > -1) {
				validate = validate.substring(validate.indexOf("(") + 1, validate.indexOf(")"));
				String[] sarr = validate.split(",");
				for (String s : sarr) {
					if (s.equals(entity.getValue()))
						return true;
				}
				return false;
			} else if (validate.toLowerCase().indexOf("ip") > -1) {
				Pattern pattern = Pattern.compile(
						"([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}");
				return pattern.matcher(entity.getValue()).matches();
			}
		}
			
		return true;
	}
	
	public boolean checkValue(String key, int value) {
		List<Specs2KeyValue> specs2KeyValues = specs2KeyValueDao.findByPropertyName(Specs2KeyValue.class, "key", key);
		if (StrUtils.checkCollection(specs2KeyValues)) {
			for (Specs2KeyValue specs2KeyValue : specs2KeyValues) {
				if ("1".equals(specs2KeyValue.getValueType())) {
					int minValue = specs2KeyValue.getMinValue();
					int maxValue = specs2KeyValue.getMaxValue();
					if (value >= minValue && value <= maxValue) {
						return false;
					}
				}
			}
		}
		return true;
	}
	
	public boolean checkValue(String key, int minValue, int maxValue) {
		List<Specs2KeyValue> specs2KeyValues = specs2KeyValueDao.findByPropertyName(Specs2KeyValue.class, "key", key);
		if (StrUtils.checkCollection(specs2KeyValues)) {
			for (Specs2KeyValue specs2KeyValue : specs2KeyValues) {
				if ("1".equals(specs2KeyValue.getValueType())) {//连续值之间不能重叠
					if ((minValue >= specs2KeyValue.getMinValue() && minValue <= specs2KeyValue.getMaxValue()) ||
							(maxValue >= specs2KeyValue.getMinValue() && maxValue <= specs2KeyValue.getMaxValue())) {
						return false;
					}
				}
			}
		}
		return true;
	}
	
	public Specs2KeyValue getValueId(String key, String value, String createdUserId) {
		Specs2KeyValue specs2KeyValue = null;
		List<Specs2KeyValue> keyValues = specs2KeyValueDao.listByClass(Specs2KeyValue.class, StrUtils.createMap("key", key));
		for (Specs2KeyValue keyValue : keyValues) {
			if (StrUtils.checkParam(keyValue.getMinValue()) && Integer.valueOf(value) >= keyValue.getMinValue() && Integer.valueOf(value) <= keyValue.getMaxValue()) {
				specs2KeyValue = keyValue;
				break;
			}
		}
		if (null == specs2KeyValue) {
			Map<String, Object> queryMap = new HashMap<>();
			queryMap.put("key", key);
			queryMap.put("value", value);
			specs2KeyValue = specs2KeyValueDao.singleByClass(Specs2KeyValue.class, queryMap);
		}
		Specs2Key specs2Key = specs2KeyDao.findById(Specs2Key.class, key);
		if (!StrUtils.checkParam(specs2KeyValue)) {
			specs2KeyValue = new Specs2KeyValue();
			specs2KeyValue.setKey(key);
			specs2KeyValue.setValue(value);
			specs2KeyValue.setValueType("0");
			specs2KeyValue.setUnit(specs2Key.getUnit());
			specs2KeyValue.createdUser(createdUserId);
			specs2KeyValueDao.add(specs2KeyValue);
		}
		return specs2KeyValue;
	}
	
	@Override
	public Specs2KeyValue getValueId (String key, int minValue, int maxValue, int step, String createdUserId) {
		Specs2KeyValue specs2KeyValue = null;
		List<Specs2KeyValue> keyValues = specs2KeyValueDao.listByClass(Specs2KeyValue.class, StrUtils.createMap("key", key));
		for (Specs2KeyValue keyValue : keyValues) {
			if (StrUtils.checkParam(keyValue.getMinValue()) && minValue >= keyValue.getMinValue() && maxValue <= keyValue.getMaxValue()) {
				specs2KeyValue = keyValue;
				break;
			}
		}
		Specs2Key specs2Key = specs2KeyDao.findById(Specs2Key.class, key);
		if (!StrUtils.checkParam(specs2KeyValue)) {
			specs2KeyValue = new Specs2KeyValue();
			specs2KeyValue.setKey(key);
			specs2KeyValue.setMinValue(minValue);
			specs2KeyValue.setMaxValue(maxValue);
			specs2KeyValue.setStep(step);
			specs2KeyValue.setValueType("1");
			specs2KeyValue.setUnit(specs2Key.getUnit());
			specs2KeyValue.createdUser(createdUserId);
			specs2KeyValueDao.add(specs2KeyValue);
		}
		return specs2KeyValue;
	}
}

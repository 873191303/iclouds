package com.h3c.iclouds.biz;

import com.h3c.iclouds.base.BaseBiz;
import com.h3c.iclouds.po.business.Specs2Key;
import com.h3c.iclouds.po.business.Specs2KeyValue;

public interface Specs2KeyValueBiz extends BaseBiz<Specs2KeyValue> {
	
	boolean checkValueType(Specs2Key s2k, Specs2KeyValue entity);

	boolean validate(Specs2Key s2k, Specs2KeyValue entity);
	
	Specs2KeyValue getValueId(String key, String value, String createdUserId);
	
	Specs2KeyValue getValueId(String key, int minValue, int maxValue, int step, String createdUserId);
	
	/**
	 * 判断某个属性预设的离散值是否已包含在该属性的连续值范围内
	 * @param key
	 * @param value
	 * @return
	 */
	boolean checkValue(String key, int value);
	
	/**
	 * 判断某个属性预设的连续值是否已包含离散值或与其它连续值重叠
	 * @param key
	 * @param value
	 * @return
	 */
	boolean checkValue(String key, int minValue, int maxValue);
}

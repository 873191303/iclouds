package com.h3c.iclouds.biz;

import com.h3c.iclouds.base.BaseBiz;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.po.business.Bill;
import com.h3c.iclouds.po.business.MeasureDetail;

import java.util.List;
import java.util.Map;

public interface BillBiz extends BaseBiz<Bill> {
	
	/**
	 * 获取某计费明细最近的一次扣费信息
	 * @param measureId
	 * @return
	 */
	Bill lastByMeasureId(String measureId);
	
	/**
	 * 记录扣费信息
	 * @param measureDetail 账单明细id
	 * @param createdBy 创建人
	 * @param billType 扣费类型 0-自动扣费 1-手动扣费
	 */
	void create(MeasureDetail measureDetail, String createdBy, String billType, Long num);
	
	List<Map<String, Object>> totalMessage(PageEntity entity);
}
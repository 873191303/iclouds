package com.h3c.iclouds.quartz;

import com.h3c.iclouds.base.SpringContextHolder;
import com.h3c.iclouds.biz.BillBiz;
import com.h3c.iclouds.biz.MeasureDetailBiz;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.po.business.Bill;
import com.h3c.iclouds.po.business.MeasureDetail;
import com.h3c.iclouds.utils.LogUtils;

import java.util.List;

/**
 * 定时计算扣费信息并记录
 * Created by yKF7317 on 2017/8/10.
 */
public class BillQuartz {

	public void calculate() {
		MeasureDetailBiz measureDetailBiz = SpringContextHolder.getBean("measureDetailBiz");
		BillBiz billBiz = SpringContextHolder.getBean("billBiz");
		List<MeasureDetail> measureDetails = measureDetailBiz.getAll(MeasureDetail.class);
		for (MeasureDetail measureDetail : measureDetails) {
			// 公网流量的内容不在该任务进行
			if(!ConfigProperty.NETWORK_FLOW_CLASSID.equals(measureDetail.getClassId())) {
				try {
					billBiz.create(measureDetail, ConfigProperty.SYSTEM_FLAG, ConfigProperty.BILL_TYPE_AUTO,
							null);
				} catch (Exception e) {
					LogUtils.exception(Bill.class, e);
				}
			}
		}
	}
	
}

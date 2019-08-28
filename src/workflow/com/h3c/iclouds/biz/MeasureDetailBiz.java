package com.h3c.iclouds.biz;

import com.h3c.iclouds.base.BaseBiz;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.po.business.FlavorParam;
import com.h3c.iclouds.po.business.MeasureDetail;

public interface MeasureDetailBiz extends BaseBiz<MeasureDetail> {

    ResultType save(FlavorParam flavorParam, String type);

    ResultType update(FlavorParam newFlavor);

    /**
     * 非流量计算使用
     * @param resourceId
     * @param createdBy
     * @param delete
     */
    void stop(String resourceId, String createdBy, boolean delete);

    /**
     * 租户被删除时流量结算使用
     * @param resourceId
     * @param createdBy
     * @param delete
     * @param num
     */
    void stop(String resourceId, String createdBy, boolean delete, Long num);
}
package com.h3c.iclouds.biz;

import com.h3c.iclouds.base.BaseBiz;
import com.h3c.iclouds.po.NovaFlavor;
import com.h3c.iclouds.po.VolumeFlavor;
import com.h3c.iclouds.po.business.ListPrice;

import java.util.Map;

public interface ListPriceBiz extends BaseBiz<ListPrice> {
	
	/**
	 * 同步云主机规格时创建相应定价的假数据
	 * @param flavor
	 * @param createdUserId
	 */
	ListPrice saveByNovaFlavor(NovaFlavor flavor, String createdUserId, String azoneId);
	
	Map<String, String> getSpecByNovaFlavor(NovaFlavor flavor, String createdUserId);
	
	ListPrice saveByVolumeFlavor(VolumeFlavor flavor, String createdUserId, String azoneId);
	
	Map<String, String> getSpecByVolumeFlavor(VolumeFlavor flavor, String createdUserId);

}

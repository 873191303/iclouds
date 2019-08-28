package com.h3c.iclouds.biz;

import com.h3c.iclouds.base.BaseBiz;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.po.AsmMaster;
import com.h3c.iclouds.po.Asset2Drawer;

public interface Asset2DrawerBiz extends BaseBiz<Asset2Drawer> {
	
	/**
	 * 验证机柜位置是否被允许
	 * @param asmMaster
	 * @param entity
	 * @param resType
	 * @return
	 */
	ResultType checkAsset2Draw(AsmMaster asmMaster, Asset2Drawer entity, String resType);
	
	/**
	 * 查询机柜最大U数使用情况
	 * @param drawId
	 * @return
	 */
	Asset2Drawer findMaxUByDrawId(String drawId);
	
}

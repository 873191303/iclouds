package com.h3c.iclouds.biz;

import java.util.Map;

import com.h3c.iclouds.base.BaseBiz;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.po.RecycleItems;

public interface RecycleItemsBiz extends BaseBiz<RecycleItems> {

	void delete(String id);

	Object recovery(RecycleItems recycleItem);

	PageModel<Map<String, Object>> findCompleteForPage(PageEntity entity);

	void work(RecycleItems recycleItem);

	/**
	 * 用户主动回收资源
	 * @param resourceId
	 * @param resourceClassId
	 */
	void recycleResourceByUser(String resourceId, String resourceClassId);

	void deleteVolume(String id);

	void recoverVolume(String id);
}

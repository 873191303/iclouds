package com.h3c.iclouds.biz.impl;

import com.h3c.iclouds.base.BaseBizImpl;
import com.h3c.iclouds.base.SqlQueryBiz;
import com.h3c.iclouds.biz.Asset2DrawerBiz;
import com.h3c.iclouds.biz.Class2ItemsBiz;
import com.h3c.iclouds.biz.DrawsBiz;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.common.SqlQueryProperty;
import com.h3c.iclouds.dao.Asset2DrawerDao;
import com.h3c.iclouds.po.AsmMaster;
import com.h3c.iclouds.po.Asset2Drawer;
import com.h3c.iclouds.po.Class2Items;
import com.h3c.iclouds.po.Draws;
import com.h3c.iclouds.utils.StrUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service("asset2DrawerBiz")
public class Asset2DrawerBizImpl extends BaseBizImpl<Asset2Drawer> implements Asset2DrawerBiz {
	
	@Resource
	private Asset2DrawerDao asset2DrawerDao;
	
	@Resource
	private SqlQueryBiz sqlQueryBiz;
	
	@Resource
	private Class2ItemsBiz class2ItemsBiz;
	
	@Resource
	private DrawsBiz drawsBiz;
	
	@Override
	public ResultType checkAsset2Draw(AsmMaster asmMaster, Asset2Drawer entity, String resType) {
		Class2Items class2Items = null;
		if(asmMaster.getAssMode() != null) {	// 验证设备型号
			class2Items = class2ItemsBiz.findById(Class2Items.class, asmMaster.getAssMode());
			if(class2Items == null) {	// 选择的设备型号不存在
				return ResultType.class_item_not_exist;
			} else if(!resType.equals(class2Items.getResType())) {
				return ResultType.class_item_in_asm_error;
			}
		}
		//验证机柜是否存在
		Draws draws = drawsBiz.findById(Draws.class, entity.getDrawsId());
		if(null == draws) {
			return ResultType.draw_not_exist;
		}
		
		//验证机柜是否停用
		if (draws.getIsUse().equals(ConfigProperty.NO)) {
			return ResultType.draw_stop_status;
		}
		
		// 验证是否超过最大U
		int um = class2Items.getUnum(); //占用U数
		int start = entity.getUnumb();	// 开始U数
		int end = start + um - 1;	// 结束U数
		
		if(end > draws.getMaxU()) {
			return ResultType.draw_unumb_over;
		}
		
		// 查询是否与其它设备占用冲突
		Map<String, Object> queryMap = StrUtils.createMap("ID", asmMaster.getId());
		queryMap.put("DRAWSID", entity.getDrawsId());	// 查询同一个机柜
		List<Map<String, Object>> list = sqlQueryBiz.queryByName(SqlQueryProperty.QUERY_ASSET_IN_DRAW, queryMap);
		if(StrUtils.checkCollection(list)) {	// 机柜中不存在设备
			for (Map<String, Object> map : list) {
				int um_ = StrUtils.tranInteger(map.get("um"));
				int start_ = StrUtils.tranInteger(map.get("unumb"));
				int end_ = start_ + um_ - 1;
				if(!(start > end_ || start_ > end)) {	// 当前开始U数大于设置结束U或者当前结束U数小于设置开始U数
					return ResultType.draw_asm_clash;
				}
			}
		}
		return ResultType.success;
	}
	
	@Override
	public Asset2Drawer findMaxUByDrawId(String drawId) {
		return this.asset2DrawerDao.findMaxUByDrawId(drawId);
	}
}

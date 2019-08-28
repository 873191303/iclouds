package com.h3c.iclouds.biz.impl;

import com.h3c.iclouds.base.BaseBizImpl;
import com.h3c.iclouds.base.BaseRestControl;
import com.h3c.iclouds.biz.AsmMasterBiz;
import com.h3c.iclouds.biz.NetGroup2ItemBiz;
import com.h3c.iclouds.biz.NetGroupBiz;
import com.h3c.iclouds.biz.VdeviceBiz;
import com.h3c.iclouds.biz.VportBiz;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.dao.NetGroupDao;
import com.h3c.iclouds.po.AsmMaster;
import com.h3c.iclouds.po.NetGroup;
import com.h3c.iclouds.po.NetGroup2Item;
import com.h3c.iclouds.po.Vdevice;
import com.h3c.iclouds.po.Vport;
import com.h3c.iclouds.utils.StrUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by ykf7317 on 2017/9/5.
 */
@Service("netGroupBiz")
public class NetGroupBizImpl extends BaseBizImpl<NetGroup> implements NetGroupBiz {
	
	@Resource
	private NetGroupDao netGroupDao;
	
	@Resource
	private AsmMasterBiz asmMasterBiz;
	
	@Resource
	private NetGroup2ItemBiz netGroup2ItemBiz;
	
	@Resource
	private VdeviceBiz vdeviceBiz;
	
	@Resource
	private VportBiz vportBiz;
	
	@Override
	public PageModel<NetGroup> findForPage (PageEntity entity) {
		return netGroupDao.findForPage(entity);
	}
	
	@Override
	public Object linkMasters (String id, List<String> masterIds) {
		NetGroup netGroup = netGroupDao.findById(NetGroup.class, id);
		if (null == netGroup) {
			return BaseRestControl.tranReturnValue(ResultType.deleted);
		}
		if (StrUtils.checkCollection(masterIds)) {
			if (ConfigProperty.YES.equals(netGroup.getIsAlone()) && masterIds.size() > 1) {
				return BaseRestControl.tranReturnValue(ResultType.alone_asset_relate_one_master);
			}
			for (String masterId : masterIds) {
				AsmMaster master = asmMasterBiz.findById(AsmMaster.class, masterId);
				if (null == master) {
					return BaseRestControl.tranReturnValue(ResultType.asm_not_exist);
				}
				if (!master.getAssetType().equals(netGroup.getAssType())) {
					return BaseRestControl.tranReturnValue(ResultType.asset_type_conflict);
				}
				if (netGroup2ItemBiz.count(NetGroup2Item.class, StrUtils.createMap("masterId", masterId)) > 0) {
					return BaseRestControl.tranReturnValue(ResultType.asm_already_relate_item);
				}
				NetGroup2Item group2Item = new NetGroup2Item();
				group2Item.setMasterId(masterId);
				group2Item.setStackId(id);
				group2Item.setSerial(master.getSerial());
				group2Item.createdUser(this.getLoginUser());
				netGroup2ItemBiz.add(group2Item);
			}
		}
		return BaseRestControl.tranReturnValue(ResultType.success);
	}
	
	@Override
	public void deleteGroup (NetGroup netGroup) {
		List<NetGroup2Item> group2Items = netGroup2ItemBiz.findByPropertyName(NetGroup2Item.class, "stackId",
				netGroup.getId());
		for (NetGroup2Item group2Item : group2Items) {
			netGroup2ItemBiz.deleteItem(group2Item);
		}
		List<Vdevice> vdevices = vdeviceBiz.findByPropertyName(Vdevice.class, "stackId", netGroup.getId());
		for (Vdevice vdevice : vdevices) {
			vdeviceBiz.delete(vdevice);
		}
		List<Vport> vports = vportBiz.findByPropertyName(Vport.class, "stackId", netGroup.getId());
		for (Vport vport : vports) {
			vportBiz.delete(vport);
		}
		netGroupDao.delete(netGroup);
	}
}

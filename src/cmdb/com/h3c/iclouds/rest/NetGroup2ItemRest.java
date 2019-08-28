package com.h3c.iclouds.rest;

import com.h3c.iclouds.base.BaseRest;
import com.h3c.iclouds.base.BaseRestControl;
import com.h3c.iclouds.biz.AsmMasterBiz;
import com.h3c.iclouds.biz.NetGroup2ItemBiz;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageList;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.po.AsmMaster;
import com.h3c.iclouds.po.NetGroup2Item;
import com.h3c.iclouds.utils.InvokeSetForm;
import com.h3c.iclouds.utils.StrUtils;
import com.h3c.iclouds.validate.ValidatorUtils;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Map;

/**
 * Created by ykf7317 on 2017/9/5.
 */
@Api(value = "配置管理堆叠子设备信息")
@RestController
@RequestMapping(value = "/net/group2Item")
public class NetGroup2ItemRest extends BaseRest<NetGroup2Item> {
	
	@Resource
	private NetGroup2ItemBiz netGroup2ItemBiz;
	
	@Resource
	private AsmMasterBiz asmMasterBiz;
	
	@Override
	@ApiOperation(value = "获取堆叠独立设备列表")
	@RequestMapping(value = "/alone", method = RequestMethod.GET)
	public Object list () {
		PageEntity entity = this.beforeList();
		PageModel<NetGroup2Item> pageModel = netGroup2ItemBiz.findForPage(entity);
		PageList<NetGroup2Item> page = new PageList<>(pageModel, entity.getsEcho());
		return BaseRestControl.tranReturnValue(page);
	}
	
	@ApiOperation(value = "获取堆叠下的子设备列表")
	@RequestMapping(value = "/group/{id}", method = RequestMethod.GET)
	public Object list (@PathVariable String id) {
		PageEntity entity = this.beforeList();
		entity.setSpecialParam(id);
		PageModel<NetGroup2Item> pageModel = netGroup2ItemBiz.findForPage(entity);
		PageList<NetGroup2Item> page = new PageList<>(pageModel, entity.getsEcho());
		return BaseRestControl.tranReturnValue(page);
	}
	
	@Override
	@ApiOperation(value = "获取堆叠子设备详细信息")
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public Object get (@PathVariable String id) {
		NetGroup2Item netGroup2Item = netGroup2ItemBiz.findById(NetGroup2Item.class, id);
		if (null == netGroup2Item) {
			return BaseRestControl.tranReturnValue(ResultType.deleted);
		}
		return BaseRestControl.tranReturnValue(netGroup2Item);
	}
	
	@ApiOperation(value = "堆叠子设备或独立设备关联资产")
	@RequestMapping(value = "/link/{id}/{masterId}", method = RequestMethod.PUT)
	public Object link (@PathVariable String id, @PathVariable String masterId) {
		NetGroup2Item netGroup2Item = netGroup2ItemBiz.findById(NetGroup2Item.class, id);
		if (null == netGroup2Item) {
			return BaseRestControl.tranReturnValue(ResultType.deleted);
		}
		if (StrUtils.checkParam(netGroup2Item.getMasterId())) {
			return BaseRestControl.tranReturnValue(ResultType.item_already_relate_asm);
		}
		AsmMaster master = asmMasterBiz.findById(AsmMaster.class, masterId);
		if (null == master) {
			return BaseRestControl.tranReturnValue(ResultType.asm_not_exist);
		}
		if (!master.getAssetType().equals(netGroup2Item.getAssetTypeId())) {
			return BaseRestControl.tranReturnValue(ResultType.asset_type_conflict);
		}
		if (StrUtils.checkParam(netGroup2Item.getSerial()) && !master.getSerial().equals(netGroup2Item.getSerial())) {
			return BaseRestControl.tranReturnValue(ResultType.serial_conflict);
		}
		if (netGroup2ItemBiz.count(NetGroup2Item.class, StrUtils.createMap("masterId", masterId)) > 0) {
			return BaseRestControl.tranReturnValue(ResultType.asm_already_relate_item);
		}
		netGroup2Item.setMasterId(masterId);
		netGroup2Item.setSerial(master.getSerial());
		netGroup2Item.updatedUser(this.getLoginUser());
		netGroup2ItemBiz.update(netGroup2Item);
		return BaseRestControl.tranReturnValue(netGroup2Item);
	}
	
	@ApiOperation(value = "堆叠子设备取消关联资产")
	@RequestMapping(value = "/unlink/{id}", method = RequestMethod.PUT)
	public Object unlink (@PathVariable String id) {
		NetGroup2Item netGroup2Item = netGroup2ItemBiz.findById(NetGroup2Item.class, id);
		if (null == netGroup2Item) {
			return BaseRestControl.tranReturnValue(ResultType.deleted);
		}
		netGroup2Item.setMasterId(null);
		netGroup2Item.setSerial(null);
		netGroup2Item.updatedUser(this.getLoginUser());
		netGroup2ItemBiz.update(netGroup2Item);
		return BaseRestControl.tranReturnValue(netGroup2Item);
	}
	
	@Override
	@ApiOperation(value = "删除堆叠子设备或独立设备信息")
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public Object delete (@PathVariable String id) {
		NetGroup2Item netGroup2Item = netGroup2ItemBiz.findById(NetGroup2Item.class, id);
		if (null == netGroup2Item) {
			return BaseRestControl.tranReturnValue(ResultType.deleted);
		}
		netGroup2ItemBiz.deleteItem(netGroup2Item);
		return BaseRestControl.tranReturnValue(ResultType.success);
	}
	
	@Override
	@ApiOperation(value = "新增堆叠子设备")
	@RequestMapping(method = RequestMethod.POST)
	public Object save (@RequestBody NetGroup2Item entity) {
		return null;
	}
	
	@Override
	@ApiOperation(value = "修改堆叠子设备信息")
	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	public Object update (@PathVariable String id, @RequestBody NetGroup2Item entity) throws IOException {
		Map<String, String> validatorMap = ValidatorUtils.validator(entity);
		if(!validatorMap.isEmpty()) {
			return BaseRestControl.tranReturnValue(ResultType.parameter_error, validatorMap);
		}
		NetGroup2Item netGroup2Item = netGroup2ItemBiz.findById(NetGroup2Item.class, id);
		if (null == netGroup2Item) {
			return BaseRestControl.tranReturnValue(ResultType.deleted);
		}
		InvokeSetForm.copyFormProperties(entity, netGroup2Item);
		netGroup2Item.updatedUser(this.getLoginUser());
		netGroup2ItemBiz.update(netGroup2Item);
		return BaseRestControl.tranReturnValue(ResultType.success, netGroup2Item);
	}
}

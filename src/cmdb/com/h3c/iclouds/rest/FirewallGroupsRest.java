package com.h3c.iclouds.rest;

import com.h3c.iclouds.base.BaseDAO;
import com.h3c.iclouds.base.BaseRest;
import com.h3c.iclouds.base.BaseRestControl;
import com.h3c.iclouds.biz.AsmMasterBiz;
import com.h3c.iclouds.biz.FirewallGroupsBiz;
import com.h3c.iclouds.biz.InitCodeBiz;
import com.h3c.iclouds.biz.NetPortsBiz;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageList;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.exception.MessageException;
import com.h3c.iclouds.po.AsmMaster;
import com.h3c.iclouds.po.FirewallGroups;
import com.h3c.iclouds.po.FirewallGroups2Items;
import com.h3c.iclouds.po.InitCode;
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
import java.util.List;
import java.util.Map;

@Api(value = "防火墙堆叠")
@RestController
@RequestMapping("/firewall/groups")
public class FirewallGroupsRest extends BaseRest<FirewallGroups> {
	
	@Resource
	private FirewallGroupsBiz firewallGroupsBiz;
	
	@Resource(name = "baseDAO")
	private BaseDAO<FirewallGroups2Items> firewallGroups2ItemsDao;
	
	@Resource
	private AsmMasterBiz asmMasterBiz;
	
	@Resource
	private NetPortsBiz netPortsBiz;
	
	@Resource
	private InitCodeBiz initCodeBiz;
	
	@Override
	@ApiOperation(value = "获取防火墙堆叠列表")
	@RequestMapping(method = RequestMethod.GET)
	//@Perms(value = "cmdb.groups.firewall.list")
	public Object list() {
		PageEntity entity = this.beforeList();
		PageModel<FirewallGroups> pageModel = firewallGroupsBiz.findForPage(entity);
		PageList<FirewallGroups> page = new PageList<>(pageModel, entity.getsEcho());
		return BaseRestControl.tranReturnValue(page);
	}
	
	@ApiOperation(value = "获取未加入堆叠的防火墙资产列表")
	@RequestMapping(method = RequestMethod.GET, value = "/without")
	public Object without() {
		PageEntity entity = this.beforeList();
		InitCode initCode = initCodeBiz.getByTypeCode(ConfigProperty.CMDB_ASSET_TYPE_FIREWALL, ConfigProperty.CMDB_ASSET_TYPE);
		if(initCode == null) {
			return ResultType.assetType_error;
		}
		entity.setSpecialParam(initCode.getId());
		PageModel<AsmMaster> pageModel = asmMasterBiz.without(entity);
		PageList<AsmMaster> page = new PageList<AsmMaster>(pageModel, entity.getsEcho());
		return BaseRestControl.tranReturnValue(page);
	}

	@Override
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@ApiOperation(value = "获取防火墙堆叠")
	//@Perms(value = "cmdb.groups.firewall.list")
	public Object get(@PathVariable String id) {
		FirewallGroups entity = firewallGroupsBiz.findById(FirewallGroups.class, id);
		if(null == entity) {
			return BaseRestControl.tranReturnValue(ResultType.deleted);
		}
		entity.setAsmMasters(asmMasterBiz.findByPropertyName(AsmMaster.class, "stackId", id));
		entity.setNetPorts(netPortsBiz.findPortByStack(id, ConfigProperty.CMDB_ASSET_TYPE_FIREWALL));
		return BaseRestControl.tranReturnValue(entity);
	}
	
	@RequestMapping(value = "/{id}/limit", method = RequestMethod.GET)
	@ApiOperation(value = "获取防火墙堆叠下的防火墙资产列表")
	//@Perms(value = "cmdb.groups.firewall.list")
	public Object limit(@PathVariable String id) {
		FirewallGroups entity = firewallGroupsBiz.findById(FirewallGroups.class, id);
		if(null == entity) {
			return BaseRestControl.tranReturnValue(ResultType.deleted);
		}
		return BaseRestControl.tranReturnValue(asmMasterBiz.findByPropertyName(AsmMaster.class, "stackId", id));
	}
	
	@RequestMapping(value = "/{id}/netPort", method = RequestMethod.GET)
	@ApiOperation(value = "获取防火墙堆叠的所有接口")
	//@Perms(value = "cmdb.groups.firewall.list")
	public Object netPort(@PathVariable String id) {
		FirewallGroups entity = firewallGroupsBiz.findById(FirewallGroups.class, id);
		if(null == entity) {
			return BaseRestControl.tranReturnValue(ResultType.deleted);
		}
		return BaseRestControl.tranReturnValue(netPortsBiz.findPortByStack(id, ConfigProperty.CMDB_ASSET_TYPE_FIREWALL));
	}


	@Override
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	@ApiOperation(value = "删除防火墙堆叠")
	//@Perms(value = "cmdb.groups.firewall.delete")
	public Object delete(@PathVariable String id) {
		FirewallGroups entity = firewallGroupsBiz.findById(FirewallGroups.class, id);
		if (null == entity) {
			return BaseRestControl.tranReturnValue(ResultType.deleted);
		}
		try {
			firewallGroupsBiz.delete(entity);
			return BaseRestControl.tranReturnValue(ResultType.success);
		} catch (Exception e) {
			this.exception(FirewallGroups.class, e);
			return BaseRestControl.tranReturnValue(ResultType.failure);
		}
	}

	@Override
	@RequestMapping(method = RequestMethod.POST)
	@ApiOperation(value = "创建防火墙堆叠")
	//@Perms(value = "cmdb.groups.firewall.save")
	public Object save(@RequestBody FirewallGroups entity) {
		Map<String, String> validatorMap = ValidatorUtils.validator(entity);
		if(!validatorMap.isEmpty()) {
			return BaseRestControl.tranReturnValue(ResultType.parameter_error, validatorMap);
		}
		try {
			entity.createdUser(this.getLoginUser());
			firewallGroupsBiz.add(entity);
			return BaseRestControl.tranReturnValue(ResultType.success, entity);
		} catch (Exception e) {
			this.exception(FirewallGroups.class, e);
			return BaseRestControl.tranReturnValue(ResultType.failure);
		}
	}

	@Override
	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	@ApiOperation(value = "更新防火墙堆叠")
	//@Perms(value = "cmdb.groups.firewall.update")
	public Object update(@PathVariable String id, @RequestBody FirewallGroups entity) throws IOException {
		Map<String, String> validatorMap = ValidatorUtils.validator(entity);
		if(!validatorMap.isEmpty()) {
			return BaseRestControl.tranReturnValue(ResultType.parameter_error, validatorMap);
		}
		FirewallGroups before = this.firewallGroupsBiz.findById(FirewallGroups.class, id);
		if(null == before) {
			return BaseRestControl.tranReturnValue(ResultType.deleted);
		}
		try {
			InvokeSetForm.copyFormProperties(entity, before);
			before.updatedUser(getLoginUser());
			this.firewallGroupsBiz.update(before);
			return BaseRestControl.tranReturnValue(ResultType.success);
		} catch (Exception e) {
			this.exception(FirewallGroups.class, e);
			return BaseRestControl.tranReturnValue(ResultType.failure);
		}
	}
	
	/**
	 * 保存用户选择的资源
	 * @param id
	 * @return
	 */
	@ApiOperation(value = "添加防火墙堆叠下的资源")
	@RequestMapping(value = "/{id}/add/{masterId}", method = RequestMethod.POST)
	//@Perms(value = "cmdb.groups.firewall.limit")
	public Object addLimit(@PathVariable String id, @PathVariable String masterId, @RequestBody Map<String, Object> jsonMap) {
		FirewallGroups group = this.firewallGroupsBiz.findById(FirewallGroups.class, id);
		if(group == null) {
			return BaseRestControl.tranReturnValue(ResultType.deleted);
		}
		
		AsmMaster entity = asmMasterBiz.findById(AsmMaster.class, masterId);
		if(entity == null || !ConfigProperty.CMDB_ASSET_FLAG2_USE.equals(entity.getStatus())) {
			// 选择的资产状态异常
			return BaseRestControl.tranReturnValue(ResultType.asset_flag_error);
		}
		
		if(!ConfigProperty.CMDB_ASSET_TYPE_FIREWALL.equals(entity.getAssetTypeCode())) {
			// 选择的资产类型不对
			return BaseRestControl.tranReturnValue(ResultType.assetType_error);
		}
		
		// 查询选择设备是否存在堆叠
		List<FirewallGroups2Items> list = firewallGroups2ItemsDao.findByPropertyName(FirewallGroups2Items.class, "masterId", entity.getId());
		if(list != null && !list.isEmpty()) {
			for (FirewallGroups2Items item : list) {
				if(!item.getStackId().equals(id)) {
					return BaseRestControl.tranReturnValue(ResultType.asset_exist_stack, "选择的设备归属于堆叠名为[" + item.getStackName() + "]");
				} else {
					// 选择的资产类型不对
					return BaseRestControl.tranReturnValue(ResultType.success);
				}
			}
		}
		try {
			this.firewallGroupsBiz.addLimit(group, entity, StrUtils.tranString(jsonMap.get("remark")));
			return BaseRestControl.tranReturnValue(ResultType.success);
		} catch (Exception e) {
			this.exception(FirewallGroups.class, e);
			return BaseRestControl.tranReturnValue(ResultType.failure);
		}
	}
	
	/**
	 * 保存用户选择的资源
	 * @param id
	 * @return
	 */
	@ApiOperation(value = "移除防火墙堆叠下的资源")
	@RequestMapping(value = "/{id}/remove/{masterId}", method = RequestMethod.DELETE)
	//@Perms(value = "cmdb.groups.firewall.limit")
	public Object removeLimit(@PathVariable String id, @PathVariable String masterId) {
		List<FirewallGroups2Items> list = this.firewallGroups2ItemsDao.findByPropertyName(FirewallGroups2Items.class, "masterId", masterId);
		if(list == null || list.isEmpty()) {
			return BaseRestControl.tranReturnValue(ResultType.success);
		}
		try {
			this.firewallGroupsBiz.removeLimit(id, list);
			return BaseRestControl.tranReturnValue(ResultType.success);
		} catch (MessageException e) {
			this.exception(FirewallGroups.class, e);
			return BaseRestControl.tranReturnValue(e.getResultCode());
		} catch (Exception e) {
			this.exception(FirewallGroups.class, e);
			return BaseRestControl.tranReturnValue(ResultType.failure);
		}
	}

}

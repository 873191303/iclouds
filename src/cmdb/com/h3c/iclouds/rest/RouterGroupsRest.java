package com.h3c.iclouds.rest;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.h3c.iclouds.auth.CacheSingleton;
import com.h3c.iclouds.base.BaseDAO;
import com.h3c.iclouds.base.BaseRest;
import com.h3c.iclouds.base.BaseRestControl;
import com.h3c.iclouds.biz.AsmMasterBiz;
import com.h3c.iclouds.biz.NetPortsBiz;
import com.h3c.iclouds.biz.RouterGroupsBiz;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageList;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.exception.MessageException;
import com.h3c.iclouds.po.AsmMaster;
import com.h3c.iclouds.po.RouterGroups;
import com.h3c.iclouds.po.RouterGroups2Items;
import com.h3c.iclouds.utils.InvokeSetForm;
import com.h3c.iclouds.utils.StrUtils;
import com.h3c.iclouds.validate.ValidatorUtils;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

@Api(value = "路由器堆叠")
@RestController
@RequestMapping("/router/groups")
public class RouterGroupsRest extends BaseRest<RouterGroups> {
	
	@Resource
	private RouterGroupsBiz routerGroupsBiz;
	
	@Resource(name = "baseDAO")
	private BaseDAO<RouterGroups2Items> routerGroups2ItemsDao;
	
	@Resource
	private AsmMasterBiz asmMasterBiz;
	
	@Resource
	private NetPortsBiz netPortsBiz;
	
	@Override
	@ApiOperation(value = "获取路由器堆叠列表")
	@RequestMapping(method = RequestMethod.GET)
	public Object list() {
		PageEntity entity = this.beforeList();
		PageModel<RouterGroups> pageModel = routerGroupsBiz.findForPage(entity);
		PageList<RouterGroups> page = new PageList<RouterGroups>(pageModel, entity.getsEcho());
		return BaseRestControl.tranReturnValue(page);
	}
	
	@ApiOperation(value = "获取路由器堆叠列表")
	@RequestMapping(method = RequestMethod.GET, value = "/without")
	public Object without() {
		PageEntity entity = this.beforeList();
		entity.setSpecialParam(CacheSingleton.getInstance().getRouterAssetType());
		PageModel<AsmMaster> pageModel = asmMasterBiz.without(entity);
		PageList<AsmMaster> page = new PageList<AsmMaster>(pageModel, entity.getsEcho());
		return BaseRestControl.tranReturnValue(page);
	}

	@Override
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@ApiOperation(value = "获取路由器堆叠")
	//@Perms(value = "cmdb.groups.router.list")
	public Object get(@PathVariable String id) {
		RouterGroups entity = routerGroupsBiz.findById(RouterGroups.class, id);
		if(entity != null) {
			entity.setAsmMasters(asmMasterBiz.findByPropertyName(AsmMaster.class, "stackId", id));
			entity.setNetPorts(netPortsBiz.findPortByStack(id, ConfigProperty.CMDB_ASSET_TYPE_ROUTER));
			return BaseRestControl.tranReturnValue(entity);
		}
		return BaseRestControl.tranReturnValue(ResultType.deleted);
	}
	
	@RequestMapping(value = "/{id}/limit", method = RequestMethod.GET)
	@ApiOperation(value = "获取路由器堆叠下的路由器列表")
	//@Perms(value = "cmdb.groups.router.list")
	public Object limit(@PathVariable String id) {
		RouterGroups entity = routerGroupsBiz.findById(RouterGroups.class, id);
		if(entity != null) {
			return BaseRestControl.tranReturnValue(asmMasterBiz.findByPropertyName(AsmMaster.class, "stackId", id));
		}
		return BaseRestControl.tranReturnValue(ResultType.deleted);
	}
	
	@RequestMapping(value = "/{id}/netPort", method = RequestMethod.GET)
	@ApiOperation(value = "获取路由器堆叠的所有接口")
	//@Perms(value = "cmdb.groups.router.list")
	public Object netPort(@PathVariable String id) {
		RouterGroups entity = routerGroupsBiz.findById(RouterGroups.class, id);
		if(entity != null) {
			return BaseRestControl.tranReturnValue(netPortsBiz.findPortByStack(id, ConfigProperty.CMDB_ASSET_TYPE_ROUTER));
		}
		return BaseRestControl.tranReturnValue(ResultType.deleted);
	}

	@Override
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	@ApiOperation(value = "删除路由器堆叠")
	//@Perms(value = "cmdb.groups.router.delete")
	public Object delete(@PathVariable String id) {
		RouterGroups entity = routerGroupsBiz.findById(RouterGroups.class, id);
		if(entity != null) {
			try {
				routerGroupsBiz.delete(entity);
				return BaseRestControl.tranReturnValue(ResultType.success);
			} catch (Exception e) {
				this.exception(this.getClass(), e);
				return BaseRestControl.tranReturnValue(ResultType.failure);
			}
		}
		return BaseRestControl.tranReturnValue(ResultType.deleted);
	}

	@Override
	@RequestMapping(method = RequestMethod.POST)
	@ApiOperation(value = "创建路由器堆叠")
	//@Perms(value = "cmdb.groups.router.save")
	public Object save(@RequestBody RouterGroups entity) {
		Map<String, String> validatorMap = ValidatorUtils.validator(entity);
		if(validatorMap.isEmpty()) {
			entity.createdUser(this.getLoginUser());
			try {
				routerGroupsBiz.add(entity);
				return BaseRestControl.tranReturnValue(ResultType.success, entity);	
			} catch (Exception e) {
				this.exception(this.getClass(), e);
				return BaseRestControl.tranReturnValue(ResultType.failure);
			}
		}
		return BaseRestControl.tranReturnValue(ResultType.parameter_error, validatorMap);
	}

	@Override
	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	@ApiOperation(value = "更新路由器堆叠")
	//@Perms(value = "cmdb.groups.router.update")
	public Object update(@PathVariable String id, @RequestBody RouterGroups entity) throws IOException {
		Map<String, String> validatorMap = ValidatorUtils.validator(entity);
		if(validatorMap.isEmpty()) {
			RouterGroups before = this.routerGroupsBiz.findById(RouterGroups.class, id);
			if(before != null) {
				try {
					InvokeSetForm.copyFormProperties(entity, before);
					before.updatedUser(getLoginUser());
					this.routerGroupsBiz.update(before);
					return BaseRestControl.tranReturnValue(ResultType.success);
				} catch (Exception e) {
					this.exception(this.getClass(), e);
					return BaseRestControl.tranReturnValue(ResultType.failure);
				}
			}
			return BaseRestControl.tranReturnValue(ResultType.deleted);
		}
		return BaseRestControl.tranReturnValue(ResultType.parameter_error, validatorMap);
	}
	
	/**
	 * 保存用户选择的资源
	 * @param req
	 * @param id
	 * @return
	 */
	@ApiOperation(value = "添加路由器堆叠下的资源")
	@RequestMapping(value = "/{id}/add/{masterId}", method = RequestMethod.POST)
	//@Perms(value = "cmdb.groups.router.limit")
	public Object addLimit(@PathVariable String id, @PathVariable String masterId, @RequestBody Map<String, Object> jsonMap) {
		RouterGroups group = this.routerGroupsBiz.findById(RouterGroups.class, id);
		if(group == null) {
			return BaseRestControl.tranReturnValue(ResultType.deleted);
		}
		
		AsmMaster entity = asmMasterBiz.findById(AsmMaster.class, masterId);
		if(entity == null || !ConfigProperty.CMDB_ASSET_FLAG2_USE.equals(entity.getStatus())) {
			// 选择的资产状态异常
			return BaseRestControl.tranReturnValue(ResultType.asset_flag_error);
		}
		
		if(!ConfigProperty.CMDB_ASSET_TYPE_ROUTER.equals(entity.getAssetTypeCode())) {
			// 选择的资产类型不对
			return BaseRestControl.tranReturnValue(ResultType.assetType_error);
		}
		
		// 查询选择设备是否存在堆叠
		List<RouterGroups2Items> list = routerGroups2ItemsDao.findByPropertyName(RouterGroups2Items.class, "masterId", entity.getId());
		if(list != null && !list.isEmpty()) {
			for (RouterGroups2Items item : list) {
				if(!item.getStackId().equals(id)) {
					return BaseRestControl.tranReturnValue(ResultType.asset_exist_stack, "选择的设备归属于堆叠名为[" + item.getStackName() + "]");
				} else {
					// 选择的资产类型不对
					return BaseRestControl.tranReturnValue(ResultType.success);
				}
			}
		}
		try {
			this.routerGroupsBiz.addLimit(group, entity, StrUtils.tranString(jsonMap.get("remark")));
			return BaseRestControl.tranReturnValue(ResultType.success);
		} catch (Exception e) {
			this.exception(clazz, e);
			return BaseRestControl.tranReturnValue(ResultType.failure);
		}
	}
	
	/**
	 * 保存用户选择的资源
	 * @param req
	 * @param id
	 * @return
	 */
	@ApiOperation(value = "移除路由器堆叠下的资源")
	@RequestMapping(value = "/{id}/remove/{masterId}", method = RequestMethod.DELETE)
	//@Perms(value = "cmdb.groups.router.limit")
	public Object removeLimit(@PathVariable String id, @PathVariable String masterId) {
		List<RouterGroups2Items> list = this.routerGroups2ItemsDao.findByPropertyName(RouterGroups2Items.class, "masterId", masterId);
		if(list == null || list.isEmpty()) {
			return BaseRestControl.tranReturnValue(ResultType.success);
		}
		try {
			this.routerGroupsBiz.removeLimit(id, list);
			return BaseRestControl.tranReturnValue(ResultType.success);
		} catch (MessageException e) {
			this.exception(this.getClass(), e);
			return BaseRestControl.tranReturnValue(e.getResultCode());
		} catch (Exception e) {
			this.exception(clazz, e);
			return BaseRestControl.tranReturnValue(ResultType.failure);
		}
	}

}

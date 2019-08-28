package com.h3c.iclouds.rest;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.h3c.iclouds.base.BaseChildRest;
import com.h3c.iclouds.base.BaseRestControl;
import com.h3c.iclouds.biz.Class2ItemsBiz;
import com.h3c.iclouds.biz.InitCodeBiz;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageList;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.po.Class2Items;
import com.h3c.iclouds.po.InitCode;
import com.h3c.iclouds.utils.InvokeSetForm;
import com.h3c.iclouds.validate.ValidatorUtils;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

@Api(value = "资产管理资源型号", description = "资产管理资源型号")
@RestController
@RequestMapping("/initCode/{pid}")
public class Class2ItemsRest extends BaseChildRest<Class2Items> {
	
	@Resource
	private Class2ItemsBiz class2ItemsBiz;
	
	@Resource
	private InitCodeBiz initCodeBiz;
	
	@ApiOperation(value = "获取资产管理资源型号列表", response = Class2Items.class)
	@RequestMapping(value = "/items/sublist", method = RequestMethod.GET)
	public Object sublist(@PathVariable String pid) {
		Map<String, String> queryMap = new HashMap<String, String>();
		queryMap.put("flag", ConfigProperty.YES);
		queryMap.put("resType", pid);
		
		InitCode initCode = initCodeBiz.getByTypeCode(pid, ConfigProperty.CMDB_ASSET_TYPE);
		if(initCode != null) {
			queryMap.put("resType", initCode.getId());
		}
		List<Class2Items> sublist = class2ItemsBiz.findByClazz(Class2Items.class, queryMap, "id", "itemId", "itemName", "unum", "resTypeCode");
		return BaseRestControl.tranReturnValue(sublist);
	}

	@Override
	@ApiOperation(value = "获取资产管理资源型号列表", response = Class2Items.class)
	@RequestMapping(value = "/items", method = RequestMethod.GET)
	public Object list(@PathVariable String pid) {
		PageEntity entity = this.beforeList();
		entity.setSpecialParam(pid);
		PageModel<Class2Items> pageModel = class2ItemsBiz.findForPage(entity);
		PageList<Class2Items> page = new PageList<Class2Items>(pageModel, entity.getsEcho());
		return BaseRestControl.tranReturnValue(page);
	}

	@Override
	@RequestMapping(value = "/items/{id}", method = RequestMethod.GET)
	@ApiOperation(value = "获取资产管理资源型号")
	public Object get(@PathVariable String pid, @PathVariable String id) {
		Class2Items entity = class2ItemsBiz.findById(Class2Items.class, id);
		if(entity != null) {
			if(!entity.getResType().equals(pid)) {
				return BaseRestControl.tranReturnValue(ResultType.parameter_error);
			}
			return BaseRestControl.tranReturnValue(entity);
		}
		return BaseRestControl.tranReturnValue(ResultType.deleted);
	}
	
	@RequestMapping(value = "/items/{id}/recover", method = RequestMethod.POST)
	@ApiOperation(value = "启用资产管理资源型号")
	public Object recover(@PathVariable String pid, @PathVariable String id) {
		Class2Items entity = class2ItemsBiz.findById(Class2Items.class, id);
		if(entity != null) {
			if(!entity.getResType().equals(pid)) {
				return BaseRestControl.tranReturnValue(ResultType.parameter_error);
			}
			if(!ConfigProperty.YES.equals(entity.getFlag())) {
				entity.setFlag(ConfigProperty.YES);
				class2ItemsBiz.update(entity);
			}
			return BaseRestControl.tranReturnValue(ResultType.success);
		}
		return BaseRestControl.tranReturnValue(ResultType.deleted);
	}

	@Override
	@RequestMapping(value = "/items/{id}", method = RequestMethod.DELETE)
	@ApiOperation(value = "禁用资产管理资源型号")
	public Object delete(@PathVariable String pid, @PathVariable String id) {
		Class2Items entity = class2ItemsBiz.findById(Class2Items.class, id);
		if(entity != null) {
			if(!entity.getResType().equals(pid)) {
				return BaseRestControl.tranReturnValue(ResultType.parameter_error);
			}
			if(!ConfigProperty.NO.equals(entity.getFlag())) {
				entity.setFlag(ConfigProperty.NO);
				class2ItemsBiz.update(entity);
			}
			return BaseRestControl.tranReturnValue(ResultType.success);
		}
		return BaseRestControl.tranReturnValue(ResultType.deleted);
	}

	@Override
	@RequestMapping(value = "/items", method = RequestMethod.POST)
	@ApiOperation(value = "创建资产管理资源型号", response = Class2Items.class)
	public Object save(@PathVariable String pid, @RequestBody Class2Items entity) {
		Map<String, String> validatorMap = ValidatorUtils.validator(entity);
		if(validatorMap.isEmpty()) {
			InitCode initCode = initCodeBiz.findById(InitCode.class, pid);
			if(initCode == null) {
				return BaseRestControl.tranReturnValue(ResultType.parent_code_not_exist);
			}
//			entity.setFlag(ConfigProperty.YES);
			entity.createdUser(this.getLoginUser());
			try {
				entity.setResType(pid);
				class2ItemsBiz.add(entity);
				return BaseRestControl.tranReturnValue(ResultType.success, entity);
			} catch (Exception e) {
				this.exception(this.getClass(), e);
				return BaseRestControl.tranReturnValue(ResultType.failure);
			}
		}
		return BaseRestControl.tranReturnValue(ResultType.parameter_error, validatorMap);
	}

	@Override
	@RequestMapping(value = "/items/{id}", method = RequestMethod.PUT)
	@ApiOperation(value = "更新资产管理资源型号", response = Class2Items.class)
	public Object update(@PathVariable String pid, @PathVariable String id, @RequestBody Class2Items entity) throws IOException {
		Map<String, String> validatorMap = ValidatorUtils.validator(entity);
		if(validatorMap.isEmpty()) {
			Class2Items before = class2ItemsBiz.findById(Class2Items.class, id);
			if(before != null) {
				if(!before.getResType().equals(pid)) {
					return BaseRestControl.tranReturnValue(ResultType.parameter_error);
				}
				InvokeSetForm.copyFormProperties(entity, before);
				before.updatedUser(this.getLoginUser());
				try {
					class2ItemsBiz.update(before);
					return BaseRestControl.tranReturnValue(ResultType.success, before);	
				} catch (Exception e) {
					this.exception(this.getClass(), e);
					return BaseRestControl.tranReturnValue(ResultType.failure);
				}
			}
			return BaseRestControl.tranReturnValue(ResultType.deleted);
		}
		return BaseRestControl.tranReturnValue(ResultType.parameter_error, validatorMap);
	}

}

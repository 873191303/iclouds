package com.h3c.iclouds.rest;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.h3c.iclouds.base.BaseRestControl;
import com.h3c.iclouds.biz.InterfacesBiz;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageList;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.po.Interfaces;
import com.h3c.iclouds.utils.InvokeSetForm;
import com.h3c.iclouds.validate.ValidatorUtils;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

@Api(value = "云运维系统参数设置2接口类 ", description = "云运维系统参数设置2接口类 ")
@RestController
@RequestMapping("/interfaces")
public class InterfacesRest extends BaseRestControl {

	@Resource
	private InterfacesBiz interfacesBiz;
	
	
	@ApiOperation(value = "获取云运维系统参数设置")
	@RequestMapping(method = RequestMethod.GET)
	public Object list() {
		PageEntity entity = this.beforeList();
		PageModel<Interfaces> pageModel = interfacesBiz.findForPage(entity);
		PageList<Interfaces> page = new PageList<Interfaces>(pageModel, entity.getsEcho());
		return BaseRestControl.tranReturnValue(page);
	}
	
	@ApiOperation(value = "获取type对应的数据")
	@RequestMapping(value = "/{codeType}/sublist", method = RequestMethod.GET)
	public Object list(@PathVariable String type) {
		List<Interfaces> list = this.interfacesBiz.findByPropertyName(Interfaces.class, "type", type);
		return BaseRestControl.tranReturnValue(list);
	}
	
	

	
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@ApiOperation(value = "获取云运维系统参数设置")
	public Object get(@PathVariable String id) {
		Interfaces entity = interfacesBiz.findById(Interfaces.class, id);
		if(entity != null) {
			return BaseRestControl.tranReturnValue(entity);
		}
		return BaseRestControl.tranReturnValue(ResultType.deleted);
	}
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	@ApiOperation(value = "获取云运维系统参数设置")
	public Object delete(@PathVariable String id) {
		Interfaces entity = interfacesBiz.findById(Interfaces.class, id);
		if(entity != null) {
			interfacesBiz.delete(entity);
			return BaseRestControl.tranReturnValue(ResultType.success);
		}
		return BaseRestControl.tranReturnValue(ResultType.deleted);
	}
	
	

	
	@RequestMapping(method = RequestMethod.POST)
	@ApiOperation(value = "创建云运维系统参数设置")
	public Object save(@RequestBody Interfaces entity) {
		Map<String, String> validatorMap = ValidatorUtils.validator(entity);
		if(validatorMap.isEmpty()) {
			entity.createdUser(this.getLoginUser());
			try {
				entity.setId(UUID.randomUUID().toString());
				interfacesBiz.add(entity);
				return BaseRestControl.tranReturnValue(ResultType.success, entity);	
			} catch (Exception e) {
				this.exception(this.getClass(), e);
				return BaseRestControl.tranReturnValue(ResultType.failure);
			}
		}
		return BaseRestControl.tranReturnValue(ResultType.parameter_error, validatorMap);
	}

	
	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	@ApiOperation(value = "更新云运维系统参数设置")
	public Object update(@PathVariable String id, @RequestBody Interfaces entity) throws IOException {
		Map<String, String> validatorMap = ValidatorUtils.validator(entity);
		if(validatorMap.isEmpty()) {
			Interfaces before = interfacesBiz.findById(Interfaces.class, id);
			if(before != null) {
				InvokeSetForm.copyFormProperties(entity, before);
				before.updatedUser(this.getLoginUser());
				try {
					interfacesBiz.update(before);
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

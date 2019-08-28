package com.h3c.iclouds.rest;

import com.h3c.iclouds.base.BaseRest;
import com.h3c.iclouds.base.BaseRestControl;
import com.h3c.iclouds.biz.InitCodeBiz;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageList;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.common.ResultType;
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

@Api(value = "系统基础数据", description = "系统基础数据")
@RestController
@RequestMapping("/initCode")
public class InitCodeRest extends BaseRest<InitCode> {
	
	@Resource
	private InitCodeBiz initCodeBiz;

	@Override
	@ApiOperation(value = "获取系统基础数据列表")
	@RequestMapping(method = RequestMethod.GET)
	public Object list() {
		PageEntity entity = this.beforeList();
		PageModel<InitCode> pageModel = initCodeBiz.findForPage(entity);
		PageList<InitCode> page = new PageList<InitCode>(pageModel, entity.getsEcho());
		return BaseRestControl.tranReturnValue(page);
	}
	
	@ApiOperation(value = "获取code对应的数据")
	@RequestMapping(value = "/{codeType}/sublist", method = RequestMethod.GET)
	public Object list(@PathVariable String codeType) {
		List<InitCode> list = this.initCodeBiz.findByPropertyName(InitCode.class, "codeTypeId", codeType);
		return BaseRestControl.tranReturnValue(list);
	}
	
	@RequestMapping(value = "/{id}/recover", method = RequestMethod.POST)
	@ApiOperation(value = "启用基础数据")
	public Object recover(@PathVariable String id) {
		InitCode entity = initCodeBiz.findById(InitCode.class, id);
		if(entity != null) {
			if(!ConfigProperty.YES.equals(entity.getStatus())) {
				entity.setStatus(ConfigProperty.YES);
				initCodeBiz.update(entity);
			}
			return BaseRestControl.tranReturnValue(ResultType.success);
		}
		return BaseRestControl.tranReturnValue(ResultType.deleted);
	}

	@Override
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@ApiOperation(value = "获取基础数据")
	public Object get(@PathVariable String id) {
		InitCode entity = initCodeBiz.findById(InitCode.class, id);
		if(entity != null) {
			return BaseRestControl.tranReturnValue(entity);
		}
		return BaseRestControl.tranReturnValue(ResultType.deleted);
	}
	
	@Override
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	@ApiOperation(value = "禁用基础数据")
	public Object delete(@PathVariable String id) {
		InitCode entity = initCodeBiz.findById(InitCode.class, id);
		if(entity != null) {
			if(!ConfigProperty.NO.equals(entity.getStatus())) {
				entity.setStatus(ConfigProperty.NO);
				initCodeBiz.update(entity);
			}
			return BaseRestControl.tranReturnValue(ResultType.success);
		}
		return BaseRestControl.tranReturnValue(ResultType.deleted);
	}

	@Override
	@RequestMapping(method = RequestMethod.POST)
	@ApiOperation(value = "创建基础数据")
	public Object save(@RequestBody InitCode entity) {
		entity.setStatus(ConfigProperty.YES);
		Map<String, String> validatorMap = ValidatorUtils.validator(entity);
		if(!validatorMap.isEmpty()) {
			return BaseRestControl.tranReturnValue(ResultType.parameter_error, validatorMap);
		}
		boolean repeat = this.initCodeBiz.checkRepeat(InitCode.class, StrUtils.createMap("codeId", entity.getCodeId()));
		if (!repeat) {
			return BaseRestControl.tranReturnValue(ResultType.asset_code_repeat);
		}
		entity.createdUser(this.getLoginUser());
		try {
			initCodeBiz.add(entity);
			return BaseRestControl.tranReturnValue(ResultType.success, entity);
		} catch (Exception e) {
			this.exception(this.getClass(), e);
			return BaseRestControl.tranReturnValue(ResultType.failure);
		}
	}

	@Override
	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	@ApiOperation(value = "更新基础数据")
	public Object update(@PathVariable String id, @RequestBody InitCode entity) throws IOException {
		Map<String, String> validatorMap = ValidatorUtils.validator(entity);
		if(validatorMap.isEmpty()) {
			InitCode before = initCodeBiz.findById(InitCode.class, id);
			if(before != null) {
				InvokeSetForm.copyFormProperties(entity, before);
				before.updatedUser(this.getLoginUser());
				try {
					initCodeBiz.update(before);
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
	
	@RequestMapping(value = "/check/{code}", method = RequestMethod.GET)
	@ApiOperation(value = "编码值验重")
	public Object check(@PathVariable String code) throws IOException {
		boolean repeat = this.initCodeBiz.checkRepeat(InitCode.class, StrUtils.createMap("codeId", code));
		if (!repeat) {
			return BaseRestControl.tranReturnValue(ResultType.asset_code_repeat);
		}
		return BaseRestControl.tranReturnValue(ResultType.success);
	}
	
}

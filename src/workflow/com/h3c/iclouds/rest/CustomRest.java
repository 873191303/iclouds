package com.h3c.iclouds.rest;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.h3c.iclouds.base.BaseRest;
import com.h3c.iclouds.base.BaseRestControl;
import com.h3c.iclouds.biz.CustomBiz;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageList;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.po.business.Custom;
import com.h3c.iclouds.utils.InvokeSetForm;
import com.h3c.iclouds.utils.StrUtils;
import com.h3c.iclouds.validate.ValidatorUtils;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/custom")
@Api(value = "客户管理", description = "客户管理")
public class CustomRest extends BaseRest<Custom> {
	
	@Resource
	private CustomBiz customBiz;

	@Override
	@ApiOperation(value = "分页获取客户列表")
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public Object list() {
		// 将查询内容存入map
		PageEntity entity = this.beforeList();
		PageModel<Custom> pageModel = customBiz.findForPage(entity);
		PageList<Custom> page = new PageList<Custom>(pageModel, entity.getsEcho());
		return BaseRestControl.tranReturnValue(page);
	}

	@Override
	@ApiOperation(value = "获取客户详细信息", response = Custom.class)
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public Object get(@PathVariable String id) {
		Custom entity = customBiz.findById(Custom.class, id);
		if (entity != null) {
			if(!entity.getOwner().equals(this.getSessionBean().getUserId())) {
				return BaseRestControl.tranReturnValue(ResultType.unAuthorized);
			}
			return BaseRestControl.tranReturnValue(entity);
		}
		return BaseRestControl.tranReturnValue(ResultType.deleted);
	}
	
	@ApiOperation(value = "恢复客户")
	@RequestMapping(value = "/{id}/recover", method = RequestMethod.PUT)
	public Object recover(@PathVariable String id) {
		Custom entity = customBiz.findById(Custom.class, id);
		if(entity != null) {
			if(!entity.getOwner().equals(this.getSessionBean().getUserId())) {
				return BaseRestControl.tranReturnValue(ResultType.unAuthorized);
			}
			try {
				if(entity.getStatus().equals(ConfigProperty.NO)) {
					entity.setStatus(ConfigProperty.YES);
					customBiz.update(entity);
				}
			} catch (Exception e) {
				this.exception(this.getClass(), e);
			}
			return BaseRestControl.tranReturnValue(ResultType.success);
		}
		return BaseRestControl.tranReturnValue(ResultType.deleted);
	}
	
	@Override
	@ApiOperation(value = "删除客户")
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public Object delete(@PathVariable String id) {
		Custom entity = customBiz.findById(Custom.class, id);
		if(entity != null) {
			if(!entity.getOwner().equals(this.getSessionBean().getUserId())) {
				return BaseRestControl.tranReturnValue(ResultType.unAuthorized);
			}
			try {
				customBiz.delete(entity);
			} catch (Exception e) {
				this.exception(this.getClass(), e);
				return BaseRestControl.tranReturnValue(ResultType.custom_in_use);
			}
			return BaseRestControl.tranReturnValue(ResultType.success);
		}
		return BaseRestControl.tranReturnValue(ResultType.deleted);
	}
	
	@ApiOperation(value = "禁用客户，并且删除客户下的联系人")
	@RequestMapping(value = "/{id}/", method = RequestMethod.PUT)
	public Object put(@PathVariable String id) {
		Custom entity = customBiz.findById(Custom.class, id);
		if(entity != null) {
			if(!entity.getOwner().equals(this.getSessionBean().getUserId())) {
				return BaseRestControl.tranReturnValue(ResultType.unAuthorized);
			}
			try {
				if(entity.getStatus().equals(ConfigProperty.YES)) {
					entity.setStatus(ConfigProperty.NO);
					customBiz.update(entity);
				}
			} catch (Exception e) {
				this.exception(this.getClass(), e);
			}
			return BaseRestControl.tranReturnValue(ResultType.success);
		}
		return BaseRestControl.tranReturnValue(ResultType.deleted);
	}

	@Override
	@ApiOperation(value = "保存新的客户信息", response = Custom.class)
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public Object save(@RequestBody Custom entity) {
		Map<String, String> validatorMap = ValidatorUtils.validator(entity);
		if(validatorMap.isEmpty()) {
			entity.setOwner(this.getLoginUser());	// 业务办理客户为提交人
			entity.createdUser(this.getLoginUser());
			entity.setStatus(ConfigProperty.YES);
			try {
				customBiz.add(entity);
				return BaseRestControl.tranReturnValue(ResultType.success, entity);
			} catch (Exception e) {
				this.exception(this.getClass(), e);
				return BaseRestControl.tranReturnValue(ResultType.failure);
			}
		}
		return BaseRestControl.tranReturnValue(ResultType.parameter_error);
	}

	@Override
	@ApiOperation(value = "修改客户信息", response = Custom.class)
	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	public Object update(@PathVariable String id, @RequestBody Custom entity) {
		Map<String, String> validatorMap = ValidatorUtils.validator(entity);
		if(validatorMap.isEmpty()) {
			Custom before = customBiz.findById(Custom.class, id);
			if(before != null) {
				if(!before.getOwner().equals(this.getSessionBean().getUserId())) {
					return BaseRestControl.tranReturnValue(ResultType.unAuthorized);
				}
				if(before.getStatus().equals(ConfigProperty.NO)) {
					return BaseRestControl.tranReturnValue(ResultType.custom_deleted);
				}
				
				InvokeSetForm.copyFormProperties(entity, before);
				before.updatedUser(this.getLoginUser());
				try {
					customBiz.update(before);
					return BaseRestControl.tranReturnValue(ResultType.success, before);	
				} catch (Exception e) {
					this.exception(this.getClass(), e);
					return BaseRestControl.tranReturnValue(ResultType.failure);
				}
			}
			return BaseRestControl.tranReturnValue(ResultType.deleted);
		}
		return BaseRestControl.tranReturnValue(ResultType.parameter_error);
	}
	
	
	
	@ApiOperation(value = "验证重复存在", response = Custom.class)
	@RequestMapping(value = "/check", method = RequestMethod.GET)
	public Object check(HttpServletRequest request) {
		String id=request.getParameter("id");
		String value=request.getParameter("value");
		if (!StrUtils.isNotEmpty(id)) {
			Map<String, Object> map1=new HashMap<String, Object>();
			map1.put("cusName", value);
			if (customBiz.checkRepeat(Custom.class, map1)) {
				return BaseRestControl.tranReturnValue(ResultType.success);
			}
			return BaseRestControl.tranReturnValue(ResultType.repeat);
		}
		Map<String, Object> map=new HashMap<>();
		map.put("id", id);
		
		map.put("cusName", value);
		if (customBiz.checkRepeat(Custom.class, map)) {
			return BaseRestControl.tranReturnValue(ResultType.success);
		}
		return BaseRestControl.tranReturnValue(ResultType.repeat);
	}
	@ApiOperation(value = "验证重复存在", response = Custom.class)
	@RequestMapping(value = "/check/cusName", method = RequestMethod.GET)
	public Object check1(HttpServletRequest request) {
		String id = request.getParameter("id");
		String value = request.getParameter("value");
		
		if(customBiz.checkRepeat(Custom.class,"cusName", value, id)) {
			return BaseRestControl.tranReturnValue(ResultType.success);
		}
		return BaseRestControl.tranReturnValue(ResultType.repeat);
	}
	
}

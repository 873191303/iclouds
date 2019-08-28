package com.h3c.iclouds.rest;

import com.h3c.iclouds.auth.Perms;
import com.h3c.iclouds.base.BaseRest;
import com.h3c.iclouds.base.BaseRestControl;
import com.h3c.iclouds.biz.ResourceBiz;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.po.Resource;
import com.h3c.iclouds.utils.InvokeSetForm;
import com.h3c.iclouds.utils.TreePick;
import com.h3c.iclouds.validate.ValidatorUtils;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/resource")
@Api(value = "资源管理", description = "资源管理")
public class ResourceRest extends BaseRest<Resource>{

	@javax.annotation.Resource
	private ResourceBiz resourceBiz;
	
	@RequestMapping(value = "/tree", method = RequestMethod.GET)
	@Perms(value = "sm.super.user")
	@ApiOperation(value = "获取资源树状列表")
	public Object list() {
		List<Resource> list = this.resourceBiz.getAll(Resource.class);
		List<Resource> root = TreePick.pickResource(list);
		return BaseRestControl.tranReturnValue(root);
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	@ApiOperation(value = "删除资源，如果存在子资源，会删除子资源")
	@Perms(value = "sm.super.user")
	public Object delete(@PathVariable String id) {
		Resource entity = resourceBiz.findById(Resource.class, id);
		if(entity != null) {
			if(!"1".equals(entity.getId())) {	// 开始菜单不允许删除
				try {
					resourceBiz.delete(entity);
				} catch (Exception e) {
					this.exception(this.getClass(), e);
					return BaseRestControl.tranReturnValue(ResultType.failure);
				}
				return BaseRestControl.tranReturnValue(ResultType.success);	
			} else {
				return BaseRestControl.tranReturnValue(ResultType.unAuthorized);		
			}
		}
		return BaseRestControl.tranReturnValue(ResultType.deleted);
	}
	
	@ApiOperation(value = "获取资源详细信息", response = Resource.class)
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@Perms(value = "sm.super.user")
	public Object get(@PathVariable String id) {
		Resource entity = resourceBiz.findById(Resource.class, id);
		if(entity != null) {
			return BaseRestControl.tranReturnValue(entity);
		}
		return BaseRestControl.tranReturnValue(ResultType.deleted);
	}
	
	@ApiOperation(value = "保存新的资源", response = Resource.class)
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	@Perms(value = "sm.super.user")
	public Object save(@RequestBody Resource entity) {
		Map<String, String> validatorMap = ValidatorUtils.validator(entity);
		if(validatorMap.isEmpty()) {
			if(entity.getDepth() == 1) {
				entity.setParentId("-1");
			} else {
				Resource parentResource = this.resourceBiz.findById(Resource.class, entity.getParentId());
				if(parentResource == null) {
					return BaseRestControl.tranReturnValue(ResultType.parameter_error, "parent_error");
				}
			}
			entity.createdUser(this.getLoginUser());
			try {
				resourceBiz.add(entity);
				return BaseRestControl.tranReturnValue(ResultType.success, entity);
			} catch (Exception e) {
				this.exception(this.getClass(), e);
				return BaseRestControl.tranReturnValue(ResultType.failure);
			}
		}
		return BaseRestControl.tranReturnValue(ResultType.parameter_error, validatorMap);
	}
	
	@ApiOperation(value = "修改资源", response = Resource.class)
	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	@Perms(value = "sm.super.user")
	public Object update(@PathVariable String id, @RequestBody Resource resource) {
		Map<String, String> validatorMap = ValidatorUtils.validator(resource);
		if(validatorMap.isEmpty()) {
			Resource entity = resourceBiz.findById(Resource.class, id);
			if(entity != null) {
				InvokeSetForm.copyFormProperties(resource, entity);
				entity.updatedUser(this.getLoginUser());
				try {
					resourceBiz.update(entity);
					return BaseRestControl.tranReturnValue(ResultType.success, entity);
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

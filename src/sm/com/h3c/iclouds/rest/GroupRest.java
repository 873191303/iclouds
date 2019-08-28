package com.h3c.iclouds.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.h3c.iclouds.auth.Perms;
import com.h3c.iclouds.base.BaseDAO;
import com.h3c.iclouds.base.BaseRest;
import com.h3c.iclouds.base.BaseRestControl;
import com.h3c.iclouds.biz.GroupBiz;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageList;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.po.Groups;
import com.h3c.iclouds.po.User2Group;
import com.h3c.iclouds.utils.InvokeSetForm;
import com.h3c.iclouds.validate.ValidatorUtils;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

@Api(value = "群组管理", description = "群组管理")
@RestController
@RequestMapping("/group")
public class GroupRest extends BaseRest<Groups> {
	
	@Resource
	private GroupBiz groupBiz;
	
	@Resource(name = "baseDAO")
	private BaseDAO<User2Group> userToGroupDao;
	
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	@Perms(value = "sm.ope.group")
	@ApiOperation(value = "分页获取群组列表")
	public Object list() {
		PageEntity entity = this.beforeList();
		PageModel<Groups> pageModel = groupBiz.findForPage(entity);
		PageList<Groups> page = new PageList<Groups>(pageModel, entity.getsEcho());
		return BaseRestControl.tranReturnValue(page);
	}
	
	@ApiOperation(value = "获取群组基本列表")
	@RequestMapping(value="/sublist", method = RequestMethod.GET)
	@Perms(value = "sm.ope.group")
	public Object listForSelect(){
		List<Groups> groupList = groupBiz.findByClazz(Groups.class, "id", "groupName");
		return BaseRestControl.tranReturnValue(groupList);
	}
	
	@ApiOperation(value = "删除群组")
	@Perms(value = "sm.ope.group")
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public Object delete(@PathVariable String id) {
		Groups entity = groupBiz.findById(Groups.class, id);
		if(entity != null) {
			try {
				if(entity.getFlag().equals(ConfigProperty.YES)) {
					return BaseRestControl.tranReturnValue(ResultType.unAuthorized);
				}
				
				groupBiz.delete(entity);
			} catch (Exception e) {
				this.exception(this.getClass(), e);
				return BaseRestControl.tranReturnValue(ResultType.failure);
			}
			return BaseRestControl.tranReturnValue(ResultType.success);
		}
		return BaseRestControl.tranReturnValue(ResultType.deleted);
	}
	
	@ApiOperation(value = "获取群组详细信息", response = Groups.class)
	@Perms(value = "sm.ope.group")
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public Object get(@PathVariable String id) {
		Groups entity = groupBiz.findById(Groups.class, id);
		if(entity != null) {
			return BaseRestControl.tranReturnValue(entity);
		}
		return BaseRestControl.tranReturnValue(ResultType.deleted);
	}
	
	@ApiOperation(value = "保存新的群组", response = Groups.class)
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	@Perms(value = "sm.ope.group")
	public Object save(@RequestBody Groups entity) {
		Map<String, String> validatorMap = ValidatorUtils.validator(entity);
		if(validatorMap.isEmpty()) {
			entity.createdUser(this.getLoginUser());
			try {
				entity.setFlag(ConfigProperty.NO);
				groupBiz.add(entity);
				return BaseRestControl.tranReturnValue(ResultType.success, entity);	
			} catch (Exception e) {
				this.exception(this.getClass(), e);
				return BaseRestControl.tranReturnValue(ResultType.failure);
			}
		}
		return BaseRestControl.tranReturnValue(ResultType.parameter_error, validatorMap);
	}
	
	@ApiOperation(value = "修改群组", response = Groups.class)
	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	@Perms(value = "sm.ope.group")
	public Object update(@PathVariable String id, @RequestBody Groups before) {
		Map<String, String> validatorMap = ValidatorUtils.validator(before);
		if(validatorMap.isEmpty()) {
			Groups entity = groupBiz.findById(Groups.class, id);
			if(entity != null) {
				if(entity.getFlag().equals(ConfigProperty.YES)) {
					return BaseRestControl.tranReturnValue(ResultType.unAuthorized);
				}
				
				InvokeSetForm.copyFormProperties(before, entity);
				entity.updatedUser(this.getLoginUser());
				try {
					groupBiz.update(entity);
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
	
	@ApiOperation(value = "保存群组归属用户")
	@Perms(value = "sm.ope.group")
	@RequestMapping(value = "/{id}/user", method = RequestMethod.POST)
	public Object resourcePost(@PathVariable String id, @RequestBody Map<String, Object> map) {
		Groups entity = this.groupBiz.findById(Groups.class, id);
		if(entity != null) {
			try {
				return BaseRestControl.tranReturnValue(this.groupBiz.update(entity, map));	
			} catch (Exception e) {
				this.exception(this.getClass(), e);
				return BaseRestControl.tranReturnValue(ResultType.failure);
			}
		}
		return BaseRestControl.tranReturnValue(ResultType.deleted);
	}	
	
	@ApiOperation(value = "获取群组归属用户")
	@Perms(value = "sm.ope.group")
	@RequestMapping(value = "/{id}/user", method = RequestMethod.GET)
	public Object resourceGet(@PathVariable String id) {
		Groups group = this.groupBiz.findById(Groups.class, id);
		if(group != null) {
			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
			List<User2Group> u2gList = userToGroupDao.findByPropertyName(User2Group.class, "gid", id);
			if(u2gList != null && !u2gList.isEmpty()) {
				for (User2Group entity : u2gList) {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("userId", entity.getUserId());
					list.add(map);
				}
			}
			return BaseRestControl.tranReturnValue(list);
		}
		return BaseRestControl.tranReturnValue(ResultType.deleted);
	}

}

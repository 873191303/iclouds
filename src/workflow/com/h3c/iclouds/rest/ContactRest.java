package com.h3c.iclouds.rest;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.h3c.iclouds.base.BaseChildRest;
import com.h3c.iclouds.base.BaseRestControl;
import com.h3c.iclouds.biz.ContactBiz;
import com.h3c.iclouds.biz.CustomBiz;
import com.h3c.iclouds.biz.RequestMasterBiz;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageList;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.po.business.Contact;
import com.h3c.iclouds.po.business.Custom;
import com.h3c.iclouds.po.business.RequestMaster;
import com.h3c.iclouds.utils.InvokeSetForm;
import com.h3c.iclouds.utils.StrUtils;
import com.h3c.iclouds.validate.ValidatorUtils;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/{customId}/contact")
@Api(value = "客户联系人管理", description = "客户联系人管理")
public class ContactRest extends BaseChildRest<Contact> {
	
	@Resource
	private ContactBiz contactBiz;
	
	@Resource
	private CustomBiz customBiz;
	
	@Resource
	private RequestMasterBiz requestMasterBiz;

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	@ApiOperation(value = "分页获取用户列表")
	public Object list(@PathVariable String customId) {
		Custom custom = customBiz.findById(Custom.class, customId);
		if(custom == null) {
			return BaseRestControl.tranReturnValue(ResultType.custom_deleted);
		}
		
		// 将查询内容存入map
		PageEntity entity = this.beforeList();
		entity.setSpecialParam(customId);	// 设置客户id		
		PageModel<Contact> pageModel = contactBiz.findForPage(entity);
		PageList<Contact> page = new PageList<Contact>(pageModel, entity.getsEcho());
		return BaseRestControl.tranReturnValue(page);
	}

	@ApiOperation(value = "获取联系人详细信息", response = Contact.class)
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public Object get(@PathVariable String customId, @PathVariable String id) {
		Custom custom = customBiz.findById(Custom.class, customId);
		if(custom == null) {
			return BaseRestControl.tranReturnValue(ResultType.custom_deleted);
		}
		if(!custom.getOwner().equals(this.getLoginUser())) {
			return BaseRestControl.tranReturnValue(ResultType.unAuthorized);
		}
		
		Contact entity = contactBiz.findById(Contact.class, id);
		if (entity != null) {
			if(!entity.getCusId().equals(customId)) {
				return BaseRestControl.tranReturnValue(ResultType.parameter_error);
			}
			return BaseRestControl.tranReturnValue(entity);
		}
		return BaseRestControl.tranReturnValue(ResultType.deleted);
	}

	@ApiOperation(value = "删除联系人")
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public Object delete(@PathVariable String customId, @PathVariable String id) {
		Custom custom = customBiz.findById(Custom.class, customId);
		if(custom == null) {
			return BaseRestControl.tranReturnValue(ResultType.custom_deleted);
		}
		if(!custom.getOwner().equals(this.getLoginUser())) {
			return BaseRestControl.tranReturnValue(ResultType.unAuthorized);
		}
		
		Contact entity = contactBiz.findById(Contact.class, id);
		if(entity != null) {
			if(!entity.getCusId().equals(customId)) {
				return BaseRestControl.tranReturnValue(ResultType.parameter_error);
			}
			int count = this.requestMasterBiz.findCountByPropertyName(RequestMaster.class, "contact", id);
			if(count > 0) {
				return BaseRestControl.tranReturnValue(ResultType.contact_in_use);
			}
			try {
				contactBiz.delete(entity);
			} catch (Exception e) {
				this.exception(e, "Delete contact failure. value" + StrUtils.toJSONString(entity));
				return BaseRestControl.tranReturnValue(ResultType.failure);
			}
			return BaseRestControl.tranReturnValue(ResultType.success);
		}
		return BaseRestControl.tranReturnValue(ResultType.deleted);
	}

	@ApiOperation(value = "保存新的联系人信息", response = Contact.class)
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public Object save(@PathVariable String customId, @RequestBody Contact entity) {
		Map<String, String> validatorMap = ValidatorUtils.validator(entity);
		if(validatorMap.isEmpty()) {
			Custom custom = customBiz.findById(Custom.class, customId);
			if(custom != null) {
				if(!custom.getOwner().equals(this.getLoginUser())) {	// 修改人必须为owner
					return BaseRestControl.tranReturnValue(ResultType.unAuthorized); 
				}
				entity.setCusId(customId);
				entity.setCustom(custom);
				entity.setOwner(this.getLoginUser());
				entity.createdUser(this.getLoginUser());
				try {
					contactBiz.add(entity);
					return BaseRestControl.tranReturnValue(ResultType.success, entity);
				} catch (Exception e) {
					this.exception(e, "Save new contact failure, value:" + StrUtils.toJSONString(entity));
					return BaseRestControl.tranReturnValue(ResultType.failure);
				}
			}
			return BaseRestControl.tranReturnValue(ResultType.custom_deleted);
		}
		return BaseRestControl.tranReturnValue(ResultType.parameter_error);
	}

	@ApiOperation(value = "修改联系人信息", response = Contact.class)
	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	public Object update(@PathVariable String customId, @PathVariable String id, @RequestBody Contact entity) {
		Map<String, String> validatorMap = ValidatorUtils.validator(entity);
		if(validatorMap.isEmpty()) {
			Custom custom = customBiz.findById(Custom.class, customId);
			if(custom == null) {
				return BaseRestControl.tranReturnValue(ResultType.custom_deleted);
			}
			if(!custom.getOwner().equals(this.getLoginUser())) {
				return BaseRestControl.tranReturnValue(ResultType.unAuthorized);
			}
			
			Contact before = contactBiz.findById(Contact.class, id);
			if(before != null) {
				if(!before.getCusId().equals(customId)) {
					return BaseRestControl.tranReturnValue(ResultType.parameter_error);
				}
				InvokeSetForm.copyFormProperties(entity, before);
				before.createdUser(this.getLoginUser());
				before.setCusId(customId); 
				before.setCustom(custom);
				try {
					contactBiz.update(before);
					return BaseRestControl.tranReturnValue(ResultType.success, before);	
				} catch (Exception e) {
					this.exception(e, "Update contact failure, value:" + StrUtils.toJSONString(entity));
					return BaseRestControl.tranReturnValue(ResultType.failure);
				}
			}
			return BaseRestControl.tranReturnValue(ResultType.deleted);
		}
		return BaseRestControl.tranReturnValue(ResultType.parameter_error);
	}

}

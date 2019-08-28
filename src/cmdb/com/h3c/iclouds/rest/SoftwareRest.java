package com.h3c.iclouds.rest;

import java.io.IOException;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.h3c.iclouds.utils.StrUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.h3c.iclouds.base.BaseRest;
import com.h3c.iclouds.base.BaseRestControl;
import com.h3c.iclouds.biz.SoftwareBiz;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageList;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.po.Software;
import com.h3c.iclouds.utils.InvokeSetForm;
import com.h3c.iclouds.validate.ValidatorUtils;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

@Api(value = "软件", description = "软件")
@RestController
@RequestMapping("/software")
public class SoftwareRest extends BaseRest<Software> {
	
	@Resource
	private SoftwareBiz softwareBiz;

	@Override
	@ApiOperation(value = "获取软件列表")
	@RequestMapping(method = RequestMethod.GET)
	public Object list() {
		PageEntity entity = this.beforeList();
		PageModel<Software> pageModel = softwareBiz.findForPage(entity);
		PageList<Software> page = new PageList<Software>(pageModel, entity.getsEcho());
		return BaseRestControl.tranReturnValue(page);
	}

	@Override
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@ApiOperation(value = "获取软件")
	public Object get(@PathVariable String id) {
		Software entity = softwareBiz.findById(Software.class, id);
		if(entity != null) {
			// 获取安装列表
			
			return BaseRestControl.tranReturnValue(entity);
		}
		return BaseRestControl.tranReturnValue(ResultType.deleted);
	}

	@Override
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	@ApiOperation(value = "删除软件")
	public Object delete(@PathVariable String id) {
		Software entity = softwareBiz.findById(Software.class, id);
		if(entity != null) {
			try {
				if(ConfigProperty.CMDB_SOFTWARE_FLAG1_DRAFT.equals(entity.getStatus())) {	// 草稿状态可以删除对应数据
					softwareBiz.delete(entity);
				} else {
					entity.setStatus(ConfigProperty.CMDB_SOFTWARE_FLAG3_UNUSE);	// 设置为不使用状态
					this.softwareBiz.update(entity);
				}
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
	@ApiOperation(value = "创建软件")
	public Object save(@RequestBody Software entity) {
		Map<String, String> validatorMap = ValidatorUtils.validator(entity);
		if(validatorMap.isEmpty()) {
			entity.createdUser(this.getLoginUser());
			try {
				if(ConfigProperty.CMDB_SOFTWARE_FLAG3_UNUSE.equals(entity.getStatus())) {
					return BaseRestControl.tranReturnValue(ResultType.software_status_error);
				}
				
				// 验证资产编号是否重复
				if(!softwareBiz.checkRepeat(Software.class, "scode", entity.getScode())) {
					return ResultType.software_assetId_repeat;
				}
				softwareBiz.add(entity);
				return BaseRestControl.tranReturnValue(ResultType.success, entity);	
			} catch (Exception e) {
				this.exception(this.getClass(), e);
				return BaseRestControl.tranReturnValue(ResultType.failure);
			}
		}
		return BaseRestControl.tranReturnValue(ResultType.parameter_error, validatorMap);
	}
	
	@ApiOperation(value = "验证资产编号是否存在，【修改的时候请传递id，用于排除本身】")
	@RequestMapping(value = "/check/scode", method = RequestMethod.GET)
	public Object check(HttpServletRequest req) {
		String id = req.getParameter("id");
		String value = req.getParameter("value");
		if(this.softwareBiz.checkRepeat(Software.class, "scode", value, id)) {
			return BaseRestControl.tranReturnValue(ResultType.success);
		}
		return BaseRestControl.tranReturnValue(ResultType.repeat);
	}

	@Override
	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	@ApiOperation(value = "更新软件")
	public Object update(@PathVariable String id, @RequestBody Software entity) throws IOException {
		Map<String, String> validatorMap = ValidatorUtils.validator(entity);
		if(validatorMap.isEmpty()) {
			Software before = this.softwareBiz.findById(Software.class, id);
			if(before != null) {
				try {
					if (!before.getStatus().equals(entity.getStatus())) {
						//草稿和已退库只能修改为使用中;使用中只能修改为停用
						if(before.getStatus().equals(ConfigProperty.CMDB_SOFTWARE_FLAG2_USE)) {
							if (!ConfigProperty.CMDB_SOFTWARE_FLAG3_UNUSE.equals(entity.getStatus())) {
								return ResultType.status_translate_error;
							}
						} else if (StrUtils.equals(before.getStatus(), ConfigProperty.CMDB_SOFTWARE_FLAG1_DRAFT, ConfigProperty.CMDB_SOFTWARE_FLAG3_UNUSE)) {
							if (!ConfigProperty.CMDB_SOFTWARE_FLAG2_USE.equals(entity.getStatus())) {
								return ResultType.status_translate_error;
							}
						}
					}
					if(ConfigProperty.CMDB_SOFTWARE_FLAG2_USE.equals(before.getStatus())) {
						entity.setStatus(ConfigProperty.CMDB_SOFTWARE_FLAG2_USE);
					}
					
					// 验证资产编号是否重复
					if(!softwareBiz.checkRepeat(Software.class, "scode", entity.getScode(), entity.getId())) {
						return ResultType.software_assetId_repeat;
					}
					
					InvokeSetForm.copyFormProperties(entity, before);
					entity.updatedUser(this.getLoginUser());
					this.softwareBiz.update(before);
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

}

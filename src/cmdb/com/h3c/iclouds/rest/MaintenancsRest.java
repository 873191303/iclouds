package com.h3c.iclouds.rest;

import java.io.IOException;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.h3c.iclouds.base.BaseChildRest;
import com.h3c.iclouds.base.BaseRestControl;
import com.h3c.iclouds.biz.AsmMasterBiz;
import com.h3c.iclouds.biz.MaintenancsBiz;
import com.h3c.iclouds.biz.SoftwareBiz;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageList;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.po.AsmMaster;
import com.h3c.iclouds.po.Maintenancs;
import com.h3c.iclouds.po.Software;
import com.h3c.iclouds.utils.InvokeSetForm;
import com.h3c.iclouds.validate.ValidatorUtils;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

@Api(value = "资产库管理资产维保信息", description = "资产库管理资产维保信息")
@RestController
@RequestMapping("/asm/master/{pid}/maintenancs")
public class MaintenancsRest extends BaseChildRest<Maintenancs> {
	
	@Resource
	private MaintenancsBiz maintenancsBiz;

	@Resource
	private AsmMasterBiz asmMasterBiz;

	@Resource
	private SoftwareBiz softwareBiz;


	@Override
	@ApiOperation(value = "获取资产管理维保信息列表", response = Maintenancs.class)
	@RequestMapping(method = RequestMethod.GET)
	public Object list(@PathVariable String pid) {
		PageEntity entity = this.beforeList();
		entity.setSpecialParam(pid);
		PageModel<Maintenancs> pageModel = maintenancsBiz.findForPage(entity);
		PageList<Maintenancs> page = new PageList<Maintenancs>(pageModel, entity.getsEcho());
		return BaseRestControl.tranReturnValue(page);
	}

	@Override
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@ApiOperation(value = "获取资产管理维保信息详细信息", response = Maintenancs.class)
	public Object get(@PathVariable String pid, @PathVariable String id) {
		Maintenancs entity = maintenancsBiz.findById(Maintenancs.class, id);
		if(entity != null) {
			if(!entity.getAssId().equals(pid)) {
				return BaseRestControl.tranReturnValue(ResultType.parameter_error);
			}
			return BaseRestControl.tranReturnValue(entity);
		}
		return BaseRestControl.tranReturnValue(ResultType.deleted);
	}
	
	@Override
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	@ApiOperation(value = "删除资产管理维保信息", response = Maintenancs.class)
	public Object delete(@PathVariable String pid, @PathVariable String id) {
		Maintenancs entity = maintenancsBiz.findById(Maintenancs.class, id);
		if(entity != null) {
			if(!entity.getAssId().equals(pid)) {
				return BaseRestControl.tranReturnValue(ResultType.parameter_error);
			}
			maintenancsBiz.deleteById(Maintenancs.class, id);
			return BaseRestControl.tranReturnValue(ResultType.success);
		}
		return BaseRestControl.tranReturnValue(ResultType.deleted);
	}

	@Override
	@RequestMapping(method = RequestMethod.POST)
	@ApiOperation(value = "创建资产管理维保信息", response = Maintenancs.class)
	public Object save(@PathVariable String pid, @RequestBody Maintenancs entity) {
		Map<String, String> validatorMap = ValidatorUtils.validator(entity);
		if(validatorMap.isEmpty()) {
			AsmMaster asmMaster = asmMasterBiz.findById(AsmMaster.class, pid);
			Software software = softwareBiz.findById(Software.class, pid);
			if(asmMaster == null && software == null) {//验证硬件资产和软件资产
				return BaseRestControl.tranReturnValue(ResultType.parent_code_not_exist);
			}
			entity.createdUser(this.getLoginUser());
			try {
				entity.setAssId(pid);
				maintenancsBiz.add(entity);
				return BaseRestControl.tranReturnValue(ResultType.success, entity);
			} catch (Exception e) {
				return BaseRestControl.tranReturnValue(ResultType.failure);
			}
		}
		return BaseRestControl.tranReturnValue(ResultType.parameter_error, validatorMap);
	}

	@Override
	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	@ApiOperation(value = "更新资产管理维保信息", response = Maintenancs.class)
	public Object update(@PathVariable String pid, @PathVariable String id, @RequestBody Maintenancs entity) throws IOException {
		Map<String, String> validatorMap = ValidatorUtils.validator(entity);
		if(validatorMap.isEmpty()) {
			Maintenancs before = maintenancsBiz.findById(Maintenancs.class, id);
			if(before != null) {
				if(!before.getAssId().equals(pid)) {
					return BaseRestControl.tranReturnValue(ResultType.parameter_error);
				}
				InvokeSetForm.copyFormProperties(entity, before);
				before.updatedUser(this.getLoginUser());
				try {
					maintenancsBiz.update(before);
					return BaseRestControl.tranReturnValue(ResultType.success, entity);
				} catch (Exception e) {
					this.exception(getClass(), e);
					return BaseRestControl.tranReturnValue(ResultType.failure);
				}
			}
			return BaseRestControl.tranReturnValue(ResultType.deleted);
		}
		return BaseRestControl.tranReturnValue(ResultType.parameter_error, validatorMap);
	}

}

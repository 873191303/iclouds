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
import com.h3c.iclouds.biz.Master2LogBiz;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageList;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.po.AsmMaster;
import com.h3c.iclouds.po.Master2Log;
import com.h3c.iclouds.utils.InvokeSetForm;
import com.h3c.iclouds.validate.ValidatorUtils;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

@Api(value = "资产管理设备变更日志", description = "资产管理设备变更日志")
@RestController
@RequestMapping("/asm/master/{pid}/master2Log")
public class Master2LogRest extends BaseChildRest<Master2Log> {

	@Resource
	private Master2LogBiz master2LogBiz;

	@Resource
	private AsmMasterBiz asmMasterBiz;

	@Override
	@ApiOperation(value = "获取资产管理变更日志列表", response = Master2Log.class)
	@RequestMapping(method = RequestMethod.GET)
	public Object list(@PathVariable String pid) {
		PageEntity entity = this.beforeList();
		entity.setSpecialParam(pid);
		PageModel<Master2Log> pageModel = master2LogBiz.findForPage(entity);
		PageList<Master2Log> page = new PageList<Master2Log>(pageModel, entity.getsEcho());
		return BaseRestControl.tranReturnValue(page);
	}

	@Override
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@ApiOperation(value = "获取资产管理变更日志详细信息", response = Master2Log.class)
	public Object get(@PathVariable String pid, @PathVariable String id) {
		Master2Log entity = master2LogBiz.findById(Master2Log.class, id);
		if(entity != null) {
			if(!entity.getAssetId().equals(pid)) {
				return BaseRestControl.tranReturnValue(ResultType.parameter_error);
			}
			return BaseRestControl.tranReturnValue(entity);
		}
		return BaseRestControl.tranReturnValue(ResultType.deleted);
	}
	
	@Override
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	@ApiOperation(value = "删除资产管理变更日志", response = Master2Log.class)
	public Object delete(@PathVariable String pid, @PathVariable String id) {
		Master2Log entity = master2LogBiz.findById(Master2Log.class, id);
		if(entity != null) {
			if(!entity.getAssetId().equals(pid)) {
				return BaseRestControl.tranReturnValue(ResultType.parameter_error);
			}
			String flag = entity.getFlag();
			if (ConfigProperty.YES.equals(flag)){//已确认状态不能删除
				return BaseRestControl.tranReturnValue(ResultType.asset_flag_error);
			}
			master2LogBiz.deleteById(Master2Log.class, id);
			return BaseRestControl.tranReturnValue(ResultType.success);
		}
		return BaseRestControl.tranReturnValue(ResultType.deleted);
	}

	@Override
	@RequestMapping(method = RequestMethod.POST)
	@ApiOperation(value = "创建资产管理变更日志", response = Master2Log.class)
	public Object save(@PathVariable String pid, @RequestBody Master2Log entity) {
		Map<String, String> validatorMap = ValidatorUtils.validator(entity);
		if(validatorMap.isEmpty()) {
			AsmMaster asmMaster = asmMasterBiz.findById(AsmMaster.class, pid);
			if(null == asmMaster) {
				return BaseRestControl.tranReturnValue(ResultType.parent_code_not_exist);
			}
			entity.createdUser(this.getLoginUser());
			try {
				entity.setAssetId(pid);
				master2LogBiz.add(entity);
				return BaseRestControl.tranReturnValue(ResultType.success, entity);
			} catch (Exception e) {
				this.exception(getClass(), e);
				return BaseRestControl.tranReturnValue(ResultType.failure);
			}
		}
		return BaseRestControl.tranReturnValue(ResultType.parameter_error, validatorMap);
	}

	@Override
	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	@ApiOperation(value = "更新资产管理变更日志", response = Master2Log.class)
	public Object update(@PathVariable String pid, @PathVariable String id, @RequestBody Master2Log entity) throws IOException {
		Map<String, String> validatorMap = ValidatorUtils.validator(entity);
		if(validatorMap.isEmpty()) {
			Master2Log before = master2LogBiz.findById(Master2Log.class, id);
			if(before != null) {
				if(!before.getAssetId().equals(pid)) {
					return BaseRestControl.tranReturnValue(ResultType.parameter_error);
				}
				String flag = before.getFlag();//获取数据的确认状态
				if (ConfigProperty.YES.equals(flag)){//已确认状态下不能修改
					return BaseRestControl.tranReturnValue(ResultType.asset_flag_error);
				}
				InvokeSetForm.copyFormProperties(entity, before);
				before.updatedUser(this.getLoginUser());
				try {
					master2LogBiz.update(before);
					return BaseRestControl.tranReturnValue(ResultType.success, entity);
				} catch (Exception e) {
					return BaseRestControl.tranReturnValue(ResultType.failure);
				}
			}
			return BaseRestControl.tranReturnValue(ResultType.deleted);
		}
		return BaseRestControl.tranReturnValue(ResultType.parameter_error, validatorMap);
	}


}

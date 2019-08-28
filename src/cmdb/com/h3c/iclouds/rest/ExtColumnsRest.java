package com.h3c.iclouds.rest;

import java.io.IOException;
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
import com.h3c.iclouds.biz.ExtAValueBiz;
import com.h3c.iclouds.biz.ExtColumnsBiz;
import com.h3c.iclouds.biz.InitCodeBiz;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageList;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.po.ExtAValue;
import com.h3c.iclouds.po.ExtColumns;
import com.h3c.iclouds.po.InitCode;
import com.h3c.iclouds.utils.InvokeSetForm;
import com.h3c.iclouds.utils.StrUtils;
import com.h3c.iclouds.validate.ValidatorUtils;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

@Api(value = "资产管理扩展属性列", description = "资产管理扩展属性列")
@RestController
@RequestMapping("/initCode")
public class ExtColumnsRest extends BaseChildRest<ExtColumns> {
	
	@Resource
	private ExtColumnsBiz extColumnsBiz;
	
	@Resource
	private ExtAValueBiz extAValueBiz;
	@Resource
	
	private InitCodeBiz initCodeBiz;

	@Override
	@ApiOperation(value = "资产管理扩展属性列列表")
	@RequestMapping(value = "/{pid}/columns", method = RequestMethod.GET)
	public Object list(@PathVariable String pid) {
		PageEntity entity = this.beforeList();
		entity.setSpecialParam(pid);
		PageModel<ExtColumns> pageModel = extColumnsBiz.findForPage(entity);
		PageList<ExtColumns> page = new PageList<ExtColumns>(pageModel, entity.getsEcho());
		return BaseRestControl.tranReturnValue(page);
	}
	
	
	@ApiOperation(value = "获取扩展属性列名")
	@RequestMapping(value = "/{pid}/columnsName", method = RequestMethod.GET)
	public Object listName(@PathVariable String pid) {
		List<InitCode> initCodes=initCodeBiz.findByPropertyName(InitCode.class, "codeId", pid);
		if(!StrUtils.checkParam(initCodes)){
			return BaseRestControl.tranReturnValue(ResultType.parameter_error);
		}
		String assType=initCodes.get(0).getId();
		List<ExtColumns> extColumns=extColumnsBiz.findByPropertyName(ExtColumns.class, "assType", assType);
		return BaseRestControl.tranReturnValue(extColumns);
	}

	@Override
	@RequestMapping(value = "/{pid}/columns/{id}", method = RequestMethod.GET)
	@ApiOperation(value = "资产管理扩展属性列")
	public Object get(@PathVariable String pid, @PathVariable String id) {
		ExtColumns entity = extColumnsBiz.findById(ExtColumns.class, id);
		if(entity != null) {
			if(!entity.getAssType().equals(pid)) {
				return BaseRestControl.tranReturnValue(ResultType.parameter_error);
			}
			return BaseRestControl.tranReturnValue(entity);
		}
		return BaseRestControl.tranReturnValue(ResultType.deleted);
	}

	/**
	 * 通过资产类型获取资产类型id
	 * @param pid
	 * @return
	 */
	public Object getAssetType(String pid) {
		List<InitCode> initCodes=initCodeBiz.findByPropertyName(InitCode.class, "codeName", pid);
		if(StrUtils.checkCollection(initCodes)){
			return initCodes.get(0).getId();
		}
		return null;
	}
	
	@Override
	@RequestMapping(value = "/{pid}/columns/{id}", method = RequestMethod.DELETE)
	@ApiOperation(value = "删除资产管理扩展属性列")
	public Object delete(@PathVariable String pid, @PathVariable String id) {
		ExtColumns entity = extColumnsBiz.findById(ExtColumns.class, id);
		if(entity != null) {
			if(!entity.getAssType().equals(pid)) {
				return BaseRestControl.tranReturnValue(ResultType.parameter_error);
			}
			List<ExtAValue> extAValue=extAValueBiz.findByPropertyName(ExtAValue.class, "extID", entity.getId());
			if(!StrUtils.checkParam(extAValue)){
				return BaseRestControl.tranReturnValue(ResultType.parameter_error);
			}
			extAValueBiz.delete(extAValue.get(0)); // 删除属性值
			extColumnsBiz.delete(entity);          // 删除属性列
			return BaseRestControl.tranReturnValue(ResultType.success);
		}
		return BaseRestControl.tranReturnValue(ResultType.deleted);
	}

	@Override
	@RequestMapping(value = "/{pid}/columns", method = RequestMethod.POST)
	@ApiOperation(value = "创建资产管理扩展属性列")
	public Object save(@PathVariable String pid, @RequestBody ExtColumns entity) {
		Map<String, String> validatorMap = ValidatorUtils.validator(entity);
		if(validatorMap.isEmpty()) {
			InitCode initCode = initCodeBiz.findById(InitCode.class, pid);
			if(initCode == null) {
				return BaseRestControl.tranReturnValue(ResultType.parameter_error);
			}
			entity.createdUser(this.getLoginUser());
			try {
				entity.setAssType(pid);
				extColumnsBiz.add(entity);
				return BaseRestControl.tranReturnValue(ResultType.success, entity);
			} catch (Exception e) {
				this.exception(this.getClass(), e);
				return BaseRestControl.tranReturnValue(ResultType.failure);
			}
		}
		return BaseRestControl.tranReturnValue(ResultType.parameter_error);
	}

	@Override
	@RequestMapping(value = "/{pid}/columns/{id}", method = RequestMethod.PUT)
	@ApiOperation(value = "更新资产管理扩展属性列")
	public Object update(@PathVariable String pid, @PathVariable String id, @RequestBody ExtColumns entity) throws IOException {
		Map<String, String> validatorMap = ValidatorUtils.validator(entity);
		if(validatorMap.isEmpty()) {
			ExtColumns before = extColumnsBiz.findById(ExtColumns.class, id);
			if(before != null) {
				if(!before.getAssType().equals(pid)) {
					return BaseRestControl.tranReturnValue(ResultType.parameter_error);
				}
				InvokeSetForm.copyFormProperties(entity, before);
				before.updatedUser(this.getLoginUser());
				try {
					extColumnsBiz.update(before);
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

}

package com.h3c.iclouds.rest;

import java.io.IOException;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.h3c.iclouds.base.BaseRest;
import com.h3c.iclouds.base.BaseRestControl;
import com.h3c.iclouds.biz.WorkRoleBiz;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageList;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.po.WorkRole;
import com.h3c.iclouds.validate.ValidatorUtils;

@RestController
@RequestMapping("/workRole")
public class WorkRoleRest extends BaseRest<WorkRole> {
	
	@Resource
	private WorkRoleBiz workRoleBiz;
	
	@RequestMapping(value="/list", method = RequestMethod.GET)
	public Object list() {
		// 将查询内容存入map
		PageEntity entity = this.beforeList("workFlowId");
		PageModel<WorkRole> pageModel = workRoleBiz.findForPage(entity);
		PageList<WorkRole> page = new PageList<WorkRole>(pageModel, entity.getsEcho());
		return BaseRestControl.tranReturnValue(page);
	}
	
	@RequestMapping(value="/{id}", method = RequestMethod.GET)
	public Object get(@PathVariable String id){
		WorkRole entity = workRoleBiz.findById(WorkRole.class, id);
		if(entity != null) {
			// 还有审批备注未添加
			return BaseRestControl.tranReturnValue(entity);
		}
		return BaseRestControl.tranReturnValue(ResultType.deleted);
	}
	
	@RequestMapping(value="/{id}", method = RequestMethod.PUT)
	public Object update(@PathVariable String id, @RequestBody WorkRole before) throws IOException {
		Map<String, String> validatorMap = ValidatorUtils.validator(before);
		if(validatorMap.isEmpty()) {
			WorkRole entity = this.workRoleBiz.findById(WorkRole.class, id);
			if(entity != null) {
				entity.setRoleId(before.getRoleId());
				entity.updatedUser(this.getLoginUser());
				this.workRoleBiz.update(entity);
				return BaseRestControl.tranReturnValue(ResultType.success);	// 操作项不存在;
			}
			return BaseRestControl.tranReturnValue(ResultType.deleted);	// 操作项不存在;
		}
		return BaseRestControl.tranReturnValue(ResultType.parameter_error);	// 操作项不存在;
	}

	@Override
	public Object delete(String id) {
		return null;
	}

	@Override
	public Object save(WorkRole t) {
		return null;
	}

}
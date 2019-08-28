package com.h3c.iclouds.rest;

import java.util.List;
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
import com.h3c.iclouds.biz.DepartmentBiz;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.po.Department;
import com.h3c.iclouds.utils.InvokeSetForm;
import com.h3c.iclouds.utils.StrUtils;
import com.h3c.iclouds.utils.TreePick;
import com.h3c.iclouds.validate.ValidatorUtils;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

@Api(value = "部门管理", description = "部门管理")
@RestController
@RequestMapping("/department")
public class DepartmentRest extends BaseRest<Department> {

	@Resource
	private DepartmentBiz departmentBiz;
	
	@RequestMapping(value = "/tree", method = RequestMethod.GET)
	@ApiOperation(value = "获取部门树状列表")
	public Object list() {
		// 将查询内容存入map
		String projectId = StrUtils.tranString(request.getParameter("projectId"));
		if (!this.getSessionBean().getSuperUser() || !StrUtils.checkParam(projectId)) {
			projectId = this.getProjectId();
		}
		List<Department> list = departmentBiz.findByPropertyName(Department.class, "projectId", projectId);
		List<Department> root = TreePick.pickDepartment(list);
		return BaseRestControl.tranReturnValue(root);
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@ApiOperation(value = "获取部门详细信息", response = Department.class)
	public Object get(@PathVariable String id) {
		Department entity = departmentBiz.findById(Department.class, id);
		if(entity != null) {
			if(!entity.getProjectId().equals(this.getProjectId())) {
				return BaseRestControl.tranReturnValue(ResultType.unAuthorized);
			}
			return BaseRestControl.tranReturnValue(entity);
		}
		return BaseRestControl.tranReturnValue(ResultType.deleted);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	@ApiOperation(value = "删除部门，如果存在子部门，会删除子部门")
	public Object delete(@PathVariable String id) {
		Department entity = departmentBiz.findById(Department.class, id);
		if(entity != null) {
			if(!"1".equals(entity.getId())) {
				try {
					if(!entity.getProjectId().equals(this.getProjectId())) {
						return BaseRestControl.tranReturnValue(ResultType.unAuthorized);
					}
					departmentBiz.delete(entity);	
				} catch (Exception e) {
					this.exception(this.getClass(), e);
					return BaseRestControl.tranReturnValue(ResultType.user_in_department);
				}
				return BaseRestControl.tranReturnValue(ResultType.success);	
			}
			return BaseRestControl.tranReturnValue(ResultType.unAuthorized);
		}
		return BaseRestControl.tranReturnValue(ResultType.deleted);
	}
	
	@ApiOperation(value = "添加新的部门", response = Department.class)
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public Object save(@RequestBody Department entity) {
		Map<String, String> validatorMap = ValidatorUtils.validator(entity);
		if(validatorMap.isEmpty()) {
			Map<String, Object> checkRepeatMap = StrUtils.createMap("deptCode", entity.getDeptCode());
			checkRepeatMap.put("projectId", this.getProjectId());
			if(departmentBiz.checkRepeat(Department.class, checkRepeatMap)) {	// 验证部门编码是否重复
				checkRepeatMap.remove("deptCode");
				checkRepeatMap.put("deptName", entity.getDeptName());
				if(departmentBiz.checkRepeat(Department.class, checkRepeatMap)) {	// 验证部门名称是否重复
					if(entity.getParentId() == null || "".equals(entity.getParentId())) {
						if(entity.getDepth() != null && entity.getDepth() == 1) {
							entity.setParentId("-1");	// 作为最高级使用	
						} else {
							return BaseRestControl.tranReturnValue(ResultType.parameter_error);
						}
					}
					entity.createdUser(this.getLoginUser());
					entity.setProjectId(this.getProjectId());	// 与当前用户同一个租户下
					try {
						departmentBiz.add(entity);
						return BaseRestControl.tranReturnValue(ResultType.success, entity);	
					} catch (Exception e) {
						this.exception(e, "Create department failure");
						return BaseRestControl.tranReturnValue(ResultType.failure);
					}
				}
				return BaseRestControl.tranReturnValue(ResultType.department_deptName_repeat);
			}
			return BaseRestControl.tranReturnValue(ResultType.department_deptCode_repeat);
		}
		return BaseRestControl.tranReturnValue(ResultType.parameter_error, validatorMap);
	}
	
	@ApiOperation(value = "修改部门信息", response = Department.class)
	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	public Object update(@PathVariable String id, @RequestBody Department before) {
		Map<String, String> validatorMap = ValidatorUtils.validator(before);
		if(validatorMap.isEmpty()) {
			Department entity = departmentBiz.findById(Department.class, id);
			if(entity != null) {
				if(!entity.getProjectId().equals(this.getProjectId())) {
					return BaseRestControl.tranReturnValue(ResultType.unAuthorized);
				}
				
				InvokeSetForm.copyFormProperties(before, entity);
				Map<String, Object> checkRepeatMap = StrUtils.createMap("deptCode", entity.getDeptCode());
				checkRepeatMap.put("projectId", this.getProjectId());
				if(departmentBiz.checkRepeat(Department.class, checkRepeatMap, id)) {
					checkRepeatMap.remove("deptCode");
					checkRepeatMap.put("deptName", entity.getDeptName());
					if(departmentBiz.checkRepeat(Department.class, checkRepeatMap, id)) {	// 验证部门名称是否重复
						entity.updatedUser(this.getLoginUser());
						try {
							departmentBiz.update(entity);
							return BaseRestControl.tranReturnValue(ResultType.success, entity);	
						} catch (Exception e) {
							this.exception(this.getClass(), e);
							return BaseRestControl.tranReturnValue(ResultType.failure);
						}
					}
					return BaseRestControl.tranReturnValue(ResultType.department_deptName_repeat);
				}
				return BaseRestControl.tranReturnValue(ResultType.department_deptCode_repeat);
			}
			return BaseRestControl.tranReturnValue(ResultType.deleted);	
		}
		return BaseRestControl.tranReturnValue(ResultType.parameter_error, validatorMap);
	}
	
	@ApiOperation(value = "验证部门编码(deptCode)/部门名称(deptName)是否重复")
	@RequestMapping(value = "/check/{type}", method = RequestMethod.GET)
	public Object loginName(HttpServletRequest req, @PathVariable String type) {
		if("deptName".equals(type) || "deptCode".equals(type)) {
			String id = req.getParameter("id");
			String value = req.getParameter("value");
			Map<String, Object> checkRepeatMap = StrUtils.createMap(type, value);
			checkRepeatMap.put("projectId", this.getProjectId());
			if(departmentBiz.checkRepeat(Department.class, checkRepeatMap, id)) {
				return BaseRestControl.tranReturnValue(ResultType.success);
			}
			return BaseRestControl.tranReturnValue(ResultType.repeat);
		}
		return BaseRestControl.tranReturnValue(ResultType.parameter_error);
	}

}

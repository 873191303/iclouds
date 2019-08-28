package com.h3c.iclouds.rest;

import com.h3c.iclouds.base.BaseRestControl;
import com.h3c.iclouds.po.bean.inside.UpdateProject2QuotaBean;
import com.h3c.iclouds.biz.Project2QuotaBiz;
import com.h3c.iclouds.biz.ProjectBiz;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.exception.MessageException;
import com.h3c.iclouds.po.Project;
import com.h3c.iclouds.po.Project2Quota;
import com.h3c.iclouds.validate.ValidatorUtils;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Map;

/**
 * Created by yK7408 on 2016/12/13.
 */
@Api(value = "云管理租户配额", description = "云管理租户配额")
@RestController
@RequestMapping("/project2Quota")
public class Project2QuotaRest extends BaseRestControl {

	@Resource
	private ProjectBiz projectBiz;

	@Resource
	private Project2QuotaBiz project2QuotaBiz;

	@ApiOperation(value = "修改云管理租户的配额", response = Project.class)
	@RequestMapping(method = RequestMethod.PUT)
	public Object updateProject2Quota(@RequestBody UpdateProject2QuotaBean bean) throws IOException {
		Map<String, String> map = ValidatorUtils.validator(bean);
		if (!map.isEmpty()) {
			return BaseRestControl.tranReturnValue(ResultType.parameter_error, map);
		}
		if(!this.getSessionBean().getSuperUser()) {
			return BaseRestControl.tranReturnValue(ResultType.unAuthorized);
		}
		Object result = null;
		try {
			result = projectBiz.updateProject2Quota(bean);
		} catch (Exception e) {
			if (e instanceof MessageException) {
				return BaseRestControl.tranReturnValue(((MessageException) e).getResultCode());
			}
			this.exception(Project.class, e);
			return BaseRestControl.tranReturnValue(ResultType.failure);
		}
		return result;
	}

	@ApiOperation(value = "获得当前配额的最大最小值", response = Project2Quota.class)
	@RequestMapping(value = "/{tenantId}", method = RequestMethod.PUT)
	public Object check(@PathVariable String tenantId, @RequestBody Project2Quota project2Quota) throws IOException {
		Map<String, String> map = ValidatorUtils.validator(project2Quota);
		if (!map.isEmpty()) {
			return BaseRestControl.tranReturnValue(ResultType.parameter_error, map);
		}
		Project project = projectBiz.get(tenantId);

		Object result = null;
		if (null != project) {
			try {
				result = project2QuotaBiz.check(project, project2Quota);
			} catch (Exception e) {
				if (e instanceof MessageException) {
					return BaseRestControl.tranReturnValue(ResultType.failure, ((MessageException) e).getMessage());
				}
				this.exception(Project.class, e);
				return BaseRestControl.tranReturnValue(ResultType.failure);
			}
		}
		return BaseRestControl.tranReturnValue(ResultType.file_quota_success,result);
	}
}

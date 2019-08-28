package com.h3c.iclouds.rest;

import com.h3c.iclouds.base.BaseRest;
import com.h3c.iclouds.base.BaseRestControl;
import com.h3c.iclouds.biz.ProjectBiz;
import com.h3c.iclouds.biz.TpartyEduBiz;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageList;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.po.Project;
import com.h3c.iclouds.po.TpartyEdu;
import com.h3c.iclouds.utils.InvokeSetForm;
import com.h3c.iclouds.utils.StrUtils;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yKF7317 on 2017/6/22.
 */
@Api(value = "教育云租户映射关系", description = "教育云租户映射关系")
@RestController
@RequestMapping("/edu")
public class TpartyEduRest extends BaseRest<TpartyEdu> {
	
	@Resource
	private TpartyEduBiz tpartyEduBiz;
	
	@Resource
	private ProjectBiz projectBiz;
	
	@Override
	@ApiOperation(value = "获取教育云租户映射关系列表", response = TpartyEdu.class)
	@RequestMapping(method = RequestMethod.GET)
	public Object list () {
		if (!this.getSessionBean().getSuperUser()) {
			return BaseRestControl.tranReturnValue(new ArrayList<>());
		}
		PageEntity entity = this.beforeList();
		PageModel<TpartyEdu> pageModel = tpartyEduBiz.findForPage(entity);
		PageList<TpartyEdu> page = new PageList<TpartyEdu>(pageModel, entity.getsEcho());
		return BaseRestControl.tranReturnValue(page);
	}
	
	@Override
	@ApiOperation(value = "获取教育云租户映射关系详细信息", response = TpartyEdu.class)
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public Object get (@PathVariable String id) {
		if (!this.getSessionBean().getSuperUser()) {
			return BaseRestControl.tranReturnValue(ResultType.unAuthorized);
		}
		TpartyEdu tpartyEdu = tpartyEduBiz.findById(TpartyEdu.class, id);
		if (null == tpartyEdu) {
			return BaseRestControl.tranReturnValue(ResultType.deleted);
		}
		return BaseRestControl.tranReturnValue(tpartyEdu);
	}
	
	@Override
	@ApiOperation(value = "删除教育云租户映射关系", response = TpartyEdu.class)
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public Object delete (@PathVariable String id) {
		if (!this.getSessionBean().getSuperUser()) {
			return BaseRestControl.tranReturnValue(ResultType.unAuthorized);
		}
		TpartyEdu tpartyEdu = tpartyEduBiz.findById(TpartyEdu.class, id);
		if (null == tpartyEdu) {
			return BaseRestControl.tranReturnValue(ResultType.deleted);
		}
		try {
			tpartyEduBiz.delete(tpartyEdu);
			return BaseRestControl.tranReturnValue(ResultType.success);
		} catch (Exception e) {
			this.exception(TpartyEdu.class, e);
			return BaseRestControl.tranReturnValue(ResultType.failure);
		}
	}
	
	@Override
	@ApiOperation(value = "保存教育云租户映射关系", response = TpartyEdu.class)
	@RequestMapping( method = RequestMethod.POST)
	public Object save (@RequestBody TpartyEdu entity) {
		if (!this.getSessionBean().getSuperUser()) {
			return BaseRestControl.tranReturnValue(ResultType.unAuthorized);
		}
		Map<String, String> validate = ValidatorUtils.validator(entity);
		if (!validate.isEmpty()) {
			return BaseRestControl.tranReturnValue(ResultType.parameter_error, validate);
		}
		String projectId = entity.getProjectId();
		if (projectId.equals(singleton.getRootProject())) {
			return BaseRestControl.tranReturnValue(ResultType.project_not_exist);
		}
		Project project = projectBiz.findById(Project.class, projectId);
		if (null == project) {
			return BaseRestControl.tranReturnValue(ResultType.can_not_relate_root_project);
		}
		int count = tpartyEduBiz.count(TpartyEdu.class, StrUtils.createMap("projectId", projectId));
		if (count > 0) {
			return BaseRestControl.tranReturnValue(ResultType.project_related_by_annother_school);
		}
		count = tpartyEduBiz.count(TpartyEdu.class, StrUtils.createMap("eduCode", entity.getEduCode()));
		if (count > 0) {
			return BaseRestControl.tranReturnValue(ResultType.code_repeat);
		}
		entity.createdUser(this.getLoginUser());
		try {
			tpartyEduBiz.add(entity);
			return BaseRestControl.tranReturnValue(ResultType.success);
		} catch (Exception e) {
			this.exception(TpartyEdu.class, e);
			return BaseRestControl.tranReturnValue(ResultType.failure);
		}
	}
	
	@Override
	@ApiOperation(value = "修改教育云租户映射关系", response = TpartyEdu.class)
	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	public Object update (@PathVariable String id, @RequestBody TpartyEdu entity) throws IOException {
		if (!this.getSessionBean().getSuperUser()) {
			return BaseRestControl.tranReturnValue(ResultType.unAuthorized);
		}
		Map<String, String> validate = ValidatorUtils.validator(entity);
		if (!validate.isEmpty()) {
			return BaseRestControl.tranReturnValue(ResultType.parameter_error, validate);
		}
		TpartyEdu tpartyEdu = tpartyEduBiz.findById(TpartyEdu.class, id);
		if (null == tpartyEdu) {
			return BaseRestControl.tranReturnValue(ResultType.deleted);
		}
		if (!entity.getProjectId().equals(tpartyEdu.getProjectId())) {
			if (entity.getProjectId().equals(singleton.getRootProject())) {
				return BaseRestControl.tranReturnValue(ResultType.can_not_relate_root_project);
			}
			int count = tpartyEduBiz.count(TpartyEdu.class, StrUtils.createMap("projectId", entity.getProjectId()));
			if (count > 0) {
				return BaseRestControl.tranReturnValue(ResultType.project_related_by_annother_school);
			}
		}
		if (!entity.getEduCode().equals(tpartyEdu.getEduCode())) {
			int count = tpartyEduBiz.count(TpartyEdu.class, StrUtils.createMap("eduCode", entity.getEduCode()));
			if (count > 0) {
				return BaseRestControl.tranReturnValue(ResultType.code_repeat);
			}
		}
		try {
			InvokeSetForm.copyFormProperties(entity, tpartyEdu);
			tpartyEdu.updatedUser(this.getLoginUser());
			tpartyEduBiz.update(tpartyEdu);
			return BaseRestControl.tranReturnValue(ResultType.success);
		} catch (Exception e) {
			this.exception(TpartyEdu.class, e);
			return BaseRestControl.tranReturnValue(ResultType.failure);
		}
	}
	
	@ApiOperation(value = "获取教育云可绑定的租户列表", response = Project.class)
	@RequestMapping(value = "/project", method = RequestMethod.GET)
	public Object projectList () {
		if (!this.getSessionBean().getSuperUser()) {
			return BaseRestControl.tranReturnValue(new ArrayList<>());
		}
		List<String> projectIds = tpartyEduBiz.getProjectIds();
		PageEntity entity = this.beforeList();
		entity.setSpecialParams(projectIds.toArray(new String[projectIds.size()]));
		PageModel<Project> pageModel = projectBiz.findForPage(entity);
		PageList<Project> page = new PageList<>(pageModel, entity.getsEcho());
		return BaseRestControl.tranReturnValue(page);
	}
	
	@ApiOperation(value = "验重", response = TpartyEdu.class)
	@RequestMapping(value = "/check/{eduCode}", method = RequestMethod.GET)
	public Object check (@PathVariable String eduCode) {
		boolean repeat = false;
		String id = request.getParameter("id");//修改时传入一个id则查重时会排除对象本身
		try {
			Map<String, Object> checkMap = new HashMap<>();
			checkMap.put("eduCode", eduCode);
			repeat = tpartyEduBiz.checkRepeat(TpartyEdu.class, checkMap, id);
			if (!repeat){//查重(名称)
				return BaseRestControl.tranReturnValue(ResultType.repeat);
			}
			return BaseRestControl.tranReturnValue(ResultType.success);
		} catch (Exception e) {
			return BaseRestControl.tranReturnValue(ResultType.failure);
		}
	}
}

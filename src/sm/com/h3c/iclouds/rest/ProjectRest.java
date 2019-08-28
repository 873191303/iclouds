package com.h3c.iclouds.rest;

import com.alibaba.fastjson.JSONObject;
import com.h3c.iclouds.base.BaseRestControl;
import com.h3c.iclouds.biz.CustomBiz;
import com.h3c.iclouds.biz.DifferencesBiz;
import com.h3c.iclouds.biz.ProjectBiz;
import com.h3c.iclouds.biz.UserBiz;
import com.h3c.iclouds.common.CompareEnum;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageList;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.exception.MessageException;
import com.h3c.iclouds.operate.CloudosAzone;
import com.h3c.iclouds.operate.CloudosClient;
import com.h3c.iclouds.operate.CloudosGroup;
import com.h3c.iclouds.operate.CloudosProject;
import com.h3c.iclouds.operate.CloudosUser;
import com.h3c.iclouds.opt.IssoClient;
import com.h3c.iclouds.opt.IssoUserOpt;
import com.h3c.iclouds.opt.MonitorClient;
import com.h3c.iclouds.opt.MonitorParams;
import com.h3c.iclouds.po.Project;
import com.h3c.iclouds.po.Project2Azone;
import com.h3c.iclouds.po.User;
import com.h3c.iclouds.po.bean.inside.SaveProjectCustomBean;
import com.h3c.iclouds.po.bean.inside.UpdateTenantBean;
import com.h3c.iclouds.po.bean.model.AzoneBean;
import com.h3c.iclouds.po.bean.outside.ProjectDetailBean;
import com.h3c.iclouds.utils.JacksonUtil;
import com.h3c.iclouds.utils.ResourceHandle;
import com.h3c.iclouds.utils.StrUtils;
import com.h3c.iclouds.validate.ValidatorUtils;
import com.h3c.iclouds.validate.ZabbixModify;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 租户rest类
 * Created by yK7408 on 2016/12/13.
 */
@Api(value = "云管理租户表", description = "云管理租户表")
@RestController
@RequestMapping("/project")
public class ProjectRest extends BaseRestControl {

	@Resource
	private ProjectBiz projectBiz;
	
	@Resource
	private CustomBiz customBiz;

	@Resource
	private UserBiz userBiz;
	
	@Resource
	private DifferencesBiz differencesBiz;

	@ApiOperation(value = "获取云管理租户列表", response = Project.class)
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public Object list() {
		try {
			PageEntity entity = this.beforeList();
			String projectId = this.getSessionBean().getUser().getProjectId();
			// 四种角色需要特殊考虑（1-运营管理员，2-运维管理员，3-电信管理员，4-租户管理员）
			// 只有运营管理员可以看到所有租户信息,其他都只能看到自己的 ,各个租户之间都是隔离了 除了最高级的
			if (!getSessionBean().getSuperUser()) {
				Map<String, Object> queryMap = new HashMap<>();
				queryMap.put("projectId", projectId);
				entity.setQueryMap(queryMap);
			}
			PageModel<Project> model = projectBiz.findForPage(entity);
			PageList<Project> page = new PageList<>(model, entity.getsEcho());
			List<Project> projects = page.getAaData();
			for (Project project : projects) {
				projectBiz.transProject(project);
			}
			return BaseRestControl.tranReturnValue(page);
		} catch (Exception e) {
			this.exception(Project.class, e);
			return BaseRestControl.tranReturnValue(ResultType.failure);
		}
	}

	@ApiOperation(value = "获取云管理租户详细信息", response = Project.class)
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public Object get(@PathVariable String id) {
		if (!getSessionBean().getSuperUser()) {
			return BaseRestControl.tranReturnValue(ResultType.unAuthorized);
		}
		try {
			Project project = projectBiz.get(id);
			if (null != project) {
				project = projectBiz.transProject(project);
				ProjectDetailBean projectDetailBean = projectBiz.get(project);
				projectDetailBean.setProject(project);
				return BaseRestControl.tranReturnValue(ResultType.success, projectDetailBean);
			}
			return BaseRestControl.tranReturnValue(ResultType.deleted);
		} catch (Exception e) {
			this.exception(Project.class, e, id, "获取详细信息异常");
			if (e instanceof MessageException) {
				return BaseRestControl.tranReturnValue(((MessageException) e).getResultCode());
			}
			this.exception(Project.class, e);
			return BaseRestControl.tranReturnValue(ResultType.failure);
		}
	}
	
	@ZabbixModify
	@ApiOperation(value = "删除云管理租户", response = Project.class)
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public Object delete(@PathVariable String id) {
		// 设置日志的资源id
		this.request.setAttribute("id", id);
		if (!getSessionBean().getSuperUser()) {
			return BaseRestControl.tranReturnValue(ResultType.unAuthorized);
		}
		Project project = projectBiz.get(id);
		if (!StrUtils.checkParam(project)) {
			return BaseRestControl.tranReturnValue(ResultType.deleted);
		}
		this.request.setAttribute("name", project.getName());
		CloudosClient client = getSessionBean().getCloudClient();
		if (!StrUtils.checkParam(client)) {
			return BaseRestControl.tranReturnValue(ResultType.system_error);
		}
		int users = userBiz.findCountByPropertyName(User.class, "projectId", project.getId());
		if (users > 0) {
			return BaseRestControl.tranReturnValue(ResultType.project_have_user);
		}
		if (!differencesBiz.checkLocalResource(CompareEnum.PROJECT, null, id)) {
			return BaseRestControl.tranReturnValue(ResultType.project_exist_resource);
		}
		MonitorClient monitorClient = MonitorClient.createClient(request.getHeader(ConfigProperty.PROJECT_TOKEN_KEY));
		if (singleton.isIssoSyn() && null == monitorClient) {
			return BaseRestControl.tranReturnValue(ResultType.monitor_exception);
		}

		try {
			Object data = projectBiz.deleteProject(project, client, monitorClient);
			return BaseRestControl.tranReturnValue(data);
		} catch (Exception e) {
			this.exception(Project.class, e, "被删失败的租户信息" + JacksonUtil.toJSon(project));
			if (e instanceof MessageException) {
				return BaseRestControl.exceptionReturn((MessageException) e);
			}
			return BaseRestControl.tranReturnValue(ResultType.failure);
		}
	}
	
	@ZabbixModify
	@ApiOperation(value = "保存云管理租户", response = Project.class)
	@RequestMapping(value = "/create", method = RequestMethod.POST)
	public Object save(@RequestBody SaveProjectCustomBean bean) {
		// 设置日志的资源名称
		this.request.setAttribute("name", bean.getProject().getName());
		if (!getSessionBean().getSuperUser()) {
			return BaseRestControl.tranReturnValue(ResultType.unAuthorized);
		}
		Map<String, String> validatorMap = ValidatorUtils.validator(bean);
		if (!validatorMap.isEmpty()) {
			return BaseRestControl.tranReturnValue(ResultType.parameter_error, validatorMap);
		}
		validatorMap = ValidatorUtils.validator(bean.getProject());
		if (!validatorMap.isEmpty()) {
			return BaseRestControl.tranReturnValue(ResultType.parameter_error, validatorMap);
		}
		CloudosClient client = getSessionBean().getCloudClient();
		if (!StrUtils.checkParam(client)) {
			return BaseRestControl.tranReturnValue(ResultType.system_error);
		}
		CloudosUser cloudosUser = new CloudosUser(client);
		if (cloudosUser.checkName(bean.getUserName())) {
			return BaseRestControl.tranReturnValue(ResultType.cloudos_login_name_exist);
		}
		IssoClient adminClient = null;
		IssoUserOpt userOpt = null;
		if (singleton.isIssoSyn()) {
			adminClient = IssoClient.createAdmin();
			if (null == adminClient) {
				return BaseRestControl.tranReturnValue(ResultType.isso_exception);
			}
			userOpt = new IssoUserOpt(adminClient);
			//在isso查重
			JSONObject jsonObject = userOpt.checkRepeat(bean.getUserName());
			if (!adminClient.checkResult(jsonObject)) {
				return BaseRestControl.tranReturnValue(ResultType.repeat_in_isso);
			}
		}
		MonitorClient monitorClient = null;
		if (singleton.isMonitorSyn()) {
			monitorClient = MonitorClient.createClient(request.getHeader(ConfigProperty.PROJECT_TOKEN_KEY));
			if (null == monitorClient) {
				return BaseRestControl.tranReturnValue(ResultType.monitor_exception);
			}
		}
		Map<String, Object> resultMap = new HashMap<>();
		try {
			resultMap = projectBiz.save(bean, adminClient, monitorClient);
			return BaseRestControl.tranReturnValue(ResultType.success, resultMap);
		} catch (Exception e) {
			this.exception(Project.class, e,"租户创建失败，输入的参数："+JacksonUtil.toJSon(bean));
			
			//cloudos回删数据
			CloudosProject cloudosProject=new CloudosProject(client);
			CloudosAzone cloudosAzone=new CloudosAzone(client);
			CloudosGroup cloudosGroup=new CloudosGroup(client);
			String projectId=bean.getProjectId();
			String userId=bean.getUserId();
			List<AzoneBean> azoneBeans=bean.getAzone();

			if (StrUtils.checkParam(userId,projectId)) {
				cloudosGroup.delete(userId, projectId);
			}
			if (StrUtils.checkParam(projectId)) {
				cloudosProject.delete(projectId);
			}
			if (StrUtils.checkParam(userId)) {
				if (ResourceHandle.judgeResponse(cloudosUser.get(userId))) {
					cloudosUser.delete(userId);
				}
			}
			if (StrUtils.checkCollection(azoneBeans)) {
				for (int j = 0; j < azoneBeans.size(); j++) {
					AzoneBean azoneBean=azoneBeans.get(j);
					if (StrUtils.checkParam(azoneBean,projectId)) {
						cloudosAzone.delete(azoneBean, projectId);
					}
				}
			}
			
			//isso回删数据
			if (StrUtils.checkParam(bean.getIssoUserId())) {
				userOpt.delete(bean.getIssoUserId());
			}
			
			//监控回删数据
			monitorClient.delete(monitorClient.tranUrl(MonitorParams.MONITOR_API_USER_DELETE,
					bean.getUserName()), null);
			monitorClient.delete(monitorClient.tranUrl(MonitorParams.MONITOR_API_PROJECT_DELETE, projectId),
					null);
			
			if (e instanceof MessageException) {
				JSONObject cloudosError = bean.getCloudosError();
				if (StrUtils.checkParam(cloudosError)) {
					String code = cloudosError.getString("result");
					if (code.equals("409")) {
						return BaseRestControl.tranReturnValue(ResultType.conflict);
					}
				}
				return BaseRestControl.exceptionReturn((MessageException) e);
			}
			return BaseRestControl.tranReturnValue(ResultType.failure);
		}
	}

	@ApiOperation(value = "修改云管理租户", response = Project.class)
	@RequestMapping(value = "/update/{id}", method = RequestMethod.PUT)
	public Object update(@PathVariable String id, @RequestBody UpdateTenantBean bean) {
		if (!getSessionBean().getSuperUser()) {
			return BaseRestControl.tranReturnValue(ResultType.unAuthorized);
		}
		// 设置日志的资源id
		this.request.setAttribute("id", id);
		CloudosClient client = getSessionBean().getCloudClient();
		CloudosAzone cloudosAzone = new CloudosAzone(client);
		CloudosProject cloudosProject = new CloudosProject(client);
		Map<String, String> map = ValidatorUtils.validator(bean);
		if (!map.isEmpty()) {
			return BaseRestControl.tranReturnValue(ResultType.parameter_error, map);
		}
		Object result = null;
		try {
			result = projectBiz.update(id, bean);
		} catch (Exception e) {
			this.exception(Project.class, e, "更新租户失败的输入参数：" + JacksonUtil.toJSon(bean));
			boolean temp[] = bean.getFlag();
			List<Project2Azone> project2Azones = bean.getProject2Azones();
			int j = 0;
			for (int i = 0; i < temp.length; i++) {
				if (i < bean.getAzones().size()) {
					if (temp[i] == true) {
						AzoneBean azoneBean = new AzoneBean();
						azoneBean.setUuid(project2Azones.get(i).getUuid());
						cloudosAzone.save(azoneBean, id);
					}
				} else if (i >= bean.getAzones().size() && i < bean.getAzones().size() * 2) {
					if (temp[i] == true) {
						AzoneBean azoneBean = bean.getAzones().get(j);
						Project2Azone project2Azone = new Project2Azone();
						project2Azone.setUuid(azoneBean.getUuid());
						j++;
						cloudosAzone.delete(project2Azone, id);
					}
				} else {
					if (temp[i] == true) {
						cloudosProject.update(bean.getTenant());
					}
				}
			}
			if (e instanceof MessageException) {
				return BaseRestControl.tranReturnValue(((MessageException) e).getResultCode());
			}
			return BaseRestControl.tranReturnValue(ResultType.failure);
		}
		return result;
	}

	@ApiOperation(value = "查询租户的客户", response = Project.class)
	@RequestMapping(value = "/listCustom", method = RequestMethod.GET)
	public Object getCustom(@RequestParam(required = false) String text) {
		Object result = null;
		try {
			result = customBiz.listCustom(text);
		} catch (Exception e) {
			this.exception(Project.class, e);
			return BaseRestControl.tranReturnValue(ResultType.failure);
		}
		return result;
	}

	@ApiOperation(value = "验证重复存在", response = Project.class)
	@RequestMapping(value = "/check/{name}", method = RequestMethod.GET)
	public Object check(HttpServletRequest request, @PathVariable String name) {
		String id = request.getParameter("id");
		if (!StrUtils.checkParam(name)) {
			return BaseRestControl.tranReturnValue(ResultType.data_not_null);
		}
		Map<String, Object> map = new HashMap<>();
		map.put("name", name);
		map.put("flag", 0);
		if (projectBiz.checkRepeat(Project.class, map, id)) {
			return BaseRestControl.tranReturnValue(ResultType.success);
		}
		return BaseRestControl.tranReturnValue(ResultType.repeat);
	}
	
	@ApiOperation(value = "获取租户数量与本月新增数量")
	@RequestMapping(value = "/count", method = RequestMethod.GET)
	public Object count() {
		Map<String, Integer> countMap = new HashMap<>();
		int totalCount = projectBiz.count(Project.class, new HashMap<>());
		countMap.put("totalCount", totalCount);
		int monthCount = projectBiz.monthCount();
		countMap.put("monthCount", monthCount);
		return BaseRestControl.tranReturnValue(countMap);
	}
	
	@ApiOperation(value = "判断租户是否为同花顺租户", response = Project.class)
	@RequestMapping(value = "/ths/{id}", method = RequestMethod.GET)
	public Object check(@PathVariable String id) {
		if (!getSessionBean().getSuperUser()) {
			return BaseRestControl.tranReturnValue(ResultType.unAuthorized);
		}
		if (id.equals(singleton.getThsTenantId())) {
			return BaseRestControl.tranReturnValue(ResultType.success);
		}
		return BaseRestControl.tranReturnValue(ResultType.failure);
	}
}

package com.h3c.iclouds.rest;

import com.h3c.iclouds.base.BaseRestControl;
import com.h3c.iclouds.biz.Project2NetworkBiz;
import com.h3c.iclouds.biz.ProjectBiz;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.dao.ProjectDao;
import com.h3c.iclouds.exception.MessageException;
import com.h3c.iclouds.po.Project;
import com.h3c.iclouds.po.Project2Network;
import com.h3c.iclouds.utils.IpValidator;
import com.h3c.iclouds.utils.StrUtils;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yK7408 on 2016/12/13.
 */
@Api(value = "云管理租户网络配额表", description = "云管理租户网络配额表")
@RestController
@RequestMapping("/project2Network")
public class Project2NetworkRest extends BaseRestControl {
	
	@Resource
	private ProjectBiz projectBiz;
	
	@Resource
	private ProjectDao projectDao;
	
	@Resource
	private Project2NetworkBiz project2NetworkBiz;
	
	@SuppressWarnings("unchecked")
	@ApiOperation(value = "为租户创建网络配额")
	@RequestMapping(value = "/{tenantId}", method = RequestMethod.PUT)
	public Object save (@PathVariable String tenantId, @RequestBody Map<String, String> map) {
		String ipPools = map.get("ipPool");
		String cidr = map.get("cidr");
		// 设置日志的资源id
		this.request.setAttribute("id", tenantId);
		if (!StrUtils.checkParam(cidr)) {
			return BaseRestControl.tranReturnValue(ResultType.cidr_not_null);
		}
		cidr = map.get("cidr").trim();
		Project project = projectDao.getExistProject(tenantId);
		if (project == null) {
			return BaseRestControl.tranReturnValue(ResultType.tenant_not_exist);
		}
		this.request.setAttribute("name", project.getName());
		try {
			//校验输入的参数是否正确
			HashMap<String, Object> ips = (HashMap<String, Object>) IpValidator.checkIp(null, ipPools, cidr, false);
			if (StrUtils.checkParam(ips.get("error"))) {
				return BaseRestControl.tranReturnValue((ResultType) ips.get("error"));
			}
			Object result = projectBiz.saveNetwork(tenantId, cidr, ips);
			return BaseRestControl.tranReturnValue(result);
		} catch (Exception e) {
			e.printStackTrace();
			this.exception(this.getClass(), e);
			if (e instanceof MessageException) {
				return BaseRestControl.tranReturnValue(((MessageException) e).getResultCode());
			}
			if (e instanceof IllegalArgumentException) {
				return BaseRestControl.tranReturnValue(ResultType.failure);
			}
			return BaseRestControl.tranReturnValue(ResultType.ip_format_error);
		}
		
	}
	
	@ApiOperation(value = "删除云管理网段和子网配额", response = Project.class)
	@RequestMapping(value = "/{tenantId}/{id}", method = RequestMethod.DELETE)
	public Object deleteNetwork (@PathVariable String tenantId, @PathVariable String id) throws IOException {
		Object result = null;// 设置日志的资源id
		this.request.setAttribute("id", tenantId);
		Project2Network project2Network = project2NetworkBiz.findById(Project2Network.class, id);
		Project project = projectBiz.get(project2Network.getTenantId());
		this.request.setAttribute("name", project.getName());
		if (StrUtils.checkParam(id)) {
			try {
				result = projectBiz.deleteProject2Network(id, tenantId);
				if (result instanceof List) {
					List<String> projectNames = (List<String>) result;
					String erroResult = "当前网段配额正被";
					for (String projectName : projectNames) {
						erroResult = erroResult + projectName + ",";
					}
					erroResult = erroResult.substring(0, erroResult.length() - 1);
					erroResult = erroResult + "所使用";
					return BaseRestControl.tranReturnValue(ResultType.file_project_error, erroResult);
				}
			} catch (Exception e) {
				this.exception(Project.class, e);
				if (e instanceof MessageException) {
					return BaseRestControl.tranReturnValue(((MessageException) e).getResultCode(), project.getName());
				}
				return BaseRestControl.tranReturnValue(ResultType.failure);
			}
		} else {
			return BaseRestControl.tranReturnValue(ResultType.tenant_quota_network_not_exist);
		}
		return BaseRestControl.tranReturnValue(result);
	}
	
	@ApiOperation(value = "获取云管理租户可用的cidr", response = Project2Network.class)
	@RequestMapping(value = "/{tenantId}", method = RequestMethod.GET)
	public Object get (@PathVariable String tenantId) {
		try {
			// 租户可用的cidr
			Project project = projectDao.getExistProject(tenantId);
			if (!StrUtils.checkParam(project.getParentId())) {
				return BaseRestControl.tranReturnValue(ResultType.tenant_not_root);
			}
			Object result = project2NetworkBiz.get(project);
			return BaseRestControl.tranReturnValue(result);
			
		} catch (Exception e) {
			this.exception(Project.class, e);
			return BaseRestControl.tranReturnValue(ResultType.failure);
		}
		
	}
	
	@ApiOperation(value = "获取云管理cidr对应的地址池", response = Project2Network.class)
	@RequestMapping(value = "/{tenantId}/{networkId}", method = RequestMethod.GET)
	public Object get (@PathVariable String tenantId, @PathVariable String networkId) {
		try {
			// 租户可用的cidr
			Project project = projectDao.getExistProject(tenantId);
			if (!StrUtils.checkParam(project.getParentId())) {
				return BaseRestControl.tranReturnValue(ResultType.tenant_not_root);
			}
			Project2Network project2Network = project2NetworkBiz.findById(Project2Network.class, networkId);
			if (!StrUtils.checkParam(project2Network)) {
				return BaseRestControl.tranReturnValue(ResultType.tenant_quota_network_not_exist);
			}
			Object result = project2NetworkBiz.get(project, networkId);
			return BaseRestControl.tranReturnValue(result);
		} catch (Exception e) {
			this.exception(Project.class, e);
			return BaseRestControl.tranReturnValue(ResultType.failure);
		}
		
	}
	
	@ApiOperation(value = "查询租户的cidr是否重复", response = Project2Network.class)
	@RequestMapping(value = "/check/{tenantId}", method = RequestMethod.PUT)
	public Object check (@PathVariable String tenantId, @RequestBody Map<String, String> map) {
		String cidr = map.get("cidr");
		if (StrUtils.checkParam(cidr)) {
			cidr = cidr.trim();
		} else {
			return BaseRestControl.tranReturnValue(ResultType.cidr_not_null);
		}
		Project project = projectDao.getExistProject(tenantId);
		if (project == null) {
			return BaseRestControl.tranReturnValue(ResultType.tenant_not_exist);
		}
		Map<String, Object> ips;
		try {
			ips = IpValidator.checkIp(null, null, cidr, false);
			if (StrUtils.checkParam(ips.get("error"))) {
				return BaseRestControl.tranReturnValue((ResultType) ips.get("error"));
			}
			Object result = project2NetworkBiz.check(tenantId, cidr, ips);
			return BaseRestControl.tranReturnValue(result);
		} catch (Exception e) {
			this.exception(this.getClass(), e);
			if (e instanceof MessageException) {
				return BaseRestControl.tranReturnValue(((MessageException) e).getResultCode());
			}
			if (e instanceof IllegalArgumentException) {
				return BaseRestControl.tranReturnValue(((IllegalArgumentException) e).getMessage());
			}
			return BaseRestControl.tranReturnValue(ResultType.ip_format_error);
		}
	}
}

package com.h3c.iclouds.biz;

import com.h3c.iclouds.base.BaseBiz;
import com.h3c.iclouds.operate.CloudosClient;
import com.h3c.iclouds.opt.IssoClient;
import com.h3c.iclouds.opt.MonitorClient;
import com.h3c.iclouds.po.Project;
import com.h3c.iclouds.po.User;
import com.h3c.iclouds.po.bean.inside.SaveProjectCustomBean;
import com.h3c.iclouds.po.bean.inside.UpdateProject2QuotaBean;
import com.h3c.iclouds.po.bean.inside.UpdateTenantBean;
import com.h3c.iclouds.po.bean.model.AzoneBean;
import com.h3c.iclouds.po.bean.outside.ProjectDetailBean;

import java.util.List;
import java.util.Map;

public interface ProjectBiz extends BaseBiz<Project> {

	Project transProject(Project project);

	Object update(String id, UpdateTenantBean bean);

	ProjectDetailBean get(Project project);

	Object updateProject2Quota(UpdateProject2QuotaBean bean);

	Object deleteProject(Project project, CloudosClient client, MonitorClient monitorClient);
	
	Map<String, Object> save(SaveProjectCustomBean bean, IssoClient adminClient, MonitorClient monitorClient) throws Exception;

	Object saveNetwork(String id, String cidr, Map<String, Object> ips);

	Object deleteProject2Network(String id, String tenantId);

	Project get(String id);

	List<Project> checkName(Project project);

	boolean checkRole();

	boolean checkOptionRole(String projectId);

	boolean checkLookRole(String projectId);

	/**
	 * 获取本月新增的租户数量
	 * @return
	 */
	int monthCount();
	
	/**
	 * 获取请求防火墙、路由器、负载均衡组、负载均衡列表时的租户id过滤条件
	 * @param projectId
	 * @param flag
	 * @return
	 */
	String getFilterProjectId(String projectId, String flag);
	
	/**
	 * 检查云主机、云硬盘、虚拟网卡、公网ip模块用户操作资源权限
	 * (当前租户是资源的所属租户且拥有租户管理员角色或拥有管理员角色)
	 * @param projectId 租户id
	 * @param userId 用户id
	 * @return 布尔
	 */
	boolean checkOptionRole(String projectId, String userId);
	
	/**
	 * 检查用户隔离资源的新增权限
	 * @param userId
	 * @param projectId
	 * @return
	 */
	boolean checkSaveRole(String userId, String projectId);
	
	void localSave(Project project, User user, List<AzoneBean> azoneBeans) throws Exception;
}

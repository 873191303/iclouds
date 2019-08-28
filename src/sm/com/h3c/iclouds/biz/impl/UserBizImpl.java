package com.h3c.iclouds.biz.impl;

import com.alibaba.fastjson.JSONObject;
import com.h3c.iclouds.auth.CacheSingleton;
import com.h3c.iclouds.auth.SessionBean;
import com.h3c.iclouds.base.BaseBizImpl;
import com.h3c.iclouds.base.BaseDAO;
import com.h3c.iclouds.base.BaseRest;
import com.h3c.iclouds.biz.ProjectBiz;
import com.h3c.iclouds.biz.ResourceBiz;
import com.h3c.iclouds.biz.RoleBiz;
import com.h3c.iclouds.biz.UserBiz;
import com.h3c.iclouds.biz.VdcBiz;
import com.h3c.iclouds.client.zabbix.ZabbixApi;
import com.h3c.iclouds.client.zabbix.ZabbixDefine;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.common.OperateLogEnum;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.common.SimpleCache;
import com.h3c.iclouds.dao.UserDao;
import com.h3c.iclouds.exception.MessageException;
import com.h3c.iclouds.operate.CloudosClient;
import com.h3c.iclouds.operate.CloudosParams;
import com.h3c.iclouds.operate.CloudosUser;
import com.h3c.iclouds.opt.IssoClient;
import com.h3c.iclouds.opt.IssoUserOpt;
import com.h3c.iclouds.opt.MonitorClient;
import com.h3c.iclouds.opt.MonitorParams;
import com.h3c.iclouds.po.LogType;
import com.h3c.iclouds.po.OperateLogs;
import com.h3c.iclouds.po.PftUser;
import com.h3c.iclouds.po.Project;
import com.h3c.iclouds.po.Role;
import com.h3c.iclouds.po.User;
import com.h3c.iclouds.po.User2Group;
import com.h3c.iclouds.po.User2Role;
import com.h3c.iclouds.po.Vdc;
import com.h3c.iclouds.po.bean.IssoUserBean;
import com.h3c.iclouds.po.vo.UserVO;
import com.h3c.iclouds.utils.HttpUtils;
import com.h3c.iclouds.utils.InvokeSetForm;
import com.h3c.iclouds.utils.PwdUtils;
import com.h3c.iclouds.utils.ResourceHandle;
import com.h3c.iclouds.utils.SessionRedisUtils;
import com.h3c.iclouds.utils.StrUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("unchecked")
@Service("userBiz")
public class UserBizImpl extends BaseBizImpl<User> implements UserBiz {
	
    @Resource
    private UserDao userDao;

    @Resource
    private RoleBiz roleBiz;
	
	@Resource(name = "baseDAO")
	private BaseDAO<User2Group> userToGroupDao;
	
	@Resource(name = "baseDAO")
	private BaseDAO<User2Role> userToRoleDao;

    @Resource(name = "baseDAO")
    private BaseDAO<LogType> logTypeDao;

    @Resource
    private VdcBiz vdcBiz;
	
    @Resource
    private ProjectBiz projectBiz;

    @javax.annotation.Resource
    private ResourceBiz resourceBiz;
    
    @Resource(name = "baseDAO")
    private BaseDAO<PftUser> pftUserDao;
	
	@Override
	public PageModel<User> findForPage(PageEntity entity) {
		return this.userDao.findForPage(entity);
	}
	
	@Override
	public ResultType update(User entity, Map<String, Object> map, String type) {
		// 非云管理员只能操作租户内的用户
		if(!this.getSessionBean().getSuperUser()) {
			if(!StrUtils.tranString(entity.getProjectId()).equals(this.getProjectId())) {
				return ResultType.unAuthorized;
			}
		}
		
		if(!map.containsKey("groups") && !map.containsKey("roles")) {
			return ResultType.parameter_error;
		}
		if(type.equals("group")) {
			if(!singleton.getRootProject().equals(entity.getProjectId())) {
				return ResultType.cannot_select_group;
			}
			List<User2Group> set = this.userToGroupDao.findByPropertyName(User2Group.class, "userId", entity.getId());
			if(set != null && !set.isEmpty()) {
				for (User2Group u2g : set) {
					this.userToGroupDao.delete(u2g);
				}
			}
			
			List<String> groupArray = (ArrayList<String>) map.get("groups");
			if(groupArray != null && !groupArray.isEmpty()) {
				for (int i = 0; i < groupArray.size(); i++) {
					User2Group u2g = new User2Group();
					u2g.setGid(groupArray.get(i));
					u2g.createdUser(this.getSessionBean().getUserId());
					u2g.setUserId(entity.getId());
					if(i == 0) {	// 提交数据的第一个群组为默认群组
						u2g.setIsDefault(ConfigProperty.YES);
					}
					userToGroupDao.add(u2g);
				}
			} else {
				// 没有群组则默认一个系统群组
				User2Group u2g = new User2Group(entity.getId(), this.getLoginUser());
				userToGroupDao.add(u2g);
			}
		}
		
		if(type.equals("role")) {
			String userProjectId = entity.getProjectId();
			if(!StrUtils.checkParam(userProjectId)) {
				return ResultType.data_error;
			}
			
			List<User2Role> roleSet = this.userToRoleDao.findByPropertyName(User2Role.class, "userId", entity.getId());
//			String superId = null;
			String beforeGroupId = null;
			String afterGroupId = null;	// 默认设置为普通用户分组
			if(userProjectId.equals(singleton.getRootProject())) {
				beforeGroupId = singleton.getConfigValue("cloudos.tenant.comptroller");	// 设置为审计员分组
			} else {
				beforeGroupId = singleton.getConfigValue("cloudos.tenant.normal.user");	// 设置为普通用户分组	
			}
			
			boolean beforeAuth = false; // 用户之前角色是否拥有云管理员或运营管理员角色标识
			if(roleSet != null && !roleSet.isEmpty()) {
				for (User2Role u2r : roleSet) {
					if (StrUtils.equals(u2r.getRoleId(), singleton.getCloudRoleId(), singleton.getOperationRoleId())) {
						beforeAuth = true;
					}
					// 原本拥有租户/电信管理员角色
					if(CacheSingleton.getInstance().getTenantRoleId().equals(u2r.getRoleId()) ||
							CacheSingleton.getInstance().getCtRoleId().equals(u2r.getRoleId())) {
//						superId = u2r.getRoleId();
						beforeGroupId = singleton.getConfigValue("cloudos.tenant.normal.manager");
						break;
					} else if(singleton.getCloudRoleId().equals(u2r.getRoleId())) {
						beforeGroupId = singleton.getConfigValue("cloudos.tenant.cloud.manager");
					}
				}
			}
			
			boolean afterAuth = false; // 用户之后角色是否拥有云管理员或运营管理员角色标识
			String authType = ConfigProperty.ZABBIX_ADMIN_TYPE;
			Set<User2Role> roles = new HashSet<User2Role>();
			List<String> roleArray = (ArrayList<String>) map.get("roles");
			if(roleArray != null) {
				
				if (roleArray.contains(singleton.getOperationRoleId()) || roleArray.contains(singleton.getCloudRoleId
						())) {
					afterAuth = true;
					authType = ConfigProperty.ZABBIX_SUPER_ADMIN_TYPE;
				}
				
				String superRoleId = null;
				// 是否为普通租户管理员
				if(!singleton.getRootProject().equals(userProjectId) && !singleton.getCtTenantId().equals(userProjectId)) {
					superRoleId = singleton.getTenantRoleId();
				} else {
					if(roleArray.size() == 0) {
						return ResultType.role_select_least_one;
					}
					if(singleton.getCtTenantId().equals(userProjectId)) {
						superRoleId = singleton.getCtRoleId();
					}
				}
				StringBuffer errorBuffer = new StringBuffer();
				for (int i = 0; i < roleArray.size(); i++) {
					String roleId = roleArray.get(i);
					Role role = this.roleBiz.findById(Role.class, roleId);
					if(role != null) {
						// 主管审批审批和权签审批角色不归属到判断范围内
						if(!roleId.equals(CacheSingleton.getInstance().getChargeRoleId()) 
								&& !roleId.equals(CacheSingleton.getInstance().getSignRoleId())
								&& !roleId.equals(CacheSingleton.getInstance().getEwoFirstRoleId())
								&& !roleId.equals(CacheSingleton.getInstance().getEwoSecondRoleId())) {
							
							boolean flag = true;
							// 调度和处理只做租户一致性判断
							if(roleId.equals(CacheSingleton.getInstance().getHandleRoleId()) ||
									roleId.equals(CacheSingleton.getInstance().getDispatchRoleId())) {
								if(!role.getProjectId().equals(entity.getProjectId())) {
									flag = false;
								}
							} else {
								if(superRoleId == null) {
									superRoleId = role.getProleId();
								} else if(!superRoleId.equals(role.getProleId())) {
									flag = false;
								}
								
								// 4个超级初始化角色不做租户判断
								if(!roleId.equals(CacheSingleton.getInstance().getCtRoleId())
										&& !roleId.equals(CacheSingleton.getInstance().getChargeRoleId())
										&& !roleId.equals(CacheSingleton.getInstance().getSignRoleId())
										&& !roleId.equals(CacheSingleton.getInstance().getCloudRoleId())
										&& !roleId.equals(CacheSingleton.getInstance().getTenantRoleId())
										&& !roleId.equals(CacheSingleton.getInstance().getOperationRoleId())
										) {
									String projectId = StrUtils.tranString(role.getProjectId());
									// 当前用户归属租户与用户选择角色的对应租户不一致
									if(!projectId.equals(entity.getProjectId())) {
										if(!roleId.equals(CacheSingleton.getInstance().getTenantRoleId())) {
											return ResultType.tenant_role_cannot_use_other_error;
										}
									}
								}
							}
							
							if(!flag) {
								this.info("User [" + entity.getId() + "][" + entity.getUserName() + 
										"] link [" + role.getRoleName() + "][" + role.getId() + "]error");
								errorBuffer.append("用户 [" + entity.getUserName() + 
										"]选择角色[" + role.getRoleName() + "]失败,");
							}
						}
						
						User2Role u2r = new User2Role();
						u2r.setRoleId(roleId);
						u2r.createdUser(this.getSessionBean().getUserId());
						u2r.setUserId(entity.getId());
						roles.add(u2r);
						if(afterGroupId == null) {
							// 未设置分组且要设置租户管理员
							if(roleId.equals(singleton.getTenantRoleId()) || roleId.equals(singleton.getCtRoleId())) {
								afterGroupId = singleton.getConfigValue("cloudos.tenant.normal.manager");
							} else if(roleId.equals(singleton.getCloudRoleId())) {	// 设置为云管理员
								afterGroupId = singleton.getConfigValue("cloudos.tenant.cloud.manager");
							}
						}
					} else {
						continue;
					}
				}
				if(errorBuffer.length() > 0) {
					throw new MessageException(errorBuffer.deleteCharAt(errorBuffer.length() - 1).toString());
				}
			}
			
			if(roleSet != null && !roleSet.isEmpty()) {
				for (User2Role u2r : roleSet) {
					this.userToRoleDao.delete(u2r);
				}
			}
			
			// 原本用户为租户管理员或者电信管理员则默认添加,如果是超级管理员操作则不在范围内
//			if(!this.getSessionBean().isSuperUser() && superId != null) {
//				User2Role u2r = new User2Role();
//				u2r.setRoleId(superId);
//				u2r.createdUser(this.getSessionBean().getUserId());
//				u2r.setUserId(entity.getId());
//				roles.add(u2r);
//				afterGroupId = singleton.getConfigValue("cloudos.tenant.normal.manager");	// 设置为组织管理员分组
//			}
			
			if(afterGroupId == null) {
				if(userProjectId.equals(singleton.getRootProject())) {
					afterGroupId = singleton.getConfigValue("cloudos.tenant.comptroller");	// 设置为审计员分组
				} else {
					afterGroupId = singleton.getConfigValue("cloudos.tenant.normal.user");	// 设置为普通用户分组	
				}
			}
			
			if(!isCtTenant(entity.getProjectId())) {
				// 前后在cloudos中的权限不一致则需要做修改
				if(!afterGroupId.equals(beforeGroupId)) {
					String uri = CacheSingleton.getInstance().getCloudosApi(CloudosParams.CLOUDOS_API_GRANT);
					uri = HttpUtils.tranUrl(uri, entity.getProjectId(), entity.getCloudosId(), beforeGroupId);// 授予普通用户角色
					CloudosClient client = getSessionBean().getCloudClient();
					if(client == null) {
						throw new MessageException(ResultType.system_error);
					}
					JSONObject obj = client.delete(uri);
					if(!obj.getString("result").startsWith("2")) {
						this.warn("Delete cloudos group[" + beforeGroupId + "] error, operate user[" + entity.getId() + "][cloudos ID:" + entity.getCloudosId() + "][" + entity.getLoginName() + "]");
						throw new MessageException(ResultType.user_cloudos_group_error);
					}
					uri = uri.replace(beforeGroupId, afterGroupId);
					obj = client.put(uri);
					if(!obj.getString("result").startsWith("2")) {
						this.warn("Grant cloudos group[" + afterGroupId + "] error, operate user[" + entity.getId() + "][cloudos ID:" + entity.getCloudosId() + "][" + entity.getLoginName() + "]");
						throw new MessageException(ResultType.user_cloudos_group_error);
					}
				}
			}
			
			if(roles != null && !roles.isEmpty()) {
				for (User2Role u2r : roles) {
					userToRoleDao.add(u2r);
				}
			}
			
			// 监控修改用户授权
			if (singleton.isMonitorSyn() && (beforeAuth ^ afterAuth)) {
				ZabbixApi zabbixApi = ZabbixApi.createAdmin();
				PftUser pftUser = this.pftUserDao.singleByClass(PftUser.class, StrUtils.createMap("alias",
						entity.getLoginName()));
				if (null == pftUser) {
					throw MessageException.create(ResultType.not_exist);
				}
				Map<String, Object> updateMap = StrUtils.createMap("userid", pftUser.getUserId());
				updateMap.put("type", authType);
				JSONObject jsonObject = zabbixApi.update(ZabbixDefine.USER, updateMap);
				if (jsonObject.containsKey("error")) {
					throw MessageException.create(ZabbixApi.getErrorMsg(jsonObject), ResultType
							.failure_in_monitor);
				}
			}
			
		}
		return ResultType.success;
	}

	@Override
	public void save(User entity, String roleId, Map<String, Object> map, CloudosClient client, IssoClient issoClient,
					 MonitorClient monitorClient) {
		entity.createdUser(this.getLoginUser());
		String projectId = entity.getProjectId();
		
		// cloudos保存用户
		String userCloudosId = null;
		if(!isCtTenant(projectId)) {// 电信租户不需要写入到cloudos
			CloudosUser cloudosUser = new CloudosUser(client);
			JSONObject result = cloudosUser.save(entity, projectId, null);
			if (!ResourceHandle.judgeResponse(result)) {
				throw MessageException.create(HttpUtils.getError(result), ResultType.cloudos_exception);
			}
			//指定到租户下
			result = HttpUtils.getJSONObject(result, "user");
			userCloudosId = result.getString("id");
			String uri = CacheSingleton.getInstance().getCloudosApi(CloudosParams.CLOUDOS_API_GRANT);
			String groupId = singleton.getConfigValue("cloudos.tenant.normal.user");
			if (projectId.equals(singleton.getRootProject())) {
				groupId = singleton.getConfigValue("cloudos.tenant.comptroller");
			} else if (StrUtils.checkParam(roleId)) {
				groupId = singleton.getConfigValue("cloudos.tenant.normal.manager");
			}
			uri = HttpUtils.tranUrl(uri, projectId, userCloudosId, groupId);// 授予普通用户角色
			JSONObject obj = client.put(uri);
			if (!ResourceHandle.judgeResponse(obj)) {
				throw MessageException.create(HttpUtils.getError(obj), ResultType.cloudos_exception);
			}
			entity.setCloudosId(userCloudosId);
		}
		
		//isso上新增用户
		IssoUserOpt userOpt = null;
		String issoUserId = null;
		if (singleton.isIssoSyn()) {
			userOpt = new IssoUserOpt(issoClient);
			//在isso查重
			JSONObject jsonObject = userOpt.checkRepeat(entity.getLoginName());
			if (!issoClient.checkResult(jsonObject)) {
				throw MessageException.create(ResultType.repeat_in_isso);
			}
			IssoUserBean userBean = new IssoUserBean(entity);
			jsonObject = userOpt.save(userBean);
			if (!issoClient.checkResult(jsonObject)) {
				throw MessageException.create(issoClient.getError(jsonObject), ResultType.failure_in_isso);
			}
			issoUserId = issoClient.getValue(jsonObject, "id");
			map.put("issoUserId", issoUserId);
		}
		
		/*
		// 监控新增用户
		if (singleton.isMonitorSyn()) {
			//设置监控用户对应新增参数(必须是密码没有加密的用户)
			UserVO userVO = UserVO.create(ConfigProperty.ZABBIX_ADMIN_TYPE, entity);
			JSONObject jsonObject = monitorClient.post(monitorClient.tranUrl(MonitorParams.MONITOR_API_USER_SAVE, projectId), InvokeSetForm.tranClassToMap(userVO));
			if (!monitorClient.checkResult(jsonObject)) {//操作失败
				throw MessageException.create(monitorClient.getError(jsonObject), ResultType.failure_in_monitor);
			}
			map.put("loginName", entity.getLoginName());
		}
		*/
		
		//本地新增
		userDao.add(entity);
		// 根租户下的用户需要增加默认群组
		if(singleton.getRootProject().equals(projectId)) {
			// 添加系统默认群组
			User2Group u2g = new User2Group(entity.getId(), this.getLoginUser());
			this.userToGroupDao.add(u2g);
		}
		if (StrUtils.checkParam(roleId)) {
			User2Role u2r = new User2Role();
			u2r.setRoleId(roleId);
			u2r.setUserId(entity.getId());
			u2r.createdUser(this.getLoginUser());
			this.userToRoleDao.add(u2r);
		}
		String password = StrUtils.tranString(entity.getPassword());
		try {
			if (StrUtils.checkParam(password)) {
				entity.setPassword(PwdUtils.encrypt(password, entity.getLoginName() + entity.getId()));
			}
		} catch (Exception e) {
			throw MessageException.create(ResultType.failure);
		}
		userDao.update(entity);
	}
	
	@Override
	public void deleteUser(User entity) {
		// 检查cloudos连接
		CloudosClient client = null;
		if(!isCtTenant(entity.getProjectId())) {
			client = getSessionBean().getCloudClient();
			if (null == client) {
				throw MessageException.create(ResultType.system_error);
			}
		}
		
		// 检查monitor连接
		MonitorClient monitorClient = null;
		if (singleton.isMonitorSyn()) {
			monitorClient = MonitorClient.createClient(request.getHeader(ConfigProperty.PROJECT_TOKEN_KEY));
			if (null == monitorClient) {
				throw MessageException.create(ResultType.monitor_exception);
			}
		}
		
		// 检查isso连接
		IssoClient issoClient = null;
		if (singleton.isIssoSyn()) {
			issoClient = IssoClient.createAdmin();
			if (null == issoClient) {
				throw MessageException.create(ResultType.isso_exception);
			}
		}
		
		
		userDao.delete(entity);
		
		//cloudos删除用户
		if(StrUtils.checkParam(client)) {
			CloudosUser cloudosUser = new CloudosUser(client);
			String userId = entity.getCloudosId();
			if (null != userId) {
				JSONObject obj = cloudosUser.get(userId);
				if (ResourceHandle.judgeResponse(obj)) {
					obj = cloudosUser.delete(userId);
					if (!ResourceHandle.judgeResponse(obj)) {
						throw MessageException.create(HttpUtils.getError(obj), ResultType.cloudos_exception);
					}
				}
			}
		}
		
		// monitor删除数据
		if (StrUtils.checkParam(monitorClient)) {
			JSONObject jsonObject = monitorClient.delete(monitorClient.tranUrl(MonitorParams.MONITOR_API_USER_DELETE, entity.getLoginName()), null);
			if (!monitorClient.checkResult(jsonObject) && !ResultType.deleted.toString().equals(jsonObject.getJSONObject("record").getString("result")))
			{//操作失败且返回结果不为已删除
				throw MessageException.create(monitorClient.getError(jsonObject), ResultType.failure_in_monitor);
			}
		}
		
		// isso删除用户
		if (StrUtils.checkParam(issoClient)) {
			IssoUserOpt userOpt = new IssoUserOpt(issoClient);
			JSONObject jsonObject = userOpt.get(entity.getLoginName());
			if (issoClient.checkResult(jsonObject)) {
				jsonObject = userOpt.delete(issoClient.getValue(jsonObject, "id"));
				if (!issoClient.checkResult(jsonObject)) {
					throw MessageException.create(issoClient.getError(jsonObject), ResultType.failure_in_isso);
				}
			}
		}
	}
	
	private boolean isCtTenant(String projectId) {
		return projectId.equals(singleton.getCtTenantId());
	}
	
	@Override
	public boolean checkUserExpireTime(User entity) {
		// 生效时间
		long efDate = StrUtils.getTime(entity.getEffectiveDate(), false);
		// 失效时间
		long exDate = StrUtils.getTime(entity.getExpireDate(), true);
		// 当前时间
		long current = System.currentTimeMillis();
		if(efDate > current || current > exDate) {
			return false;
		}
		return true;
	}

	@Override
	public SessionBean createSessionBean(User user, String sysFlag, String remoteIp) {
        // 验证用户是否在有效时间内
        if(!this.checkUserExpireTime(user)) {
            throw MessageException.create(ResultType.date_mapping_status_error);
        }

        if (ConfigProperty.NO.equals(user.getStatus())) {
            throw MessageException.create(ResultType.user_status_error);// 用户状态异常
        }

        Map<String, Role> roleMap = this.getUserRoleMap(user, sysFlag);
        if (roleMap == null) {
            throw MessageException.create(ResultType.user_role_error);// 用户状态异常
        }

        // 生成token
        String tokenKey = StrUtils.getUUID(ConfigProperty.PROJECT_TOKEN_IYUN_PROFIX);
        SessionBean sessionBean = new SessionBean(user, tokenKey);
        sessionBean.setRemoteIp(remoteIp);

        String superRoleId = CacheSingleton.getInstance().getCloudRoleId();
        for (String s : roleMap.keySet()) {
            if (s.equals(superRoleId)) {
                sessionBean.setSuperUser(true);
                break;
            }
        }

        // 设置key
        resourceBiz.getResourceBySessionBean(sessionBean);

        String projectId = sessionBean.getProjectId();
        if(projectId != null) {
            Project project = this.projectBiz.findById(Project.class, projectId);
            if(ConfigProperty.YES.equals(project.getSelfAllowed())) {
                sessionBean.setSelfAllowed(true);
            }
        }

        sessionBean.setRoleMap(roleMap);
        List<Vdc> vdcs = vdcBiz.findByPropertyName(Vdc.class, "projectId", user.getProjectId());
        if (StrUtils.checkParam(vdcs)) {
            String vdcId = vdcs.get(0).getId();
            sessionBean.setVdcId(vdcId);
        }

//        sessionBean.setPassword(bean.getPassword());
        Date lastDate = saveLogin2Log(ResultType.success, user.getId(), remoteIp, user.getLoginName());
        user.setLastLoginDate(lastDate); // 显示最后登录时间
        SessionRedisUtils.setValue2Redis(tokenKey, sessionBean);
		return sessionBean;
	}

    @Override
    public Date saveLogin2Log(ResultType result, String userId, String remoteIp, String loginName) {
        OperateLogs log = new OperateLogs();
		loginName = loginName == null ? "not_fount_loginName" : loginName;
        userId = userId == null ? "error_" + loginName : userId;
        log.createdUser(userId);
        String logTypeId = OperateLogEnum.LOGIN.getLogTypeId();
        log.setLogTypeId(logTypeId);
        log.setResult(result.toString());
        Map<String, Object> map = StrUtils.createMap("remoteIp", remoteIp);
        map.put("result", log.getResult());
        map.put("date", log.getCreatedDate());
        map.put("loginName", loginName);
        LogType logType = logTypeDao.findById(LogType.class, logTypeId);
        if (StrUtils.checkParam(logType)){
            log.setRemark(logType.getDescription() + JSONObject.toJSONString(map));
        }else {
            log.setRemark(JSONObject.toJSONString(map));
        }
        Date lastDate = new Date();
        if (result == ResultType.success) {
            OperateLogs lastLog = operateLogsBiz.findLastDateByUserId(userId);
            if (lastLog != null) {
                lastDate = lastLog.getCreatedDate();
            }
        }
        log.setId(StrUtils.getUUID());
        log.setUserId(userId);
        log.setResourceId(userId);
        log.setResourceName(loginName);
        log.setIp(BaseRest.getIpAddress(request));
        log.setCreatedBy(loginName);
        log.setUpdatedBy(loginName);
        this.operateLogsBiz.add(log);
        return lastDate;
    }

    /**
	 * 获取用户对应角色
	 *
	 * @param user
	 * @return
	 */
	private Map<String, Role> getUserRoleMap(User user, String sysFlag) {
		Map<String, Role> roleMap = new HashMap<>();
		List<User2Role> set = userToRoleDao.findByPropertyName(User2Role.class, "userId", user.getId());
		String superRoleId = null;
		if (StrUtils.checkCollection(set)) {
			for (User2Role user2Role : set) {
				Role role = this.roleBiz.findById(Role.class, user2Role.getRoleId());
				if (role != null) {
					if (!CacheSingleton.getInstance().getWorkFlowRoleId().contains(role.getId())) {
						if (superRoleId == null) {
							superRoleId = role.getProleId();
						} else {
							if (!superRoleId.equals(role.getProleId())) { // 角色类型不一致
								return null;
							}
						}
					} else {
						// 非权签和区域经理审批权限则角色为云平台角色
						if(!CacheSingleton.getInstance().getSignRoleId().equals(role.getId())
								&& !CacheSingleton.getInstance().getChargeRoleId().equals(role.getId())) {
							superRoleId = singleton.getCloudRoleId();
						}
					}
					roleMap.put(role.getId(), role);
				}
			}
		}

        if(!StrUtils.checkCollection(set) || superRoleId == null) {
            // 根租户用户
            if(singleton.getRootProject().equals(user.getProjectId())) {
                if(!ConfigProperty.SYS_FLAG_YUNWEI.equals(sysFlag)) {	// 运维平台
                    return null;
                }
            } else {
                if(!ConfigProperty.SYS_FLAG_YUNYING.equals(sysFlag)) {	// 用户平台
                    return null;
                }
            }
            return roleMap;
        }
		if(StrUtils.checkParam(sysFlag) && !CacheSingleton.getInstance().isDevMode()) {
			// 云运营管理员不受登录限制
			if(!roleMap.containsKey(CacheSingleton.getInstance().getCloudRoleId())) {
				// 电信管理员或者租户管理员或者没有归属
				if(CacheSingleton.getInstance().getTenantRoleId().equals(superRoleId) ||
						CacheSingleton.getInstance().getCtRoleId().equals(superRoleId)) {
					if(!ConfigProperty.SYS_FLAG_YUNYING.equals(sysFlag)) {	// 用户平台
						return null;
					}
				} else if(!ConfigProperty.SYS_FLAG_YUNWEI.equals(sysFlag)) {	// 运维平台
					return null;
				}
			}
		}
		return roleMap;
	}

    @Override
    public void logout(String token, boolean isExpire) {
        // 处理过期后的一系列处理内容
        if(SessionRedisUtils.existKey(token)) {
            CloudosClient client = SimpleCache.CLOUDOS_CLIENT_MAP.remove(token);
            if(client != null) {
                client = null;
            }
            // 移除单点登录映射
            Map<String, String> t2tMap = CacheSingleton.getInstance().getTicket2TokenMap();
            String ticket = t2tMap.remove(token);
            if(null != ticket) {
                t2tMap.remove(ticket);
            }
            // 移除锁定的内容
            String userId = SimpleCache.TOKEN_TO_USER_MAP.remove(token);
            SessionRedisUtils.removeSession(token, userId);
            if(!isExpire) {	// 非失效需要手动清除
                SessionRedisUtils.delValue(token);
            }
        }
    }
	
	@Override
	public void update (User entity, String newPassword, boolean update) {
		String password = newPassword;
		try {
			if (StrUtils.checkParam(newPassword)) {
				update = true;
				String rePassword = PwdUtils.encrypt(newPassword, entity.getLoginName() + entity.getId());
				entity.setPassword(rePassword);
			} else {
				password = PwdUtils.decrypt(entity.getPassword(), entity.getLoginName() + entity.getId());
			}
			userDao.update(entity);
		} catch (Exception e) {
			throw MessageException.create(ResultType.failure);
		}
		
		//检查监控连接
		MonitorClient monitorClient = null;
		if (singleton.isMonitorSyn() && update) {
			monitorClient = MonitorClient.createClient(request.getHeader(ConfigProperty.PROJECT_TOKEN_KEY));
			if (null == monitorClient) {
				throw MessageException.create(ResultType.monitor_exception);
			}
		}
		//检查isso连接
		IssoClient adminClient = null;
		if (singleton.isIssoSyn() && StrUtils.checkParam(newPassword)) {
			adminClient = IssoClient.createAdmin();
			if (null == adminClient) {
				throw MessageException.create(ResultType.isso_exception);
			}
		}
		
		//cloudos修改密码
		if (StrUtils.checkParam(newPassword) && StrUtils.checkParam(entity.getCloudosId())) {
			this.updatePassword(entity.getCloudosId(), newPassword);
		}
		
		//isso修改
		if (null != adminClient) {
			IssoUserOpt userOpt = new IssoUserOpt(adminClient);
			JSONObject jsonObject = userOpt.get(entity.getLoginName());
			if (adminClient.checkResult(jsonObject)) {
				String issoUserId = adminClient.getValue(jsonObject, "id");
				IssoUserBean authUserBean = new IssoUserBean(entity);
				authUserBean.setPassword(password);
				jsonObject = userOpt.update(authUserBean, issoUserId);
				if (!adminClient.checkResult(jsonObject)) {
					throw MessageException.create(adminClient.getError(jsonObject), ResultType.failure_in_isso);
				}
			}
		}
		
		//monitor修改
		if (null != monitorClient) {
			UserVO userVO = UserVO.create(entity, password);
			JSONObject jsonObject = monitorClient.put(monitorClient.tranUrl(MonitorParams.MONITOR_API_USER_UPDATE,
					entity.getLoginName()), InvokeSetForm.tranClassToMap(userVO));
			if (!monitorClient.checkResult(jsonObject)) {//操作失败
				throw MessageException.create(monitorClient.getError(jsonObject), ResultType.failure_in_monitor);
			}
		}
	}
	
	private void updatePassword(String cloudosId, String newPassword) {
		if(!this.getProjectId().equals(CacheSingleton.getInstance().getCtTenantId())) {
			Map<String, Object> params = new HashMap<>();
			Map<String, Object> userMap = new HashMap<>();
			userMap.put("password", newPassword);
			params.put("user", userMap);
			CloudosClient client = CloudosClient.createAdmin();
			if(client == null) {
				throw MessageException.create(ResultType.system_error);
			}
			String uri = HttpUtils.getUrl(CloudosParams.CLOUDOS_API_USERS_ACTION, cloudosId);
			JSONObject result = client.patch(uri, params);
			if(!ResourceHandle.judgeResponse(result)) {
				throw MessageException.create(HttpUtils.getError(result), ResultType.system_error);
			}
		}
	}
	
	
}

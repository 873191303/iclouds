package com.h3c.iclouds.biz.impl;

import com.alibaba.fastjson.JSONObject;
import com.h3c.iclouds.auth.CacheSingleton;
import com.h3c.iclouds.base.BaseBizImpl;
import com.h3c.iclouds.base.BaseDAO;
import com.h3c.iclouds.biz.DifferencesBiz;
import com.h3c.iclouds.biz.RoleBiz;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.dao.ResourceDao;
import com.h3c.iclouds.dao.RoleDao;
import com.h3c.iclouds.dao.UserDao;
import com.h3c.iclouds.exception.MessageException;
import com.h3c.iclouds.operate.CloudosClient;
import com.h3c.iclouds.operate.CloudosParams;
import com.h3c.iclouds.po.Resource;
import com.h3c.iclouds.po.Role;
import com.h3c.iclouds.po.Role2Res;
import com.h3c.iclouds.po.User;
import com.h3c.iclouds.po.User2Role;
import com.h3c.iclouds.utils.ResourceHandle;
import com.h3c.iclouds.utils.HttpUtils;
import com.h3c.iclouds.utils.StrUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("unchecked")
@Service("roleBiz")
public class RoleBizImpl extends BaseBizImpl<Role> implements RoleBiz {

	@javax.annotation.Resource
	private RoleDao roleDao;
	
	@javax.annotation.Resource
	private UserDao userDao;
	
	@javax.annotation.Resource
	private ResourceDao resourceDao;
	
	@javax.annotation.Resource(name = "baseDAO")
	private BaseDAO<Role2Res> roleToResDao;
	
	@javax.annotation.Resource(name = "baseDAO")
	private BaseDAO<User2Role> userToRoleDao;
	
	@javax.annotation.Resource
	private DifferencesBiz differencesBiz;
	
	@Override
	public PageModel<Role> findForPage(PageEntity entity) {
		return this.roleDao.findForPage(entity);
	}

	@Override
	public ResultType update(Role entity, Map<String, Object> map, String type) {
		if(type.equals("resource")) {
			List<Role2Res> set = roleToResDao.findByPropertyName(Role2Res.class, "roleId", entity.getId());
			List<String> resourceArray = (ArrayList<String>) map.get("resources");
			
			if(resourceArray != null && !resourceArray.isEmpty()) {
				String proId = entity.getProleId();
				// 只要区分用户平台和运营平台
				int index = proId.equals(singleton.getTenantRoleId()) ? 0 : 1;

				Set<Role2Res> resources = new HashSet<>();
				StringBuffer errorBuffer = new StringBuffer();
				for (int i = 0; i < resourceArray.size(); i++) {
					Role2Res r2r = new Role2Res();
					r2r.setResId(resourceArray.get(i));
					r2r.createdUser(this.getSessionBean().getUserId());
					r2r.setRoleId(entity.getId());
					resources.add(r2r);
					
					// 修改云管理员角色不受限制
					if(!entity.getId().equals(CacheSingleton.getInstance().getCloudRoleId())) {
						Resource resource = this.resourceDao.findById(Resource.class, r2r.getResId());
						if(ConfigProperty.NO.equals(resource.getAuthType())) {	// 不可再授权
							return ResultType.unAuthorized;
						}

						String sysType = resource.getSysType();
						if(!ConfigProperty.YES.equals(sysType.substring(index, index + 1))) {	// 当前角色无权限授权该资源菜单
							this.info("资源 [" + resource.getResName() + "][" + resource.getId() + 
									"]与角色 [" + entity.getRoleName() + "][" + entity.getId() + "]不匹配");
							errorBuffer.append("资源 [" + resource.getResName() + 
									"]与角色 [" + entity.getRoleName() + "]不匹配,");
						}
					}
				}
				if(errorBuffer.length() > 0) {
					throw new MessageException(errorBuffer.deleteCharAt(errorBuffer.length() - 1).toString());
				}
				
				if(resources != null && !resources.isEmpty()) {
					for (Role2Res r2r : resources) {
						this.roleToResDao.add(r2r);
					}
				}
			}
			
			if(set != null && !set.isEmpty()) {
				for (Role2Res r2r : set) {
					this.roleToResDao.delete(r2r);
				}
			}
		}
		
		if(type.equals("user")) {
			List<String> newUserIds = new ArrayList<>();
			if (StrUtils.checkParam(map.get("users"))) {//预防空指针
				List<String> userIds = (ArrayList<String>) map.get("users");//获取新授权的用户id
				if (StrUtils.checkCollection(userIds)) {
					newUserIds = userIds;
				}
			}
			List<User2Role> user2Roles = this.userToRoleDao.findByPropertyName(User2Role.class, "roleId", entity.getId());
			List<String> oldUserIds = new ArrayList<>();
			if (StrUtils.checkCollection(user2Roles)) {//获取原来的该角色下的用户id集合
				for (User2Role user2Role : user2Roles) {
					oldUserIds.add(user2Role.getUserId());
				}
			}
			//当重新授权的用户没有发生改变时-前后都为空或前后一致
			if (newUserIds.size() == oldUserIds.size() && newUserIds.containsAll(oldUserIds)) {
				return ResultType.success;
			}
			List<String> tempList = new ArrayList<>();//中转list
			tempList.addAll(oldUserIds);
			//对于新授权的用户和原来用户的交集不操作
			oldUserIds.removeAll(newUserIds);//获取取消该角色的授权用户id
			newUserIds.removeAll(tempList);//获取新授权该角色的用户id
			ResultType rs = checkRole(newUserIds, oldUserIds, entity.getId());
			if (!ResultType.success.equals(rs)) {
				return rs;
			}
			List<String> userIds = new ArrayList<>();
			userIds.addAll(oldUserIds);
			userIds.addAll(newUserIds);
			for (String userId : userIds) {//对所有角色有变动的用户在cloudos重新授权
				User user = userDao.findById(User.class, userId);
				//当租户不为电信以及给用户授权了特殊权限时需要重新授权
				if (!singleton.getCtTenantId().equals(user.getProjectId()) && checkCloudosRole(entity.getId())) {
					rs = cloudosUpdateRole(oldUserIds, userId, user.getCloudosId(), user.getProjectId(), entity.getId());
					if (!ResultType.success.equals(rs)) {
						return rs;
					}
				}
			}
			updateRole(oldUserIds, newUserIds, entity.getId());
		}
		return ResultType.success;
	}
	
	@Override
	public List<User> getUser (String roleId) {
		String rootTenantId = singleton.getRootProject();
		List<String> commonList = Arrays.asList(singleton.getTenantRoleId());//只获取非H3C下用户
		if (commonList.contains(roleId)) {
			String hql = "FROM User WHERE projectId != '"+rootTenantId+"'";
			return userDao.findByHql(hql);
		}
		List<String> allList = Arrays.asList(singleton.getChargeRoleId(), singleton.getSignRoleId());//获取所有用户
		if (allList.contains(roleId)) {
			return userDao.getAll(User.class);
		}
		List<String> h3cList = Arrays.asList(singleton.getHandleRoleId(), singleton.getDispatchRoleId(), singleton
				.getCloudRoleId(), singleton.getOperationRoleId(), singleton.getEwoFirstRoleId(), singleton.getEwoSecondRoleId());
		//获取H3C下用户
		if (h3cList.contains(roleId)) {
			List<User> users = userDao.findByPropertyName(User.class, "projectId", rootTenantId);
			List<User> removeList = new ArrayList<>();
			if (StrUtils.checkCollection(users)) {
				for (User user : users) {
					Map<String, Object> queryMap = new HashMap<>();
					queryMap.put("userId", user.getId());
					if (roleId.equals(singleton.getCloudRoleId())) {//云管理员-过滤掉已有运营管理员身份用户
						queryMap.put("roleId", singleton.getOperationRoleId());
						List<User2Role> user2Roles = userToRoleDao.listByClass(User2Role.class, queryMap);
						if (StrUtils.checkCollection(user2Roles)) {
							removeList.add(user);
						}
					} else if (roleId.equals(singleton.getOperationRoleId())) {//运营管理员-过滤掉已有云管理员身份用户
						queryMap.put("roleId", singleton.getCloudRoleId());
						List<User2Role> user2Roles = userToRoleDao.listByClass(User2Role.class, queryMap);
						if (StrUtils.checkCollection(user2Roles)) {
							removeList.add(user);
						}
					}
				}
			}
			if (StrUtils.checkCollection(removeList)) {
				for (User user : removeList) {
					users.remove(user);
				}
			}
			return users;
		}
		Role role = roleDao.findById(Role.class, roleId);
		return userDao.findByPropertyName(User.class, "projectId", role.getProjectId());
	}
	
	public JSONObject getUserJson(String cloudosId, CloudosClient client) {
		if (!StrUtils.checkParam(cloudosId)) {
			return null;
		}
		String uri = HttpUtils.tranUrl(singleton.getCloudosApi(CloudosParams.CLOUDOS_API_USERS_ACTION), cloudosId);
		JSONObject json = HttpUtils.getJson(uri, "user", client);
		return json;
	}
	
	/**
	 * cloudos更新授权
	 * @param oldUserIds
	 * @param userId
	 * @param userCdId
	 * @param projectId
	 * @return
	 */
	private ResultType cloudosUpdateRole(List<String> oldUserIds, String userId, String userCdId, String projectId,
										 String roleId) {
		CloudosClient client = this.getSessionBean().getCloudClient();
		if (null == client) {
			return ResultType.system_error;
		}
		JSONObject userJson = getUserJson(userCdId, client);
		if (!StrUtils.checkParam(userJson)) {
			return ResultType.user_not_exist_in_cloudos;
		}
		List<User2Role> user2Roles = userToRoleDao.findByPropertyName(User2Role.class, "userId", userId);
		List<String> roleIds = new ArrayList<>();
		for (User2Role user2Role : user2Roles) {
			roleIds.add(user2Role.getRoleId());
		}
		//获取用户之前对应的cloudos角色
		String beforeRoleId = differencesBiz.getGroupId(roleIds, projectId);
		if (oldUserIds.contains(userId)) {
			roleIds.remove(roleId);
		} else {
			roleIds.add(roleId);
		}
		//获取用户之后对应的cloudos角色
		String afterRoleId = differencesBiz.getGroupId(roleIds, projectId);
		if (!beforeRoleId.equals(afterRoleId)) {
			//当用户前后对应的角色不一致时则先清除原本角色再授权新角色
			String uri = HttpUtils.tranUrl(HttpUtils.getUrl(CloudosParams.CLOUDOS_API_GRANT),
					projectId, userCdId, beforeRoleId);
			JSONObject obj = client.delete(uri);//删除以前角色
			if(!ResourceHandle.judgeResponse(obj)) {
				this.warn("Delete cloudos role[" + beforeRoleId + "] error, operate user[" + userId
						+ "][cloudos ID:" + userCdId + "]");
				return ResultType.user_cloudos_group_error;
			}
			uri = uri.replace(beforeRoleId, afterRoleId);
			obj = client.put(uri);//授权新角色
			if(!ResourceHandle.judgeResponse(obj)) {
				this.warn("Grant cloudos role[" + afterRoleId + "] error, operate user[" + userId
						+ "][cloudos ID:" + userCdId + "]");
				uri = uri.replace(afterRoleId, beforeRoleId);
				obj = client.put(uri);//出错时将原来角色授权回去
				if (!ResourceHandle.judgeResponse(obj)) {
					this.warn("Grant back cloudos role[" + beforeRoleId + "] error, operate user[" + userId
							+ "][cloudos ID:" + userCdId + "]");
				}
				return ResultType.user_cloudos_group_error;
			}
		}
		return ResultType.success;
	}
	
	private void updateRole(List<String> oldUserIds, List<String> newUserIds, String roleId) {
		if (StrUtils.checkCollection(oldUserIds)) {
			//取消这些用户的该角色
			for (String oldUserId : oldUserIds) {
				Map<String, Object> deleteMap = new HashMap<>();
				deleteMap.put("userId", oldUserId);
				deleteMap.put("roleId", roleId);
				userToRoleDao.delete(deleteMap, User2Role.class);
			}
		}
		if (StrUtils.checkCollection(newUserIds)) {
			//给这些用户授权该角色
			for (String newUserId : newUserIds) {
				User2Role user2Role = new User2Role();
				user2Role.setUserId(newUserId);
				user2Role.setRoleId(roleId);
				user2Role.createdUser(this.getLoginUser());
				userToRoleDao.add(user2Role);
			}
		}
	}
	
	/**
	 * 检查给用户授权是否存在冲突
	 * @return
	 */
	private ResultType checkRole(List<String> newUserIds, List<String> oldUsers, String roleId) {
		if (StrUtils.checkCollection(newUserIds)) {
			List<String> commonList = Arrays.asList(singleton.getTenantRoleId());//只能授权给非H3C下用户的角色
			List<String> h3cList = Arrays.asList(singleton.getHandleRoleId(), singleton.getDispatchRoleId(), singleton
					.getCloudRoleId(), singleton.getOperationRoleId(), singleton.getEwoFirstRoleId(), singleton.getEwoSecondRoleId());//只能授权给H3C下用户的角色
			List<String> allList = Arrays.asList(singleton.getChargeRoleId(), singleton.getSignRoleId());//获取所有用户
			for (String userId : newUserIds) {
				User user = userDao.findById(User.class, userId);
				if (!StrUtils.checkParam(user)) {
					return ResultType.user_not_exist;
				}
				if (commonList.contains(roleId)) {
					if (singleton.getRootProject().equals(user.getProjectId())) {
						return ResultType.role_cannot_auth_to_root_project;
					}
				} else if (h3cList.contains(roleId)) {
					if (!singleton.getRootProject().equals(user.getProjectId())) {
						return ResultType.role_only_auth_to_root_project;
					}
				} else if (!allList.contains(roleId)) {//自定义角色判断租户是否一致
					Role role = roleDao.findById(Role.class, roleId);
					if (!role.getProjectId().equals(user.getProjectId())) {
						return ResultType.role_add_user_project_error;
					}
				}
				//租户管理员不能与云管理员和运营管理员属于同一个用户
				Map<String, Object> queryMap = new HashMap<>();
				queryMap.put("userId", userId);
				if (roleId.equals(singleton.getTenantRoleId())) {
					queryMap.put("roleId", singleton.getCloudRoleId());
					User2Role user2Role = userToRoleDao.singleByClass(User2Role.class, queryMap);
					if (StrUtils.checkParam(user2Role)) {
						return ResultType.user_cannot_both_have_tenant_and_cloud;
					}
					queryMap.put("roleId", singleton.getOperationRoleId());
					user2Role = userToRoleDao.singleByClass(User2Role.class, queryMap);
					if (StrUtils.checkParam(user2Role)) {
						return ResultType.user_cannot_both_have_tenant_and_operation;
					}
				} else if (roleId.equals(singleton.getCloudRoleId())) {
					queryMap.put("roleId", singleton.getTenantRoleId());
					User2Role user2Role = userToRoleDao.singleByClass(User2Role.class, queryMap);
					if (StrUtils.checkParam(user2Role)) {
						return ResultType.user_cannot_both_have_tenant_and_cloud;
					}
					queryMap.put("roleId", singleton.getOperationRoleId());
					user2Role = userToRoleDao.singleByClass(User2Role.class, queryMap);
					if (StrUtils.checkParam(user2Role)) {
						return ResultType.user_cannot_both_have_operation_and_cloud;
					}
				} else if (roleId.equals(singleton.getOperationRoleId())) {
					queryMap.put("roleId", singleton.getCloudRoleId());
					User2Role user2Role = userToRoleDao.singleByClass(User2Role.class, queryMap);
					if (StrUtils.checkParam(user2Role)) {
						return ResultType.user_cannot_both_have_operation_and_cloud;
					}
					queryMap.put("roleId", singleton.getTenantRoleId());
					user2Role = userToRoleDao.singleByClass(User2Role.class, queryMap);
					if (StrUtils.checkParam(user2Role)) {
						return ResultType.user_cannot_both_have_tenant_and_operation;
					}
				}
			}
		}
		if (StrUtils.checkCollection(oldUsers)) {
			for (String userId : oldUsers) {
				User user = userDao.findById(User.class, userId);
				if (!StrUtils.checkParam(user)) {
					return ResultType.user_not_exist;
				}
				if (singleton.getRootProject().equals(user.getProjectId())) {
					String hql = "FROM User2Role WHERE userId = '"+userId+"' AND roleId != '"+roleId+"'";
					List<User2Role> user2Roles = userToRoleDao.findByHql(hql);
					if (!StrUtils.checkCollection(user2Roles)) {
						return ResultType.role_select_least_one;
					}
				}
			}
		}
		return ResultType.success;
	}
	
	/**
	 * 检查是否需要在cloudos重新授权(当授权或取消授权的角色为租户管理员或云管理员时需在cloudos重新授权)
	 * @param roleId
	 * @return
	 */
	private boolean checkCloudosRole(String roleId) {
		if (singleton.getTenantRoleId().equals(roleId)) {
			return true;
		}
		if (singleton.getCloudRoleId().equals(roleId)) {
			return true;
		}
		return false;
	}
}

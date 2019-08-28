package com.h3c.iclouds.rest;

import com.h3c.iclouds.auth.CacheSingleton;
import com.h3c.iclouds.auth.Perms;
import com.h3c.iclouds.base.BaseDAO;
import com.h3c.iclouds.base.BaseRest;
import com.h3c.iclouds.base.BaseRestControl;
import com.h3c.iclouds.biz.RoleBiz;
import com.h3c.iclouds.biz.WorkRoleBiz;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageList;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.dao.UserDao;
import com.h3c.iclouds.exception.MessageException;
import com.h3c.iclouds.po.Role;
import com.h3c.iclouds.po.Role2Res;
import com.h3c.iclouds.po.User;
import com.h3c.iclouds.po.User2Role;
import com.h3c.iclouds.po.WorkRole;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 只有归属租户才能才做对应角色
 * @author zkf5485
 *
 */
@RestController
@RequestMapping("/role")
@Api(value = "角色管理", description = "角色管理")
public class
RoleRest extends BaseRest<Role> {
	
	@Resource
	private RoleBiz roleBiz;
	
	@Resource(name = "baseDAO")
	private BaseDAO<Role2Res> roleToResDao;
	
	@Resource(name = "baseDAO")
	private BaseDAO<User2Role> userToRoleDao;
	
	@Resource
	private WorkRoleBiz workRoleBiz;
	
	@Resource
	private UserDao userDao;
	
	@RequestMapping(value = "/user/{userId}", method = RequestMethod.GET)
	@ApiOperation(value = "获取用户可授权角色列表")
	@Perms(value = "sm.ope.user.role")
	public Object list(@PathVariable String userId) {
		User entity = userDao.findById(User.class, userId);
		if(entity != null) {
			// 非超级管理员只允许查看主机租户内的用户
			if(!this.getSessionBean().getSuperUser()) {
				if(!this.getProjectId().equals(entity.getProjectId())) {
					return BaseRestControl.tranReturnValue(ResultType.unAuthorized);
				}
			}
			// 顶级租户用户
			if(singleton.getRootProject().equals(entity.getProjectId())) {
				List<Role> list = this.roleBiz.findByPropertyName(Role.class, "projectId", singleton.getRootProject());
				if(StrUtils.checkCollection(list)) {
					Iterator<Role> it = list.iterator();
					while(it.hasNext()) {
						Role role = it.next();
						String roleId = role.getId();
						if(StrUtils.contains(roleId, singleton.getCtRoleId(), singleton.getTenantRoleId())) {
							it.remove();
						}
					}
				}
				return BaseRestControl.tranReturnValue(list);
			} else if(singleton.getCtTenantId().equals(entity.getProjectId())) {
				List<Role> projectRoleList = this.roleBiz.findByPropertyName(Role.class, "projectId", singleton.getCtTenantId());
				String[] array = {singleton.getChargeRoleId(), singleton.getSignRoleId(), singleton.getCtRoleId()};
				Map<String, Object> map = StrUtils.createMap("id", array);
				List<Role> workRoleList = this.roleBiz.listByClass(Role.class, map);
				projectRoleList.addAll(workRoleList);
				return BaseRestControl.tranReturnValue(projectRoleList);
			} else {
				String[] array = {singleton.getChargeRoleId(), singleton.getSignRoleId()};
				Map<String, Object> map = StrUtils.createMap("id", array);
				List<Role> workRoleList = this.roleBiz.listByClass(Role.class, map);
				List<Role> projectRoleList = this.roleBiz.findByPropertyName(Role.class, "id", singleton.getTenantRoleId());
				projectRoleList.addAll(workRoleList);
				return BaseRestControl.tranReturnValue(projectRoleList);
			}
		}
		return BaseRestControl.tranReturnValue(Collections.EMPTY_LIST);
	}
	
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	@ApiOperation(value = "分页获取角色列表")
	@Perms(value = "sm.ope.role.simple")
	public Object list() {
		PageEntity entity = this.beforeList();
		PageModel<Role> pageModel = roleBiz.findForPage(entity);
		PageList<Role> page = new PageList<Role>(pageModel, entity.getsEcho());
		return BaseRestControl.tranReturnValue(page);
	}

	@ApiOperation(value = "获取角色基本列表")
	@Perms(value = "sm.ope.role.simple")
	@RequestMapping(value="/sublist", method = RequestMethod.GET)
	public Object listForSelect(){
		PageEntity entity = this.beforeList();
		entity.setPageSize(-1);
		PageModel<Role> pageModel = roleBiz.findForPage(entity);
		PageList<Role> page = new PageList<Role>(pageModel, entity.getsEcho());
		return BaseRestControl.tranReturnValue(page.getAaData());
	}
		
	@ApiOperation(value = "删除角色")
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	@Perms(value = "sm.ope.role")
	public Object delete(@PathVariable String id) {
		Role entity = roleBiz.findById(Role.class, id);
		if(entity != null) {
			if(!baseInfoCheck(entity)) {	// 角色类型不匹配
				return BaseRestControl.tranReturnValue(ResultType.unAuthorized);
			}
			
			List<WorkRole> list = workRoleBiz.findByPropertyName(WorkRole.class, "roleId", entity.getId());
			if(list == null || list.isEmpty()) {
				try {
					roleBiz.delete(entity);
				} catch (Exception e) {
					this.exception(this.getClass(), e);
					return BaseRestControl.tranReturnValue(ResultType.failure);
				}
				return BaseRestControl.tranReturnValue(ResultType.success);
			}
			return BaseRestControl.tranReturnValue(ResultType.workflow_role_use);
		}
		return BaseRestControl.tranReturnValue(ResultType.deleted);
	}
	
	@ApiOperation(value = "获取角色详细信息", response = Role.class)
	@Perms(value = "sm.ope.role")
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public Object get(@PathVariable String id) {
		Role entity = roleBiz.findById(Role.class, id);
		if(entity != null) {
			// 审批角色
			if(CacheSingleton.getInstance().getChargeRoleId().equals(id) ||
					CacheSingleton.getInstance().getSignRoleId().equals(id)) {
				return BaseRestControl.tranReturnValue(entity);
			}
			
			// 云运营管理员不受限
			if(this.getSessionBean().getSuperUser()) {
				return BaseRestControl.tranReturnValue(entity);
			}
			
			// 同一个租户
			if(entity.getProjectId().equals(this.getProjectId())) {
				if(entity.getProleId().equals(this.getSessionBean().getSuperRoleId())) {	// 对应管理员才允许操作
					if(id.equals(this.getSessionBean().getSuperRoleId()) && !this.getSessionBean().getSuperRole()) {
						return BaseRestControl.tranReturnValue(ResultType.unAuthorized);
					}
					return BaseRestControl.tranReturnValue(entity);
				}
			} else {
				// 选择的角色为系统初始化角色，当前用户必须为初始化管理员
				if(entity.getId().equals(this.getSessionBean().getSuperRoleId()) && this.getSessionBean().getSuperRole()) {
					return BaseRestControl.tranReturnValue(entity);
				}
			}
			return BaseRestControl.tranReturnValue(ResultType.unAuthorized);
		}
		return BaseRestControl.tranReturnValue(ResultType.deleted);
	}
	
	@ApiOperation(value = "保存新的角色", response = Role.class)
	@Perms(value = "sm.ope.role")
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public Object save(@RequestBody Role entity) {
		Map<String, String> validatorMap = ValidatorUtils.validator(entity);
		if(validatorMap.isEmpty()) {
			Map<String, Object> checkMap = StrUtils.createMap("roleName", entity.getRoleName());
			if (!roleBiz.checkRepeat(Role.class, checkMap)) {
				return BaseRestControl.tranReturnValue(ResultType.name_repeat);
			}
			entity.createdUser(this.getLoginUser());
			try {
				entity.setFlag(ConfigProperty.SM_ROLE_FLAG2_PROJECT);	// 角色类型为租户角色
				entity.setProjectId(this.getProjectId());	// 与当前用户同一个租户下
				entity.setProleId(this.getSessionBean().getSuperRoleId());
				roleBiz.add(entity);
				return BaseRestControl.tranReturnValue(ResultType.success, entity);
			} catch (Exception e) {
				this.exception(this.getClass(), e);
				return BaseRestControl.tranReturnValue(ResultType.failure);
			}
		}
		return BaseRestControl.tranReturnValue(ResultType.parameter_error, validatorMap);
	}
	
	@ApiOperation(value = "修改角色", response = Role.class)
	@Perms(value = "sm.ope.role")
	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	public Object update(@PathVariable String id, @RequestBody Role role) {
		Map<String, String> validatorMap = ValidatorUtils.validator(role);
		if(validatorMap.isEmpty()) {
			Role entity = roleBiz.findById(Role.class, id);
			if(entity != null) {
				if(!baseInfoCheck(entity)) {	// 角色类型不匹配
					return BaseRestControl.tranReturnValue(ResultType.unAuthorized);
				}
				Map<String, Object> checkMap = StrUtils.createMap("roleName", entity.getRoleName());
				if (!roleBiz.checkRepeat(Role.class, checkMap, id)) {
					return BaseRestControl.tranReturnValue(ResultType.name_repeat);
				}
				InvokeSetForm.copyFormProperties(role, entity);
				entity.updatedUser(this.getLoginUser());
				try {
					roleBiz.update(entity);
					return BaseRestControl.tranReturnValue(ResultType.success, entity);
				} catch (Exception e) {
					this.exception(this.getClass(), e);
					return BaseRestControl.tranReturnValue(ResultType.failure);
				}
			}
			return BaseRestControl.tranReturnValue(ResultType.deleted);
		}
		return BaseRestControl.tranReturnValue(ResultType.parameter_error, validatorMap);
	}
	
	/**
	 * 保存角色选择的资源
	 * @param map
	 * @param id
	 * @return
	 */
	@ApiOperation(value = "保存角色下的资源")
	@Perms(value = "sm.ope.role")
	@RequestMapping(value = "/{id}/resource", method = RequestMethod.POST)
	public Object resource(@PathVariable String id, @RequestBody Map<String, Object> map) {
		Role entity = this.roleBiz.findById(Role.class, id);
		if(entity == null) {
			return BaseRestControl.tranReturnValue(ResultType.deleted);
		}
		
		// 只有超级管理员可以修改映射
		if(!this.getSessionBean().getSuperUser()) {
			return BaseRestControl.tranReturnValue(ResultType.unAuthorized);
		}
		
		try {
			return BaseRestControl.tranReturnValue(this.roleBiz.update(entity, map, "resource"));
		} catch (MessageException e) {
			this.exception(this.getClass(), e);
			return BaseRestControl.tranReturnValue(ResultType.failure, e.getMessage());
		} catch (Exception e) {
			this.exception(this.getClass(), e);
			return BaseRestControl.tranReturnValue(ResultType.failure);
		}
	}
	
	public boolean baseInfoCheck(Role entity) {
		if(ConfigProperty.SM_ROLE_FLAG1_SYSTEM.equals(entity.getFlag())) {	// 系统角色不允许删除
			return false;
		}
		
		if(!entity.getProjectId().equals(this.getProjectId())) {	// 租户不匹配
			return false;
		}
		
		if(!entity.getProleId().equals(this.getSessionBean().getSuperRoleId())) {	// 当前用户归属角色类型不匹配
			return false;
		}
		
		return true;
	}
	
	/**
	 * 保存角色选择的用户
	 * @param map
	 * @param id
	 * @return
	 */
	@ApiOperation(value = "保存角色下的用户")
	@Perms(value = "sm.ope.role")
	@RequestMapping(value = "/{id}/user", method = RequestMethod.POST)
	public Object user(@PathVariable String id, @RequestBody Map<String, Object> map) {
		Role entity = this.roleBiz.findById(Role.class, id);
		if(!StrUtils.checkParam(entity)) {// 只有超级管理员可以修改映射
			return BaseRestControl.tranReturnValue(ResultType.deleted);
		}
		
		// 只有超级管理员可以修改映射
		if(!this.getSessionBean().getSuperUser()) {
			return BaseRestControl.tranReturnValue(ResultType.unAuthorized);
		}
		
		try {
			return BaseRestControl.tranReturnValue(this.roleBiz.update(entity, map, "user"));
		} catch (MessageException e) {
			this.exception(this.getClass(), e);
			return BaseRestControl.tranReturnValue(e.getResultCode(), e.getMessage());
		} catch (Exception e) {
			this.exception(this.getClass(), e);
			return BaseRestControl.tranReturnValue(ResultType.failure);
		}
	}
	
	/**
	 * 获取角色下的用户
	 * @param id
	 * @return
	 */
	@ApiOperation(value = "获取角色下的用户users", response = User.class)
	@RequestMapping(value = "/{id}/user", method = RequestMethod.GET)
	@Perms(value = "sm.ope.role")
	public Object userGet(@PathVariable String id) {
		Role role = roleBiz.findById(Role.class, id);
		if (!StrUtils.checkParam(role)) {
			return BaseRestControl.tranReturnValue(ResultType.role_not_exist);
		}
		Map<String, Object> map = new HashMap<>();
		List<User2Role> set = userToRoleDao.findByPropertyName(User2Role.class, "roleId", id);
		if(set != null && !set.isEmpty()) {
			List<String> userIds = new ArrayList<>();
			for (User2Role entity : set) {
				userIds.add(entity.getUserId());
			}
			map.put("authed", userIds);//已授权该角色用户
		}
		List<User> users = roleBiz.getUser(id);
		map.put("users", users);
		if (StrUtils.checkCollection(users)) {
			List<String> unAllowIds = new ArrayList<>();
			for (User user : users) {
				List<User2Role> user2Roles = userToRoleDao.findByPropertyName(User2Role.class, "userId", user.getId());
				if (singleton.getRootProject().equals(user.getProjectId()) && user2Roles.size() == 1 && user2Roles.get
						(0).getRoleId()
						.equals(id)) {
					unAllowIds.add(user.getId());//找出当前角色为用户唯一角色的h3c下的用户集合
				}
			}
			map.put("unAllowIds", unAllowIds);//当前角色为h3c下用户的唯一角色时不能取消授权
		}
		return BaseRestControl.tranReturnValue(map);
	}
	
	/**
	 * 获取角色下的资源
	 * @param id
	 * @return
	 */
	@ApiOperation(value = "获取角色下的资源resources", response = Role.class)
	@RequestMapping(value = "/{id}/resource", method = RequestMethod.GET)
	@Perms(value = "sm.ope.role")
	public Object resourceGet(@PathVariable String id) {
		Role role = this.roleBiz.findById(Role.class, id);
		if(!StrUtils.checkParam(role)) {// 只有超级管理员可以修改映射
			return BaseRestControl.tranReturnValue(ResultType.deleted);
		}
		if(!this.getSessionBean().getSuperUser()) {
			return BaseRestControl.tranReturnValue(ResultType.unAuthorized);
		}
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		List<Role2Res> set = roleToResDao.findByPropertyName(Role2Res.class, "roleId", id);
		if(set != null && !set.isEmpty()) {
			for (Role2Res entity : set) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("resId", entity.getResId());
				list.add(map);
			}
		}
		return BaseRestControl.tranReturnValue(list);
	}
	
	@ApiOperation(value = "角色名称验重", response = Role.class)
	@Perms(value = "sm.ope.role")
	@RequestMapping(value = "/check", method = RequestMethod.GET)
	public Object check() {
		String name = request.getParameter("name");
		Map<String, Object> checkMap = StrUtils.createMap("roleName", name);
		String id = StrUtils.tranString(request.getParameter("id"));
		boolean result = roleBiz.checkRepeat(Role.class, checkMap, id);
		return BaseRestControl.tranReturnValue(result ? ResultType.success : ResultType.failure);
	}
}

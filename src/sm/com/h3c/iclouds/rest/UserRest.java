package com.h3c.iclouds.rest;

import com.alibaba.fastjson.JSONObject;
import com.h3c.iclouds.auth.CacheSingleton;
import com.h3c.iclouds.auth.Perms;
import com.h3c.iclouds.base.BaseDAO;
import com.h3c.iclouds.base.BaseRest;
import com.h3c.iclouds.base.BaseRestControl;
import com.h3c.iclouds.biz.DepartmentBiz;
import com.h3c.iclouds.biz.DifferencesBiz;
import com.h3c.iclouds.biz.ProjectBiz;
import com.h3c.iclouds.biz.UserBiz;
import com.h3c.iclouds.common.CompareEnum;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageList;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.common.SimpleCache;
import com.h3c.iclouds.exception.MessageException;
import com.h3c.iclouds.operate.CloudosClient;
import com.h3c.iclouds.operate.CloudosUser;
import com.h3c.iclouds.opt.IssoClient;
import com.h3c.iclouds.opt.IssoUserOpt;
import com.h3c.iclouds.opt.MonitorClient;
import com.h3c.iclouds.opt.MonitorParams;
import com.h3c.iclouds.po.Department;
import com.h3c.iclouds.po.Project;
import com.h3c.iclouds.po.User;
import com.h3c.iclouds.po.User2Group;
import com.h3c.iclouds.po.User2Role;
import com.h3c.iclouds.utils.InvokeSetForm;
import com.h3c.iclouds.utils.LogUtils;
import com.h3c.iclouds.utils.PwdUtils;
import com.h3c.iclouds.utils.StrUtils;
import com.h3c.iclouds.validate.ValidatorUtils;
import com.h3c.iclouds.validate.ZabbixModify;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/user")
@Api(value = "用户管理", description = "用户管理")
public class UserRest extends BaseRest<User> {

	@Resource
	private UserBiz userBiz;

	@Resource(name = "baseDAO")
	private BaseDAO<User2Group> userToGroupDao;

	@Resource(name = "baseDAO")
	private BaseDAO<User2Role> userToRoleDao;

	@Resource
	private DepartmentBiz departmentBiz;
	
	@Resource
	private ProjectBiz projectBiz;
	
	@Resource
	private DifferencesBiz differencesBiz;
	
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	@Perms(value = "sm.ope.user.simple")
	@ApiOperation(value = "分页获取用户列表")
	public Object list() {
		PageEntity entity = this.beforeList();
		PageModel<User> pageModel = userBiz.findForPage(entity);
		PageList<User> page = new PageList<User>(pageModel, entity.getsEcho());
		return BaseRestControl.tranReturnValue(page);
	}
	
	
	
	
	//获取当前用户信息
	@RequestMapping(value = "/getuser", method = RequestMethod.POST)
	@ApiOperation(value = "获取当前用户信息", notes = "获取当前用户信息", response = User.class)
	public Object getUser() {
			Map<String, String> map = new HashMap<>();
			map.put("projectId", this.getProjectId());
			List<User> list =  userBiz.findByMap(User.class, map);
			User user = list.get(0);
			String name = user.getLoginName();
			String tell = user.getTelephone();
			String email = user.getEmail();
			Map<String, Object> newMap = new HashMap<>();
			newMap.put("name", name);
			newMap.put("tell", tell);
			newMap.put("email", email);
			return BaseRestControl.tranReturnValue(newMap);
	}
	
	@RequestMapping(value = "/{projectId}/list", method = RequestMethod.GET)
	@Perms(value = "sm.ope.user.simple")
	@ApiOperation(value = "分页获取用户列表")
	public Object list(@PathVariable String projectId) {
		Project project = projectBiz.findById(Project.class, projectId);
		if (null == project) {
			return BaseRestControl.tranReturnValue(ResultType.not_exist); // 该租户不存在
		}
		List<User> users = userBiz.findByPropertyName(User.class, "projectId", projectId);
		return BaseRestControl.tranReturnValue(users);
	}
	
	@RequestMapping(value = "/sublist", method = RequestMethod.GET)
	@ApiOperation(value = "获取用户基本列表")
//	@Perms(value = "sm.ope.user.simple")
	public Object sublist(){
		String projectId = this.getProjectId();
		Map<String, String> queryMap = new HashMap<String, String>();
		queryMap.put("status", ConfigProperty.YES);
		if(!this.getSessionBean().getSuperUser()) {
			queryMap.put("projectId", projectId);
		}
		List<User> userList = userBiz.findByClazz(User.class, queryMap, "id", "loginName", "userName", "email", "telephone", "deptName");
		return BaseRestControl.tranReturnValue(userList);
	}

	@ZabbixModify
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	@ApiOperation(value = "删除用户")
	@Perms(value = "sm.ope.user")
	public Object delete(@PathVariable String id) {
		User user = userBiz.findById(User.class, id);
		if (null == user) {
			return BaseRestControl.tranReturnValue(ResultType.deleted);
		}
		if(user.getId().equals(this.getLoginUser())) {
			return BaseRestControl.tranReturnValue(ResultType.delete_current_user);
		}
		if (!this.getSessionBean().getSuperUser() && !user.getProjectId().equals(this.getProjectId())) {
			return BaseRestControl.tranReturnValue(ResultType.unAuthorized);
		}
		Map<String, Object> map = StrUtils.createMap("userId", user.getId());
		map.put("roleId", CacheSingleton.getInstance().getCloudRoleId());
		List<User2Role> list = this.userToRoleDao.listByClass(User2Role.class, map);
		if(StrUtils.checkCollection(list)) {
			return BaseRestControl.tranReturnValue(ResultType.unAuthorized);
		}
		if (!differencesBiz.checkLocalResource(CompareEnum.USER, id, null)) {
			return BaseRestControl.tranReturnValue(ResultType.still_relate_resouces);
		}
		try {
			userBiz.deleteUser(user);
			this.destroyUserSession(id);
			this.warn("Delete user:" + StrUtils.toJSONString(user));
			return BaseRestControl.tranReturnValue(ResultType.success);
		} catch (Exception e) {
			this.exception(e, "Delete user [" + user.getLoginName() + "][" + user.getId() +"] failure, error :"
					+ e.getMessage());
			if(e instanceof MessageException) {
				return BaseRestControl.exceptionReturn((MessageException) e);
			}
			return BaseRestControl.tranReturnValue(ResultType.failure);
		}
	}

	/**
	 * 注销被删除的用户session
	 * @param userId
     */
	private void destroyUserSession(String userId) {
		Map<String, String> map = SimpleCache.TOKEN_TO_USER_MAP;
		map.forEach((k, v) -> {
			if(userId.equals(v)) {
				this.userBiz.logout(k, false);
			}
		});
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@ApiOperation(value = "获取用户详细信息", response = User.class)
//	@Perms(value = "sm.ope.user.simple")
	public Object get(@PathVariable String id) {
		User user = userBiz.findById(User.class, id);
		if (user != null) {
			if (!this.getSessionBean().getSuperUser() && !user.getProjectId().equals(this.getProjectId())) {
				return BaseRestControl.tranReturnValue(ResultType.unAuthorized);
			}

			if(!this.getSessionBean().getSuperRole()) {
				if(!id.equals(this.getLoginUser())) {
					return BaseRestControl.tranReturnValue(ResultType.unAuthorized);
				}
			}
			return BaseRestControl.tranReturnValue(user);
		}
		return BaseRestControl.tranReturnValue(ResultType.deleted);
	}
	
	@ZabbixModify
	@ApiOperation(value = "保存新的用户", response = User.class)
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	@Perms(value = "sm.ope.user")
	public Object save(@RequestBody Map<String, Object> map) throws IllegalAccessException, InvocationTargetException {
		if(!this.getSessionBean().getSuperRole()) {
			return BaseRestControl.tranReturnValue(ResultType.unAuthorized);
		}
		String projectId = this.getProjectId();
		return this.save(projectId, map);
	}
	
	@ZabbixModify
	@ApiOperation(value = "管理员保存租户下的用户", response = User.class)
	@RequestMapping(value = "/admin/save/{projectId}", method = RequestMethod.POST)
	@Perms(value = "sm.ope.user")
	public Object adminSave(@PathVariable String projectId, @RequestBody Map<String, Object> map) throws
			IllegalAccessException, InvocationTargetException {
		if(!this.getSessionBean().getSuperUser()) {
			return BaseRestControl.tranReturnValue(ResultType.unAuthorized);
		}
		if (projectId.equals(singleton.getRootProject())) {
			map.remove("role");
		}
		return this.save(projectId, map);
	}
	
	@ApiOperation(value = "修改用户", response = User.class)
	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	public Object update(@PathVariable String id, @RequestBody Map<String, Object> map) throws IllegalAccessException, InvocationTargetException {
		User before = new User();
		BeanUtils.populate(before, map);
		Map<String, String> validatorMap = ValidatorUtils.validator(before);
		if (!validatorMap.isEmpty()) {
			return BaseRestControl.tranReturnValue(ResultType.parameter_error, validatorMap);
		}
		if(!userBiz.checkUserExpireTime(before)) {
			before.setStatus(ConfigProperty.NO);
		} else {
			before.setStatus(ConfigProperty.YES);
		}
		User entity = userBiz.findById(User.class, id);
		if (null == entity) {
			return BaseRestControl.tranReturnValue(ResultType.deleted);
		}
		if (!projectBiz.checkOptionRole(entity.getProjectId(), entity.getId())) {
			return BaseRestControl.tranReturnValue(ResultType.unAuthorized);
		}
		String deptId = before.getDeptId();
		Department dept = this.departmentBiz.findById(Department.class, deptId);
		if(null == dept) {
			return BaseRestControl.tranReturnValue(ResultType.choose_department_not_exists);
		}
		if(!entity.getProjectId().equals(dept.getProjectId())) {
			return BaseRestControl.tranReturnValue(ResultType.department_mapping_user_error);
		}
		boolean update = false;
		if (StrUtils.checkParam(map.get("password")) || !before.getUserName().equals(entity.getUserName()) ||
				(null != before.getTelephone() && !before.getTelephone().equals(entity.getTelephone())) ||
				(null != before.getEmail() && !before.getEmail().equals(entity.getEmail()))) {
			update = true;
		}
		InvokeSetForm.copyFormProperties(before, entity);
		entity.updatedUser(this.getLoginUser());
		try {
			userBiz.update(entity, StrUtils.tranString(map.get("password")), update);
			return BaseRestControl.tranReturnValue(ResultType.success);
		} catch (Exception e) {
			this.exception(e, "Update user failure, value:" + StrUtils.toJSONString(entity) + ", error :" + e.getMessage());
			if (e instanceof MessageException) {
				return BaseRestControl.exceptionReturn((MessageException)e);
			}
			return BaseRestControl.tranReturnValue(ResultType.failure);
		}
	}

	@ApiOperation(value = "验证登录名是否存在")
	@Perms(value = "sm.ope.user.simple")
	@RequestMapping(value = "/check/loginName", method = RequestMethod.GET)
	public Object loginName(HttpServletRequest req) {
		String id = req.getParameter("id");
		String value = req.getParameter("value");
		if (!this.userBiz.checkRepeat(User.class, "loginName", value, id)) {
			return BaseRestControl.tranReturnValue(ResultType.repeat);
		}
		
		//在isso查重
		IssoClient adminClient = IssoClient.createAdmin();
		if (null == adminClient) {
			return BaseRestControl.tranReturnValue(ResultType.isso_exception);
		}
		IssoUserOpt userOpt = new IssoUserOpt(adminClient);
		JSONObject jsonObject = userOpt.checkRepeat(value);
		if (!adminClient.checkResult(jsonObject)) {
			return BaseRestControl.tranReturnValue(ResultType.repeat_in_isso);
		}
		return BaseRestControl.tranReturnValue(ResultType.success);
	}

	/**
	 * 保存用户选择的资源
	 *
	 * @param map
	 * @param id
	 * @return
	 */
	@ApiOperation(value = "保存用户下的群组")
	@Perms(value = "sm.ope.user.group")
	@RequestMapping(value = "/{id}/group", method = RequestMethod.POST)
	public Object group(@PathVariable String id, @RequestBody Map<String, Object> map) {
		return this.handleUserRole(id, "group", map);
	}

	/**
	 * 保存用户选择的资源
	 *
	 * @param map
	 * @param id
	 * @return
	 */
	@ApiOperation(value = "保存用户下的角色")
	@RequestMapping(value = "/{id}/role", method = RequestMethod.POST)
	@Perms(value = "sm.ope.user.role")
	public Object role(@PathVariable String id, @RequestBody Map<String, Object> map) {
		return this.handleUserRole(id, "role", map);
	}

	public Object handleUserRole(String id, String type, Map<String, Object> map) {
		// 不是管理员不允许操作
		if(!this.getSessionBean().getSuperRole()) {
			return BaseRestControl.tranReturnValue(ResultType.unAuthorized);
		}
		
		User entity = this.userBiz.findById(User.class, id);
		if (null == entity) {
			return BaseRestControl.tranReturnValue(ResultType.deleted);
		}
		try {
			// admin不允许修改
			if (!"989116e3-79a2-426b-bfbe-668165104885".equals(this.getLoginUser())
					&& entity.getLoginName().equals("admin")) {
				return BaseRestControl.tranReturnValue(ResultType.unAuthorized);
			}
			return BaseRestControl.tranReturnValue(this.userBiz.update(entity, map, type));
		} catch (Exception e) {
			this.exception(this.getClass(), e);
			if(e instanceof MessageException) {
				return BaseRestControl.tranReturnValue(ResultType.failure, e.getMessage());
			}
			return BaseRestControl.tranReturnValue(ResultType.failure);
		}
	}

	/**
	 * 获取角色下的资源
	 *
	 * @param id
	 * @return
	 */
	@ApiOperation(value = "获取用户下的角色roles和群组groups")
	@RequestMapping(value = "/{id}/{type}", method = RequestMethod.GET)
	@Perms(value = "sm.ope.user.simple")
	public Object resourceGet(@PathVariable String id, @PathVariable String type) {
		User user = this.userBiz.findById(User.class, id);
		if (user != null) {
			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
			if ("group".equals(type)) {
				List<User2Group> set = this.userToGroupDao.findByPropertyName(User2Group.class, "userId", id);
				if (set != null && !set.isEmpty()) {
					for (User2Group entity : set) {
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("gid", entity.getGid());
						map.put("flag", entity.getIsDefault());
						list.add(map);
					}
				}
			} else if ("role".equals(type)) {
				List<User2Role> set = this.userToRoleDao.findByPropertyName(User2Role.class, "userId", id);
				if (set != null && !set.isEmpty()) {
					for (User2Role entity : set) {
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("roleId", entity.getRoleId());
						list.add(map);
					}
				}
			}
			return BaseRestControl.tranReturnValue(list);
		}
		return BaseRestControl.tranReturnValue(ResultType.deleted);
	}

	@ApiOperation(value = "检查名称是否重复")
	@Perms(value = "sm.ope.user.simple")
	@RequestMapping(value = "/check/{name}", method = RequestMethod.GET)
	public Object checkRepeat(@PathVariable String name) {
		String id = request.getParameter("id");// 修改时传入一个id则查重时会排除对象本身
		boolean userNameRepeat = userBiz.checkRepeat(User.class, "userName", name, id);
		if (!userNameRepeat) {// 查重(用户名称)
			return BaseRestControl.tranReturnValue(ResultType.repeat);
		}
		return BaseRestControl.tranReturnValue(ResultType.success);
	}
	
	@ApiOperation(value = "更改密码")
	@RequestMapping(value = "/{id}/changepassword", method = RequestMethod.PUT)
	public Object changePassword(@PathVariable String id, @RequestBody Map<String, String> map) {
		User user = this.userBiz.findById(User.class, id);
		if (null == user) {
			return BaseRestControl.tranReturnValue(ResultType.deleted);
		}
		String newPassword = map.get("newPassword");
		String password = map.get("password");
		Pattern pattern = Pattern.compile("^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z_#$!@%&-]{6,32}$");
		if(!pattern.matcher(newPassword).matches() || !pattern.matcher(password).matches()){
			return BaseRestControl.tranReturnValue(ResultType.parameter_error);
		}
		if (!id.equals(getSessionBean().getUserId())) {
			return BaseRestControl.tranReturnValue(ResultType.unAuthorized);
		}
		boolean isValid = StrUtils.checkParam(password, newPassword);
		if (!isValid) {
			return BaseRestControl.tranReturnValue(ResultType.parameter_error);
		}
		
		String decryptPassword = null;
		try {
			decryptPassword = PwdUtils.decrypt(user.getPassword(), user.getLoginName() + user.getId());
		} catch (Exception e) {
			this.exception(this.getClass(), e);
			return BaseRestControl.tranReturnValue(ResultType.failure);
		}
		
		if (!password.equals(decryptPassword)) {
			return BaseRestControl.tranReturnValue(ResultType.old_password_error);
		}
		
		int length = newPassword.length();
		if (length < ConfigProperty.PASSWORD_MIN_LENGTH) {
			return BaseRestControl.tranReturnValue(ResultType.password_length_less_than_six);
		}
		if (length > ConfigProperty.PASSWORD_MAX_LENGTH) {
			return BaseRestControl.tranReturnValue(ResultType.password_too_long);
		}
		try {
			userBiz.update(user, newPassword, true);
			return BaseRestControl.tranReturnValue(ResultType.success);
		} catch (Exception e) {
			this.exception(e, "Update user password failure, error :" + e.getMessage());
			if (e instanceof MessageException) {
				return BaseRestControl.exceptionReturn((MessageException)e);
			}
			return BaseRestControl.tranReturnValue(ResultType.failure);
		}
		
	}
	
	@Override
	public Object save(User entity) {
		return null;
	}

	@Override
	public Object update(String id, User entity) throws IOException {
		return null;
	}
	
	private Object save(String projectId, Map<String, Object> map) throws IllegalAccessException, InvocationTargetException {
		String roleId = null;
		if (StrUtils.checkParam(map.get("role")) && ConfigProperty.AUTH_TENANT_ADMIN.equals(StrUtils.tranString(map.get("role")))) {
			roleId = this.singleton.getTenantRoleId();
			map.remove("role");
		}
		User entity = new User();
		BeanUtils.populate(entity, map);
		if (!StrUtils.checkParam(entity.getPassword())) {
			return BaseRestControl.tranReturnValue(ResultType.password_not_null);
		}
		entity.setProjectId(projectId);
		Map<String, String> validatorMap = ValidatorUtils.validator(entity);
		if (!validatorMap.isEmpty()) {
			return BaseRestControl.tranReturnValue(ResultType.parameter_error, validatorMap);
		}
		if (!this.userBiz.checkRepeat(User.class, "loginName", entity.getLoginName())) {
			return BaseRestControl.tranReturnValue(ResultType.login_name_exist);
		}
		if(!userBiz.checkUserExpireTime(entity)) {
			entity.setStatus(ConfigProperty.NO);
		} else {
			entity.setStatus(ConfigProperty.YES);
		}
		String deptId = entity.getDeptId();
		Department dept = this.departmentBiz.findById(Department.class, deptId);
		if(null == dept) {
			return BaseRestControl.tranReturnValue(ResultType.choose_department_not_exists);
		}
		if(!projectId.equals(dept.getProjectId())) {
			return BaseRestControl.tranReturnValue(ResultType.department_mapping_user_error);
		}
		
		//cloudos连接检查
		CloudosClient client = null;
		if(!projectId.equals(singleton.getCtTenantId())) {
			client = getSessionBean().getCloudClient();
			if (null == client) {
				throw MessageException.create(ResultType.system_error);
			}
		}
		
		//isso连接检查
		IssoClient issoClient = null;
		IssoUserOpt userOpt = null;
		if (singleton.isIssoSyn()) {
			issoClient = IssoClient.createAdmin();
			if (null == issoClient) {
				throw MessageException.create(ResultType.isso_exception);
			}
			userOpt = new IssoUserOpt(issoClient);
		}
		
		
		//监控连接检查
		MonitorClient monitorClient = null;
		/*
		if (singleton.isMonitorSyn()) {
			monitorClient = MonitorClient.createClient(request.getHeader(ConfigProperty.PROJECT_TOKEN_KEY));
			if (null == monitorClient) {
				throw MessageException.create(ResultType.monitor_exception);
			}
		}
		*/
		map.clear();
		try {
			userBiz.save(entity, roleId, map, client, issoClient, monitorClient);
			LogUtils.warn(User.class, "Save User Success");
			return BaseRestControl.tranReturnValue(ResultType.success, entity);
		} catch (Exception e) {
			this.exception(this.getClass(), e, "Save new user failure, value:" + StrUtils.toJSONString(entity) + ", " +
					"error: " + e.getMessage());
			if (StrUtils.checkParam(map.get("issoUserId"))) {
				userOpt.delete(StrUtils.tranString(map.get("issoUserId")));
			}
			/*
			if (StrUtils.checkParam(map.get("loginName"))) {
				monitorClient.delete(monitorClient.tranUrl(MonitorParams.MONITOR_API_USER_DELETE,
						entity.getLoginName()), null);
			}
			*/
			if (StrUtils.checkParam(entity.getCloudosId())) {
				CloudosUser cloudosUser = new CloudosUser(client);
				cloudosUser.delete(entity.getCloudosId());
			}
			if(e instanceof MessageException) {
				return BaseRestControl.exceptionReturn((MessageException) e);
			}
			return BaseRestControl.tranReturnValue(ResultType.failure);
		}
	}
}

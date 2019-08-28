package com.h3c.iclouds.rest;

import com.alibaba.fastjson.JSONObject;
import com.h3c.iclouds.base.BaseDAO;
import com.h3c.iclouds.base.BaseRestControl;
import com.h3c.iclouds.biz.ListPriceBiz;
import com.h3c.iclouds.biz.NetworkBiz;
import com.h3c.iclouds.biz.NovaVmBiz;
import com.h3c.iclouds.biz.ProjectBiz;
import com.h3c.iclouds.biz.RenewalBiz;
import com.h3c.iclouds.biz.Server2VmBiz;
import com.h3c.iclouds.biz.SpePortBiz;
import com.h3c.iclouds.biz.TaskBiz;
import com.h3c.iclouds.biz.UserBiz;
import com.h3c.iclouds.biz.VlbMemberBiz;
import com.h3c.iclouds.biz.VlbPoolBiz;
import com.h3c.iclouds.biz.VolumeBiz;
import com.h3c.iclouds.client.CasClient;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageList;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.common.TaskTypeProperty;
import com.h3c.iclouds.dao.NovaFlavorDao;
import com.h3c.iclouds.dao.NovaVmDao;
import com.h3c.iclouds.dao.NovaVmViewDao;
import com.h3c.iclouds.dao.ProjectDao;
import com.h3c.iclouds.dao.QuotaUsedDao;
import com.h3c.iclouds.dao.RenewalDao;
import com.h3c.iclouds.dao.VmTopDao;
import com.h3c.iclouds.exception.MessageException;
import com.h3c.iclouds.operate.CloudosClient;
import com.h3c.iclouds.operate.CloudosNovaVm;
import com.h3c.iclouds.operate.CloudosParams;
import com.h3c.iclouds.po.NovaFlavor;
import com.h3c.iclouds.po.NovaVm;
import com.h3c.iclouds.po.NovaVmView;
import com.h3c.iclouds.po.Renewal;
import com.h3c.iclouds.po.Project;
import com.h3c.iclouds.po.QuotaUsed;
import com.h3c.iclouds.po.Rules;
import com.h3c.iclouds.po.Server2Vm;
import com.h3c.iclouds.po.SpePort;
import com.h3c.iclouds.po.Subnet;
import com.h3c.iclouds.po.Task;
import com.h3c.iclouds.po.User;
import com.h3c.iclouds.po.VlbMember;
import com.h3c.iclouds.po.VlbPool;
import com.h3c.iclouds.po.VmTop;
import com.h3c.iclouds.po.Volume;
import com.h3c.iclouds.po.bean.cloudos.CloneOrImageBean;
import com.h3c.iclouds.po.bean.inside.SaveNovaVmBean;
import com.h3c.iclouds.po.bean.nova.MonitorInitBean;
import com.h3c.iclouds.po.bean.outside.NovaVmDetailBean;
import com.h3c.iclouds.po.bean.outside.ProjectDetailBean;
import com.h3c.iclouds.po.bean.outside.TenantNetworkBean;
import com.h3c.iclouds.po.business.ListPrice;
import com.h3c.iclouds.utils.HttpUtils;
import com.h3c.iclouds.utils.JacksonUtil;
import com.h3c.iclouds.utils.LogUtils;
import com.h3c.iclouds.utils.OpeAuth;
import com.h3c.iclouds.utils.StrUtils;
import com.h3c.iclouds.validate.ValidatorUtils;
import com.wordnik.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;
import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/server")
public class NovaVmRest extends BaseRestControl {

	@Resource
	private NovaVmDao novaVmDao;

	@Resource
	private ProjectDao projectDao;

	@Resource
	private NovaVmBiz novaVmBiz;

	@Resource
	private NetworkBiz networkBiz;

	@Resource(name = "baseDAO")
	private BaseDAO<Subnet> subnetDao;

	@Resource
	private VlbPoolBiz vlbPoolBiz;

	@Resource
	private TaskBiz taskBiz;

	@Resource
	private Server2VmBiz server2VmBiz;

	@Resource(name = "baseDAO")
	private BaseDAO<Rules> rulesDao;

	@Resource
	private UserBiz userBiz;

	@Resource
	private VlbMemberBiz memberBiz;

	@Resource
	private NovaFlavorDao flavorDao;

	@Resource
	private SpePortBiz spePortBiz;

	@Resource
	private ListPriceBiz listPriceBiz;

	@Resource
	private NovaVmViewDao novaVmViewDao;

	@Resource
	private VolumeBiz volumeBiz;

	@Resource
	private VmTopDao vmTopDao;
	
	@Resource
	private ProjectBiz projectBiz;
	
	//租期表
	@Resource
	private RenewalBiz renewalBiz;
	
	//租期表dao
	@Resource
	private RenewalDao renewalDao;
	
	
	@Resource
    private QuotaUsedDao quotaUsedDao;

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	@ApiOperation(value = "云主机列表查询")
	public Object list() {
		PageEntity entity = this.beforeList();
		PageModel<NovaVmView> pageModel = novaVmViewDao.findForPage(entity);
		PageList<NovaVmView> page = new PageList<>(pageModel, entity.getsEcho());
		return BaseRestControl.tranReturnValue(page);
	}
	//租期时间表展示
	@RequestMapping(value = "/renewal/list", method = RequestMethod.GET)
	@ApiOperation(value = "租期时间展示")
	public Object renewalList() {
		PageEntity entity = this.beforeList();
		PageModel<Renewal> pageModel = renewalDao.findForPage(entity);
		PageList<Renewal> page = new PageList<>(pageModel, entity.getsEcho());
		
		return BaseRestControl.tranReturnValue(page);
	}
	

	@RequestMapping(value = "/top/{type}", method = RequestMethod.GET)
	@ApiOperation(value = "云主机cpu和内存使用率的top5查询")
	public Object top(@PathVariable String type) {
		if (!StrUtils.equals(type, "cpu", "memory")) {
			return BaseRestControl.tranReturnValue(Collections.emptyList());
		}
		List<VmTop> list = this.vmTopDao.findTop(type, 5);
		return BaseRestControl.tranReturnValue(list);
	}

	@RequestMapping(value = "/tenant", method = RequestMethod.GET)
	@ApiOperation(value = "租户下的云主机列表（用于资源绑定到云主机;返回值 hostname-主机名；name-网络名，cidr-网络）[云硬盘]")
	public Object listByTenant() {
		PageEntity entity = this.beforeList();
		String userId = request.getParameter("userId");
		if (!StrUtils.checkParam(userId)) {
			userId = this.getLoginUser();
		} else {
			if (!this.getSessionBean().getSuperUser()) {
				if (!this.getSessionBean().getSuperRole()) {
					userId = this.getLoginUser();
				}
			}
		}
		entity.setSpecialParam(userId);
		PageModel<NovaVmView> pageModel = novaVmViewDao.findForPage(entity);
		PageList<NovaVmView> page = new PageList<>(pageModel, entity.getsEcho());
		return BaseRestControl.tranReturnValue(page);
	}
	//云主机续租 wxg 2018-08-30
	@RequestMapping(value = "/{id}/renewal ", method = RequestMethod.POST)
	@ApiOperation(value = "云主机续租")
	public Object Renewal(@PathVariable("id") String id,@RequestBody JSONObject objStr) {
		// 设置日志的资源id
		this.request.setAttribute("id", id);
		// 验证操作权限
		NovaVm novaVm = novaVmDao.findById(NovaVm.class, id);
		if (novaVm != null) {
			// 设置日志的资源名称
			this.request.setAttribute("name", novaVm.getHostName());
		}
		OpeAuth opeAuth = new OpeAuth();
		ResultType result = opeAuth.checkInTask(novaVm);

		// ResultType result = this.checkOpeAuth(novaVm, false);
		if (result != ResultType.success) {
			return BaseRestControl.tranReturnValue(result);
		}
		result = opeAuth.checkStopOrReboot(novaVm);
//		if (result != ResultType.success) {
//			return BaseRestControl.tranReturnValue(result);
//		}
		result = opeAuth.checkStatus(novaVm);
//		if (result != ResultType.success) {
//			return BaseRestControl.tranReturnValue(result);
//		}
		result = checkAuth(novaVm);
//		if (result != ResultType.success) {
//			return BaseRestControl.tranReturnValue(result);
//		}
		//获取关联用户的projectid
		try {
			String time = objStr.get("time").toString(); 
			ResultType rss = renewalBiz.toReanewal(time, id, "yzj"); //novaVmBiz.Renewal(ren, userId, time);
			return BaseRestControl.tranReturnValue(rss);

		} catch (Exception e) {
			LogUtils.exception(this.getClass(), e);
			if (e instanceof MessageException) {
				return BaseRestControl.tranReturnValue(((MessageException) e).getResultCode());
			}
			return BaseRestControl.tranReturnValue(ResultType.failure);
		}
		
	}
	
	//云主机杀毒wxg 2018-08-28
	@RequestMapping(value = "/{id}/viruses", method = RequestMethod.POST)
	@ApiOperation(value = "云主机杀毒功能")
	public Object Viruses(@PathVariable("id") String id) {
		// 设置日志的资源id
		this.request.setAttribute("id", id);
		// 验证操作权限
		NovaVm novaVm = novaVmDao.findById(NovaVm.class, id);
		if (novaVm != null) {
			// 设置日志的资源名称
			this.request.setAttribute("name", novaVm.getHostName());
		}
		OpeAuth opeAuth = new OpeAuth();
		ResultType result = opeAuth.checkInTask(novaVm);

		if (result != ResultType.success) {
			return BaseRestControl.tranReturnValue(result);
		}
		result = opeAuth.checkStopOrReboot(novaVm);
		if (result != ResultType.success) {
			return BaseRestControl.tranReturnValue(result);
		}
		result = opeAuth.checkStatus(novaVm);
		if (result != ResultType.success) {
			return BaseRestControl.tranReturnValue(result);
		}
		result = checkAuth(novaVm);
		if (result != ResultType.success) {
			return BaseRestControl.tranReturnValue(result);
		}
		return null;
	}
	//云主机快照wxg 2018-08-28
	@RequestMapping(value = "/{id}/snapshot", method = RequestMethod.POST)
	@ApiOperation(value = "云主机快照功能")
	public Object snapshot(@PathVariable("id") String id) {
		// 设置日志的资源id
		this.request.setAttribute("id", id);
		// 验证操作权限
		NovaVm novaVm = novaVmDao.findById(NovaVm.class, id);
		if (novaVm != null) {
			// 设置日志的资源名称
			this.request.setAttribute("name", novaVm.getHostName());
		}
		OpeAuth opeAuth = new OpeAuth();
		ResultType result = opeAuth.checkInTask(novaVm);

		// ResultType result = this.checkOpeAuth(novaVm, false);
		if (result != ResultType.success) {
			return BaseRestControl.tranReturnValue(result);
		}
		result = opeAuth.checkStopOrReboot(novaVm);
		if (result != ResultType.success) {
			return BaseRestControl.tranReturnValue(result);
		}
		result = opeAuth.checkStatus(novaVm);
		if (result != ResultType.success) {
			return BaseRestControl.tranReturnValue(result);
		}
		result = checkAuth(novaVm);
		if (result != ResultType.success) {
			return BaseRestControl.tranReturnValue(result);
		}
		try {
				ResultType rs = novaVmBiz.snapshot(novaVm.getId());
				return BaseRestControl.tranReturnValue(rs);
		} catch (Exception e) {
			LogUtils.exception(this.getClass(), e);
			if (e instanceof MessageException) {
				return BaseRestControl.tranReturnValue(((MessageException) e).getResultCode());
			}
			return BaseRestControl.tranReturnValue(ResultType.failure);
		}
		
	}

	// 云主机密码重置wxg 2018-08-17
	@RequestMapping(value = "/{id}/updatepwd", method = RequestMethod.POST)
	@ApiOperation(value = "云主机密码重置功能")
	public Object updatePwd(@PathVariable("id") String id, @RequestBody JSONObject objStr) {
		// 设置日志的资源id
		this.request.setAttribute("id", id);
		// 验证操作权限
		NovaVm novaVm = novaVmDao.findById(NovaVm.class, id);
		if (novaVm != null) {
			// 设置日志的资源名称
			this.request.setAttribute("name", novaVm.getHostName());
		}
		OpeAuth opeAuth = new OpeAuth();
		ResultType result = opeAuth.checkInTask(novaVm);

		// ResultType result = this.checkOpeAuth(novaVm, false);
		if (result != ResultType.success) {
			return BaseRestControl.tranReturnValue(result);
		}
		result = opeAuth.checkStopOrReboot(novaVm);
		if (result != ResultType.success) {
			return BaseRestControl.tranReturnValue(result);
		}
		result = opeAuth.checkStatus(novaVm);
		if (result != ResultType.success) {
			return BaseRestControl.tranReturnValue(result);
		}
		result = checkAuth(novaVm);
		if (result != ResultType.success) {
			return BaseRestControl.tranReturnValue(result);
		}
		try {
			//查询旧密码和密码是否匹配
			String oldPwd = novaVmBiz.selPassWord(novaVm);
			if(objStr.get("oldPassWord").equals(oldPwd)) {
				//在这里进行业务处理
				String userId = this.getLoginUser();
				// 返回提示信息
				ResultType rs = novaVmBiz.update(null,novaVm.getId(), userId, objStr.get("newPassWord").toString());
				return BaseRestControl.tranReturnValue(rs);
			}else {
				return BaseRestControl.tranReturnValue(ResultType.password_error);
			}

		} catch (Exception e) {
			LogUtils.exception(this.getClass(), e);
			if (e instanceof MessageException) {
				return BaseRestControl.tranReturnValue(((MessageException) e).getResultCode());
			}
			return BaseRestControl.tranReturnValue(ResultType.failure);
		}
	}
	//租户配额已经使用 wxg 2018-08-23
	@RequestMapping(value = "/quotaused", method = RequestMethod.GET)
	@ApiOperation(value = "获取配额已经使用情况")
	public Object quotaUsed() {
		Map<String, Object> queryMap = new HashMap<>();
		queryMap.put("tenantId", this.getProjectId());
		queryMap.put("deleted", 0);
		List<QuotaUsed> list = quotaUsedDao.listByClass(QuotaUsed.class, queryMap);
		return BaseRestControl.tranReturnValue(ResultType.success, list);
	}
	
	//查询配额信息wxg 2018-08-23
	@ApiOperation(value = "获取云管理租户配额", response = Project.class)
	@RequestMapping(value = "/{id}/quota", method = RequestMethod.GET)
	public Object get(@PathVariable String id) {
		// 验证操作权限
		NovaVm novaVm = novaVmDao.findById(NovaVm.class, id);
		if (novaVm != null) {
			// 设置日志的资源名称
			this.request.setAttribute("name", novaVm.getHostName());
		}
		//String projectId = novaVm.getProjectId();
		String projectId = this.getProjectId();
//		if (!getSessionBean().getSuperUser()) {
//			return BaseRestControl.tranReturnValue(ResultType.unAuthorized);
//		}
		try {
			Project project = projectBiz.get(projectId);
			if (null != project) {
				project = projectBiz.transProject(project);
				ProjectDetailBean projectDetailBean = projectBiz.get(project);
				projectDetailBean.setProject(project);
				return BaseRestControl.tranReturnValue(ResultType.success, projectDetailBean);
			}
			return BaseRestControl.tranReturnValue(ResultType.deleted);
		} catch (Exception e) {
			this.exception(Project.class, e, projectId, "获取详细信息异常");
			if (e instanceof MessageException) {
				return BaseRestControl.tranReturnValue(((MessageException) e).getResultCode());
			}
			this.exception(Project.class, e);
			return BaseRestControl.tranReturnValue(ResultType.failure);
		}
	}
	
	
	

	@RequestMapping(value = "/{id}/detail", method = RequestMethod.GET)
	@ApiOperation(value = "云主机详细信息")
	public Object detail(@PathVariable String id) {
		// 设置日志的资源id
		this.request.setAttribute("id", id);
		// 验证操作权限
		NovaVm novaVm = novaVmDao.findById(NovaVm.class, id);
		if (novaVm == null) {
			return BaseRestControl.tranReturnValue(ResultType.novam_not_exist);
		}
		this.request.setAttribute("name", novaVm.getHostName());
		ResultType result = this.checkAuth(novaVm);
		if (result != ResultType.success) {
			return BaseRestControl.tranReturnValue(result);
		}
		NovaVmDetailBean bean;
		try {
			bean = novaVmBiz.detail(novaVm);
			return BaseRestControl.tranReturnValue(bean);
		} catch (Exception e) {
			e.printStackTrace();
			return BaseRestControl.tranReturnValue(ResultType.failure);
		}
	}

	/**
	 * 判断操作权限
	 * 
	 * @param entity
	 * @param isDeleted
	 * @return
	 */
	private ResultType checkOpeAuth(NovaVm entity, boolean isDeleted) {
		CloudosClient client = getSessionBean().getCloudClient();
		if (entity == null) {
			return ResultType.not_exist;
		}
		String vmState = entity.getVmState();
		if (!this.getSessionBean().getSuperUser()) {
			String projectId = this.getProjectId();
			if (!projectId.equals(entity.getProjectId())) {
				return ResultType.tenant_role_cannot_use_other_error;
			}
		}
		ConcurrentHashMap<Integer, String> map = ConfigProperty.novaVmState;
		if (map.get(4).equals(vmState) || map.get(6).equals(vmState)) {
			return ResultType.novavm_state_illegal;
		}
		// 主机对应在cloudos的uuid未写入
		if (!StrUtils.checkParam(entity.getUuid())) {
			return ResultType.state_creating;
		}

		Map<String, Object> params = new HashMap<>();
		params.put("busId", entity.getId());
		Task task = taskBiz.singleByClass(Task.class, params);
		if (StrUtils.checkParam(task)) {
			if (!StrUtils.checkParam(client)) {
				return ResultType.system_error;
			}
			CloudosNovaVm cloudosNovaVm = new CloudosNovaVm(client);
			if (ConfigProperty.novaVmState.get(1).equals(entity.getVmState())) {
				if (cloudosNovaVm.check(entity)) {
					taskBiz.delete(task);
					return ResultType.success;
				}
			}

			// 判断cloudos上的云主机是否正常的，且本地是正常，则删除任务记录往下走，这是由于执行任务异常
			return ResultType.in_handle_task;
		}
		return ResultType.success;
	}

	@RequestMapping(value = "/{id}/console", method = RequestMethod.POST)
	@ApiOperation(value = "云主机控制台")
	public Object console(@PathVariable String id) {
		// 设置日志的资源id
		this.request.setAttribute("id", id);
		// 验证操作权限
		NovaVm novaVm = novaVmDao.findById(NovaVm.class, id);
		if (novaVm != null) {
			// 设置日志的资源名称
			this.request.setAttribute("name", novaVm.getHostName());
		}
		ResultType resultType = checkAuth(novaVm);
		if (resultType != ResultType.success) {
			return BaseRestControl.tranReturnValue(resultType);
		}
		ResultType result = this.checkOpeAuth(novaVm, false);
		if (result != ResultType.success) {
			return BaseRestControl.tranReturnValue(result);
		}
		String url = HttpUtils.getUrl(CloudosParams.CLOUDOS_API_SERVER_ACTION, this.getProjectId(), novaVm.getUuid());
		String params = "{\"os-getVNCConsole\": {\"type\": \"novnc\"}}";
		CloudosClient client = this.getSessionBean().getCloudClient();
		JSONObject jsonObj = client.post(url, JSONObject.parseObject(params));
		if (!StrUtils.checkParam(jsonObj)) {
			return BaseRestControl.tranReturnValue(ResultType.vnc_server_error);
		}
		// 获取结果
		jsonObj = HttpUtils.getJSONObject(jsonObj, "console");
		if (jsonObj != null) {
			String vncUrl = jsonObj.getString("url");
			if (StrUtils.checkParam(vncUrl)) {
				try {
					URI uri = URI.create(vncUrl); // 转成URI对象，用于提取端口和token
					Map<String, Object> resultMap = StrUtils.createMap("port", uri.getPort());
					String[] queries = uri.getQuery().split("&");
					for (String query : queries) {
						if ("token".equals(query.split("=")[0].toLowerCase())) {
							String token = query.split("=")[1];
							resultMap.put("port", uri.getPort());
							resultMap.put("token", token);
							// resultMap.put("server",
							// CacheSingleton.getInstance().getConfigValue("iclouds.cloudos.api.ip"));
							break;
						}
					}
					return BaseRestControl.tranReturnValue(resultMap);
				} catch (Exception e) {
					this.exception(getClass(), e);
				}
			}
		}
		return BaseRestControl.tranReturnValue(ResultType.failure);
	}

	@RequestMapping(value = "/{id}/{type}", method = RequestMethod.POST)
	@ApiOperation(value = "开|关云主机（start|stop）")
	public Object action(@PathVariable String id, @PathVariable String type) {
		// 设置日志的资源id
		this.request.setAttribute("id", id);
		// 验证操作权限
		NovaVm novaVm = novaVmDao.findById(NovaVm.class, id);
		if (novaVm != null) {
			// 设置日志的资源名称
			this.request.setAttribute("name", novaVm.getHostName());
		}
		OpeAuth opeAuth = new OpeAuth();
		ResultType result = opeAuth.checkOpeAuth(novaVm, false);
		if (result != ResultType.success) {
			return BaseRestControl.tranReturnValue(result);
		}
		result = opeAuth.checkStatus(novaVm);
		if (result != ResultType.success) {
			return BaseRestControl.tranReturnValue(result);
		}
		result = checkAuth(novaVm);
		if (result != ResultType.success) {
			return BaseRestControl.tranReturnValue(result);
		}
		String vmState = null;
		Map<String, Object> params = null;
		String busType = null;
		switch (type) {
		case "start": // 开机
			result = opeAuth.checkStart(novaVm);
			if (result != ResultType.success) {
				return BaseRestControl.tranReturnValue(result);
			}
			vmState = ConfigProperty.novaVmState.get(8);
			params = StrUtils.createMap(CloudosParams.SERVER_ACTION_OS_START, "null");
			busType = TaskTypeProperty.VM_STARTUP;
			break;
		case "stop": // 关机
			result = opeAuth.checkStopOrReboot(novaVm);
			if (result != ResultType.success) {
				return BaseRestControl.tranReturnValue(result);
			}
			vmState = ConfigProperty.novaVmState.get(9);
			params = StrUtils.createMap(CloudosParams.SERVER_ACTION_OS_STOP, "null");
			busType = TaskTypeProperty.VM_SHUTDOWN;
			break;
		default:
			break;
		}
		try {
			if (null == vmState) { // 不存在对应类型的操作
				return BaseRestControl.tranReturnValue(ResultType.parameter_error);
			}
			this.novaVmBiz.action(novaVm, vmState, params, busType);
		} catch (Exception e) {
			this.exception(this.getClass(), e);
			return BaseRestControl.tranReturnValue(ResultType.failure);
		}
		return BaseRestControl.tranReturnValue(ResultType.success);
	}

	@RequestMapping(value = "/{id}/reboot/{type}", method = RequestMethod.POST)
	@ApiOperation(value = "重启云主机")
	public Object reboot(@PathVariable String id, @PathVariable String type) {
		// 设置日志的资源id
		this.request.setAttribute("id", id);
		// 验证操作权限
		NovaVm novaVm = novaVmDao.findById(NovaVm.class, id);
		if (novaVm != null) {
			// 设置日志的资源名称
			this.request.setAttribute("name", novaVm.getHostName());
		}
		OpeAuth opeAuth = new OpeAuth();
		ResultType result = opeAuth.checkInTask(novaVm);

		// ResultType result = this.checkOpeAuth(novaVm, false);
		if (result != ResultType.success) {
			return BaseRestControl.tranReturnValue(result);
		}
		result = opeAuth.checkStopOrReboot(novaVm);
		if (result != ResultType.success) {
			return BaseRestControl.tranReturnValue(result);
		}
		result = opeAuth.checkStatus(novaVm);
		if (result != ResultType.success) {
			return BaseRestControl.tranReturnValue(result);
		}
		result = checkAuth(novaVm);
		if (result != ResultType.success) {
			return BaseRestControl.tranReturnValue(result);
		}
		String vmState = ConfigProperty.novaVmState.get(7);
		Map<String, Object> params = new HashMap<String, Object>();
		Map<String, Object> typeMap = new HashMap<String, Object>();
		if (!("SOFT".equals(type) || "HARD".equals(type))) {
			return BaseRestControl.tranReturnValue(ResultType.parameter_error);
		}
		typeMap.put("type", type);
		params.put(CloudosParams.SERVER_ACTION_OS_REBOOT, typeMap);
		try {
			this.novaVmBiz.action(novaVm, vmState, params, TaskTypeProperty.VM_REBOOT);
		} catch (Exception e) {
			LogUtils.exception(NovaVm.class, e, "云主机[" + id + "]" + "非任务部分异常", "重启类型" + type);
			return BaseRestControl.tranReturnValue(ResultType.failure);
		}
		return BaseRestControl.tranReturnValue(ResultType.success);
	}

	@RequestMapping(value = "/create", method = RequestMethod.POST)
	@ApiOperation(value = "创建云主机")
	public Object save(@RequestBody SaveNovaVmBean bean) {
		Map<String, String> validatorMap = ValidatorUtils.validator(bean);
		if (!validatorMap.isEmpty()) {
			return BaseRestControl.tranReturnValue(ResultType.parameter_error, validatorMap);
		}
		if (!this.getSessionBean().getSelfAllowed()) {
			return BaseRestControl.tranReturnValue(ResultType.project_cannot_create_resource);
		}
		bean.setProjectId(this.getProjectId());
		return save(bean, null, null);
	}

	@RequestMapping(value = "/admin/create/{userId}", method = RequestMethod.POST)
	@ApiOperation(value = "管理员替用户创建云主机")
	public Object save(@PathVariable String userId, @RequestBody SaveNovaVmBean bean) {
		User user = userBiz.findById(User.class, userId);
		Map<String, Object> check = this.adminSave(bean.getProjectId(), user);
		if (null != check) {
			return check;
		}
		bean.setProjectId(user.getProjectId());
		return save(bean, userId, user.getLoginName());
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	@ApiOperation(value = "云主机进回收站")
	public Object delete(@PathVariable String id) {
		// 设置日志的资源id
		this.request.setAttribute("id", id);
		NovaVm novaVm = novaVmDao.findById(NovaVm.class, id);
		if (novaVm != null) {
			// 设置日志的资源名称
			this.request.setAttribute("name", novaVm.getHostName());
		}
		int count = volumeBiz.count(Volume.class, StrUtils.createMap("host", id));
		if (count > 0) {
			return BaseRestControl.tranReturnValue(ResultType.cloud_host_exist_volume);
		}
		// 验证操作权限
		ResultType resultType = checkAuth(novaVm);
		if (resultType != ResultType.success) {
			return BaseRestControl.tranReturnValue(resultType);
		}
		if (!StrUtils.checkParam(novaVm)) {
			LogUtils.warn(NovaVm.class, "novavm deleted");
			return BaseRestControl.tranReturnValue(ResultType.novam_not_exist);
		}
		OpeAuth opeAuth = new OpeAuth();
		resultType = opeAuth.checkIng(novaVm);
		if (resultType != ResultType.success) {
			return BaseRestControl.tranReturnValue(resultType);
		}
		// 检查云主机是否关联实服务成员
		List<VlbMember> members = memberBiz.findByPropertyName(VlbMember.class, "vmId", id);
		if (StrUtils.checkCollection(members)) {
			return BaseRestControl.tranReturnValue(ResultType.still_relate_vlbMember);
		}
		try {
			Object result = novaVmBiz.delete(id);
			if (result instanceof List) {
				return BaseRestControl.tranReturnValue(ResultType.file_cloudhost_delect_hasclouddisk, result);
			}
			return BaseRestControl.tranReturnValue(result);
		} catch (Exception e) {
			LogUtils.exception(this.getClass(), e);
			if (e instanceof MessageException) {
				return BaseRestControl.tranReturnValue(((MessageException) e).getResultCode());
			}
			return BaseRestControl.tranReturnValue(ResultType.failure);
		}
	}

	@ApiOperation(value = "获取创建云主机的基础网络", response = TenantNetworkBean.class)
	@RequestMapping(value = "/tenantNetwork", method = RequestMethod.GET)
	public Object getTenantNetwork() {
		String tenantId = request.getParameter("projectId");
		if (this.getSessionBean().getSuperUser()) {// 管理员获取租户的列表，非管理员只能获取自己的列表
			if (!StrUtils.checkParam(tenantId)) {
				tenantId = this.getProjectId();
			}
		} else {
			tenantId = this.getProjectId();
		}
		// 租户网络
		Project project = projectDao.getExistProject(tenantId);
		if (project == null) {
			return BaseRestControl.tranReturnValue(ResultType.tenant_not_exist);
		}
		List<TenantNetworkBean> beans = networkBiz.getTenantNetwork(tenantId);
		// 自定义数据以后要删除
		// beans = my_test_init_TenantNetworkBean(beans);
		Map<String, Object> result = new HashMap<>();
		result.put("networks", beans);
		return BaseRestControl.tranReturnValue(result);
	}
	// //自定义数据以后要删除
	// private List<TenantNetworkBean>
	// my_test_init_TenantNetworkBean(List<TenantNetworkBean> beans){
	// if(beans==null || beans.size() == 0) {
	// beans = new ArrayList<TenantNetworkBean>();
	// TenantNetworkBean tenantNetworkBean= new TenantNetworkBean();
	// tenantNetworkBean.setCidr("ceshi1");
	// tenantNetworkBean.setId("1111111111111111111");
	// tenantNetworkBean.setName("ceshi1");
	// beans.add(tenantNetworkBean);
	//
	// tenantNetworkBean= new TenantNetworkBean();
	// tenantNetworkBean.setCidr("ceshi2");
	// tenantNetworkBean.setId("1111111111111111112");
	// tenantNetworkBean.setName("ceshi2");
	// beans.add(tenantNetworkBean);
	//
	// }
	// return beans;
	// }

	@ApiOperation(value = "获取实服务云主机列表", response = NovaVm.class)
	@RequestMapping(value = "/{poolId}/list", method = RequestMethod.GET)
	public Object serverList(@PathVariable String poolId) {
		VlbPool vlbPool = vlbPoolBiz.findById(VlbPool.class, poolId);
		if (null == vlbPool) {
			return BaseRestControl.tranReturnValue(ResultType.vlbPool_not_exist);
		}
		String factSubnetId = vlbPool.getFactSubnetId();
		Subnet subnet = subnetDao.findById(Subnet.class, factSubnetId);
		if (null == subnet) {
			return BaseRestControl.tranReturnValue(ResultType.network_not_exist);
		}
		List<Map<String, Object>> list = novaVmBiz.getServerPort(factSubnetId);
		Map<String, Object> map = new HashMap<>();
		map.put("aaData", list);
		return BaseRestControl.tranReturnValue(map);
	}

	/**
	 * 验证开关机、重启及控制台,租户权限
	 */
	private ResultType checkAuth(NovaVm novaVm) {
		if (getSessionBean().getSuperUser()) {
			return ResultType.success;
		}
		if (!getSessionBean().getSuperRole()) {
			if (!getLoginUser().equals(novaVm.getOwner())) {
				return ResultType.unAuthorized;
			}
		} else {
			if (!this.getProjectId().equals(novaVm.getProjectId())) {
				return ResultType.unAuthorized;
			}
		}
		return ResultType.success;
	}

	/**
	 * 操作允许条件： 1.租户内的虚拟机 2.没有正在处理的任务 3.主机处理开机/或者关机状态 4.在cas表中能够查询的到 5.转换任务小于预设值
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/{id}/clone", method = RequestMethod.POST)
	@ApiOperation(value = "克隆云主机")
	public Object clone(@PathVariable String id, @RequestBody CloneOrImageBean bean) {
		String type = "clone";
		return this.ope(type, id, bean);
	}

	private Object ope(String type, String id, CloneOrImageBean bean) {
		Map<String, String> validatorMap = ValidatorUtils.validator(bean);
		if (!validatorMap.isEmpty()) {
			return BaseRestControl.tranReturnValue(ResultType.parameter_error);
		}
		// 设置日志的资源id
		this.request.setAttribute("id", id);

		if ("to-image".equals(type)) {
			// 转换为镜像需要验证镜像名称是否重复
			boolean userNameRepeat = rulesDao.checkRepeat(Rules.class, "osMirName", bean.getName());
			if (!userNameRepeat) {// 查重(用户名称)
				return BaseRestControl.tranReturnValue(ResultType.repeat);
			}
		} else if ("clone".equals(type)) {
			if (bean.getCount() == null) { // 克隆需要传递克隆数量
				return BaseRestControl.tranReturnValue(ResultType.parameter_error);
			}
			bean.setCloneMode(true);
		} else {
			return BaseRestControl.tranReturnValue(ResultType.failure);
		}

		NovaVm novaVm = novaVmDao.findById(NovaVm.class, id);
		if (novaVm == null) {
			return BaseRestControl.tranReturnValue(ResultType.deleted);
		}

		this.request.setAttribute("name", novaVm.getHostName()); // 设置日志的资源名称
		if (!this.getProjectId().equals(novaVm.getProjectId())) {
			return BaseRestControl.tranReturnValue(ResultType.unAuthorized);
		}

		if (!StrUtils.checkParam(novaVm.getUuid())) {
			return BaseRestControl.tranReturnValue(ResultType.server_status_exception);
		}

		String status = novaVm.getVmState();
		if (!ConfigProperty.novaVmState.get(2).equals(status)) {
			return BaseRestControl.tranReturnValue(ResultType.trans_vm_need_be_stop);
		}

		OpeAuth opeAuth = new OpeAuth();
		ResultType result = opeAuth.checkInTask(novaVm);
		if (result != ResultType.success) {
			return BaseRestControl.tranReturnValue(result);
		}

		Server2Vm entity = server2VmBiz.singleByClass(Server2Vm.class, StrUtils.createMap("uuid", novaVm.getUuid()));
		if (entity == null) { // 在cas数据中不存在
			return BaseRestControl.tranReturnValue(ResultType.server_status_exception);
		}

		try {
			CasClient casClient = CasClient.createByCasId(CasClient.CAS_INTERFACE_ID);
			if (casClient == null) {
				return BaseRestControl.tranReturnValue(ResultType.cloud_host_create_mode_error);
			}

			JSONObject baseCasVmObj = casClient.getStorePath(entity.getCasId());
			if (baseCasVmObj == null) {
				return BaseRestControl.tranReturnValue(ResultType.cloud_host_create_mode_error);
			}
		} catch (Exception e) {
			this.exception(e, ResultType.cloud_host_create_mode_error);
			return BaseRestControl.tranReturnValue(ResultType.cloud_host_create_mode_error);
		}

		// VM_TO_IMAGE：转为镜像，VM_CLONE：克隆
		String[] types = new String[] { TaskTypeProperty.VM_TO_IMAGE, TaskTypeProperty.VM_CLONE };
		// 检查正在执行的个数
		Map<String, Object> map = StrUtils.createMap("busType", Arrays.asList(types));
		List<Task> list = taskBiz.listByClass(Task.class, map);
		if (StrUtils.checkCollection(list)) {
			if (list.size() >= singleton.getVmToImageCount()) { // 超过预设的上限
				return BaseRestControl.tranReturnValue(ResultType.file_size_than_max);
			}
			// 检查正在执行的任务中是否存在含有选择的云主机
			for (Task task : list) {
				if (task.getBusId().equals(id)) {
					return BaseRestControl.tranReturnValue(ResultType.already_vm_to_image);
				}
			}
		}
		try {
			this.novaVmBiz.vmToImage(novaVm, entity, bean);
		} catch (Exception e) {
			this.exception(e, "Start Vm[" + entity.getVmName() + "] to " + type + " image failure.");
			return BaseRestControl.tranReturnValue(ResultType.failure);
		}
		return BaseRestControl.tranReturnValue(ResultType.success);
	}

	/**
	 * 操作允许条件： 1.租户内的虚拟机 2.没有正在处理的任务 3.主机处理开机/或者关机状态 4.在cas表中能够查询的到 5.转换任务小于预设值
	 * 
	 * @param id
	 * @param bean
	 * @return
	 */
	@RequestMapping(value = "/{id}/to-image", method = RequestMethod.POST)
	@ApiOperation(value = "云主机转为镜像,需要将云主机关机，如果存在任务可以取消任务")
	public Object toImage(@PathVariable String id, @RequestBody CloneOrImageBean bean) {
		String type = "to-image";
		return this.ope(type, id, bean);
	}

	private Object save(SaveNovaVmBean bean, String userId, String userName) {
		CloudosClient client;
		if (StrUtils.checkParam(userId)) {
			User user = userBiz.findById(User.class, userId);
			client = CloudosClient.create(user.getCloudosId(), userName);
		} else {
			client = this.getSessionBean().getCloudClient();
			userId = this.getLoginUser();
		}

		NovaFlavor flavor;
		if (!StrUtils.checkParam(bean.getFlavorId())) {
			int disk = bean.getRamdisk_gb();
			int ram = bean.getMemory_mb();
			int vcpus = bean.getVcpus();
			Map<String, Object> queryMap = new HashMap<>();
			queryMap.put("disk", disk);
			queryMap.put("ram", ram);
			queryMap.put("vcpus", vcpus);
			flavor = flavorDao.singleByClass(NovaFlavor.class, queryMap);
			if (!StrUtils.checkParam(flavor)) {
				return BaseRestControl.tranReturnValue(ResultType.novavm_flavor_not_exist);
			}
		} else {
			flavor = flavorDao.findById(NovaFlavor.class, bean.getFlavorId());
		}

		try {
			listPriceBiz.saveByNovaFlavor(flavor, this.getLoginUser(), bean.getAzoneId());
		} catch (Exception e) {
			LogUtils.exception(ListPrice.class, e);
		}
		bean.setFlavorId(flavor.getId());
		ResultType rs = novaVmBiz.verify(bean, client);
		if (!ResultType.success.equals(rs)) {
			return BaseRestControl.tranReturnValue(rs);
		}
		try {
			// 返回提示信息
			rs = novaVmBiz.save(bean, userId, client);
			return BaseRestControl.tranReturnValue(rs);
		} catch (Exception e) {
			if (e instanceof MessageException) {
				if (StrUtils.checkParam(bean.getError())) {
					return BaseRestControl.tranReturnValue(((MessageException) e).getResultCode(),
							bean.getError().toJSONString());
				} else {
					return BaseRestControl.tranReturnValue(((MessageException) e).getResultCode());
				}
			}
			this.exception(this.getClass(), e, JacksonUtil.toJSon(bean));
			return BaseRestControl.tranReturnValue(ResultType.failure);
		}
	}

	/**
	 * 移除监控
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/{id}/monitor", method = RequestMethod.DELETE)
	@ApiOperation(value = "初始化监控及第二张网卡记录")
	public Object initMonitor(@PathVariable String id) {
		return this.initMonitor(id, null);
	}

	/**
	 * 加入监控
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/{id}/monitor", method = RequestMethod.POST)
	@ApiOperation(value = "初始化监控及第二张网卡记录")
	public Object initMonitor(@PathVariable String id, @RequestBody MonitorInitBean bean) {
		this.request.setAttribute("id", id);
		if (bean != null) {
			Map<String, String> validatorMap = ValidatorUtils.validator(bean);
			if (!validatorMap.isEmpty()) {
				return BaseRestControl.tranReturnValue(ResultType.parameter_error, validatorMap);
			}
		}
		NovaVm entity = this.novaVmBiz.findById(NovaVm.class, id);
		if (null == entity) {
			return BaseRestControl.tranReturnValue(ResultType.not_exist);
		}
		this.request.setAttribute("name", entity.getHostName());
		ResultType result = checkAuth(entity);
		if (result != ResultType.success) {
			return BaseRestControl.tranReturnValue(result);
		}
		SpePort spePort = this.spePortBiz.singleByClass(SpePort.class, StrUtils.createMap("hostId", id));
		try {
			if (bean != null) {
				if (spePort != null) {
					return BaseRestControl.tranReturnValue(ResultType.already_join_monitor);
				}
				this.spePortBiz.initMonitor(bean, entity);
			} else {
				if (spePort == null) {
					return BaseRestControl.tranReturnValue(ResultType.not_join_monitor);
				}
				this.spePortBiz.removeMonitor(spePort);
			}
			return BaseRestControl.tranReturnValue(ResultType.success);
		} catch (MessageException e) {
			this.exception(e, e.getMessage());
			if (e.getResultCode() != null) {
				return BaseRestControl.tranReturnValue(e.getResultCode());
			} else {
				return BaseRestControl.tranReturnValue(ResultType.failure, e.getMessage());
			}
		} catch (Exception e) {
			e.printStackTrace();
			this.exception(e, "Unkown exception");
			return BaseRestControl.tranReturnValue(ResultType.failure);
		}
	}

	@ApiOperation(value = "我的云主机")
	@RequestMapping(value = "/{id}/count", method = RequestMethod.GET)
	public Object countByUser(@PathVariable String id) {
		List<NovaVm> novaVms = null;
		if (this.getSessionBean().getSuperUser()) {
			novaVms = novaVmBiz.findByPropertyName(NovaVm.class);
		} else if (this.getSessionBean().getSuperRole()) {
			novaVms = novaVmBiz.findByPropertyName(NovaVm.class, "projectId", this.getProjectId());
		} else {
			novaVms = novaVmBiz.findByPropertyName(NovaVm.class, "owner", id);
		}
		int vcpu = 0;
		int vmem = 0;
		int count = 0;
		int disk = 0;
		if (StrUtils.checkParam(novaVms)) {
			for (NovaVm novaVm : novaVms) {
				vcpu += novaVm.getVcpus();
				vmem += novaVm.getMemory();
				disk += novaVm.getRamdisk();
				count++;
			}
		}
		Map<String, Integer> map = new HashMap<>();
		map.put("count", count);
		map.put("vcpu", vcpu);
		map.put("vmem", vmem);
		map.put("disk", disk);
		return BaseRestControl.tranReturnValue(map);
	}

	@ApiOperation(value = "获取云主机数量与本月新增数量")
	@RequestMapping(value = "/count", method = RequestMethod.GET)
	public Object count() {
		Map<String, Integer> countMap = new HashMap<>();
		int totalCount = novaVmBiz.count(NovaVm.class, new HashMap<>());
		countMap.put("totalCount", totalCount);
		int monthCount = novaVmBiz.monthCount();
		countMap.put("monthCount", monthCount);
		return BaseRestControl.tranReturnValue(countMap);
	}
}

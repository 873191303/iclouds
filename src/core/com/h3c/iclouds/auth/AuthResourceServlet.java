package com.h3c.iclouds.auth;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.h3c.iclouds.base.BaseDAO;
import com.h3c.iclouds.base.SpringContextHolder;
import com.h3c.iclouds.biz.AppViewsBiz;
import com.h3c.iclouds.biz.AsmMasterBiz;
import com.h3c.iclouds.biz.AzoneBiz;
import com.h3c.iclouds.biz.InterfacesBiz;
import com.h3c.iclouds.biz.NetworkBiz;
import com.h3c.iclouds.biz.NovaVmBiz;
import com.h3c.iclouds.biz.PortBiz;
import com.h3c.iclouds.biz.QuotaUsedBiz;
import com.h3c.iclouds.biz.Server2VmBiz;
import com.h3c.iclouds.biz.TaskBiz;
import com.h3c.iclouds.biz.UserBiz;
import com.h3c.iclouds.biz.VdcBiz;
import com.h3c.iclouds.biz.WorkFlowBiz;
import com.h3c.iclouds.client.CasClient;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.common.OperateLogEnum;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.common.TaskTypeProperty;
import com.h3c.iclouds.dao.NovaVmViewDao;
import com.h3c.iclouds.dao.Project2AzoneDao;
import com.h3c.iclouds.dao.ProjectDao;
import com.h3c.iclouds.dao.QuotaClassDao;
import com.h3c.iclouds.operate.CloudosClient;
import com.h3c.iclouds.operate.CloudosParams;
import com.h3c.iclouds.opt.IssoClient;
import com.h3c.iclouds.opt.IssoUserOpt;
import com.h3c.iclouds.po.AppViews;
import com.h3c.iclouds.po.Azone;
import com.h3c.iclouds.po.Interfaces;
import com.h3c.iclouds.po.IpAllocation;
import com.h3c.iclouds.po.LogType;
import com.h3c.iclouds.po.Network;
import com.h3c.iclouds.po.NovaVm;
import com.h3c.iclouds.po.NovaVmView;
import com.h3c.iclouds.po.Port;
import com.h3c.iclouds.po.Project;
import com.h3c.iclouds.po.Project2Azone;
import com.h3c.iclouds.po.QuotaClass;
import com.h3c.iclouds.po.Server2Vm;
import com.h3c.iclouds.po.Task;
import com.h3c.iclouds.po.User;
import com.h3c.iclouds.po.Vdc;
import com.h3c.iclouds.po.WorkFlow;
import com.h3c.iclouds.po.bean.IssoUserBean;
import com.h3c.iclouds.quartz.NetworkAssetQuartz;
import com.h3c.iclouds.utils.DateConverter;
import com.h3c.iclouds.utils.HttpUtils;
import com.h3c.iclouds.utils.LogUtils;
import static com.h3c.iclouds.utils.PwdUtils.decrypt;
import com.h3c.iclouds.utils.ResourceHandle;
import com.h3c.iclouds.utils.StrUtils;
import com.h3c.iclouds.utils.UploadFileUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.springframework.stereotype.Component;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 启动准备工作
 *
 * @author zkf5485
 *
 */
@Component("initialServlet")
public class AuthResourceServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);

		// 获取发布路径
		String projectPath = config.getServletContext().getRealPath("/");
		LogUtils.info(this.getClass(), projectPath);
		CacheSingleton.getInstance().setProjectPath(projectPath);

		// 注册时间转化器
		ConvertUtils.register(new DateConverter(), Date.class);

		LogUtils.info(this.getClass(), "initQuotaClass.");
		this.initQuotaClass();

		LogUtils.info(this.getClass(), "initServerIP.");
		// 当前服务器的IP
		this.initServerIP();

		LogUtils.info(this.getClass(), "initTask.");
		// 重启任务
		this.initTask();

		LogUtils.info(this.getClass(), "initLogType.");
		// 初始化日志类型
		this.initLogType();

		LogUtils.info(this.getClass(), "initIssoUser.");
		// 初始化单点用户
		this.initIssoUser();

		LogUtils.info(this.getClass(), "initCasPort.");
		//初始化同花顺在cas的第二张网卡
		this.initCasPort();

		LogUtils.info(this.getClass(), "initVdcView.");
		// 初始化vdc
		this.initVdcView();

		LogUtils.info(this.getClass(), "initAppView.");
		// 初始化应用
		this.initAppView();

		LogUtils.info(this.getClass(), "initWorkFlow.");
		// 初始化工作流
		this.initWorkFlow();

		LogUtils.info(this.getClass(), "initProjectName.");
		// 初始化租户name
		this.initProjectName();
		
		// 初始化服务器ip、mac信息
		this.initMasterIpAndMac();
	}

	private void initServerIP() {
		try {
			String ipAddr = InetAddress.getLocalHost().getHostAddress();
			CacheSingleton.getInstance().setIpAddr(ipAddr);
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		}
	}

	private void initQuotaClass() {
		QuotaClassDao quotaClassDao = SpringContextHolder.getBean("quotaClassDao");
		List<QuotaClass> list = quotaClassDao.getAll(QuotaClass.class);
		CacheSingleton.getInstance().setQuotaClasses(list);
	}

	private void initAzone() {
		final String root = CacheSingleton.getInstance().getConfigValue("rootid");
		Project2AzoneDao project2AzoneDao = SpringContextHolder.getBean("project2AzoneDao");
		AzoneBiz azoneBiz = SpringContextHolder.getBean("azoneBiz");
		List<Azone> azones = azoneBiz.getAll(Azone.class);
		List<Project2Azone> project2Azones = project2AzoneDao.findByPropertyName(Project2Azone.class, "id", root);
		for (Azone azone : azones) {
			if (StrUtils.checkCollection(project2Azones)) {
				if (check(azone,project2Azones)) {
					continue;
				}
			}
			Project2Azone project2Azone = new Project2Azone();
			project2Azone.setDeleted(ConfigProperty.YES);
			project2Azone.setId(root);
			project2Azone.setIyuUuid(azone.getUuid());
			project2AzoneDao.add(project2Azone);

		}
	}

	private boolean check(Azone azone, List<Project2Azone> project2Azones) {
		for (Project2Azone project2Azone : project2Azones) {
			if (azone.getUuid().equals(project2Azone.getIyuUuid())) {
				return true;
			}
		}
		return false;
	}

	private void initTask() {
		TaskBiz taskBiz = SpringContextHolder.getBean("taskBiz");
		String hql = "from Task where stackTime<> null and stackIp=?";
		String args = CacheSingleton.getInstance().getIpAddr();
		if (StrUtils.checkParam(args)) {
			List<Task> tasks = taskBiz.findByHql(hql, args);
			if (StrUtils.checkCollection(tasks)) {
				for (Task task : tasks) {
					// 镜像不需要处理
					if(!TaskTypeProperty.VM_TO_IMAGE.equals(task.getBusType())) {
						taskBiz.save(task);
					}
				}
			}
		}
	}

	/**
	 * 初始化接口数据
	 */
    private void initInterface() {
        InterfacesBiz interfacesBiz = SpringContextHolder.getBean("interfacesBiz");
        // 初始化snmp内容
        this.saveSnmpInterface(NetworkAssetQuartz.product, NetworkAssetQuartz.product_octet, interfacesBiz);
        this.saveSnmpInterface(NetworkAssetQuartz.test, NetworkAssetQuartz.test_octet, interfacesBiz);
    }

    private void saveSnmpInterface(String[] ips, String octet, InterfacesBiz interfacesBiz) {
        String port = "161";
        for (String s : ips) {
            Map<String, Object> queryMap = StrUtils.createMap("port", port);
            queryMap.put("ip", s);
            queryMap.put("type", "snmp-switch");
            Interfaces ins = interfacesBiz.singleByClass(Interfaces.class, queryMap);
            if(ins == null) {
                ins = new Interfaces();
                ins.setId(StrUtils.getUUID());
                ins.setIp(s);
                ins.setPort(port);
                ins.setAdmin(octet);
                ins.setPasswd(octet);
                ins.setType("snmp-switch");
                ins.createdUser(ConfigProperty.SYSTEM_FLAG);
                interfacesBiz.add(ins);
            }
        }
    }

	private void initIssoUser() {
		if (!CacheSingleton.getInstance().isIssoSyn()) {
			return;
		}
		IssoClient client = IssoClient.createAdmin();
		if (null == client) {
			LogUtils.warn(this.getClass(), "Create Isso Client Failure");
			return;
		}
		IssoUserOpt userOpt = new IssoUserOpt(client);
		UserBiz userBiz = SpringContextHolder.getBean("userBiz");
		List<User> users = userBiz.getAll(User.class);
		if (StrUtils.checkCollection(users)) {
			for (User user : users) {
				String loginName = user.getLoginName();
				String password = user.getPassword();
				try {
					user.setPassword(decrypt(password, loginName + user.getId()));
					JSONObject jsonObject = userOpt.get(loginName);
					if (!client.checkResult(jsonObject)) {
						IssoUserBean authUserBean = new IssoUserBean(user);
						jsonObject = userOpt.save(authUserBean);
						if (!client.checkResult(jsonObject)) {
							LogUtils.warn(this.getClass(), "Save User To Isso Failure, userId:" + user.getId() + ", " +
									"loginName:" + loginName + ", record:" + client.getError(jsonObject));
						}
					}
				} catch (Exception e) {
					LogUtils.exception(User.class, e);
				}
			}
		}
	}

	/**
	 * 同步cas那边的管理网卡至本地
	 */
	private void initCasPort() {
		InterfacesBiz interfacesBiz = SpringContextHolder.getBean("interfacesBiz");
		Server2VmBiz server2VmBiz = SpringContextHolder.getBean("server2VmBiz");
		NovaVmViewDao novaVmViewDao = SpringContextHolder.getBean("novaVmViewDao");
		List<Interfaces> interfacesList = interfacesBiz.listByClass(Interfaces.class, StrUtils.createMap("type", "cas"));
		if (StrUtils.checkCollection(interfacesList)) {//遍历cas接口
			for (Interfaces interfaces : interfacesList) {
				List<Server2Vm> server2Vms = server2VmBiz.findByPropertyName(Server2Vm.class, "belongCas", interfaces.getId());
				if (!StrUtils.checkCollection(server2Vms)) {
					continue;
				}
				String ip = interfaces.getIp();
				String port = interfaces.getPort();
				String userName = interfaces.getAdmin();
				String password = interfaces.getPasswd();
				CasClient casClient = new CasClient(ip, Integer.parseInt(port), userName, password, true);
				for (Server2Vm server2Vm : server2Vms) {//遍历cas的虚拟机
					try {
						String uuid = server2Vm.getUuid();
						NovaVmView novaVmView = novaVmViewDao.singleByClass(NovaVmView.class, StrUtils.createMap("uuid", uuid));
						//只需要处理同花顺租户本地建立的虚拟机
						if (!StrUtils.checkParam(novaVmView) || !novaVmView.getProjectId().equals(CacheSingleton
								.getInstance().getThsTenantId())) {
							continue;
						}
						String vmCasId = server2Vm.getCasId();
						JSONObject vmObject = casClient.get("/cas/casrs/vm/" + vmCasId);
						if (!ResourceHandle.judgeResponse(vmObject)){//cas查询虚拟机信息错误
							LogUtils.warn(NovaVm.class, "Get Vm Message In Cas Failure" + JSONObject.toJSONString(vmObject));
							continue;
						}
						vmObject = vmObject.getJSONObject("record");
						if (!vmObject.containsKey("network")) {//cas没有网卡时不需要处理
							continue;
						}
						try {
							JSONArray networkArray = vmObject.getJSONArray("network");
							for (int i = 0; i < networkArray.size(); i++) {
								JSONObject networkJson = networkArray.getJSONObject(i);
								initCasPort(networkJson, novaVmView);
							}
						} catch (Exception e) {//一张网卡时不需要处理
							JSONObject networkJson = vmObject.getJSONObject("network");
							initCasPort(networkJson, novaVmView);
						}
					} catch (Exception e) {
						LogUtils.exception(this.getClass(), e);
					}
				}
			}
		}
	}

	private void initCasPort(JSONObject networkJson, NovaVmView novaVmView) {
		String privateIp = novaVmView.getPrivateIp();
		String manageIp = novaVmView.getManageIp();
		String ip =  networkJson.getString("ipAddr");
		//校验cas挂载的网卡是不是在本地已经挂载至该主机
		if (StrUtils.checkParam(privateIp) && privateIp.contains(ip) || StrUtils.checkParam(manageIp) && manageIp.equals(ip)) {
			return;
		}
		try {
			UserBiz userBiz = SpringContextHolder.getBean("userBiz");
			NovaVmBiz novaVmBiz = SpringContextHolder.getBean("novaVmBiz");
			PortBiz portBiz = SpringContextHolder.getBean("portBiz");
			NetworkBiz networkBiz = SpringContextHolder.getBean("networkBiz");
			QuotaUsedBiz quotaUsedBiz = SpringContextHolder.getBean("quotaUsedBiz");
			BaseDAO<IpAllocation> ipAllDao = SpringContextHolder.getBean("baseDAO");
			String networkId = networkBiz.getNetworkIdByIp(ip, novaVmView.getProjectId());
			if (null == networkId) {//没有该ip对应的网络时不处理
				LogUtils.warn(this.getClass(), "Cannot Find Subnet For Ip, ip:" + ip);
				return;
			}
			Network network = networkBiz.findById(Network.class, networkId);
			if (novaVmView.getCidr().contains(network.getCidr())) {//当cas挂载的其它网卡跟本地主机已经挂载的网卡所属的网络属于同一个时不处理
				LogUtils.warn(this.getClass(), "Cannot Attach Ip In Same Network With Local Port, ip:" + ip);
				return;
			}
			String mac = networkJson.getString("mac");
			String owner = novaVmView.getUserId();
			NovaVm novaVm = novaVmBiz.findById(NovaVm.class, novaVmView.getId());
			Map<String, Object> queryMap = new HashMap<>();
			queryMap.put("subnetId", network.getSubnetId());
			queryMap.put("ipAddress", ip);
			IpAllocation ipAllocation = ipAllDao.singleByClass(IpAllocation.class, queryMap);
			boolean saveFlag = false;
			Port port = null;
			if (StrUtils.checkParam(ipAllocation)) {//本地已存在该网卡
				port = portBiz.findById(Port.class, ipAllocation.getPortId());
				if (StrUtils.checkParam(port.getDeviceId())) {//ip已被其它设备占用
					LogUtils.warn(this.getClass(), "Ip Used By Annother Resource, ip:" + ip);
					return;
				}
				if (!mac.equals(port.getMacAddress())) {//两边mac不一致时删除重建建立网卡
					portBiz.deleteLocalPort(port);
					saveFlag = true;
				}
			} else {
				saveFlag = true;
			}
			if (saveFlag) {//保存网卡
				ResultType rs = quotaUsedBiz.checkQuota(ConfigProperty.PORT_QUOTA_CLASSCODE, novaVmView.getProjectId(), 1);
				if (!ResultType.success.equals(rs)) {
					LogUtils.warn(this.getClass(), "Port Reach Used Quota, projectId:" + novaVmView.getProjectId());
				}
				User user = userBiz.findById(User.class, owner);
				port = new Port();
				port.setAddress(ip);
				port.setTenantId(novaVm.getProjectId());
				port.setNetworkCloudosId(network.getCloudosId());
				port.setAdminStateUp(Boolean.TRUE);
				port.createdUser(owner);
				port.setMacAddress(mac);
				port.setName("spe_port_1");
				port.setUserId(owner);
				port.setIsinit(false);
				JSONObject response = portBiz.cloudosSave(port, user.getCloudosId(), CloudosClient.createAdmin());
				if (!ResourceHandle.judgeResponse(response)) {
					LogUtils.warn(this.getClass(), "Save Port In Cloudos Failure, error message:" + HttpUtils
							.getError(response));
				}
				portBiz.localSave(port, response, network.getSubnetId());
			}
			port.updatedUser(owner);
			port.setDeviceOwner("compute:cas01");
			port.setDeviceId(novaVm.getUuid());
			portBiz.update(port);
		} catch (Exception e) {
			LogUtils.exception(this.getClass(), e);
		}
	}

	private void initLogType() {
		// 日志类型初始化
		OperateLogEnum[] array = OperateLogEnum.values();
		if (array != null) {
			BaseDAO<LogType> baseDAO = SpringContextHolder.getBean("baseDAO");
			for (OperateLogEnum ope : array) {
				String id = ope.getLogTypeId();
				if (StrUtils.checkParam(id)) {
					String value = ope.getLogTypeValue();
					LogType entity = baseDAO.findById(LogType.class, id);
					if (entity == null) {
						entity = new LogType();
						entity.createdUser(ConfigProperty.SYSTEM_FLAG);
						entity.setRemark(value);
						entity.setDescription(value);
						entity.setId(id);
						baseDAO.add(entity);
					} else {
						if (!value.equals(entity.getDescription())) {
							entity.setRemark(value);
							entity.setDescription(value);
							baseDAO.update(entity);
						}
					}
				}
			}
		}
	}

	private void initWorkFlow() {
		WorkFlowBiz workFlowBiz = SpringContextHolder.getBean("workFlowBiz");
		List<WorkFlow> workFlows = workFlowBiz.findByPropertyName(WorkFlow.class);
		if (workFlows != null && !workFlows.isEmpty()) {
			workFlows.forEach(entity -> {
				if (entity.getStatus().equals(ConfigProperty.WORKFLOW_STATUS1_UNDEPLOY)) {
					try {
						workFlowBiz.deploy(entity);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
				}
			});
		}
	}

	private void initAppView() {
		AppViewsBiz appViewsBiz = SpringContextHolder.getBean("appViewsBiz");
		List<AppViews> appViewses = appViewsBiz.getAll(AppViews.class);
		if(StrUtils.checkCollection(appViewses)) {
			appViewses.forEach(appViews -> appViewsBiz.clearLock(appViews));
		}
	}

	private void initVdcView() {
		VdcBiz vdcBiz = SpringContextHolder.getBean("vdcBiz");
		List<Vdc> vdcs = vdcBiz.getAll(Vdc.class);
		if (StrUtils.checkCollection(vdcs)) {
			vdcs.forEach(vdc -> vdcBiz.clearLock(vdc));
		}
	}

	private void initProjectName() {
		CacheSingleton singleton = CacheSingleton.getInstance();
		Map<String, String> prjectNameMap = singleton.getProjectNameMap();
		CloudosClient client = CloudosClient.createAdmin();
		JSONObject jsonObj = client.get(HttpUtils.getUrl(CloudosParams.CLOUDOS_API_PROJECTS));
		String result = jsonObj.getString("result");
		if(!"200".equals(result)) {
			throw new RuntimeException("初始化租户信息失败");
		}
		JSONArray array = HttpUtils.getJSONArray(jsonObj, "projects");
		ProjectDao projectDao = SpringContextHolder.getBean("projectDao");
		for (int i = 0; i < array.size(); i++) {
			JSONObject projectObj = array.getJSONObject(i);
			String projectId = projectObj.getString("id");
			Project entity = projectDao.findById(Project.class, projectId);
			if(entity != null) {
				String name = projectObj.getString("name");
				prjectNameMap.put(projectId, name);
				// 不一致则修改为一致，防止调用API错误
				if(!name.equals(entity.getDescription())) {
					entity.setDescription(name);
					projectDao.update(entity);
				}
			}
		}

	}
	
	private void initMasterIpAndMac() {
		AsmMasterBiz asmMasterBiz = SpringContextHolder.getBean("asmMasterBiz");
		List<Map<String, String>> list = new ArrayList<>();
		String fileName = ConfigProperty.MASTER_IP_MAC_FILE_NAME;
		File file = UploadFileUtils.getFile(fileName);
		try{
			BufferedReader br = new BufferedReader(new FileReader(file));//构造一个BufferedReader类来读取文件
			String s = null;
			while((s = br.readLine()) != null){//使用readLine方法，一次读一行
				String [] ipMacs = s.split("@");
				if (ipMacs.length == 3) {
					Map<String, String> map = new HashMap<>();
					map.put("iloIp", ipMacs[0]);
					map.put("ip", ipMacs[1]);
					map.put("mac", ipMacs[2]);
					list.add(map);
				}
			}
			br.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		if (StrUtils.checkCollection(list)) {
			asmMasterBiz.initMonitor(list);
		}
	}
	
}

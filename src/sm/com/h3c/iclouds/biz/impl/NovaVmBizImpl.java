package com.h3c.iclouds.biz.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.h3c.iclouds.base.BaseBizImpl;
import com.h3c.iclouds.base.BaseDAO;
import com.h3c.iclouds.base.SqlQueryBiz;
import com.h3c.iclouds.biz.MeasureDetailBiz;
import com.h3c.iclouds.biz.NetworkBiz;
import com.h3c.iclouds.biz.NovaVmBiz;
import com.h3c.iclouds.biz.PortBiz;
import com.h3c.iclouds.biz.Project2QuotaBiz;
import com.h3c.iclouds.biz.RecycleItemsBiz;
import com.h3c.iclouds.biz.RenewalBiz;
import com.h3c.iclouds.biz.SpePortBiz;
import com.h3c.iclouds.biz.TaskBiz;
import com.h3c.iclouds.biz.UserBiz;
import com.h3c.iclouds.biz.VolumeBiz;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.common.SqlQueryProperty;
import com.h3c.iclouds.common.TaskTypeProperty;
import com.h3c.iclouds.dao.AzoneDao;
import com.h3c.iclouds.dao.NovaVmDao;
import com.h3c.iclouds.dao.Project2AzoneDao;
import com.h3c.iclouds.dao.RecycleItemsDao;
import com.h3c.iclouds.dao.VolumeDao;
import com.h3c.iclouds.exception.MessageException;
import com.h3c.iclouds.operate.CloudosClient;
import com.h3c.iclouds.operate.CloudosFlavor;
import com.h3c.iclouds.operate.CloudosParams;
import com.h3c.iclouds.po.Azone;
import com.h3c.iclouds.po.Metadata;
import com.h3c.iclouds.po.Network;
import com.h3c.iclouds.po.NovaFlavor;
import com.h3c.iclouds.po.NovaVm;
import com.h3c.iclouds.po.NovaVmView;
import com.h3c.iclouds.po.Port;
import com.h3c.iclouds.po.Project2Azone;
import com.h3c.iclouds.po.RecycleItems;
import com.h3c.iclouds.po.Rules;
import com.h3c.iclouds.po.Server2Vm;
import com.h3c.iclouds.po.SpePort;
import com.h3c.iclouds.po.Subnet;
import com.h3c.iclouds.po.Task;
import com.h3c.iclouds.po.User;
import com.h3c.iclouds.po.VmExtra;
import com.h3c.iclouds.po.Volume;
import com.h3c.iclouds.po.bean.cloudos.CloneOrImageBean;
import com.h3c.iclouds.po.bean.inside.SaveNovaVmBean;
import com.h3c.iclouds.po.bean.outside.NovaVmDetailBean;
import com.h3c.iclouds.po.business.FlavorParam;
import com.h3c.iclouds.utils.HttpUtils;
import com.h3c.iclouds.utils.InvokeSetForm;
import com.h3c.iclouds.utils.IpValidator;
import com.h3c.iclouds.utils.OpeAuth;
import com.h3c.iclouds.utils.PwdUtils;
import com.h3c.iclouds.utils.ResourceNovaHandle;
import com.h3c.iclouds.utils.StrUtils;
import com.h3c.iclouds.validate.ValidatorUtils;
import org.springframework.stereotype.Service;
import com.h3c.iclouds.po.Renewal;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service("novaVmBiz")
public class NovaVmBizImpl extends BaseBizImpl<NovaVm> implements NovaVmBiz {

	@Resource
	public HttpServletRequest request;

	@Resource
	private NovaVmDao novaVmDao;

	@Resource(name = "baseDAO")
	private BaseDAO<VmExtra> vmExtraDao;

	@Resource
	private VolumeDao volumeDao;

	@Resource
	private VolumeBiz volumeBiz;

	@Resource
	private AzoneDao azoneDao;

	@Resource(name = "baseDAO")
	private BaseDAO<Metadata> metadatDao;

	@Resource(name = "baseDAO")
	private BaseDAO<Rules> rulesDao;

	@Resource(name = "baseDAO")
	private BaseDAO<NovaFlavor> novaFlavorDao;

	@Resource
	private RecycleItemsDao recycleItemsDao;

	@Resource
	private Project2QuotaBiz project2QuotaBiz;

	@Resource
	private SqlQueryBiz sqlQueryBiz;

	@Resource
	private TaskBiz taskBiz;

	@Resource(name = "baseDAO")
	private BaseDAO<Subnet> subnetDao;

	@Resource
	private NetworkBiz networkBiz;

	@Resource
	private Project2AzoneDao project2AzoneDao;

	@Resource
	private PortBiz portBiz;

	@Resource
	private RecycleItemsBiz recycleItemsBiz;

	@Resource
	private SpePortBiz spePortBiz;

	@Resource
	private MeasureDetailBiz measureDetailBiz;

	@javax.annotation.Resource
	private UserBiz userBiz;

	// 租期过期
	@Resource
	private RenewalBiz renewalBiz;

	

	// 日期格式转时间戳
	public String date2TimeStamp(String date_str) {
		Date dt1;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			dt1 = sdf.parse(date_str);
			long ts1 = dt1.getTime();
			return String.valueOf(ts1);
		} catch (ParseException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();

		}
		return "";
	}

	// 云主机快照功能
	@Override
	public ResultType snapshot(String novaVmId) {
		NovaVm novaVm = novaVmDao.findById(NovaVm.class, novaVmId);
		String projectId = novaVm.getProjectId();
		CloudosClient cloudosClient = CloudosClient.create(userBiz,projectId);
		String uuid = novaVm.getUuid();
		String uri = singleton.getCloudosApi(CloudosParams.CLOUDOS_API_SERVER_ACTION);
		uri = HttpUtils.tranUrl(uri, projectId);
		uri = HttpUtils.tranUrl(uri, uuid);
		Map<String, Object> params = new HashMap<>();
		Map<String, Object> params2 = new HashMap<>();
		Map<String, Object> params3 = new HashMap<>();
		params.put("myvar", "foobar");
		params2.put("name", "foo-image");
		params2.put("metadata", params);
		params3.put("createImage", params2);
		JSONObject re = (JSONObject) JSONObject.toJSON(params3);
		for (int i = 0; i < 2; i++) {
			JSONObject result = cloudosClient.post(uri, re);
			String status = result.getString("result");
			if ("202".equals(status)) {
				return ResultType.success;
			}
		}
		return ResultType.system_error;
	}

	// 修改云主机密码
	@Override
	public ResultType update(SaveNovaVmBean bean, String novaVmId, String userId, String pwd) throws Exception {
		NovaVm novaVm = novaVmDao.findById(NovaVm.class, novaVmId);
		String projectId = novaVm.getProjectId();
		CloudosClient cloudosClient = CloudosClient.create(userBiz,projectId);
		String uuid = novaVm.getUuid();
		String uri = singleton.getCloudosApi(CloudosParams.CLOUDOS_API_SERVER_ACTION);
		uri = HttpUtils.tranUrl(uri, projectId);
		uri = HttpUtils.tranUrl(uri, uuid);
		Map<String, Object> params = new HashMap<>();
		Map<String, Object> params2 = new HashMap<>();
		params.put("adminPass", pwd);
		params2.put("changePassword", params);
		JSONObject re = (JSONObject) JSONObject.toJSON(params2);
		for (int i = 0; i < 2; i++) {
			JSONObject result = cloudosClient.post(uri, re);
			String status = result.getString("result");
			if ("202".equals(status)) {
				VmExtra vmExtra = new VmExtra();
				vmExtra.setId(novaVmId);
				vmExtra.setOsPasswd(PwdUtils.encrypt(pwd, novaVmId + userId));
				vmExtraDao.update(vmExtra);
				return ResultType.success;
			}
		}
		return ResultType.system_error;

	}

	@Override
	public ResultType save(SaveNovaVmBean bean, String userId, CloudosClient client) throws Exception {
		project2QuotaBiz.novaQuota(bean);
		ResourceNovaHandle resourceNovaHandle = new ResourceNovaHandle();
		// true使用配额
		ResultType resultType = resourceNovaHandle.updateQuota(bean, true, bean.getProjectId());
		if (resultType != ResultType.success) {
			this.warn("修改配额失败");
			return resultType;
		}
		StringBuffer ids = new StringBuffer();
		StringBuffer names = new StringBuffer();
		for (int i = 0; i < bean.getCount(); i++) {
			NovaVm novaVm = new NovaVm();
			InvokeSetForm.copyFormProperties1(bean, novaVm);
			if (bean.getCount() > 1) {
				novaVm.setHostName(bean.getHostName() + "_" + (i + 1));
			} else {
				novaVm.setHostName(bean.getHostName());
			}
			novaVm.setUuid(null);
			novaVm.setAzoneId(bean.getAzoneId());
			novaVm.setHost("宿主机");
			novaVm.createdUser(getLoginUser());
			novaVm.setMemory(bean.getMemory_mb());
			novaVm.setRamdisk(bean.getRamdisk_gb());
			novaVm.setVmState(ConfigProperty.novaVmState.get(3));
			novaVm.setPowerState(TaskTypeProperty.VM_POWER_STATE_SHUTDOWN);// 关机状态
			novaVm.setProjectId(bean.getProjectId());
			novaVm.setOwner(userId);
			novaVmDao.add(novaVm);
			ids.append(novaVm.getId() + ",");
			names.append(novaVm.getHostName() + ",");

			VmExtra vmExtra = new VmExtra();
			vmExtra.setOsUser(userId);

			// 加密
			if (!StrUtils.checkParam(bean.getOsPasswd())) {
				bean.setOsPasswd(PwdUtils.getPwd());
			}
			vmExtra.setId(novaVm.getId());
			vmExtra.setSshKey(null);
			vmExtraDao.add(vmExtra);
			// 修改bean的密码
			String encryptPassword = PwdUtils.encrypt(bean.getOsPasswd(), novaVm.getId() + userId);
			vmExtra.setOsPasswd(encryptPassword);
			vmExtraDao.update(vmExtra);

			Metadata metadata = new Metadata();
			metadata.setId(UUID.randomUUID().toString());
			metadata.setInstanceUuid(novaVm.getId());
			metadata.createdUser(getLoginUser());
			metadata.setDeleted(ConfigProperty.YES);
			metadata.setDeleteBy(null);
			metadata.setKey(null);
			metadata.setValue(null);
			metadata.setDeleteDate(null);
			metadatDao.add(metadata);

			String uri = singleton.getCloudosApi(CloudosParams.CLOUDOS_API_SERVER);
			uri = HttpUtils.tranUrl(uri, bean.getProjectId());
			Map<String, Object> map = paramsCreate(bean, i);
			Map<String, Object> server = new HashMap<>();
			server.put("uri", uri);
			// cloudos创建的参数
			server.put("params", map);
			server.put("ramdisk_gb", bean.getRamdisk_gb());
			server.put("azoneId", bean.getAzoneId());
			server.put("time", bean.getMonth());
			// 任务紧跟着租户相关信息
			String busId = novaVm.getId();
			String busType = TaskTypeProperty.VM_CREATE;
			// 注册通道标志符
			server.put(ConfigProperty.NOVAVM_PREFIX, ConfigProperty.NOVAVM_PREFIX + userId);
			String input = new JSONObject(server).toJSONString();

			taskBiz.save(busId, busType, input);
		}
		if (ids.length() > 0) {
			// 设置日志的资源id
			this.request.setAttribute("id", ids.deleteCharAt(ids.length() - 1).toString());
			// 设置日志的资源名称
			this.request.setAttribute("name", names.deleteCharAt(names.length() - 1).toString());
		}
		return ResultType.success;
	}

	private Map<String, Object> paramsCreate(SaveNovaVmBean bean, int i) {
		Map<String, Object> server = new HashMap<>();
		Map<String, Object> params = new HashMap<>();
		if (bean.getCount() > 1) {
			params.put("name", bean.getHostName() + "_" + (i + 1));
		} else {
			params.put("name", bean.getHostName());
		}
		params.put("imageRef", bean.getImageRef());
		params.put("flavorRef", bean.getFlavorId());
		params.put("max_count", 1);
		params.put("min_count", 1);
		params.put("availability_zone", bean.getAzoneName()); // 设置可用域
		params.put("adminPass", bean.getOsPasswd());
		List<Object> networks = new ArrayList<Object>();
		Map<String, Object> subnet = new HashMap<>();
		subnet.put("uuid", bean.getNetworkId());
		networks.add(subnet);
		params.put("networks", networks);
		List<Object> security_groups = new ArrayList<Object>();
		Map<String, Object> user = new HashMap<>();
		user.put("name", "default");
		// user.put("name1", "another-secgroup-name");
		security_groups.add(user);
		params.put("security_groups", security_groups);
		server.put("server", params);
		return server;

	}

	@Override
	public void action(NovaVm novaVm, String vmState, Map<String, Object> params, String busType) {
		// 修改云主机状态
		novaVm.setVmState(vmState);
		this.update(novaVm);

		// 设置操作的url
		String uri = singleton.getCloudosApi(CloudosParams.CLOUDOS_API_SERVER_ACTION);
		uri = HttpUtils.tranUrl(uri, this.getProjectId(), novaVm.getUuid());

		// 设置操作的参数
		Map<String, Object> inputMap = new HashMap<>();
		inputMap.put("uri", uri);
		inputMap.put("params", params);
		String input = new JSONObject(inputMap).toJSONString();

		taskBiz.save(novaVm.getId(), busType, input);
	}

	// 查询云主机旧密码
	@Override
	public String selPassWord(NovaVm novaVm) {
		String id = novaVm.getId();
		VmExtra vmExtra = vmExtraDao.findById(VmExtra.class, id);
		String decryptPassword = "";
		try {
			decryptPassword = PwdUtils.decrypt(vmExtra.getOsPasswd(), novaVm.getId() + novaVm.getOwner());
		} catch (Exception e) {
			this.exception(e, "Dectypt password error.");
		}
		return decryptPassword;
	}

	// 云主机密码加密
	@Override
	public String setPassWord(NovaVm novaVm) {
		String id = novaVm.getId();
		VmExtra vmExtra = vmExtraDao.findById(VmExtra.class, id);
		String encryptPassword = "";
		try {
			encryptPassword = PwdUtils.encrypt(vmExtra.getOsPasswd(), novaVm.getId() + novaVm.getOwner());
		} catch (Exception e) {
			this.exception(e, "Dectypt password error.");
		}
		return encryptPassword;
	}

	@Override
	public NovaVmDetailBean detail(NovaVm novaVm) throws Exception {
		String id = novaVm.getId();
		VmExtra vmExtra = vmExtraDao.findById(VmExtra.class, id);
		Map<String, Object> params = new HashMap<>();
		params.put("deleted", "0");
		params.put("host", id);
		params.put("status", ConfigProperty.VOLUME_STATE_ATTACHED);
		// 有可能含有状态值
		List<Volume> volumes = volumeDao.listByClass(Volume.class, params);
		Azone azone = azoneDao.findById(Azone.class, novaVm.getAzoneId());
		Rules image = rulesDao.findById(Rules.class, novaVm.getImageRef());
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("novaVmId", id);
		map.put("owner", novaVm.getOwner());
		NovaVmDetailBean detailBean = new NovaVmDetailBean();
		if (StrUtils.checkParam(vmExtra)) {
			if (StrUtils.checkParam(vmExtra.getOsPasswd())) {
				// 当管理员查看时不能使用getLoginUser
				try {
					String decryptPassword = PwdUtils.decrypt(vmExtra.getOsPasswd(),
							novaVm.getId() + novaVm.getOwner());
					detailBean.setOsPasswd(decryptPassword);
				} catch (Exception e) {
					this.exception(e, "Dectypt password error.");
				}
			}
		} else {
			detailBean.setOsPasswd(null);
		}
		detailBean.setVolumes(volumes);
		detailBean.setAzone(azone);
		detailBean.setNovaVm(novaVm);
		detailBean.setImageName(image.getOsMirName());
		List<Map<String, Object>> list = sqlQueryBiz.queryByName(SqlQueryProperty.QUERY_NOVAVM_DETAIL, map);
		for (Map<String, Object> map2 : list) {
			InvokeSetForm.settingForm(map2, detailBean.getNovaVm());
			detailBean.setIpAddress((String) map2.get("ipaddress"));
			// 密码解密
			detailBean = dealIp1(detailBean);
		}
		String pubLicIp = getIpByUuid(novaVm.getUuid(), true);
		detailBean.setPublicIp(pubLicIp);

		SpePort monitor = spePortBiz.singleByClass(SpePort.class, StrUtils.createMap("hostId", id));
		detailBean.setMonitor(monitor);
		return detailBean;
	}

	private NovaVmDetailBean dealIp1(NovaVmDetailBean bean) {
		String ip = bean.getIpAddress();
		StringBuffer publicIp = new StringBuffer();
		StringBuffer privateIp = new StringBuffer();
		if (StrUtils.checkParam(ip)) {
			String ips[] = ip.split("T|F");
			for (String string : ips) {
				int temp = ip.indexOf(string) + string.length();
				if (ip.charAt(temp) == 'F') {
					privateIp.append(string).append(",");
				}
				if (ip.charAt(temp) == 'T') {
					publicIp.append(string).append(",");
				}

			}
		}
		if (publicIp.length() != 0) {
			bean.setPublicIp(publicIp.substring(0, publicIp.length() - 1));
		}
		if (privateIp.length() != 0) {
			bean.setPrivateIp(privateIp.substring(0, privateIp.length() - 1));
		}
		return bean;
	}

	@Override
	public Object delete(String id) {
		// 查询当前云主机的状态
		NovaVm novaVm = novaVmDao.findById(NovaVm.class, id);
		if (!StrUtils.checkParam(novaVm)) {
			throw new MessageException(ResultType.novam_not_exist);
		}
		List<Volume> volumes = volumeBiz.findByPropertyName(Volume.class, "host", novaVm.getId());
		List<String> error = new ArrayList<>();
		if (StrUtils.checkCollection(volumes)) {
			for (Volume volume : volumes) {
				error.add(volume.getName());
			}
			return error;
		}
		String projectId = novaVm.getProjectId();
		// 如果已经存在，则更新
		List<RecycleItems> recycleItems = recycleItemsDao.findByPropertyName(RecycleItems.class, "resId",
				novaVm.getId());
		if (StrUtils.checkCollection(recycleItems)) {
			RecycleItems items = recycleItems.get(0);
			items.updatedUser(getLoginUser());
			items.setInboundTime(new Date());
			items.setRecycleAction("1");
		} else {
			// 记录到回收站中
			RecycleItems item = new RecycleItems();
			item.createdUser(getLoginUser());
			item.setResId(id);
			item.setClassId("1");
			item.setRecycleType("0");
			item.setInboundTime(new Date());
			item.setRecycleAction("1");
			item.setRecycleTime(null);// 当返原的时候设置
			recycleItemsDao.add(item);
		}
		// 进回收站异常，创建失败，删除失败状态处理,不更新云主机状态
		OpeAuth opeAuth = new OpeAuth();
		ResultType result = opeAuth.checkStatus(novaVm);
		if (result != ResultType.success) {
			Map<String, Object> queryMap = new HashMap<>();
			queryMap.put("projectId", projectId);
			queryMap.put("busId", id);
			Task task = taskBiz.singleByClass(Task.class, queryMap);
			if (StrUtils.checkParam(task)) {
				taskBiz.delete(task);
			}
			return ResultType.success;
		}
		// 如果云主机已关机
		if (!"state_stop".equals(novaVm.getVmState())) {
			String uri = singleton.getCloudosApi(CloudosParams.CLOUDOS_API_SERVER_ACTION);
			Map<String, Object> params = StrUtils.createMap(CloudosParams.SERVER_ACTION_OS_STOP, "null");
			// 设置操作的参数
			Map<String, Object> inputMap = new HashMap<>();
			inputMap.put("params", params);
			String uuid = novaVm.getUuid();
			if (StrUtils.checkParam(uuid)) {
				uri = HttpUtils.tranUrl(uri, this.getProjectId(), novaVm.getUuid());
				inputMap.put("uri", uri);
				inputMap.put("flag", false);
			} else {
				uri = HttpUtils.tranUrl(uri, this.getProjectId());
				inputMap.put("uri", uri);
				inputMap.put("flag", true);
			}
			inputMap.put("recycle", true);
			String input = new JSONObject(inputMap).toJSONString();
			// 如果处在任务中，则等待
			String vmState = novaVm.getVmState();
			if (ConfigProperty.novaVmState.get(1).equals(vmState)) {// 正常
				// 启动等待任务，然后执行关机
				novaVm.setVmState(ConfigProperty.novaVmState.get(9));// 关机中
				update(novaVm);
				taskBiz.save(id, TaskTypeProperty.VM_SHUTDOWN, input);
				return ResultType.success;
			}
			List<Task> tasks = taskBiz.findByPropertyName(Task.class, "busId", novaVm.getId());
			if (StrUtils.checkCollection(tasks)) {
				for (int i = 0; i < tasks.size(); i++) {
					Task task = tasks.get(i);
					if (task.getBusType().equals(TaskTypeProperty.VM_WAIT_SHUTDOWN)) {
						// 已有等待关机
						return ResultType.success;
					}
					inputMap.put("taskId" + i, task.getId());
				}
				inputMap.put("taskNum", tasks.size());
				input = new JSONObject(inputMap).toJSONString();
				novaVm.setVmState(ConfigProperty.novaVmState.get(9));// 关机中
				update(novaVm);
				taskBiz.save(id, TaskTypeProperty.VM_WAIT_SHUTDOWN, input);
			} else {
				// 异常，失败状态直接进回收站
				return ResultType.success;
			}
		}
		return ResultType.success;
	}

	@Override
	public ResultType vmToImage(NovaVm novaVm, Server2Vm entity, CloneOrImageBean inputBean) {
		Map<String, Object> input = StrUtils.createMap();
		Azone zone = this.azoneDao.findById(Azone.class, novaVm.getAzoneId());
		if (zone == null) {
			throw new MessageException(ResultType.azone_not_exist);
		}

		CloudosClient client = this.getSessionBean().getCloudClient();
		NovaFlavor flavorEntity = novaFlavorDao.findById(NovaFlavor.class, novaVm.getFlavorId());
		if (flavorEntity == null) {
			throw new MessageException(ResultType.flavor_not_exist);
		}

		// 获取网络
		String uri = HttpUtils.getUrl(CloudosParams.CLOUDOS_API_OSINTERFACE, this.getProjectId(), novaVm.getUuid());
		JSONObject jsonObj = client.get(uri);
		JSONArray interfaceAttachments = HttpUtils.getJSONArray(jsonObj, "interfaceAttachments");
		if (!StrUtils.checkCollection(interfaceAttachments)) {
			throw new MessageException(ResultType.network_not_exist);
		}
		JSONObject interfaceAttachment = interfaceAttachments.getJSONObject(0);
		String netId = interfaceAttachment.getString("net_id");

		SaveNovaVmBean bean = new SaveNovaVmBean();
		bean.setFlavorId(flavorEntity.getId());
		bean.setCount(inputBean.getCloneMode() ? inputBean.getCount() : 1);

		if (inputBean.getCloneMode()) {
			// 验证配额
			project2QuotaBiz.novaQuota(bean);
			ResourceNovaHandle resourceNovaHandle = new ResourceNovaHandle();
			// true使用配额
			ResultType resultType = resourceNovaHandle.updateQuota(bean, true, getProjectId());
			if (resultType != ResultType.success) {
				this.warn("修改配额失败");
				throw new MessageException(resultType);
			}

			JSONArray fixed_ips = interfaceAttachment.getJSONArray("fixed_ips");
			if (!StrUtils.checkCollection(fixed_ips)) {
				throw MessageException.create(ResultType.server_has_no_ip);
			}
			JSONObject fixed_ip = fixed_ips.getJSONObject(0);
			String subnet_id = fixed_ip.getString("subnet_id");
			if (subnet_id == null) {
				throw MessageException.create(ResultType.subnet_not_exist);
			}
			Subnet subnet = subnetDao.singleByClass(Subnet.class, StrUtils.createMap("cloudosId", subnet_id));
			input.put("gateway", subnet.getGatewayIp());
			input.put("imageId", novaVm.getImageRef());
			input.put("cidr", subnet.getCidr());
			String mask = IpValidator.getMaskbyLen(IpValidator.getMask(subnet.getCidr()));
			input.put("mask", mask);
			input.put("count", inputBean.getCount());
			novaVm.setVmState(ConfigProperty.novaVmState.get(18));
		} else {
			// 新增一个临时的镜像
			Rules rules = new Rules();
			rules.setId("temp" + StrUtils.getUUID());
			rules.setOsMirName(inputBean.getName());
			rules.setOsMirId("2");
			rules.setUserId(this.getLoginUser());
			rules.setTenantId(this.getProjectId());
			rules.setMinSwap(1);
			rules.setIsDefault("1"); // 设置为不可用
			rules.setMinRam(1024);
			rules.setMinDisk(10);
			rules.setVcpu(1);
			rules.setSyncTime(new Date());
			rules.setFormat("");
			rules.createdUser(this.getLoginUser());
			this.rulesDao.add(rules);
			novaVm.setVmState(ConfigProperty.novaVmState.get(12));
			input.put("remark", inputBean.getRemark());
			input.put("label", inputBean.getLabel());
		}
		this.update(novaVm);
		input.put("name", inputBean.getName());
		input.put("novaId", novaVm.getId());
		input.put("flavorId", flavorEntity.getId());
		input.put("azoneName", zone.getZone());
		input.put("azoneId", novaVm.getAzoneId());
		input.put("netId", netId);
		input.put("uuid", novaVm.getUuid());
		input.put("vcpus", flavorEntity.getVcpus());
		input.put("memory", flavorEntity.getRam());
		input.put("ramdisk", flavorEntity.getDisk());
		taskBiz.saveImageTask(novaVm.getId(),
				inputBean.getCloneMode() ? TaskTypeProperty.VM_CLONE : TaskTypeProperty.VM_TO_IMAGE,
				JSONObject.toJSONString(input));
		return ResultType.success;
	}

	@Override
	public JSONArray getServerArray(CloudosClient client) {
		String uri = singleton.getCloudosApi(CloudosParams.CLOUDOS_API_SERVER_DETAIL);
		String rootid = singleton.getConfigValue("rootid");
		uri = HttpUtils.tranUrl(uri, rootid);
		JSONArray jsonArray = HttpUtils.getArray(uri, "servers", null, client);
		return jsonArray;
	}

	@Override
	public int monthCount() {
		return novaVmDao.monthCount();
	}

	@Override
	public List<NovaVm> findListByProfix(String projectId, String profix) {
		return novaVmDao.findListByProfix(projectId, profix);
	}

	@Override
	public ResultType verify(SaveNovaVmBean bean, CloudosClient client) {
		if (null == client) {
			return ResultType.system_error;
		}
		Map<String, String> map = ValidatorUtils.validator(bean);
		if (!map.isEmpty()) {
			return ResultType.parameter_error;
		}
		Map<String, Object> params = new HashMap<>();
		params.put("cloudosId", bean.getNetworkId());
		Network network = networkBiz.singleByClass(Network.class, params);
		String projectId = bean.getProjectId();
		if (!StrUtils.checkParam(network)) {
			return ResultType.network_not_exist;
		}
		if (network.getExternalNetworks().equals(true)) {
			return ResultType.novavm_create_not_public_network;
		}
		if (!projectId.equals(network.getTenantId())) {
			return ResultType.tenant_inconformity;
		}
		JSONObject ntJson = networkBiz.getNetworkJson(network.getCloudosId(), client);
		if (!StrUtils.checkParam(ntJson)) {
			return ResultType.network_not_exist_in_cloudos;
		}
		// 查询可用域是否存在
		Azone azone = azoneDao.findById(Azone.class, bean.getAzoneId());
		if (azone == null) {
			return ResultType.azone_not_exist;
		}
		// 普通租户只能获取属于自己的可用域
		if (!singleton.getRootProject().equals(projectId)) {
			Map<String, Object> queryMap = new HashMap<>();
			queryMap.put("id", projectId);
			queryMap.put("iyuUuid", azone.getUuid());
			Project2Azone p2a = project2AzoneDao.singleByClass(Project2Azone.class, queryMap);
			if (!StrUtils.checkParam(p2a)) {
				return ResultType.azone_belong_to_other_tenant;
			}
		}
		bean.setAzoneName(azone.getZone());

		CloudosFlavor cloudosFlavor = new CloudosFlavor(client);
		if (!cloudosFlavor.get(bean.getProjectId(), bean.getFlavorId())) {
			this.warn("请求的规格cloudos不存在");
			return ResultType.cloudos_novaflavor_not_exist;
		}
		Rules rules = rulesDao.findById(Rules.class, bean.getImageRef());
		if (!StrUtils.checkParam(rules)) {
			return ResultType.image_not_exist;
		}
		bean.setOsType(rules.getOsType());
		if ("1".equals(rules.getIsDefault())) {
			return ResultType.image_can_not_be_used;
		}
		String tenantId = rules.getTenantId();
		// 只能获取根租户的镜像和属于当前租户的镜像
		if (!singleton.getRootProject().equals(tenantId) && !projectId.equals(tenantId)) {
			return ResultType.image_belong_to_other_tenant;
		}
		Integer min_disk = rules.getMinDisk();
		Integer min_ram = rules.getMinRam();
		Integer min_vcpu = rules.getVcpu();
		NovaFlavor novaFlavor = novaFlavorDao.findById(NovaFlavor.class, bean.getFlavorId());
		Map<String, Object> tip = new HashMap<>();
		if (!StrUtils.checkParam(novaFlavor)) {
			return ResultType.flavor_not_exist;
		}
		if (StrUtils.checkParam(min_ram, min_disk, min_vcpu)) {
			if (!(novaFlavor.getRam() >= min_ram && novaFlavor.getDisk() >= min_disk
					&& novaFlavor.getVcpus() >= min_vcpu)) {
				tip.put("min_ram", min_ram);
				tip.put("min_disk", min_disk);
				tip.put("min_vcpu", min_vcpu);
				this.warn("镜像最小值要求：min_ram" + min_ram + ",min_disk" + min_disk + ",min_vcpu" + min_vcpu);
				bean.setError(new JSONObject(tip));
				return ResultType.file_novaflavor_failure;
			}
		}
		return ResultType.success;
	}

	@Override
	public List<Map<String, Object>> getServerPort(String subnetId) {
		Map<String, Object> queryMap = new HashMap<>();
		queryMap.put("subnetId", subnetId);
		List<Map<String, Object>> ports = sqlQueryBiz.queryByName(SqlQueryProperty.QUERY_VLB_MEMBER_PORT, queryMap);
		return ports;
	}

	public String getIpByUuid(String uuid, boolean flag) {
		Map<String, Object> queryMap = new HashMap<>();
		queryMap.put("uuid", uuid);
		String vmIps = null;
		List<Map<String, Object>> ipList = new ArrayList<>();
		if (flag) {
			ipList = sqlQueryBiz.queryByName(SqlQueryProperty.QUERY_VM_PUBLIC_IP, queryMap);
		} else {
			ipList = sqlQueryBiz.queryByName(SqlQueryProperty.QUERY_VM_PRIVATE_IP, queryMap);
		}
		if (StrUtils.checkCollection(ipList)) {
			StringBuffer ips = new StringBuffer();
			for (Map<String, Object> stringObjectMap : ipList) {
				String ip = StrUtils.tranString(stringObjectMap.get("ip"));
				ips.append(ip + ",");
			}
			if (ips.length() > 0) {
				vmIps = ips.substring(0, ips.length() - 1);
			}
		}
		return vmIps;
	}

	@Override
	public void deleteNovaVm(NovaVm novaVm, String busId, CloudosClient client) {
		// 删除对应的网卡
		if (StrUtils.checkParam(novaVm.getUuid())) {
			List<Port> ports = portBiz.findByPropertyName(Port.class, "deviceId", novaVm.getUuid());
			for (Port port : ports) {
				String uri = HttpUtils.tranUrl(singleton.getCloudosApi(CloudosParams.CLOUDOS_API_PORTS_ACTION),
						port.getCloudosId());
				client.delete(uri);
				portBiz.deleteLocalPort(port);
			}
		}

		// 删除回收站记录
		Map<String, Object> deleteWhere2 = new HashMap<>();
		deleteWhere2.put("resId", busId);
		recycleItemsBiz.delete(deleteWhere2, RecycleItems.class);
		// 解除云硬盘绑定
		List<Volume> volumes = volumeBiz.findVolume(novaVm);
		for (Volume volume : volumes) {
			volume.setHost(null);
			volume.setAttachStatus("1");
			volume.setStatus(ConfigProperty.VOLUME_STATE_AVAILABLE);
			volumeBiz.update(volume);
		}
		ResourceNovaHandle resourceNovaHandle = new ResourceNovaHandle();
		resourceNovaHandle.updateFlavorQuota(novaVm.getFlavorId(), novaVm.getProjectId(), false, 1);
		// 删除云主机
		novaVmDao.deleteNovavm(novaVm);
		measureDetailBiz.stop(novaVm.getUuid(), this.getLoginUser(), true);
	}

	public JSONObject getVmJson(String uuid, String projectId, CloudosClient client) {
		if (null == uuid) {
			return null;
		}
		String uri = HttpUtils.tranUrl(singleton.getCloudosApi(CloudosParams.CLOUDOS_API_SERVER), projectId) + "/"
				+ uuid;
		JSONObject json = HttpUtils.getJson(uri, "server", client);
		return json;
	}

	@Override
	public boolean auth(String id) {
		if (this.getSessionBean().getSuperUser()) {
			return true;
		}
		return false;
	}

	@Override
	public void writeBack(NovaVm novaVm, String uuid) {
		novaVm.setUuid(uuid);
		novaVm.setPowerState(TaskTypeProperty.VM_POWER_STATE_START);
		novaVm.setVmState(ConfigProperty.novaVmState.get(1));// 正常
		novaVmDao.update(novaVm);
		FlavorParam flavorParam = new FlavorParam();
		flavorParam.setClassId("1");
		flavorParam.setAzoneId(novaVm.getAzoneId());
		flavorParam.setFlavorId(novaVm.getFlavorId());
		flavorParam.setUserId(novaVm.getOwner());
		flavorParam.setTenantId(novaVm.getProjectId());
		flavorParam.setResourceId(uuid);
		flavorParam.setCreatedBy(novaVm.getCreatedBy());
		measureDetailBiz.save(flavorParam, "创建");
	}

}

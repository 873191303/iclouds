package com.h3c.iclouds.task.novavm;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.h3c.iclouds.auth.CacheSingleton;
import com.h3c.iclouds.base.BaseDAO;
import com.h3c.iclouds.base.SpringContextHolder;
import com.h3c.iclouds.biz.NetworkBiz;
import com.h3c.iclouds.biz.NovaVmBiz;
import com.h3c.iclouds.biz.Server2VmBiz;
import com.h3c.iclouds.client.CasClient;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.dao.PortDao;
import com.h3c.iclouds.operate.CloudosParams;
import com.h3c.iclouds.po.IpAllocation;
import com.h3c.iclouds.po.Network;
import com.h3c.iclouds.po.NovaVm;
import com.h3c.iclouds.po.Port;
import com.h3c.iclouds.po.Rules;
import com.h3c.iclouds.po.Server2Vm;
import com.h3c.iclouds.po.Subnet;
import com.h3c.iclouds.po.Task;
import com.h3c.iclouds.task.BaseTask;
import com.h3c.iclouds.utils.ResourceHandle;
import com.h3c.iclouds.utils.Ssh2Utils;
import com.h3c.iclouds.utils.HttpUtils;
import com.h3c.iclouds.utils.LogUtils;
import com.h3c.iclouds.utils.StrUtils;

/**
 * 将虚拟机转为镜像
 * 1.将云主机关机
 * 2.在cloudos创建镜像（小文件镜像）
 * 3.根据创建的镜像创建云主机
 * 4.在cas上将云主机转为模板
 * 5.将cas上的模板替换镜像文件
 * @author zkf5485
 *
 */
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class NovaVmToImageTask extends BaseTask {
	
	private NovaVmBiz novaVmBiz = SpringContextHolder.getBean("novaVmBiz");
	
	private Server2VmBiz server2VmBiz = SpringContextHolder.getBean("server2VmBiz");
	
	private PortDao portDao = SpringContextHolder.getBean("portDao");
	
	private BaseDAO<IpAllocation> ipAllocationDao = SpringContextHolder.getBean("baseDAO");
	
	private BaseDAO<Rules> rulesDao = SpringContextHolder.getBean("baseDAO");
	
	private String imageName = "";
	
	// 新镜像id
	private String imageId = null;
	
	// 云主机id
	private String serverId = null;
	
	// 云主机id
	private String templateId = null;
	
	// cas连接
	private CasClient casClient = null;
	
	// 操作结果标志
	private boolean execSuccess = false;
	
	private Port port = null;
	
	private IpAllocation ipAllocation = null;
	
	private NovaVm novaVm = null;
	
	private List<String> msgList = new ArrayList<String>();
	
	private Rules rules = null;
	
	private Map<String, Object> serverMap = null;
	
	private String unuseServerId = null;
	
	public NovaVmToImageTask(Task task) {
		super(task);
	}
	
	/**
	 * 最后数据回收处理
	 */
	public void callbackHandle() {
		try {
			this.addMsg("========== callback handle ==========");
			
			// 删除cas模板
			if(templateId != null) {
				this.addMsg("Ready to delete cas template, id: " + templateId);
				String uri = "/cas/casrs/vmTemplate/deleteVmTemplate?id=" + templateId;
				JSONObject jsonObj = casClient.delete(uri, null);
				if(!HttpUtils.checkResultCode(jsonObj, "204", "410")) {
					this.addMsg("Delete cas template failure. id: " + templateId + ", name: " + this.imageName + ", result: " + jsonObj.toJSONString());
				}
			}
			
			// 是否创建了云主机
			if(serverId != null) {
				this.addMsg("Ready to delete cloudos cloud host, id: " + serverId);
				String uri = HttpUtils.getUrl(CloudosParams.CLOUDOS_API_SERVER_GET, this.task.getProjectId(), novaVm.getUuid());
				JSONObject jsonObj = this.client.delete(uri);
				if(HttpUtils.checkResultCode(jsonObj, "204")) {
					// 删除ip使用记录
					if(this.ipAllocation != null) {
						this.addMsg("Ready to delete ipAllocation:" + JSONObject.toJSONString(ipAllocation));
						ipAllocationDao.delete(this.ipAllocation);
						this.addMsg("Delete ipAllocation success");
					}
					
					// 删除网卡
					if(this.port != null) {
						this.addMsg("Ready to delete port:" + JSONObject.toJSONString(port));
						portDao.delete(port);
						this.addMsg("Delete port success");
					}
					
					// 删除虚拟机
					if(this.novaVm != null) {
						this.addMsg("Ready to delete novaVm:" + JSONObject.toJSONString(novaVm));
						novaVmBiz.delete(novaVm);
						this.addMsg("Delete iyun novaVm success");
					}
				} else {
					this.addMsg("Delete cloudos cloud host failure. id: " + serverId + ", name: " + this.imageName + ", result: " + jsonObj.toJSONString());
				}
			}
			
			this.deleteUnuseHost();
			
			if (rules != null) {
				this.addMsg("Ready to delete iyun image, id: " + imageId);
				this.rulesDao.delete(rules);
				this.addMsg("Delete iyun image success");
			}
			
			// 删除原临时使用的镜像数据
			List<Rules> list = this.rulesDao.findByPropertyName(Rules.class, "osMirName", imageName);
			if(StrUtils.checkCollection(list)) {
				for (Rules tempRule : list) {
					if(tempRule.getId().startsWith("temp")) {
						this.rulesDao.delete(tempRule);
					}
				}
			}
			
			// 删除创建的镜像
			if(imageId != null) {
				this.addMsg("Ready to delete cloudos image, id: " + imageId);
				String uri = HttpUtils.getUrl(CloudosParams.CLOUDOS_API_IMAGE, imageId);
				JSONObject jsonObj = this.client.delete(uri);
				if(!HttpUtils.checkResultCode(jsonObj, "204")) {
					this.addMsg("Delete cloudos image failure. id: " + imageId + ", name: " + this.imageName + ", result: " + jsonObj.toJSONString());
				}
			}
		} catch (Exception e) {
			this.addMsg("Callback handle error");
			LogUtils.exception(getClass(), e, "Callback handle error");
		}
	}
	
	private void deleteUnuseHost() {
		if(unuseServerId != null) {
			this.addMsg("Ready to delete cloudos unuse cloud host, id: " + unuseServerId);
			String uri = HttpUtils.getUrl(CloudosParams.CLOUDOS_API_SERVER_GET, this.task.getProjectId(), unuseServerId);
			JSONObject jsonObj = this.client.delete(uri);
			if(!HttpUtils.checkResultCode(jsonObj, "204", "404")) {
				this.addMsg("Delete cloudos unuse cloud host failure. id: " + unuseServerId + ", name: " + this.imageName + ", result: " + jsonObj.toJSONString());
			} else {
				this.addMsg("Delete cloudos unuse cloud host success. id: " + unuseServerId);
			}
			this.unuseServerId = null;
		}
	}

	@Override
	protected String handle() {
		JSONObject inputObj = JSONObject.parseObject(task.getInput());
		String novaId = inputObj.getString("novaId");
		String uuid = inputObj.getString("uuid");
		NovaVm vm = null;
		try {
			this.addMsg("Get source cloud host in cloudos");
			vm = novaVmBiz.findById(NovaVm.class, novaId);
			if(vm == null) {
				this.addMsg("Not find vm[" + novaId + "] in iyun");
				return ConfigProperty.TASK_STATUS4_END_FAILURE;
			}
			// 设置后续创建镜像/模板的名称
			imageName = inputObj.getString("name");
			this.addMsg("Get source cloud host in cas");
			Server2Vm entity = server2VmBiz.singleByClass(Server2Vm.class, StrUtils.createMap("uuid", uuid));
			if(entity == null) {
				this.addMsg("Not find vm[" + uuid + "] in cas");
				return ConfigProperty.TASK_STATUS4_END_FAILURE;
			}
			
			this.addMsg("Get cloudos cloud host vm status");
			String uri = HttpUtils.getUrl(CloudosParams.CLOUDOS_API_SERVER_GET, this.task.getProjectId(), uuid);
			JSONObject jsonObj = this.client.get(uri);
			JSONObject server = HttpUtils.getJSONObject(jsonObj, "server");
			if(server == null) {
				this.addMsg("Get vm in cloudos failure");
				return ConfigProperty.TASK_STATUS4_END_FAILURE;
			}
			// 云主机需要为关机状态
			String vmState = CloudosParams.getVmState(server.getString("status"));
			if(!"state_stop".equals(vmState)) {
				this.addMsg("Vm[" + entity.getVmName() + "] status is " + vmState);
				return ConfigProperty.TASK_STATUS4_END_FAILURE;
			}
			
			vm.setVmState(ConfigProperty.novaVmState.get(14));
			this.novaVmBiz.update(vm);
			this.addMsg("Create image in cloudos");
			// 在cloudos中创建一个小镜像
			String result = this.createCloudosImages(vm, inputObj);
			if(!ConfigProperty.TASK_STATUS3_END_SUCCESS.equals(result)) {
				this.addMsg(result);
				return ConfigProperty.TASK_STATUS4_END_FAILURE;
			}
			
			vm.setVmState(ConfigProperty.novaVmState.get(15));
			this.novaVmBiz.update(vm);
			// 根据镜像生成云主机
			try {
				this.addMsg("Create unuse cloud host in cloudos");
				result = this.createImageVm(inputObj, imageId);
				if(!ConfigProperty.TASK_STATUS3_END_SUCCESS.equals(result)) {
					this.addMsg(result);
					return ConfigProperty.TASK_STATUS4_END_FAILURE;
				}
			} catch (Exception e) {
				this.addMsg("Create cloud host by image error. cause: " + e.getCause().toString());
				LogUtils.exception(this.getClass(), e, "Create unuse cloud host by image error");
				return ConfigProperty.TASK_STATUS4_END_FAILURE;
			}
			
			vm.setVmState(ConfigProperty.novaVmState.get(16));
			this.novaVmBiz.update(vm);
			// 在cas创建模板
			this.addMsg("Create cloud host template in cas");
			result = this.createCasTemplate(entity);
			if(!ConfigProperty.TASK_STATUS3_END_SUCCESS.equals(result)) {
				this.addMsg(result);
				return ConfigProperty.TASK_STATUS4_END_FAILURE;
			}
			
			vm.setVmState(ConfigProperty.novaVmState.get(17));
			this.novaVmBiz.update(vm);
			try {
				this.addMsg("Create cloud host in cloudos");
				result = createNovaVm(inputObj, imageId);
				if(!ConfigProperty.TASK_STATUS3_END_SUCCESS.equals(result)) {
					this.addMsg(result);
					return ConfigProperty.TASK_STATUS4_END_FAILURE;
				}
			} catch (Exception e) {
				this.addMsg("Create cloud host by image error. cause: " + e.getCause().toString());
				LogUtils.exception(this.getClass(), e, "Create cloud host by image error");
				return ConfigProperty.TASK_STATUS4_END_FAILURE;
			}
			
			// 成功标志，表示不需要做回滚操作
			execSuccess = true;
		} catch (Exception e) {
			LogUtils.exception(getClass(), e, "Create image by cas vm failure");
			this.addMsg("Create image by cas vm failure. cause: " + e.getCause().toString());
			return ConfigProperty.TASK_STATUS4_END_FAILURE;
		} finally {
			String resultValue = ConfigProperty.TASK_STATUS3_END_SUCCESS;
			vm.setVmState(ConfigProperty.novaVmState.get(2));
			if(!execSuccess) {
				resultValue = ConfigProperty.TASK_STATUS4_END_FAILURE;
				this.callbackHandle();
			}
			this.novaVmBiz.update(vm);
			StringBuffer resultBuffer = new StringBuffer();
			if(msgList.isEmpty()) {
				this.addMsg(ResultType.success.toString());
			}
			for (String msg : msgList) {
				resultBuffer.append(msg);
			}
			this.saveTask2Exec(resultBuffer.toString(), resultValue, 0);
		}
		return ConfigProperty.TASK_STATUS3_END_SUCCESS;
	}
	
	/**
	 * 根据镜像创建一个虚拟机
	 * @param inputObj
	 * @param imageId
	 * @return
	 */
	private String createImageVm(JSONObject inputObj, String imageId) {
		String flavorId = inputObj.getString("flavorId");
		String azoneName = inputObj.getString("azoneName");
		String netId = inputObj.getString("netId");
//		String azoneId = inputObj.getString("azoneId");
		List<Map<String, Object>> networks = new ArrayList<Map<String, Object>>();
		Map<String, Object> network = StrUtils.createMap("uuid", netId);
		networks.add(network);
		
		List<Map<String, Object>> security_groups = new ArrayList<Map<String, Object>>();
		Map<String, Object> security_group = StrUtils.createMap("name", "default");
		security_groups.add(security_group);
		
		serverMap = StrUtils.createMap("name", imageName);
		serverMap.put("adminPass", "1234567890");
		serverMap.put("flavorRef", flavorId);
		serverMap.put("max_count", 1);
		serverMap.put("min_count", 1);
		serverMap.put("imageRef", imageId);
		serverMap.put("availability_zone", azoneName);
		serverMap.put("security_groups", security_groups);
		serverMap.put("networks", networks);
		String uri = CacheSingleton.getInstance().getCloudosApi(CloudosParams.CLOUDOS_API_SERVER);
		uri = HttpUtils.tranUrl(uri, this.task.getProjectId());
		JSONObject jsonObj = client.post(uri, StrUtils.createMap("server", serverMap));
		JSONObject server = HttpUtils.getJSONObject(jsonObj, "server");
		if(server == null) {
			return "Create cloud host error, result: " + jsonObj.toJSONString();
		}
		this.unuseServerId = server.getString("id");
		int create_query_num = StrUtils.tranInteger(CacheSingleton.getInstance().getConfigValue("create_query_num"));
		if(create_query_num == 0) {
			create_query_num = 100;
		}
		uri = HttpUtils.getUrl(CloudosParams.CLOUDOS_API_SERVER_GET, this.task.getProjectId(), unuseServerId);
		for (int i = 0; i < create_query_num; i++) {
			jsonObj = this.client.get(uri);
			server = HttpUtils.getJSONObject(jsonObj, "server");
			if(server == null) {
				return "Create cloud host error";
			}
			String state = server.getString("status");
			if (null != state) {
				if ("ACTIVE".equals(state)) {
					break;
				} else if ("ERROR".equals(state)) {
					return "Get cloud host error";
				} else if ("Unknown".equals(state)) {
					return "Get cloud host unknown";
				}
			}
			try {
				TimeUnit.SECONDS.sleep(5);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return ConfigProperty.TASK_STATUS3_END_SUCCESS;
	}
	
	private String createNovaVm(JSONObject inputObj, String imageId) {
		String flavorId = inputObj.getString("flavorId");
		String azoneId = inputObj.getString("azoneId");
		if(serverMap == null) {
			return "Create server parameter is null";
		}
		String uri = CacheSingleton.getInstance().getCloudosApi(CloudosParams.CLOUDOS_API_SERVER);
		uri = HttpUtils.tranUrl(uri, this.task.getProjectId());
		JSONObject jsonObj = client.post(uri, StrUtils.createMap("server", serverMap));
		JSONObject server = HttpUtils.getJSONObject(jsonObj, "server");
		if(server == null) {
			return "Create cloud host error, result: " + jsonObj.toJSONString();
		}
		String serverId = server.getString("id");
		int create_query_num = StrUtils.tranInteger(CacheSingleton.getInstance().getConfigValue("create_query_num"));
		if(create_query_num == 0) {
			create_query_num = 100;
		}
		uri = HttpUtils.getUrl(CloudosParams.CLOUDOS_API_SERVER_GET, this.task.getProjectId(), serverId);
		for (int i = 0; i < create_query_num; i++) {
			jsonObj = this.client.get(uri);
			server = HttpUtils.getJSONObject(jsonObj, "server");
			if(server == null) {
				return "Create cloud host error";
			}
			String state = server.getString("status");
			if (null != state) {
				if ("ACTIVE".equals(state)) {
					break;
				} else if ("ERROR".equals(state)) {
					return "Get cloud host error";
				} else if ("Unknown".equals(state)) {
					return "Get cloud host unknown";
				}
			}
			try {
				TimeUnit.SECONDS.sleep(5);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		novaVm = new NovaVm();
		novaVm.setAzoneId(azoneId);
		novaVm.setFlavorId(flavorId);
		novaVm.setHostName(imageName);
		novaVm.setHost("宿主机");
		novaVm.setUuid(serverId);
		novaVm.setVmState(ResultType.state_normal.toString());
		novaVm.setVcpus(inputObj.getInteger("vcpus"));
		novaVm.setMemory(inputObj.getInteger("memory"));
		novaVm.setRamdisk(inputObj.getInteger("ramdisk"));
		novaVm.setOsType(inputObj.getString("osType"));
		novaVm.setPowerState(1);
		novaVm.setProjectId(this.task.getProjectId());
		novaVm.setOwner(this.task.getCreatedBy());
		novaVm.createdUser(this.task.getCreatedBy());
		novaVm.setImageRef(imageId);
		this.novaVmBiz.add(novaVm);
		this.serverId = novaVm.getId();
		// 获取网络
		uri = HttpUtils.getUrl(CloudosParams.CLOUDOS_API_OSINTERFACE, this.task.getProjectId(), novaVm.getUuid());
		JSONArray interfaceAttachments = HttpUtils.getJSONArray(this.client.get(uri), "interfaceAttachments");
		if(StrUtils.checkCollection(interfaceAttachments)) {
			savePort(novaVm, interfaceAttachments.getJSONObject(0));
		}
		
		// 删除之前不可用
		this.deleteUnuseHost();
		return ConfigProperty.TASK_STATUS3_END_SUCCESS;
	}
	
	/**
	 * 在cloudos中创建一个小的镜像
	 * @param vm
	 * @return
	 */
	private String createCloudosImages(NovaVm vm, JSONObject inputObj) {
		// 获取原镜像类型
		String originImageId = vm.getImageRef();
		String uri = HttpUtils.getUrl(CloudosParams.CLOUDOS_API_IMAGE, originImageId);
		JSONObject image = HttpUtils.getJSONObject(this.client.get(uri));
		String cas_ostype = "linux|CentOS 4/5/6(64bit)";
		if(image != null) {
			cas_ostype = image.getString("cas_ostype");
		}
		
		this.imageId = null;
		Map<String, Object> map = StrUtils.createMap();
		map.put("disk_format", "qcow2");
		map.put("name", imageName);
		map.put("cas_ostype", cas_ostype);
		map.put("virt_type", "CAS");
		map.put("min_cpu", "1");
		map.put("min_disk", 10);
		map.put("min_ram", 2048);
		map.put("container_format", "bare");
		// 创建一个新的镜像
		uri = CacheSingleton.getInstance().getCloudosApi(CloudosParams.CLOUDOS_API_IMAGES);
		JSONObject obj = this.client.post(uri, map);
		JSONObject record = HttpUtils.getJSONObject(obj);
		if(record == null) {
			return "Create image info failure. result: " + obj.toJSONString();
		}
		String fileUri = record.getString("file");
		imageId = record.getString("id");
		
		// 获取镜像路径
		String filePath = CacheSingleton.getInstance().getConfigValue("iyun.image.path");
		if(!StrUtils.checkParam(filePath, fileUri)) {
			return "Upload file or cloudos get file path is null";
		}
		File file = new File(filePath);
		if(!file.exists()) {
			return "Upload file is not exist";
		}
		map.clear();
		map = StrUtils.createMap(CloudosParams.INPUTSTREAM, filePath);
		// 上传文件
		JSONObject jsonObj = client.setHeaderLocal(CloudosParams.CONTENT_TYPE, CloudosParams.APPLICATION_OCTET_STREAM).put(fileUri, map);
		if(!jsonObj.getString("result").startsWith("2")) {
			return "Upload file to image error";
		}
		
		// 删除原临时使用的镜像数据
		List<Rules> list = this.rulesDao.findByPropertyName(Rules.class, "osMirName", inputObj.getString("name"));
		if(StrUtils.checkCollection(list)) {
			for (Rules tempRule : list) {
				if(tempRule.getId().startsWith("temp")) {
					this.rulesDao.delete(tempRule);
				}
			}
		}
		
		rules = new Rules();
		rules.setId(imageId);
		rules.setOsMirName(inputObj.getString("name"));
		rules.setOsMirId("2");
		rules.setUserId(this.task.getCreatedBy());
		rules.setTenantId(this.task.getProjectId());
		rules.setMinSwap(1);
		rules.setIsDefault("0");
		rules.setMinRam(1024);
		rules.setMinDisk(10);
		rules.setVcpu(1);
		rules.setSyncTime(new Date());
		rules.setFormat(image.getString("container_format"));
		rules.createdUser(this.task.getCreatedBy());
		this.rulesDao.add(rules);
		return ConfigProperty.TASK_STATUS3_END_SUCCESS;
	}
	
	/**
	 * 在cas中创建模板
	 * @param entity
	 * @return
	 */
	private String createCasTemplate(Server2Vm entity) {
		casClient = CasClient.createByCasId(entity.getBelongCas());
		if(casClient == null) {
			return "Get Cas Client failure";
		}
		
		JSONObject casCloudHost = HttpUtils.getJSONObject(casClient.get("/cas/casrs/vm/" + entity.getCasId()));
		if(casCloudHost == null) {
			return "Get cloud host in cas failure";
		}
		
		String filePath = null;
		try {
			JSONObject storage = casCloudHost.getJSONObject("storage");
			if(storage != null) {
				filePath = storage.getString("storeFile");
			}
		} catch (Exception e) {
			LogUtils.exception(getClass(), e, "Get storage exception");
			try {
				JSONArray storages = casCloudHost.getJSONArray("storage");
				if(StrUtils.checkCollection(storages)) {
					for (int i = 0; i < storages.size(); i++) {
						String storeFile = storages.getJSONObject(i).getString("storeFile");
						// 包含inc，但不包含volume
						if(storeFile.contains("inc") && !storeFile.contains("volume")) {
							filePath = storeFile;
							break;
						}
					}
				}
			} catch (Exception e2) {
				LogUtils.exception(getClass(), e, "Get storage exception");
			}
		}
		
		if(!StrUtils.checkParam(filePath)) {
			return "Get cloud host storage in cas failure";
		}
		String result = this.moveCasTemplate(filePath, imageName);
		// 替换文件
		return result;
	}
	
	/**
	 * 将cloudos下发的镜像替换为cas模板
	 * @param filePath	模板存储路径
	 * @param templateName			
	 * @return
	 */
	private String moveCasTemplate(String filePath, String templateName) {
		// cloudos上传镜像路径
		String sharePath = CacheSingleton.getInstance().getConfigValue("iyun.cas.share.path");
		String hostIp = CacheSingleton.getInstance().getConfigValue("iyun.cas.host.ip");
		String password = CacheSingleton.getInstance().getConfigValue("iyun.cas.host.password");
		String userName = CacheSingleton.getInstance().getConfigValue("iyun.cas.host.username");
		this.addMsg("Connection to " + hostIp + ", user: " + userName);
		Ssh2Utils ssh = Ssh2Utils.create(hostIp, userName, password);
		if(!ssh.isAuthenticated()) {
			return "Connection to cas host error";
		}
		
		// 检查是否存在及是否为链接克隆
		String checkTypeCmd = "qemu-img info " + filePath;
		List<String> list = ssh.execCmd(checkTypeCmd);
		if(list == null || list.isEmpty()) {
			return "Check file failure. Exec cmd: " + checkTypeCmd + ". result is null";
		}
		// 默认为非克隆
		boolean isLinkClone = false;
		for (String res : list) {
			if(res.contains("backing file")) {	// 有base标志
				isLinkClone = true;
				break;
			}
		}
		
		try {
			String execCmd = null;
			if(isLinkClone) {
				// 合并成新的文件
				execCmd = "qemu-img convert -f qcow2 -O qcow2 $sourceFile $targetFile";
			} else {
				// 拷贝文件
				execCmd = "cp $sourceFile $targetFile";
			}
			execCmd = execCmd.replace("$sourceFile", filePath).replace("$targetFile", sharePath + this.imageId);
			this.addMsg("Exec cmd: " + execCmd);
			list = ssh.execCmd(execCmd);
			if(list == null || !list.isEmpty()) {
				return "Move cas template to share path error. Exec cmd: " + execCmd + ". result is null";
			}
		} catch (Exception e) {
			LogUtils.exception(getClass(), e, "Exec cmd error.");
			return "Move cas template to share path error. e.Message: " + e.getMessage();
		} finally {
			ssh.close();	
		}
		return ConfigProperty.TASK_STATUS3_END_SUCCESS; 
	}
	
	
	/**
	 * 将cloudos下发的镜像替换为cas模板
	 * @param templetStoragePath	模板存储路径
	 * @param templateName			
	 * @return
	 */
	private String apiMoveCasTemplate(String templetStoragePath, String templateName) {
		// cloudos上传镜像路径
		String sharePath = CacheSingleton.getInstance().getConfigValue("iyun.cas.share.path");
		String hostIp = CacheSingleton.getInstance().getConfigValue("iyun.cas.host.ip");
		String password = CacheSingleton.getInstance().getConfigValue("iyun.cas.host.password");
		String userName = CacheSingleton.getInstance().getConfigValue("iyun.cas.host.username");
		this.addMsg("Connection to " + hostIp + ", user: " + userName);
		Ssh2Utils ssh = Ssh2Utils.create(hostIp, userName, password);
		if(!ssh.isAuthenticated()) {
			return "Connection to cas host error";
		}
		String cmd = "ls " + templetStoragePath;
		this.addMsg("Exec cmd: " + cmd);
		List<String> list = ssh.execCmd(cmd);
		if(list == null) {
			return "Get template file path error. Exec cmd: " + cmd + ". result is null";
		}
		String fileName = null;
		for (String line : list) {
			if(line.contains(templateName) && line.contains("inc")) {
				fileName = line;
				break;
			}
		}
		
		// mv templatefilePath sharePath
		StringBuffer cmdBuffer = new StringBuffer("mv ");
		cmdBuffer.append(templetStoragePath).append("/").append(fileName).append(" ").append(sharePath).append(this.imageId);
		this.addMsg("Exec cmd: " + cmdBuffer.toString());
		list = ssh.execCmd(cmdBuffer.toString());
		if(list == null) {
			return "Move cas template to share path error. Exec cmd: " + cmdBuffer.toString() + ". result is null";
		}
		return ConfigProperty.TASK_STATUS3_END_SUCCESS; 
	}
	
	/**
	 * 保存云主机网卡，IP信息
	 * @param novaVm
	 * @param interfaceAttachment
	 */
	private void savePort(NovaVm novaVm, JSONObject interfaceAttachment) {
		NetworkBiz networkBiz = SpringContextHolder.getBean("networkBiz");
		BaseDAO<Subnet> subnetDao = SpringContextHolder.getBean("baseDAO");
		String networkId = interfaceAttachment.getString("net_id");
		// 插入端口表
		port = new Port();
		port.setTenantId(novaVm.getProjectId());
		port.setDeviceId(novaVm.getUuid());
		port.setDeviceOwner(interfaceAttachment.getString("device_owner"));
		port.createdUser(novaVm.getOwner());
		port.setUserId(novaVm.getOwner());
		port.setNetWorkId(networkId);
		String port_id = interfaceAttachment.getString("port_id");// 获得网卡id
		String macAddress = interfaceAttachment.getString("mac_addr"); // 网卡mac地址
		port.setMacAddress(macAddress);
		port.setCloudosId(port_id);
		this.syncPortName(port_id, port, novaVm); // 同步网卡名称
		// 云主机创建的自带网卡标志
		port.setIsinit(true);
		portDao.add(port);
		JSONArray fixed_ips = interfaceAttachment.getJSONArray("fixed_ips");
		for (int k = 0; k < fixed_ips.size(); k++) {
			JSONObject fixed_ip = fixed_ips.getJSONObject(k);
			String ipAddress = fixed_ip.getString("ip_address");
			// 插入Ip使用表
			ipAllocation = new IpAllocation();
			// 防止外键插不进去
			List<Port> ports = portDao.findByPropertyName(Port.class, "deviceId", novaVm.getUuid());
			ipAllocation.setPortId(ports.get(0).getId());
			ipAllocation.setIpAddress(ipAddress);
			List<Network> networks2 = networkBiz.findByPropertyName(Network.class, "cloudosId", networkId);
			Network network = networks2.get(0);

			List<Subnet> subnets = subnetDao.findByPropertyName(Subnet.class, "networkId", network.getId());
			Subnet subnet = subnets.get(0);
			ipAllocation.setSubnetId(subnet.getId());// 外键
			ipAllocationDao.add(ipAllocation);
		}
	}
	
	private void syncPortName(String portId, Port port, NovaVm novaVm) {
		String uri = CacheSingleton.getInstance().getCloudosApi(CloudosParams.CLOUDOS_API_PORTS_ACTION);
		uri = HttpUtils.tranUrl(uri, portId);
		JSONObject response = client.get(uri);
		if (ResourceHandle.judgeResponse(response)) {
			response=HttpUtils.getJSONObject(response, "port");
			port.setDeviceOwner(response.getString("device_owner"));
			port.setStatus(response.getString("status"));
			Boolean admin_state_up=response.getBoolean("admin_state_up");
			if (StrUtils.checkParam(admin_state_up)) {
				port.setAdminStateUp(admin_state_up);
			}
			port.setPortType(response.getString("binding:vnic_type"));
			String name = response.getString("name");
			if (StrUtils.checkParam(name)) {
				port.setName(name);
			} else {
				port.setName(generate(novaVm.getHostName()));
			}
		}
	}
	
	private String generate(String hostName) {
		String result = "";
		String uuid = UUID.randomUUID().toString().substring(0, 6);
		result += hostName + "_VirtualNIC_" + uuid;
		return result;
	}
	
	private void addMsg(String msg) {
		this.msgList.add(msg + ".</br>");
	}
	
	protected String apiCreateCasTemplate(Server2Vm entity) {
		casClient = CasClient.createByCasId(entity.getBelongCas());
		if(casClient == null) {
			return "Get Cas Client failure";
		}
		
		StringBuffer uriBuffer = new StringBuffer("/cas/casrs/vmTemplate/addVmTemplate?");
		uriBuffer.append("id=" + entity.getCasId());
		uriBuffer.append("&domainName=" + imageName);
		uriBuffer.append("&desc=" + imageName);
		uriBuffer.append("&opType=1");
		JSONObject record = HttpUtils.getJSONObject(casClient.post(uriBuffer.toString(), null));
		if(record == null) {	// 状态是否异常
			return "Create cas template failure. request uri: " + uriBuffer.toString();
		}
		
		String msgId = record.getString("msgId");
		String uri = "/cas/casrs/message/" + msgId;
		int times = StrUtils.tranInteger(CacheSingleton.getInstance().getConfigValue("iyun.cas.template.wait.times"));
		if(times == 0) {
			times = 288;	// 默认为228
		}
		for (int i = 0; i < times; i++) {
			JSONObject jsonObj = HttpUtils.getJSONObject(casClient.get(uri));
			if(jsonObj == null) {
				return "Get create template process error. msgId: " + msgId;
			}
			String result_ = jsonObj.getString("result");
			if(StrUtils.checkParam(result_)) {
				if(!"0".equals(result_)) {
					return "Get create template failure. msgId: " + msgId;
				} else {
					break;
				}
			}
			try {
				TimeUnit.SECONDS.sleep(5);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		uri = "/cas/casrs/vmTemplate/vmTemplateList?offset=0&limit=100";
		JSONObject jsonObj = casClient.get(uri);
		JSONArray array = HttpUtils.getJSONArray(jsonObj, "domain");
		if(!StrUtils.checkCollection(array)) {
			return "Get template list size is 0";
		}
		
		// 获取生成模板的存储路径
		String templetStoragePath = null;
		for (int i = 0; i < array.size(); i++) {
			JSONObject obj = array.getJSONObject(i);
			String name = obj.getString("name");
			if(this.imageName.equals(name)) {
				this.templateId = obj.getString("id");
				templetStoragePath = obj.getString("templetStoragePath") + "/" + name;
				break;
			}
		}
		String result = this.apiMoveCasTemplate(templetStoragePath, imageName);
		// 替换文件
		return result;
	}
	
}

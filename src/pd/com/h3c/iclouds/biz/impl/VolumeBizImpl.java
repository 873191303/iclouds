package com.h3c.iclouds.biz.impl;

import com.alibaba.fastjson.JSONObject;
import com.h3c.iclouds.auth.CacheSingleton;
import com.h3c.iclouds.base.BaseBizImpl;
import com.h3c.iclouds.base.BaseRestControl;
import com.h3c.iclouds.biz.MeasureDetailBiz;
import com.h3c.iclouds.biz.NovaVmBiz;
import com.h3c.iclouds.biz.ProjectBiz;
import com.h3c.iclouds.biz.QuotaUsedBiz;
import com.h3c.iclouds.biz.RecycleItemsBiz;
import com.h3c.iclouds.biz.RenewalBiz;
import com.h3c.iclouds.biz.TaskBiz;
import com.h3c.iclouds.biz.UserBiz;
import com.h3c.iclouds.biz.VolumeBiz;
import com.h3c.iclouds.po.Renewal;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.common.TaskTypeProperty;
import com.h3c.iclouds.dao.UserDao;
import com.h3c.iclouds.dao.VolumeDao;
import com.h3c.iclouds.dao.VolumeFlavorDao;
import com.h3c.iclouds.exception.MessageException;
import com.h3c.iclouds.operate.CloudosClient;
import com.h3c.iclouds.operate.CloudosParams;
import com.h3c.iclouds.po.NovaVm;
import com.h3c.iclouds.po.RecycleItems;
import com.h3c.iclouds.po.User;
import com.h3c.iclouds.po.VmExtra;
import com.h3c.iclouds.po.Volume;
import com.h3c.iclouds.po.VolumeFlavor;
import com.h3c.iclouds.utils.HttpUtils;
import com.h3c.iclouds.utils.InvokeSetForm;
import com.h3c.iclouds.utils.PwdUtils;
import com.h3c.iclouds.utils.ResourceHandle;
import com.h3c.iclouds.utils.StrUtils;
import com.h3c.iclouds.validate.ValidatorUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("volumeBiz")
public class VolumeBizImpl extends BaseBizImpl<Volume> implements VolumeBiz {

	@Resource
	private VolumeDao volumeDao;

	@Resource
	private UserDao userDao;

	@Resource
	private NovaVmBiz novaVmBiz;

	@Resource
	private RecycleItemsBiz recycleItemsBiz;

	@Resource
	private TaskBiz taskBiz;

	@Resource
	private VolumeFlavorDao volumeFlavorDao;

	@Resource
	private ProjectBiz projectBiz;

	@Resource
	private QuotaUsedBiz quotaUsedBiz;

	@Resource
	private MeasureDetailBiz measureDetailBiz;

	@javax.annotation.Resource
	private UserBiz userBiz;

	// 租期过期
	@Resource
	private RenewalBiz renewalBiz;

	@Override
	public PageModel<Volume> findForPage(PageEntity entity) {
		return volumeDao.findForPage(entity);
	}
	
	//云硬盘扩容
	//cloudos.api.server.renewaldisk
	@Override
	public ResultType Capacity(Volume bean,String capacity) {//capacity 扩容大小
		CloudosClient cloudosClient = CloudosClient.createAdmin();
		String uri = singleton.getCloudosApi(CloudosParams.CLOUDOS_API_SERVER_RENEWALDISK);
		uri = HttpUtils.tranUrl(uri, bean.getProjectId());
		uri = HttpUtils.tranUrl(uri, bean.getUuid());
		Map<String, Object> params = new HashMap<>();
		Map<String, Object> params2 = new HashMap<>();
		params.put("new_size", Integer.parseInt(capacity));
		params2.put("os-extend", params);
		JSONObject re = (JSONObject) JSONObject.toJSON(params2);
		for (int i = 0; i < 1; i++) {
			JSONObject result = cloudosClient.post(uri, re);
			String status = result.getString("result");
			if ("202".equals(status)) { 
				bean.setSize(Integer.parseInt(capacity));
				this.update(bean);
				return ResultType.success;
			}else {
				return ResultType.data_error;
			}
		}
		return ResultType.system_error;
	}

	// 云硬盘续租
	@Override
	public ResultType Renewal(Renewal bean, String userId, String day) {
		//User user = userBiz.findById(User.class, userId);
		CloudosClient cloudosClient = CloudosClient.createAdmin();
		// String uri =
		// singleton.getCloudosApi(CloudosParams.CLOUDOS_API_SERVER_RENEWAL);
		String uri = "/cloudos/restenancy";
		Map<String, Object> params = new HashMap<>();
		Map<String, Object> params2 = new HashMap<>();
		// 组装的数据最好从cloudos平台查询
		params2.put("uuid", bean.getUuid());// 资源租期uuid
		params2.put("userUuid", bean.getUserUuid());// 用户uuid
		params2.put("serUuid", bean.getServiceUuid());// 主机服务id
		params2.put("resUuid", bean.getResourceUuid());// 主机id
		params2.put("resName", bean.getResourceName());// 主机名称
		params2.put("startTime", bean.getStartTime().getTime());// 租期开始时间
		// 过期时间计算
//		Calendar rightNow = Calendar.getInstance();
//		rightNow.setTime(bean.getEndTime());
//		rightNow.add(Calendar.MONTH, Integer.parseInt(day));// 日期加N个月
//		Date endTime = rightNow.getTime();
		params2.put("endTime", bean.getEndTime().getTime());// 租期到期时间
		params2.put("due", bean.getDueAction());// 是否到期
		params2.put("tenantId", bean.getServiceUuid());// 组织id
		params2.put("tenancyDay", null);// 续租时间
		params2.put("userName", bean.getUserName());// 用户名
		params2.put("tenancyUuid", null);
		String dayUuid = "";
		switch(day) {
			case  "1天":
				dayUuid = "23d539d1-d7b0-493c-9486-de439618efc1";
				break;
			case  "1周":
				dayUuid = "494c3e40-01c5-4173-aa95-04350659d6ac";
				break;
			case  "1季度":
				dayUuid = "8bf4a75f-4c9e-4d2f-936f-18fe2711ee65";
				break;
			case  "1月":
				dayUuid = "ee120318-0500-4678-9782-0113e5375f8f";
				break;
			case  "1年":
				dayUuid = "d7f93e3d-e4d5-4d96-999a-8cd864bd9c58";
				break;
			case  "永久":
				dayUuid = "7b0a70c0-a480-42cd-a5ea-c95cf77af89a";
				break;
				
		}
		params2.put("tenancyUuid", dayUuid);
		params2.put("description", "");// 未作说明
		params.put("resourceTenancy", params2);
		JSONObject re = (JSONObject) JSONObject.toJSON(params);
		for (int i = 0; i < 2; i++) {
			JSONObject result = cloudosClient.put(uri, re);
			String status = result.getString("result");
			if ("200".equals(status)) {
				// 修改本地租期时间表最好是查询clouds平台时间然后插入本地
				// 在这里查询cloudos 平台的过期时间表，然后更新本地的过期时间表
				JSONObject obj = result.getJSONObject("record");
				JSONObject obj2 = obj.getJSONObject("resourceTenancy");
				Renewal ren = new Renewal();
				ren.setUuid(obj2.getString("uuid"));
				ren.setUserUuid(obj2.getString("userUuid"));
				ren.setUserName(obj2.getString("userName"));
				ren.setServiceUuid(obj2.getString("serUuid"));
				ren.setResourceUuid(obj2.getString("resUuid"));
				ren.setResourceName(obj2.getString("resName"));
				ren.setStartTime(new Date(obj2.getLong("startTime")));
				ren.setEndTime(new Date(obj2.getLong("endTime")));
				ren.setIsdue(obj2.getBoolean("due"));
				ren.setTenancyDay(obj2.getInteger("tenancyDay"));
				ren.setDeleted(obj2.getBoolean("deleted"));
				ren.setDescription(obj2.getString("description"));
				renewalBiz.update(ren);
				return ResultType.success;
			}
		}
		return ResultType.system_error;
	}

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

	@Override
	public Volume update(String id, Volume entity) {
		// 设置日志的资源id
		this.request.setAttribute("id", id);
		CloudosClient client = this.getSessionBean().getCloudClient();
		if (null == client) {
			throw new MessageException(ResultType.system_error);
		}
		String user = this.getLoginUser();
		String projectId = this.getProjectId();
		Map<String, String> validatorMap = ValidatorUtils.validator(entity); // 验证数据
		if (validatorMap.isEmpty()) {
			Volume volume = this.findUsableVolumeById(id);
			if (null != volume) {
				// 设置日志的资源名称
				this.request.setAttribute("name", volume.getName());

				String cloudosId = volume.getUuid();
				if (null == volume.getUuid() || "".equals(volume.getUuid())) { // 验证
					// 对应的cloudos主键是否存在
					throw new MessageException(ResultType.cloudos_id_not_exist);
				}

				if (!projectBiz.checkOptionRole(volume.getProjectId(), volume.getOwner2())) {
					throw new MessageException(ResultType.unAuthorized);
				}

				Map<String, Object> vmDataMap = ResourceHandle.tranToMap(entity, ConfigProperty.RESOURCE_TYPE_VOLUME);
				Map<String, Object> vmParams = ResourceHandle.getParamMap(vmDataMap, "volume"); // 准备参数
				String uri = CacheSingleton.getInstance().getCloudosApi(CloudosParams.CLOUDOS_API_VOLUME_ACTION);

				Map<String, String> params = new HashMap<>();
				params.put("tenant_id", projectId);
				params.put("volume_id", cloudosId);
				uri = HttpUtils.tranUrl(uri, params); // uri
				JSONObject vmResponse = client.put(uri, vmParams); // 修改云硬盘
				if (!ResourceHandle.judgeResponse(vmResponse)) {
					throw new MessageException(ResultType.cloudos_exception);
				}

				// 更新规格
				InvokeSetForm.copyFormProperties(entity, volume);
				volume.updatedUser(user);
				String flavorId = volume.getFlavorId();
				if (null != flavorId && !"".equals(flavorId)) {
					VolumeFlavor flavor = volumeFlavorDao.findById(VolumeFlavor.class, volume.getFlavorId());
					flavor.setDescription(entity.getDescription());
					flavor.updatedUser(user);
				} else {
					VolumeFlavor flavor = new VolumeFlavor();
					flavor.setSize(volume.getSize());
					flavor.setDeleted(ConfigProperty.VOLUME_DELETED0_NORMAL);
					flavor.setDescription(entity.getDescription());
					flavor.createdUser(user);
					flavor.setVolumeType(volume.getVolumeType());
					flavor.updatedUser(user);
					String newFid = volumeFlavorDao.add(flavor);
					volume.setFlavorId(newFid);
				}
				volumeDao.update(volume); // 更新本地
				return volume;
			}
			throw new MessageException(ResultType.deleted);
		}
		throw new MessageException(ResultType.parameter_error);
	}

	@Override
	public void save(Volume entity, String userId, CloudosClient client) {
		String projectId = entity.getProjectId(); // 获取当前租户
		boolean isValid = StrUtils.checkParam(entity.getName(), entity.getSize(), entity.getVolumeType(),
				entity.getAzoneId());
		if (isValid) {
			String volumeType = entity.getVolumeType();
			if (!volumeType.equals(ConfigProperty.VOLUME_TYPE_NORMAL)
					&& !volumeType.equals(ConfigProperty.VOLUME_TYPE_SSD)) {
				throw new MessageException(ResultType.volume_type_error);
			}

			User user = userDao.findById(User.class, userId);
			if (null == user.getCloudosId() || "".equals(user.getCloudosId())) {
				throw new MessageException(ResultType.cloudos_id_not_exist);
			}

			String imageId = entity.getImageRef();
			if (null != imageId && !"".equals(imageId)) {
				String imageBaseUrl = CacheSingleton.getInstance().getCloudosApi(CloudosParams.CLOUDOS_API_IMAGE);
				if (!checkBaseResource(imageBaseUrl, imageId, client)) {
					throw new MessageException(ResultType.image_resource_not_synchronized);
				}
			}

			String zoneId = entity.getAzoneId();
			if (null != zoneId && !"".equals(zoneId)) {
				String zoneBaseUrl = CacheSingleton.getInstance()
						.getCloudosApi(CloudosParams.CLOUDOS_API_AZONES_ACTION);
				if (!checkBaseResource(zoneBaseUrl, zoneId, client)) {
					throw new MessageException(ResultType.zone_resource_not_synchronized);
				}
			}

			int count = StrUtils.tranInteger(entity.getCount());
			count = (count <= 0) ? 1 : count; // 默认创建一个硬盘
			String cloudosId = user.getCloudosId();
			entity.setProjectId(projectId); // 设置租户属性值
			// TODO volumeType（类型）， lease（租期）
			Map<String, Object> vmDataMap = ResourceHandle.tranToMap(entity, ConfigProperty.RESOURCE_TYPE_VOLUME);
			vmDataMap.put("user_id", cloudosId);
			Map<String, String> metaMap = new HashMap<>(); // metadata信息
			metaMap.put("user_name", user.getLoginName());
			metaMap.put("azone_label", entity.getZone());
			metaMap.put("azone_uuid", entity.getAzoneId());
			vmDataMap.put("metadata", JSONObject.toJSON(metaMap));
			String uri = HttpUtils.tranUrl(singleton.getCloudosApi(CloudosParams.CLOUDOS_API_VOLUMES), projectId);
			JSONObject params = new JSONObject();
			params.put("uri", uri);

			String name = entity.getName();

			StringBuffer ids = new StringBuffer();
			StringBuffer names = new StringBuffer();

			for (int i = 0; i < count; i++) {
				String newName = name;
				if (count > 1) {
					int postfix = i + 1;
					newName = name + "_" + postfix;
				}

				// 更新硬盘规格表(检查同种规格同种类型的规格是否已经存在-不存在则新建)
				Map<String, Object> queryMap = new HashMap<>();
				queryMap.put("size", entity.getSize());
				queryMap.put("volumeType", volumeType);
				List<VolumeFlavor> volumeFlavors = volumeFlavorDao.listByClass(VolumeFlavor.class, queryMap);
				VolumeFlavor volumeFlavor;
				if (StrUtils.checkCollection(volumeFlavors)) {
					volumeFlavor = volumeFlavors.get(0);
					if (!StrUtils.checkParam(volumeFlavor.getName())) {
						String volumeTypeName = "1".equals(volumeType) ? "普通硬盘"
								: ("2".equals(volumeType) ? "固态硬盘" : "创建云主机的系统盘");
						volumeFlavor.setName(entity.getSize() + "*" + volumeTypeName);
						volumeFlavorDao.update(volumeFlavor);
					}
					for (int j = 1; j < volumeFlavors.size(); j++) {
						// TODO: 2017/5/9 删除重复的规格并将云硬盘的规格id改正(兼容上个版本)
						VolumeFlavor flavor = volumeFlavors.get(j);
						List<Volume> volumes = volumeDao.findByPropertyName(Volume.class, "flavorId", flavor.getUuid());
						if (StrUtils.checkCollection(volumes)) {
							for (Volume volume : volumes) {
								volume.setFlavorId(volumeFlavor.getUuid());
								volumeDao.update(volume);
							}
						}
						volumeFlavorDao.delete(flavor);
					}
				} else {
					volumeFlavor = new VolumeFlavor();
					volumeFlavor.setSize(entity.getSize());
					volumeFlavor.setDeleted(ConfigProperty.VOLUME_DELETED0_NORMAL);
					volumeFlavor.setDescription(entity.getDescription());
					volumeFlavor.createdUser(this.getLoginUser());
					volumeFlavor.setVolumeType(volumeType);
					String volumeTypeName = "1".equals(volumeType) ? "普通硬盘"
							: ("2".equals(volumeType) ? "固态硬盘" : "创建云主机的系统盘");
					volumeFlavor.setName(entity.getSize() + "*" + volumeTypeName);
					volumeFlavorDao.add(volumeFlavor);
				}
				String flavorId = volumeFlavor.getUuid();

				Volume volume = new Volume();
				InvokeSetForm.copyFormProperties(entity, volume);
				volume.setName(newName);
				volume.setStatus(ConfigProperty.VOLUME_STATE_CREATING);
				volume.setAttachStatus("1"); // 卸载状态
				volume.setDeleted(ConfigProperty.VOLUME_DELETED0_NORMAL); // 删除标志位置0
				volume.setFlavorId(flavorId);
				volume.createdUser(this.getLoginUser());
				volumeDao.add(volume);

				ids.append(volume.getId() + ",");
				names.append(volume.getName() + ",");

				// 更新配额
				quotaUsedBiz.change(ConfigProperty.VOLUME_QUOTA_CLASSCODE, projectId, true, 1);
				quotaUsedBiz.change(ConfigProperty.GIGABYTES_QUOTA_CLASSCODE, projectId, true, entity.getSize());

				vmDataMap.put("name", newName);
				Map<String, Object> vmParams = ResourceHandle.getParamMap(vmDataMap, "volume"); // 准备参数
				params.put("param", vmParams);
				params.put("time", entity.getLease());//租期时间
				taskBiz.save(volume.getId(), TaskTypeProperty.VOLUME_CREATE, params.toJSONString());
			}
			if (ids.length() > 0) {
				// 设置日志的资源id
				this.request.setAttribute("id", ids.deleteCharAt(ids.length() - 1).toString());
				// 设置日志的资源名称
				this.request.setAttribute("name", names.deleteCharAt(names.length() - 1).toString());
			}
			return;
		}
		throw new MessageException(ResultType.parameter_error);
	}

	public String tranState(String state) {
		String result = null;
		switch (state) {
		case ConfigProperty.VOLUME_STATE_CLOUDOS_CREATING:
			result = ConfigProperty.VOLUME_STATE_CREATING;
			break;

		case ConfigProperty.VOLUME_STATE_CLOUDOS_AVAILABLE:
			result = ConfigProperty.VOLUME_STATE_AVAILABLE;
			break;

		case ConfigProperty.VOLUME_STATE_CLOUDOS_ERROR:
			result = ConfigProperty.VOLUME_STATE_ERROR;
			break;

		case ConfigProperty.VOLUME_STATE_CLOUDOS_ATTACHED:
			result = ConfigProperty.VOLUME_STATE_ATTACHED;
			break;

		default:
			result = ConfigProperty.VOLUME_STATE_AVAILABLE;
			break;
		}
		return result;
	}

	@Override
	public void dettach(String id, String serverId) {
		CloudosClient client = this.getSessionBean().getCloudClient();
		// 设置日志的资源id
		this.request.setAttribute("id", id);
		Volume volume = this.findUsableVolumeById(id);
		if (null != volume) {
			// 设置日志的资源名称
			this.request.setAttribute("name", volume.getName());
			if (!volume.getStatus().equals(ConfigProperty.VOLUME_STATE_ATTACHED)) {
				throw new MessageException(ResultType.cloudos_id_not_exist);
			}
			String cloudosId = volume.getUuid();
			String tenantId = volume.getProjectId();
			if (!projectBiz.checkOptionRole(volume.getProjectId(), volume.getOwner2())) {
				throw new MessageException(ResultType.unAuthorized);
			}

			String userId = volume.getOwner2();
			User user = userDao.findById(User.class, userId);
			if (this.getSessionBean().getSuperUser() && !userId.equals(this.getLoginUser())) {
				client = CloudosClient.create(user.getCloudosId(), user.getLoginName());
			}
			if (null == client) {
				throw new MessageException(ResultType.system_error);
			}
			if (null == this.getVolumeJson(cloudosId, volume.getProjectId(), client)) {
				throw new MessageException(ResultType.volume_not_exist_in_cloudos);
			}
			NovaVm nv = novaVmBiz.findById(NovaVm.class, serverId);
			if (nv == null) { // 验证主机是否存在
				throw new MessageException(ResultType.novam_not_exist);
			}
			if (null == novaVmBiz.getVmJson(nv.getUuid(), nv.getProjectId(), client)) {
				throw new MessageException(ResultType.novavm_not_exist_in_cloudos);
			}

			String host = volume.getHost();
			if (null == host || "".equals(host)) {
				throw new MessageException(ResultType.volume_not_attach_host);
			}
			if (!host.equals(serverId)) { // 验证主机id是否一致(一块硬盘最多只能挂载一个主机,所以做此验证)
				throw new MessageException(ResultType.parameter_error);
			}

			String uri = CacheSingleton.getInstance().getCloudosApi(CloudosParams.CLOUDOS_API_VOLUMES_OFFATTACHMENTS);
			Map<String, String> tempMap = new HashMap<>();
			tempMap.put("tenant_id", tenantId);
			tempMap.put("server_id", nv.getUuid());
			tempMap.put("attachment_id", cloudosId);
			uri = HttpUtils.tranUrl(uri, tempMap);
			JSONObject params = new JSONObject();
			params.put("uri", uri);

			volume.updatedUser(this.getLoginUser());
			volume.setAttachStatus("1"); // 更新挂载状态
			volume.setStatus(ConfigProperty.VOLUME_STATE_DETACHING); // 更新运行状态
			volumeDao.update(volume);
			taskBiz.save(volume.getId(), TaskTypeProperty.VOLUME_DETTACH, JSONObject.toJSONString(params));
			return;
		}
		throw new MessageException(ResultType.deleted);
	}

	@Override
	public void attach(String id, String serverId) {
		// 设置日志的资源id
		this.request.setAttribute("id", id);
		CloudosClient client = this.getSessionBean().getCloudClient();
		Volume volume = this.findUsableVolumeById(id);
		if (null == volume) {
			throw new MessageException(ResultType.deleted);
		}
		// 设置日志的资源名称
		this.request.setAttribute("name", volume.getName());
		String tenantId = volume.getProjectId();
		if (!projectBiz.checkOptionRole(volume.getProjectId(), volume.getOwner2())) {
			throw new MessageException(ResultType.unAuthorized);
		}
		String status = volume.getStatus();
		if (!status.equals(ConfigProperty.VOLUME_STATE_AVAILABLE)) {
			throw new MessageException(ResultType.only_available_voluem_can_attach_host);
		}
		String userId = volume.getOwner2();
		User user = userDao.findById(User.class, userId);
		if (this.getSessionBean().getSuperUser() && !userId.equals(this.getLoginUser())) {
			client = CloudosClient.create(user.getCloudosId(), user.getLoginName());
		}
		if (null == client) {
			throw new MessageException(ResultType.system_error);
		}
		JSONObject volumeJson = this.getVolumeJson(volume.getUuid(), volume.getProjectId(), client);
		if (null == volumeJson) {
			throw new MessageException(ResultType.volume_not_exist_in_cloudos);
		}
		NovaVm nv = novaVmBiz.findById(NovaVm.class, serverId);
		if (nv == null) { // 验证主机是否存在
			throw new MessageException(ResultType.novam_not_exist);
		}
		if (!volume.getOwner2().equals(nv.getOwner())) {
			throw new MessageException(ResultType.user_inconformity);
		}
		String vmstate = nv.getVmState();
		if (!"state_normal".equals(vmstate) && !"state_stop".equals(vmstate)) {
			throw new MessageException(ResultType.only_can_attach_normal_or_stop_state_novavm);
		}
		if (null == novaVmBiz.getVmJson(nv.getUuid(), nv.getProjectId(), client)) {
			throw new MessageException(ResultType.novavm_not_exist_in_cloudos);
		}
		String host = volume.getHost();
		if (null != host && serverId.equals(host)) {
			throw new MessageException(ResultType.volume_already_attached_success);
		}
		if (null != host && !"".equals(host)) {
			throw new MessageException(ResultType.volume_already_attached_other_host);
		}

		String uri = HttpUtils.tranUrl(singleton.getCloudosApi(CloudosParams.CLOUDOS_API_OSVOLUMEATTACHMENTS), tenantId,
				nv.getUuid());
		Map<String, Object> dataMap = new HashMap<String, Object>();
		dataMap.put("volumeId", volume.getUuid());
		Map<String, Object> paramMap = ResourceHandle.getParamMap(dataMap, "volumeAttachment");
		JSONObject params = new JSONObject();
		params.put("uri", uri);
		params.put("param", paramMap);

		volume.updatedUser(this.getLoginUser());
		volume.setAttachStatus("0"); // 更新挂载状态
		volume.setStatus(ConfigProperty.VOLUME_STATE_ATTACHING); // 更新运行状态
		volume.setHost(serverId);
		volumeDao.update(volume);
		taskBiz.save(volume.getId(), TaskTypeProperty.VOLUME_ATTACH, JSONObject.toJSONString(params));
	}

	@Override
	public Object delete(String id) {
		String userId = this.getLoginUser();
		// 设置日志的资源id
		this.request.setAttribute("id", id);
		Volume volume = this.findUsableVolumeById(id);
		if (null != volume && "0".equals(volume.getDeleted())) {
			// 设置日志的资源名称
			this.request.setAttribute("name", volume.getName());
			if (!projectBiz.checkOptionRole(volume.getProjectId(), volume.getOwner2())) {
				return BaseRestControl.tranReturnValue(ResultType.unAuthorized);
			}

			if (null != volume.getHost() && !"".equals(volume.getHost())) { // 如果挂载到主机不能删除
				return BaseRestControl.tranReturnValue(ResultType.still_relate_host);
			}

			String status = volume.getStatus();
			if (status.equals(ConfigProperty.VOLUME_STATE_CREATING)
					|| status.equals(ConfigProperty.VOLUME_STATE_ATTACHING)
					|| status.equals(ConfigProperty.VOLUME_STATE_DETACHING)) {
				throw new MessageException(ResultType.volume_status_exception);
			}
			volume.setDeleted(ConfigProperty.VOLUME_DELETED1_RECYCLE); // 回收站状态
			volume.setDeleteBy(userId);
			volume.updatedUser(userId);
			volumeDao.update(volume);

			// 放入回收站
			recycleItemsBiz.recycleResourceByUser(volume.getId(), ConfigProperty.TEMPLATES_CLASS2_DISK);
			return BaseRestControl.tranReturnValue(ResultType.success);
		}
		return BaseRestControl.tranReturnValue(ResultType.deleted);
	}

	/**
	 * 通过主键和删除标志位查找硬盘
	 */
	private Volume findUsableVolumeById(String id) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("id", id);
		map.put("deleted", "0");
		List<Volume> vms = volumeDao.findByMap(Volume.class, map);
		if (null != vms && vms.size() > 0) {
			return vms.get(0);
		}
		return null;
	}

	public void deleteFromRecycle(Volume volume) {
		RecycleItems item = recycleItemsBiz.singleByClass(RecycleItems.class,
				StrUtils.createMap("resId", volume.getId()));
		if (StrUtils.checkParam(item)) {
			recycleItemsBiz.delete(item);
		}
		volumeDao.delete(volume);
		quotaUsedBiz.change(ConfigProperty.VOLUME_QUOTA_CLASSCODE, volume.getProjectId(), false, 1);
		quotaUsedBiz.change(ConfigProperty.GIGABYTES_QUOTA_CLASSCODE, volume.getProjectId(), false, volume.getSize());
		measureDetailBiz.stop(volume.getId(), this.getLoginUser(), true);
	}

	public void recoverFromRecycle(Volume volume) {
		String user = this.getLoginUser();
		volume.setDeleted(ConfigProperty.VOLUME_DELETED0_NORMAL);
		volume.setDeleteBy(null);
		volume.updatedUser(user);

		String flavorId = volume.getFlavorId();
		if (null != flavorId && !"".equals(flavorId)) {
			VolumeFlavor flavor = volumeFlavorDao.findById(VolumeFlavor.class, volume.getFlavorId());
			flavor.setDeleted(ConfigProperty.VOLUME_DELETED0_NORMAL);
			flavor.setDeleteBy(null);
			flavor.updatedUser(user);
			volumeFlavorDao.update(flavor);
		} else {
			VolumeFlavor flavor = new VolumeFlavor();
			flavor.setSize(volume.getSize());
			flavor.setDeleted(ConfigProperty.VOLUME_DELETED0_NORMAL);
			flavor.createdUser(user);
			flavor.setVolumeType(volume.getVolumeType());
			flavor.updatedUser(user);
			String newFid = volumeFlavorDao.add(flavor);
			volume.setFlavorId(newFid);
		}

		volumeDao.update(volume);
	}

	/**
	 * 检查基础资源是否可用
	 *
	 * @param baseUrl
	 * @param id
	 * @return
	 */
	private boolean checkBaseResource(String baseUrl, String id, CloudosClient client) {
		baseUrl = HttpUtils.tranUrl(baseUrl, id);
		JSONObject response = client.get(baseUrl);
		if (ResourceHandle.judgeResponse(response))
			return true;
		else
			return false;
	}

	@Override
	public List<Volume> findVolume(NovaVm novaVm) {
		return volumeDao.findVolume(novaVm);
	}

	@Override
	public int monthCount() {
		return volumeDao.monthCount();
	}

	public JSONObject getVolumeJson(String uuid, String projectId, CloudosClient client) {
		if (null == uuid) {
			return null;
		}
		String uri = HttpUtils.tranUrl(singleton.getCloudosApi(CloudosParams.CLOUDOS_API_VOLUME_ACTION), projectId,
				uuid);
		JSONObject json = HttpUtils.getJson(uri, "volume", client);
		return json;
	}


}

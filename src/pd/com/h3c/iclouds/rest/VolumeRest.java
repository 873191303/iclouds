package com.h3c.iclouds.rest;

import com.alibaba.fastjson.JSONObject;
import com.h3c.iclouds.base.BaseRest;
import com.h3c.iclouds.base.BaseRestControl;
import com.h3c.iclouds.biz.QuotaUsedBiz;
import com.h3c.iclouds.biz.RenewalBiz;
import com.h3c.iclouds.biz.UserBiz;
import com.h3c.iclouds.biz.VolumeBiz;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageList;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.dao.RenewalDao;
import com.h3c.iclouds.dao.VolumeDao;
import com.h3c.iclouds.exception.MessageException;
import com.h3c.iclouds.operate.CloudosClient;
import com.h3c.iclouds.po.Renewal;
import com.h3c.iclouds.po.User;
import com.h3c.iclouds.po.Volume;
import com.h3c.iclouds.utils.LogUtils;
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
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(value = "云硬盘操作")
@RestController
@RequestMapping("/volume")
public class VolumeRest extends BaseRest<Volume> {

	@Resource
	private VolumeDao volumeDao;

	@Resource
	private VolumeBiz volumeBiz;

	@Resource
	private UserBiz userBiz;

	@Resource
	private QuotaUsedBiz quotaUsedBiz;
	
	@Resource
	private RenewalBiz renewalBiz;

	@Override
	@ApiOperation(value = "获取云硬盘列表", response = Volume.class)
	@RequestMapping(method = RequestMethod.GET)
	public Object list() {
		boolean isAdmin = this.getSessionBean().getSuperUser();
		List<Volume> volumes;
		if (isAdmin) {
			volumes = volumeDao.findByPropertyName(Volume.class, "deleted", "0");
		} else {
			boolean isSuperRole = this.getSessionBean().getSuperRole();
			Map<String, String> map = new HashMap<String, String>();
			if (isSuperRole) {
				String projectId = this.getProjectId();
				map.put("projectId", projectId);
			} else {
				String user = this.getLoginUser();
				map.put("owner2", user);
			}
			map.put("deleted", "0");
			volumes = volumeDao.findByMap(Volume.class, map);
		}
		return BaseRestControl.tranReturnValue(volumes);
	}
	//云硬盘扩容 2018-09-01
	@RequestMapping(value = "/{id}/capacity", method = RequestMethod.POST)
	@ApiOperation(value = "云硬盘扩容")
	public Object Capacity(@PathVariable("id") String id, @RequestBody JSONObject objStr) {
		// 设置日志的资源id
		this.request.setAttribute("id", id);

		try {
			String capacity = objStr.get("capacity").toString();//扩容容量
			// 在这里进行业务处理
			//String userId = this.getLoginUser();

			Volume volume = volumeBiz.findById(Volume.class, id);
			if (null != volume) {
				if (!"0".equals(volume.getDeleted())) {
					return BaseRestControl.tranReturnValue(ResultType.deleted);
				}
			}
			// 返回提示信息
			ResultType rss = volumeBiz.Capacity(volume,capacity);
			return BaseRestControl.tranReturnValue(rss);

		} catch (Exception e) {
			LogUtils.exception(this.getClass(), e);
			if (e instanceof MessageException) {
				return BaseRestControl.tranReturnValue(((MessageException) e).getResultCode());
			}
			return BaseRestControl.tranReturnValue(ResultType.data_error);
		}

	}
	

	// 云硬盘续租 wxg 2018-09-01
	@RequestMapping(value = "/{id}/renewal ", method = RequestMethod.POST)
	@ApiOperation(value = "云硬盘续租")
	public Object Renewal(@PathVariable("id") String id, @RequestBody JSONObject objStr) {
		// 设置日志的资源id
		this.request.setAttribute("id", id);

		try {
			String time = objStr.get("time").toString(); 
			ResultType rss = renewalBiz.toReanewal(time, id, "yyp"); //novaVmBiz.Renewal(ren, userId, time);
			return BaseRestControl.tranReturnValue(rss);

		} catch (Exception e) {
			LogUtils.exception(this.getClass(), e);
			if (e instanceof MessageException) {
				return BaseRestControl.tranReturnValue(((MessageException) e).getResultCode());
			}
			return BaseRestControl.tranReturnValue(ResultType.failure);
		}

	}

	@ApiOperation(value = "获取云硬盘分页列表", response = Volume.class)
	@RequestMapping(value = "/pageList", method = RequestMethod.GET)
	public Object pageList() {
		PageEntity entity = this.beforeList();
		PageModel<Volume> pageModel = volumeBiz.findForPage(entity);
		PageList<Volume> page = new PageList<>(pageModel, entity.getsEcho());
		return BaseRestControl.tranReturnValue(page);
	}

	@Override
	@ApiOperation(value = "获取云硬盘详细信息", response = Volume.class)
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public Object get(@PathVariable String id) {
		Volume volume = volumeBiz.findById(Volume.class, id);
		if (null != volume) {
			if (!"0".equals(volume.getDeleted())) {
				return BaseRestControl.tranReturnValue(ResultType.deleted);
			}
			return BaseRestControl.tranReturnValue(volume);
		}
		return BaseRestControl.tranReturnValue(ResultType.not_exist);
	}

	@Override
	@RequestMapping(value = "/create", method = RequestMethod.POST)
	@ApiOperation(value = "创建云硬盘")
	public Object save(@RequestBody Volume entity) {
		if (!this.getSessionBean().getSelfAllowed()) {
			return BaseRestControl.tranReturnValue(ResultType.project_cannot_create_resource);
		}
		entity.setProjectId(this.getProjectId());
		return save(entity, null, null);
	}

	@RequestMapping(value = "/admin/create/{userId}", method = RequestMethod.POST)
	@ApiOperation(value = "创建云硬盘")
	public Object save(@PathVariable String userId, @RequestBody Volume entity) {
		User user = userBiz.findById(User.class, userId);
		Map<String, Object> check = this.adminSave(entity.getProjectId(), user);
		if (null != check) {
			return check;
		}
		entity.setProjectId(user.getProjectId());
		return save(entity, user.getId(), user.getLoginName());
	}

	@Override
	@ApiOperation(value = "修改云硬盘数据，id云硬盤id", response = Volume.class)
	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	public Object update(@PathVariable String id, @RequestBody Volume entity) throws IOException {
		try {
			Volume vm = volumeBiz.update(id, entity);
			this.warn("Update volume:" + id + ". Parameter: [" + StrUtils.toJSONString(entity) + "].");
			return BaseRestControl.tranReturnValue(ResultType.success, vm);
		} catch (Exception e) {
			this.exception(this.getClass(), e);
			if (e instanceof MessageException) {
				return BaseRestControl.tranReturnValue(((MessageException) e).getResultCode());
			}
			return BaseRestControl.tranReturnValue(ResultType.failure);
		}
	}

	@RequestMapping(value = "/dettach/{id}/{server_id}", method = RequestMethod.POST)
	@ApiOperation(value = "从指定主机卸载云硬盘,id云硬盘id，server_id云主机id")
	public Object offAttach(@PathVariable String id, @PathVariable String server_id) {
		try {
			volumeBiz.dettach(id, server_id);
			StringBuffer buffer = new StringBuffer();
			buffer.append("Host dettach volume. ");
			buffer.append("Host:" + server_id + ", ");
			buffer.append("volume:" + id + ".");
			this.warn(buffer.toString());
			return BaseRestControl.tranReturnValue(ResultType.success);
		} catch (Exception e) {
			this.exception(this.getClass(), e);
			if (e instanceof MessageException) {
				return BaseRestControl.tranReturnValue(((MessageException) e).getResultCode());
			}
			return BaseRestControl.tranReturnValue(ResultType.failure);
		}
	}

	@RequestMapping(value = "/attach/{id}/{server_id}", method = RequestMethod.POST)
	@ApiOperation(value = "云硬盘挂载到主机")
	public Object loadVolume(@PathVariable String id, @PathVariable String server_id) {
		try {
			volumeBiz.attach(id, server_id);
			StringBuffer buffer = new StringBuffer();
			buffer.append("Host attach volume. ");
			buffer.append("Host:" + server_id + ", ");
			buffer.append("volume:" + id + ".");
			this.warn(buffer.toString());
			return BaseRestControl.tranReturnValue(ResultType.success);
		} catch (Exception e) {
			this.exception(this.getClass(), e);
			if (e instanceof MessageException) {
				return BaseRestControl.tranReturnValue(((MessageException) e).getResultCode());
			}
			return BaseRestControl.tranReturnValue(ResultType.failure);
		}
	}

	@Override
	@ApiOperation(value = "删除云硬盘")
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public Object delete(@PathVariable String id) {
		try {
			Object object = volumeBiz.delete(id);
			this.warn("Delete volume:" + id);
			return object;
		} catch (Exception e) {
			this.exception(this.getClass(), e);
			return BaseRestControl.tranReturnValue(ResultType.failure);
		}
	}

	@ApiOperation(value = "检查配额(count-数量; size-单片硬盘容量，单位G)")
	@RequestMapping(value = "/checkQuota", method = RequestMethod.POST)
	public Object checkQuota(@RequestBody JSONObject entity) {
		try {
			int countInt = StrUtils.tranInteger(entity.get("count"));
			int sizeInt = StrUtils.tranInteger(entity.get("size"));
			boolean isValid = StrUtils.checkParam(countInt, sizeInt);
			if (!isValid) {
				return BaseRestControl.tranReturnValue(ResultType.parameter_error);
			}
			ResultType rs = quotaUsedBiz.checkQuota(ConfigProperty.VOLUME_QUOTA_CLASSCODE, this.getProjectId(),
					countInt);
			if (!ResultType.success.equals(rs)) {
				return BaseRestControl.tranReturnValue(rs, "volume");
			}
			rs = quotaUsedBiz.checkQuota(ConfigProperty.GIGABYTES_QUOTA_CLASSCODE, this.getProjectId(),
					sizeInt * countInt);
			if (!ResultType.success.equals(rs)) {
				return BaseRestControl.tranReturnValue(rs, "gigabytes");
			}
			return BaseRestControl.tranReturnValue(ResultType.success);
		} catch (Exception e) {
			return BaseRestControl.tranReturnValue(ResultType.failure);
		}
	}

	@ApiOperation(value = "获取云硬盘数量与本月新增数量")
	@RequestMapping(value = "/count", method = RequestMethod.GET)
	public Object count() {
		Map<String, Integer> countMap = new HashMap<>();
		int totalCount = volumeBiz.count(Volume.class, new HashMap<>());
		countMap.put("totalCount", totalCount);
		int monthCount = volumeBiz.monthCount();
		countMap.put("monthCount", monthCount);
		return BaseRestControl.tranReturnValue(countMap);
	}

	@ApiOperation(value = "我的云硬盘")
	@RequestMapping(value = "/{id}/count", method = RequestMethod.GET)
	public Object countByUser(@PathVariable String id) {
		List<Volume> volumes = null;
		if (this.getSessionBean().getSuperUser()) {
			volumes = volumeBiz.findByPropertyName(Volume.class);
		} else if (this.getSessionBean().getSuperRole()) {
			volumes = volumeBiz.findByPropertyName(Volume.class, "projectId", this.getProjectId());
		} else {
			volumes = volumeBiz.findByPropertyName(Volume.class, "owner2", id);
		}
		int count = 0;
		int totalSize = 0;

		if (StrUtils.checkParam(volumes)) {
			for (Volume volume : volumes) {
				count++;
				totalSize += volume.getSize();
			}
		}
		Map<String, Integer> map = new HashMap<>();
		map.put("count", count);
		map.put("totalSize", totalSize);
		return BaseRestControl.tranReturnValue(map);
	}

	@ApiOperation(value = "检查名称是否重复")
	@RequestMapping(value = "/check/{name}", method = RequestMethod.GET)
	public Object checkRepeat(@PathVariable String name) {
		boolean repeat = false;
		String id = request.getParameter("id");// 修改时传入一个id则查重时会排除对象本身
		try {
			Map<String, Object> checkMap = new HashMap<>();
			checkMap.put("name", name);
			repeat = volumeBiz.checkRepeat(Volume.class, checkMap, id);
			if (!repeat) {// 查重(名称)
				return BaseRestControl.tranReturnValue(ResultType.repeat);
			}
			return BaseRestControl.tranReturnValue(ResultType.success);
		} catch (Exception e) {
			return BaseRestControl.tranReturnValue(ResultType.failure);
		}
	}

	private Object save(Volume entity, String userId, String userName) {
		CloudosClient client;
		if (StrUtils.checkParam(userId)) {
			User user = userBiz.findById(User.class, userId);
			client = CloudosClient.create(user.getCloudosId(), userName);
		} else {
			userId = this.getLoginUser();
			client = this.getSessionBean().getCloudClient();
		}
		entity.setOwner2(userId);
		if (null == client) {
			return BaseRestControl.tranReturnValue(ResultType.system_error);
		}
		Map<String, String> validatorMap = ValidatorUtils.validator(entity);
		if (!validatorMap.isEmpty()) {
			return BaseRestControl.tranReturnValue(ResultType.parameter_error, validatorMap);
		}
		int count = StrUtils.tranInteger(entity.getCount());
		count = (count <= 0) ? 1 : count; // 默认创建一个硬盘
		ResultType rs = quotaUsedBiz.checkQuota(ConfigProperty.VOLUME_QUOTA_CLASSCODE, entity.getProjectId(), 1);
		if (!ResultType.success.equals(rs)) {
			return BaseRestControl.tranReturnValue(rs, "volume");
		}
		rs = quotaUsedBiz.checkQuota(ConfigProperty.GIGABYTES_QUOTA_CLASSCODE, entity.getProjectId(),
				entity.getSize() * count);
		if (!ResultType.success.equals(rs)) {
			return BaseRestControl.tranReturnValue(rs, "gigabytes");
		}

		try {
			volumeBiz.save(entity, userId, client);
			this.warn("Save volume. Parameter: [" + JSONObject.toJSONString(entity) + "].");
			return BaseRestControl.tranReturnValue(ResultType.success);
		} catch (Exception e) {
			if (e instanceof MessageException) {
				return BaseRestControl.tranReturnValue(((MessageException) e).getResultCode());
			}
			this.exception(this.getClass(), e);
			return BaseRestControl.tranReturnValue(ResultType.failure);
		}
	}

}

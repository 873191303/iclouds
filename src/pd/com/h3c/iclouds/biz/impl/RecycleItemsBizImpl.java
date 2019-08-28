package com.h3c.iclouds.biz.impl;

import com.alibaba.fastjson.JSONObject;
import com.h3c.iclouds.base.BaseBizImpl;
import com.h3c.iclouds.biz.ListPriceBiz;
import com.h3c.iclouds.biz.MeasureDetailBiz;
import com.h3c.iclouds.biz.NovaVmBiz;
import com.h3c.iclouds.biz.ProjectBiz;
import com.h3c.iclouds.biz.RecycleItemsBiz;
import com.h3c.iclouds.biz.TaskBiz;
import com.h3c.iclouds.biz.UserBiz;
import com.h3c.iclouds.biz.VolumeBiz;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.common.TaskTypeProperty;
import com.h3c.iclouds.dao.RecycleItemsDao;
import com.h3c.iclouds.dao.VolumeFlavorDao;
import com.h3c.iclouds.exception.MessageException;
import com.h3c.iclouds.operate.CloudosClient;
import com.h3c.iclouds.operate.CloudosParams;
import com.h3c.iclouds.po.NovaVm;
import com.h3c.iclouds.po.RecycleItems;
import com.h3c.iclouds.po.Task;
import com.h3c.iclouds.po.User;
import com.h3c.iclouds.po.Volume;
import com.h3c.iclouds.utils.HttpUtils;
import com.h3c.iclouds.utils.StrUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("recycleItemsBiz")
public class RecycleItemsBizImpl extends BaseBizImpl<RecycleItems> implements RecycleItemsBiz {
	
	@Resource
	private NovaVmBiz novaVmBiz;
	
	@Resource
	private UserBiz userBiz;
	
	@Resource
	private RecycleItemsDao recycleItemsDao;
	
	@Resource
	private TaskBiz taskBiz;
	
	@Resource
	private VolumeBiz volumeBiz;
	
	@Resource
	private ProjectBiz projectBiz;
	
	@Resource
	private MeasureDetailBiz measureDetailBiz;
	
	@Resource
	private ListPriceBiz listPriceBiz;
	
	@Resource
	private VolumeFlavorDao volumeFlavorDao;
	
	@Override
	public void delete (String id) {
		// 设置日志的资源id
		this.request.setAttribute("id", id);
		// 删除云主机列表数据
		NovaVm novaVm = novaVmBiz.findById(NovaVm.class, id);
		if (!StrUtils.checkParam(novaVm)) {
			throw new MessageException(ResultType.novam_not_exist);
		}
		// 设置日志的资源名称
		this.request.setAttribute("name", novaVm.getHostName());
		
		ResultType resultCheck = checkAuth(novaVm);
		if (resultCheck != ResultType.success) {
			throw new MessageException(resultCheck);
		}
		//如果云主机已经正则删除则直接返回
		if ("state_deleting".equals(novaVm.getVmState())) {
			return;
		}
		
		String uri = singleton.getCloudosApi(CloudosParams.CLOUDOS_API_SERVER_DELETE);
		String uuid = novaVm.getUuid();
		String busId = novaVm.getId();
		if (StrUtils.checkParam(uuid)) {
			uri = HttpUtils.tranUrl(uri, novaVm.getProjectId(), uuid);
		} else {//云主机没有uuid
			if (!novaVm.getVmState().equals(ConfigProperty.novaVmState.get(8))) {//状态不为开机中则直接在本地删除
				User user = userBiz.findById(User.class, novaVm.getOwner());
				CloudosClient cloudosClient = CloudosClient.create(user.getCloudosId(), user.getLoginName());
				novaVmBiz.deleteNovaVm(novaVm, busId, cloudosClient);
				return;
			}
		}
		novaVm.setVmState(ConfigProperty.novaVmState.get(10));// 删除中
		novaVmBiz.update(novaVm);
		Map<String, Object> server = new HashMap<>();
		server.put("uri", uri);
		server.put("params", null);
		String input = new JSONObject(server).toJSONString();
		List<Task> tasks = taskBiz.findByPropertyName(Task.class, "busId", novaVm.getId());
		if (StrUtils.checkCollection(tasks)) {
			for (int i = 0; i < tasks.size(); i++) {
				Task task = tasks.get(i);
				if (task.getBusType().equals(TaskTypeProperty.VM_WAIT_DELETE)) {
					return;
				}
				server.put("taskId" + i, tasks.get(i).getId());
			}
			server.put("taskNum", tasks.size());
			input = new JSONObject(server).toJSONString();
			taskBiz.save(busId, TaskTypeProperty.VM_WAIT_DELETE, input);
			return;
		}
		String busType = TaskTypeProperty.VM_DELETE;
		taskBiz.save(busId, busType, input);
	}
	
	@Override
	public Object recovery (RecycleItems recycleItem) {
		// 设置回收的时间，云主机列表查询时，这个为空
		// 删除回收站记录
		NovaVm novaVm = novaVmBiz.findById(NovaVm.class, recycleItem.getResId());
		// 设置日志的资源id
		this.request.setAttribute("id", recycleItem.getResId());
		// 设置日志的资源名称
		this.request.setAttribute("name", novaVm.getHostName());
		// 删除中不准还原
		if (StrUtils.checkParam(novaVm)) {
			ResultType resultCheck = checkAuth(novaVm);
			if (resultCheck != ResultType.success) {
				throw new MessageException(resultCheck);
			}
			if (novaVm.getVmState().equals("state_deleting")) {
				throw new MessageException(ResultType.novavm_deleting_not_recovery);
			}
		}
		recycleItem.setRecycleTime(new Date());
		// 还原设置为0
		recycleItem.setRecycleAction("0");
		recycleItem.updatedUser(getLoginUser());
		recycleItemsDao.update(recycleItem);
		return ResultType.success;
	}
	
	@Override
	public PageModel<Map<String, Object>> findCompleteForPage (PageEntity entity) {
		String specialParam = entity.getSpecialParam();
		if (StrUtils.checkParam(specialParam)) {
			switch (specialParam) {
				case "1":
					return recycleItemsDao.findCompleteForPage(entity);
				case "2":
					return recycleItemsDao.findVolumeCompleteForPage(entity);
			}
		}
		return recycleItemsDao.findCompleteForPage(entity);
		
	}
	
	@Override
	public void work (RecycleItems recycleItem) {
		// TODO Auto-generated method stub
		
	}
	
	public void recycleResourceByUser (String resourceId, String resourceClassId) {
		RecycleItems item = new RecycleItems();
		item.createdUser(getLoginUser());
		item.setResId(resourceId);
		item.setClassId(resourceClassId);
		item.setRecycleType("0");
		item.setInboundTime(new Date());
		item.setRecycleAction("用户删除动作");
		item.setRecycleTime(null);// 当返原的时候设置
		recycleItemsDao.add(item);
	}
	
	@Override
	public void deleteVolume (String id) {
		// 设置日志的资源id
		this.request.setAttribute("id", id);
		List<RecycleItems> items = recycleItemsDao.findByPropertyName(RecycleItems.class, "resId", id);
		if (null == items || items.size() == 0) {
			throw new MessageException(ResultType.volume_not_in_recycle);
		}
		Volume volume = volumeBiz.findById(Volume.class, id);
		if (null == volume) {
			throw new MessageException(ResultType.res_not_exist);
		}
		if (!projectBiz.checkOptionRole(volume.getProjectId(), volume.getOwner2())) {
			throw new MessageException(ResultType.unAuthorized);
		}
		// 设置日志的资源名称
		this.request.setAttribute("name", volume.getName());
		if (!StrUtils.checkParam(volume.getUuid())) {
			volumeBiz.deleteFromRecycle(volume);
		} else {
			volume.setStatus(ConfigProperty.VOLUME_STATE_DELETING);
			volumeBiz.update(volume);
			String uri = HttpUtils.tranUrl(singleton.getCloudosApi(CloudosParams.CLOUDOS_API_VOLUME_ACTION),
					this.getProjectId(), volume.getUuid());
			JSONObject params = new JSONObject();
			params.put("uri", uri);
			taskBiz.save(volume.getId(), TaskTypeProperty.VOLUME_DELETE, JSONObject.toJSONString(params));
		}
	}
	
	@Override
	public void recoverVolume (String id) {
		// 设置日志的资源id
		this.request.setAttribute("id", id);
		List<RecycleItems> items = recycleItemsDao.findByPropertyName(RecycleItems.class, "resId", id);
		if (null == items || items.size() == 0) {
			throw new MessageException(ResultType.volume_not_in_recycle);
		}
		Volume volume = volumeBiz.findById(Volume.class, id);
		if (null == volume) {
			throw new MessageException(ResultType.res_not_exist);
		}
		if (!projectBiz.checkOptionRole(volume.getProjectId(), volume.getOwner2())) {
			throw new MessageException(ResultType.unAuthorized);
		}
		// 设置日志的资源名称
		this.request.setAttribute("name", volume.getName());
		recycleItemsDao.delete(items.get(0));
		volumeBiz.recoverFromRecycle(volume);
	}
	
	/**
	 * 验证还原和彻底删除的租户权限
	 */
	private ResultType checkAuth (NovaVm novaVm) {
		if (!getSessionBean().getSuperUser() && !getProjectId().equals(novaVm.getProjectId())) {
			return ResultType.unAuthorized;
		}
		return ResultType.success;
	}
}
package com.h3c.iclouds.task.Volume;

import com.alibaba.fastjson.JSONObject;
import com.h3c.iclouds.base.SpringContextHolder;
import com.h3c.iclouds.biz.ListPriceBiz;
import com.h3c.iclouds.biz.MeasureDetailBiz;
import com.h3c.iclouds.biz.RenewalBiz;
import com.h3c.iclouds.biz.UserBiz;
import com.h3c.iclouds.biz.VolumeBiz;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.dao.VolumeFlavorDao;
import com.h3c.iclouds.operate.CloudosClient;
import com.h3c.iclouds.po.Task;
import com.h3c.iclouds.po.User;
import com.h3c.iclouds.po.Volume;
import com.h3c.iclouds.po.VolumeFlavor;
import com.h3c.iclouds.po.business.FlavorParam;
import com.h3c.iclouds.po.business.MeasureDetail;
import com.h3c.iclouds.task.BaseTask;
import com.h3c.iclouds.utils.LogUtils;
import com.h3c.iclouds.utils.ResourceHandle;
import java.util.concurrent.TimeUnit;
import javax.annotation.Resource;

/**
 * Created by yKF7317 on 2017/6/19.
 */
public class VolumeCreateTask extends BaseTask {
	
	final static int second = 5;
	
	private VolumeBiz volumeBiz = SpringContextHolder.getBean("volumeBiz");
	
	private MeasureDetailBiz measureDetailBiz = SpringContextHolder.getBean("measureDetailBiz");
	
	private VolumeFlavorDao volumeFlavorDao = SpringContextHolder.getBean("volumeFlavorDao");
	
	private ListPriceBiz listPriceBiz = SpringContextHolder.getBean("listPriceBiz");
	
	private UserBiz userBiz = SpringContextHolder.getBean("userBiz");
	
	// 租期过期
	private RenewalBiz renewalBiz = SpringContextHolder.getBean("renewalBiz");
	
	private Integer create_query_num = 100;
	// 租期时间
	private String time = "";
	
	public VolumeCreateTask (Task task) {
		super(task);
	}
	
	@Override
	protected String handle () {
		String id = task.getBusId();
		Volume volume = volumeBiz.findById(Volume.class, id);
		if (null == volume) {
			this.saveTask2Exec("未查询到云硬盘[" + id + "]", ConfigProperty.TASK_STATUS4_END_FAILURE, 0);
			return ConfigProperty.TASK_STATUS4_END_FAILURE;
		}
		JSONObject json = JSONObject.parseObject(task.getInput());
		String url = json.getString("uri");
		JSONObject params = json.getJSONObject("param");
		time = json.getString("time");
		String userId = volume.getOwner2();
		User user = userBiz.findById(User.class, userId);
		CloudosClient cloudosClient = CloudosClient.create(user.getCloudosId(), user.getLoginName());
		for (int i = 0; i < retryNum; i++) {
			try {
				boolean flag = false;
				JSONObject jsonObject = cloudosClient.post(url, params);
				if (!ResourceHandle.judgeResponse(jsonObject)) { // synchronize state
					continue;
				} else {
					jsonObject = jsonObject.getJSONObject("record").getJSONObject("volume");
					String uuid = jsonObject.getString("id");
					String metadata = jsonObject.getString("metadata");
					String status = jsonObject.getString("status");
					if (ConfigProperty.VOLUME_STATE_CLOUDOS_AVAILABLE.equals(status) || ConfigProperty.VOLUME_STATE_CLOUDOS_ERROR.equals(status)) {
						flag = true;
					} else {
						for (int j = 0; j < create_query_num; j++) {
							JSONObject volumeJson = volumeBiz.getVolumeJson(uuid, volume.getProjectId(), cloudosClient);
							if (null == volumeJson) { // synchronize state
								volume.setStatus(ConfigProperty.VOLUME_STATE_CREATE_EXCEPTION);
								volumeBiz.update(volume);
								saveTask2Exec(ConfigProperty.VOLUME_STATE_CREATE_EXCEPTION, ConfigProperty.TASK_STATUS4_END_FAILURE, i);
								return ConfigProperty.TASK_STATUS4_END_FAILURE;
							}
							status = volumeJson.getString("status");
							if (ConfigProperty.VOLUME_STATE_CLOUDOS_AVAILABLE.equals(status) || ConfigProperty
									.VOLUME_STATE_CLOUDOS_ERROR.equals(status)) {
								flag = true;
								break;
							}
							TimeUnit.SECONDS.sleep(second);
							continue;
						}
					}
					if (flag) {
						status = volumeBiz.tranState(status);
						volume.setStatus(status);
						volume.setMetaData(metadata);
						volume.setUuid(uuid);
						volumeBiz.update(volume);
						
						//创建账单
						try {
							String specId = listPriceBiz.saveByVolumeFlavor(volumeFlavorDao.findById(VolumeFlavor.class, volume.getFlavorId()), volume.getCreatedBy(), volume.getAzoneId()).getId();
							FlavorParam flavorParam = new FlavorParam();
							flavorParam.setClassId("2");
							flavorParam.setSpecId(specId);
							flavorParam.setValue(volume.getSize());
							flavorParam.setAzoneId(volume.getAzoneId());
							flavorParam.setUserId(volume.getOwner2());
							flavorParam.setTenantId(volume.getProjectId());
							flavorParam.setResourceId(volume.getId());
							flavorParam.setCreatedBy(volume.getCreatedBy());
							measureDetailBiz.save(flavorParam, "创建");
						} catch (Exception e) {
							LogUtils.exception(MeasureDetail.class, e, "账单创建失败");
						}
						
						saveTask2Exec("success",ConfigProperty.TASK_STATUS3_END_SUCCESS, i);
						return ConfigProperty.TASK_STATUS3_END_SUCCESS;
					}
				}
			} catch (Exception e) { // 同步异常
				volume.setStatus(ConfigProperty.VOLUME_STATE_CREATE_EXCEPTION);
				volumeBiz.update(volume);
				saveTask2Exec(e.getMessage(),ConfigProperty.TASK_STATUS4_END_FAILURE, i);
				return ConfigProperty.TASK_STATUS4_END_FAILURE;
			}
			//添加finally 总会执行
			finally {
				renewalBiz.addRenewal(renewalBiz, userBiz, cloudosClient, "1c13c9ba-3ee3-4547-9d0d-79a83447cdd0",
						time, volume.getProjectId(), volume.getUuid(), volume.getName());
			}
		}
		volume.setStatus(ConfigProperty.VOLUME_STATE_CREATE_EXCEPTION);
		volumeBiz.update(volume);
		saveTask2Exec("failure",ConfigProperty.TASK_STATUS4_END_FAILURE, retryNum);
		return ConfigProperty.TASK_STATUS4_END_FAILURE;
	}
}

package com.h3c.iclouds.task.Volume;

import com.alibaba.fastjson.JSONObject;
import com.h3c.iclouds.base.SpringContextHolder;
import com.h3c.iclouds.biz.UserBiz;
import com.h3c.iclouds.biz.VolumeBiz;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.operate.CloudosClient;
import com.h3c.iclouds.po.Task;
import com.h3c.iclouds.po.User;
import com.h3c.iclouds.po.Volume;
import com.h3c.iclouds.task.BaseTask;
import com.h3c.iclouds.utils.ResourceHandle;

import java.util.concurrent.TimeUnit;

/**
 * Created by yKF7317 on 2017/6/19.
 */
public class VolumeDettachTask extends BaseTask {
	
	final static int second = 5;
	
	private VolumeBiz volumeBiz = SpringContextHolder.getBean("volumeBiz");
	
	private UserBiz userBiz = SpringContextHolder.getBean("userBiz");
	
	private Integer create_query_num = 100;
	
	public VolumeDettachTask (Task task) {
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
		String userId = volume.getOwner2();
		User user = userBiz.findById(User.class, userId);
		CloudosClient cloudosClient = CloudosClient.create(user.getCloudosId(), user.getLoginName());
		for (int i = 0; i < retryNum; i++) {
			try {
				boolean flag = false;
				JSONObject jsonObject = cloudosClient.delete(url);
				if (!ResourceHandle.judgeResponse(jsonObject)) {
					continue;
				} else {
					for (int j = 0; j < create_query_num; j++) {
						jsonObject = volumeBiz.getVolumeJson(volume.getUuid(), volume.getProjectId(), cloudosClient);
						if (null == jsonObject) {
							volume.setStatus(ConfigProperty.VOLUME_STATE_DETTACH_FAILURE);
							volumeBiz.update(volume);
							saveTask2Exec(ConfigProperty.VOLUME_STATE_DETTACH_FAILURE, ConfigProperty.TASK_STATUS4_END_FAILURE, i);
							return ConfigProperty.TASK_STATUS4_END_FAILURE;
						}
						String status = jsonObject.getString("status");
						if (ConfigProperty.VOLUME_STATE_CLOUDOS_AVAILABLE.equals(status) || ConfigProperty.VOLUME_STATE_CLOUDOS_ERROR.equals(status)) {
							flag = true;
							status = volumeBiz.tranState(status);
							volume.setStatus(status);
							break;
						}
						TimeUnit.SECONDS.sleep(second);
						continue;
					}
					if (flag) {
						volume.setHost(null);
						volumeBiz.update(volume);
						saveTask2Exec("success",ConfigProperty.TASK_STATUS3_END_SUCCESS, i);
						return ConfigProperty.TASK_STATUS3_END_SUCCESS;
					}
				}
			} catch (Exception e) { // 同步异常
				volume.setStatus(ConfigProperty.VOLUME_STATE_DETTACH_EXCEPTION);
				volumeBiz.update(volume);
				saveTask2Exec(e.getMessage(),ConfigProperty.TASK_STATUS4_END_FAILURE, i);
				return ConfigProperty.TASK_STATUS4_END_FAILURE;
			}
		}
		volume.setStatus(ConfigProperty.VOLUME_STATE_DETTACH_FAILURE);
		volumeBiz.update(volume);
		saveTask2Exec("failure",ConfigProperty.TASK_STATUS4_END_FAILURE, retryNum);
		return ConfigProperty.TASK_STATUS4_END_FAILURE;
	}
}

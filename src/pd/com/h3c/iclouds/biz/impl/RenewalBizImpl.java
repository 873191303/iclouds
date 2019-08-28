package com.h3c.iclouds.biz.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.h3c.iclouds.base.BaseBizImpl;
import com.h3c.iclouds.base.BaseRestControl;
import com.h3c.iclouds.biz.RenewalBiz;
import com.h3c.iclouds.biz.UserBiz;
import com.h3c.iclouds.biz.VolumeBiz;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.dao.NovaVmViewDao;
import com.h3c.iclouds.dao.RenewalDao;
import com.h3c.iclouds.exception.MessageException;
import com.h3c.iclouds.operate.CloudosClient;
import com.h3c.iclouds.po.NovaVmView;
import com.h3c.iclouds.po.Renewal;
import com.h3c.iclouds.po.User;
import com.h3c.iclouds.po.Volume;
import com.h3c.iclouds.task.novavm.NovaVmCloneTask;
import com.h3c.iclouds.utils.LogUtils;

@Service("renewalBiz")
public class RenewalBizImpl extends BaseBizImpl<Renewal> implements RenewalBiz {

	@Resource
	private RenewalDao renewalDao;

	@Resource
	private NovaVmViewDao novaVmViewDao;

	@Resource
	private VolumeBiz volumeBiz;

	@Resource
	private UserBiz userBiz;

	@Override
	public List<Renewal> selRenewalByAdmin(String userName) {
		return renewalDao.selRenewalByAdmin(userName);
	}

	// 保存本地租期
	@Override
	public String addRenewal(RenewalBiz renewalBiz, UserBiz userBiz, CloudosClient cloudosClient, String serUuid,
			String time, String projectid, String resUuid, String resName) {
		// 获取用户信息
		Map<String, String> map = new HashMap<>();
		map.put("projectId", projectid);
		List<User> list = userBiz.findByMap(User.class, map);
		User user = list.get(0);
		// String name = user.getLoginName();
		// 计算云主机过期时间运算和转换
		Date date = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);// 设置起时间
		cal.add(Calendar.MONTH, Integer.parseInt(time));// 增加月
		date = cal.getTime();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		String overdueTime = sdf.format(date);
		try {
			// 先请求底层平台租期接口
			Map<String, String> params = new HashMap<String, String>();
			Map<String, Object> params2 = new HashMap<String, Object>();
			params.put("userUuid", user.getCloudosId());
			params.put("userName", user.getLoginName());
			params.put("serUuid", serUuid);// ""
			params.put("resUuid", resUuid);
			params.put("resName", resName);
			params.put("tenancyEndTime", overdueTime);
			params.put("tenancyUuid", "123");
			params2.put("resourceTenancy", params);
			JSONObject re = (JSONObject) JSONObject.toJSON(params2);
			for (int i = 0; i < 2; i++) {
				JSONObject result = cloudosClient.post("/cloudos/restenancy/" + user.getProjectId() + "", re);
				String status = result.getString("result");
				if ("201".equals(status)) {
					Map<String, String> maps = new HashMap<String, String>();

					JSONObject cloudosResults = JSONObject.parseObject(result.getString("record"));
					JSONObject cloudosResult = cloudosResults.getJSONObject("resourceTenancy");
					maps.put("resourceUuid", resUuid);
					List<Renewal> listRen = renewalBiz.findByMap(Renewal.class, maps);
					if (listRen.size() == 0) {
						// 插入成功调用cloudos查询接口，把查询出的数据插入到本地
						renewalBiz.insertRenewal(cloudosResult.getString("uuid"), cloudosResult.getString("userUuid"),
								cloudosResult.getString("userName"), user.getEmail(),
								cloudosResult.getString("serUuid"), cloudosResult.getString("resUuid"),
								cloudosResult.getString("resName"), new Date(cloudosResult.getLongValue("startTime")),
								new Date(cloudosResult.getLongValue("endTime")), false, "", -1, false, "", "");
						// 如果成功执行本地持久化，如果失败，返回错误
						return ConfigProperty.TASK_STATUS3_END_SUCCESS;

					}

				}
			}
			return ConfigProperty.TASK_STATUS4_END_FAILURE;
		} catch (Exception e) {
			LogUtils.exception(NovaVmCloneTask.class, e, " NovaVmCloneTask addZhuQiTime exception");
			return ConfigProperty.TASK_STATUS4_END_FAILURE;
		}
	}

	@Override
	public ResultType toReanewal(String time, String id, String ywlb) {
		int num = VerificationDate(time, id, ywlb);
		if(1 == num) {
			return ResultType.renewal_error;
		}
		ResultType back = null;
		String userId = this.getLoginUser();
		Map<String, String> map = new HashMap<String, String>();
		Map<String, Object> map2 = new HashMap<String, Object>();
		List<Renewal> list = null;
		Renewal renewal = new Renewal();
		if ("yzj".equals(ywlb)) {
			NovaVmView bean = novaVmViewDao.findById(NovaVmView.class, id);
			map.put("resourceUuid", bean.getUuid());
			map2.put("createdate", bean.getCreatedate());
			map2.put("resourceName", bean.getHostname());

		} else if ("yyp".equals(ywlb)) {// 这里用云硬盘查询
			Volume volume = volumeBiz.findById(Volume.class, id);
			map.put("resourceUuid", volume.getUuid());
			map2.put("createdate", volume.getCreatedDate());
			map2.put("resourceName", volume.getName());

		}
		list = renewalDao.findByMap(Renewal.class, map);

		if (list != null && list.size() > 0) {
			renewal = list.get(0);
			renewalDao.getSession().evict(list.get(0));
			back = renewalcl(renewal, userId, time, "修改");
		} else {
			renewal.setResourceUuid(map.get("resourceUuid"));
			renewal.setStartTime((Date) map2.get("createdate"));
			renewal.setResourceName((String) map2.get("resourceName"));
			renewal.setEndTime(new Date());
			// 赋予默认值
			back = renewalcl(renewal, userId, time, "新增");
		}

		return back;
	}

	// 验证续租时间是否超过合同时间
	public int VerificationDate(String time,String id,String ywlb) {
		Map<String,String> map = new HashMap<String,String>();
		Map<String,Object> map2 = new HashMap<String,Object>();
		List<Renewal> list = null; 
		Date overdue = null;
		Renewal renewal = new Renewal(); 
		if("yzj".equals(ywlb)) {
			NovaVmView bean = novaVmViewDao.findById(NovaVmView.class, id);
			map.put("resourceUuid", bean.getUuid());
			map2.put("createdate", bean.getCreatedate());
			map2.put("resourceName", bean.getHostname());
			Map<String, String> maps = new HashMap<>();
			maps.put("projectId", bean.getProjectId());
			List<User> lists =  userBiz.findByMap(User.class, maps);
			User user = lists.get(0);
			overdue =user.getExpireDate();
			
		}else if("yyp".equals(ywlb)) {//这里用云硬盘查询
			Volume volume = volumeBiz.findById(Volume.class, id);
			map.put("resourceUuid", volume.getUuid());
			map2.put("createdate", volume.getCreatedDate());
			map2.put("resourceName", volume.getName());
			Map<String, String> maps = new HashMap<>();
			maps.put("projectId", volume.getProjectId());
			List<User> lists =  userBiz.findByMap(User.class, maps);
			User user = lists.get(0);
			overdue = user.getExpireDate();
			
		}
		list = renewalDao.findByMap(Renewal.class, map);
		if(list!=null&&list.size()>0) {
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式
			renewal = list.get(0); 
			Date endTime = renewal.getEndTime();
			Calendar calendar =Calendar.getInstance();
			calendar.setTime(endTime);
			switch(time) {
			case  "1天":
				calendar.add(calendar.DAY_OF_YEAR, 1);//增加一天,负数为减少一天
				break;
			case  "1周":
				calendar.add(calendar.WEEK_OF_MONTH, 1);//增加一个礼拜
				break;
			case  "1季度":
				calendar.add(calendar.MONTH, 4);//增加四个月
				break;
			case  "1月":
				calendar.add(calendar.MONTH, 1);//增加1个月
				break;
			case  "1年":
				calendar.add(calendar.YEAR, 1);//增加一年
				break;
			case  "永久":
				calendar.add(calendar.YEAR, 10);//增加10年
				break;
				
		}
		return compare_date(df.format(calendar.getTime()),overdue);
		}else {
			renewal.setResourceUuid(map.get("resourceUuid"));
			renewal.setStartTime((Date)map2.get("createdate"));
			renewal.setResourceName((String)map2.get("resourceName"));
			renewal.setEndTime(new Date());
		}
		return 0;
	}

	// 封装时间大小比较方法
	public int compare_date(String date1, Date dt2) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			Date dt1 = df.parse(date1);
			// Date dt2 = df.parse(date2);
			if (dt1.getTime() > dt2.getTime()) {
				// System.out.println("dt1 在dt2前");
				return 1;
			} else if (dt1.getTime() < dt2.getTime()) {
				// System.out.println("dt1在dt2后");
				return -1;
			} else {
				return 0;
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return 0;
	}

	// 云主机续租功能

	public ResultType renewalcl(Renewal bean, String userId, String day, String czlb) {
		// User user = userBiz.findById(User.class, userId);
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
		// Calendar rightNow = Calendar.getInstance();
		// rightNow.setTime(bean.getEndTime());
		// rightNow.add(Calendar.MONTH, Integer.parseInt(day));// 日期加N个月
		// Date endTime = rightNow.getTime();
		params2.put("endTime", bean.getEndTime().getTime());// 租期到期时间
		params2.put("due", bean.isIsdue());// 是否到期
		params2.put("tenantId", bean.getServiceUuid());// 组织id
		params2.put("tenancyDay", null);// 续租时间
		params2.put("userName", bean.getUserName());// 用户名
		params2.put("tenancyUuid", null);
		String dayUuid = "";
		switch (day) {
		case "1天":
			dayUuid = "23d539d1-d7b0-493c-9486-de439618efc1";
			break;
		case "1周":
			dayUuid = "494c3e40-01c5-4173-aa95-04350659d6ac";
			break;
		case "1季度":
			dayUuid = "8bf4a75f-4c9e-4d2f-936f-18fe2711ee65";
			break;
		case "1月":
			dayUuid = "ee120318-0500-4678-9782-0113e5375f8f";
			break;
		case "1年":
			dayUuid = "d7f93e3d-e4d5-4d96-999a-8cd864bd9c58";
			break;
		case "永久":
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
				if (czlb.equals("修改")) {
					renewalDao.update(ren);
				} else {
					renewalDao.add(ren);
				}

				return ResultType.success;
			}
		}
		return ResultType.system_error;
	}

	@Override
	public Object insertRenewal(String uuid, String userUuid, String userName, String email, String serviceUuid,
			String resourceUuid, String resourceName, Date startTime, Date endTime, boolean isdue, String tenantId,
			Integer tenancyDay, boolean deleted, String description, String dueAction) {
		Renewal dto = new Renewal();
		dto.setUuid(uuid);
		dto.setUserName(userName);
		dto.setUserUuid(userUuid);
		dto.setEmail(email);
		dto.setServiceUuid(serviceUuid);
		dto.setResourceUuid(resourceUuid);
		dto.setResourceName(resourceName);
		dto.setStartTime(startTime);
		dto.setEndTime(endTime);
		dto.setIsdue(isdue);
		dto.setTenantId(tenantId);
		dto.setTenancyDay(tenancyDay);
		dto.setDeleted(deleted);
		dto.setDescription(description);
		dto.setDueAction(dueAction);
		renewalDao.insertRenewal(dto);
		return ResultType.success;
	}

	@Override
	public Object updateRenewal(String uuid, String userUuid, String userName, String email, String serviceUuid,
			String resourceUuid, String resourceName, Date startTime, Date endTime, boolean isdue, String tenantId,
			Integer tenancyDay, boolean deleted, String description, String dueAction) {
		Renewal dto = new Renewal();
		dto.setUuid(uuid);
		dto.setUserName(userName);
		dto.setUserUuid(userUuid);
		dto.setEmail(email);
		dto.setServiceUuid(serviceUuid);
		dto.setResourceUuid(resourceUuid);
		dto.setResourceName(resourceName);
		dto.setStartTime(startTime);
		dto.setEndTime(endTime);
		dto.setIsdue(isdue);
		dto.setTenantId(tenantId);
		dto.setTenancyDay(tenancyDay);
		dto.setDeleted(deleted);
		dto.setDescription(description);
		dto.setDueAction(dueAction);//
		// renewalDao.update(dto);
		renewalDao.updateRenewal(dto);
		return ResultType.success;
	}

}

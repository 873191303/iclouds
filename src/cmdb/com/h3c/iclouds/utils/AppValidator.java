package com.h3c.iclouds.utils;

import com.h3c.iclouds.base.SpringContextHolder;
import com.h3c.iclouds.po.bean.AppInfo;
import com.h3c.iclouds.po.bean.ApplicationBean;
import com.h3c.iclouds.biz.ApplicationMasterBiz;
import com.h3c.iclouds.biz.DatabaseMasterBiz;
import com.h3c.iclouds.biz.ServiceClusterBiz;
import com.h3c.iclouds.biz.ServiceMasterBiz;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.exception.MessageException;
import com.h3c.iclouds.po.ApplicationMaster;
import com.h3c.iclouds.po.DatabaseMaster;
import com.h3c.iclouds.po.ServiceCluster;
import com.h3c.iclouds.po.ServiceMaster;
import com.h3c.iclouds.validate.ValidatorUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class AppValidator {

	@Resource
	private ApplicationMasterBiz applicationMasterBiz = SpringContextHolder.getBean("applicationMasterBiz");
	@Resource
	private DatabaseMasterBiz databaseMasterBiz = SpringContextHolder.getBean("databaseMasterBiz");
	@Resource
	private ServiceClusterBiz serviceClusterBiz = SpringContextHolder.getBean("serviceClusterBiz");
	@Resource
	private ServiceMasterBiz serviceMasterBiz = SpringContextHolder.getBean("serviceMasterBiz");

	public static final Set<String> types = new HashSet<>();

	public static final ConcurrentHashMap<String, Object> cache = new ConcurrentHashMap<>();

	public static Set<String> getTypes() {
		return types;
	}

	public static final Map<Integer, String> sortMap = new HashMap<>();
	static {
		types.add(ConfigProperty.APP_DATABASE);
		types.add(ConfigProperty.APP_CLUSTER_DATABASE);
		types.add(ConfigProperty.APP_SERVICE);
		types.add(ConfigProperty.APP_CLUSTER_SERVICE);
		types.add(ConfigProperty.APP_APPLICATION);
		// 操作顺序
		sortMap.put(0, "03");// 删除数据库
		sortMap.put(1, "013");// 删除数据库集群
		sortMap.put(2, "02");// 删除中间件
		sortMap.put(3, "012");// 删除中间件集群
		sortMap.put(4, "13");// 新增数据库
		sortMap.put(5, "113");// 新增数据库集群
		sortMap.put(6, "12");// 新增中间件
		sortMap.put(7, "112");// 新增中间件集群
		sortMap.put(8, "20");// 修改应用app
		sortMap.put(9, "212");// 修改中间件集群
		sortMap.put(10, "22");// 修改中间件
		sortMap.put(11, "213");// 修改数据库集群
		sortMap.put(12, "23");// 修改数据库
	}

	public static Map<Integer, String> getSortMap() {
		return sortMap;
	}

	public static Map<String, String> getRelation() {
		return relation;
	}

	public static Map<String, String> relation = new HashMap<>();

	public static List<Map<String, String>> verifyData(AppInfo appInfo) {
		List<Map<String, String>> errorList = new ArrayList<>();// 错误集合
		// 不能有没有父节点的存在
		List<ApplicationBean> list = appInfo.getData();
		Set<String> ids = new HashSet<>();
		System.out.println("前端传入根节点的Appid：" + appInfo.getAppId() + "节点个数" + list.size());
		for (ApplicationBean bean : list) {
			System.out.println("节点类型" + bean.getType() + "前端传入的id：" + bean.getId() + "前端传入的pid" + bean.getPid());
			ids.add(bean.getId());
		}
		ids.add(appInfo.getAppId());
		if (ids.size() != (list.size())) {
			throw new MessageException(ResultType.app_id_repeat);
		}
		if (StrUtils.checkParam(list)) {
			for (ApplicationBean bean : list) {
				if (!check(bean)) {
					throw new MessageException(ResultType.option_not_null);
				}
				if(StrUtils.checkCollection(bean.getPid())) {
					for (String pid : bean.getPid()) {
						if (pid.equals("-1")) {
							continue;
						}
						if (!ids.contains(pid)) {
							throw new MessageException(ResultType.app_pid_not_point);
						}
					}
					// 验证特定的数据
					Map<String, String> errorMap = checkBean(bean, list);//
					if (StrUtils.checkParam(errorMap)) {//
						errorList.add(errorMap);
					}
				}
			}
		}
		return errorList;// 返回错误信息
	}

	private static Map<String, String> checkBean(ApplicationBean bean, List<ApplicationBean> list) {
		Map<String, Object> data = bean.getData();
		Map<String, String> errorMap = new HashMap<>();
		/*
		 * if (!StrUtils.checkParam(data)) { throw new
		 * MessageException(ResultType.data_not_null); }
		 */
		List<String> pids = bean.getPid();
		if (!StrUtils.checkCollection(pids)) {
			throw new MessageException(ResultType.app_pid_not_point);
		}
		ApplicationBean parentBean = check(bean, list);
		ServiceCluster serviceCluster = new ServiceCluster();
		switch (bean.getType()) {
		case "0":
			ApplicationMaster applicationMaster = new ApplicationMaster();
			if (StrUtils.checkParam(data)) {
				InvokeSetForm.settingForm(data, applicationMaster);
				errorMap = ValidatorUtils.validator(applicationMaster);
			}
			if (!"-1".equals(bean.getPid().get(0))) {
				errorMap.put("pid", "父节点id不合法");
			}
			break;
		case "12":
			if (StrUtils.checkParam(data)) {
				InvokeSetForm.settingForm(data, serviceCluster);
				errorMap = ValidatorUtils.validator(serviceCluster);
			}
			if (!StrUtils.checkParam(parentBean)) {
				errorMap.put("pid", "元素没有指定父节点");
			}
			if (!"0".equals(parentBean.getType())) {
				errorMap.put("pid", "父节点必须是应用节点");
			}
			break;
		case "13":
			if (StrUtils.checkParam(data)) {
				InvokeSetForm.settingForm(data, serviceCluster);
				errorMap = ValidatorUtils.validator(serviceCluster);
			}
			if (!StrUtils.checkParam(parentBean)) {
				errorMap.put("pid", "元素没有指定父节点");
			}
			if (!("0".equals(parentBean.getType()) || "2".equals(parentBean.getType()))) {
				errorMap.put("pid", "父节点必须是应用节点或者中间件");
			}
			break;
		case "2":
			if (StrUtils.checkParam(data)) {
				ServiceMaster master = new ServiceMaster();
				InvokeSetForm.settingForm(data, master);
				errorMap = ValidatorUtils.validator(master);
			}
			if (!StrUtils.checkParam(parentBean)) {
				errorMap.put("pid", "元素没有指定父节点");
			}
			if (!("0".equals(parentBean.getType()) || "12".equals(parentBean.getType()))) {
				errorMap.put("pid", "父节点必须是应用节点或者中间集群");
			}
			break;
		case "3":
			if (StrUtils.checkParam(data)) {
				DatabaseMaster databaseMaster = new DatabaseMaster();
				InvokeSetForm.settingForm(data, databaseMaster);
				errorMap = ValidatorUtils.validator(databaseMaster);
			}
			if (!StrUtils.checkParam(parentBean)) {
				errorMap.put("pid", "元素没有指定父节点");
			}
			if (!("2".equals(parentBean.getType()) || "13".equals(parentBean.getType()))) {
				errorMap.put("pid", "父节点必须是中间件或者数据库集群");
			}
			break;
		}
		return errorMap;

	}

	private static ApplicationBean check(ApplicationBean bean, List<ApplicationBean> list) {
		for (String pid : bean.getPid()) {
			for (ApplicationBean applicationBean : list) {
				if (pid.equals(applicationBean.getId())) {
					return applicationBean;
				}
			}
		}
		return null;

	}

	private static boolean check(ApplicationBean bean) {
		String option = bean.getOption();
		if (option.equals(ConfigProperty.RESOURCE_OPTION_DELETE) || option.equals(ConfigProperty.RESOURCE_OPTION_UPDATE)
				|| option.equals(ConfigProperty.RESOURCE_OPTION_ADD)||option.equals("3")) {
			return true;
		} else {
			return false;
		}
	}

}

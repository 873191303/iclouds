package com.h3c.iclouds.biz.impl;

import com.h3c.iclouds.base.BaseBizImpl;
import com.h3c.iclouds.biz.AppItemsBiz;
import com.h3c.iclouds.biz.AppRelationsBiz;
import com.h3c.iclouds.biz.AppViewsBiz;
import com.h3c.iclouds.biz.ApplicationMasterBiz;
import com.h3c.iclouds.biz.DatabaseMasterBiz;
import com.h3c.iclouds.biz.ServiceClusterBiz;
import com.h3c.iclouds.biz.ServiceMasterBiz;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.dao.ApplicationMasterDao;
import com.h3c.iclouds.exception.MessageException;
import com.h3c.iclouds.po.AppItems;
import com.h3c.iclouds.po.AppRelations;
import com.h3c.iclouds.po.AppViews;
import com.h3c.iclouds.po.ApplicationMaster;
import com.h3c.iclouds.po.DatabaseMaster;
import com.h3c.iclouds.po.ServiceCluster;
import com.h3c.iclouds.po.ServiceMaster;
import com.h3c.iclouds.po.bean.AppInfo;
import com.h3c.iclouds.po.bean.ApplicationBean;
import com.h3c.iclouds.utils.AppValidator;
import com.h3c.iclouds.utils.InvokeSetForm;
import com.h3c.iclouds.utils.StrUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service("applicationMasterBiz")
public class ApplicationMasterBizImpl extends BaseBizImpl<ApplicationMaster> implements ApplicationMasterBiz {

	@Resource
	private ApplicationMasterDao applicationMasterDao;

	@Resource
	private AppViewsBiz appViewsBiz;

	@Resource
	private AppRelationsBiz appRelationsBiz;

	@Resource
	private AppItemsBiz appItemsBiz;

	@Resource
	private DatabaseMasterBiz databaseMasterBiz;
	
	@Resource
	private ServiceClusterBiz serviceClusterBiz;
	
	@Resource
	private ServiceMasterBiz serviceMasterBiz;

	@SuppressWarnings("unused")
	private Map<String, Object> errorMap = null;

	@Override
	public PageModel<ApplicationMaster> findForPage(PageEntity entity) {
		return applicationMasterDao.findForPage(entity);
	}

	@Override
	public ResultType save(ApplicationMaster applicationMaster) {
		applicationMaster.createdUser(getLoginUser());
		String appId = UUID.randomUUID().toString();
		applicationMaster.setId(appId);
		applicationMaster.setProjectId(getProjectId());
		applicationMaster.setOwner(getLoginUser());
		applicationMasterDao.add(applicationMaster);
		// 保存第一次应用视图
		// 应用对象
		AppItems appItems = new AppItems();
		appItems.setId(appId);
		appItems.setItemtype("0");
		appItems.setOption("0");
		appItems.setUuid(appId);
		appItems.setName(applicationMaster.getAppName());
		appItemsBiz.add(appItems);
		// 应用视图
		AppViews appViews = new AppViews();
		String viewsId = UUID.randomUUID().toString();
		appViews.setId(viewsId);
		appViews.setName(applicationMaster.getAppName());
		appViews.createdUser(getLoginUser());
		appViews.setSequence(0);
		appViews.setProjectId(getProjectId());
		appViewsBiz.add(appViews);
		// appViews.setDescription(applicationMaster.get);
		// 应用拓扑关系图
		AppRelations appRelations = new AppRelations();
		appRelations.setAppId(appId);
		appRelations.setViewId(viewsId);
		appRelations.setPrevious("-1");
		appRelations.setSequence(0);
		appRelationsBiz.add(appRelations);
		return ResultType.success;
	}

	@Override
	public ResultType delete(String appId) {
		
		String viewId = appViewsBiz.getViews(appId);
		List<AppRelations> appRelationss = appRelationsBiz.listByClass(AppRelations.class, StrUtils.createMap
				("viewId", viewId));
		Set<AppItems> appItemss = new HashSet<>();
		for (AppRelations appRelations : appRelationss) {
			AppItems appItems = appItemsBiz.findById(AppItems.class, appRelations.getAppId());
			appItemss.add(appItems);
			appRelationsBiz.delete(appRelations);
		}
		
		for (AppItems appItems : appItemss) {
			this.deleteByItem(appItems);
		}
		
		AppViews appViews = appViewsBiz.findById(AppViews.class, viewId);
		appViewsBiz.delete(appViews);
		return ResultType.success;
	}

	@Override
	public void update(AppInfo appInfo) {
		errorMap = new HashMap<>();
		List<ApplicationBean> beans = appInfo.getData();
		String appId = appInfo.getAppId();
		String viewId = appViewsBiz.getViews(appId);
		// 应增加的个数
		Map<Integer, String> sortMap = AppValidator.getSortMap();
		for (int i = 0; i < sortMap.size(); i++) {
			String sequence = sortMap.get(i);
			for (ApplicationBean bean : beans) {
				if (sequence.equals(bean.getSequence())) {// 根据操作顺序操作数据
					this.info("执行的操作码" + sequence);
					String option = bean.getOption();
					switch (option) {
					case "0":
						this.delete(bean);
						break;
					case "1":
						// 需要根据type来编排顺序
						// 新增根本不知道前者id，也可能知道是什么,让前端制造id
						// 添加
						this.add(bean, viewId);
						break;
					case "2":
						// 修改
						this.update(bean);
						break;
					default:
						break;
					}
				}
			}
		}
	}

	@SuppressWarnings("unused")
	public void add(ApplicationBean bean, String viewId) {
		String type = bean.getType();
		Map<String, Object> data = bean.getData();
		String itemId = bean.getId();

		switch (type) {
		case "0":
			throw new MessageException("不能添加应用");

		case "12": // 中间件集群
			ServiceCluster serviceCluster = serviceClusterBiz.save(bean);
			break;

		case "2": // 中间件
			ServiceMaster serviceMaster = new ServiceMaster();
			serviceMaster.createdUser(getLoginUser());
			serviceMaster.setId(itemId);
			serviceMaster.setProjectId(getProjectId());
			InvokeSetForm.settingForm(data, serviceMaster);
			// 子id ,父id
			serviceMasterBiz.add(serviceMaster);
			break;
		case "13": // 2-集群
			ServiceCluster dbCluster = serviceClusterBiz.save(bean);
			break;
		case "3": // 数据库
			DatabaseMaster databaseMaster = new DatabaseMaster();
			InvokeSetForm.settingForm(data, databaseMaster);
			databaseMaster.createdUser(getLoginUser());
			databaseMaster.setId(itemId);
			databaseMasterBiz.add(databaseMaster);
			break;
		default:
			break;
		}
		appRelationsBiz.save(bean, viewId);
	}

	private void delete(ApplicationBean bean) {
		String id = bean.getId();
		AppItems appItems = appItemsBiz.findById(AppItems.class, id);
		List<AppRelations> keeps = new ArrayList<>();
		List<AppRelations> children = appRelationsBiz.findByPropertyName(AppRelations.class, "previous", id);
		for (AppRelations child : children) {//检查底下是否挂载子集
			if (appRelationsBiz.count(AppRelations.class, StrUtils.createMap("appId", child.getAppId())) > 1) {
				//如果子集有除了当前以外的父级则可以直接删除
				appRelationsBiz.delete(child);
			} else {
				keeps.add(child);//不能直接删除 需要特殊处理 --将关联的父级移到上一层
			}
		}
		
		// 删除关系
		Set<String> previousIds = new HashSet<>();//记录当前连接的上级
		List<AppRelations> relations = appRelationsBiz.findByPropertyName(AppRelations.class, "appId", id);
		for (AppRelations appRelations : relations) {
			appRelationsBiz.delete(appRelations);
			previousIds.add(appRelations.getPrevious());
		}
		
		for (AppRelations keep : keeps) {
			for (String previousId : previousIds) {//建立新的上下级关系
				AppRelations appRelations = new AppRelations();
				InvokeSetForm.copyFormProperties(keep, appRelations);
				appRelations.setPrevious(previousId);
				appRelationsBiz.add(appRelations);
			}
			appRelationsBiz.delete(keep);//删除原本的上下级关系
		}
		
		String type = bean.getType();
		// 删除应用对象
		if (StrUtils.checkParam(appItems)) {
			appItemsBiz.delete(appItems);
		}
		
		switch (type) {
		case "0":

			break;
		case "12": // 2-集群
			// 删除对应的实体数据
			serviceClusterBiz.deleteById(ServiceCluster.class, bean.getId());
			break;
		case "2": // 中间件
			serviceMasterBiz.deleteById(ServiceMaster.class, bean.getId());
			break;
		case "13": // 2-集群
			serviceClusterBiz.deleteById(ServiceCluster.class, bean.getId());
			break;
		case "3": // 数据库
			databaseMasterBiz.deleteById(DatabaseMaster.class, bean.getId());
			break;
		default:
			break;
		}
	}

	public void update(ApplicationBean bean) {
		ServiceCluster serviceCluster;
		String type = bean.getType();
		Map<String, Object> params = new HashMap<>();
		Map<String, Object> data = bean.getData();
		String id = bean.getId();
		params.put("appId", bean.getId());
		
		switch (type) {
		case "0":
			// 修改相应的属性
			ApplicationMaster applicationMaster = findById(ApplicationMaster.class, id);
			if (!StrUtils.checkParam(applicationMaster)) {
				throw new MessageException(ResultType.app_not_exist);
			}
			InvokeSetForm.settingForm(data, applicationMaster);
			applicationMaster.updatedUser(getLoginUser());
			applicationMaster.setProjectId(getProjectId());
			this.update(applicationMaster);
			break;
		case "12": // 2-集群
			serviceCluster = serviceClusterBiz.findById(ServiceCluster.class, id);
			if (!StrUtils.checkParam(serviceCluster)) {
				throw new MessageException(ResultType.app_cluster_not_exist);
			}
			InvokeSetForm.settingForm(data, serviceCluster);
			serviceCluster.setProjectId(getProjectId());
			serviceCluster.updatedUser(getLoginUser());
			serviceClusterBiz.update(serviceCluster);
			break;
		case "2": // 中间件
			ServiceMaster serviceMaster = serviceMasterBiz.findById(ServiceMaster.class, id);
			if (!StrUtils.checkParam(serviceMaster)) {
				throw new MessageException(ResultType.service_not_exist);
			}
			InvokeSetForm.settingForm(data, serviceMaster);
			serviceMaster.updatedUser(getLoginUser());
			serviceMaster.setProjectId(getProjectId());
			serviceMaster.setServerOwner(getLoginUser());
			serviceMasterBiz.update(serviceMaster);
			break;
		case "13": // 2-集群
			serviceCluster = serviceClusterBiz.findById(ServiceCluster.class, id);
			if (!StrUtils.checkParam(serviceCluster)) {
				throw new MessageException(ResultType.app_cluster_not_exist);
			}
			InvokeSetForm.settingForm(data, serviceCluster);
			serviceCluster.setProjectId(getProjectId());
			serviceCluster.updatedUser(getLoginUser());
			serviceClusterBiz.update(serviceCluster);
			break;
		case "3": // 数据库
			DatabaseMaster databaseMaster = databaseMasterBiz.findById(DatabaseMaster.class, id);
			if (!StrUtils.checkParam(databaseMaster)) {
				throw new MessageException(ResultType.app_database_not_exist);
			}
			InvokeSetForm.settingForm(data, databaseMaster);
			databaseMaster.updatedUser(getLoginUser());
			databaseMaster.setProjectId(getProjectId());
			databaseMasterBiz.update(databaseMaster);
			break;
		default:
			break;
		}
		AppItems items = appItemsBiz.findById(AppItems.class, id);
		String name;
		if ("3".equals(type)) {
			name = StrUtils.tranString(bean.getData().get("dbName"));
		} else if (StrUtils.equals(type, "12", "13")) {
			name = StrUtils.tranString(bean.getData().get("cname"));
		} else {
			name = StrUtils.tranString(bean.getData().get("name"));
		}
		items.setName(name);
		appItemsBiz.update(items);
	}

	private void deleteByItem(AppItems appItems) {
		String id = appItems.getId();
		String type = appItems.getItemtype();
		switch (type) {
		case "0":
			this.deleteById(ApplicationMaster.class, id);
			break;
		case "12":
			serviceClusterBiz.deleteById(ServiceCluster.class, id);
			break;
		case "13":
			serviceClusterBiz.deleteById(ServiceCluster.class, id);
			break;
		case "2":
			serviceMasterBiz.deleteById(ServiceMaster.class, id);
			break;
		case "3":
			databaseMasterBiz.deleteById(DatabaseMaster.class, id);
			break;
		default:
			break;
		}
		appItemsBiz.delete(appItems);
	}
	
	@Override
	public void updateApp (ApplicationMaster applicationMaster) {
		this.update(applicationMaster);
		AppItems appItems = appItemsBiz.findById(AppItems.class, applicationMaster.getId());
		appItems.setName(applicationMaster.getAppName());
		appItemsBiz.update(appItems);
		AppViews appViews = appViewsBiz.findById(AppViews.class, appViewsBiz.getViews(applicationMaster.getId()));
		appViews.setName(applicationMaster.getAppName());
		appViewsBiz.update(appViews);
	}
}

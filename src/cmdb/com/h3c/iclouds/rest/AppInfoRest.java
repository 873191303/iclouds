package com.h3c.iclouds.rest;

import com.h3c.iclouds.base.BaseRestControl;
import com.h3c.iclouds.biz.AppItemsBiz;
import com.h3c.iclouds.biz.AppRelationsBiz;
import com.h3c.iclouds.biz.AppViewsBiz;
import com.h3c.iclouds.biz.ApplicationMasterBiz;
import com.h3c.iclouds.biz.ProjectBiz;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.exception.MessageException;
import com.h3c.iclouds.po.AppItems;
import com.h3c.iclouds.po.AppRelations;
import com.h3c.iclouds.po.AppViews;
import com.h3c.iclouds.po.ApplicationMaster;
import com.h3c.iclouds.po.Project;
import com.h3c.iclouds.po.ServiceCluster;
import com.h3c.iclouds.po.ServiceMaster;
import com.h3c.iclouds.po.Vdc;
import com.h3c.iclouds.po.bean.AppInfo;
import com.h3c.iclouds.utils.AppHandle;
import com.h3c.iclouds.utils.AppValidator;
import com.h3c.iclouds.utils.JacksonUtil;
import com.h3c.iclouds.utils.StrUtils;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Api(value = "应用视图功能")
@RestController
@RequestMapping("/appInfo")
public class AppInfoRest extends BaseRestControl {

	@Resource
	private ApplicationMasterBiz applicationMasterBiz;

	@Resource
	private AppViewsBiz appViewsBiz;

	@Resource
	private AppRelationsBiz appRelationsBiz;

	@Resource
	private AppItemsBiz appItemsBiz;
	
	@Resource
	private ProjectBiz projectBiz;
	
	@ApiOperation(value = "保存应用配置视图信息", response = AppInfo.class)
	@RequestMapping(value = "/update/{version}", method = RequestMethod.POST)
	public Object update(@RequestBody AppInfo appInfo, @PathVariable String version) {
		if (StrUtils.checkParam(appInfo.getAppId())) {
			ApplicationMaster applicationMaster = applicationMasterBiz.findById(ApplicationMaster.class,
					appInfo.getAppId());
			if (!StrUtils.checkParam(applicationMaster)) {
				return BaseRestControl.tranReturnValue(ResultType.app_not_exist);
			}
			if (!projectBiz.checkOptionRole(applicationMaster.getProjectId(), applicationMaster.getOwner())) {
				return BaseRestControl.tranReturnValue(ResultType.unAuthorized);
			}
		}else {
			unlockApp(version, appInfo.getAppId());// 传进空集合时解锁app
			return BaseRestControl.tranReturnValue(ResultType.app_not_exist);
		}
		AppViews appViews = getViews(appInfo.getAppId());
		ResultType rs = checkApplicationMaster(appViews, version, "save");// 检查app的userId、sessionId、version是否匹配
		if (!rs.equals(ResultType.success)) {
			return BaseRestControl.tranReturnValue(rs);
		}
		try {
			List<Map<String, String>> errorList = AppValidator.verifyData(appInfo);// 验证所有提交数据的参数和格式以及操作逻辑是否错误
			if (StrUtils.checkParam(errorList)) {// 出错时直接返回错误
				unlockApp(version, appInfo.getAppId());
				return BaseRestControl.tranReturnValue(ResultType.parameter_error, errorList);
			}
			AppHandle appHandle = new AppHandle();
			appHandle.sortAppInfo(appInfo.getData());
			applicationMasterBiz.update(appInfo);
			unlockApp(version, appInfo.getAppId());// 传进空集合时解锁app
			return BaseRestControl.tranReturnValue(ResultType.success);// 返回操作成功
		} catch (Exception e) {
			this.exception(AppViews.class, e,"整图保存失败参数"+JacksonUtil.toJSon(appInfo));
			unlockApp(version, appInfo.getAppId());
			if (e instanceof MessageException) {
				return BaseRestControl.exceptionReturn((MessageException) e);
			}
			return BaseRestControl.tranReturnValue(ResultType.failure);// 返回操作成功
		}
	}

	@ApiOperation(value = "获取当前用户应用配置视图信息")
	@RequestMapping(value = "/{appId}", method = RequestMethod.GET)
	public Object get(@PathVariable String appId) {
		ApplicationMaster applicationMaster = applicationMasterBiz.findById(ApplicationMaster.class, appId);
		if (!StrUtils.checkParam(applicationMaster)) {
			return BaseRestControl.tranReturnValue(ResultType.still_not_create_app);
		}
		Map<String, Object> result = appViewsBiz.getAppViewByAppId(appId);
		return BaseRestControl.tranReturnValue(ResultType.success, result);
	}

	@ApiOperation(value = "锁定当前应用配置视图")
	@RequestMapping(value = "/lock/{appId}", method = RequestMethod.GET)
	public Object lock(@PathVariable String appId) {
		try {
			 Map<String, Object> result = new HashMap<>();
			AppViews appViews = getViews(appId);
			result.put("appViews", appViews);
			if (!StrUtils.checkParam(appViews)) {// 检测app是否存在
				return BaseRestControl.tranReturnValue(ResultType.still_not_create_app);
			}

			if (appViews.getLock()) {// 检查app是否已锁定
				return BaseRestControl.tranReturnValue(ResultType.app_option_lock);
			}
			String userId = this.getSessionBean().getUserId();
			String sessionId = this.getUserToken();
			String version = UUID.randomUUID().toString();
			if (StrUtils.checkParam(appViews.getUserId())) {
				if (!appViews.getUserId().equals(userId)) {// 其他用户在操作
					return BaseRestControl.tranReturnValue(ResultType.lock_by_other_user);
				} else {// 当前用户在操作
					if (StrUtils.checkParam(appViews.getSessionId()) && sessionId.equals(appViews.getSessionId())) {// 检查操作session是否与当前一致
						this.warn("另一个点正在操作应用视图");
						appViews.setVersion(version);// 一致时覆盖当前用户之前的操作版本
						appViewsBiz.update(appViews);
						return BaseRestControl.tranReturnValue(ResultType.success, appViews);
					} else {
						return BaseRestControl.tranReturnValue(ResultType.lock_by_other_address);
					}
				}
			} else {// 没有用户在操作就锁定vdc
				appViews.setUserId(userId);
				appViews.setVersion(version);
				appViews.setSessionId(sessionId);
				appViews.setLock(true);
				appViewsBiz.update(appViews);
				result.put("version", version);
				return BaseRestControl.tranReturnValue(ResultType.success, result);
			}
		} catch (Exception e) {
			this.exception(ApplicationMaster.class, e);
			return BaseRestControl.tranReturnValue(ResultType.failure);
		}
	}

	@ApiOperation(value = "解锁当前应用配置视图")
	@RequestMapping(value = "/unlock/{appId}/{version}", method = RequestMethod.GET)
	public Object unlock(@PathVariable String appId, @PathVariable String version) {
		try {
			return BaseRestControl.tranReturnValue(unlockApp(version, appId));
		} catch (Exception e) {
			this.exception(AppInfo.class, e);
			return BaseRestControl.tranReturnValue(ResultType.failure);
		}
	}

	/**
	 * 检查app锁信息是否匹配
	 *
	 * @param appViews
	 * @param version
	 * @return
	 */
	public ResultType checkApplicationMaster(AppViews appViews, String version, String type) {
		if (!StrUtils.checkParam(appViews)) {
			return ResultType.still_not_create_app;
		}
//		if (appViews.getLock()) {
//			return ResultType.app_option_lock;
//		}
		String userId = this.getSessionBean().getUserId();
		String sessionId = this.getUserToken();
		if (!StrUtils.checkParam(appViews.getUserId())) {// 检查用户
			if ("save".equals(type)) {// 保存时没有其它操作用户即保存当前用户
				appViews.setUserId(userId);
				appViewsBiz.update(appViews);
			}
		} else {
			if (!userId.equals(appViews.getUserId())) {// 检测用户是否匹配
				return ResultType.mapping_user_error;
			}
		}
		if (!StrUtils.checkParam(appViews.getSessionId())) {// 检查session
			if ("save".equals(type)) {// 保存时没有其它session操作时即保存当前session
				appViews.setUserId(userId);
				appViewsBiz.update(appViews);
			}
		} else {
			if (!sessionId.equals(appViews.getSessionId())) {// 检测session是否匹配
				return ResultType.mapping_session_error;
			}
		}
		if (!StrUtils.checkParam(appViews.getVersion())) {// 检查版本
			if ("save".equals(type)) {// 保存当前操作版本
				appViews.setUserId(userId);
				appViewsBiz.update(appViews);
			}
		} else {
			if (!version.equals(appViews.getVersion())) {// 检测版本是否匹配
				return ResultType.mapping_version_error;
			}
		}
		return ResultType.success;
	}

	/**
	 * 解锁app
	 *
	 * @param version
	 */
	public ResultType unlockApp(String version, String appId) {
		AppViews appViews = getViews(appId);
		ResultType rs = checkApplicationMaster(appViews, version, "unlock");// 检查user、session、版本是否匹配
		if (ResultType.success.equals(rs)) {
            appViewsBiz.clearLock(appViews);
			return ResultType.success;
		} else {
			return ResultType.failure;
		}
	}

	@ApiOperation(value = "获取详细信息", response = ServiceMaster.class)
	@RequestMapping(value = "/detail", method = RequestMethod.GET)
	public Object get() {
		String type = request.getParameter("type");
		if (!AppValidator.getTypes().contains(type)) {
			return BaseRestControl.tranReturnValue(ResultType.app_type_not_exist);
		}
		String id = request.getParameter("id");
		if (StrUtils.checkParam(type)) {
			Object result = null;
			try {
				result = appViewsBiz.get(type, id);
				return BaseRestControl.tranReturnValue(result);
			} catch (Exception e) {
				this.exception(Project.class, e);
				if (e instanceof MessageException) {
					return BaseRestControl.tranReturnValue(((MessageException) e).getResultCode());
				}
				this.exception(Project.class, e);
				return BaseRestControl.tranReturnValue(ResultType.failure);
			}
		} else {
			return BaseRestControl.tranReturnValue(ResultType.parameter_error);
		}
	}

	@ApiOperation(value = "中间件云运维服务配置信息【测试】", response = ServiceMaster.class)
	@RequestMapping(value = "/ServiceMaster", method = RequestMethod.GET)
	public Object ServiceMaster(@PathVariable String appId, @PathVariable String version) {
		try {
			ResultType rs = unlockApp(version, appId);// 解除锁定
			return BaseRestControl.tranReturnValue(rs);
		} catch (Exception e) {
			this.exception(Vdc.class, e);
			return BaseRestControl.tranReturnValue(ResultType.failure);
		}
	}

	@ApiOperation(value = "集群云运维服务关系表【测试】", response = ServiceCluster.class)
	@RequestMapping(value = "/ServiceCluster", method = RequestMethod.GET)
	public Object ServiceCluster(@PathVariable String appId, @PathVariable String version) {
		try {
			ResultType rs = unlockApp(version, appId);// 解除锁定
			return BaseRestControl.tranReturnValue(rs);
		} catch (Exception e) {
			this.exception(Vdc.class, e);
			return BaseRestControl.tranReturnValue(ResultType.failure);
		}
	}

	private AppViews getViews(String appId) {
		AppItems appItems = appItemsBiz.findById(AppItems.class, appId);
		if (!StrUtils.checkParam(appItems)) {
			throw new MessageException(ResultType.app_not_exist);
		}
		Map<String, Object> params1 = new HashMap<>();
		params1.put("appId", appItems.getId());
		AppRelations appRelations = appRelationsBiz.singleByClass(AppRelations.class, params1);
		if (!StrUtils.checkParam(appRelations)) {
			throw new MessageException(ResultType.app_relation_not_exist);
		}
		AppViews appViews = appViewsBiz.findById(AppViews.class, appRelations.getViewId());
		return appViews;
	}

}

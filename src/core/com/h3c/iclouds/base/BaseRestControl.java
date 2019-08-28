package com.h3c.iclouds.base;

import com.h3c.iclouds.auth.CacheSingleton;
import com.h3c.iclouds.auth.SessionBean;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.exception.MessageException;
import com.h3c.iclouds.po.User;
import com.h3c.iclouds.utils.LogUtils;
import com.h3c.iclouds.utils.StrUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public abstract class BaseRestControl {
	
	@Resource
	public HttpServletRequest request;
	
	@SuppressWarnings("rawtypes")
	@Resource
	private BaseDAO baseDAO;
	
	@SuppressWarnings("rawtypes")
	public Class clazz = this.getClass();
	
	public Logger log = LoggerFactory.getLogger(clazz);
	
	public CacheSingleton singleton = CacheSingleton.getInstance();
	
	public String getUserToken() {
		return baseDAO.getUserToken();
	}
	
	/**
	 * 获取用户session
	 * @return
	 */
	public SessionBean getSessionBean() {
		return this.baseDAO.getSessionBean();
	}
	
	/**
	 * 获取当前用户相应群组id
	 * @return
	 */
	public Set<String> getGroupSet(){
		return getSessionBean().getGroupSet();
	}
	
	/**
	 * 获取当前用户租户id
	 * @return
	 */
	public String getProjectId(){
		SessionBean sessionBean = getSessionBean();
		if(sessionBean != null) {
			return sessionBean.getProjectId();
		}
		return null;
	}
	
	/**
	 * 获取当前用户登录名
	 * @return
	 */
	public String getLoginUser(){
		SessionBean sessionBean = getSessionBean();
		if(sessionBean != null) {
			String userId = sessionBean.getUserId();
			// junit test
			if(userId == null) {
				return "junitTest";
			}
			return sessionBean.getUserId();
		}
		return null;
	}
	
	/**
	 * 获取项目路径
	 * @return
	 */
	public String getProjectPath() {
		String path = request.getSession().getServletContext().getRealPath("/");
		return path;
	}
	
	/**
	 * 列表之前整理数据
	 * @param keys
	 * @return
	 */
	public PageEntity beforeList(String... keys) {
		// 取得request中的参数
		int startIndex = StrUtils.tranInteger(request.getParameter("startIndex"));
		int pageSize = StrUtils.tranInteger(request.getParameter("pageSize"));
		String sEcho = StrUtils.tranString(request.getParameter("sEcho"));
		
		// 初始化分页参数
		startIndex = startIndex == 0 ? 0 : startIndex;
		pageSize = pageSize == 0 ? 10 : pageSize;
//		int pageNo = Integer.valueOf(startIndex) / Integer.valueOf(pageSize);
		
		String searchValue = StrUtils.tranString(request.getParameter("searchValue"));
		String asSorting = StrUtils.tranString(request.getParameter("asSorting"));
		String columnName = StrUtils.tranString(request.getParameter("columnName"));
		
		PageEntity entity = new PageEntity();
		entity.setAsSorting(asSorting);
		entity.setColumnName(columnName);
		entity.setPageNo(startIndex);
		entity.setPageSize(pageSize);
		entity.setsEcho(sEcho);
		entity.setSearchValue(searchValue);
		entity.setGroupSet(this.getGroupSet());
		if(keys != null && keys.length > 0) {
			String[] array = new String[keys.length];
			Map<String, Object> queryMap = new HashMap<String, Object>();
			for (int i = 0; i < array.length; i++) {
				array[i] = StrUtils.tranString(request.getParameter(keys[i]));
				queryMap.put(keys[i], array[i]);
			}
			entity.setSpecialParams(array);
			entity.setQueryMap(queryMap);
		}
		Map<String, Object> queryMap = new HashMap<String, Object>();
		Map<String, String[]> map = request.getParameterMap();
		if(StrUtils.checkParam(map)) {
			for (String key : map.keySet()) {
				String[] value = map.get(key);
				if(value != null) {
					if(value.length > 1) {
						queryMap.put(key, value);
					} else {
						queryMap.put(key, value[0]);
					}
				}
			}
		}
		entity.setQueryMap(queryMap);
		return entity;
	}
	
	public static Map<String, Object> tranReturnValue(ResultType rt, Object obj) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(ConfigProperty.RESULT, rt);
		map.put(ConfigProperty.TRANSLATE, rt.getOpeValue());
		if(obj != null) {
			map.put(ConfigProperty.RECORD, obj);	
		}
		return map;
	}
	
	public static Map<String, Object> tranReturnValue(Object obj) {
		return tranReturnValue(ResultType.success, obj);
	}

	public static Map<String, Object> tranReturnValue(ResultType rt) {
		return tranReturnValue(rt, null);
	}

	public static String getIpAddress(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_CLIENT_IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_X_FORWARDED_FOR");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}
	
	public static CacheSingleton getCacheSingleton() {
		return CacheSingleton.getInstance();
	}

	public static Map<String, Object> getActionMap(String uri, String action, Map<String, Object> param){
		Map<String, Object> actionMap = new HashMap<>();
		actionMap.put("uri", uri);
		actionMap.put("action", action);
		actionMap.put("param", param);
		return actionMap;
	}

	public static boolean checkStatus(String status){
		boolean result = true;
		switch (status){
			case ConfigProperty.RESOURCE_OPTION_STATUS_CREATE_EXCEPTION:
				result = false;
				break;
			case ConfigProperty.RESOURCE_OPTION_STATUS_UPDATE_EXCEPTION:
				result = false;
				break;
			case ConfigProperty.RESOURCE_OPTION_STATUS_DELETE_EXCEPTION:
				result = false;
				break;
			case "ERROR":
				result = false;
			default:break;
		}
		return result;
	}

	public static String [] getExceptionStatus(){
		String [] status = new String[4];
		status[0] = ConfigProperty.RESOURCE_OPTION_STATUS_CREATE_EXCEPTION;
		status[1] = ConfigProperty.RESOURCE_OPTION_STATUS_UPDATE_EXCEPTION;
		status[2] = ConfigProperty.RESOURCE_OPTION_STATUS_DELETE_EXCEPTION;
		status[3] = "ERROR";
		return status;
	}
	
	/**
	 * 需要记录的信息日志
	 * @param obj
	 */
	public void info(Object obj) {
		log.info(LogUtils.print(obj));
	}
	
	/**
	 * 异常内容日志打印
	 * @param clazz
	 * @param e
	 */
	@SuppressWarnings("rawtypes")
	public void exception(Class clazz, Exception e, Object...objects) {
		if(!(e instanceof MessageException)) {	// message异常不做打印
			e.printStackTrace();
		}
		log.error(LogUtils.print(e.getMessage()));
		if(objects != null && objects.length > 0) {
			for (Object obj : objects) {
				log.error(LogUtils.print(obj));
			}
		}
	}
	
	/**
	 * 异常内容日志打印
	 * @param e
	 * @param objects
	 */
	public void exception(Exception e, Object...objects) {
		if(!(e instanceof MessageException)) {	// message异常不做打印
			e.printStackTrace();
		}
		log.error(LogUtils.print(e.getMessage()));
		if(objects != null && objects.length > 0) {
			for (Object obj : objects) {
				log.error(LogUtils.print(obj));
			}
		}
	}
	
	/**
	 * 重要内容日志打印
	 * @param obj
	 */
	public void warn(Object obj) {
		log.warn(LogUtils.print(obj));
	}
	
	public Map<String, Object> adminSave(String projectId, User user) {
		if (!this.getSessionBean().getSuperUser()) {
			return BaseRestControl.tranReturnValue(ResultType.unAuthorized);
		}
		if (!StrUtils.checkParam(user)) {
			return BaseRestControl.tranReturnValue(ResultType.user_not_exist);
		}
		if (StrUtils.checkParam(projectId) && !projectId.equals(user.getProjectId())) {
			return BaseRestControl.tranReturnValue(ResultType.user_not_belong_to_project);
		}
		return null;
	}

	public static Object exceptionReturn(MessageException e) {
		if(e.getResultCode() != null) {
			return BaseRestControl.tranReturnValue(e.getResultCode(), e.getMessage());
		}
		return BaseRestControl.tranReturnValue(ResultType.failure, e.getMessage());
	}
}

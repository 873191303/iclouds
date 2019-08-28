package com.h3c.iclouds.biz.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.h3c.iclouds.utils.ThreadContext;
import org.springframework.stereotype.Service;

import com.h3c.iclouds.base.BaseBizImpl;
import com.h3c.iclouds.base.BaseRest;
import com.h3c.iclouds.base.SqlQueryBiz;
import com.h3c.iclouds.biz.OperateLogsBiz;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.common.OperateLogEnum;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.common.SqlQueryProperty;
import com.h3c.iclouds.dao.OperateLogsDao;
import com.h3c.iclouds.po.OperateLogs;
import com.h3c.iclouds.utils.StrUtils;

@Service("operateLogsBiz")
public class OperateLogsBizImpl extends BaseBizImpl<OperateLogs> implements OperateLogsBiz {

	@Resource
	private OperateLogsDao login2LogsDao;

	@Resource
	private SqlQueryBiz sqlQueryBiz;

	@Override
	public OperateLogs findLastDateByUserId(String userId) {
		return login2LogsDao.findLastDateByUserId(userId);
	}

	@Override
	public OperateLogs save(String uuid, OperateLogEnum logType, String result, String remark, String id, String name)
			throws Exception {
		OperateLogs log = new OperateLogs(); 
		// 防止日志记录异常影响正常程序
		log.setId(StrUtils.checkParam(uuid) ? uuid : StrUtils.getUUID());
		log.setLogTypeId(logType.getLogTypeId());
		log.setResult(StrUtils.tranString(result));
        String methodApi = StrUtils.tranString(ThreadContext.get("methodApi"));

        StringBuffer buffer = new StringBuffer("类型: [")
                .append(logType.getLogTypeValue())
                .append("][methodApi:")
                .append(methodApi)
                .append("]")
                .append(remark);
		log.setRemark(buffer.toString());

        String userId = this.getLoginUser() == null ? ConfigProperty.SYSTEM_FLAG : this.getLoginUser();
		log.setUserId(userId);
		log.createdUser(userId);

        String loginName = this.getSessionBean().getLoginName() == null ? ConfigProperty.SYSTEM_FLAG
                : this.getSessionBean().getLoginName();
        log.createdUser(loginName);
		log.setIp(BaseRest.getIpAddress(request));
        log.setResourceId(id);
		log.setResourceName(name);	 
		this.add(log);
		return log;
	}

	public boolean checkRestrictLogin(String userId) {
		OperateLogs lastLog = login2LogsDao.findLastRecordByUserId(userId);
		if (null == lastLog)
			return false;
		Date currentTime = new Date();
		Date lastTime = lastLog.getCreatedDate();
		long seconds = (currentTime.getTime() - lastTime.getTime()) / 1000;
		if (seconds >= 60 * ConfigProperty.LOGIN_RESTRICT_TIME)
			return false;
		// 连续三条密码错误记录，且现在和最后一条插入时间在 LOGIN_RESTRICT_TIME 之内
		int errorTimes = pwdErrorTimes(userId, lastTime);
		if (errorTimes == ConfigProperty.PASSWORD_MAX_ERROR_TIMES) {
			return true;
		}
		return false;
	}

	public boolean checkPwdError(String userId) {
		Date currentTime = new Date();
		int errorTimes = pwdErrorTimes(userId, currentTime);
		if (errorTimes > ConfigProperty.PASSWORD_MAX_ERROR_TIMES - 1) {
			// SimpleCache
//			session.setAttribute("errorTime", currentTime);
			return true;
		}
		return false;
	}

	/** 以某个时间点为起点，LOGIN_SEPERATE_TIME 内 密码错误的次数 */
	private int pwdErrorTimes(String userId, Date startDate) {
		Map<String, Object> queryMap = new HashMap<String, Object>();
		queryMap.put("userId", userId);
		queryMap.put("times", ConfigProperty.PASSWORD_MAX_ERROR_TIMES);
		List<Map<String, Object>> lists = sqlQueryBiz.queryByName(SqlQueryProperty.QUERY_USER_LATELY_LOGS, queryMap);

		if (null == lists || lists.size() == 0) {
			return 0;
		}

		int errorCount = 0; // error time
		long critical = startDate.getTime() - 60 * ConfigProperty.LOGIN_SEPERATE_TIME * 1000; // LOGIN_SEPERATE_TIME
																								// 之内
		Date firstElementDate = (Date) lists.get(0).get("createddate");

		for (Map<String, Object> subMap : lists) {
			Date date = (Date) subMap.get("createddate");
			long interval = firstElementDate.getTime() - date.getTime();

			String result = StrUtils.tranString(subMap.get("result"));
			if (ResultType.loginName_or_password_error.toString().equals(result) && date.getTime() >= critical
					&& interval < 60 * ConfigProperty.LOGIN_SEPERATE_TIME * 1000) { // 要保证是一分钟之内的错误才能加
				errorCount++;
			} else {
				break;
			}
		}
		return errorCount;
	}

	@Override
	public PageModel<OperateLogs> findForPage(PageEntity entity) {
		return login2LogsDao.findForPage(entity);
	}

}

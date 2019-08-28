package com.h3c.iclouds.biz;

import com.h3c.iclouds.base.BaseBiz;
import com.h3c.iclouds.common.OperateLogEnum;
import com.h3c.iclouds.po.OperateLogs;

public interface OperateLogsBiz extends BaseBiz<OperateLogs> {

	OperateLogs findLastDateByUserId(String userId);

	OperateLogs save(String uuid, OperateLogEnum logType, String result, String remark, String id, String name) throws Exception;

	/** 是否限制登录 */
	boolean checkRestrictLogin(String userId);

	/** 是否进行密码错误太多提示 */
	boolean checkPwdError(String userId);
}

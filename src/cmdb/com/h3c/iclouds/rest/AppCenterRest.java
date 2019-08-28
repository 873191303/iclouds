package com.h3c.iclouds.rest;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.h3c.iclouds.base.BaseRestControl;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.dao.ApplicationMasterDao;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

@Api(value = "应用中心")
@RestController
@RequestMapping("/appCenter")
public class AppCenterRest extends BaseRestControl{

	@Resource
	private ApplicationMasterDao applicationMasterDao;

	@ApiOperation(value = "获取应用中心数据")
	@RequestMapping(value = "", method = RequestMethod.GET)
	public Object get() {
		Object result=applicationMasterDao.get();
		return BaseRestControl.tranReturnValue(ResultType.success, result);
	}
}

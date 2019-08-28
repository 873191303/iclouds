package com.h3c.iclouds.rest;

import com.h3c.iclouds.base.BaseDAO;
import com.h3c.iclouds.base.BaseRest;
import com.h3c.iclouds.base.BaseRestControl;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageList;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.dao.TaskLogDao;
import com.h3c.iclouds.po.Task2Exec;
import com.h3c.iclouds.po.TaskLogView;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import edu.emory.mathcs.backport.java.util.Collections;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

/**
 * 任务日志视图
 * Created by yKF7317 on 2017/4/20.
 */
@Api(value = "任务日志信息")
@RestController
@RequestMapping("/taskLog")
public class TaskLogRest extends BaseRest<TaskLogView> {
	
	@Resource
	private TaskLogDao taskLogDao;
	
	@Resource(name = "baseDAO")
	private BaseDAO<Task2Exec> detailDao;
	
	@Override
	@ApiOperation(value = "获取日志分页列表", response = TaskLogView.class)
	@RequestMapping(method = RequestMethod.GET)
	public Object list () {
		PageEntity entity = this.beforeList();
		PageModel<TaskLogView> pageModel = taskLogDao.findForPage(entity);
		PageList<TaskLogView> page = new PageList<>(pageModel, entity.getsEcho());
		return BaseRestControl.tranReturnValue(page);
	}
	
	@ApiOperation(value = "获取日志的执行详情列表", response = Task2Exec.class)
	@RequestMapping(value = "/{taskId}/detail", method = RequestMethod.GET)
	public Object list (@PathVariable String taskId) {
		// 非超级管理员需要过滤数据
		if(!this.getSessionBean().getSuperUser()) {
			TaskLogView view = this.taskLogDao.findById(TaskLogView.class, taskId);
			if(view == null) {
				return BaseRestControl.tranReturnValue(Collections.emptyList());
			}
			if(!view.getProjectId().equals(this.getProjectId())) {
				return BaseRestControl.tranReturnValue(ResultType.unAuthorized);
			}
			// 非租户管理员只允许查看自己的日志
			if(!this.getSessionBean().getSuperRole()) {
				if(!view.getCreatedBy().equals(this.getLoginUser())) {
					return BaseRestControl.tranReturnValue(ResultType.unAuthorized);
				}
			}
		}
		
		List<Task2Exec> details = detailDao.findByPropertyName(Task2Exec.class, "taskId", taskId);
		return BaseRestControl.tranReturnValue(details);
	}
	
	@Override
	public Object get (@PathVariable String id) {
		return null;
	}
	
	@Override
	public Object delete (@PathVariable String id) {
		return null;
	}
	
	@Override
	public Object save (@RequestBody TaskLogView entity) {
		return null;
	}
	
	@Override
	public Object update (@PathVariable String id, @RequestBody TaskLogView entity) throws IOException {
		return null;
	}
}

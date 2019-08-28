package com.h3c.iclouds.rest;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.activiti.engine.task.Task;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.h3c.iclouds.base.BaseRest;
import com.h3c.iclouds.base.BaseRestControl;
import com.h3c.iclouds.base.SqlQueryBiz;
import com.h3c.iclouds.biz.IncMasterBiz;
import com.h3c.iclouds.biz.TaskFlowBiz;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageList;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.common.SqlQueryProperty;
import com.h3c.iclouds.dao.Inc2ApproveLogDao;
import com.h3c.iclouds.exception.MessageException;
import com.h3c.iclouds.po.inc.Inc2ApproveLog;
import com.h3c.iclouds.po.inc.IncMaster;
import com.h3c.iclouds.utils.StrUtils;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/complete/inc")
@Api(value = "流程审批", description = "流程审批")
public class IncTaskFlowRest extends BaseRest<IncMaster> {
	
	@Resource
	private IncMasterBiz incMasterBiz;
	
	@Resource
	private Inc2ApproveLogDao inc2ApproveLogDao;
	
	@Resource
	private TaskFlowBiz taskFlowBiz;
	
	@Resource
	private SqlQueryBiz sqlQueryBiz;
	
	@RequestMapping(value = "/{id}/close", method = RequestMethod.POST)
	@ApiOperation(value = "业务办理流程审批")
	public Object close(@PathVariable String id, @RequestBody Map<String, Object> saveMap) {
		IncMaster entity = incMasterBiz.findById(IncMaster.class, id);
		if(entity != null) {
			try {
				if(!entity.getCreatedBy().equals(this.getLoginUser())) {
					return BaseRestControl.tranReturnValue(ResultType.unAuthorized);
				}
				Inc2ApproveLog log = incMasterBiz.close(saveMap, entity);
				return BaseRestControl.tranReturnValue(ResultType.success, log);
			} catch (Exception e) {
				this.exception(this.getClass(), e);
				if(e instanceof MessageException) {
					return BaseRestControl.tranReturnValue(((MessageException)e).getResultCode());
				}
			}
			return BaseRestControl.tranReturnValue(ResultType.parameter_error);
		}
		return BaseRestControl.tranReturnValue(ResultType.deleted);
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.POST)
	@ApiOperation(value = "业务办理流程审批")
	public Object master(@PathVariable String id, @RequestBody Map<String, Object> saveMap) {
		this.info("Inc complete value [id:" + id + "][saveMap:" + StrUtils.toJSONString(saveMap) + "]");
		IncMaster entity = incMasterBiz.findById(IncMaster.class, id);
		if(entity != null) {
			try {
				return BaseRestControl.tranReturnValue(incMasterBiz.complete(saveMap, entity));
			} catch (Exception e) {
				this.exception(this.getClass(), e);
				if(e instanceof MessageException) {
					ResultType result = ((MessageException)e).getResultCode();
					if(result != null) {
						return BaseRestControl.tranReturnValue(((MessageException)e).getResultCode());	
					}
					return BaseRestControl.tranReturnValue(ResultType.failure, e.getMessage());
				}
			}
			return BaseRestControl.tranReturnValue(ResultType.parameter_error);
		}
		return BaseRestControl.tranReturnValue(ResultType.deleted);
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@ApiOperation(value = "获取业务办理审批详细信息")
	public Object master(@PathVariable String id) {
		IncMaster entity = incMasterBiz.findById(IncMaster.class, id);
		if(entity != null) {
			boolean result = taskFlowBiz.taskAuth(entity.getStep(), this.getLoginUser(), entity.getCreatedBy(), entity.getWorkFlowId());
			if(!result) {
				Map<String, Object> map = StrUtils.createMap("approver", this.getLoginUser());
				map.put("reqId", id);
				List<Inc2ApproveLog> list = this.inc2ApproveLogDao.listByClass(Inc2ApproveLog.class, map);
				result = (list != null && !list.isEmpty());
			}
 			if(result) {
				List<Inc2ApproveLog> approveLogs = this.inc2ApproveLogDao.findByMasterId(entity.getId());
				entity.setApproveLogs(approveLogs);
				return BaseRestControl.tranReturnValue(entity);
			}
			return BaseRestControl.tranReturnValue(ResultType.unAuthorized);
		}
		return BaseRestControl.tranReturnValue(ResultType.deleted);
	}
	
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	@ApiOperation(value = "获取业务办理审批列表")
	public Object master() {
		PageEntity entity = this.beforeList();
		PageModel<Map<String, Object>> pageModel = incMasterBiz.findCompleteForPage(entity);
		PageList<Map<String, Object>> page = new PageList<Map<String, Object>>(pageModel, entity.getsEcho());
		return BaseRestControl.tranReturnValue(page);
	}
	
	@RequestMapping(value = "/history", method = RequestMethod.GET)
	@ApiOperation(value = "获取业务办理审批历史列表")
	public Object history() {
		PageEntity entity = this.beforeList();
		PageModel<IncMaster> pageModel = incMasterBiz.findForPageByApprover(entity);
		PageList<IncMaster> page = new PageList<IncMaster>(pageModel, entity.getsEcho());
		return BaseRestControl.tranReturnValue(page);
	}
	
	/**
	 * 获取审批人
	 * @return
	 */
	@RequestMapping(value = "/approver", method = RequestMethod.GET)
	@ApiOperation(value = "获取业务办理审批人")
	public Object masterApprover() {
		String id = request.getParameter("id");
		if(!StrUtils.checkParam(id)) {
			return BaseRestControl.tranReturnValue(ResultType.parameter_error);
		}
		
		IncMaster entity = incMasterBiz.findById(IncMaster.class, id);
		if(entity == null) {
			return BaseRestControl.tranReturnValue(ResultType.deleted);	// 申请单已被删除
		}
		if(entity.getStep().length() > 1) {	// 已经进入审批
			String approve = ConfigProperty.REQUEST_PARAMETER_REJECT;	// 默认为未解决，因为已解决都是走到用户验证环节
			Task task = this.taskFlowBiz.getTask(entity.getInstanceId());
			if(task != null) {
				String nextSegment = this.taskFlowBiz.getNextTaskSegment(entity.getWorkFlowId(), task.getTaskDefinitionKey(), approve);
				if(nextSegment != null && !nextSegment.contains(ConfigProperty.WORKFLOW_END)) {
					if(nextSegment != null && !"".equals(nextSegment)) {
						Map<String, Object> queryMap = new HashMap<String, Object>();	// 查询条件
						queryMap.put("WORKFLOWID", entity.getWorkFlowId());
						queryMap.put("SEGMENT", nextSegment);
						queryMap.put("USERID", this.getLoginUser());
						List<Map<String, Object>> list = sqlQueryBiz.queryByName(SqlQueryProperty.QUERY_MASTER_APPROVER, queryMap);
						return BaseRestControl.tranReturnValue(list);
					}
				}
			}
		}
		return BaseRestControl.tranReturnValue(ResultType.bus_end);
	}
	
	@RequestMapping(value = "/handle/count", method = RequestMethod.GET)
	@ApiOperation(value = "业务办理流程审批")
	public Object handle() {
		return BaseRestControl.tranReturnValue(this.incMasterBiz.queryUserApplyCount());
	}

	@ApiOperation(value = "获取事件工单数量与本月新增数量")
	@RequestMapping(value = "/count", method = RequestMethod.GET)
	public Object count() {
		Map<String, Integer> countMap = new HashMap<>();
		countMap.put("totalCount", incMasterBiz.count(null));
		countMap.put("monthCount", incMasterBiz.count("month"));
		return BaseRestControl.tranReturnValue(countMap);
	}
	
	@Override
	public Object list() {
		
		return null;
	}

	@Override
	public Object get(String id) {
		
		return null;
	}

	@Override
	public Object delete(String id) {
		
		return null;
	}

	@Override
	public Object save(IncMaster entity) {
		
		return null;
	}

	@Override
	public Object update(String id, IncMaster entity) throws IOException {
		
		return null;
	}
}

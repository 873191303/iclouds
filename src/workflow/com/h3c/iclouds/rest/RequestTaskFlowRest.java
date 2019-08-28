package com.h3c.iclouds.rest;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.alibaba.fastjson.JSONObject;
import org.activiti.engine.task.Task;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.h3c.iclouds.base.BaseRest;
import com.h3c.iclouds.base.BaseRestControl;
import com.h3c.iclouds.base.SqlQueryBiz;
import com.h3c.iclouds.biz.DepartmentBiz;
import com.h3c.iclouds.biz.RequestMasterBiz;
import com.h3c.iclouds.biz.TaskFlowBiz;
import com.h3c.iclouds.biz.UserBiz;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageList;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.common.SqlQueryProperty;
import com.h3c.iclouds.dao.Request2ApproveLogDao;
import com.h3c.iclouds.exception.MessageException;
import com.h3c.iclouds.po.Department;
import com.h3c.iclouds.po.User;
import com.h3c.iclouds.po.business.Request2ApproveLog;
import com.h3c.iclouds.po.business.RequestMaster;
import com.h3c.iclouds.utils.StrUtils;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/complete/master")
@Api(value = "流程审批", description = "流程审批")
public class RequestTaskFlowRest extends BaseRest<RequestMaster> {
	
	@Resource
	private RequestMasterBiz requestMasterBiz;
	
	@Resource
	private Request2ApproveLogDao master2ApproveLogDao;
	
	@Resource
	private TaskFlowBiz taskFlowBiz;
	
	@Resource
	private SqlQueryBiz sqlQueryBiz;
	
	@Resource
	private UserBiz userBiz;
	
	@Resource
	private DepartmentBiz departmentBiz;
	
	@RequestMapping(value = "/{id}/file", method = RequestMethod.POST)
	@ApiOperation(value = "业务办理流程审批")
	public Object master(@PathVariable String id, HttpServletRequest request) {
		String comment = request.getParameter(ConfigProperty.REQUEST_COMMENT);
		String result = request.getParameter(ConfigProperty.REQUEST_PARAMETER);
		String approver = request.getParameter(ConfigProperty.REQUEST_APPROVER);
		// 转型为MultipartHttpRequest
		Map<String, Object> saveMap = new HashMap<String, Object>();
		saveMap.put("token", this.getUserToken());
		saveMap.put(ConfigProperty.REQUEST_COMMENT, comment);
		saveMap.put(ConfigProperty.REQUEST_PARAMETER, result);
		saveMap.put(ConfigProperty.REQUEST_APPROVER, approver);
		MultipartFile file = null;
		try {
			MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
			Map<String, MultipartFile> map = multipartRequest.getFileMap();
			for (String key : map.keySet()) {
				file = map.get(key);
				// 单位为B
				long size = file.getSize();
				final long MAX_SIZE = 3 * 1024 * 1024;	// 3M上限
				if(size > MAX_SIZE) {
					return BaseRestControl.tranReturnValue(ResultType.file_size_than_max);
				}
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		RequestMaster entity = requestMasterBiz.findById(RequestMaster.class, id);
		if(entity != null) {
			try {
				// 增加附件参数
				saveMap.put("attachment", file);
				this.warn("Inc complete value [id:" + id + "][saveMap:" + JSONObject.toJSONString(saveMap) + "]");
				return BaseRestControl.tranReturnValue(requestMasterBiz.complete(saveMap, entity));
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
		this.warn("Inc complete value [id:" + id + "][saveMap:" + StrUtils.toJSONString(saveMap) + "]");
		RequestMaster entity = requestMasterBiz.findById(RequestMaster.class, id);
		if(entity != null) {
			try {
				return BaseRestControl.tranReturnValue(requestMasterBiz.complete(saveMap, entity));
			} catch (Exception e) {
				this.exception(this.getClass(), e);
				if(e instanceof MessageException) {
					ResultType result = ((MessageException)e).getResultCode();
					if(result != null) {
						return BaseRestControl.tranReturnValue(result);
					}
					return BaseRestControl.tranReturnValue(result);
				}
				return BaseRestControl.tranReturnValue(ResultType.failure);
			}
		}
		return BaseRestControl.tranReturnValue(ResultType.deleted);
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@ApiOperation(value = "获取业务办理审批详细信息")
	public Object master(@PathVariable String id) {
		RequestMaster entity = requestMasterBiz.findById(RequestMaster.class, id);
		if(entity != null) {
			boolean result = taskFlowBiz.taskAuth(entity.getStep(), this.getLoginUser(), entity.getCreatedBy(), entity.getWorkFlowId());
			if(!result) {
				Map<String, Object> map = StrUtils.createMap("approver", this.getLoginUser());
				map.put("reqId", id);
				List<Request2ApproveLog> list = this.master2ApproveLogDao.listByClass(Request2ApproveLog.class, map);
				result = (list != null && !list.isEmpty());
			}
 			if(result) {
				List<Request2ApproveLog> approveLogs = this.master2ApproveLogDao.findByMasterId(entity.getId());
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
		PageModel<Map<String, Object>> pageModel = requestMasterBiz.findCompleteForPage(entity, true);
		PageList<Map<String, Object>> page = new PageList<Map<String, Object>>(pageModel, entity.getsEcho());
		return BaseRestControl.tranReturnValue(page);
	}
	
	@RequestMapping(value = "/history", method = RequestMethod.GET)
	@ApiOperation(value = "获取业务办理审批历史列表")
	public Object history() {
		PageEntity entity = this.beforeList();
		PageModel<RequestMaster> pageModel = requestMasterBiz.findForPageByApprover(entity);
		PageList<RequestMaster> page = new PageList<RequestMaster>(pageModel, entity.getsEcho());
		return BaseRestControl.tranReturnValue(page);
	}
	
	/**
	 * 获取审批人
	 * 1.未开始
	 * @return
	 */
	@RequestMapping(value = "/approver", method = RequestMethod.GET)
	@ApiOperation(value = "获取业务办理审批人")
	public Object masterApprover() {
		String id = request.getParameter("id");
		String workFlowId = request.getParameter("workFlowId");	// 选择的流程镜像id
		if(workFlowId == null || "".equals(workFlowId)) {
			return BaseRestControl.tranReturnValue(ResultType.parameter_error);
		}
		Map<String, Object> queryMap = new HashMap<String, Object>();	// 查询条件
		queryMap.put("WORKFLOWID", workFlowId);
		RequestMaster entity = null;
		if(id != null) {
			entity = requestMasterBiz.findById(RequestMaster.class, id);
			if(entity == null) {
				return BaseRestControl.tranReturnValue(ResultType.deleted);	// 申请单已被删除
			}
			if(!workFlowId.equals(entity.getWorkFlowId())) {
				return BaseRestControl.tranReturnValue(ResultType.request_not_match_workflow);	// 申请单与流程不匹配
			}
			
			if(entity.getStep().length() > 1) {	// 已经进入审批
				String approve = request.getParameter(ConfigProperty.REQUEST_PARAMETER);	// 选择是否同意
				if(approve == null || !approve.equals(ConfigProperty.REQUEST_PARAMETER_REJECT)) {	// 为空或者
					approve = ConfigProperty.REQUEST_PARAMETER_APPROVE;
				}
				Task task = this.taskFlowBiz.getTask(entity.getInstanceId());
				if(task != null) {
					String nextSegment = this.taskFlowBiz.getNextTaskSegment(entity.getWorkFlowId(), task.getTaskDefinitionKey(), approve);
					if(StrUtils.checkParam(nextSegment)) {
						if(!nextSegment.contains(ConfigProperty.WORKFLOW_END)) {
							queryMap.put("SEGMENT", nextSegment);
							queryMap.put("USERID", this.getLoginUser());
							List<Map<String, Object>> list = sqlQueryBiz.queryByName(SqlQueryProperty.QUERY_MASTER_APPROVER, queryMap);
							return BaseRestControl.tranReturnValue(list);
						}
						return BaseRestControl.tranReturnValue(Collections.EMPTY_LIST);
					}
				}
				return BaseRestControl.tranReturnValue(ResultType.parameter_error);
			}
		}
		try {
			User user = userBiz.findById(User.class, this.getLoginUser());
			if(user.getDeptId() != null) {
				List<Map<String, Object>> list = null;
				Department department = departmentBiz.findById(Department.class, user.getDeptId());
				queryMap.put("USERID", this.getLoginUser());
				if(department.getDepth() > 2) {	// 需要做部门过滤
					queryMap.put("SEGMENT", ConfigProperty.MASTER_STEP2_DEPARTMENT_APPROVE);
					list = sqlQueryBiz.queryByName(SqlQueryProperty.QUERY_MASTER_APPROVER_DEPARTMENT, queryMap);
				} else {
					queryMap.put("SEGMENT", ConfigProperty.MASTER_STEP3_SIGN_APPROVE);
					list = sqlQueryBiz.queryByName(SqlQueryProperty.QUERY_MASTER_APPROVER, queryMap);
				}
				return BaseRestControl.tranReturnValue(list);
			}
			return BaseRestControl.tranReturnValue(ResultType.bus_start_applier_department);
		} catch (Exception e) {
			this.exception(this.getClass(), e);
		}
		return BaseRestControl.tranReturnValue(ResultType.deleted);
	}
	
	@RequestMapping(value = "/handle/count", method = RequestMethod.GET)
	@ApiOperation(value = "业务办理流程审批")
	public Object handle() {
		return BaseRestControl.tranReturnValue(this.requestMasterBiz.queryUserApplyCount());
	}

	@ApiOperation(value = "获取需求工单数量与本月新增数量")
	@RequestMapping(value = "/count", method = RequestMethod.GET)
	public Object count() {
		Map<String, Integer> countMap = new HashMap<>();
		countMap.put("totalCount", requestMasterBiz.count(null));
		countMap.put("monthCount", requestMasterBiz.count("month"));
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
	public Object save(RequestMaster entity) {
		
		return null;
	}

	@Override
	public Object update(String id, RequestMaster entity) throws IOException {
		
		return null;
	}
}

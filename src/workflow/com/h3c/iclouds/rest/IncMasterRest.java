package com.h3c.iclouds.rest;

import com.h3c.iclouds.base.BaseRest;
import com.h3c.iclouds.base.BaseRestControl;
import com.h3c.iclouds.biz.IncMasterBiz;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageList;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.dao.Inc2ApproveLogDao;
import com.h3c.iclouds.dao.User2RoleDao;
import com.h3c.iclouds.po.User2Role;
import com.h3c.iclouds.po.inc.Inc2ApproveLog;
import com.h3c.iclouds.po.inc.IncMaster;
import com.h3c.iclouds.utils.StrUtils;
import com.h3c.iclouds.utils.UploadFileUtils;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/inc/master")
@Api(value = "事件工单")
public class IncMasterRest extends BaseRest<IncMaster> {
	
	@Resource
	private IncMasterBiz incMasterBiz;
	
	@Resource
	private Inc2ApproveLogDao inc2ApproveLogDao;

	@Resource
	private User2RoleDao user2RoleDao;

	@RequestMapping(value = "/list/all", method = RequestMethod.GET)
	@ApiOperation(value = "事件工单列表", response = IncMaster.class)
	public Object listAll() {
		PageEntity entity = this.beforeList();
		if(this.getSessionBean().getSuperUser()) {
			entity.setSpecialParam("list-all");
		}
		PageModel<IncMaster> pageModel = incMasterBiz.findForPage(entity);
		PageList<IncMaster> page = new PageList<IncMaster>(pageModel, entity.getsEcho());
		return BaseRestControl.tranReturnValue(page);
	}

	@Override
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	@ApiOperation(value = "事件工单列表", response = IncMaster.class)
	public Object list() {
		PageEntity entity = this.beforeList();
		PageModel<IncMaster> pageModel = incMasterBiz.findForPage(entity);
		PageList<IncMaster> page = new PageList<IncMaster>(pageModel, entity.getsEcho());
		return BaseRestControl.tranReturnValue(page);
	}

	@Override
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@ApiOperation(value = "获取事件工单", response = IncMaster.class)
	public Object get(@PathVariable String id) {
		IncMaster entity = incMasterBiz.findById(IncMaster.class, id);
		if (entity != null) {
			if(entity.getCreatedBy().equals(this.getSessionBean().getUserId()) || this.getSessionBean().getSuperUser()) {
				List<Inc2ApproveLog> approveLogs = this.inc2ApproveLogDao.findByMasterId(entity.getId());
				entity.setApproveLogs(approveLogs);
				return BaseRestControl.tranReturnValue(entity);
			}
			return BaseRestControl.tranReturnValue(ResultType.unAuthorized);	
		}
		return BaseRestControl.tranReturnValue(ResultType.deleted);
	}

	@Override
	@ApiOperation(value = "删除事件工单", notes = "只有在还未开始申请才能删除")
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public Object delete(@PathVariable String id) {
		IncMaster entity = incMasterBiz.findById(IncMaster.class, id);
		if(entity != null) {
			if(!entity.getCreatedBy().equals(this.getLoginUser())) {
				return BaseRestControl.tranReturnValue(ResultType.unAuthorized);		
			}
			
			// 只有待提交状态才能进行修改
			String step = entity.getStep();
			if(!step.equals(ConfigProperty.MASTER_STEP_SUBMIT)) {
				return BaseRestControl.tranReturnValue(ResultType.bus_started);
			}
			incMasterBiz.delete(entity);	// 删除审批记录，删除业务申请单
			return BaseRestControl.tranReturnValue(ResultType.success);
		}
		return BaseRestControl.tranReturnValue(ResultType.deleted);
	}
	
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	@ApiOperation(value = "保存事件工单", notes = "通过修改step环节开始事件工单", response = IncMaster.class)
	public Object save(@RequestBody Map<String, Object> saveMap) {
		try {
			Map<String, String> validateMap = new HashMap<String, String>();
			return BaseRestControl.tranReturnValue(this.incMasterBiz.save(saveMap, validateMap), validateMap);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return BaseRestControl.tranReturnValue(ResultType.parameter_error);
	}
	
	@RequestMapping(value = "/{id}/log/{logId}/attachment/{uuid}", method = RequestMethod.GET)
	@ApiOperation(value = "下载附件")
	public void master(@PathVariable String id, @PathVariable String logId, @PathVariable String uuid, HttpServletResponse response) {
		IncMaster entity = incMasterBiz.findById(IncMaster.class, id);
		if(entity == null) {
			this.info("事件工单被删除");
			response.setStatus(404);
			return;
		}
		Inc2ApproveLog log = inc2ApproveLogDao.findById(Inc2ApproveLog.class, logId);
		if(log == null) {
			this.info("事件工单被删除");
			response.setStatus(404);
			return;
		}
		String att = log.getAttachment();
		if(!StrUtils.checkParam(att)) {
			this.info("事件工单不存在附件");
			response.setStatus(404);
			return;
		}
		String[] array = att.split("\\|");
		String fileName = null;
		for (int i = 0; i < array.length; i++) {
			if(array[i].contains(uuid)) {
				fileName = array[i];
				break;
			}
		}
		if(null == fileName) {
			this.info("传递的uuid不正确");
			response.setStatus(404);
			return;
		}

		if(!this.getSessionBean().getSuperUser()) {
			// 只有参与工单的人才允许查看图片
			Map<String, Object> map = StrUtils.createMap("reqId", id);
			map.put("createdBy", this.getLoginUser());
			int count = this.inc2ApproveLogDao.count(Inc2ApproveLog.class, map);
			if(count == 0) {
				List<User2Role> list = user2RoleDao.listByClass(User2Role.class, StrUtils.createMap("userId", this.getLoginUser()));
				boolean flag = false;
				// 有一线，二线权限可以查看图片
				if(StrUtils.checkCollection(list)) {
					for (User2Role user2Role : list) {
						if(StrUtils.equals(user2Role.getRoleId(), singleton.getEwoFirstRoleId(), singleton.getEwoSecondRoleId())) {
							flag = true;
						}
					}
				}
				if(!flag) {
					this.info("权限不足");
					response.setStatus(403);
					return;
				}
			}
		}

		response.setContentType("text/html;charset=UTF-8");
		String path = UploadFileUtils.getUploadPath();	// 获取webapps路径
		File f = new File(path + File.separator + fileName);
		this.info(f.getAbsoluteFile());
		UploadFileUtils.writeFile(f, response, "image/jpeg");
		return;
	}

	@RequestMapping(value = "/count", method = RequestMethod.GET)
	@ApiOperation(value = "我的申请单")
	public Object myCount() {
		int count = incMasterBiz.count(IncMaster.class,StrUtils.createMap("createdBy", this.getLoginUser()));
		return BaseRestControl.tranReturnValue(count);
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

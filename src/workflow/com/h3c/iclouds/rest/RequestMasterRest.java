package com.h3c.iclouds.rest;

import com.h3c.iclouds.base.BaseRest;
import com.h3c.iclouds.base.BaseRestControl;
import com.h3c.iclouds.biz.RequestMasterBiz;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageList;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.dao.Request2ApproveLogDao;
import com.h3c.iclouds.po.business.Request2ApproveLog;
import com.h3c.iclouds.po.business.RequestMaster;
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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/request/master")
@Api(value = "业务办理", description = "业务办理")
public class RequestMasterRest extends BaseRest<RequestMaster> {
	
	@Resource
	private RequestMasterBiz requestMasterBiz;
	
	@Resource
	private Request2ApproveLogDao master2ApproveLogDao;
	
	
	
	@RequestMapping(value = "/list/all", method = RequestMethod.GET)
	@ApiOperation(value = "个人业务办理列表", response = RequestMaster.class)
	public Object listAll() {
		PageEntity entity = this.beforeList();
		if(this.getSessionBean().getSuperUser()) {
			entity.setSpecialParam("list-all");
		}
		PageModel<RequestMaster> pageModel = requestMasterBiz.findForPage(entity);
		PageList<RequestMaster> page = new PageList<RequestMaster>(pageModel, entity.getsEcho());
		return BaseRestControl.tranReturnValue(page);
	}

	@Override
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	@ApiOperation(value = "个人业务办理列表", response = RequestMaster.class)
	public Object list() {
		PageEntity entity = this.beforeList();
		PageModel<RequestMaster> pageModel = requestMasterBiz.findForPage(entity);
		PageList<RequestMaster> page = new PageList<RequestMaster>(pageModel, entity.getsEcho());
		return BaseRestControl.tranReturnValue(page);
	}

	@Override
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@ApiOperation(value = "获取业务办理", response = RequestMaster.class)
	public Object get(@PathVariable String id) {
		RequestMaster entity = requestMasterBiz.findById(RequestMaster.class, id);
		if (entity != null) {
			if(entity.getCreatedBy().equals(this.getSessionBean().getUserId()) || this.getSessionBean().getSuperUser()) {
				List<Request2ApproveLog> approveLogs = this.master2ApproveLogDao.findByMasterId(entity.getId());
				entity.setApproveLogs(approveLogs);
				return BaseRestControl.tranReturnValue(entity);
			}
			return BaseRestControl.tranReturnValue(ResultType.unAuthorized);	
		}
		return BaseRestControl.tranReturnValue(ResultType.deleted);
	}

	@Override
	@ApiOperation(value = "删除业务办理", notes = "只有在还未开始申请才能删除")
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public Object delete(@PathVariable String id) {
		RequestMaster entity = requestMasterBiz.findById(RequestMaster.class, id);
		if(entity != null) {
			if(!entity.getCreatedBy().equals(this.getLoginUser())) {
				return BaseRestControl.tranReturnValue(ResultType.unAuthorized);		
			}
			
			// 只有待提交状态才能进行修改
			String step = entity.getStep();
			if(!step.equals(ConfigProperty.MASTER_STEP_SUBMIT)) {
				return BaseRestControl.tranReturnValue(ResultType.bus_started);
			}
			
			requestMasterBiz.delete(entity);	// 删除审批记录，删除业务申请单
			return BaseRestControl.tranReturnValue(ResultType.success);
		}
		return BaseRestControl.tranReturnValue(ResultType.deleted);
	}
	
	@RequestMapping(value = "/{id}/change", method = RequestMethod.POST)
	@ApiOperation(value = "变更业务办理", response = RequestMaster.class)
	public Object change(@PathVariable String id, @RequestBody Map<String, Object> saveMap) {
		try {
			return BaseRestControl.tranReturnValue(this.requestMasterBiz.saveChange(id, saveMap));
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return BaseRestControl.tranReturnValue(ResultType.parameter_error);
	}
	
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	@ApiOperation(value = "保存业务办理", notes = "通过修改step环节开始业务办理", response = RequestMaster.class)
	public Object save(@RequestBody Map<String, Object> saveMap) {
		try {
			Map<String, String> validateMap = new HashMap<String, String>();
			return BaseRestControl.tranReturnValue(this.requestMasterBiz.save(saveMap, validateMap), validateMap);
		} catch (Exception e1) {
			this.exception(this.getClass(), e1);
		}
		return BaseRestControl.tranReturnValue(ResultType.parameter_error);
	}
	
	@RequestMapping(value = "/xz/save", method = RequestMethod.POST)
	@ApiOperation(value = "续租保存业务办理", notes = "通过修改step环节开始业务办理", response = RequestMaster.class)
	public Object xzsave(@RequestBody Map<String, Object> saveMap) {
		try {
			Map<String, String> validateMap = new HashMap<String, String>();
			//return BaseRestControl.tranReturnValue(this.requestMasterBiz.save(saveMap, validateMap), validateMap);
			//续租处理
			
		} catch (Exception e1) {
			this.exception(this.getClass(), e1);
		}
		return BaseRestControl.tranReturnValue(ResultType.parameter_error);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	@ApiOperation(value = "修改业务办理", notes = "通过修改step环节开始业务办理,只有在还未开始申请才能修改", response = RequestMaster.class)
	public Object update(@PathVariable String id, @RequestBody Map<String, Object> saveMap) throws IOException {
		try {
			Map<String, String> validateMap = new HashMap<String, String>();
			return BaseRestControl.tranReturnValue(this.requestMasterBiz.update(id, saveMap, validateMap), validateMap);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return BaseRestControl.tranReturnValue(ResultType.parameter_error);
	}
	
	@RequestMapping(value = "/{id}/log/{logId}/attachment", method = RequestMethod.GET)
	@ApiOperation(value = "下载附件")
	public void master(@PathVariable String id, @PathVariable String logId, HttpServletRequest request, HttpServletResponse response) {
		RequestMaster entity = requestMasterBiz.findById(RequestMaster.class, id);
		if(entity == null) {
			this.info("申请单被删除");
			response.setStatus(404);
			return;
		}
		Request2ApproveLog log = master2ApproveLogDao.findById(Request2ApproveLog.class, logId);
		if(log == null || !StrUtils.checkParam(log.getAttachment())) {
			this.info("申请单被删除");
			response.setStatus(404);
			return;
		}
		if(!this.getSessionBean().getSuperUser()) {
			// 只有申请单，处理人可以查看附件
			if(!entity.getCreatedBy().equals(this.getLoginUser()) && !log.getCreatedBy().equals(this.getLoginUser())) {
				this.info("权限不足");
				response.setStatus(403);
				return;
			}
		}

		response.setContentType("text/html;charset=UTF-8");
		BufferedInputStream in = null;
		BufferedOutputStream out = null;
		String fileName = log.getAttachment();
		String path = UploadFileUtils.getUploadPath();	// 获取webapps路径
		File f = new File(path + File.separator + fileName);
		this.info(f.getAbsoluteFile());
		if (f.exists()) {
			try {
				String attachment = f.getName().contains("_") ? f.getName().split("_")[1] : f.getName();
				request.setCharacterEncoding("UTF-8");
				response.addHeader("Content-Disposition", "attachment;filename=" + java.net.URLEncoder.encode(attachment, "UTF-8"));
				response.addHeader("Content-Length", String.valueOf(f.length()));
				in = new BufferedInputStream(new FileInputStream(f));
				out = new BufferedOutputStream(response.getOutputStream());
				byte[] data = new byte[1024];
				int len = 0;
				while (-1 != (len = in.read(data, 0, data.length))) {
					out.write(data, 0, len);
				}
				return;
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if (in != null)
						in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				try {
					if (out != null)
						out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		response.setStatus(404);
		return;
	}

	@RequestMapping(value = "/count", method = RequestMethod.GET)
	@ApiOperation(value = "我的申请单")
	public Object myCount() {
		int count = requestMasterBiz.count(RequestMaster.class,StrUtils.createMap("createdBy", this.getLoginUser()));
		return BaseRestControl.tranReturnValue(count);
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

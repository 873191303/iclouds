package com.h3c.iclouds.rest;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.h3c.iclouds.base.BaseRest;
import com.h3c.iclouds.base.BaseRestControl;
import com.h3c.iclouds.biz.WorkFlowBiz;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageList;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.po.WorkFlow;
import com.h3c.iclouds.validate.ValidatorUtils;

@RestController
@RequestMapping("/workFlow")
public class WorkFlowRest extends BaseRest<WorkFlow> {
	
	@Resource
	private WorkFlowBiz workFlowBiz;
	
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public Object list() {
		// 将查询内容存入map
		PageEntity entity = this.beforeList("status");
		PageModel<WorkFlow> pageModel = workFlowBiz.findForPage(entity);
		PageList<WorkFlow> page = new PageList<WorkFlow>(pageModel, entity.getsEcho());
		return BaseRestControl.tranReturnValue(page);
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public Object get(@PathVariable String id) {
		WorkFlow entity = workFlowBiz.findById(WorkFlow.class, id);
		if (entity != null) {
			return BaseRestControl.tranReturnValue(entity);
		}
		return BaseRestControl.tranReturnValue(ResultType.deleted);
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public Object delete(@PathVariable String id) {
		WorkFlow entity = workFlowBiz.findById(WorkFlow.class, id);
		if (entity != null) {
			if (ConfigProperty.WORKFLOW_STATUS1_UNDEPLOY.equals(entity.getStatus())) {
				try {
					workFlowBiz.delete(entity);
					return BaseRestControl.tranReturnValue(ResultType.success);
				} catch (Exception e) {
					this.exception(this.getClass(), e);
					return BaseRestControl.tranReturnValue(ResultType.failure);
				}
			} else {
				return BaseRestControl.tranReturnValue(ResultType.process_delete_error);
			}
		}
		return BaseRestControl.tranReturnValue(ResultType.deleted);
	}
	
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public Object save(@RequestBody WorkFlow entity, HttpServletResponse rep, @RequestParam("upFile") MultipartFile file) throws IOException {
		Map<String, String> validatorMap = ValidatorUtils.validator(entity);
		if(validatorMap.isEmpty()) {
			Map<String, Object> checkRepeatMap = new HashMap<String, Object>();
			checkRepeatMap.put("key", entity.getKey());
			checkRepeatMap.put("version", entity.getVersion());
			if(!this.workFlowBiz.checkRepeat(WorkFlow.class, checkRepeatMap)) {	// 验证key+version是否存在
				return BaseRestControl.tranReturnValue(ResultType.work_flow_exist);
			}
			if (null != file && !file.isEmpty()){	// 上传了流程文件
				String originalFilename = file.getOriginalFilename();
				if(!originalFilename.endsWith(".bpmn")) {	// 是否为工作流文件
					return BaseRestControl.tranReturnValue(ResultType.file_type_error);	// 文件上传出错 
				}
			}
			entity.createdUser(this.getLoginUser());
			try {
				return BaseRestControl.tranReturnValue(workFlowBiz.save(entity, file));
			} catch (Exception e) {
				this.exception(this.getClass(), e);
				return BaseRestControl.tranReturnValue(ResultType.upload_failure);
			}
		}
		return BaseRestControl.tranReturnValue(ResultType.parameter_error);
	}
	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	public Object uploadFile(@RequestParam("file")  MultipartFile file ) {
		String path = request.getSession().getServletContext().getRealPath("upload");
		String fileName=file.getOriginalFilename();
		File targetFile=new File(path, fileName);
		try {
			file.transferTo(targetFile);
		} catch (IllegalStateException | IOException e1) {
			
			 this.exception(MultipartFile.class, e1);
		}
		return workFlowBiz;
		
	}
	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	public Object update(@PathVariable String id, @RequestBody WorkFlow entity, @RequestParam("uploadFile") MultipartFile file) throws IOException{
		Map<String, String> validatorMap = ValidatorUtils.validator(entity);
		if(validatorMap.isEmpty()) {	// 验证是否通过
			Map<String, Object> checkRepeatMap = new HashMap<String, Object>();
			checkRepeatMap.put("key", entity.getKey());
			checkRepeatMap.put("version", entity.getVersion());
			if(!this.workFlowBiz.checkRepeat(WorkFlow.class, checkRepeatMap, entity.getId())) {	// 验证key+version是否存在
				return BaseRestControl.tranReturnValue(ResultType.work_flow_exist);
			}
			
			if (null != file && !file.isEmpty()){	// 上传了流程文件
				String originalFilename = file.getOriginalFilename();
				if(!originalFilename.endsWith(".bpmn")) {	// 是否为工作流文件
					return BaseRestControl.tranReturnValue(ResultType.file_type_error);	// 文件上传出错 
				}
			}
			
			WorkFlow before = workFlowBiz.findById(WorkFlow.class, id);
			if(before != null) {
				try {
					return BaseRestControl.tranReturnValue(workFlowBiz.update(entity, before, file));
				} catch (Exception e) {
					this.exception(this.getClass(), e);
					return BaseRestControl.tranReturnValue(ResultType.upload_failure);
				}
			}
			return BaseRestControl.tranReturnValue(ResultType.deleted);
		}
		return BaseRestControl.tranReturnValue(ResultType.parameter_error);
	}
	
	@RequestMapping(value = "/deploy/{id}", method = RequestMethod.POST)
	public Object deploy(HttpServletRequest req, HttpServletResponse rep, @PathVariable String id) throws IOException{
		WorkFlow entity = workFlowBiz.findById(WorkFlow.class, id);
		if(entity != null) {
			if(ConfigProperty.WORKFLOW_STATUS1_UNDEPLOY.equals(entity.getStatus())) {
				try {
					return BaseRestControl.tranReturnValue(this.workFlowBiz.deploy(entity));
				} catch (Exception e) {
					this.exception(this.getClass(), e);
					return BaseRestControl.tranReturnValue(ResultType.failure);
				}
			} else {
				return BaseRestControl.tranReturnValue(ResultType.workflow_flow_deployed);
			}
		}
		return BaseRestControl.tranReturnValue(ResultType.deleted);
	}
	

	@RequestMapping(value = "/undeploy/{id}", method = RequestMethod.POST)
	public Object undeploy(HttpServletRequest req, HttpServletResponse rep, @PathVariable String id) throws IOException{
		WorkFlow entity = workFlowBiz.findById(WorkFlow.class, id);
		if(entity != null) {
			if(ConfigProperty.WORKFLOW_STATUS2_DEPLOY.equals(entity.getStatus())) {
				try {
					return BaseRestControl.tranReturnValue(this.workFlowBiz.undeploy(entity));
				} catch (Exception e) {
					this.exception(this.getClass(), e);
					return BaseRestControl.tranReturnValue(ResultType.failure);
				}
			} else {
				return BaseRestControl.tranReturnValue(ResultType.workflow_flow_deployed);
			}
		}
		return BaseRestControl.tranReturnValue(ResultType.deleted);
	}
	
	@Override
	public Object save(WorkFlow t) {
		return null;
	}

	@Override
	public Object update(String id, WorkFlow t) {
		return null;
	}

}

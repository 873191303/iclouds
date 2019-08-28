package com.h3c.iclouds.rest;

import com.h3c.iclouds.base.BaseRestControl;
import com.h3c.iclouds.biz.ApplicationMasterBiz;
import com.h3c.iclouds.biz.ProjectBiz;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageList;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.po.ApplicationMaster;
import com.h3c.iclouds.utils.JacksonUtil;
import com.h3c.iclouds.utils.StrUtils;
import com.h3c.iclouds.utils.UploadFileUtils;
import com.h3c.iclouds.validate.ValidatorUtils;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

@Api(value = "云运维应用配置信息", description = "云运维应用配置信息")
@RestController
@RequestMapping("/app")
public class ApplicationMasterRest extends BaseRestControl {

	@Resource
	private ApplicationMasterBiz applicationMasterBiz;
	
	@Resource
	private ProjectBiz projectBiz;
	
	@ApiOperation(value = "获取应用配置列表", response = ApplicationMaster.class)
	@RequestMapping(method = RequestMethod.GET)
	public Object list() {
		PageEntity entity = this.beforeList();
		PageModel<ApplicationMaster> pageModel = applicationMasterBiz.findForPage(entity);
		PageList<ApplicationMaster> page = new PageList<>(pageModel, entity.getsEcho());
		return BaseRestControl.tranReturnValue(page);
	}
	
	@ApiOperation(value = "新增应用配置", response = ApplicationMaster.class)
	@RequestMapping(value = "/file", method = RequestMethod.POST)
	public Object save(HttpServletRequest request) {
//		if (!getSessionBean().getSuperRole()) {//判断权限
//			return BaseRestControl.tranReturnValue(ResultType.unAuthorized);
//		}
		String projectId = getSessionBean().getProjectId();
		if (!StrUtils.checkParam(projectBiz.get(projectId))) {
			return BaseRestControl.tranReturnValue(ResultType.tenant_not_exist);
		}
		String appName =  request.getParameter("appName");
		Map<String, Object> checkMap = new HashMap<>();
		checkMap.put("appName", appName);
		checkMap.put("projectId", this.getProjectId());
		if (!applicationMasterBiz.checkRepeat(ApplicationMaster.class, checkMap, null)){//查重(名称)
			return BaseRestControl.tranReturnValue(ResultType.repeat);
		}
		String url =  request.getParameter("url");
		String remark =  request.getParameter("remark");
		ApplicationMaster applicationMaster = new ApplicationMaster(appName, url, remark);
		Map<String, String> validatorMap = ValidatorUtils.validator(applicationMaster);
		if (!validatorMap.isEmpty()) {//校验参数
			return BaseRestControl.tranReturnValue(ResultType.parameter_error, validatorMap);
		}
		File resultFile = null;
		boolean flag = false;
		if (request instanceof MultipartHttpServletRequest) {
			try {
				//转型为MultipartHttpRequest
				MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
				Map<String, MultipartFile> map = multipartRequest.getFileMap();
				//获得图片（得到上传的文件）
				MultipartFile file = null;
				if (StrUtils.checkParam(map)) {
					for (String s : map.keySet()) {
						file = map.get(s);
					}
				}
				long size = 0;
				if (null != file && !file.isEmpty()) {
					if (!UploadFileUtils.checkPicture(file)) {
						return BaseRestControl.tranReturnValue(ResultType.file_type_error);
					}
					// 单位为B
					size = file.getSize();
					final long MAX_SIZE = 2 * 1024 * 1024;	// 2M上限
					if(size > MAX_SIZE) {
						return BaseRestControl.tranReturnValue(ResultType.file_size_than_max);
					}
					resultFile = UploadFileUtils.uploadAttathmentFile(file);
					this.info("Upload file name：" + resultFile.getName() + "\tsize:" + size);
					String fileName = resultFile.getName();
					applicationMaster.setIconPath(fileName);
					flag = true;
				} else {
					applicationMaster.setIconPath(ConfigProperty.APP_ICON_NAME);
				}
			} catch (Exception e) {
				this.exception(ApplicationMaster.class, e, "上传图标文件失败");
				return BaseRestControl.tranReturnValue(ResultType.file_upload_error);
			}
		}
		try {
			ResultType rs = applicationMasterBiz.save(applicationMaster);
			return BaseRestControl.tranReturnValue(rs);
		} catch (Exception e) {
			if (null != resultFile && flag) {
				UploadFileUtils.deleteFile(resultFile);//删除文件
			}
			this.exception(ApplicationMaster.class, e,"新增应用失败输入的参数："+JacksonUtil.toJSon(applicationMaster));
			return BaseRestControl.tranReturnValue(ResultType.failure);
		}
	}
	
	@ApiOperation(value = "修改应用配置", response = ApplicationMaster.class)
	@RequestMapping(value = "/{appId}/file", method = RequestMethod.POST)
	public Object update(@PathVariable String appId, HttpServletRequest request) {
		String projectId = getSessionBean().getProjectId();
		if (!StrUtils.checkParam(projectBiz.get(projectId))) {
			return BaseRestControl.tranReturnValue(ResultType.tenant_not_exist);
		}
		ApplicationMaster master = applicationMasterBiz.findById(ApplicationMaster.class, appId);
		if (!StrUtils.checkParam(master)) {
			return BaseRestControl.tranReturnValue(ResultType.deleted);
		}
		if (!projectBiz.checkOptionRole(master.getProjectId(), master.getOwner())) {
			return BaseRestControl.tranReturnValue(ResultType.unAuthorized);
		}
		String appName =  request.getParameter("appName");
		String url =  request.getParameter("url");
		String remark =  request.getParameter("remark");
		String type =  request.getParameter("flag");
		String fileName = master.getIconPath();
		if (StrUtils.checkParam(appName)) {
			Map<String, Object> checkMap = new HashMap<>();
			checkMap.put("appName", appName);
			checkMap.put("projectId", this.getProjectId());
			if (!applicationMasterBiz.checkRepeat(ApplicationMaster.class, checkMap, appId)){//查重(名称)
				return BaseRestControl.tranReturnValue(ResultType.repeat);
			}
			master.setAppName(appName);
		}
		master.setUrl(url);
		master.setRemark(remark);
		Map<String, String> validatorMap = ValidatorUtils.validator(master);
		if (!validatorMap.isEmpty()) {//校验参数
			return BaseRestControl.tranReturnValue(ResultType.parameter_error, validatorMap);
		}
		File resultFile = null;
		boolean flag = false;
		if ("0".equals(type)) {
			if (request instanceof MultipartHttpServletRequest) {
				try {
					//转型为MultipartHttpRequest
					MultipartHttpServletRequest multipartRequest  =  (MultipartHttpServletRequest) request;
					//获得图片（根据前台的name名称得到上传的文件）
					Map<String, MultipartFile> map = multipartRequest.getFileMap();
					//获得图片（得到上传的文件）
					MultipartFile file = null;
					if (StrUtils.checkParam(map)) {
						for (String s : map.keySet()) {
							file = map.get(s);
						}
					}
					long size = 0;
					if (null != file && !file.isEmpty()) {
						// 单位为B
						size = file.getSize();
						final long MAX_SIZE = 2 * 1024 * 1024;	// 2M上限
						if(size > MAX_SIZE) {
							return BaseRestControl.tranReturnValue(ResultType.file_size_than_max);
						}
						if (!UploadFileUtils.checkPicture(file)) {
							return BaseRestControl.tranReturnValue(ResultType.file_type_error);
						}
						resultFile = UploadFileUtils.uploadAttathmentFile(file);
						this.info("Upload file name：" + resultFile.getName() + "\tsize:" + size);
						master.setIconPath(resultFile.getName());
						flag = true;
					} else {
						master.setIconPath(ConfigProperty.APP_ICON_NAME);
					}
				} catch (Exception e) {
					this.exception(ApplicationMaster.class, e, "上传图标文件失败");
					return BaseRestControl.tranReturnValue(ResultType.file_upload_error);
				}
			}
		}
		try {
			applicationMasterBiz.updateApp(master);
			File oldFile = UploadFileUtils.getUploadFile(fileName);
			if (null != oldFile && "0".equals(type)) {
				UploadFileUtils.deleteFile(oldFile);
			}
			return BaseRestControl.tranReturnValue(ResultType.success);
		} catch (Exception e) {
			if (null != resultFile && flag) {
				UploadFileUtils.deleteFile(resultFile);
			}
			this.exception(ApplicationMaster.class, e,"修改应用失败输入的参数：" + JacksonUtil.toJSon(master));
			return BaseRestControl.tranReturnValue(ResultType.failure);
		}
	}
	
	@ApiOperation(value = "删除应用配置", response = ApplicationMaster.class)
	@RequestMapping(value = "/{appId}",method = RequestMethod.DELETE)
	public Object delete(@PathVariable String appId) {
		ApplicationMaster master = applicationMasterBiz.findById(ApplicationMaster.class, appId);
		if (!StrUtils.checkParam(master)) {
			return BaseRestControl.tranReturnValue(ResultType.deleted);
		}
		if (!projectBiz.checkOptionRole(master.getProjectId(), master.getOwner())) {
			return BaseRestControl.tranReturnValue(ResultType.unAuthorized);
		}
		String fileName = master.getIconPath();
		ResultType result = null;
		try {
			result = applicationMasterBiz.delete(appId);
			if (ResultType.success.equals(result)) {
				File file = UploadFileUtils.getUploadFile(fileName);
				UploadFileUtils.deleteFile(file);
			}
			return BaseRestControl.tranReturnValue(result);
		} catch (Exception e) {
			this.exception(ApplicationMaster.class, e ,result);
			return BaseRestControl.tranReturnValue(ResultType.failure);
		}
		
	}
	
	@ApiOperation(value = "获取应用图标")
	@RequestMapping(value = "/{appId}/file", method = RequestMethod.GET)
	public void getIcon(@PathVariable String appId, HttpServletResponse response){
		ApplicationMaster master = applicationMasterBiz.findById(ApplicationMaster.class, appId);
		if (!StrUtils.checkParam(master)) {
			return;
		}
		if (!projectBiz.checkOptionRole(master.getProjectId(), master.getOwner())) {
			return;
		}
		String fileName = master.getIconPath();
		File file = UploadFileUtils.getUploadFile(fileName);
		if (null != file && file.exists()) {
			this.info("Get App Icon, Icon Name:" + file.getAbsoluteFile());
		} else {
			file = UploadFileUtils.getFile(fileName);
		}
		UploadFileUtils.writeFile(file, response, "image/jpeg");
		return;
	}
	
	@ApiOperation(value = "检查名称是否重复")
	@RequestMapping(value = "/check/{name}", method = RequestMethod.GET)
	public Object checkRepeat(@PathVariable String name){
		boolean appRepeat = false;
		String id = request.getParameter("id");//修改时传入一个id则查重时会排除对象本身
		try {
			Map<String, Object> checkMap = new HashMap<>();
			checkMap.put("appName", name);
			checkMap.put("projectId", this.getProjectId());
			appRepeat = applicationMasterBiz.checkRepeat(ApplicationMaster.class, checkMap, id);
			if (!appRepeat){//查重(名称)
				return BaseRestControl.tranReturnValue(ResultType.repeat);
			}
			return BaseRestControl.tranReturnValue(ResultType.success);
		} catch (Exception e) {
			return BaseRestControl.tranReturnValue(ResultType.failure);
		}
	}
	
	@ApiOperation(value = "租户拥有应用数量")
	@RequestMapping(value = "/count", method = RequestMethod.GET)
	public Object countByUser(){
		int count=0;
		if(this.getSessionBean().getSuperUser()){
			count=applicationMasterBiz.count(ApplicationMaster.class, null);
		}else {
			count=applicationMasterBiz.count(ApplicationMaster.class, StrUtils.createMap("projectId",this.getProjectId()));
		}
		return BaseRestControl.tranReturnValue(count);
	}
}

package com.h3c.iclouds.rest;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.h3c.iclouds.base.BaseRest;
import com.h3c.iclouds.base.BaseRestControl;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.common.SimpleCache;
import com.h3c.iclouds.common.UploadFileModal;
import com.h3c.iclouds.po.User;
import com.h3c.iclouds.utils.RedisUtils;
import com.h3c.iclouds.utils.StrUtils;
import com.h3c.iclouds.utils.UploadFileUtils;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/")
@Api(value = "文件操作")
public class FileRest extends BaseRest<User> {
	
	public static final long FILE_TIMEOUT = 30 * 60 * 1000;	// 30分钟
	
	@ApiOperation(value = "获取上传附件key, type: ewo")
	@RequestMapping(value = "/file/{type}/key", method = RequestMethod.GET)
	public Object get(@PathVariable String type, HttpServletRequest request) {
		String fileKey = request.getParameter("fileKey");
		// 先删除原本的附件
		if(StrUtils.checkParam(fileKey)) {
			UploadFileModal modal = SimpleCache.UPLOAD_FILE_MAP.get(fileKey);
			UploadFileUtils.deleteFile(fileKey, modal);
		}
		String key = this.getSessionBean().getFileKey(type);
		RedisUtils.set(key, type, FILE_TIMEOUT);	// 写入到redis
		return BaseRestControl.tranReturnValue(key);
	}
	
	@ApiOperation(value = "上传附件")
	@RequestMapping(value = "/file/upload/{key}", method = RequestMethod.POST)
	public Object upload(@PathVariable String key, HttpServletRequest request) {
		UploadFileModal ufm = SimpleCache.UPLOAD_FILE_MAP.get(key);
		if(null != ufm && ufm.getToken().equals(this.getUserToken()) && RedisUtils.get(key) != null) {
			ResultType result = null;
			try {
				UploadFileModal entity = SimpleCache.UPLOAD_FILE_MAP.get(key);
				// 检查附件详情
				if(entity == null || entity.getFileNames().size() > 2) {
					result = ResultType.file_than_max;
					return BaseRestControl.tranReturnValue(ResultType.file_than_max);
				}
				MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
				Map<String, MultipartFile> map = multipartRequest.getFileMap();
				MultipartFile file = null;
				for (String fileKey : map.keySet()) {
					file = map.get(fileKey);
					break;
				}
				// 单位为B
				long size = file.getSize();
				final long MAX_SIZE = 3 * 1024 * 1024;	// 3M上限
				if(size > MAX_SIZE) {
					result = ResultType.file_size_than_max;
					return BaseRestControl.tranReturnValue(ResultType.file_size_than_max);
				}
				this.info("Upload file name：" + file.getOriginalFilename() + "\tsize:" + size);
				File resultFile = UploadFileUtils.uploadAttathmentFile(file);
				entity.addFileName(resultFile.getName());
				RedisUtils.expire(key, FILE_TIMEOUT);// 重新设置超时时长
			} catch (Exception e) {
				this.exception(this.getClass(), e, "Upload file error");
			} finally {
				// 上传文件异常则删除文件
				if(result != null) {
					UploadFileModal modal = SimpleCache.UPLOAD_FILE_MAP.get(key);
					UploadFileUtils.deleteFile(key, modal);
				}
			}
			return BaseRestControl.tranReturnValue(ResultType.success);
		}
		return BaseRestControl.tranReturnValue(ResultType.file_upload_error);
	}
	
	@Override
	public Object list() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object save(User entity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object update(String id, User entity) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object delete(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object get(String id) {
		// TODO Auto-generated method stub
		return null;
	}

}

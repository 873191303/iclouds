package com.h3c.iclouds.rest;

import com.alibaba.fastjson.JSONObject;
import com.h3c.iclouds.auth.CacheSingleton;
import com.h3c.iclouds.auth.ThirdPart;
import com.h3c.iclouds.auth.ThirdPartEnum;
import com.h3c.iclouds.base.BaseRest;
import com.h3c.iclouds.base.BaseRestControl;
import com.h3c.iclouds.biz.DepartmentBiz;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.exception.MessageException;
import com.h3c.iclouds.po.Department;
import com.h3c.iclouds.po.Project;
import com.h3c.iclouds.utils.PwdRSAUtils;
import com.h3c.iclouds.utils.SessionRedisUtils;
import com.h3c.iclouds.utils.StrUtils;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author Administrator
 *
 */
@Api(value = "获取租户信息", description = "获取租户信息")
@RestController
@RequestMapping("/")
@ThirdPart(ThirdPartEnum.EISOO)
public class TestProjectRest extends BaseRest<Project> {
	
	private static final String APPCODE = "AISHU";

	/** 密钥文件路径 */
	private static final String PRIVATEPATH = "encrypt/h3crypto.pri";
	private static final String PUBLICPATH = "encrypt/h3crypto.pub";
	
	public static String userId = CacheSingleton.getInstance().getConfigMap().get("iclouds.abc.api.username.name");

	@Resource
	private DepartmentBiz departmentBiz;
	
	@ApiOperation(value = "验证凭证")
	@RequestMapping(value = "/v2/auth/token", method = RequestMethod.POST)
	public Object getToken(@RequestBody JSONObject map) {
		// 获取系统名称
		String path = null;
		String path1 = this.getClass().getResource("/").getPath();
		CacheSingleton singleTon = CacheSingleton.getInstance();
		String name = singleTon.getOsName();
		if(name.toLowerCase().indexOf("window") > -1) {
			path = path1.substring(1);
		} else {
			path = path1;
		}
		System.out.println("============>" + path);
		
		// 密钥文件路径获取
		String pubPath = (path + PUBLICPATH);
		String priPath = (path + PRIVATEPATH);
		
		String appCode = StrUtils.tranString(map.get("AppCode"));
		appCode = APPCODE;
		if (appCode == null || !APPCODE.equals(appCode)) {
			//TODO ...log
			MessageException e = new MessageException("appCode 错误");
			this.exception(clazz, e);
			return BaseRestControl.tranReturnValue(ResultType.parameter_error);  // appCode error
		}

		// 公钥加密的后的信息
		String requestInfo = StrUtils.tranString(map.get("RequestInfo"));
		
		PwdRSAUtils util = new PwdRSAUtils(priPath, pubPath);
		String plainInfo = null;
		try {
			plainInfo = util.decryptByPri(requestInfo);
		} catch (Exception e) {
			this.exception(this.getClass(), e);
			return BaseRestControl.tranReturnValue(ResultType.failure);
		}

		String verify = CacheSingleton.getInstance().getConfigMap().get("iclouds.abc.get.token");
		plainInfo = verify;
		if(verify.equals(plainInfo)) {
			Map<String, Object> resultMap = new HashMap<String, Object>();
			String tokenKey = StrUtils.getUUID(ConfigProperty.PROJECT_TOKEN_ABC_PROFIX);
			resultMap.put(ConfigProperty.PROJECT_TOKEN_KEY, tokenKey);
			//  保存到session中
			SessionRedisUtils.setValue2Redis(tokenKey, new JSONObject(resultMap));
			return BaseRestControl.tranReturnValue(ResultType.success, resultMap);
		}
		
		this.info("爱数获取token校验失败");
		return BaseRestControl.tranReturnValue(ResultType.parameter_error);
	}

	@ApiOperation(value = "获取部门列表")
	@RequestMapping(value = "/v2/deptment", method = RequestMethod.GET)
	public Map<String, Object> deptmentList() {
		List<Department> lists = departmentBiz.getAll(Department.class);
		Map<String, Map<String, Object>> resultMap = new HashMap<>();
		for (Department department : lists) {
			// 查看租户id是否在map中存在
			String tenantId = department.getProjectId();
			if (!resultMap.containsKey(tenantId)) {
				Map<String, Object> deptMap = new HashMap<>();
				List<Map<String, String>> deptLists = new ArrayList<>();
				deptMap.put("TenantId", tenantId);
				deptMap.put("Deptment", deptLists);
				resultMap.put(tenantId, deptMap);
			} 
			
			Map<String, String> deptInfo = new HashMap<String, String> ();
			deptInfo.put("DeptID", department.getId()); 
			deptInfo.put("DeptName", department.getDeptName());
			deptInfo.put("ParentDeptCode", department.getParentId());		
			
			Map<String, Object> temp = resultMap.get(tenantId);
			@SuppressWarnings("unchecked")
			List<Map<String, String>> tempList =  (List<Map<String, String>>)temp.get("Deptment");
			tempList.add(deptInfo);
		}
		
		return BaseRestControl.tranReturnValue(ResultType.success, resultMap.values());
	}

	@Override
	public Object list() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object get(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object delete(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object save(Project entity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object update(String id, Project entity) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

}

package com.h3c.iclouds.rest;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.h3c.iclouds.auth.CacheSingleton;
import com.h3c.iclouds.auth.Perms;
import com.h3c.iclouds.base.BaseRestControl;
import com.h3c.iclouds.biz.SecurityGroupBiz;
import com.h3c.iclouds.operate.CloudosClient;
import com.h3c.iclouds.operate.CloudosParams;
import com.h3c.iclouds.utils.HttpUtils;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/security")
@Api(value = "安全组", description = "安全组操作")
public class SecurityGroupRest extends BaseRestControl{
	
	@Resource
	private SecurityGroupBiz securityGroupBiz;
	
	@RequestMapping(value = "/updategroup/{groupId}", method = RequestMethod.PUT)
	@Perms(value = "sm.user.list")
	@ApiOperation(value = "修改指定安全组")
	public Object update(@PathVariable String groupId) {
		CloudosClient client = this.getSessionBean().getCloudClient();
		String uri = CacheSingleton.getInstance().getCloudosApi(CloudosParams.CLOUDOS_API_GROUP_SECURITY);
		uri = HttpUtils.tranUrl(uri, groupId);
		Map<String, Object> params = groupParamsCreate();
		JSONObject serverArray = client.put(uri, params);
		return BaseRestControl.tranReturnValue(serverArray);
	}
	
	@RequestMapping(value = "/creategroup", method = RequestMethod.POST)
	@Perms(value = "sm.user.list")
	@ApiOperation(value = "创建安全组")
	public Object create() {
		CloudosClient client = this.getSessionBean().getCloudClient();
		String uri = CacheSingleton.getInstance().getCloudosApi(CloudosParams.CLOUDOS_API_SECURITY);
		uri = HttpUtils.tranUrl(uri, this.getProjectId());
		Map<String, Object> params = groupParamsCreate();
		JSONObject serverArray = client.put(uri, params);
		return BaseRestControl.tranReturnValue(serverArray);
	}
	
	@RequestMapping(value = "/listgroup", method = RequestMethod.GET)
	@Perms(value = "sm.user.list")
	@ApiOperation(value = "查询所有安全组")
	public Object list() {
		CloudosClient client = this.getSessionBean().getCloudClient();
		String uri = CacheSingleton.getInstance().getCloudosApi(CloudosParams.CLOUDOS_API_SECURITY);
		uri = HttpUtils.tranUrl(uri, this.getProjectId());
		//Map<String, Object> params = groupParamsCreate();
		//uri = HttpUtils.tranUrl(uri, groupId);
		JSONObject serverArray = client.get(uri);
		return BaseRestControl.tranReturnValue(serverArray);
	}
	
	@RequestMapping(value = "/listgroup/{groupId}", method = RequestMethod.GET)
	@Perms(value = "sm.user.list")
	@ApiOperation(value = "查询指定安全组")
	public Object listgroup(@PathVariable String groupId) {
		CloudosClient client = this.getSessionBean().getCloudClient();
		String uri = CacheSingleton.getInstance().getCloudosApi(CloudosParams.CLOUDOS_API_GROUP_SECURITY);
		uri = HttpUtils.tranUrl(uri, groupId);
		JSONObject serverArray = client.get(uri);
		return BaseRestControl.tranReturnValue(serverArray);
	}
	
	@RequestMapping(value = "/updategroup/{groupId}", method = RequestMethod.DELETE)
	@Perms(value = "sm.user.list")
	@ApiOperation(value = "删除安全组")
	public Object listFlavor(@PathVariable String groupId) {
		CloudosClient client = this.getSessionBean().getCloudClient();
		String uri = CacheSingleton.getInstance().getCloudosApi(CloudosParams.CLOUDOS_API_GROUP_SECURITY);
		uri = HttpUtils.tranUrl(uri, groupId);
		JSONObject serverArray = client.delete(uri);
		return BaseRestControl.tranReturnValue(serverArray);
	}
	
	private Map<String, Object> groupParamsCreate(){
		Map<String, Object> group = new HashMap<>();
		Map<String, Object> params = new HashMap<>();
		params.put("description", "hello group");
		params.put("name", "Test123");
		group.put("security_group", params);
		return group;
	}
	
}

package com.h3c.iclouds.rest;

import com.h3c.iclouds.base.BaseDAO;
import com.h3c.iclouds.base.BaseRest;
import com.h3c.iclouds.base.BaseRestControl;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.operate.CloudosClient;
import com.h3c.iclouds.operate.CloudosImage;
import com.h3c.iclouds.po.Rules;
import com.h3c.iclouds.utils.StrUtils;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/rules")
@Api(value = "资源配置规则", description = "资源配置规则表")
public class RulesRest extends BaseRest<Rules> {
	
	@Resource
	public BaseDAO<Rules> baseDAO;
	
	@Override
	@ApiOperation(value = "规则列表", response = Rules.class)
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public Object list () {
		String tenantId = request.getParameter("projectId");
		if (this.getSessionBean().getSuperUser()) {//管理员获取自己的列表，非管理员获取自己和根租户的列表
			if (!StrUtils.checkParam(tenantId)) {
				tenantId = this.getProjectId();
			}
		} else {
			tenantId = this.getProjectId();
		}
		Map<String, Object> params = new HashMap<>();
		params.put("isDefault", "0");
		params.put("tenantId", new String[] {tenantId, singleton.getRootProject()});
		List<Rules> list = this.baseDAO.listByClass(Rules.class, params);
		if ("1".equals(singleton.getConfigValue("imageflag"))) {
			//过滤image
			CloudosClient client = getSessionBean().getCloudClient();
			CloudosImage image = new CloudosImage(client);
			list = image.get(list);
		}
		return BaseRestControl.tranReturnValue(list);
	}
	
	@ApiOperation(value = "检查名称是否重复")
	@RequestMapping(value = "/check/{name}", method = RequestMethod.GET)
	public Object checkRepeat (@PathVariable String name) {
		boolean userNameRepeat = baseDAO.checkRepeat(Rules.class, "osMirName", name);
		if (!userNameRepeat) {// 查重(用户名称)
			return BaseRestControl.tranReturnValue(ResultType.repeat);
		}
		return BaseRestControl.tranReturnValue(ResultType.success);
	}
	
	@ApiOperation(value = "镜像列表;0--预定义镜像；1--自定义镜像", response = Rules.class)
	@RequestMapping(value = "/list/{type}", method = RequestMethod.GET)
	public Object defaultList (@PathVariable String type) {
		String tenantId = request.getParameter("projectId");
		if (this.getSessionBean().getSuperUser()) {//管理员获取租户的列表，非管理员只能获取自己的列表
			if (!StrUtils.checkParam(tenantId)) {
				tenantId = this.getProjectId();
			}
		} else {
			tenantId = this.getProjectId();
		}
		Map<String, Object> params = new HashMap<>();
		params.put("isDefault", "0");
		params.put("definition", type);
		params.put("tenantId", new String[] {tenantId, singleton.getRootProject()});
		List<Rules> list = this.baseDAO.listByClass(Rules.class, params);
		return BaseRestControl.tranReturnValue(list);
	}
	
	@Override
	@ApiOperation(value = "获取规则详细信息", response = Rules.class)
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public Object get (String id) {
		Rules entity = this.baseDAO.findById(Rules.class, id);
		if (entity != null) {
			return BaseRestControl.tranReturnValue(entity);
		}
		return BaseRestControl.tranReturnValue(ResultType.deleted);
	}
	//模拟数据以后要删除
//	private List<Rules> my_test_init_Rules(List<Rules> beans){
//		if(beans==null || beans.size() == 0) {
//			beans = new Array
//		}
//	}
	
	@Override
	public Object delete (String id) {
		return null;
	}
	
	@Override
	public Object save (Rules entity) {
		return null;
	}
	
	@Override
	public Object update (String id, Rules entity) throws IOException {
		return null;
	}
	
	
}
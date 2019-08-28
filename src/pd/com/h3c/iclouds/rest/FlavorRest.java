package com.h3c.iclouds.rest;

import com.alibaba.fastjson.JSONObject;
import com.h3c.iclouds.auth.CacheSingleton;
import com.h3c.iclouds.base.BaseDAO;
import com.h3c.iclouds.base.BaseRestControl;
import com.h3c.iclouds.base.SqlQueryBiz;
import com.h3c.iclouds.biz.AzoneBiz;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageList;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.common.SqlQueryProperty;
import com.h3c.iclouds.dao.NovaFlavorDao;
import com.h3c.iclouds.operate.CloudosClient;
import com.h3c.iclouds.operate.CloudosFlavor;
import com.h3c.iclouds.po.Azone;
import com.h3c.iclouds.po.NovaFlavor;
import com.h3c.iclouds.po.Rules;
import com.h3c.iclouds.utils.ResourceHandle;
import com.h3c.iclouds.utils.HttpUtils;
import com.h3c.iclouds.utils.LogUtils;
import com.h3c.iclouds.utils.StrUtils;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(value = "云主机资源配置规格", description = "云主机资源配置规格")
@RestController
@RequestMapping("/flavor")
public class FlavorRest extends BaseRestControl {
	
	@Resource(name = "baseDAO")
	private BaseDAO<Rules> rulesDao;
	
	@Resource
	private NovaFlavorDao novaFlavorDao;
	
	@Resource
	private SqlQueryBiz sqlQueryBiz;
	
	@Resource
	private AzoneBiz azoneBiz;
	
	@ApiOperation(value = "获取云主机规格分页列表", response = NovaFlavor.class)
	@RequestMapping(method = RequestMethod.GET)
	public Object subList () {
		PageEntity entity = this.beforeList();
		PageModel<NovaFlavor> pageModel = novaFlavorDao.findForPage(entity);
		PageList<NovaFlavor> pageList = new PageList<>(pageModel, entity.getsEcho());
		return BaseRestControl.tranReturnValue(pageList);
	}
	
	@ApiOperation(value = "获取未定价和已定价的云主机规格分页列表", response = NovaFlavor.class)
	@RequestMapping(value = "/{azoneId}/{type}", method = RequestMethod.GET)
	public Object listByAzone (@PathVariable String azoneId, @PathVariable  String type) {
		List<String> flavorIds = getFlavorIds(azoneId, type);
		PageEntity entity = this.beforeList();
		entity.setSpecialParams(flavorIds.toArray(new String[flavorIds.size()]));
		PageModel<NovaFlavor> pageModel = novaFlavorDao.findForPage(entity);
		PageList<NovaFlavor> pageList = new PageList<>(pageModel, entity.getsEcho());
		return BaseRestControl.tranReturnValue(pageList);
	}
	
	@ApiOperation(value = "资源配置组合规格列表")
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public Object list () {
		List<NovaFlavor> list = novaFlavorDao.get();
		if ("1".equals(CacheSingleton.getInstance().getConfigValue("novaflavorflag"))) {
			CloudosClient client = getSessionBean().getCloudClient();
			CloudosFlavor cloudosFlavor = new CloudosFlavor(client);
			list = cloudosFlavor.get(getProjectId(), list);
		}
		return BaseRestControl.tranReturnValue(list);
	}
	
	@ApiOperation(value = "获取资源配置组合规格详细信息")
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public Object get (@PathVariable String id) {
		NovaFlavor entity = novaFlavorDao.findById(NovaFlavor.class, id);
		if (entity != null) {
			if (ConfigProperty.YES.equals(entity.getIsPublic()))
				return BaseRestControl.tranReturnValue(entity);
		}
		return BaseRestControl.tranReturnValue(ResultType.deleted);
	}
	
	@ApiOperation(value = "获取资源配置组合规格详细信息")
	@RequestMapping(value = "/image/{id}", method = RequestMethod.GET)
	public Object getFlavor (@PathVariable String id) {
		Rules entity = rulesDao.findById(Rules.class, id);
		if (entity != null) {
			NovaFlavor novaFlavor = novaFlavorDao.getByImage(entity);
			if (StrUtils.checkParam(novaFlavor)) {
				return BaseRestControl.tranReturnValue(novaFlavor);
			} else {
				return BaseRestControl.tranReturnValue(ResultType.flavor_not_exist);
			}
		}
		return BaseRestControl.tranReturnValue(ResultType.image_not_exist);
	}
	
	@ApiOperation(value = "新增云主机规格", response = NovaFlavor.class)
	@RequestMapping(method = RequestMethod.POST)
	public Object save (@RequestBody NovaFlavor entity) {
		if (!this.getSessionBean().getSuperUser()) {
			return BaseRestControl.tranReturnValue(ResultType.unAuthorized);
		}
		int vcpus = entity.getVcpus();
		int ram = entity.getRam();
		int disk = entity.getDisk();
		Map<String, Object> param = new HashMap<>();
		Map<String, Object> flavorMap = new HashMap<>();
		flavorMap.put("vcpus", vcpus);
		flavorMap.put("ram", ram * 1024);
		flavorMap.put("disk", disk);
		flavorMap.put("name", vcpus + "*" + ram + "*" + disk);
		//flavorMap.put("diskType", entity.getDiskType());
		List<NovaFlavor> novaFlavors = novaFlavorDao.listByClass(NovaFlavor.class, flavorMap);
		if (StrUtils.checkCollection(novaFlavors)) {
			return BaseRestControl.tranReturnValue(ResultType.flavor_already_exist);
		}
		param.put("flavor", flavorMap);
		CloudosClient client = this.getSessionBean().getCloudClient();
		if (null == client) {
			return BaseRestControl.tranReturnValue(ResultType.system_error);
		}
		try {
			JSONObject jsonObject = client.post("/v2/" + client.getTenantId() + "/flavors", param);
			if (!ResourceHandle.judgeResponse(jsonObject)) {
				return BaseRestControl.tranReturnValue(ResultType.cloudos_exception, HttpUtils.getError(jsonObject));
			}
			jsonObject = jsonObject.getJSONObject("record").getJSONObject("flavor");
			String flavorId = jsonObject.getString("id");
			entity.setRam(ram * 1024);
			entity.setName(vcpus + "*" + ram + "*" + disk);
			entity.setId(flavorId);
			entity.createdUser(this.getLoginUser());
			novaFlavorDao.add(entity);
			return BaseRestControl.tranReturnValue(ResultType.success);
		} catch (Exception e) {
			LogUtils.exception(NovaFlavor.class, e);
			return BaseRestControl.tranReturnValue(ResultType.failure);
		}
	}
	
	/**
	 * 获取未定价和已定价的
	 * @param azoneId
	 * @param type
	 * @return
	 */
	private List<String> getFlavorIds(String azoneId, String type) {
		List<String> flavorIds = new ArrayList<>();
		Azone azone = azoneBiz.findById(Azone.class, azoneId);
		if (!StrUtils.checkParam(azone) || !"nova".equals(azone.getResourceType())) {
			flavorIds.add("null");
			return flavorIds;
		}
		Map<String, Object> queryMap = new HashMap<>();
		queryMap.put("azoneId", azoneId);
		List<Map<String, Object>> list = new ArrayList<>();
		if ("0".equals(type)) {//已定价
			list = sqlQueryBiz.queryByName(SqlQueryProperty.QUERY_PRICED_VM_FLAVOR, queryMap);
		} else {//未定价
			list = sqlQueryBiz.queryByName(SqlQueryProperty.QUERY_UNPRICED_VM_FLAVOR, queryMap);
		}
		if (StrUtils.checkCollection(list)) {
			for (Map<String, Object> map : list) {
				String flavorId = StrUtils.tranString(map.get("flavorid"));
				flavorIds.add(flavorId);
			}
		}
		return flavorIds;
	}
}

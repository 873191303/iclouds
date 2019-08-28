package com.h3c.iclouds.rest;

import com.h3c.iclouds.base.BaseRest;
import com.h3c.iclouds.base.BaseRestControl;
import com.h3c.iclouds.biz.InitCodeBiz;
import com.h3c.iclouds.biz.NetGroupBiz;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageList;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.po.InitCode;
import com.h3c.iclouds.po.NetGroup;
import com.h3c.iclouds.utils.InvokeSetForm;
import com.h3c.iclouds.validate.ValidatorUtils;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by ykf7317 on 2017/9/5.
 */
@Api(value = "配置管理网络堆叠信息")
@RestController
@RequestMapping(value = "/net/group")
public class NetGroupRest extends BaseRest<NetGroup> {
	
	@Resource
	private NetGroupBiz netGroupBiz;
	
	@Resource
	private InitCodeBiz initCodeBiz;
	
	@Override
	public Object list () {
		return null;
	}
	
	@ApiOperation(value = "获取某个资产类型的集群堆叠列表")
	@RequestMapping(value = "/type/{typeCode}", method = RequestMethod.GET)
	public Object list (@PathVariable String typeCode) {
		PageEntity entity = this.beforeList();
		InitCode initCode = initCodeBiz.getByTypeCode(typeCode, ConfigProperty.CMDB_ASSET_TYPE);
		if (null == initCode) {
			return BaseRestControl.tranReturnValue(ResultType.assetType_error);
		}
		entity.setSpecialParam(initCode.getId());
		PageModel<NetGroup> pageModel = netGroupBiz.findForPage(entity);
		PageList<NetGroup> page = new PageList<>(pageModel, entity.getsEcho());
		return BaseRestControl.tranReturnValue(page);
	}
	
	@Override
	@ApiOperation(value = "获取堆叠详细信息")
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public Object get (@PathVariable String id) {
		NetGroup netGroup = netGroupBiz.findById(NetGroup.class, id);
		if (null == netGroup) {
			return BaseRestControl.tranReturnValue(ResultType.deleted);
		}
		return BaseRestControl.tranReturnValue(netGroup);
	}
	
	@ApiOperation(value = "在当前堆叠下添加子设备资产(绑定资产)")
	@RequestMapping(value = "/{id}/linkMasters", method = RequestMethod.POST)
	public Object linkMasters(@PathVariable String id, @RequestBody List<String> masterIds){
		return netGroupBiz.linkMasters(id, masterIds);
	}
	
	@Override
	@ApiOperation(value = "删除堆叠")
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public Object delete (@PathVariable String id) {
		NetGroup netGroup = netGroupBiz.findById(NetGroup.class, id);
		if (null == netGroup) {
			return BaseRestControl.tranReturnValue(ResultType.deleted);
		}
		netGroupBiz.deleteGroup(netGroup);
		return BaseRestControl.tranReturnValue(ResultType.success);
	}
	
	@Override
	@ApiOperation(value = "新增堆叠")
	@RequestMapping(method = RequestMethod.POST)
	public Object save (@RequestBody NetGroup entity) {
		return null;
	}
	
	@Override
	@ApiOperation(value = "修改堆叠信息")
	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	public Object update (@PathVariable String id, @RequestBody NetGroup entity) throws IOException {
		Map<String, String> validatorMap = ValidatorUtils.validator(entity);
		if(!validatorMap.isEmpty()) {
			return BaseRestControl.tranReturnValue(ResultType.parameter_error, validatorMap);
		}
		NetGroup netGroup = netGroupBiz.findById(NetGroup.class, id);
		if (null == netGroup) {
			return BaseRestControl.tranReturnValue(ResultType.deleted);
		}
		InvokeSetForm.copyFormProperties(entity, netGroup);
		netGroup.updatedUser(this.getLoginUser());
		netGroupBiz.update(netGroup);
		return BaseRestControl.tranReturnValue(ResultType.success, entity);
	}
}

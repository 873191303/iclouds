package com.h3c.iclouds.rest;

import com.h3c.iclouds.base.BaseRest;
import com.h3c.iclouds.base.BaseRestControl;
import com.h3c.iclouds.biz.VportBiz;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageList;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.po.Vport;
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
import java.util.Map;

/**
 * Created by ykf7317 on 2017/9/5.
 */
@Api(value = "配置管理网络端口")
@RestController
@RequestMapping(value = "/net/vport")
public class VportRest extends BaseRest<Vport> {
	
	@Resource
	private VportBiz vportBiz;
	
	@Override
	@ApiOperation(value = "获取堆叠管理网络端口列表")
	@RequestMapping(method = RequestMethod.GET)
	public Object list () {
		PageEntity entity = this.beforeList();
		PageModel<Vport> pageModel = vportBiz.findForPage(entity);
		PageList<Vport> page = new PageList<>(pageModel, entity.getsEcho());
		return BaseRestControl.tranReturnValue(page);
	}
	
	@ApiOperation(value = "获取堆叠的管理网络端口列表")
	@RequestMapping(value = "/group/{id}", method = RequestMethod.GET)
	public Object list (@PathVariable String id) {
		PageEntity entity = this.beforeList();
		entity.setSpecialParam(id);
		PageModel<Vport> pageModel = vportBiz.findForPage(entity);
		PageList<Vport> page = new PageList<>(pageModel, entity.getsEcho());
		return BaseRestControl.tranReturnValue(page);
	}
	
	@Override
	@ApiOperation(value = "获取堆叠管理网络端口详细信息")
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public Object get (@PathVariable String id) {
		Vport vport = vportBiz.findById(Vport.class, id);
		if (null == vport) {
			return BaseRestControl.tranReturnValue(ResultType.deleted);
		}
		return BaseRestControl.tranReturnValue(vport);
	}
	
	@Override
	@ApiOperation(value = "删除管理网络端口虚拟设备")
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public Object delete (@PathVariable String id) {
		return null;
	}
	
	@Override
	@ApiOperation(value = "新增堆叠管理网络端口")
	@RequestMapping(method = RequestMethod.POST)
	public Object save (@RequestBody Vport entity) {
		return null;
	}
	
	@Override
	@ApiOperation(value = "修改堆叠管理网络端口信息")
	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	public Object update (@PathVariable String id, @RequestBody Vport entity) throws IOException {
		Map<String, String> validatorMap = ValidatorUtils.validator(entity);
		if(!validatorMap.isEmpty()) {
			return BaseRestControl.tranReturnValue(ResultType.parameter_error, validatorMap);
		}
		Vport vport = vportBiz.findById(Vport.class, id);
		if (null == vport) {
			return BaseRestControl.tranReturnValue(ResultType.deleted);
		}
		InvokeSetForm.copyFormProperties(entity, vport);
		vport.updatedUser(this.getLoginUser());
		vportBiz.update(vport);
		return BaseRestControl.tranReturnValue(ResultType.success, vport);
	}
}

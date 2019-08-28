package com.h3c.iclouds.rest;

import com.h3c.iclouds.base.BaseChildRest;
import com.h3c.iclouds.base.BaseRestControl;
import com.h3c.iclouds.biz.AsmMasterBiz;
import com.h3c.iclouds.biz.IpRelationBiz;
import com.h3c.iclouds.biz.NetPortsBiz;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageList;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.po.AsmMaster;
import com.h3c.iclouds.po.IpRelation;
import com.h3c.iclouds.po.NetPorts;
import com.h3c.iclouds.utils.InvokeSetForm;
import com.h3c.iclouds.utils.StrUtils;
import com.h3c.iclouds.validate.PatternValidator;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(value = "资产管理-网口配置", description = "资产管理-网口配置")
@RestController
@RequestMapping("/asm/master/{masterId}/netPort")
public class NetPortsRest extends BaseChildRest<NetPorts> {
	
	@Resource
	private NetPortsBiz netPortsBiz;
	
	@Resource
	private AsmMasterBiz asmMasterBiz;
	
	@Resource
	private IpRelationBiz ipRelationBiz;
	
	@Override
	@ApiOperation(value = "获取网口列表数据")
	@RequestMapping(method = RequestMethod.GET)
	public Object list(@PathVariable String masterId) {
		PageEntity entity = this.beforeList();
		entity.setSpecialParam(masterId);
		PageModel<NetPorts> pageModel = netPortsBiz.findForPage(entity);
		PageList<NetPorts> page = new PageList<NetPorts>(pageModel, entity.getsEcho());
		return BaseRestControl.tranReturnValue(page);
	}

	@Override
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@ApiOperation(value = "获取网口信息")
	public Object get(@PathVariable String masterId, @PathVariable String id) {
		NetPorts entity = this.netPortsBiz.findById(NetPorts.class, id);
		ResultType result = checkNetPort(entity, masterId);
		if(result != ResultType.success) {
			return BaseRestControl.tranReturnValue(result);
		}
		return BaseRestControl.tranReturnValue(entity);
	}

	@Override
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	@ApiOperation(value = "删除网口信息")
	public Object delete(@PathVariable String masterId, @PathVariable String id) {
		if(isUseAsmMaster(masterId) == null) {
			return BaseRestControl.tranReturnValue(ResultType.asset_flag_error);
		}
		
		NetPorts entity = this.netPortsBiz.findById(NetPorts.class, id);
		ResultType result = checkNetPort(entity, masterId);
		if(result != ResultType.success) {
			return BaseRestControl.tranReturnValue(result);
		}
		
		try {
			this.netPortsBiz.deleteNetports(entity);
			return BaseRestControl.tranReturnValue(ResultType.success);
		} catch (Exception e) {
			this.exception(getClass(), e);
		}
		return BaseRestControl.tranReturnValue(ResultType.parameter_error);
	}
	
	/**
	 * 判断资产状态是否为使用中
	 * @param masterId
	 * @return
	 */
	private AsmMaster isUseAsmMaster(String masterId) {
		// 判断资产状态是否为使用中
		AsmMaster asmMaster = asmMasterBiz.findById(AsmMaster.class, masterId);
		if(asmMaster == null) {
			return null;
		}
		if(!ConfigProperty.CMDB_ASSET_FLAG2_USE.equals(asmMaster.getStatus())) {
			return null;
		}
		return asmMaster;
	}

	@Override
	@RequestMapping(method = RequestMethod.POST)
	@ApiOperation(value = "增加网口信息")
	public Object save(@PathVariable String masterId, @RequestBody NetPorts entity) {
		if(isUseAsmMaster(masterId) == null) {
			return BaseRestControl.tranReturnValue(ResultType.asset_flag_error);
		}
		Map<String, String> validatorMap = ValidatorUtils.validator(entity);
		if(validatorMap.isEmpty()) {
			entity.setMasterId(masterId);
			Map<String, Object> queryMap = new HashMap<String, Object>();
			queryMap.put("masterId", masterId);
			queryMap.put("seq", entity.getSeq());
			boolean repeatResult = this.netPortsBiz.checkRepeat(NetPorts.class, queryMap);
			if(!repeatResult) {
				return BaseRestControl.tranReturnValue(ResultType.netport_repeat);
			}
//			repeatResult = this.netPortsBiz.checkRepeat(NetPorts.class, StrUtils.createMap("mac", entity.getMac()));
//			if(!repeatResult) {
//				return BaseRestControl.tranReturnValue(ResultType.mac_repeat);
//			}
			try {
				entity.createdUser(getLoginUser());
				this.netPortsBiz.add(entity);
				return BaseRestControl.tranReturnValue(entity.getId());
			} catch (Exception e) {
				this.exception(this.getClass(), e);
				return BaseRestControl.tranReturnValue(ResultType.failure);
			}
		}
		return BaseRestControl.tranReturnValue(ResultType.parameter_error, validatorMap);
	}

	@Override
	@ApiOperation(value = "修改网口信息")
	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	public Object update(@PathVariable String masterId, @PathVariable String id, @RequestBody NetPorts entity) throws IOException {
		if(isUseAsmMaster(masterId) == null) {
			return BaseRestControl.tranReturnValue(ResultType.asset_flag_error);
		}
		Map<String, String> validatorMap = ValidatorUtils.validator(entity);
		if(validatorMap.isEmpty()) {
			NetPorts before = this.netPortsBiz.findById(NetPorts.class, id);
			ResultType result = checkNetPort(before, masterId);
			if(result != ResultType.success) {
				return BaseRestControl.tranReturnValue(result);
			}
			
			Map<String, Object> queryMap = new HashMap<String, Object>();
			queryMap.put("masterId", before.getMasterId());
			queryMap.put("seq", entity.getSeq());
			// 验证
			boolean repeatResult = this.netPortsBiz.checkRepeat(NetPorts.class, queryMap, id);
			if(!repeatResult) {
				return BaseRestControl.tranReturnValue(ResultType.repeat);
			}
			repeatResult = this.netPortsBiz.checkRepeat(NetPorts.class, StrUtils.createMap("mac", entity.getMac()), id);
			if(!repeatResult) {
				return BaseRestControl.tranReturnValue(ResultType.mac_repeat);
			}
			InvokeSetForm.copyFormProperties(entity, before);
			before.updatedUser(this.getLoginUser());
			try {
				this.netPortsBiz.update(before);
				return BaseRestControl.tranReturnValue(ResultType.success);
			} catch (Exception e) {
				this.exception(getClass(), e);
				return BaseRestControl.tranReturnValue(ResultType.failure);
			}
		}
		return BaseRestControl.tranReturnValue(ResultType.parameter_error, validatorMap);
	}

	@ApiOperation(value = "网口对应IP列表")
	@RequestMapping(value = "/ip", method = RequestMethod.GET)
	public Object ip(@PathVariable String masterId) {
		PageEntity entity = this.beforeList();
		entity.setSpecialParam(masterId);
		PageModel<IpRelation> pageModel = ipRelationBiz.findForPage(entity);
		PageList<IpRelation> page = new PageList<IpRelation>(pageModel, entity.getsEcho());
		return BaseRestControl.tranReturnValue(page);
		
	}
	
	@ApiOperation(value = "网口绑定IP")
	@RequestMapping(value = "/{id}/ip/link", method = RequestMethod.POST)
	public Object link(@PathVariable String masterId, @PathVariable String id, @RequestBody Map<String, Object> map) {
		AsmMaster asmMaster = isUseAsmMaster(masterId);
		if(asmMaster == null) {
			return BaseRestControl.tranReturnValue(ResultType.asset_flag_error);
		}
		NetPorts entity = this.netPortsBiz.findById(NetPorts.class, id);
		ResultType result = checkNetPort(entity, masterId);
		if(result != ResultType.success) {
			return BaseRestControl.tranReturnValue(result);
		}
		
		String ip = StrUtils.tranString(map.get("ip"));	// IP地址
		if(!StrUtils.checkParam(ip) || !PatternValidator.ipCheck(ip)) {
			return BaseRestControl.tranReturnValue(ResultType.parameter_error, StrUtils.createMap("ip", "ip为空或者格式错误"));
		}
		
		IpRelation ipRelation = ipRelationBiz.saveIp(ip, entity.getMasterId(), asmMaster.getAssetType(), entity.getId(), asmMaster.getGroupId());
		if(ipRelation == null) {
			return BaseRestControl.tranReturnValue(ResultType.ip_was_used);
		}
		return BaseRestControl.tranReturnValue(ResultType.success);
	}
	
	@ApiOperation(value = "网口解除绑定IP")
	@RequestMapping(value = "/{id}/ip/unlink", method = RequestMethod.POST)
	public Object unlink(@PathVariable String masterId, @PathVariable String id, @RequestBody Map<String, Object> map) {
		AsmMaster asmMaster = isUseAsmMaster(masterId);
		if(asmMaster == null) {
			return BaseRestControl.tranReturnValue(ResultType.asset_flag_error);
		}
		NetPorts entity = this.netPortsBiz.findById(NetPorts.class, id);
		ResultType result = checkNetPort(entity, masterId);
		if(result != ResultType.success) {
			return BaseRestControl.tranReturnValue(result);
		}
		
		String ip = StrUtils.tranString(map.get("ip"));	// IP地址
		if(!StrUtils.checkParam(ip) || !PatternValidator.ipCheck(ip)) {
			return BaseRestControl.tranReturnValue(ResultType.parameter_error, StrUtils.createMap("ip", "ip为空或者格式错误"));
		}
		List<IpRelation> list = ipRelationBiz.findByPropertyName(IpRelation.class, "ip", ip);
		IpRelation ipRelation = null;
		if(list != null && !list.isEmpty()) {
			ipRelation = list.get(0);
		}
		if(ipRelation != null) {
			ipRelationBiz.removeIp(ip, asmMaster, ipRelation, id);
		}
		return BaseRestControl.tranReturnValue(ResultType.success);
	}
	
	private ResultType checkNetPort(NetPorts entity, String masterId) {
		if(entity == null) {
			return ResultType.deleted;
		}
		if(!entity.getMasterId().equals(masterId)) {	// 选择网口与归属资产不匹配
			return ResultType.parameter_error;
		}
		return ResultType.success;
	}
	
	@ApiOperation(value = "校验网口mac是否重复")
	@RequestMapping(value = "/check/{mac}", method = RequestMethod.GET)
	public Object check(@PathVariable String mac) {
		Map<String, Object> queryMap = StrUtils.createMap("mac", mac);
		String id = request.getParameter("id");
		boolean repeatResult = this.netPortsBiz.checkRepeat(NetPorts.class, queryMap, id);
		if(!repeatResult) {
			return BaseRestControl.tranReturnValue(ResultType.failure);
		}
		return BaseRestControl.tranReturnValue(ResultType.success);
	}

}

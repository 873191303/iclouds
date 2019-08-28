package com.h3c.iclouds.rest;

import com.h3c.iclouds.base.BaseEntity;
import com.h3c.iclouds.base.BaseRest;
import com.h3c.iclouds.base.BaseRestControl;
import com.h3c.iclouds.biz.AsmMasterBiz;
import com.h3c.iclouds.biz.Asset2DrawerBiz;
import com.h3c.iclouds.biz.ExtAValueBiz;
import com.h3c.iclouds.biz.InitCodeBiz;
import com.h3c.iclouds.biz.NetPortsBiz;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageList;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.exception.MessageException;
import com.h3c.iclouds.po.AsmMaster;
import com.h3c.iclouds.po.Asset2Drawer;
import com.h3c.iclouds.po.Config;
import com.h3c.iclouds.po.ExtAValue;
import com.h3c.iclouds.po.InitCode;
import com.h3c.iclouds.po.Master2Server;
import com.h3c.iclouds.po.NetPorts;
import com.h3c.iclouds.utils.StrUtils;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(value = "资产管理-设置资产管理", description = "资产管理-设置资产管理")
@RestController
@RequestMapping("/asm/master")
public class AsmMasterRest extends BaseRest<AsmMaster> {

	@Resource
	private AsmMasterBiz asmMasterBiz;

	@Resource
	private InitCodeBiz initCodeBiz;

	@Resource
	private Asset2DrawerBiz asset2DrawerBiz;

	@Resource
	private NetPortsBiz netPortsBiz;
	
	@Resource
	private ExtAValueBiz extAValueBiz;
	
	@Override
	@ApiOperation(value = "获取设备资产列表", response = AsmMaster.class)
	@RequestMapping(method = RequestMethod.GET)
	public Object list() {
		PageEntity entity = this.beforeList();
		String assetType = this.request.getParameter("assetTypeCode");
		if (StrUtils.checkParam(assetType)) {
			InitCode initCode = initCodeBiz.getByTypeCode(assetType, ConfigProperty.CMDB_ASSET_TYPE);
			if (initCode == null) {
				return BaseRestControl.tranReturnValue(ResultType.assetType_error);
			}
			entity.setSpecialParam(initCode.getId());
		}

		PageModel<AsmMaster> pageModel = asmMasterBiz.findForPage(entity);
		PageList<AsmMaster> page = new PageList<AsmMaster>(pageModel, entity.getsEcho());
		return BaseRestControl.tranReturnValue(page);
	}
	
	@ApiOperation(value = "获取用于存储集群或堆叠子设备的设备资产列表", response = AsmMaster.class)
	@RequestMapping(value = "/item/{typeCode}", method = RequestMethod.GET)
	public Object list(@PathVariable String typeCode) {
		PageEntity entity = this.beforeList();
		InitCode initCode = initCodeBiz.getByTypeCode(typeCode, ConfigProperty.CMDB_ASSET_TYPE);
		if (initCode == null) {
			return BaseRestControl.tranReturnValue(ResultType.assetType_error);
		}
		entity.setSpecialParam(initCode.getId());
		PageModel<AsmMaster> pageModel = asmMasterBiz.findForPage(entity, true);
		PageList<AsmMaster> page = new PageList<AsmMaster>(pageModel, entity.getsEcho());
		return BaseRestControl.tranReturnValue(page);
	}
	
	@ApiOperation(value = "获取设备资产列表（状态为正常的）", response = AsmMaster.class)
	@RequestMapping(method = RequestMethod.GET, value = "/sublist")
	public Object sublist() {
		String assetType = this.request.getParameter("assetTypeCode");
		Map<String, Object> map = new HashMap<String, Object>();
		if (StrUtils.checkParam(assetType)) {
			InitCode initCode = initCodeBiz.getByTypeCode(assetType, ConfigProperty.CMDB_ASSET_TYPE);
			if (initCode == null) {
				return BaseRestControl.tranReturnValue(ResultType.parameter_error);
			}
			map.put("assetType", initCode.getId());
		}
		map.put("status", ConfigProperty.CMDB_ASSET_FLAG2_USE);
		return BaseRestControl.tranReturnValue(this.asmMasterBiz.listByClass(AsmMaster.class, map));
	}

	@SuppressWarnings("unchecked")
	@Override
	@ApiOperation(value = "获取设备资产详细信息", response = AsmMaster.class)
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public Object get(@PathVariable String id) {
		AsmMaster asmMaster = this.asmMasterBiz.findById(AsmMaster.class, id);
		if (asmMaster != null) {
			// 机柜信息
			Asset2Drawer draw = this.asset2DrawerBiz.findById(Asset2Drawer.class, id);
			asmMaster.setDraw(draw);

			Config config = this.asmMasterBiz.getConfigByType(asmMaster.getAssetTypeCode());
			if (config != null) {
				BaseEntity entity = (BaseEntity) config.getDao().findById(config.getClazz(), asmMaster.getId());
				asmMaster.setConfig(entity);
			}

			// 维保

			// 网卡
			List<NetPorts> netPorts = this.netPortsBiz.findByPropertyName(NetPorts.class, "masterId", id);
			asmMaster.setNetPorts(netPorts);
			return BaseRestControl.tranReturnValue(asmMaster);
		}
		return BaseRestControl.tranReturnValue(ResultType.deleted);
	}

	@ApiOperation(value = "验证重复是否存在 assetId(资产编号), serial(设备序列号),【修改的时候请传递id，用于排除本身】")
	@RequestMapping(value = "/check/{columnName:assetId|serial|assetName|iloIP|mmac}", method = RequestMethod.GET)
	public Object check(HttpServletRequest req, @PathVariable String columnName) {
		String id = req.getParameter("id");
		String value = req.getParameter("value");
		//String assetType = req.getParameter("assetType");
		//String assetTypeCode = req.getParameter("assetTypeCode");
		Map<String, Object> checkMap = StrUtils.createMap(columnName, value);
//		if (StrUtils.checkParam(assetType)) {
//			checkMap.put("assetType", assetType);
//		}
//		if (StrUtils.checkParam(assetTypeCode)) {
//			InitCode initCode = initCodeBiz.getByTypeCode(assetTypeCode, ConfigProperty.CMDB_ASSET_TYPE);
//			if(initCode == null) {
//				return ResultType.assetType_error;
//			}
//			checkMap.put("assetType", initCode.getId());
//		}
		if (this.asmMasterBiz.checkRepeat(AsmMaster.class, checkMap, id)) {
			return BaseRestControl.tranReturnValue(ResultType.success);
		}
		return BaseRestControl.tranReturnValue(ResultType.repeat);
	}

	@ApiOperation(value = "启动设备资产")
	@RequestMapping(value = "/{id}/recover", method = RequestMethod.POST)
	public Object recover(@PathVariable String id) {
		AsmMaster entity = this.asmMasterBiz.findById(AsmMaster.class, id);
		if (entity != null) {
			try {
				if (!ConfigProperty.CMDB_ASSET_FLAG3_UNUSE.equals(entity.getStatus())) {
					return BaseRestControl.tranReturnValue(ResultType.asset_flag_error);
				}
				entity.setStatus(ConfigProperty.CMDB_ASSET_FLAG2_USE);
				this.asmMasterBiz.update(entity);
				return BaseRestControl.tranReturnValue(ResultType.success);
			} catch (Exception e) {
				this.exception(this.getClass(), e);
				return BaseRestControl.tranReturnValue(ResultType.failure);
			}
		}
		return BaseRestControl.tranReturnValue(ResultType.deleted);
	}

	@Override
	@ApiOperation(value = "删除/禁用设备设备资产")
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public Object delete(@PathVariable String id) {
		AsmMaster entity = this.asmMasterBiz.findById(AsmMaster.class, id);
		if (entity != null) {
			try {
				if (ConfigProperty.CMDB_ASSET_FLAG1_DRAFT.equals(entity.getStatus())) {
					Asset2Drawer draw = this.asset2DrawerBiz.findById(Asset2Drawer.class, entity.getId());
					if (draw != null) {
						this.asset2DrawerBiz.delete(draw);
					}
					// 删除扩展列的属性
					List<ExtAValue> extAValues = extAValueBiz.findByPropertyName(ExtAValue.class, "assetID",
							entity.getId());
					if (extAValues != null && !extAValues.isEmpty()) {
						for (ExtAValue e : extAValues) {
							extAValueBiz.delete(e);
						}
					}
					asmMasterBiz.delete(entity); // 删除关联表的数据
				} else {
					// 需要判断设备的使用情况，确定是否能够退库
					entity.setStatus(ConfigProperty.CMDB_ASSET_FLAG3_UNUSE);
					this.asmMasterBiz.update(entity);
				}
				return BaseRestControl.tranReturnValue(ResultType.success);
			} catch (Exception e) {
				this.exception(this.getClass(), e);
				return BaseRestControl.tranReturnValue(ResultType.failure);
			}
		}
		return BaseRestControl.tranReturnValue(ResultType.deleted);
	}

	@ApiOperation(value = "修改设备资产基本信息", response = AsmMaster.class)
	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	public Object update(@PathVariable String id, @RequestBody Map<String, Object> map) {
		
		try {
			ResultType result = this.asmMasterBiz.saveOrUpdate(map, id);
			return BaseRestControl.tranReturnValue(result);
		} catch (MessageException e) {
			this.exception(this.getClass(), e);
			return BaseRestControl.tranReturnValue(e.getResultCode());
		} catch (Exception e) {
			this.exception(this.getClass(), e);
			return BaseRestControl.tranReturnValue(ResultType.failure);
		}
	}

	@ApiOperation(value = "创建设备资产", response = AsmMaster.class)
	@RequestMapping(method = RequestMethod.POST)
	public Object save(@RequestBody Map<String, Object> map) {
		try {
			ResultType result = this.asmMasterBiz.saveOrUpdate(map, null);
			return BaseRestControl.tranReturnValue(result);
		} catch (MessageException e) {
			this.exception(this.getClass(), e);
			return BaseRestControl.tranReturnValue(e.getResultCode());
		} catch (Exception e) {
			this.exception(this.getClass(), e);
			return BaseRestControl.tranReturnValue(ResultType.failure);
		}
	}

	@ApiOperation(value = "配置设备信息", response = Master2Server.class)
	@RequestMapping(value = "/{id}/config", method = RequestMethod.PUT)
	public Object config(@PathVariable String id, @RequestBody Map<String, Object> map) {
		AsmMaster asmMaster = this.asmMasterBiz.findById(AsmMaster.class, id);
		if (asmMaster != null) {
			try {
				if (!asmMaster.getStatus().equals(ConfigProperty.CMDB_ASSET_FLAG2_USE)) {
					return BaseRestControl.tranReturnValue(ResultType.asset_flag_error);
				}

				ResultType result = asmMasterBiz.config(asmMaster, map);
				return BaseRestControl.tranReturnValue(result);
			} catch (Exception e) {
				this.exception(this.getClass(), e);
				return BaseRestControl.tranReturnValue(ResultType.failure);
			}
		}
		return BaseRestControl.tranReturnValue(ResultType.deleted);
	}

	@ApiOperation(value = "服务器概况")
	@RequestMapping(value = "/situation", method = RequestMethod.GET)
	public Object serverSituation() {
		Map<String, Integer> map=new HashMap<>();
		List<InitCode> initCodes=initCodeBiz.findByPropertyName(InitCode.class,"codeId","server");
		if(!StrUtils.checkParam(initCodes)){
			return BaseRestControl.tranReturnValue(ResultType.parameter_error);
		}
		String assetTypeId=initCodes.get(0).getId();
		Map<String, Object> queryMap=new HashMap<>();
		queryMap.put("assetType", assetTypeId);
		queryMap.put("useFlag", ConfigProperty.CMDB_ASSET_USEFLAG_PRODUCT);
		int proCount=asmMasterBiz.count(AsmMaster.class, queryMap);
		queryMap.put("useFlag", ConfigProperty.CMDB_ASSET_USEFLAG_TEST);
		int testCount=asmMasterBiz.count(AsmMaster.class, queryMap);
		
		int otherCount=asmMasterBiz.otherUseFlag();
		
		map.put("proCount", proCount);
		map.put("testCount", testCount);
		map.put("otherCount", otherCount);
		return BaseRestControl.tranReturnValue(map);
	}



	@Override
	public Object save(AsmMaster entity) {
		return null;
	}

	@Override
	public Object update(String id, AsmMaster entity) throws IOException {
		return null;
	}

}

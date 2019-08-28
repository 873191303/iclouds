package com.h3c.iclouds.rest;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.h3c.iclouds.base.BaseRest;
import com.h3c.iclouds.base.BaseRestControl;
import com.h3c.iclouds.biz.AsmMasterBiz;
import com.h3c.iclouds.biz.NetPortsBiz;
import com.h3c.iclouds.biz.NetPortsLinkBiz;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.po.AsmMaster;
import com.h3c.iclouds.po.NetPorts;
import com.h3c.iclouds.po.NetPortsLink;
import com.h3c.iclouds.utils.StrUtils;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

/**
 * 服务器连接存储作为上联口，其他作为下联口
 * 存储只允许作为下联口
 * 交换机、路由器可以连接自身网口
 * @author zkf5485
 *
 */
@Api(value = "网口连接", description = "网口连接")
@RestController
@RequestMapping("/netPort")
public class NetPortsLinkRest extends BaseRest<NetPortsLink> {
	
	@Resource
	private NetPortsBiz netPortsBiz;
	
	@Resource
	private NetPortsLinkBiz netPortsLinkBiz;
	
	@Resource
	private AsmMasterBiz asmMasterBiz;
	
	@RequestMapping(method = RequestMethod.POST, value = "/link/{id}")
	@ApiOperation(value = "网口连接")
	public Object link(@PathVariable String id, @RequestBody Map<String, Object> map) {
		String linkPort = StrUtils.tranString(map.get("linkPort"));
		String linkType = StrUtils.tranString(map.get("linkType"));
		if(!linkType.equals(ConfigProperty.CMDB_NETPORT_LINK_ACCESS) && !linkType.equals(ConfigProperty.CMDB_NETPORT_LINK_TRUNK)) {
			return BaseRestControl.tranReturnValue(ResultType.parameter_error);
		}
		if(linkPort.equals(id)) {	// 选择的端口一致
			return BaseRestControl.tranReturnValue(ResultType.parameter_error);
		}
		
		NetPorts entity = netPortsBiz.findById(NetPorts.class, id);
		if(entity == null) {
			return BaseRestControl.tranReturnValue(ResultType.deleted);
		}
		
		NetPorts linkEntity = netPortsBiz.findById(NetPorts.class, linkPort);
		if(linkEntity == null) {
			return BaseRestControl.tranReturnValue(ResultType.select_port_deleted);
		}
		
		if(!entity.getPortType().equals(linkEntity.getPortType())) {	// 类型不一致
			return BaseRestControl.tranReturnValue(ResultType.port_type_error);
		}
		if(entity.getEthType().equals(ConfigProperty.CMDB_NETPORT_ETHTYPE2_VIRTUAL) || 
				linkEntity.getEthType().equals(ConfigProperty.CMDB_NETPORT_ETHTYPE2_VIRTUAL)) {
			return BaseRestControl.tranReturnValue(ResultType.virtual_eth_type_cannt_link);
		}
				
		List<NetPortsLink> linkList = netPortsLinkBiz.findByNetPortId(linkPort);
		if(linkList != null && !linkList.isEmpty()) {	// 获取连接设备是否存在被连接网口
			if(linkList.size() > 1) {	// 删除异常数据
				for (int i = 1; i < linkList.size(); i++) {
					netPortsLinkBiz.delete(linkList.get(i));
				}
			}
			NetPortsLink link = linkList.get(0);
			// 存在连接，但是连接不是操作网口
			if(linkPort.equals(link.getAccessTo()) && !id.equals(link.getTrunkTo())) {
				return BaseRestControl.tranReturnValue(ResultType.select_port_already_linked);
			} else if(link.getTrunkTo().equals(linkPort) && !link.getAccessTo().equals(id)) {
				return BaseRestControl.tranReturnValue(ResultType.select_port_already_linked);
			}
		}
		
		NetPortsLink saveEntity = null;
		AsmMaster master = this.asmMasterBiz.findById(AsmMaster.class, entity.getMasterId());
		if(!master.getStatus().equals(ConfigProperty.CMDB_ASSET_FLAG2_USE)) {
			return BaseRestControl.tranReturnValue(ResultType.asset_flag_error);
		}
		AsmMaster linkMaster = this.asmMasterBiz.findById(AsmMaster.class, linkEntity.getMasterId());
		if(!linkMaster.getStatus().equals(ConfigProperty.CMDB_ASSET_FLAG2_USE)) {
			return BaseRestControl.tranReturnValue(ResultType.asset_flag_error);
		}
		// 操作的设备作为上联口，选择的设备作为下联口
		if(linkType.equals(ConfigProperty.CMDB_NETPORT_LINK_ACCESS)) {
			String assetTypeCode = master.getAssetTypeCode();
			if(assetTypeCode.equals(ConfigProperty.CMDB_ASSET_TYPE_STOCK)) {	// 存储不能作为上联设备
				return BaseRestControl.tranReturnValue(ResultType.stock_port_cannt_be_trunk);
			} else if(assetTypeCode.equals(ConfigProperty.CMDB_ASSET_TYPE_SERVER)) {	// 服务器作为上联口，则下联口类型只能为存储
				if(!linkMaster.getAssetTypeCode().equals(ConfigProperty.CMDB_ASSET_TYPE_STOCK)) {
					return BaseRestControl.tranReturnValue(ResultType.stock_port_cannt_be_trunk);
				}
			}
			saveEntity = new NetPortsLink();
			saveEntity.setAccessTo(linkPort);
			saveEntity.setTrunkTo(id);
		} else {	// 操作的设备作为下联口，选择的设备作为上联口
			String assetTypeCode = linkMaster.getAssetTypeCode();
			if(assetTypeCode.equals(ConfigProperty.CMDB_ASSET_TYPE_STOCK)) {	// 存储不能作为上联设备
				return BaseRestControl.tranReturnValue(ResultType.stock_port_cannt_be_trunk);
			} else if(assetTypeCode.equals(ConfigProperty.CMDB_ASSET_TYPE_SERVER)) {	// 服务器作为上联口，则下联口类型只能为存储
				if(!master.getAssetTypeCode().equals(ConfigProperty.CMDB_ASSET_TYPE_STOCK)) {
					return BaseRestControl.tranReturnValue(ResultType.stock_port_cannt_be_trunk);
				}
			}
			saveEntity = new NetPortsLink();
			saveEntity.setAccessTo(id);
			saveEntity.setTrunkTo(linkPort);
		}
		saveEntity.createdUser(this.getLoginUser());
		saveEntity.setRemark(StrUtils.tranString(map.get("remark")));
		saveEntity.setVlan(StrUtils.tranString(map.get("vlan")));
		try {
			this.netPortsLinkBiz.saveLink(id, saveEntity);
			return BaseRestControl.tranReturnValue(ResultType.success);
		} catch (Exception e) {
			this.exception(clazz, e);
			return BaseRestControl.tranReturnValue(ResultType.failure);
		}
	}
	
	@ApiOperation(value = "端口概况")
	@RequestMapping(value = "/situation", method = RequestMethod.GET)
	public Object netportSituation() {
		Map<String, Integer> map=new HashMap<>();
		int total=netPortsBiz.count(NetPorts.class,null);
		int used=netPortsLinkBiz.count(NetPortsLink.class,null)*2;
		map.put("netPortTotal", total);
		map.put("netPortUsed", used);
		return BaseRestControl.tranReturnValue(map);
	}

	@Override
	public Object list() {
		return null;
	}

	@Override
	public Object get(String id) {
		return null;
	}

	@Override
	public Object delete(String id) {
		return null;
	}

	@Override
	public Object save(NetPortsLink entity) {
		return null;
	}

	@Override
	public Object update(String id, NetPortsLink entity) throws IOException {
		return null;
	}

}

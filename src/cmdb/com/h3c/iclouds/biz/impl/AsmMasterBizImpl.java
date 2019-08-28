package com.h3c.iclouds.biz.impl;

import com.alibaba.fastjson.JSONObject;
import com.h3c.iclouds.base.BaseBizImpl;
import com.h3c.iclouds.base.BaseDAO;
import com.h3c.iclouds.base.BaseEntity;
import com.h3c.iclouds.base.BaseRestControl;
import com.h3c.iclouds.base.SqlQueryBiz;
import com.h3c.iclouds.biz.AsmMasterBiz;
import com.h3c.iclouds.biz.Asset2DrawerBiz;
import com.h3c.iclouds.biz.Class2ItemsBiz;
import com.h3c.iclouds.biz.DrawsBiz;
import com.h3c.iclouds.biz.ExtAValueBiz;
import com.h3c.iclouds.biz.ExtColumnsBiz;
import com.h3c.iclouds.biz.InitCodeBiz;
import com.h3c.iclouds.biz.IpRelationBiz;
import com.h3c.iclouds.biz.NetPortsBiz;
import com.h3c.iclouds.biz.NetPortsLinkBiz;
import com.h3c.iclouds.biz.RoomsBiz;
import com.h3c.iclouds.biz.UserBiz;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.common.SqlQueryProperty;
import com.h3c.iclouds.dao.AsmMasterDao;
import com.h3c.iclouds.exception.MessageException;
import com.h3c.iclouds.opt.MonitorClient;
import com.h3c.iclouds.opt.MonitorParams;
import com.h3c.iclouds.po.AsmMaster;
import com.h3c.iclouds.po.Asset2Drawer;
import com.h3c.iclouds.po.Class2Items;
import com.h3c.iclouds.po.Config;
import com.h3c.iclouds.po.Draws;
import com.h3c.iclouds.po.ExtAValue;
import com.h3c.iclouds.po.ExtColumns;
import com.h3c.iclouds.po.InitCode;
import com.h3c.iclouds.po.IpRelation;
import com.h3c.iclouds.po.Master2Boards;
import com.h3c.iclouds.po.Master2Other;
import com.h3c.iclouds.po.Master2Router;
import com.h3c.iclouds.po.Master2Server;
import com.h3c.iclouds.po.Master2Stock;
import com.h3c.iclouds.po.Master2Switch;
import com.h3c.iclouds.po.NetPorts;
import com.h3c.iclouds.po.NetPortsLink;
import com.h3c.iclouds.po.Rooms;
import com.h3c.iclouds.po.User;
import com.h3c.iclouds.rest.DataImportRest;
import com.h3c.iclouds.utils.DateUtils;
import com.h3c.iclouds.utils.InvokeSetForm;
import com.h3c.iclouds.utils.LogUtils;
import com.h3c.iclouds.utils.StrUtils;
import com.h3c.iclouds.validate.ValidatorUtils;
import jxl.Sheet;
import jxl.Workbook;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service("asmMasterBiz")
@SuppressWarnings({ "rawtypes", "unchecked" })
public class AsmMasterBizImpl extends BaseBizImpl<AsmMaster> implements AsmMasterBiz {
	
	@Resource
	private AsmMasterDao asmMasterDao;
	
	@Resource
	private InitCodeBiz initCodeBiz;
	
	@Resource(name = "baseDAO")
	private BaseDAO<Master2Switch> master2SwitchDao;
	
	@Resource(name = "baseDAO")
	private BaseDAO<Master2Router> master2RouterDao;
	
	@Resource(name = "baseDAO")
	private BaseDAO<Master2Server> master2ServerDao;
	
	@Resource(name = "baseDAO")
	private BaseDAO<Master2Stock> master2StockDao;
	
	@Resource(name = "baseDAO")
	private BaseDAO<Master2Other> master2OtherDao;
	
	@Resource(name = "baseDAO")
	private BaseDAO<Master2Boards> master2BoardsDao;
	
	@Resource
	private IpRelationBiz ipRelationBiz;
	
	@Resource
	private Asset2DrawerBiz asset2DrawerBiz;
	
	@Resource
	private ExtColumnsBiz extColumnsBiz;
	
	@Resource
	private ExtAValueBiz extAValueBiz;
	
	@Resource
	private NetPortsBiz netPortsBiz;
	
	@Resource
	private UserBiz userBiz;
	
	@Resource
	private SqlQueryBiz sqlQueryBiz;
	
	@Resource
	private Class2ItemsBiz class2ItemsBiz;
	
	@Resource
	private NetPortsLinkBiz netPortsLinkBiz;
	
	@Resource
	private RoomsBiz roomsBiz;
	
	@Resource
	private DrawsBiz drawsBiz;
	
	@Override
	public PageModel<AsmMaster> findForPage(PageEntity entity) {
		PageModel<AsmMaster> pageModel=asmMasterDao.findForPage(entity, false);
		List<AsmMaster> asmMasters=pageModel.getDatas();
		for(AsmMaster asmMaster:asmMasters){
			Asset2Drawer asset2Drawer=asset2DrawerBiz.findById(Asset2Drawer.class, asmMaster.getId());
			if(StrUtils.checkParam(asset2Drawer)){
				asmMaster.setDraw(asset2Drawer);
			}
		}
		return pageModel;
	}
	
	@Override
	public PageModel<AsmMaster> findForPage(PageEntity entity, boolean flag) {
		return asmMasterDao.findForPage(entity, flag);
	}
	
	@Override
	public ResultType saveOrUpdate(Map<String, Object> map, String id) {
		AsmMaster entity = new AsmMaster();
		InvokeSetForm.settingForm(map, entity);
		
		// 不能新增已退库状态服务器
		if(null == id && ConfigProperty.CMDB_ASSET_FLAG3_UNUSE.equals(entity.getStatus())) {
			return ResultType.unallowed_status;
		}
		
		Map<String, String> validatorMap = ValidatorUtils.validator(entity);
		if(!validatorMap.isEmpty()) {
			return ResultType.parameter_error;
		}
		
		// 验证资产编号是否重复
		if(!checkRepeat(AsmMaster.class, "assetId", entity.getAssetId(), id)) {
			return ResultType.assetId_repeat;
		}
		
		// 验证设备序列号是否重复
		if(!checkRepeat(AsmMaster.class, "serial", entity.getSerial(), id)) {
			return ResultType.serial_repeat;
		}
		
		// 验证资产名称是否重复
		if(!checkRepeat(AsmMaster.class, "assetName", entity.getAssetName(), id)) {
			return ResultType.name_repeat;
		}
		
		String iloIP = entity.getIloIP();
		if (StrUtils.checkParam(iloIP) && !checkRepeat(AsmMaster.class, "iloIP", iloIP, id)) {
			// 验证管理网ip是否重复
			return ResultType.iloip_repeat;
		}
		if(id != null) {	// 更新操作
			AsmMaster before = this.findById(AsmMaster.class, id);
			if(before == null) {
				return ResultType.deleted;
			}
			
			// 状态修改
			if (!before.getStatus().equals(entity.getStatus())) {
				//草稿和已退库只能修改为使用中;使用中只能修改为退库
				if(before.getStatus().equals(ConfigProperty.CMDB_ASSET_FLAG2_USE)) {
					if (!ConfigProperty.CMDB_ASSET_FLAG3_UNUSE.equals(entity.getStatus())) {
						return ResultType.status_translate_error;
					}
					if (before.getCount() > 0) {
						return ResultType.still_in_monitor;
					}
				} else if (StrUtils.equals(before.getStatus(), ConfigProperty.CMDB_ASSET_FLAG1_DRAFT, ConfigProperty.CMDB_ASSET_FLAG3_UNUSE)) {
					if (!ConfigProperty.CMDB_ASSET_FLAG2_USE.equals(entity.getStatus())) {
						return ResultType.status_translate_error;
					}
				}
			}
			
			// 原本有IP，且IP不一致，则先移除原本的IP
			if(StrUtils.checkParam(before.getIloIP()) && !before.getIloIP().equals(iloIP)) {
				this.ipRelationBiz.removeIp(before.getIloIP(), before, null, null);
			}
			
			InvokeSetForm.copyFormProperties(entity, before);
			before.updatedUser(this.getLoginUser());
			entity = before;
			this.update(before);
		} else {
			InitCode initCode = initCodeBiz.getByTypeCode(entity.getAssetTypeCode(), ConfigProperty.CMDB_ASSET_TYPE);
			if(initCode == null) {
				return ResultType.assetType_error;
			}
			entity.setAssetType(initCode.getId());	// 设置类型
			entity.createdUser(this.getLoginUser());
			this.add(entity);
			id = entity.getId();
		}
		// 保存IP使用记录
		if(StrUtils.checkParam(iloIP)) {
			IpRelation ipEntity = ipRelationBiz.saveIp(iloIP, entity.getId(), entity.getAssetType(), null, entity.getGroupId());
			if(ipEntity == null) {
				throw new MessageException(ResultType.ip_was_used);
			}
		}
		
		// 保存机柜信息
		Asset2Drawer drawEntity = this.asset2DrawerBiz.findById(Asset2Drawer.class, id);
		boolean drawFlag = drawEntity == null;
		String drawsId = entity.getDrawsId();
		Integer unumb = StrUtils.tranInteger(entity.getUnumb());
		if (StrUtils.checkParam(drawsId, unumb)) {// 有机柜信息
			if(drawFlag) {
				drawEntity = new Asset2Drawer();
				drawEntity.setId(id);
				drawEntity.setDrawsId(drawsId);
				drawEntity.setUnumb(unumb);
				drawEntity.createdUser(this.getLoginUser());
			} else {
				drawEntity.setDrawsId(drawsId);
				drawEntity.setUnumb(unumb);
				drawEntity.updatedUser(this.getLoginUser());
			}
			ResultType result = asset2DrawerBiz.checkAsset2Draw(entity, drawEntity, entity.getAssetType());
			if(result != ResultType.success) {	// 机柜信息异常
				throw new MessageException(result);
			}
			if(drawFlag) {
				this.asset2DrawerBiz.add(drawEntity);
			} else {
				this.asset2DrawerBiz.update(drawEntity);
			}
		} else {
			if (!drawFlag) {// 删除原本的机柜信息
				asset2DrawerBiz.delete(drawEntity);
			}
		}
		//drawEntity.setDrawsId("".equals(drawsId.trim()) ? null : drawsId);	// 机柜如果为空则不赋值
		//drawEntity.setUnumb(unumb > 0 ? unumb : null);	// 如果大于0
		
		//设置自助属性
		List<ExtColumns> extColumns = extColumnsBiz.findByPropertyName(ExtColumns.class, "assType", entity.getAssetType());
		if(StrUtils.checkParam(extColumns)){
			for(String key:map.keySet()){
				for(ExtColumns e:extColumns){
					if(e.getXcName().equals(key)){
						Map<String, String> queryMap=new HashMap<>();
						queryMap.put("extName", key);
						queryMap.put("assetID",id);
						List<ExtAValue> extAValues=extAValueBiz.findByMap(ExtAValue.class,queryMap);
						ExtAValue extAValue=null;
						if(StrUtils.checkParam(extAValues)){
							extAValue=extAValues.get(0);
							extAValue.setExtValue(map.get(key)==null?"":map.get(key).toString());
							extAValue.updatedUser(getLoginUser());
							extAValueBiz.update(extAValue);
						}else{
							extAValue=new ExtAValue();
							extAValue.setAssetID(id);
							extAValue.setExtName(key);
							extAValue.setExtValue(map.get(key)==null?"":map.get(key).toString());
							extAValue.setExtID(e.getId());
							extAValue.createdUser(getLoginUser());
							extAValueBiz.add(extAValue);
						}
					}
				}
			}
		}
		return ResultType.success;
	}

	public Config getConfigByType(String type) {
		BaseDAO dao = null;
		Class clazz = null;
		switch (type) {
			case ConfigProperty.CMDB_ASSET_TYPE_SWITCH:
				clazz = Master2Switch.class;
				dao = master2SwitchDao;
				break;
			case ConfigProperty.CMDB_ASSET_TYPE_ROUTER:
				clazz = Master2Router.class;
				dao = master2RouterDao;
				break;
			case ConfigProperty.CMDB_ASSET_TYPE_SERVER:
				clazz = Master2Server.class;
				dao = master2ServerDao;
				break;
			case ConfigProperty.CMDB_ASSET_TYPE_OTHER:
				clazz = Master2Other.class; 
				dao = master2OtherDao;
				break;
			case ConfigProperty.CMDB_ASSET_TYPE_BOARDS:
				clazz = Master2Boards.class;
				dao = master2BoardsDao;
				break;
			case ConfigProperty.CMDB_ASSET_TYPE_STOCK:
				clazz = Master2Stock.class;
				dao = master2StockDao;
				break;
		}
		return dao == null ? null : new Config(dao, clazz);
	}
	
	@Override
	public ResultType config(AsmMaster asmMaster, Map<String, Object> map) {
		Config config = this.getConfigByType(asmMaster.getAssetTypeCode());
		if(config == null) {
			return ResultType.assetType_error;
		}
		
		map.put("id", asmMaster.getId());	// 设置到map
		String jsonString = new JSONObject(map).toJSONString();
		BaseEntity entity = (BaseEntity) JSONObject.parseObject(jsonString, config.getClazz());
		Map<String, String> validatorMap = ValidatorUtils.validator(entity);
		if(validatorMap.isEmpty()) {
			BaseEntity before = (BaseEntity) config.getDao().findById(config.getClazz(), asmMaster.getId());
			if(before == null) {
				entity.createdUser(this.getLoginUser());
				config.getDao().add(entity);
			} else {
				InvokeSetForm.copyFormProperties(entity, before);
				before.updatedUser(this.getLoginUser());
				config.getDao().update(before);
			}
			return ResultType.success;
		}
		return ResultType.parameter_error;
	}

	@Override
	public int otherUseFlag() {
		List<InitCode> initCodes=initCodeBiz.findByPropertyName(InitCode.class, "codeId", "server");
		
		if(!StrUtils.checkParam(initCodes)){
			return 0;
		}
		String assetType=initCodes.get(0).getId();
		return this.asmMasterDao.otherUseFlag(assetType);
	}
	@Override
	public PageModel<AsmMaster> without(PageEntity entity) {
		return this.asmMasterDao.without(entity);
	}

	public void initMonitor(List<Map<String, String>> ipMacs) {
		User user = this.userBiz.findById(User.class, "989116e3-79a2-426b-bfbe-668165104885");
		MonitorClient client = MonitorClient.createAdmin(user);
		if(client == null) {
			LogUtils.warn(this.getClass(), "初始化服务器监控信息失败，失败原因：未获取到client.");
			return;
		}
		for (Map<String, String> ipMac : ipMacs) {
			String ip = ipMac.get("ip");
			String iloIp = ipMac.get("iloIp");
			String mac = ipMac.get("mac");
			if (StrUtils.checkParam(ip, mac)) {
				AsmMaster asmMaster = this.getAsmMaster(iloIp);
				if (StrUtils.checkParam(asmMaster)) {
					NetPorts netPort = this.getNetPort(asmMaster.getId(), mac);
					this.getIpRelation(asmMaster.getId(), ip, netPort.getId());
					if (asmMaster.getCount() <= 0) {
						Map<String, Object> map = StrUtils.createMap("id", asmMaster.getId());
						map.put("type", "1");
						String templateId = this.getDefaultTemplateId(asmMaster.getOs(), "agent");
						if (StrUtils.checkParam(templateId)) {
							map.put("templateId", templateId);
							Map<String, Object> interfaceMap = StrUtils.createMap("ip", ip);
							interfaceMap.put("port", "10050");
							map.put("aninterface", interfaceMap);
							JSONObject jsonObject = client.post(client.tranUrl(MonitorParams.MONITOR_API_HOST_ADD), map);
							if (!client.checkResult(jsonObject)) {
								LogUtils.warn(AsmMaster.class, "Add Host Failure, error :" + client.getError(jsonObject));
							}
						}
					}
				}
			}
		}
	}
	
	private AsmMaster getAsmMaster(String iloIp) {
		AsmMaster asmMaster = this.singleByClass(AsmMaster.class, StrUtils.createMap("iloIP", iloIp));
		if (StrUtils.checkParam(asmMaster)) {
			return asmMaster;
		}
		return null;
	}
	
	private NetPorts getNetPort(String masterId, String mac) {
		Map<String, Object> queryMap = StrUtils.createMap("masterId", masterId);
		queryMap.put("mac", mac);
		NetPorts netPort = this.netPortsBiz.singleByClass(NetPorts.class, queryMap);
		if (!StrUtils.checkParam(netPort)) {
			netPort = new NetPorts();
			netPort.setMac(mac);
			netPort.setMasterId(masterId);
			netPort.setEthType("1");// TODO: 2017/8/29  临时设值
			netPort.setPortType("1");// TODO: 2017/8/29  临时设值
			netPort.setSeq(10050);// TODO: 2017/8/29  临时设值
			netPort.createdUser(this.getLoginUser());
			netPortsBiz.add(netPort);
		}
		return netPort;
	}
	
	private IpRelation getIpRelation(String masterId, String ip, String ncId) {
		Map<String, Object> queryMap = StrUtils.createMap("assetId", masterId);
		queryMap.put("ip", ip);
		IpRelation ipRelation = ipRelationBiz.singleByClass(IpRelation.class, queryMap);
		if (!StrUtils.checkParam(ipRelation)) {
			ipRelation = new IpRelation();
			ipRelation.setIp(ip);
			ipRelation.setAssetId(masterId);
			ipRelation.setNcid(ncId);
			ipRelation.setIsIlop(1);
			ipRelation.createdUser(this.getLoginUser());
			ipRelationBiz.add(ipRelation);
		} else {
			if (!ncId.equals(ipRelation.getNcid())) {
				ipRelation.setNcid(ncId);
				ipRelationBiz.update(ipRelation);
			}
		}
		return ipRelation;
	}
	
	private String getDefaultTemplateId(String os, String type) {
		Map<String, Object> queryMap = StrUtils.createMap("interfaceType", "%" + type.toUpperCase() + "%");
		queryMap.put("os", "windows".equals(os) ? "%OS_WINDOWS%" : "%OS_LINUX%");
		List<Map<String, Object>> list = sqlQueryBiz.queryByName(SqlQueryProperty.QUERY_HOST_TEMPLATE, queryMap);
		if (StrUtils.checkCollection(list)) {
			return StrUtils.tranString(list.get(0).get("templateid"));
		}
		return null;
	}
	
	public Object getAllDate(String path) {
		System.out.println("into getAllDate");
		File file = new File(path);
		Workbook rwb = null;
		try {
			rwb = Workbook.getWorkbook(file);
		} catch (Exception e) {
			return BaseRestControl.tranReturnValue(ResultType.file_content_error);
		}
		
		//文件是否有资产信息的标识
		boolean flag = false;
		
		// 主机信息
		System.out.println("getHostInfo");
		Sheet hostRs = rwb.getSheet("主机信息");
		if (null != hostRs) {
			int hostRows = hostRs.getRows();// 得到总行数
			int hostCols = hostRs.getColumns();// 得到总列数
			String type = this.getType(ConfigProperty.CMDB_ASSET_TYPE_SERVER);// 根据类型名获取类型id
			if(!StrUtils.checkParam(type)) {
				throw MessageException.create("没有主机信息设备类型", ResultType.assetType_error);
			}
			
			// 设置扩展属性列
			for (int j = 0; j < hostCols; j++) {
				String name = hostRs.getCell(j, 0).getContents().trim();
				if (null != name && !StrUtils.equals(name, "服务器iLO名称", "管理地址", "上架时间", "序列号", "机房", "资产编号", "型号", "")) {
					this.setExtColumns(type, name);
				}
			}
			
			// 设置设备型号
			this.setAssMode(hostRs, hostRows, hostCols, type);
			
			// 错误信息
			ResultType rs = ResultType.success;
			StringBuffer error = new StringBuffer("主机信息");
			for (int i = 1; i < hostRows; i++) {
				error.append("第" + i + "行");
				
				// 过滤空数据行
				if (!StrUtils.checkParam(hostRs.getCell(0, i).getContents().trim())
						&& !StrUtils.checkParam(hostRs.getCell(1, i).getContents().trim())
						&& !StrUtils.checkParam(hostRs.getCell(2, i).getContents().trim())) {
					LogUtils.info(DataImportRest.class, "主机信息第" + (i + 1) + "行数据为空数据");
					continue;
				}
				
				flag = true;//至少有一条数据
				
				AsmMaster asmMaster = null;
				// 判断资产是否存在,并处理
				Map<String, Object> map = this.checkAsmMaster(asmMaster, type, hostRs, hostCols, i);
				asmMaster = (AsmMaster) map.get("asmMaster");
				String check = StrUtils.tranString(map.get("flag"));
				if("failure".equals(check)) {
					rs = ResultType.serial_not_null;
					break;
				} else if ("repeat".equals(check)) {
					rs = ResultType.assetId_repeat;
					break;
				}
				
				String row = "";
				String col = "";
				String uNumb = "";
				String roomName = "";
				// 设置资产基本属性
				for (int j = 0; j < hostCols; j++) {
					String name = hostRs.getCell(j, 0).getContents().trim();// 列名
					String value = hostRs.getCell(j, i).getContents().trim();// 列值
					// 设置资产基础属性
					if ("服务器iLO名称".equals(name)) {
						// 初始U数处理
						uNumb = value.substring(value.lastIndexOf("-") + 1, value.length() - 1);
						// 机位所在行列处理
						String draw = value.substring(0, 3);
						row = draw.substring(0, 1);
						col = draw.substring(1);
						char rowChar = row.charAt(0);
						// 转化ABC为123
						row = ((Integer) (rowChar - 'A' + 1)).toString();
						boolean repeat = this.asmMasterDao.checkRepeat(AsmMaster.class, StrUtils.createMap
								("assetName", value), asmMaster.getId());
						if (!repeat) {
							rs = ResultType.name_repeat;
							break;
						}
						asmMaster.setAssetName(value);
					} else if ("管理地址".equals(name)) {
						boolean repeat = this.asmMasterDao.checkRepeat(AsmMaster.class, StrUtils.createMap
								("iloIP", value), asmMaster.getId());
						if (!repeat) {
							rs = ResultType.iloip_repeat;
							break;
						}
						asmMaster.setIloIP(value);
					} else if ("上架时间".equals(name)) {
						if (null == value || "".equals(value)) {
							asmMaster.setBegDate(new Date());
						} else {
							asmMaster.setBegDate(DateUtils.getDateByString(value));
						}
					} else if ("型号".equals(name)) {
						Map<String, Object> queryMap = StrUtils.createMap("resType", type);
						queryMap.put("itemName", value);
						Class2Items class2Item = class2ItemsBiz.singleByClass(Class2Items.class, queryMap);
						asmMaster.setAssMode(class2Item.getId());
					} else if ("环境类型".equals(name)) {
						asmMaster.setUseFlag(value);
					} else if("机房".equals(name)){
						roomName = value;
					}
				}
				
				if (!ResultType.success.equals(rs)) {
					break;
				}
				
				//保存或修改资产信息
				this.saveOrUpdate(check, asmMaster);
				
				// 机房处理
				roomName = "".equals(roomName) ? "default" : roomName;
				rs = this.roomHandle(row, col, asmMaster, uNumb, roomName);
				if (!ResultType.success.equals(rs)) {
					break;
				}
				
				// 设置资产扩展属性值
				for (int j = 0; j < hostCols; j++) {
					String name = hostRs.getCell(j, 0).getContents().trim();// 列名
					String value = hostRs.getCell(j, i).getContents().trim();// 列值
					// 设置资产扩展属性
					if (null != name && !StrUtils.equals(name, "服务器iLO名称", "管理地址", "上架时间", "序列号", "机房", "资产编号", "型号", "")) {
						// 设置资产扩展属性
						rs = this.setExtAValue(name, value, asmMaster.getId(), type);
						if (!ResultType.success.equals(rs)) {
							break;
						}
					}
				}
			}
			
			if (!ResultType.success.equals(rs)) {
				throw MessageException.create(error.append(rs.getOpeValue()).toString(), rs);
			}
			System.out.println("host over");
		}
		
		// 网络设备信息
		System.out.println("getswitch");
		Sheet switchRs = rwb.getSheet("网络设备信息");
		if (null != switchRs) {
			int switchRows = switchRs.getRows();// 得到总行数
			int switchCols = switchRs.getColumns();// 得到总列数
			String type = getType(ConfigProperty.CMDB_ASSET_TYPE_SWITCH);// 根据类型名获取类型id
			if(!StrUtils.checkParam(type)) {
				throw MessageException.create("没有网络设备信息设备类型", ResultType.assetType_error);
			}
			
			// 导入设备型号
			this.setAssMode(switchRs, switchRows, switchCols, type);
			
			// 设置扩展属性列
			for (int j = 0; j < switchCols; j++) {
				String name = switchRs.getCell(j, 0).getContents().trim();
				if (null != name && !StrUtils.equals(name, "设备型号", "IP地址", "型号", "序列号", "机房", "资产编号", "机位", "")) {
					this.setExtColumns(type, name);
				}
			}
			
			// 错误信息
			ResultType rs = ResultType.success;
			StringBuffer error = new StringBuffer("网络设备信息");
			for (int i = 1; i < switchRows; i++) {
				error.append("第" + i + "行");
				
				// 过滤空数据行
				if (!StrUtils.checkParam(switchRs.getCell(1, i).getContents().trim())
						&& !StrUtils.checkParam(switchRs.getCell(2, i).getContents().trim())
						&& !StrUtils.checkParam(switchRs.getCell(0, i).getContents().trim())) {
					LogUtils.info(DataImportRest.class, "网络设备信息第" + (i + 1) + "行数据为空数据");
					continue;
				}
				
				flag = true;//至少有一条数据
				
				AsmMaster asmMaster = null;
				// 判断资产是否存在,并处理
				Map<String, Object> map = checkAsmMaster(asmMaster, type, switchRs, switchCols, i);
				asmMaster = (AsmMaster) map.get("asmMaster");
				String check = StrUtils.tranString(map.get("flag"));
				if("failure".equals(check)) {
					rs = ResultType.serial_not_null;
					break;
				} else if ("repeat".equals(check)) {
					rs = ResultType.assetId_repeat;
					break;
				}
				
				String roomName = "";
				String row = "";
				String col = "";
				
				// 设置资产基本属性
				for (int j = 0; j < switchCols; j++) {
					String name = switchRs.getCell(j, 0).getContents().trim();// 列名
					String value = switchRs.getCell(j, i).getContents().trim();// 列值
					
					// 设置资产基础属性
					if ("设备型号".equals(name)) {
						asmMaster.setAssetName(value);
						boolean repeat = this.asmMasterDao.checkRepeat(AsmMaster.class, StrUtils.createMap
								("assetName", value), asmMaster.getId());
						if (!repeat) {
							rs = ResultType.name_repeat;
							break;
						}
					} else if ("型号".equals(name)) {
						Map<String, Object> queryMap = StrUtils.createMap("resType", type);
						queryMap.put("itemName", value);
						Class2Items class2Item = class2ItemsBiz.singleByClass(Class2Items.class, queryMap);
						asmMaster.setAssMode(class2Item.getId());
					} else if ("IP地址".equals(name)) {
						asmMaster.setIloIP(value.trim());
					} else if ("序列号".equals(name)) {
						continue;
					} else if ("机房".equals(name)) {
						roomName = value;
					} else if ("机位".equals(name)) {
						if (StrUtils.checkParam(value)) {
							if (value.contains("-")) {
								row = value.split("-")[0];
								col = value.split("-")[1];
							} else {
								row = value.substring(0, 1);
								col = value.substring(1);
							}
							char rowChar = row.charAt(0);
							row = ((Integer) (rowChar - 'A' + 1)).toString();
						}
					}
				}
				
				if (!ResultType.success.equals(rs)) {
					break;
				}
				
				// 导入资产数据
				this.saveOrUpdate(check, asmMaster);
				
				// 处理机房
				roomName = "".equals(roomName) ? "default": roomName;
				rs = this.roomHandle(row, col, asmMaster, null, roomName);
				if (!ResultType.success.equals(rs)) {
					break;
				}
				
				// 设置扩展属性值
				for (int j = 0; j < switchCols; j++) {
					String name = switchRs.getCell(j, 0).getContents().trim();// 列名
					String value = switchRs.getCell(j, i).getContents().trim();// 列值
					// 设置资产扩展属性
					if (null != name && !StrUtils.equals(name, "设备型号", "IP地址", "型号", "序列号", "机房", "资产编号", "机位", "")) {
						rs = this.setExtAValue(name, value, asmMaster.getId(), type);
						if (!ResultType.success.equals(rs)) {
							break;
						}
					}
				}
			}
			
			if (!ResultType.success.equals(rs)) {
				throw MessageException.create(error.append(rs.getOpeValue()).toString(), rs);
			}
			
			System.out.println("switch over");
		}
		
		// 存储信息
		System.out.println("getStockInfo");
		Sheet storageRs = rwb.getSheet("存储信息");
		if (null != storageRs) {
			int storageRows = storageRs.getRows();// 得到总行数
			int storageCols = storageRs.getColumns();// 得到总列数
			String type = getType(ConfigProperty.CMDB_ASSET_TYPE_STOCK);// 根据类型名获取类型id
			if(!StrUtils.checkParam(type)) {
				throw MessageException.create("没有存储信息设备类型", ResultType.assetType_error);
			}
			
			// 设置设备型号
			this.setAssMode(storageRs, storageRows, storageCols, type);
			
			// 设置扩展属性列
			for (int j = 0; j < storageCols; j++) {
				String name = storageRs.getCell(j, 0).getContents().trim();
				if (null != name && !StrUtils.equals("序号", "集群", "机型", "机房", "资产编号", "起始U数", "服务器位置", "")) {
					this.setExtColumns(type, name);
				}
			}
			
			// 错误信息
			ResultType rs = ResultType.success;
			StringBuffer error = new StringBuffer("存储信息");
			for (int i = 1; i < storageRows; i++) {
				error.append("第" + i + "行");
				
				//过滤空数据行
				if (!StrUtils.checkParam(storageRs.getCell(1, i).getContents().trim())
						&& !StrUtils.checkParam(storageRs.getCell(0, i).getContents().trim())) {
					LogUtils.info(DataImportRest.class, "存储信息第" + (i + 1) + "行数据为空数据");
					continue;
				}
				
				flag = true;//至少有一条数据
				
				AsmMaster asmMaster = null;
				// 判断资产是否存在,并处理
				Map<String, Object> map = checkAsmMaster(asmMaster, type, storageRs, storageCols, i);
				asmMaster = (AsmMaster) map.get("asmMaster");
				String check = StrUtils.tranString(map.get("flag"));
				if("failure".equals(check)){
					rs = ResultType.serial_not_null;
					break;
				} else if ("repeat".equals(check)) {
					rs = ResultType.assetId_repeat;
					break;
				}
				
				String row = "";
				String col = "";
				String roomName = "";
				String uNumb = "";
				// 设置资产基本属性
				for (int j = 0; j < storageCols; j++) {
					String name = storageRs.getCell(j, 0).getContents().trim();// 列名
					String value = storageRs.getCell(j, i).getContents().trim();// 列值
					// 设置资产基础属性
					if ("序号".equals(name)) {
						continue;
					} else if ("集群".equals(name)) {
						if (StrUtils.checkParam(value)) {
							asmMaster.setGroupId(value);
						}
					} else if ("服务器位置".equals(name)) {
						// 获取机柜行列
						row = value.substring(0, 1);
						col = value.substring(1);
						char rowChar = row.charAt(0);
						row = ((Integer) (rowChar - 'A' + 1)).toString();
					} else if ("起始U数".equals(name)) {
						// 获取起始U数
						uNumb = value.substring(value.lastIndexOf("-") + 1, value.length() - 1);
						// 设置资产名
						asmMaster.setAssetName(value);
						boolean repeat = this.asmMasterDao.checkRepeat(AsmMaster.class, StrUtils.createMap
								("assetName", value), asmMaster.getId());
						if (!repeat) {
							rs = ResultType.name_repeat;
							break;
						}
					} else if ("机型".equals(name)) {
						Map<String, Object> queryMap = StrUtils.createMap("resType", type);
						queryMap.put("itemName", value);
						Class2Items class2Item = class2ItemsBiz.singleByClass(Class2Items.class, queryMap);
						asmMaster.setAssMode(class2Item.getId());
					} else if("机房".equals(name)){
						roomName=value;
					}
				}
				
				if (!ResultType.success.equals(rs)) {
					break;
				}
				
				//保存或修改资产信息
				this.saveOrUpdate(check, asmMaster);
				
				//保存机房信息
				roomName = "".equals(roomName) ? "default" : roomName;
				rs = this.roomHandle(row, col, asmMaster, uNumb, roomName);
				if (!ResultType.success.equals(rs)) {
					break;
				}
				
				// 设置资产扩展属性
				for (int j = 0; j < storageCols; j++) {
					String name = storageRs.getCell(j, 0).getContents().trim();// 列名
					String value = storageRs.getCell(j, i).getContents().trim();// 列值
					// 设置资产基础属性
					if (null != name && !StrUtils.equals("序号", "集群", "机型", "机房", "资产编号", "起始U数", "服务器位置", "")) {
						rs = this.setExtAValue(name, value, asmMaster.getId(), type);
						if (!ResultType.success.equals(rs)) {
							break;
						}
					}
				}
			}
			
			if (!ResultType.success.equals(rs)) {
				throw MessageException.create(error.append(rs.getOpeValue()).toString(), rs);
			}
			
			System.out.println("stock over");
		}
		
		if (!flag) {
			throw MessageException.create(ResultType.lack_of_master_message);
		}
		
		// 线缆关系
		System.out.println("get link");
		Sheet linkRs = rwb.getSheet("线缆关系");
		if(null != linkRs){
			int linkRows = linkRs.getRows();
			int linkCols = linkRs.getColumns();
			for (int i = 1; i < linkRows; i++) {
				String vlan = "";
				AsmMaster trunkAsmMaster = null;
				Integer trunkPort = null;
				Integer accessPort = null;
				
				//对端设备查询条件
				Map<String, Object> accessMap = new HashedMap();
				
				//过滤空数据行
				if (!StrUtils.checkParam(linkRs.getCell(1, i).getContents().trim())
						&& !StrUtils.checkParam(linkRs.getCell(0, i).getContents().trim())) {
					LogUtils.info(DataImportRest.class, "线缆关系第" + (i + 1) + "行数据为空数据");
					continue;
				}
				
				//错误信息
				ResultType rs = ResultType.success;
				StringBuffer error = new StringBuffer("线缆关系第" + i + "行");
				for (int j = 0; j < linkCols; j++) {
					String name = linkRs.getCell(j, 0).getContents().trim();// 列名
					String value = linkRs.getCell(j, i).getContents().trim();// 列值
					
					if ("本端设备名称".equals(name)) {
						if (!StrUtils.checkParam(value)) {
							rs = ResultType.trunk_master_not_null;
							break;
						}
						trunkAsmMaster = asmMasterDao.singleByClass(AsmMaster.class, StrUtils.createMap
								("assetName", value));
						if (!StrUtils.checkParam(trunkAsmMaster)) {
							rs = ResultType.trunk_master_not_exist;
							break;
						}
					} else if ("本端端口".equals(name)) {
						if (StrUtils.checkParam(value) && value.contains("/")) {
							// 端口格式(G1/0/41) 截取最后的数字
							value = value.substring(value.lastIndexOf("/") + 1);
							trunkPort = Integer.parseInt(value);
						} else {
							rs = ResultType.trunk_port_error;
							break;
						}
					} else if ("对端设备功能".equals(name)) {
						String assetTypeCode;
						// 根据设备功能,找出所有的满足的设备
						if ("服务器".equals(value)) {
							assetTypeCode = ConfigProperty.CMDB_ASSET_TYPE_SERVER;
						} else if ("交换机".equals(value)) {
							assetTypeCode = ConfigProperty.CMDB_ASSET_TYPE_SWITCH;
						} else if ("路由器".equals(value)) {
							assetTypeCode = ConfigProperty.CMDB_ASSET_TYPE_ROUTER;
						} else if ("存储".equals(value)) {
							assetTypeCode = ConfigProperty.CMDB_ASSET_TYPE_STOCK;
						} else if ("板卡".equals(value)) {
							assetTypeCode = ConfigProperty.CMDB_ASSET_TYPE_BOARDS;
						} else {
							assetTypeCode = ConfigProperty.CMDB_ASSET_TYPE_OTHER;
						}
						InitCode initCode = this.initCodeBiz.singleByClass(InitCode.class, StrUtils.createMap
								("codeId", assetTypeCode));
						if (!StrUtils.checkParam(initCode)) {
							rs = ResultType.assetType_error;
							break;
						}
						accessMap.put("assetType", initCode.getId());
					} else if ("对端设备名称".equals(name)) {
						// 处理对端设备名称(A07-服务器-10)
						if (!StrUtils.checkParam(value)) {
							rs = ResultType.access_master_not_null;
							break;
						}
						accessMap.put("assetName", value);
					} else if ("对端端口".equals(name)) {
						if (StrUtils.checkParam(value)) {
							// 格式有(存储-1口)(eth-1)(TG1/0/0/23)(fc-1)
							if (value.contains("/")) { // 处理对端端口
								value = value.substring(value.lastIndexOf("/") + 1);
							} else if (value.contains("-")) {
								if(value.startsWith("eth")){
									value="2"+value.substring(value.lastIndexOf("-") + 1);
								}else if(value.startsWith("fc")){
									value ="1"+value.substring(value.lastIndexOf("-") + 1);
								}else if(value.startsWith("FC")){
									value ="3"+value.substring(value.lastIndexOf("-") + 1);
								}else if(value.startsWith("c")){
									value ="4"+value.substring(value.lastIndexOf("-") + 1);
								}else if(value.startsWith("g")){
									value ="5"+value.substring(value.lastIndexOf("-") + 1);
								}else{
									value =value.substring(value.lastIndexOf("-") + 1);
								}
								if (!StrUtils.isInteger(value)) {
									value = value.substring(0, 1);
								}
							}
							accessPort = Integer.parseInt(value);
						} else {
							rs = ResultType.access_port_not_null;
							break;
						}
					} else if ("VLAN".equals(name)) {
						vlan = value;
					}
				}
				
				if (!ResultType.success.equals(rs)) {
					throw MessageException.create(error.append(rs.getOpeValue()).toString(), rs);
				}
				
				// 本端端口
				String trunkNetPortId = this.saveNetport(trunkAsmMaster.getId(), trunkPort);
				
				// 对端端口
				// 查询对端设备
				AsmMaster accessAsmMaster = this.asmMasterDao.singleByClass(AsmMaster.class, accessMap);
				if (!StrUtils.checkParam(accessAsmMaster)) {
					throw MessageException.create(error.append("对端设备不存在").toString(), ResultType.access_master_not_exist);
				}
				
				String accessNetPortId = this.saveNetport(accessAsmMaster.getId(), accessPort);
				
				// 线路关系
				Map<String, Object> linkMap = new HashMap<>();
				linkMap.put("trunkTo", trunkNetPortId);
				linkMap.put("accessTo", accessNetPortId);
				NetPortsLink netPortsLink = netPortsLinkBiz.singleByClass(NetPortsLink.class, linkMap);
				if (!StrUtils.checkParam(netPortsLink)) {
					netPortsLink = new NetPortsLink();
					netPortsLink.setId(UUID.randomUUID().toString());
					netPortsLink.setTrunkTo(trunkNetPortId);
					netPortsLink.setAccessTo(accessNetPortId);
					netPortsLink.setVlan(vlan);
					netPortsLink.createdUser(getLoginUser());
					netPortsLinkBiz.add(netPortsLink);
				}
			}
			
			System.out.println("link over");
		}
		
		return BaseRestControl.tranReturnValue(ResultType.success);
	}
	
	/**
	 * 机房处理
	 *
	 * @param row
	 *            机柜所在行
	 * @param col
	 *            机柜坐在列
	 * @param asmMaster
	 *            资产
	 * @param uNumb
	 *            起始U数
	 * @param roomName
	 *            机房名
	 */
	private ResultType roomHandle(String row, String col, AsmMaster asmMaster, String uNumb, String roomName) {
		if (!StrUtils.checkParam(row, col, uNumb)) {
			return ResultType.success;
		}
		
		// 查看机房是否存在 不存在则新增一个默认机房
		Rooms room = roomsBiz.singleByClass(Rooms.class, StrUtils.createMap("roomName", roomName));
		if (!StrUtils.checkParam(room)) {
			room = new Rooms();
			room.setId(UUID.randomUUID().toString());
			room.setRoomName(roomName);
			room.setMaxRows(10);// TODO 临时设置
			room.setMaxCols(10);// TODO 临时设置
			room.setDefaultU(43);
			room.setRegion("unknow");// TODO 临时设置
			room.setLocalAdminTel("unknow");// TODO 临时设置
			room.setSupplier("unknow");// TODO 临时设置
			room.setContact("unknow");// TODO 临时设置
			room.createdUser(getLoginUser());
			roomsBiz.add(room);
		}
		
		// 查看机柜是否超过机房行列数
		if (Integer.valueOf(row) > room.getMaxRows() || Integer.valueOf(col) > room.getMaxCols()) {
			return ResultType.draw_row_or_col_over;
		}
		
		// 查看是否有该机柜
		Map<String, Object> queryMap = StrUtils.createMap("roomId", room.getId());
		queryMap.put("rowNum", Integer.valueOf(row));
		queryMap.put("colNum", Integer.valueOf(col));
		Draws draws = drawsBiz.singleByClass(Draws.class, queryMap);
		if (!StrUtils.checkParam(draws)) {
			return ResultType.draw_not_exist;
		}
		
		// 校验机柜占用情况
		Asset2Drawer asset2Drawer = asset2DrawerBiz.findById(Asset2Drawer.class, asmMaster.getId());
		boolean isExist = null != asset2Drawer;
		if (isExist) {
			asset2Drawer.setDrawsId(draws.getId());
			asset2Drawer.setUnumb(Integer.parseInt(uNumb));
			asset2Drawer.updatedUser(getLoginUser());
		} else {
			asset2Drawer = new Asset2Drawer();
			asset2Drawer.setId(asmMaster.getId());
			asset2Drawer.setDrawsId(draws.getId());
			asset2Drawer.setUnumb(Integer.parseInt(uNumb));
			asset2Drawer.createdUser(getLoginUser());
		}
		ResultType rs = asset2DrawerBiz.checkAsset2Draw(asmMaster, asset2Drawer, asmMaster.getAssetType());
		if(ResultType.success.equals(rs)) {	// 机柜信息异常
			if (isExist) {
				asset2DrawerBiz.update(asset2Drawer);
			} else {
				asset2DrawerBiz.add(asset2Drawer);
			}
		}
		return rs;
	}
	
	/**
	 * 设置设备型号
	 *
	 * @param rows
	 *            总行数
	 * @param rs
	 *            excle表
	 * @param cols
	 *            总列数
	 */
	private void setAssMode(Sheet rs, int rows, int cols, String type) {
		for (int i = 1; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				String name = rs.getCell(j, 0).getContents().trim();
				String value = rs.getCell(j, i).getContents().trim();
				
				if ("型号".equals(name) || "机型".equals(name) && StrUtils.checkParam(value)) {
					Map<String, Object> queryMap = StrUtils.createMap("itemName", value);
					queryMap.put("resType", type);
					Class2Items class2Item = class2ItemsBiz.singleByClass(Class2Items.class, queryMap);
					if (!StrUtils.checkParam(class2Item)) {
						class2Item = new Class2Items();
						class2Item.setId(UUID.randomUUID().toString());
						class2Item.setFlag(ConfigProperty.YES);
						class2Item.setItemName(value);
						class2Item.setResType(type);
						class2Item.setItemId(UUID.randomUUID().toString());
						class2Item.createdUser(getLoginUser());
						class2Item.setUnum(3); // 默认占用3U
						class2ItemsBiz.add(class2Item);
					}
				}
			}
			
		}
	}
	
	/**
	 * 根据资产类型名获取资产类型id
	 *
	 * @param typeCode
	 * @return
	 */
	private String getType(String typeCode) {
		InitCode initCode = null;
		String type = null;
		List<InitCode> initCodes = initCodeBiz.findByPropertyName(InitCode.class, "codeId", typeCode);
		if (StrUtils.checkParam(initCodes)) {
			initCode = initCodes.get(0);
			type = initCode.getId();
		}
		return type;
	}
	
	/**
	 * 根据序列号查看资产是否存在,并做处理
	 *
	 * @param asmMaster
	 *            资产
	 * @param type
	 *            资产类型
	 * @param rs
	 *            表
	 * @param cols
	 *            总列数
	 * @param i
	 *            行号
	 * @return
	 */
	private Map<String, Object> checkAsmMaster(AsmMaster asmMaster, String type, Sheet rs, int cols, int i) {
		String flag = "";
		Map<String, Object> map = new HashMap<>(2);
		String assetId = null;
		boolean flag1 = false;
		boolean flag2 = false;
		for (int j = 0; j < cols; j++) {
			String name = rs.getCell(j, 0).getContents().trim();
			String value = rs.getCell(j, i).getContents().trim();
			if ("序列号".equals(name) || "序号".equals(name)) {
				if(StrUtils.checkParam(value)){
					flag1 = true;
					asmMaster = asmMasterDao.singleByClass(AsmMaster.class, StrUtils.createMap("serial", value));
					if (!StrUtils.checkParam(asmMaster)) {// 不存在做初始化处理
						asmMaster = new AsmMaster();
						this.initAsmMaster(asmMaster);
						asmMaster.setSerial(value);
						asmMaster.setAssetType(type);
						flag = "add";
					} else {//存在做修改处理
						flag = "update";
					}
				}
			} else if("资产编号".equals(name)){
				if(StrUtils.checkParam(value)){
					flag2 = true;
					assetId = value;
					asmMaster.setAssetId(assetId);
				}
			}
		}
		
		if (flag1 && flag2) {//查看资产编号是否重复
			if (!this.asmMasterDao.checkRepeat(AsmMaster.class, StrUtils.createMap("assetId", assetId), asmMaster
					.getId())) {
				flag = "repeat";
			}
		} else if (flag1 && !flag2) {
			assetId = UUID.randomUUID().toString();
			asmMaster.setAssetId(assetId);
		} else {
			flag = "failure";
		}
		
		map.put("asmMaster", asmMaster);
		map.put("flag", flag);
		return map;
	}
	
	/**
	 * 设置扩展属性列
	 *
	 * @param type
	 *            资产类型
	 * @param name
	 *            excle表列名
	 */
	private void setExtColumns(String type, String name) {
		Map<String, Object> map = StrUtils.createMap("assType", type);
		map.put("xcName", name);
		ExtColumns extColumn = extColumnsBiz.singleByClass(ExtColumns.class, map);
		if (!StrUtils.checkParam(extColumn)) {
			extColumn = new ExtColumns();
			extColumn.setId(UUID.randomUUID().toString());
			extColumn.setAssType(type);
			extColumn.setXcName(name);
			extColumn.setXcType("string");// TODO 临时设置
			extColumn.setXcLength(36);// TODO 临时设置
			extColumn.createdUser(getLoginUser());
			extColumnsBiz.add(extColumn);
		}
	}
	
	/**
	 * 设置扩展属性
	 *
	 * @param name
	 *            列名
	 * @param value
	 *            列值
	 * @param masterId
	 *            资产id
	 * @param type
	 *            资产类型
	 */
	private ResultType setExtAValue(String name, String value, String masterId, String type) throws MessageException {
		// 该资产类型包含的扩展属性
		Map<String, Object> map = StrUtils.createMap("assType", type);
		map.put("xcName", name);
		ExtColumns extColumn = extColumnsBiz.singleByClass(ExtColumns.class, map);
		
		if (StrUtils.checkParam(extColumn)) {
			if (value.length() > extColumn.getXcLength()) {
				return ResultType.out_of_extcolumn_length;
			}
			map.clear();
			map.put("assetID", masterId);
			map.put("extID", extColumn.getId());
			ExtAValue extAValue = extAValueBiz.singleByClass(ExtAValue.class, map);
			if (StrUtils.checkParam(extAValue)) {// 存在改属性值则修改
				extAValue.setExtValue(value);
				extAValue.updatedUser(getLoginUser());
				extAValueBiz.update(extAValue);
			} else {
				// 不存在则添加
				extAValue = new ExtAValue();
				extAValue.setId(UUID.randomUUID().toString());
				extAValue.setExtValue(value);
				extAValue.setAssetID(masterId);
				extAValue.setExtID(extColumn.getId());
				extAValue.createdUser(getLoginUser());
				extAValueBiz.add(extAValue);
			}
		}
		return ResultType.success;
	}
	
	/**
	 * 初始化资产的基本属性
	 *
	 * @param asmMaster
	 */
	private void initAsmMaster(AsmMaster asmMaster) {
		asmMaster.setId(UUID.randomUUID().toString());
		asmMaster.setStatus(ConfigProperty.CMDB_ASSET_FLAG2_USE); // TODO
		asmMaster.setProvide("unknow");// TODO 根据表格数据来赋值,暂无数据,非空属性临时赋值
		asmMaster.setAssetUser("989116e3-79a2-426b-bfbe-668165104885");// TODO 同上
		asmMaster.setAssetTypeCode("server");
		asmMaster.setDepart("8a92408456ba52ab0156ba5339330000");// TODO 临时设置
		asmMaster.createdUser(getLoginUser());
		asmMaster.setBegDate(new Date());
		
	}
	
	private void saveOrUpdate(String option, AsmMaster asmMaster) {
		if ("add".equals(option)) {
			asmMasterDao.add(asmMaster);
		}else if("update".equals(option)){
			asmMasterDao.update(asmMaster);
		}
	}
	
	private String saveNetport(String masterId, int seq) {
		// 根据 资产id 和 端口 找到对应的网口
		Map<String, Object> map = StrUtils.createMap("masterId", masterId);
		map.put("seq", seq);
		NetPorts netPort = netPortsBiz.singleByClass(NetPorts.class, map);
		if (!StrUtils.checkParam(netPort)) {
			netPort = new NetPorts();
			netPort.createdUser(getLoginUser());
			netPort.setEthType("1");// TODO 临时值
			netPort.setPortType("1");// TODO 临时值
			netPort.setMasterId(masterId);
			netPort.setSeq(seq);
			netPort.setId(UUID.randomUUID().toString());
			netPortsBiz.add(netPort);
		}
		return netPort.getId();
	}
}

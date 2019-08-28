package com.h3c.iclouds.rest;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.h3c.iclouds.base.BaseRest;
import com.h3c.iclouds.biz.AsmMasterBiz;
import com.h3c.iclouds.biz.ExtAValueBiz;
import com.h3c.iclouds.biz.ExtColumnsBiz;
import com.h3c.iclouds.biz.NetPortsBiz;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.po.AsmMaster;
import com.h3c.iclouds.po.ExtAValue;
import com.h3c.iclouds.po.ExtColumns;
import com.h3c.iclouds.po.IpRelation;
import com.h3c.iclouds.po.NetPorts;
import com.h3c.iclouds.utils.StrUtils;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

@Api(value = "数据导出")
@RestController
@RequestMapping("/dataexport")
public class DataExportRest extends BaseRest<AsmMaster> {
	@Resource
	ExtColumnsBiz extColumnsBiz;

	@Resource
	ExtAValueBiz extAValueBiz;

	@Resource
	AsmMasterBiz asmMasterBiz;

	@Resource
	NetPortsBiz netPortsBiz;

	@ApiOperation(value = "导出数据")
	@RequestMapping(method = RequestMethod.GET)
	public void export(HttpServletResponse response) {
		OutputStream os=null;
		try {
			os=response.getOutputStream();
			String fileName="资产及链接数据.xls";
			response.reset();// 清空输出流
			//设置为下载模式
	        response.setHeader("Content-disposition", "attachment; filename="+ java.net.URLEncoder.encode(fileName, "UTF-8"));
	        response.setContentType("application/msexcel");// 定义输出类型为excle
			writeExcel(os);
			
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			try {
				if(os!=null){
					os.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	@ApiOperation(value = "导出服务器数据")
	@RequestMapping(value="/{type}",method = RequestMethod.GET)
	public void exportServer(HttpServletResponse response,@PathVariable String type) {
		OutputStream os=null;
		try {
			os=response.getOutputStream();
			String fileName="";
			String name="";
			if(ConfigProperty.CMDB_ASSET_TYPE_SERVER.equals(type)){
				fileName="服务器数据.xls";
				name="服务器信息";
			}else if(ConfigProperty.CMDB_ASSET_TYPE_SWITCH.equals(type)){
				fileName="交换机数据.xls";
				name="交换机信息";
			}else if(ConfigProperty.CMDB_ASSET_TYPE_ROUTER.equals(type)){
				fileName="路由器数据.xls";
				name="路由器信息";
			}else if(ConfigProperty.CMDB_ASSET_TYPE_STOCK.equals(type)){
				fileName="存储设备数据.xls";
				name="存储设备信息";
			}else if(ConfigProperty.CMDB_ASSET_TYPE_FIREWALL.equals(type)){
				fileName="防火墙设备数据.xls";
				name="防火墙设备信息";
			}else if(ConfigProperty.CMDB_ASSET_TYPE_OTHER.equals(type)){
				fileName="其他设备数据.xls";
				name="其他设备信息";
			}
			
			response.reset();// 清空输出流
	        response.setHeader("Content-disposition", "attachment; filename="+ java.net.URLEncoder.encode(fileName, "UTF-8"));
	        response.setContentType("application/msexcel");// 定义输出类型
	        writeOne(os,type,name);
	        os.close();
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			try {
				if(os!=null){
					os.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void writeOne(OutputStream os,String type,String name) {
		WritableWorkbook workbook=null;
		System.out.println("start export");
		try {
			workbook = Workbook.createWorkbook(os); // 创建Excel文件
			
			WritableSheet serverSheet = workbook.createSheet(name, 0); // 创建工作簿
			setContent(serverSheet,type);   // 设置基础属性和扩展属性
			System.out.println("begin write");
			workbook.write();//写入excle
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (workbook != null) {
					workbook.close();
				}
			} catch (WriteException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println("end export");
	}

	/**
	 * 导出Excel表
	 * 
	 * @param rs
	 *            数据库结果集
	 * @param filePath
	 *            要保存的路径，文件名为 fileName.xls
	 * @param sheetName
	 *            工作簿名称 工作簿名称，本方法目前只支持导出一个Excel工作簿
	 * @param columnName
	 *            列名，类型为List<string>
	 */
	public void writeExcel(OutputStream os) {
		WritableWorkbook workbook = null;
		
		System.out.println("start export");
		
		try {

			workbook = Workbook.createWorkbook(os); // 创建Excel文件
			
			WritableSheet serverSheet = workbook.createSheet("服务器信息", 0); // 创建工作簿
			WritableSheet switchSheet = workbook.createSheet("交换机信息", 1); // 创建工作簿
			WritableSheet routerSheet = workbook.createSheet("路由器信息", 2); // 创建工作簿
			WritableSheet stockSheet = workbook.createSheet("存储信息", 3); // 创建工作簿
			WritableSheet otherSheet = workbook.createSheet("其他设备信息", 4); // 创建工作簿
			WritableSheet linkToSheet = workbook.createSheet("链接关系", 5); // 创建工作簿
			
			setContent(serverSheet,"server");   // 设置excle内容
			setContent(switchSheet,"switch");
			setContent(routerSheet,"router");
			setContent(stockSheet,"stock");
			setContent(otherSheet,"other");
			
			setLinkTo(linkToSheet);// 设置链接关系
			
			System.out.println("begin write");
			workbook.write();//写入excle

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (workbook != null) {
					workbook.close();
				}
			} catch (WriteException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println("end export");
	}
	/**
	 * 设置链接关系
	 * @param sheet
	 * @throws RowsExceededException
	 * @throws WriteException
	 */
	private void setLinkTo(WritableSheet sheet) throws RowsExceededException, WriteException {
		
		// 设置excle的列名
		List<String> columns=new ArrayList<>();
		columns.add("本端设备名称");
		columns.add("本端端口编号");
		columns.add("本端端口类型");
		columns.add("本端网口类型");
		columns.add("本端MAC地址");
		columns.add("本端IP地址");
		columns.add("VLAN");
		columns.add("掩码");
		columns.add("对端设备功能");
		columns.add("对端设备名称");
		columns.add("对端端口编号");
		columns.add("对端端口类型");
		columns.add("对端网口类型");
		columns.add("对端MAC地址");
		columns.add("对端IP地址");
		writeCol(sheet, columns, 0);
		
		List<NetPorts> netPorts=netPortsBiz.findByHql("FROM NetPorts WHERE id=trunkTo");
		System.out.println(netPorts.size());
		int rowNum=1;
		Label label = null;
		for(NetPorts n:netPorts){
			NetPorts nPorts=netPortsBiz.findById(NetPorts.class, n.getAccessTo());
			for(int j=0;j<columns.size();j++){
				if("本端设备名称".equals(columns.get(j))){
					label = new Label(j, rowNum,n.getTrunkName());
					sheet.addCell(label);
				}else if("本端端口编号".equals(columns.get(j))){
					label = new Label(j, rowNum,n.getTrunkSeq());
					sheet.addCell(label);
				}else if("本端端口类型".equals(columns.get(j))){
					label = new Label(j, rowNum,"1".equals(n.getPortType())?"电口":"光口");
					sheet.addCell(label);
				}else if("本端网口类型".equals(columns.get(j))){
					label = new Label(j, rowNum,"1".equals(n.getEthType())?"物理网口":"虚拟网口");
					sheet.addCell(label);
				}else if("本端MAC地址".equals(columns.get(j))){
					label = new Label(j, rowNum,n.getTrunkMac());
					sheet.addCell(label);
				}else if("VLAN".equals(columns.get(j))){
					label = new Label(j, rowNum,n.getTrunkVlan());
					sheet.addCell(label);
				}else if("本端IP地址".equals(columns.get(j))){
					List<IpRelation> ips=n.getIps();
					if(StrUtils.checkParam(ips)){
						label = new Label(j, rowNum,ips.get(0).getIp());
						sheet.addCell(label);
					}
				}else if("掩码".equals(columns.get(j))){
					label = new Label(j, rowNum,"");
					sheet.addCell(label);
				}else if("对端设备功能".equals(columns.get(j))){
					if(nPorts!=null){
						AsmMaster asmMaster=asmMasterBiz.findById(AsmMaster.class, nPorts.getMasterId());
						if(asmMaster!=null){
							label = new Label(j, rowNum,asmMaster.getAssetTypeName());
							sheet.addCell(label);
						}
					}
				}else if("对端设备名称".equals(columns.get(j))){
					label = new Label(j, rowNum,n.getAccessName());
					sheet.addCell(label);
				}else if("对端端口编号".equals(columns.get(j))){
					label = new Label(j, rowNum,n.getAccessSeq());
					sheet.addCell(label);
				}else if("对端端口类型".equals(columns.get(j))){
					if(nPorts!=null){
						label = new Label(j, rowNum,"1".equals(nPorts.getPortType())?"电口":"光口");
						sheet.addCell(label);
					}
				}else if("对端网口类型".equals(columns.get(j))){
					if(nPorts!=null){
						label = new Label(j, rowNum,"1".equals(n.getEthType())?"物理网口":"虚拟网口");
						sheet.addCell(label);
					}
				}else if("对端MAC地址".equals(columns.get(j))){
					label = new Label(j, rowNum,n.getAccessMac());
					sheet.addCell(label);
				}else if("对端IP地址".equals(columns.get(j))){
					List<IpRelation> ips=nPorts.getIps();
					if(StrUtils.checkParam(ips)){
						label = new Label(j, rowNum,n.getTrunkName());
						sheet.addCell(label);
					}
				}
			}
			rowNum++;
		}
		
	}

	public WritableSheet setContent(WritableSheet sheet,String type) throws RowsExceededException, WriteException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		
		// 判断资产类型
		String typeName="server".equals(type)?"服务器":
			"stock".equals(type)?"存储":
				"switch".equals(type)?"交换机":
					"router".equals(type)?"路由器":"其他";
		
		List<String> columnName = new ArrayList<>();
		List<String> columnResoueceName = new ArrayList<>();
		
		getColumnName(columnName, columnResoueceName);// 获取基础属性字段作为excle的列名
		
		List<String> extColm=null;
		// 获取扩展字段
		List<ExtColumns> extCol=extColumnsBiz.findByPropertyName(ExtColumns.class, "assTypeCode",typeName);
		
		// 将扩展字段加入到excle列名集合中
		if(StrUtils.checkParam(extCol)){
			extColm=new ArrayList<>();
			for(ExtColumns e:extCol){
				extColm.add(e.getXcName());
			}
			columnName.addAll(extColm);
			columnResoueceName.addAll(extColm);
		}
		this.writeCol(sheet, columnName, 0); // 将列名写入excle中
		
		// 获取该资产类型的资产
		List<AsmMaster> asmMasters = asmMasterBiz.findByPropertyName(AsmMaster.class,"assetTypeCode",type);
		
		//获取资产的基础属性字段
		Field[] fields = AsmMaster.class.getDeclaredFields();
		
		int rowNum = 1; // 从第一行开始写入
		
		if (StrUtils.checkParam(asmMasters)) {
			for (AsmMaster asmMaster : asmMasters) {
				
				List<ExtAValue> extAValues=extAValueBiz.findByPropertyName(ExtAValue.class, "assetID", asmMaster.getId());
				
				for (int j = 0; j < columnResoueceName.size(); j++) {
					// 过滤不需要的字段
					if ("serialVersionUID".equals(columnResoueceName.get(j))
							|| columnResoueceName.get(j).indexOf("raw") > -1) {
						continue;
					}
					// 设置基础属性
					for (Field field : fields) { 
						String name = field.getName();
						if (columnResoueceName.get(j).equals(field.getName())) {
							name = name.substring(0, 1).toUpperCase() + name.substring(1);
							Method method = asmMaster.getClass().getMethod("get" + name);
							if (method != null) {
								Object object = method.invoke(asmMaster);
								if (object != null) {
									if("status".equals(field.getName())){
										if(ConfigProperty.CMDB_ASSET_FLAG1_DRAFT.equals(object.toString())){
											object="草稿";
										}else if(ConfigProperty.CMDB_ASSET_FLAG2_USE.equals(object.toString())){
											object="使用中";
										}else if(ConfigProperty.CMDB_ASSET_FLAG3_UNUSE.equals(object.toString())){
											object="已退库";
										}
									}
									if("useFlag".equals(field.getName())){
										if(ConfigProperty.CMDB_ASSET_USEFLAG_FREE.equals(object.toString())){
											object="空闲";
										}else if(ConfigProperty.CMDB_ASSET_USEFLAG_TEST.equals(object.toString())){
											object="测试";
										}else if(ConfigProperty.CMDB_ASSET_USEFLAG_UAT.equals(object.toString())){
											object="UAT";
										}else if(ConfigProperty.CMDB_ASSET_USEFLAG_PRODUCT.equals(object.toString())){
											object="生产环境";
										}
									}
									Label label = new Label(j, rowNum, object.toString());
									sheet.addCell(label);
								}
							}
						}
					}
					
					// 设置扩展属性
					for(ExtAValue e:extAValues){
						if(columnResoueceName.get(j).equals(e.getExtName())){
							Label label = new Label(j, rowNum, e.getExtValue());
							sheet.addCell(label);
						}
					}
				}
				rowNum++;
			}
		}
		return sheet;
	}
	
	/**
	 * 获取基础属性字段
	 * @param columnName 用于添加字段
	 * @param columnResoueceName 用于添加字段值
	 */
	public void getColumnName(List<String> columnName, List<String> columnResoueceName) {

		Field[] fields = AsmMaster.class.getDeclaredFields();

		for (Field field : fields) {
			String fieldName = field.getName();
			if ("serialVersionUID".equals(fieldName)) {
				continue;
			} else if ("assetId".equals(fieldName)) {
				columnName.add("资产编号");
				columnResoueceName.add("assetId");
			} else if ("status".equals(fieldName)) {
				columnName.add("资产状态");
				columnResoueceName.add("status");
			} else if ("assetName".equals(fieldName)) {
				columnName.add("资产名称");
				columnResoueceName.add("assetName");
			} else if ("serial".equals(fieldName)) {
				columnName.add("序列号");
				columnResoueceName.add("serial");
			} else if ("assetTypeName".equals(fieldName)) {
				columnName.add("资产类型");
				columnResoueceName.add("assetTypeName");
			} else if ("provide".equals(fieldName)) {
				columnName.add("品牌");
				columnResoueceName.add("provide");
			} else if ("modeName".equals(fieldName)) {
				columnName.add("设备型号");
				columnResoueceName.add("modeName");
			} else if ("departName".equals(fieldName)) {
				columnName.add("归属部门");
				columnResoueceName.add("departName");
			} else if ("assUserName".equals(fieldName)) {
				columnName.add("设备管理员");
				columnResoueceName.add("assUserName");
			} else if ("assetUserName".equals(fieldName)) {
				columnName.add("资产管理员");
				columnResoueceName.add("assetUserName");
			} else if ("sysUserName".equals(fieldName)) {
				columnName.add("系统管理员");
				columnResoueceName.add("sysUserName");
			} else if ("userIdName".equals(fieldName)) {
				columnName.add("当前使用人");
				columnResoueceName.add("userIdName");
			} else if ("useFlag".equals(fieldName)) {
				columnName.add("环境类型");
				columnResoueceName.add("useFlag");
			} else if ("os".equals(fieldName)) {
				columnName.add("操作系统");
				columnResoueceName.add("os");
			} else if ("parentId".equals(fieldName)) {
				columnName.add("父资产id");
				columnResoueceName.add("parentId");
			} else if ("mmac".equals(fieldName)) {
				columnName.add("管理MAC");
				columnResoueceName.add("mmac");
			} else if ("iloIP".equals(fieldName)) {
				columnName.add("管理IP");
				columnResoueceName.add("iloIP");
			} else if ("begDate".equals(fieldName)) {
				columnName.add("启用日期");
				columnResoueceName.add("begDate");
			} else if ("endDate".equals(fieldName)) {
				columnName.add("停用日期");
				columnResoueceName.add("endDate");
			} else if ("lifeYears".equals(fieldName)) {
				columnName.add("折旧期限");
				columnResoueceName.add("lifeYears");
			} else if ("retirementDate".equals(fieldName)) {
				columnName.add("报废日期");
				columnResoueceName.add("retirementDate");
			} else if ("ncpu".equals(fieldName)) {
				columnName.add("CPU个数");
				columnResoueceName.add("ncpu");
			} else if ("ncore".equals(fieldName)) {
				columnName.add("CPU核数");
				columnResoueceName.add("ncore");
			} else if ("ram".equals(fieldName)) {
				columnName.add("内存条数");
				columnResoueceName.add("ram");
			} else if ("memTotal".equals(fieldName)) {
				columnName.add("内存总量");
				columnResoueceName.add("memTotal");
			} else if ("ndisks".equals(fieldName)) {
				columnName.add("硬盘个数");
				columnResoueceName.add("ndisks");
			} else if ("diskTotal".equals(fieldName)) {
				columnName.add("硬盘总量");
				columnResoueceName.add("diskTotal");
			} else if ("npower".equals(fieldName)) {
				columnName.add("电源数量");
				columnResoueceName.add("npower");
			} else if ("pkws".equals(fieldName)) {
				columnName.add("功率");
				columnResoueceName.add("pkws");
			} else if ("switchPort".equals(fieldName)) {
				columnName.add("端口数");
				columnResoueceName.add("switchPort");
			} else if ("remark".equals(fieldName)) {
				columnName.add("备注");
				columnResoueceName.add("remark");
			} else if ("comeFromPrj".equals(fieldName)) {
				columnName.add("来源项目");
				columnResoueceName.add("comeFromPrj");
			} else if ("upTime".equals(fieldName)) {
				columnName.add("上线日期");
				columnResoueceName.add("upTime");
			} else if ("downTime".equals(fieldName)) {
				columnName.add("下线日期");
				columnResoueceName.add("downTime");
			}else if("unumb".equals(fieldName)){
				columnName.add("起始U号");
				columnResoueceName.add("unumb");
			}
		}
	}

	/***
	 * 将数组写入工作簿
	 * 
	 * @param sheet
	 *            要写入的工作簿
	 * @param col
	 *            要写入的数据数组
	 * @param rowNum
	 *            要写入哪一行
	 * @throws WriteException
	 * @throws RowsExceededException
	 */
	private void writeCol(WritableSheet sheet, List<String> col, int rowNum)
			throws RowsExceededException, WriteException {
		int size = col.size(); // 获取集合大小

		for (int i = 0; i < size; i++) { // 写入每一列
			Label label = new Label(i, rowNum, col.get(i));
			sheet.addCell(label);
		}
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
	public Object save(AsmMaster entity) {
		return null;
	}

	@Override
	public Object update(String id, AsmMaster entity) throws IOException {
		return null;
	}
}

package com.h3c.iclouds.rest;

import com.h3c.iclouds.base.BaseRest;
import com.h3c.iclouds.base.BaseRestControl;
import com.h3c.iclouds.biz.AsmMasterBiz;
import com.h3c.iclouds.biz.NetPortsBiz;
import com.h3c.iclouds.biz.Server2OveBiz;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.dao.IpRelationDao;
import com.h3c.iclouds.dao.NetPortsDao;
import com.h3c.iclouds.dao.NetPortsLinkDao;
import com.h3c.iclouds.exception.MessageException;
import com.h3c.iclouds.po.AsmMaster;
import com.h3c.iclouds.po.IpRelation;
import com.h3c.iclouds.po.NetPorts;
import com.h3c.iclouds.po.NetPortsLink;
import com.h3c.iclouds.po.Server2Ove;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Api(value="数据导入")
@RestController
@RequestMapping("/dataimport")
public class DataImportRest extends BaseRest<AsmMaster> {
	
	@Resource
	private AsmMasterBiz asmMasterBiz;
	
	@Resource
	private Server2OveBiz server2OveBiz;
	
	@Resource
	private IpRelationDao ipRelationDao;
	
	@Resource
	private NetPortsLinkDao netPortsLinkDao;
	
	@Resource
	private NetPortsBiz netPortsBiz;
	
	@Resource
	private NetPortsDao netPortsDao;
	
	
	@ApiOperation(value="导入数据")
	@RequestMapping(value="/file",method=RequestMethod.POST)
	public Object upload(HttpServletRequest request){
		//页面上传数据文件
		FileOutputStream fos=null;
		String path="";
		try {
			MultipartHttpServletRequest mreq = (MultipartHttpServletRequest)request;
			MultipartFile file = mreq.getFile("file");
			String fileName = file.getOriginalFilename();
			path=request.getSession().getServletContext().getRealPath("/")+fileName;
			fos = new FileOutputStream(path);
			fos.write(file.getBytes());
			fos.flush();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return BaseRestControl.tranReturnValue(ResultType.file_not_exist);
		} catch (IOException e) {
			e.printStackTrace();
			return BaseRestControl.tranReturnValue(ResultType.upload_failure);
		}finally {
			try {
				if(fos!=null){
					fos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		//导入数据
		try {
			return asmMasterBiz.getAllDate(path);
		} catch (MessageException e) {
			this.exception(this.getClass(), e);
			return BaseRestControl.exceptionReturn(e);
		} catch (Exception e) {
			this.exception(this.getClass(), e);
			return BaseRestControl.tranReturnValue(ResultType.failure);
		}
	}
	
	@ApiOperation(value="导入cvk端口,mac和ip数据")
	@RequestMapping(value="/update/file",method=RequestMethod.POST)
	public Object update(HttpServletRequest request){
		//页面上传数据文件
		FileOutputStream fos=null;
		String path="";
		try {
			MultipartHttpServletRequest mreq = (MultipartHttpServletRequest)request;
			MultipartFile file = null;
			Map<String, MultipartFile> map = mreq.getFileMap();
			for (Map.Entry<String, MultipartFile> stringMultipartFileEntry : map.entrySet()) {
				file = stringMultipartFileEntry.getValue();
			}
			if(file == null) {
				return BaseRestControl.tranReturnValue(ResultType.file_not_exist);
			}
			String fileName = file.getOriginalFilename();
			path=request.getSession().getServletContext().getRealPath("/")+fileName;
			fos = new FileOutputStream(path);
			fos.write(file.getBytes());
			fos.flush();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return BaseRestControl.tranReturnValue(ResultType.file_not_exist);
		} catch (IOException e) {
			e.printStackTrace();
			return BaseRestControl.tranReturnValue(ResultType.upload_failure);
		}finally {
			try {
				if(fos!=null){
					fos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		//导入数据
		return updateData(path);
		
	}
	
	private Object updateData(String path) {
		File file=new File(path);
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			String tempString = null;
			AsmMaster newAsmMaster=null;
			String cvkNameTemp=""; // 用于判断与前一条信息是否是同一个cvk
			List<String> netPortsMac=new ArrayList<String>(); // 用于存放端口信息
			List<String> storageIp=new ArrayList<>(); // 用于存放存储网ip
			List<String> managerIp=new ArrayList<>(); // 用于存放管理ip
			// 一次读入一行，直到读入null为文件结束
			while ((tempString = reader.readLine()) != null) {
				
				String[] array=tempString.trim().split(" ");
				String serial=array[2].split("\\.")[0]; // 序列号
				String cvkName=array[2].split("\\.")[1]; // 主机名
				String type=array[0];
				String macOrIp=array[1]; // mac或ip地址
				System.out.println("主机名:"+cvkName+";序列号:"+serial+";mac或ip:"+macOrIp+";类型:"+type);
				
				List<Server2Ove> list = server2OveBiz.findByPropertyName(Server2Ove.class,"hostName",cvkName);
				
				if("".equals(cvkNameTemp) || "".equals(cvkName) || !cvkName.equals(cvkNameTemp)){
					// cvk与上一个不同时,将收集的端口信息录入
					if(netPortsMac!=null && !netPortsMac.isEmpty()){
						for(int i=0;i<netPortsMac.size();i++){
							NetPorts netPorts=new NetPorts();
							netPorts.setMac(netPortsMac.get(i));
							netPorts.setSeq(i+1);
							netPorts.setMasterId(newAsmMaster.getId());
							netPorts.setPortType("1"); // 电口
							netPorts.setEthType(ConfigProperty.CMDB_NETPORT_ETHTYPE1_PHYSICS);
							netPorts.createdUser("junit");
							netPortsBiz.add(netPorts);
						}
					}
					if(storageIp!=null && !storageIp.isEmpty()){
						for(int i=0;i<storageIp.size();i++){
							NetPorts netPorts=new NetPorts();
							netPorts.setSeq(i+1+netPortsMac.size());
							netPorts.setMasterId(newAsmMaster.getId());
							netPorts.setPortType("1"); // 电口
							netPorts.setEthType(ConfigProperty.CMDB_NETPORT_ETHTYPE1_PHYSICS);
							netPorts.createdUser("junit");
							netPortsBiz.add(netPorts);
							
							IpRelation ipRelation=new IpRelation();
							ipRelation.setAssetId(newAsmMaster.getId());
							ipRelation.setNcid(netPorts.getId());
							ipRelation.setIp(storageIp.get(i));
							ipRelation.setClassId(newAsmMaster.getAssetType());
							ipRelation.setIsIlop(0);
							ipRelation.setAsmPortSeq(i+1+netPortsMac.size());
							ipRelation.createdUser("junit");
							ipRelationDao.add(ipRelation);
						}
					}
					if(managerIp!=null && !managerIp.isEmpty()){
						for(int i=0;i<managerIp.size();i++){
							NetPorts netPorts=new NetPorts();
							netPorts.setSeq(i+1+netPortsMac.size()+storageIp.size());
							netPorts.setMasterId(newAsmMaster.getId());
							netPorts.setPortType("1"); // 电口
							netPorts.setEthType(ConfigProperty.CMDB_NETPORT_ETHTYPE1_PHYSICS);
							netPorts.createdUser("junit");
							netPortsBiz.add(netPorts);
							
							IpRelation ipRelation=new IpRelation();
							ipRelation.setAssetId(newAsmMaster.getId());
							ipRelation.setNcid(netPorts.getId());
							ipRelation.setIp(managerIp.get(i));
							ipRelation.setClassId(newAsmMaster.getAssetType());
							ipRelation.setIsIlop(1);
							ipRelation.setAsmPortSeq(i+1+netPortsMac.size()+storageIp.size());
							ipRelation.createdUser("junit");
							ipRelationDao.add(ipRelation);
						}
					}
					netPortsMac.clear(); // 录入后清除原有内容
					storageIp.clear();
					managerIp.clear();
					
				}
				if(list!=null && !list.isEmpty()){
					Server2Ove s2o=list.get(0);
					s2o.setSerilNum(serial);
					if("".equals(cvkNameTemp) || "".equals(cvkName) || !cvkName.equals(cvkNameTemp)){
						newAsmMaster = asmMasterBiz.findById(AsmMaster.class, s2o.getAssetId());
						newAsmMaster.setSerial(serial);
						asmMasterBiz.update(newAsmMaster);
						
						// cvk与上一个不同时,重置cvk端口
						if(newAsmMaster!=null){
							List<NetPorts> netPorts=netPortsBiz.findByPropertyName(NetPorts.class, "masterId", newAsmMaster.getId());
							if(netPorts!=null && !netPorts.isEmpty()){
								for(NetPorts n:netPorts){
									List<NetPortsLink> npls=netPortsLinkDao.findByPropertyName(NetPortsLink.class, "trunkTo", n.getId());
									List<NetPortsLink> npls2=netPortsLinkDao.findByPropertyName(NetPortsLink.class, "accessTo", n.getId());
									npls.addAll(npls2);
									netPortsLinkDao.delete(npls);
									List<IpRelation> ipRelations=ipRelationDao.findByPropertyName(IpRelation.class, "ncid", n.getId());
									ipRelationDao.delete(ipRelations);
								}
								netPortsDao.delete(netPorts);
							}
						}
						cvkNameTemp=cvkName;
					}
					if("1".equals(type)){ //端口
						netPortsMac.add(macOrIp);
					}else if("2".equals(type)){ // 管理ip
						s2o.setIp(macOrIp);
						managerIp.add(macOrIp);
						server2OveBiz.update(s2o);
					}else if("3".equals(type)){ //存储ip
						storageIp.add(macOrIp);
					}
				}
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
			return BaseRestControl.tranReturnValue(ResultType.upload_failure);
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
				}
			}
		}
		return BaseRestControl.tranReturnValue(ResultType.success);
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
  
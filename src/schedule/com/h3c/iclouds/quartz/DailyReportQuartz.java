package com.h3c.iclouds.quartz;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.h3c.iclouds.auth.CacheSingleton;
import com.h3c.iclouds.biz.Cvm2OveBiz;
import com.h3c.iclouds.biz.Server2OveBiz;
import com.h3c.iclouds.biz.Storage2OveBiz;
import com.h3c.iclouds.po.Cvm2Ove;
import com.h3c.iclouds.po.Server2Ove;
import com.h3c.iclouds.po.Storage2Ove;
import com.h3c.iclouds.utils.PictureSingleton;
import com.h3c.iclouds.utils.LogUtils;
import com.h3c.iclouds.utils.MailUtils;
import com.h3c.iclouds.utils.StrUtils;
import com.wordnik.swagger.annotations.ApiOperation;

import org.apache.commons.codec.binary.Base64;

public class DailyReportQuartz {

	@Resource
	private Server2OveBiz server2OveBiz;

	@Resource
	private Storage2OveBiz storage2OveBiz;

	@Resource
	private Cvm2OveBiz cvm2OveBiz;
	
	/** 日报功能页面地址*/
	private String url;
	/** 日报功能file存放路径*/
	private String path;

	/** phantomJs 路径*/
	private String phPath;
	/** phantomjs执行的js文件*/
	private String jsPath;
	
	
	@SuppressWarnings("static-access")
	@ApiOperation(value = "容量管理 日报表")
	public void sendMail() {
		CacheSingleton cacheSingleton=CacheSingleton.getInstance();
		//获取config.properties配置文件
		Map<String, String> map=cacheSingleton.getConfigMap();
		
		if(cacheSingleton.getSystemType().toLowerCase().indexOf("window")>-1){
			phPath="\""+cacheSingleton.getProjectPath()+map.get("dailyReport_windows_phPath")+"\"";
			path=cacheSingleton.getProjectPath()+map.get("dailyReport_windows_path");
			url = map.get("dailyReport_windows_url");
			jsPath="\""+cacheSingleton.getProjectPath()+map.get("dailyReport_windows_jsPath")+"\"";
		}else if(cacheSingleton.getSystemType().toLowerCase().indexOf("linux")>-1){
			phPath=cacheSingleton.getProjectPath()+map.get("dailyReport_linux_phPath");
			path=cacheSingleton.getProjectPath()+map.get("dailyReport_linux_path");
			url = map.get("dailyReport_linux_url");
			jsPath=cacheSingleton.getProjectPath()+map.get("dailyReport_linux_jsPath");
		}
		
		// 收件人
		List<String> targetList = new ArrayList<>();
		String addressee =map.get("dailyReport_addressee");
		if(addressee!=null && !"".equals(addressee.trim())){
			if(addressee.indexOf(",")>-1){
				String[] adds=addressee.split(",");
				for(String s:adds){
					targetList.add(s);
				}
			}else{
				targetList.add(addressee);
			}
		}
		

		// 获取TOP5
		List<Server2Ove> server2Oves = server2OveBiz.findTop5();
		List<Cvm2Ove> cvm2Oves = cvm2OveBiz.findTop5();
		List<Storage2Ove> storage2Oves = storage2OveBiz.findTop5();

		PictureSingleton pic = PictureSingleton.getInstance();

		// 访问页面
		getAjaxCotnent();
		
		int count = 0;
		//等待页面加载完成
		while(pic.getCvm2Ove()==null || pic.getServer2Ove()==null || pic.getStorage2Ove()==null){
			try {
				if(count>=10){//页面加载超过60秒
					LogUtils.info(DailyReportQuartz.class, "页面加载失败");
					return;
				}
				Thread.currentThread().sleep(6000);
				count ++;
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}
		
		// 获取base64格式图片
		String server2Ove = pic.getServer2Ove()==null?"":pic.getServer2Ove();
		String cvm2Ove = pic.getCvm2Ove()==null?"":pic.getCvm2Ove();
		String storage2Ove = pic.getStorage2Ove()==null?"":pic.getStorage2Ove();

		// Base64解码
		generateImage(server2Ove, "server2Ove");
		generateImage(cvm2Ove, "cvm2Ove");
		generateImage(storage2Ove, "storage2Ove");

		pic.setServer2Ove(null);
		pic.setCvm2Ove(null);
		pic.setStorage2Ove(null);
		
		// 获取要发送的文件file[]
		File server2OveFile = new File(path + "server2Ove.png");
		File storage2OveFile = new File(path + "storage2Ove.png");
		File cvm2OveFile = new File(path + "cvm2Ove.png");
		File[] files = new File[] { server2OveFile, storage2OveFile, cvm2OveFile };

		StringBuilder sBuilder = new StringBuilder("<h1>容量管理-日报表</h1>");

		// 转化数字为百分比格式
		NumberFormat nf = NumberFormat.getPercentInstance();
		nf.setMaximumFractionDigits(2);
		nf.setMinimumFractionDigits(2);

		sBuilder.append("主机超配TOP5\n");
		sBuilder.append(
				"<table border='1' cellpadding='0' cellspacing='0' style='text-align:center;border-collapse: collapse;'><tr><td>主机名称</td><td>归属cvm名称</td><td>cpu</td><td>cpu超配值</td>"
						+ "<td>ram</td><td>ram超配值</td><td>虚拟机数</td><td>已分配内存</td><td>内存分配率</td><td>已分配CPU核</td>"
						+ "<td>cpu分配率</td></tr>");
		if (StrUtils.checkCollection(server2Oves)) {
			for (Server2Ove e : server2Oves) {
				sBuilder.append("<tr>");
				sBuilder.append("<td>" + e.getHostName() + "</td>");
				sBuilder.append("<td>" + e.getBelongCvm() + "</td>");
				sBuilder.append("<td>" + e.getCpus() + "</td>");
				sBuilder.append("<td>" + e.getCpuOverSize() + "</td>");
				sBuilder.append("<td>" + e.getRam() + "</td>");
				sBuilder.append("<td>" + e.getRamOverSize() + "</td>");
				sBuilder.append("<td>" + e.getVms() + "</td>");
				sBuilder.append("<td>" + e.getAssignMem() + "</td>");
				sBuilder.append("<td>" + nf.format((e.getMemUsage() == null ? 0 : e.getMemUsage()) / 100) + "</td>");
				sBuilder.append("<td>" + e.getAssignCpu() + "</td>");
				sBuilder.append("<td>" + nf.format(e.getCpuUsage() / 100) + "</td>");
				sBuilder.append("</tr>");
			}
		}
		sBuilder.append("</table><br><img src='cid:" + server2OveFile.getName() + "'><br><br>");

		sBuilder.append("CVM超配TOP5\n");
		sBuilder.append(
				"<table border='1' cellpadding='0' cellspacing='0' style='text-align:center;border-collapse: collapse;'>"
						+ "<tr><td>cvm名称</td><td>总CPU核</td><td>已分配CPU核</td><td>cpu使用率</td>"
						+ "<td>总内存</td><td>已分配内存</td><td>内存使用率</td></tr>");
		if (StrUtils.checkCollection(cvm2Oves)) {
			for (Cvm2Ove e : cvm2Oves) {
				sBuilder.append("<tr>");
				sBuilder.append("<td>" + e.getCvmName() + "</td>");
				sBuilder.append("<td>" + e.getTotalCpu() + "</td>");
				sBuilder.append("<td>" + e.getAssignCpu() + "</td>");
				sBuilder.append("<td>" + nf.format(e.getCpuUsage() / 100) + "</td>");
				sBuilder.append("<td>" + e.getTotalMem() + "</td>");
				sBuilder.append("<td>" + e.getAssignMem() + "</td>");
				sBuilder.append("<td>" + nf.format(e.getMemUsage() / 100) + "</td>");
				sBuilder.append("</tr>");
			}
		}
		sBuilder.append("</table><br><img src='cid:" + cvm2OveFile.getName() + "'><br><br>");

		sBuilder.append("储存容量超配TOP5\n");
		sBuilder.append(
				"<table border='1' cellpadding='0' cellspacing='0' style='text-align:center;border-collapse: collapse;'>"
						+ "<tr><td>存储或集群名称</td><td>类型</td><td>总容量</td><td>已分配容量</td>"
						+ "<td>已使用容量</td><td>剩余容量</td><td>容量使用率</td><td>容量超配率</td></tr>");
		if (StrUtils.checkCollection(storage2Oves)) {
			for (Storage2Ove e : storage2Oves) {
				sBuilder.append("<tr>");
				sBuilder.append("<td>" + e.getName() + "</td>");
				sBuilder.append("<td>" + e.getType() + "</td>");
				sBuilder.append("<td>" + new BigDecimal(e.getTotalCapa()).toString() + "</td>");
				sBuilder.append("<td>" + new BigDecimal(e.getAllocationCapa() == null ? 0.0f : e.getAllocationCapa())
						+ "</td>");
				sBuilder.append("<td>" + new BigDecimal(e.getUsedCapa() == null ? 0.0f : e.getUsedCapa()) + "</td>");
				sBuilder.append("<td>" + new BigDecimal(e.getFreeCapa()).toString() + "</td>");
				sBuilder.append("<td>" + nf.format((e.getCapaUsage() == null ? 0 : e.getCapaUsage()) / 100) + "</td>");
				sBuilder.append("<td>" + nf.format(e.getCapaOverflow() / 100) + "</td>");
				sBuilder.append("</tr>");
			}
		}
		sBuilder.append("</table><br><img src='cid:" + storage2OveFile.getName() + "'><br><br>");
		MailUtils.sendEmailShowImg(targetList, "容量管理-日报表", sBuilder.toString(), files);

	}

	// 访问网页
	public void getAjaxCotnent() {
		try {
			Runtime rt = Runtime.getRuntime();
			
			if(System.getProperty("os.name").toLowerCase().indexOf("linux")>-1){
				rt.exec("chmod 777 " + phPath).waitFor();
			}
			System.out.println(phPath +" " + jsPath + " " + url);
			rt.exec(phPath +" " + jsPath + " " + url).waitFor();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// base64解码
	public boolean generateImage(String imgStr, String name) {
		if (imgStr == null) {
			return false;
		}
		imgStr=imgStr.replace("data:image/png;base64,","").replace("base64,","");	
		try {
			// 解密
			byte[] b = Base64.decodeBase64(imgStr);
			// 处理数据
			for (int i = 0; i < b.length; ++i) {
				if (b[i] < 0) {
					b[i] += 256;
				}
			}
			OutputStream out = new FileOutputStream(path + name + ".png");
			out.write(b);
			out.flush();
			out.close();
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}

package com.h3c.iclouds.junit.rest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.h3c.iclouds.po.bean.outside.NovaVmDetailBean;
import com.h3c.iclouds.po.Azone;
import com.h3c.iclouds.po.NovaVm;
import com.h3c.iclouds.po.Volume;
import com.h3c.iclouds.utils.JacksonUtil;

public class NovaRestTest {

	public static void main(String[] args) {
		NovaVmDetailBean bean=new NovaVmDetailBean();
		bean.setOsPasswd("初始化密码");
		Azone azone=new Azone();
		azone.setLableName("资源区域");
		bean.setAzone(azone);
		bean.setPrivateIp("公网IP");
		bean.setPrivateIp("私网IP");
		
		NovaVm novaVm=new NovaVm();
		novaVm.setCreatedDate(new Date());
		novaVm.setHostName("主机名称");
		novaVm.setMemory(12);
		novaVm.setRamdisk(12);
		novaVm.setVcpus(13);
		novaVm.setOsType("操作系统版本");
		novaVm.setVmState("状态");
		bean.setNovaVm(novaVm);
		List<Volume> volumes=new ArrayList<Volume>();
		Volume volume=new Volume();
		volume.setName("硬盘名称");
		volume.setSize(18);
		volume.setVolumeType("存储类型");
		volumes.add(volume);
		//bean.set
		bean.setVolumes(volumes);
		System.out.println(JacksonUtil.toJSon(bean));
	}

}

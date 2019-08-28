package com.h3c.iclouds.common;

public class TaskTypeProperty {
	
	/**
	 * 云主机开机
	 */
	public static final Integer VM_POWER_STATE_START =1;
	
	/**
	 * 云主机关机
	 */
	public static final Integer VM_POWER_STATE_SHUTDOWN =2;
	
	/**
	 * 云主机开机
	 */
	public static final String VM_STARTUP = "vm.startup";
	
	/**
	 * 云主机重置密码
	 */
	public static final String VM_UPDATEPWD = "vm.update";
	
	/**
	 * 云主机关机
	 */
	public static final String VM_SHUTDOWN = "vm.shutdown";
	
	/**
	 * 云主机重启
	 */
	public static final String VM_REBOOT = "vm.reboot";

	/**
	 * 云主机创建
	 */
	public static final String VM_CREATE = "vm.create";

	/**
	 * 云主机删除
	 */
	public static final String VM_DELETE = "vm.delete";
	
	/**
	 * VDC视图创建
	 */
	public static final String VDC_VIEW_CREATE = "vdc.view.create";
	
	/**
	 * 硬盘创建
	 */
	public static final String VOLUME_CREATE = "volume.create";
	
	/**
	 * 硬盘删除
	 */
	public static final String VOLUME_DELETE = "volume.delete";
	
	/**
	 * 硬盘挂载
	 */
	public static final String VOLUME_ATTACH = "volume.attach";
	
	/**
	 * 硬盘卸载
	 */
	public static final String VOLUME_DETTACH= "volume.dettach";
	
	/**
	 * 等待关机
	 */
	public static final String VM_WAIT_SHUTDOWN = "vm.wait.shutdown";

	/**
	 * 等待删除
	 */
	public static final String VM_WAIT_DELETE = "vm.wait.delete";
	
	/**
	 * 根据云主机转为镜像
	 */
	public static final String VM_TO_IMAGE = "vm.to.image";

	/**
	 * 根据云主机进行克隆
	 */
	public static final String VM_CLONE = "vm.clone";
	
}

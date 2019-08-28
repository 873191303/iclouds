package com.h3c.iclouds.listen;

import com.h3c.iclouds.auth.CacheSingleton;
import com.h3c.iclouds.base.BaseDAO;
import com.h3c.iclouds.base.SpringContextHolder;
import com.h3c.iclouds.common.TaskTypeProperty;
import com.h3c.iclouds.po.Task;
import com.h3c.iclouds.task.BaseTask;
import com.h3c.iclouds.task.Vdc.VdcCreateTask;
import com.h3c.iclouds.task.Volume.VolumeAttachTask;
import com.h3c.iclouds.task.Volume.VolumeCreateTask;
import com.h3c.iclouds.task.Volume.VolumeDeleteTask;
import com.h3c.iclouds.task.Volume.VolumeDettachTask;
import com.h3c.iclouds.task.novavm.NovaVmCreateTask;
import com.h3c.iclouds.task.novavm.NovaVmDeleteTask;
import com.h3c.iclouds.task.novavm.NovaVmRebootTask;
import com.h3c.iclouds.task.novavm.NovaVmShutDownTask;
import com.h3c.iclouds.task.novavm.NovaVmStartUpTask;
import com.h3c.iclouds.task.novavm.NovaVmWaitShutdownTask;
import com.h3c.iclouds.task.novavm.TaskWaitDeleteTask;
import com.h3c.iclouds.utils.ErrHandler;
import com.h3c.iclouds.utils.LogUtils;
import com.h3c.iclouds.utils.StrUtils;

/**
 * 监听队列，分发队列收到的处理内容
 * @author zkf5485
 *
 */
public class StartTaskQueenListen implements Runnable {
	
	private BaseDAO<Task> taskDao;
	
	private String taskId;
	
	/**
	 * 正在执行的任务个数
	 */
	public static int CURRENT_EXEC_TASK_NUM = 0;
	
	/**
	 * 最多同时执行任务个数
	 */
	public final static int MAX_EXEC_TASK_NUM = 5;
	
	/**
	 * 是否繁忙
	 * @return
	 */
	public static boolean isBusy() {
		return CURRENT_EXEC_TASK_NUM > MAX_EXEC_TASK_NUM;
	}
	
	public StartTaskQueenListen(String taskId) {
		this.taskId = taskId;
		taskDao = SpringContextHolder.getBean("baseDAO");
	}
	
	public static void startTask(String taskId) {
		StartTaskQueenListen stql = new StartTaskQueenListen(taskId);
		CacheSingleton.getInstance().startThread(stql);
	}
	
	@Override
	public void run() {
		Task entity = null;
		for (int i = 0; i < 10; i++) {	// 增加循环查询数据
			entity = taskDao.findById(Task.class, taskId);
			if(entity != null) {
				break;
			}
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace(); 
			}
		}
		if(null != entity) {
			LogUtils.info(this.getClass(), "Start task, value:" + StrUtils.toJSONString(entity));
			BaseTask task = null;
			// TODO 后面修改基础数据表中获取，目前暂时使用字符串
			switch (entity.getBusType()) {
				case TaskTypeProperty.VM_STARTUP:	// 开机
					task = new NovaVmStartUpTask(entity);
					break;
				case TaskTypeProperty.VM_SHUTDOWN:	// 关机
					task = new NovaVmShutDownTask(entity);
					break;
				case TaskTypeProperty.VM_CREATE:	// 创建
					task = new NovaVmCreateTask(entity);
					break;
				case TaskTypeProperty.VM_REBOOT:	// 重启
					task = new NovaVmRebootTask(entity);
					break;
				case TaskTypeProperty.VM_DELETE:	// 删除
					task = new NovaVmDeleteTask(entity);
					break;
				case TaskTypeProperty.VM_WAIT_SHUTDOWN:
					task = new NovaVmWaitShutdownTask(entity); // 等待关机
					break;
				case TaskTypeProperty.VM_WAIT_DELETE:
					task = new TaskWaitDeleteTask(entity); // 等待删除
					break;
				case TaskTypeProperty.VDC_VIEW_CREATE://创建vdc视图
					task = new VdcCreateTask(entity);
					break;
				case TaskTypeProperty.VOLUME_CREATE:
					task = new VolumeCreateTask(entity); // 创建云硬盘
					break;
				case TaskTypeProperty.VOLUME_DELETE:
					task = new VolumeDeleteTask(entity); // 删除云硬盘
					break;
				case TaskTypeProperty.VOLUME_ATTACH:
					task = new VolumeAttachTask(entity); // 挂载云硬盘
					break;
				case TaskTypeProperty.VOLUME_DETTACH:
					task = new VolumeDettachTask(entity); // 卸载云硬盘
					break;
				// 镜像任务不在这里处理
//				case TaskTypeProperty.VM_TO_IMAGE:
//					task = new NovaVmToImageTask(entity); // synchronize volume state
//					break;
				default:
					break;
			}
			if(null != task) {
				++StartTaskQueenListen.CURRENT_EXEC_TASK_NUM;
				Thread tt = new Thread(task);
				tt.setName(entity.getBusId());
				tt.setDaemon(true);
				tt.setUncaughtExceptionHandler(new ErrHandler());
				CacheSingleton.getInstance().startThread(tt);
			} else {
				LogUtils.info(this.getClass(), "BusType:" + entity.getBusType() + " not define");
			}
		} else {
			LogUtils.info(this.getClass(), "Task :[" + taskId + "], not exists");
		}
	}

}

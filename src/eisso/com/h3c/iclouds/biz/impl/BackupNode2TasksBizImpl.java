package com.h3c.iclouds.biz.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.h3c.iclouds.base.BaseBizImpl;
import com.h3c.iclouds.biz.BackupNode2TasksBiz;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.dao.Backup2NodesDao;
import com.h3c.iclouds.dao.BackupNode2TasksDao;
import com.h3c.iclouds.exception.MessageException;
import com.h3c.iclouds.po.Backup2Nodes;
import com.h3c.iclouds.po.BackupNode2Tasks;
import com.h3c.iclouds.utils.InvokeSetForm;


@Service("backupNode2TasksBizImpl")
public class BackupNode2TasksBizImpl extends BaseBizImpl<BackupNode2Tasks> implements BackupNode2TasksBiz {

	
	@Resource
	private BackupNode2TasksDao backupNode2TasksDao;
	
	@Resource
	private Backup2NodesDao backup2NodesDao;
	
	@Override
	public void update(List<BackupNode2Tasks> tasks) {
		for (BackupNode2Tasks task : tasks) {
			
			int id = task.getTaskId();
			BackupNode2Tasks taskLocal = backupNode2TasksDao.findById(BackupNode2Tasks.class, id);
			if ( null != taskLocal) {
				// backupNodeId 字段从爱数接口接口过来的是备份节点id，但是表中实际存储的应该是备份节点表中相应的主键
				task.setBackupNodeId(taskLocal.getBackupNodeId());
				InvokeSetForm.copyFormProperties(task, taskLocal);
				taskLocal.setUpdatedDate(new Date());
				backupNode2TasksDao.update(taskLocal);
			} else {
				// 查找 备份节点表中的记录
				String backupNodeId = task.getBackupNodeId();
				
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("backupNodeId", Integer.valueOf(backupNodeId));
				List<Backup2Nodes> node = backup2NodesDao.listByClass(Backup2Nodes.class, map);
				
				if (null != node && node.size() > 0 ) {
					String bId = node.get(0).getId();
					task.setBackupNodeId(bId);
					task.setCreatedDate(new Date());
					task.setUpdatedDate(new Date());
					backupNode2TasksDao.add(task);
				} else {
					// 抛出异常，对应的节点id不存在
					throw new MessageException(ResultType.not_exist);
				}
				
			}
		}
	}
	
	@Override
    public PageModel<BackupNode2Tasks> findForPage(PageEntity entity) {
        return backupNode2TasksDao.findForPage(entity);
    }

}

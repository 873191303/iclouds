package com.h3c.iclouds.biz.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.h3c.iclouds.base.BaseBizImpl;
import com.h3c.iclouds.biz.Backup2NodesBiz;
import com.h3c.iclouds.dao.Backup2NodesDao;
import com.h3c.iclouds.po.Backup2Nodes;
import com.h3c.iclouds.utils.InvokeSetForm;

@Service("backup2NodesBizImpl")
public class Backup2NodesBizImpl extends BaseBizImpl<Backup2Nodes>  implements Backup2NodesBiz  {

	@Resource
	private Backup2NodesDao  backup2NodesDao;
	
	@Override
	public void upate(List<Backup2Nodes> nodes) {
		
		for (Backup2Nodes node : nodes) {
			System.out.println(node);
			int backupNodeId = node.getBackupNodeId();
			System.out.println(backupNodeId);
			// 查找本地是否存在对应数据
			List<Backup2Nodes> nodesLocal = backup2NodesDao.findByPropertyName(Backup2Nodes.class, 
					"backupNodeId", node.getBackupNodeId());
			
			// 存在更新本地
			if (null != nodesLocal && nodesLocal.size() > 0) {
				Backup2Nodes nodeLocal = nodesLocal.get(0);
				
				InvokeSetForm.copyFormProperties(node, nodeLocal);
				nodeLocal.setUpdatedDate(new Date());
				backup2NodesDao.update(nodeLocal);
			} else {
				// TODO 测试环境是固定tenantId，真实环境要修改
				node.setCreatedDate(new Date());
				node.setUpdatedDate(new Date());
				backup2NodesDao.add(node);
			}
		}
	}

}

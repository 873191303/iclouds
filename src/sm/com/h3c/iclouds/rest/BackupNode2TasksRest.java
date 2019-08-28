package com.h3c.iclouds.rest;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.h3c.iclouds.client.EisooAbcParams;
import com.h3c.iclouds.base.BaseRest;
import com.h3c.iclouds.base.BaseRestControl;
import com.h3c.iclouds.biz.BackupNode2TasksBiz;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageList;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.dao.Backup2NodesDao;
import com.h3c.iclouds.dao.Project2QuotaDao;
import com.h3c.iclouds.dao.QuotaUsedDao;
import com.h3c.iclouds.po.Backup2Nodes;
import com.h3c.iclouds.po.BackupNode2Tasks;
import com.h3c.iclouds.po.Project2Quota;
import com.h3c.iclouds.po.QuotaUsed;
import com.wordnik.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/abc")
public class BackupNode2TasksRest extends BaseRest<BackupNode2Tasks> {
	
	@Resource
	private BackupNode2TasksBiz backupNode2TasksBiz;
	
	@Resource
	private QuotaUsedDao quotaUsedDao;
	
	@Resource
	private Backup2NodesDao backup2NodesDao;
	
	@Resource
	private Project2QuotaDao project2QuotaDao;
	
	@RequestMapping(value = "/backuptasks", method = RequestMethod.GET)
	@ApiOperation(value = "获取本地的备份任务, 分页列表")
	public Object backupTasks() {
		PageEntity entity = this.beforeList();
//		String[] nodeIds = this.getNodeIds(this.getProjectId());
		// TODO 測試用
		String[] nodeIds = this.getNodeIds(EisooAbcParams.ABC_TEST_TENANT);
		entity.setSpecialParams(nodeIds);
		PageModel<BackupNode2Tasks> pageModel = backupNode2TasksBiz.findForPage(entity);
		PageList<BackupNode2Tasks> page = new PageList<BackupNode2Tasks>(pageModel, entity.getsEcho());
		return BaseRestControl.tranReturnValue(page);
	}
	
	private String[] getNodeIds(String tenantId) {
		List<Backup2Nodes> nodes = backup2NodesDao.findByPropertyName(Backup2Nodes.class, "tenantId", tenantId);
		if (null != nodes && nodes.size() > 0){
            String [] ids = new String[nodes.size()];
            for (Backup2Nodes node : nodes) {
                String nodeId = node.getId();
                ids[nodes.indexOf(node)] = nodeId;
            }
            return ids;
        }
		return null;
	}
	
	@RequestMapping(value = "/quotaused", method = RequestMethod.GET)
	@ApiOperation(value = "获取配额使用情况")
	public Object quotaUsed() {
		Map<String, Object> queryMap = new HashMap<>();
		// TODO
		queryMap.put("tenantId", this.getProjectId());
		queryMap.put("classId", EisooAbcParams.ABC_QUOTA_FLAG);
		queryMap.put("deleted", 0);
		List<QuotaUsed> list = quotaUsedDao.listByClass(QuotaUsed.class, queryMap);
		
		int quotaUsed = 0;
		if (null != list && list.size() > 0) {
			quotaUsed = list.get(0).getQuotaUsed();
		}
		
		List<Project2Quota> list1 = project2QuotaDao.listByClass(Project2Quota.class, queryMap);
		
		int quota = 0;
		if (null != list1 && list1.size() > 0) {
			quota = list1.get(0).getHardLimit();
		}
		
		if ((null == list || list.size() == 0) && (null == list1 || list1.size() == 0)) {
			return BaseRestControl.tranReturnValue(ResultType.not_exist);
		}
		
		Map <String, Integer> map = new HashMap<String, Integer>();
		map.put("quotaUsed", quotaUsed);
		map.put("quota", quota);
		
		return BaseRestControl.tranReturnValue(ResultType.success, map);
	}
	

	@Override
	public Object list() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object get(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object delete(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object save(BackupNode2Tasks entity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object update(String id, BackupNode2Tasks entity) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

}

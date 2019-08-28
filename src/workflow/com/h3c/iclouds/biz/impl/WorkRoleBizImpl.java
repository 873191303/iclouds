package com.h3c.iclouds.biz.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.h3c.iclouds.base.BaseBizImpl;
import com.h3c.iclouds.biz.WorkRoleBiz;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.dao.RoleDao;
import com.h3c.iclouds.dao.WorkRoleDao;
import com.h3c.iclouds.po.Role;
import com.h3c.iclouds.po.WorkFlow;
import com.h3c.iclouds.po.WorkRole;
import com.h3c.iclouds.utils.StrUtils;

@Service("workRoleBiz")
public class WorkRoleBizImpl extends BaseBizImpl<WorkRole> implements WorkRoleBiz {

	@Resource
	private WorkRoleDao workRoleDao;
	
	@Resource
	private RoleDao roleDao;

	@Override
	public PageModel<WorkRole> findForPage(PageEntity entity) {
		return workRoleDao.findForPage(entity);
	}

	@Override
	public void saveRoleByList(List<Map<String, String>> list, String workFlowId, WorkFlow workFlow) {
		Map<String, Object> deleteMap = new HashMap<String, Object>();
		deleteMap.put("workFlowId", workFlowId);
		workRoleDao.delete(deleteMap, WorkRole.class);
		String workName = workFlow.getName();
		
		if(list != null && !list.isEmpty()) {
			for(Map<String, String> map : list) {
				String candidateGroups = map.get("candidateGroups");
				if(candidateGroups != null && !"".equals(candidateGroups)) {
					WorkRole entity = new WorkRole();
					entity.setWorkFlowId(workFlowId);
					entity.setRoleKey(candidateGroups);
					entity.setLevel(StrUtils.tranInteger(map.get("value")));
					entity.setProcessSegment(map.get("id"));
					entity.setProcessName(map.get("name"));
					entity.setRemark("部署新流程时,系统分析部署文件自动导入。");
					entity.createdUser(this.getSessionUserId() == null ? ConfigProperty.SYSTEM_FLAG : this.getSessionUserId());
					
					String processName = entity.getProcessName();
					String roleName = workName + "_" + processName;
					List<Role> roleList = this.roleDao.findByPropertyName(Role.class, "roleName", roleName);
					if(roleList != null && !roleList.isEmpty()) {
						entity.setRoleId(roleList.get(0).getId());
					} else {
						roleList = this.roleDao.findByPropertyName(Role.class, "roleName", processName);
					}
					if(StrUtils.checkCollection(roleList)) {
						entity.setRoleId(roleList.get(0).getId());
					}
					workRoleDao.add(entity);
				}
			}
		}
	}
	
}

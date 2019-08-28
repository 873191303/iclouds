package com.h3c.iclouds.biz.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.h3c.iclouds.base.BaseBizImpl;
import com.h3c.iclouds.base.BaseDAO;
import com.h3c.iclouds.biz.GroupBiz;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.dao.GroupDao;
import com.h3c.iclouds.po.Groups;
import com.h3c.iclouds.po.User2Group;

@SuppressWarnings("unchecked")
@Service("groupBiz")
public class GroupBizImpl extends BaseBizImpl<Groups> implements GroupBiz {
	
	@Resource
	private GroupDao groupDao;
	
	@Resource(name = "baseDAO")
	private BaseDAO<User2Group> userToGroupDao;
	
	@Override
	public PageModel<Groups> findForPage(PageEntity entity) {
		return this.groupDao.findForPage(entity);
	}
	
	@Override
	public ResultType update(Groups entity, Map<String, Object> map) {
		if(map.containsKey("users")) {
			List<User2Group> set = userToGroupDao.findByPropertyName(User2Group.class, "gid", entity.getId());
			Set<String> userIds = new HashSet<String>();
			if(set != null && !set.isEmpty()) {
				for (User2Group r2g : set) {
					this.userToGroupDao.delete(r2g);
					userIds.add(r2g.getUserId());
				}
			}
			List<String> userArray = (ArrayList<String>) map.get("users");
			if(userArray != null && !userArray.isEmpty()) {
				for (int i = 0; i < userArray.size(); i++) {
					User2Group u2g = new User2Group();
					u2g.setUserId(userArray.get(i));
					u2g.createdUser(this.getSessionBean().getUserId());
					u2g.setGid(entity.getId());
					
					Map<String, Object> queryMap = new HashMap<String, Object>();
					queryMap.put("userId", u2g.getUserId());
					List<User2Group> userGroupList = this.userToGroupDao.listByClass(User2Group.class, queryMap);
					if(userGroupList == null || userGroupList.isEmpty()) {	// 用户不存在群组，则当前群组作为用户的默认群组
						u2g.setIsDefault(ConfigProperty.YES);
					} else {
						boolean defaultGroup = false;
						for (User2Group temp : userGroupList) {
							if(ConfigProperty.YES.equals(temp.getIsDefault())) {	// 用户存在默认群组则不需要处理
								defaultGroup = true;
								break;
							}
						}
						if(!defaultGroup) {
							u2g.setIsDefault(ConfigProperty.YES);
						}
					}
					userToGroupDao.add(u2g);
					if(userIds.contains(u2g.getUserId())) {
						userIds.remove(u2g.getUserId());
					}
				}
			}
			if(!userIds.isEmpty()) {
				for (String userId : userIds) {
					// 没有群组则默认一个系统群组
					User2Group u2g = new User2Group(userId, this.getLoginUser());
					userToGroupDao.add(u2g);
				}
			}
		}
		return ResultType.success;
	}
}

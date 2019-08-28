package com.h3c.iclouds.biz.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.h3c.iclouds.base.BaseBizImpl;
import com.h3c.iclouds.base.BaseDAO;
import com.h3c.iclouds.base.SqlQueryBiz;
import com.h3c.iclouds.biz.NoticeBiz;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.common.SqlQueryProperty;
import com.h3c.iclouds.dao.NoticeDao;
import com.h3c.iclouds.dao.ProjectDao;
import com.h3c.iclouds.exception.MessageException;
import com.h3c.iclouds.po.Notice;
import com.h3c.iclouds.po.Notice2Project;
import com.h3c.iclouds.po.NoticeDetail;
import com.h3c.iclouds.po.Project;
import com.h3c.iclouds.utils.InvokeSetForm;
import com.h3c.iclouds.validate.ValidatorUtils;

/**
* @author  zKF7420
* @date 2017年1月11日 上午11:22:08
*/
@Service(value = "noticeBiz")
public class NoticeBizImpl extends BaseBizImpl<Notice> implements NoticeBiz {
	
	@Resource
	NoticeDao noticeDao;
	
	@Resource(name = "baseDAO")
    private BaseDAO<NoticeDetail> noticeDetailDao;
	
	@Resource
	private ProjectDao projectDao;
	
	@Resource(name = "baseDAO")
	private BaseDAO<Notice2Project> notice2ProjectDao;
	
	@Resource
	private SqlQueryBiz sqlQueryBiz;
	
	@Override
	public void withdraw(String id) {
		boolean isAdmin = this.getSessionBean().getSuperUser();
		if (!isAdmin) {
			throw new MessageException(ResultType.unAuthorized);
		}
		
		Notice notice = noticeDao.findById(Notice.class, id);
		if (null == notice || !"0".equals(notice.getDeleted())) {
			throw new MessageException(ResultType.deleted);
		}
		
		String status = notice.getStatus();
		if (!status.equals(ConfigProperty.NOTICE_STATUS2_USE)) {
			throw new MessageException(ResultType.only_can_withdraw_published_notice);
		}
		
		notice.setStatus(ConfigProperty.NOTICE_STATUS1_DRAFT);
		noticeDao.update(notice);
		List<Notice2Project> lists = notice2ProjectDao.findByPropertyName(Notice2Project.class, "noticeId", id);
		notice2ProjectDao.delete(lists);
	}

	@Override
	public void save(Notice entity) {
		boolean isAdmin = this.getSessionBean().getSuperUser();
		if (!isAdmin) {
			throw new MessageException(ResultType.unAuthorized);
		}
		Map<String, String> validatorMap = ValidatorUtils.validator(entity); // 验证数据
		if (!validatorMap.isEmpty()) {
			throw new MessageException(ResultType.parameter_error);
		}
		
		String tenantId = this.getProjectId();
		String userId = this.getLoginUser();
		String noticeId = this.saveNoticeAndDetail(tenantId, userId, entity);
		
		// 如果是草稿状态,不用向  n2p 中 插入数据
		if (ConfigProperty.NOTICE_STATUS2_USE.equals(entity.getStatus())) {
			List<Project> projects = projectDao.getAll(Project.class);
			for (Project project : projects) {
				this.saveNotice2Project(userId, project.getId(), noticeId);
			}
		}
	}
	
	@Override
	public void update(String id, Notice entity) {
		boolean isAdmin = this.getSessionBean().getSuperUser();
		if (!isAdmin) {
			throw new MessageException(ResultType.unAuthorized);
		}
		
		Map<String, String> map = new HashMap<String, String>();
		map.put("deleted", "0");
		map.put("id", id);
		List<Notice> notices = noticeDao.findByMap(Notice.class, map);
		if(notices == null || notices.size() == 0) {
			throw new MessageException(ResultType.deleted);
		}
		String oldStatus = notices.get(0).getStatus();
		if (oldStatus.equals(ConfigProperty.NOTICE_STATUS2_USE)) {
			throw new MessageException(ResultType.publicshed_notice_cant_be_updated);
		}
		
		String detail = entity.getDetail();
		if (null != detail && !"".equals(detail)) {
			Map<String, Object> wMap = new HashMap<String, Object>();
			wMap.put("noticeId", id);
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("detail", detail);
			noticeDetailDao.update(wMap, paramMap, NoticeDetail.class);
		}
		
		Notice notice = notices.get(0);
		InvokeSetForm.copyFormProperties(entity, notice);
		String userId = this.getLoginUser();
		notice.updatedUser(userId);
		noticeDao.update(notice);
		
		String tenantId = this.getProjectId();
		String newStatus = entity.getStatus();
		if (newStatus.equals(ConfigProperty.NOTICE_STATUS2_USE)) { // 如果从草稿状态修改为发布状态，就插入notice-project的对应关系
			List<Project> projects = projectDao.getAll(Project.class);
			for (Project project : projects) {
				if (!tenantId.equals(project.getId())) {
					this.saveNotice2Project(userId, project.getId(), notice.getId());
				} 
			}
		}
	}

	@Override
	public void changeReadStatus(String tenantId, String noticeId) {
		boolean isAdmin = this.getSessionBean().getSuperUser();
		if (isAdmin) {
			throw new MessageException(ResultType.dont_have_this_operation);
		}
		
		Map<String, String> map = new HashMap<String, String>();
		map.put("deleted", "0");
		map.put("id", noticeId);
		List<Notice> notices = noticeDao.findByMap(Notice.class, map);
		if (null == notices || notices.size() ==0) {
			throw new MessageException(ResultType.deleted);
		}
		
		Map<String, String> queryMap = new HashMap<String, String>();
		queryMap.put("tenantId", tenantId);
		queryMap.put("noticeId", noticeId);
		List<Notice2Project> no2Pros = notice2ProjectDao.findByMap(Notice2Project.class, queryMap);
		if (null == no2Pros || no2Pros.size() ==0) {
			throw new MessageException(ResultType.deleted);
		}
		
		String userId = this.getLoginUser();
		Notice2Project no2Pro = no2Pros.get(0);
		boolean isRead = no2Pro.getIsRead();
		no2Pro.setIsRead(!isRead);
		no2Pro.updatedUser(userId);
		notice2ProjectDao.update(no2Pro);
	}

	@Override
	public List<Notice> lastlyUnreadNotice(String tenantId) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("tenantId", tenantId);
		
		List<Map<String, Object>> lists = sqlQueryBiz.queryByName(SqlQueryProperty.QUERY_TENANT_LATELY_UNREAND_NOTICES, map);
		List<Notice> records = new ArrayList<Notice>();
		if (null != lists && lists.size() > 0) {
			for (Map<String, Object> subMap : lists) {
				String str = JSON.toJSONString(subMap);
				Notice record = JSON.parseObject(str, Notice.class);
				records.add(record);
			}
		}
		return records;
	}

	@Override
	public void sendPrivateNotice(String tenantId, Notice entity) {
		boolean isAdmin = this.getSessionBean().getSuperUser();
		if (isAdmin) {
			throw new MessageException(ResultType.unAuthorized);
		}
		String projectId = this.getProjectId();
		if (projectId.equals(tenantId)) {
			throw new MessageException(ResultType.canot_send_notice_to_yourself);
		}
		
		Map<String, String> validatorMap = ValidatorUtils.validator(entity); // 验证数据
		if (!validatorMap.isEmpty()) {
			throw new MessageException(ResultType.parameter_error);
		}
		
		String userId = this.getLoginUser();
		String noticeId = this.saveNoticeAndDetail(projectId, userId, entity);
		this.saveNotice2Project(userId, tenantId, noticeId);
	}
	
	private String saveNoticeAndDetail(String tenantId, String userId, Notice entity) {
		entity.createdUser(userId);
		entity.setDeleted("0");
		entity.setTenantId(tenantId);
		String noticeId = noticeDao.add(entity);
		
		NoticeDetail detail = new NoticeDetail();
		detail.setNoticeId(noticeId);
		detail.setDetail(entity.getDetail());
		noticeDetailDao.add(detail);
		return noticeId;
	}
	
	private void saveNotice2Project(String userId, String tenantId, String noticeId) {
		Notice2Project no2Pro = new Notice2Project();
		no2Pro.createdUser(userId);
		no2Pro.setNoticeId(noticeId);
		no2Pro.setIsRead(Boolean.FALSE);
		no2Pro.setTenantId(tenantId);
		notice2ProjectDao.add(no2Pro);
	}

	@Override
	public void delete(String id) {
		boolean isAdmin = this.getSessionBean().getSuperUser();
		if (!isAdmin) {
			throw new MessageException(ResultType.unAuthorized);
		}
		
		Notice notice = noticeDao.findById(Notice.class, id);
		if (null == notice || !"0".equals(notice.getDeleted())) {
			throw new MessageException(ResultType.deleted);
		}
		
		String status = notice.getStatus();
		if (!status.equals(ConfigProperty.NOTICE_STATUS1_DRAFT)) {
			throw new MessageException(ResultType.only_can_delete_draf_notice);
		}
		notice.setDeleted("1");
		noticeDao.update(notice);
	}
	
}

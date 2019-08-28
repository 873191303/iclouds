package com.h3c.iclouds.biz;

import java.util.List;

import com.h3c.iclouds.base.BaseBiz;
import com.h3c.iclouds.po.Notice;

/**
* @author  zKF7420
* @date 2017年1月11日 上午11:21:06
*/
public interface NoticeBiz extends BaseBiz<Notice> {

	void delete(String id);

	void save(Notice entity);

	void update(String id, Notice entity);

	/** 公告置为已读*/
	void changeReadStatus(String tenantId, String noticeId);

	/** 租户最近未读通知公告*/
	List<Notice> lastlyUnreadNotice(String tenantId);

	/** 发送私信*/
	void sendPrivateNotice(String tenantId, Notice entity);

	/** 撤销通知公告*/
	void withdraw(String id);

}

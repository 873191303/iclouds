package com.h3c.iclouds.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.h3c.iclouds.base.BaseDAO;
import com.h3c.iclouds.base.BaseRest;
import com.h3c.iclouds.base.BaseRestControl;
import com.h3c.iclouds.biz.NoticeBiz;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageList;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.dao.NoticeDao;
import com.h3c.iclouds.exception.MessageException;
import com.h3c.iclouds.po.Notice;
import com.h3c.iclouds.po.Notice2Project;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

/**
 * @author zKF7420
 * @date 2017年1月11日 上午11:11:36
 */
@Api(value = "通知公告")
@RestController
@RequestMapping(value = "/notice")
public class NoticeRest extends BaseRest<Notice> {

	@Resource
	private NoticeDao noticeDao;

	@Resource
	private NoticeBiz noticeBiz;
	
	@Resource(name = "baseDAO")
	private BaseDAO<Notice2Project> notice2ProjectDao;

	@Override
	@ApiOperation(value = "获取通知公告列表", response = Notice.class)
	@RequestMapping(method = RequestMethod.GET)
	public Object list() {
		boolean isAdmin = this.getSessionBean().getSuperUser();
		String tenantId = this.getProjectId();
		List<Notice> notices = new ArrayList<Notice>();
		if (isAdmin) {
			Map<String, String> map = new HashMap<String, String>();
			map.put("deleted", "0");
			map.put("tenantId", tenantId);
			notices = noticeBiz.findByMap(Notice.class, map);
		} else {
			List<String> ids = this.getIds(tenantId);
			Map<String, Object> queryMap = new HashMap<String, Object>();
			if (ids.size() > 0) {
				queryMap.put("id", ids);
			}
			queryMap.put("status", ConfigProperty.NOTICE_STATUS2_USE); // 普通租户只能看到发布状态的通知公告
			notices = noticeBiz.listByClass(Notice.class, queryMap);
			this.addIsRead(notices, tenantId);
		}
		return BaseRestControl.tranReturnValue(ResultType.success, notices);
	}

	@ApiOperation(value = "获取通知公告分页列表", response = Notice.class)
	@RequestMapping(value = "/pageList", method = RequestMethod.GET)
	public Object pageList() {
		PageEntity entity = this.beforeList();
		boolean isAdmin = this.getSessionBean().getSuperUser();
		String tenantId = this.getProjectId();
		if (isAdmin) {
			entity.setSpecialParam(tenantId);
		} else {
			List<String> ids = this.getIds(tenantId);
			entity.setSpecialParams(ids.toArray(new String[ids.size()]));
		}
		PageModel<Notice> pageModel = noticeDao.findForPage(entity);
		PageList<Notice> page = new PageList<Notice>(pageModel, entity.getsEcho());
		if (!isAdmin) {
			List<Notice> notices = page.getAaData();
	        this.addIsRead(notices, tenantId);
		}
		return BaseRestControl.tranReturnValue(page);
	}

	@Override
	@ApiOperation(value = "通知公告详情", response = Notice.class)
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public Object get(@PathVariable String id) {
		Notice notice = noticeDao.findById(Notice.class, id);
		if (null != notice) {
			return BaseRestControl.tranReturnValue(ResultType.success, notice);
		}
		return BaseRestControl.tranReturnValue(ResultType.deleted);
	}

	@Override
	@ApiOperation(value = "删除通知公告(只能删除草稿状态的通知公告)")
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public Object delete(@PathVariable String id) {
		try {
			noticeBiz.delete(id);
			return BaseRestControl.tranReturnValue(ResultType.success);
		} catch (Exception e) {
			if (e instanceof MessageException) {
				return BaseRestControl.tranReturnValue(ResultType.failure, ((MessageException) e).getResultCode());
			}
			this.exception(this.getClass(), e);
			return BaseRestControl.tranReturnValue(ResultType.failure);
		}
	}
	
	@ApiOperation(value = "撤销通知公告(只能撤回发布状态的通知公告)")
	@RequestMapping(value = "/{id}/withdraw", method = RequestMethod.PUT)
	public Object withdraw(@PathVariable String id) {
		try {
			noticeBiz.withdraw(id);
			return BaseRestControl.tranReturnValue(ResultType.success);
		} catch (Exception e) {
			if (e instanceof MessageException) {
				return BaseRestControl.tranReturnValue(ResultType.failure, ((MessageException) e).getResultCode());
			}
			this.exception(this.getClass(), e);
			return BaseRestControl.tranReturnValue(ResultType.failure);
		}
	}

	@Override
	@ApiOperation(value = "发布通知公告")
	@RequestMapping(method = RequestMethod.POST)
	public Object save(@RequestBody Notice entity) {
		try {
			noticeBiz.save(entity);
			return BaseRestControl.tranReturnValue(ResultType.success);
		} catch (Exception e) {
			if (e instanceof MessageException) {
				return BaseRestControl.tranReturnValue(ResultType.failure, ((MessageException) e).getResultCode());
			}
			this.exception(this.getClass(), e);
			return BaseRestControl.tranReturnValue(ResultType.failure);
		}
	}

	@Override
	@ApiOperation(value = "修改通知公告")
	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	public Object update(@PathVariable String id, @RequestBody Notice entity) throws IOException {
		try {
			noticeBiz.update(id, entity);
			return BaseRestControl.tranReturnValue(ResultType.success);
		} catch (Exception e) {
			if (e instanceof MessageException) {
				return BaseRestControl.tranReturnValue(ResultType.failure, ((MessageException) e).getResultCode());
			}
			this.exception(this.getClass(), e);
			return BaseRestControl.tranReturnValue(ResultType.failure);
		}
	}

	@ApiOperation(value = "改变是否已阅读")
	@RequestMapping(value = "/{id}/changeRead", method = RequestMethod.PUT)
	public Object changeReadStatus(@PathVariable String id) {
		try {
			String tenantId = this.getProjectId();
			noticeBiz.changeReadStatus(tenantId, id);
			return BaseRestControl.tranReturnValue(ResultType.success);
		} catch (Exception e) {
			if (e instanceof MessageException) {
				return BaseRestControl.tranReturnValue(ResultType.failure, ((MessageException) e).getResultCode());
			}
			this.exception(this.getClass(), e);
			return BaseRestControl.tranReturnValue(ResultType.failure);
		}
	}

	@ApiOperation(value = "最近未阅读的通知公告", response = Notice.class)
	@RequestMapping(value = "/unread", method = RequestMethod.GET)
	public Object lastlyUnreadNotice() {
		try {
			String tenantId = this.getProjectId();
			List<Notice> notices = noticeBiz.lastlyUnreadNotice(tenantId);
			return BaseRestControl.tranReturnValue(ResultType.success, notices);
		} catch (Exception e) {
			if (e instanceof MessageException) {
				return BaseRestControl.tranReturnValue(ResultType.failure, ((MessageException) e).getResultCode());
			}
			this.exception(this.getClass(), e);
			return BaseRestControl.tranReturnValue(ResultType.failure);
		}
	}
	
//	@ApiOperation(value = "发送私信")
//	@RequestMapping(value = "/{tenantId}/sendPrivate", method = RequestMethod.POST)
//	public Object sendPrivateNotice(@PathVariable String tenantId, @RequestBody Notice entity) {
//		try {
//			noticeBiz.sendPrivateNotice(tenantId, entity);
//			return BaseRestControl.tranReturnValue(ResultType.success);
//		} catch (Exception e) {
//			if (e instanceof MessageException) {
//				return BaseRestControl.tranReturnValue(ResultType.failure, ((MessageException) e).getResultCode());
//			}
//			this.exception(this.getClass(), e);
//			return BaseRestControl.tranReturnValue(ResultType.failure);
//		}
//	}

	private List<String> getIds(String tenantId) {
		List<String> ids = new ArrayList<String>();
		List<Notice2Project> no2pros = notice2ProjectDao.findByPropertyName(Notice2Project.class, "tenantId", tenantId);
		for (Notice2Project notice2Project : no2pros) {
			ids.add(notice2Project.getNoticeId());
		}
		return ids;
	}
	
	private void addIsRead(List<Notice> notices, String tenantId) {
		if (null == notices || notices.size() == 0) {
			return ;
		}
		for (int i = 0; i < notices.size(); i++) {
			Notice notice = notices.get(i);
			Map<String, Object> queryMap = new HashMap<String, Object>();
			queryMap.put("noticeId", notice.getId());
			queryMap.put("tenantId", tenantId);
			List<Notice2Project> no2Pros = notice2ProjectDao.listByClass(Notice2Project.class, queryMap);
			if (null != no2Pros && no2Pros.size() > 0) {
				notice.setIsRead(no2Pros.get(0).getIsRead());
			}
		}
	}

}

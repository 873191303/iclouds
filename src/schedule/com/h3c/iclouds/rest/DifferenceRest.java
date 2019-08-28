package com.h3c.iclouds.rest;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.h3c.iclouds.base.BaseDAO;
import com.h3c.iclouds.base.BaseRest;
import com.h3c.iclouds.base.BaseRestControl;
import com.h3c.iclouds.biz.DifferencesBiz;
import com.h3c.iclouds.common.CompareEnum;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageList;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.exception.MessageException;
import com.h3c.iclouds.po.Differences;
import com.h3c.iclouds.po.TaskDispatch;
import com.h3c.iclouds.utils.StrUtils;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/task")
@Api(value = "同步差异处理")
public class DifferenceRest extends BaseRest<Differences> {
	
	@Resource
	private DifferencesBiz differencesBiz;
	
	@Resource(name = "baseDAO")
	private BaseDAO<TaskDispatch> baseDAO;

	@RequestMapping(method = RequestMethod.GET)
	@ApiOperation(value = "获取任务列表")
	public Object task() {
		List<TaskDispatch> list = baseDAO.getAll(TaskDispatch.class);
		return BaseRestControl.tranReturnValue(list);
	}

	@RequestMapping(value = "/diff/last", method = RequestMethod.GET)
	@ApiOperation(value = "获取最后一次异常同步列表")
	@Override
	public Object list() {
		TaskDispatch entity = baseDAO.singleByClass(TaskDispatch.class, StrUtils.createMap("id", CompareEnum.TASK_DIFF_ID.getOpeValue()));
		ResultType resultType = this.checkDiffTask(entity);
		if(resultType != ResultType.success) {
			return resultType;
		}
		Map<String, Object> queryMap = StrUtils.createMap("syncDate", entity.getLastSyncTime());
		PageEntity pageEntity = this.beforeList();
		pageEntity.setQueryMap(queryMap);
		PageModel<Differences> pageModel = differencesBiz.findForPage(pageEntity);
		PageList<Differences> page = new PageList<Differences>(pageModel, pageEntity.getsEcho());
		return BaseRestControl.tranReturnValue(page);
	}

	@Override
	@RequestMapping(value = "/diff/{id}", method = RequestMethod.GET)
	@ApiOperation(value = "获取异常同步项")
	public Object get(@PathVariable String id) {
		Differences entity = differencesBiz.findById(Differences.class, id);
		if(entity == null) {
			return BaseRestControl.tranReturnValue(ResultType.not_exist);
		}
		return BaseRestControl.tranReturnValue(entity);
	}
	
	@RequestMapping(value = "/diff/{id}", method = RequestMethod.DELETE)
	@ApiOperation(value = "删除同步差异项")
	@Override
	public Object delete(@PathVariable String id) {
		return this.handle(id, CompareEnum.OPE_DEL);
	}
	
	@RequestMapping(value = "/diff/{id}", method = RequestMethod.POST)
	@ApiOperation(value = "同步差异部分的信息")
	public Object postDiff(@PathVariable String id) {
		return this.handle(id, CompareEnum.OPE_SYNC);
	}
	
	@RequestMapping(value = "/diff/{id}", method = RequestMethod.PUT)
	@ApiOperation(value = "同步差异项")
	public Object putDiff(@PathVariable String id) {
		return this.handle(id, CompareEnum.OPE_DIFF);
	}

	private Object handle(String id, CompareEnum compareEnum) {
		Differences entity = differencesBiz.findById(Differences.class, id);
		ResultType resultType = checkHandleDiff(entity);
		if(resultType != ResultType.success) {
			return BaseRestControl.tranReturnValue(resultType);
		}
		String diffType = entity.getDiffType();
		// 数据差异类型为属性
		if(CompareEnum.DATA_DIFF.toString().equals(diffType)) {
			if(compareEnum != CompareEnum.OPE_DIFF) {
				return BaseRestControl.tranReturnValue(ResultType.diff_handle_method_error);
			}
		} else {
			if(compareEnum == CompareEnum.OPE_DIFF) {
				return BaseRestControl.tranReturnValue(ResultType.diff_handle_method_error);
			}
		}
		try {
            // 任务设置为正在执行
            entity.setResult(ResultType.task_diff_already_handled.toString());
            differencesBiz.update(entity);
			resultType = this.differencesBiz.handleDiff(entity, compareEnum);
		} catch (Exception e) {
			this.exception(e);
			resultType = ResultType.failure;
			if (e instanceof MessageException){
				String resultMsg = e.getMessage();
				resultType = ((MessageException) e).getResultCode();
				if (!StrUtils.checkParam(resultType)){
					resultType = ResultType.cloudos_exception;
				}
				return BaseRestControl.tranReturnValue(resultType, resultMsg);
			}
			return BaseRestControl.tranReturnValue(resultType);
		} finally {
			entity.setTodoType(compareEnum.toString());// 设置类型
			entity.setResult(resultType.toString());
			entity.setTodoTime(new Date());
			entity.setTodoUser(this.getLoginUser());
			this.differencesBiz.update(entity);
		}
		return BaseRestControl.tranReturnValue(resultType, entity);
	}

	/**
	 * 验证是否符合操作的条件
	 * 必须是最新的同步记录
	 * 还未处理或者邮件处理完但未成功的状态
	 * @param entity
	 * @return
	 */
	public ResultType checkHandleDiff(Differences entity) {
		TaskDispatch task = baseDAO.singleByClass(TaskDispatch.class, StrUtils.createMap("id", CompareEnum.TASK_DIFF_ID.getOpeValue()));
		ResultType resultType = this.checkDiffTask(task);
		if(resultType != ResultType.success) {
			return resultType;
		}
		
		if(entity == null) {
			return ResultType.not_exist;
		}
		
		// 是否是过期内容
		if(entity.getSyncDate().getTime() != task.getLastSyncTime().getTime()) {
			return ResultType.task_diff_limit_effect;
		}
		
		String result = entity.getResult();
		// 已经处理或者正在处理
		if("task_diff_already_handled".equals(result) || "success".equals(result)) {
			return ResultType.task_diff_already_handled;
		}
		return ResultType.success;
	}
	
	public ResultType checkDiffTask(TaskDispatch entity) {
		if(entity == null) {
			return ResultType.task_diff_not_define;
		}
		Date date = entity.getLastSyncTime();
		if(date == null) {
			return ResultType.task_diff_not_start;
		}
		return ResultType.success;
	}

	@Override
	public Object save(Differences entity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object update(String id, Differences entity) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

}

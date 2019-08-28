package com.h3c.iclouds.base;

import com.h3c.iclouds.auth.SessionBean;
import com.h3c.iclouds.biz.OperateLogsBiz;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.exception.MessageException;
import com.h3c.iclouds.po.User;
import com.h3c.iclouds.utils.LogUtils;
import com.h3c.iclouds.utils.SessionRedisUtils;
import com.h3c.iclouds.utils.StrUtils;
import com.h3c.iclouds.utils.ThreadContext;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.internal.CriteriaImpl;
import org.hibernate.persister.entity.AbstractEntityPersister;
import org.hibernate.transform.Transformers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 数据访问层实现基类
 * 
 * @author Administrator
 * 
 * @param <T>
 */
@Repository(value = "baseDAO")
public class BaseDAOImpl<T extends java.io.Serializable> implements BaseDAO<T> {
	
	@Resource(name="sessionFactory")
	private SessionFactory sessionFactory;
	
	@Resource
	public OperateLogsBiz operateLogsBiz;
	
	@Resource
	public HttpServletRequest request;
	
	public Logger log = LoggerFactory.getLogger(this.getClass());
	
	/**
	 * 需要记录的信息日志
	 * @param obj
	 */
	public void info(Object obj) {
		log.info(LogUtils.print(obj));
	}
	
	/**
	 * 异常内容日志打印
	 * @param e
	 */
	public void exception(Exception e, Object...objects) {
		if(!(e instanceof MessageException)) {	// message异常不做打印
			e.printStackTrace();
		}
		log.error(LogUtils.print(e.getMessage()));
		if(objects != null && objects.length > 0) {
			for (Object obj : objects) {
				log.error(LogUtils.print(obj));
			}
		}
	}
	
	/**
	 * 重要内容日志打印
	 */
	public void warn(Object obj) {
		log.warn(LogUtils.print(obj));
	}
	
	@Override
	public String getUserToken() {
		return StrUtils.tranString(ThreadContext.get(ConfigProperty.PROJECT_TOKEN_KEY));
	}
	
	/**
	 * 頁號
	 */
	private int pageNo = 1;
	/**
	 * 頁面大小
	 */
	private int pageSize = 20;

	public int getPageNo() {
		return pageNo;
	}

	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	@Override
	public Session getSession() {
		return this.sessionFactory.getCurrentSession();
	}
	
	/**
	 * 新增
	 */
	@Override
	public String add(T obj) {
		try {
			this.info("Add " + obj.getClass() + " ---- value:" + StrUtils.toJSONString(obj));
			Serializable ser = getSession().save(obj);
			return ser.toString();
		} catch (RuntimeException re) {
			throw re;
		}
	}
	
	public void addOrUpdate(T obj) {
		try {
			this.info("AddOrUpdate " + obj.getClass() + " ---- value:" + StrUtils.toJSONString(obj));
			this.getSession().saveOrUpdate(obj);
		} catch (RuntimeException re) {
			throw re;
		}
	}
	
	public void persist(T obj) {
		try {
			this.getSession().persist(obj);
		} catch (RuntimeException re) {
			throw re;
		}
	}

	/**
	 * 更新
	 */

	@Override
	public void update(T obj) {
		try {
			this.info("Update " + obj.getClass() + " ---- value:" + StrUtils.toJSONString(obj));
			this.getSession().update(obj);
		} catch (RuntimeException re) {
			throw re;
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public T load(Class<T> clazz, String objId) {
		try {
			return (T) this.getSession().load(clazz, objId);
		} catch (RuntimeException re) {
			throw re;
		}
	}
	/**
	 * 更新
	 * @param updateWhere 更新条件
	 * @param updateMap 更新值
	 * @param clazz 更新对象
	 * @return
	 */
	public int update(Map<String, Object> updateWhere,
			Map<String, Object> updateMap, Class<T> clazz) {
		this.info("Update " + clazz + " ---- updateWhere:" + StrUtils.toJSONString(updateWhere) + " ---- updateWhere:" + StrUtils.toJSONString(updateMap));
		if (updateMap == null || updateMap.size() == 0)
			return 0;

		StringBuilder hql = new StringBuilder();
		hql.append(MessageFormat.format("Update {0} tab set", clazz.getName()));
		for (String str : updateMap.keySet())
			hql.append(MessageFormat.format(" tab.{0}=:{0},", str));

		hql.deleteCharAt(hql.length() - 1);
		if (updateWhere != null && updateWhere.size() > 0){
			hql.append(" where");
			for (String str : updateWhere.keySet()) {
				Object objValue = updateWhere.get(str);
				if (objValue instanceof Collection)
					hql.append(MessageFormat.format(" tab.{0} in(:{0}) and", str));
				else
					hql.append(MessageFormat.format(" tab.{0}=:{0} and", str));
			}
			hql.delete(hql.length() - 4, hql.length());
			updateMap.putAll(updateWhere);
		}
		return this.updateByHql(hql.toString(), null, updateMap);
	}

	/**
	 * 更新
	 * @param deleteWhere 删除条件
	 * @param clazz 更新对象
	 * @return
	 */
	public int delete(Map<String, Object> deleteWhere, Class<T> clazz) {
		this.warn("Delete " + clazz + " ---- deleteWhere:" + StrUtils.toJSONString(deleteWhere));
		StringBuilder hql = new StringBuilder();
		hql.append(MessageFormat.format("delete {0} tab ", clazz.getName()));
        if(deleteWhere!=null&&deleteWhere.size()>0){
        	hql.append(" where");
    		for (String str : deleteWhere.keySet()) {
    			Object objValue = deleteWhere.get(str);
    			if (objValue instanceof Collection)
    				hql.append(MessageFormat.format(" tab.{0} in(:{0}) and", str));
    			else
    				hql.append(MessageFormat.format(" tab.{0}=:{0} and", str));
    		}
    		hql.delete(hql.length() - 4, hql.length());
        }
		return this.updateByHql(hql.toString(), null, deleteWhere);
	}
	
	/**
	 * 删除
	 */
	@Override
	public void delete(T obj) {
		try {
			this.warn("Delete " + this.getClass() + " ---- value:" + StrUtils.toJSONString(obj));
			this.getSession().delete(obj);
		} catch (RuntimeException re) {
			throw re;
		}
	}

	/**
	 * 根据主键查找
	 */
	@SuppressWarnings("unchecked")
	@Override
	public T findById(Class<T> clazz, String objId) {
		try {
			return (T) this.getSession().get(clazz, objId);
		} catch (RuntimeException re) {
			throw re;
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public T findById(Class<T> clazz, int objId) {
		try {
			return (T) this.getSession().get(clazz, objId);
		} catch (RuntimeException re) {
			throw re;
		}
	}
	
	public T findById(Class<T> clazz, String objId,boolean clearSession){
		if(clearSession)
			this.getSession().clear();
		return findById(clazz,objId);
	}

	/**
	 * 查找所有
	 * 
	 * @param clazz
	 * @return
	 */
	@Override
	public List<T> getAll(Class<T> clazz) {
		String hql = "from " + clazz.getName();
		return this.list(hql);
	}
	
	/**
	 * 查找所有
	 * add by zhaowei
	 * @param clazz
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public List<T> listByClass(Class<T> clazz, Map<String, Object> params) {
		Criteria criteria = getSession().createCriteria(clazz);
		if (params != null && params.size() > 0) {
			for (String str : params.keySet()) {
				Object obj = params.get(str);
				if (obj instanceof Collection) {
					criteria.add(Restrictions.in(str, (Collection) obj));
				} else if (obj.getClass().isArray()) {
					criteria.add(Restrictions.in(str, (Object[]) obj));
				} else {
					criteria.add(Restrictions.eq(str, obj));
				}
			}
		}
		return criteria.list();
	}

	/**
	 * 设置参数
	 * 
	 * @param query
	 * @param alias
	 */
	public void setAliasParameter(Query query, Map<String, Object> alias) {
		if (alias != null) {
			Set<String> keys = alias.keySet();
			for (String key : keys) {
				Object val = alias.get(key);
				if (val instanceof Collection)
					query.setParameterList(key, (Collection<?>) val);
				else
					query.setParameter(key, val);
			}
		}
	}

	/**
	 * 设置参数
	 * 
	 * @param query
	 * @param args
	 */
	private void setParameter(Query query, Object[] args) {
		if (args != null && args.length > 0) {
			int index = 0;
			for (Object arg : args){
				if(arg!=null)
					query.setParameter(index, arg);
					index++;
			}
		}
	}

	/**
	 * 查询
	 * 
	 * @param hql
	 * @param args
	 * @return
	 */
	public List<T> list(String hql, Object[] args) {
		return this.list(hql, args, null);
	}

	/**
	 * 查询
	 * 
	 * @param hql
	 * @param arg
	 * @return
	 */
	public List<T> list(String hql, Object arg) {
		return this.list(hql, new Object[] { arg });
	}

	/**
	 * 查询
	 * 
	 * @param hql
	 * @return
	 */
	public List<T> list(String hql) {
		return this.list(hql, null);
	}

	/**
	 * 查询
	 * 
	 * @param hql
	 * @param args
	 * @param alias
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<T> list(String hql, Object[] args, Map<String, Object> alias) {
		Query query = getSession().createQuery(hql);
		setAliasParameter(query, alias);
		setParameter(query, args);
		return query.list();
	}

	/**
	 * 查詢
	 * 
	 * @param hql
	 * @param alias
	 * @return
	 */
	public List<T> listByalias(String hql, Map<String, Object> alias) {
		return this.list(hql, null, alias);
	}
	
	/**
	 * 查询
	 * @param hql
	 * @param alias
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <N> List<N> listEntryByalias(String hql, Map<String, Object> alias){
		Query query = getSession().createQuery(hql);
		setAliasParameter(query, alias);
		return	query.list();
	}

	/**
	 * 分頁查詢
	 * 
	 * @param hql
	 * @param args
	 * @return
	 */
	public PageModel<T> find(String hql, Object[] args) {
		return this.find(hql, args, null);
	}

	/**
	 * 分頁查詢
	 * 
	 * @param hql
	 * @param arg
	 * @return
	 */
	public PageModel<T> find(String hql, Object arg) {
		return this.find(hql, new Object[] { arg });
	}

	/**
	 * 分頁查詢
	 * 
	 * @param hql
	 * @return
	 */
	public PageModel<T> find(String hql) {
		return this.find(hql, null);
	}

	/**
	 * 分頁設置
	 * 
	 * @param query
	 * @param pages
	 */
	private void setPagers(Query query, PageModel<T> pages) {
		int pageSize = this.getPageSize();
		int pageNo = this.getPageNo();
		if (pageNo < 0)
			pageNo = 0;

		if (pageSize < 0)
			pageSize = 20;

		pages.setPageNo(pageNo);
		pages.setPageSize(pageSize);
		query.setFirstResult(pageNo * pageSize).setMaxResults(
//				(pageNo + 1) * 
				pageSize);
	}

	/**
	 * 組裝查詢總行數的Hql
	 * 
	 * @param hql
	 * @param isHql
	 * @return
	 */
	private String getCountHql(String hql, boolean isHql) {
		if(isHql){
			return MessageFormat.format("select count(*) {0}", hql.substring(hql.indexOf("from")));
		}else{
			return MessageFormat.format("select count(0) from ({0}) baseModel", hql);
		}
	}

	/**
	 * 分頁查詢
	 * 
	 * @param hql
	 * @param args
	 * @param alias
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public PageModel<T> find(String hql, Object[] args,
			Map<String, Object> alias) {
		String cq = getCountHql(hql, true);
		Query cquery = getSession().createQuery(cq);
		Query query = getSession().createQuery(hql);
		// 璁剧疆鍒悕鍙傛暟
		setAliasParameter(query, alias);
		setAliasParameter(cquery, alias);
		// 璁剧疆鍙傛暟
		setParameter(query, args);
		setParameter(cquery, args);
		PageModel<T> pages = new PageModel<T>();
		setPagers(query, pages);
		List<T> datas = query.list();
		pages.setDatas(datas);
		Long total = (Long) cquery.uniqueResult();
		pages.setTotalRecords(total);
		return pages;
	}
	
	/**
	 * 利用离线查询进行分页查询
	 * 
	 * @param clazz
	 * @param criteria
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public PageModel<T> findForPage(Class<T> clazz, Criteria criteria) {
		CriteriaImpl impl = (CriteriaImpl) criteria;
		
		//先把Projection和OrderBy条件取出来,清空两者来执行Count操作
		Projection projection = impl.getProjection();
		
		//执行查询
		int totalCount = ((Long) criteria.setProjection(Projections.rowCount()).uniqueResult()).intValue();
		
		//将之前的Projection和OrderBy条件重新设回去
		criteria.setProjection(projection);   
		
		if (projection == null) {   
			criteria.setResultTransformer(CriteriaSpecification.ROOT_ENTITY);   
		}
		criteria.setFirstResult(pageNo * pageSize);
		criteria.setMaxResults(pageSize);
		PageModel<T> pages = new PageModel<T>();
		List<T> list = null;
		try {
			if(totalCount > 0) {
				list = criteria.list();
			} else {
				list = new ArrayList<T>();
			}
			
			pages.setPageNo(pageNo * pageSize);
			pages.setPageSize(pageSize);
			pages.setTotalRecords(totalCount);
		} catch (Exception e) {
			e.printStackTrace();
			list = new ArrayList<T>();
			pages.setPageNo(pageNo * pageSize);
			pages.setPageSize(pageSize);
			pages.setTotalRecords(0);
		}
		pages.setDatas(list);
		return pages;
	}

	/**
	 * 分頁查詢
	 * 
	 * @param hql
	 * @param alias
	 * @return
	 */
	public PageModel<T> findByAlias(String hql, Map<String, Object> alias) {
		return this.find(hql, null, alias);
	}

	/**
	 * 查询单个结果
	 * 
	 * @param hql
	 * @param args
	 * @return
	 */
	public Object queryObject(String hql, Object[] args) {
		return this.queryObject(hql, args, null);
	}

	/**
	 * 查询单个结果
	 * 
	 * @param hql
	 * @param args
	 * @return
	 */
	public Object queryObject(String hql, Object args) {
		return this.queryObject(hql, new Object[] { args });
	}

	/**
	 * 查询单个结果
	 * 
	 * @param hql
	 * @return
	 */
	public Object queryObject(String hql) {
		return this.queryObject(hql, null);
	}

	/**
	 * 查询单个结果
	 * 
	 * @param hql
	 * @param args
	 * @param alias
	 * @return
	 */
	public Object queryObject(String hql, Object[] args,
			Map<String, Object> alias) {
		Query query = getSession().createQuery(hql);
		setAliasParameter(query, alias);
		setParameter(query, args);
		return query.uniqueResult();
	}

	/**
	 * 查询单个结果
	 * 
	 * @param hql
	 * @param alias
	 * @return
	 */
	public Object queryObjectByAlias(String hql, Map<String, Object> alias) {
		return this.queryObject(hql, null, alias);
	}

	/**
	 * 执行、更新Hql语句
	 * 
	 * @param hql
	 * @param args
	 * @param alias
	 */
	public int updateByHql(String hql, Object[] args, Map<String, Object> alias) {
		this.info("UpdateByHql " + hql + " ---- args:" + StrUtils.toJSONString(args) + " ---- alias:" + StrUtils.toJSONString(alias));
		Query query = getSession().createQuery(hql);
		setParameter(query, args);
		setAliasParameter(query, alias);
		return query.executeUpdate();
	}
	
	@Override
	public int updateByHql(String hql, Object[] args) {
		this.info("UpdateByHql " + hql + " ---- args:" + StrUtils.toJSONString(args));
		Query query = getSession().createQuery(hql);
		setParameter(query, args);
		return query.executeUpdate();
	}
	
	/**
	 * 执行、更新Hql语句
	 * 
	 * @param hql
	 * @param args
	 */
	@Override
	public int updateByHql(String hql, Object args) {
		return this.updateByHql(hql, new Object[] { args }, null);
	}

	/**
	 * 执行、更新Hql语句
	 * 
	 * @param hql
	 */
	@Override
	public int updateByHql(String hql) {
		return this.updateByHql(hql, null);
	}
	
	/**
	 * 执行、更新Sql语句
	 * @param sql
	 * @param args
	 * @param alias
	 * @return
	 */
	@Override
	public int updateBySql(String sql,Object[] args, Map<String, Object> alias){
		this.info("UpdateBySql " + sql + " ---- args:" + StrUtils.toJSONString(args) + " ---- alias:" + StrUtils.toJSONString(alias));
		SQLQuery sq = getSession().createSQLQuery(sql);
		setAliasParameter(sq, alias);
		setParameter(sq, args);
		return sq.executeUpdate(); 
	}

	/**
	 * 使用Sql语句查询
	 * 
	 * @param sql
	 * @param args
	 * @param clz
	 * @param hasEntiry
	 * @return
	 */
	public List<T> listBySql(String sql, Object[] args, Class<T> clz,
			boolean hasEntiry) {
		return this.listBySql(sql, args, null, clz, hasEntiry);
	}

	/**
	 * 使用Sql语句查询
	 * 
	 * @param sql
	 * @param args
	 * @param clz
	 * @param hasEntiry
	 * @return
	 */
	public List<T> listBySql(String sql, Object args, Class<T> clz,
			boolean hasEntiry) {
		return this.listBySql(sql, new Object[] { args }, null, clz, hasEntiry);
	}

	/**
	 * 使用Sql语句查询
	 * 
	 * @param sql
	 * @param clz
	 * @param hasEntiry
	 * @return
	 */
	public List<T> listBySql(String sql, Class<T> clz, boolean hasEntiry) {
		return this.listBySql(sql, null, clz, hasEntiry);
	}

	/**
	 * 使用Sql语句查询
	 * 
	 * @param sql
	 * @param args
	 * @param alias
	 * @param clz
	 * @param hasEntiry
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<T> listBySql(String sql, Object[] args,
			Map<String, Object> alias, Class<T> clz, boolean hasEntiry) {
		SQLQuery sq = getSession().createSQLQuery(sql);
		setAliasParameter(sq, alias);
		setParameter(sq, args);
		if (hasEntiry) {
			sq.addEntity(clz);
		} else {
			sq.setResultTransformer(Transformers.aliasToBean(clz));
		}
		return sq.list();
	}
	
	/**
	 * 使用Sql语句查询
	 * @param sql
	 * @param args
	 * @param alias
	 * @param clz
	 * @param hasEntiry
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <N> List<N> listBySqlN(String sql, Object[] args,
			Map<String, Object> alias, Class<N> clz, boolean hasEntiry){
		SQLQuery sq = getSession().createSQLQuery(sql);
		setAliasParameter(sq, alias);
		setParameter(sq, args);
		if (hasEntiry) {
			sq.addEntity(clz);
		} else {
			sq.setResultTransformer(Transformers.aliasToBean(clz));
		}
		return sq.list();
	}

	/**
	 * 使用Sql语句查询
	 * 
	 * @param sql
	 * @param alias
	 * @param clz
	 * @param hasEntiry
	 * @return
	 */
	public List<T> listByAliasSql(String sql, Map<String, Object> alias,
			Class<T> clz, boolean hasEntiry) {
		return this.listBySql(sql, null, alias, clz, hasEntiry);
	}
	
	/**
	 * 使用Sql语句查询
	 * @param sql
	 * @param alias
	 * @param clz
	 * @param hasEntiry
	 * @return
	 */
	public <N> List<N> listByAliasSqlN(String sql, Map<String, Object> alias,
			Class<N> clz, boolean hasEntiry){
		return this.listBySqlN(sql, null, alias, clz, hasEntiry);
	}

	/**
	 * 使用Sql语句分页
	 * 
	 * @param sql
	 * @param args
	 * @param clz
	 * @param hasEntiry
	 * @return
	 */
	public PageModel<T> findBySql(String sql, Object[] args, Class<T> clz,
			boolean hasEntiry) {
		return this.findBySql(sql, args, null, clz, hasEntiry);
	}

	/**
	 * 使用Sql语句分页
	 * 
	 * @param sql
	 * @param args
	 * @param clz
	 * @param hasEntiry
	 * @return
	 */
	public PageModel<T> findBySql(String sql, Object args, Class<T> clz,
			boolean hasEntiry) {
		return this.findBySql(sql, new Object[] { args }, clz, hasEntiry);
	}

	/**
	 * 使用Sql语句分页
	 * 
	 * @param sql
	 * @param clz
	 * @param hasEntiry
	 * @return
	 */
	public PageModel<T> findBySql(String sql, Class<T> clz, boolean hasEntiry) {
		return this.findBySql(sql, null, clz, hasEntiry);
	}

	/**
	 * 使用Sql语句分页
	 * 
	 * @param sql
	 * @param args
	 * @param alias
	 * @param clz
	 * @param hasEntiry
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public PageModel<T> findBySql(String sql, Object[] args,Map<String, Object> alias, Class<T> clz, boolean hasEntiry) {
		String cq = getCountHql(sql, false);
		SQLQuery sq = getSession().createSQLQuery(sql);
		SQLQuery cquery = getSession().createSQLQuery(cq);
		setAliasParameter(sq, alias);
		setAliasParameter(cquery, alias);
		setParameter(sq, args);
		setParameter(cquery, args);
		PageModel<T> pages = new PageModel<T>();
		setPagers(sq, pages);
		if (hasEntiry) {
			sq.addEntity(clz);
		} else {
			sq.setResultTransformer(Transformers.aliasToBean(clz));
		}
		
		List<T> datas = sq.list();
		pages.setDatas(datas);
		Object obj = cquery.uniqueResult();
		if( obj == null)
			pages.setTotalRecords(0);
		else
			pages.setTotalRecords(Integer.parseInt(obj.toString()));
	
		return pages;
	}
	
	/**
	 * 使用Sql语句分页
	 * @param sql
	 * @param alias
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public PageModel<T> findBySqlReturnMap(String sql,Map<String, Object> alias){
		String cq = getCountHql(sql, false);
		SQLQuery sq = getSession().createSQLQuery(sql);
		SQLQuery cquery = getSession().createSQLQuery(cq);
		setAliasParameter(sq, alias);
		setAliasParameter(cquery, alias);
		PageModel<T> pages = new PageModel<T>();
		setPagers(sq, pages);
		sq.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		List<T> datas = sq.list();
		pages.setDatas(datas);
		Object obj = cquery.uniqueResult();
		if( obj==null)
			pages.setTotalRecords(0);
		else
			pages.setTotalRecords(Integer.parseInt(obj.toString()));
	
		return pages;
	}
	
	@SuppressWarnings("unchecked")
	public List<T> find(T entity, int start, int max) {
		Criteria c = this.getSession().createCriteria(entity.getClass());
		c.setFirstResult(start);
		c.setMaxResults(max);
		return c.list();
	}
	
	/**
	 * 使用Sql语句分页
	 * 
	 * @param sql
	 * @param alias
	 * @param clz
	 * @param hasEntiry
	 * @return
	 */
	public PageModel<T> findByAliasSql(String sql, Map<String, Object> alias,
			Class<T> clz, boolean hasEntiry) {
		return this.findBySql(sql, null, alias, clz, hasEntiry);
	}

	@Override
	public void deleteById(Class<T> clazz, String objId) {
		this.delete(findById(clazz, objId));
	}
	
	@Override
	public void deleteById(Class<T> clazz, int objId) {
		this.delete(findById(clazz, objId));
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<T> findByPropertyName(Class<T> clazz, String propertyName, String propertyValue) {
		Criteria criteria = getSession().createCriteria(clazz);
		if(propertyName != null) {
			criteria.add(Restrictions.eq(propertyName, propertyValue));	
		}
		return criteria.list();
	}
	
	@Override
	public int findCountByPropertyName(Class<T> clazz, String propertyName, String propertyValue) {
		Criteria criteria = getSession().createCriteria(clazz);
		if(propertyName != null) {
			criteria.add(Restrictions.eq(propertyName, propertyValue));	
		}
		return ((Long) criteria.setProjection(Projections.rowCount()).uniqueResult()).intValue();
	}
	
	@Override
	public List<T> findByPropertyName(Class<T> clazz) {
		return findByPropertyName(clazz, null, null);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<T> findByPropertyName(Class<T> clazz, String propertyName, int propertyValue) {
		Criteria criteria = getSession().createCriteria(clazz);
		if(propertyName != null) {
			criteria.add(Restrictions.eq(propertyName, propertyValue));	
		}
		return criteria.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<T> findByPropertyName(Class<T> clazz, String propertyName, String propertyValue, Set<String> s) {
		Criteria criteria = getSession().createCriteria(clazz);
		if(propertyName != null) {
			criteria.add(Restrictions.eq(propertyName, propertyValue));
		}
		return criteria.list();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<T> findByMap(Class<T> clazz, Map<String, String> map) {
		Criteria criteria = getSession().createCriteria(clazz);
		for(String key : map.keySet()) {
			criteria.add(Restrictions.eq(key, map.get(key)));
		}
		return criteria.list();
	}

	@SuppressWarnings("unchecked")
	public List<T> findByPropertyName(Class<T> clazz, String propertyName, String propertyValue, Set<String> groupIds, String[] paramArray) {
		Criteria criteria = getSession().createCriteria(clazz);
		if(propertyName != null) {
			criteria.add(Restrictions.eq(propertyName, propertyValue));	
		}
		ProjectionList proList = Projections.projectionList();// 设置投影集合
		if(paramArray != null && paramArray.length > 0) {
			for(String param : paramArray) {
				proList.add(Projections.groupProperty(param));
			}
			criteria.setProjection(proList);
		}
		return criteria.list();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<T> findByPropertyName(Class<T> clazz, String propertyName, String propertyValue, String[] paramArray) {
		Criteria criteria = getSession().createCriteria(clazz);
		if(propertyName != null && !"".equals(propertyName)) {
			criteria.add(Restrictions.eq(propertyName, propertyValue));	
		}
		ProjectionList proList = Projections.projectionList();// 设置投影集合
		if(paramArray != null && paramArray.length > 0) {
			for(String param : paramArray) {
				proList.add(Projections.groupProperty(param));
			}
			criteria.setProjection(proList);
		}
		return criteria.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, Object>> findByQuery(String queryName, Map<String, String> paramMap) {
		Query query = getSession().getNamedQuery(queryName);
		if(paramMap != null) {
			for(String key : paramMap.keySet()) {
				query.setString(key, paramMap.get(key));
			}
		}
		query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		return query.list();
	}

	@Override
	public void createCriteria(Criteria criteria, Map<String, String> map) {
		for (String key : map.keySet()) {
			String value = map.get(key);
			if(value != null && !"".equals(value.trim())) 					
				criteria.add(Restrictions.like(key, "%" + value + "%"));
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<T> findByHql(String hql,Object... args) {
		Query query = this.getSession().createQuery(hql);
		setParameter(query, args);
		return query.list();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<T> findByHql(String hql,Map<String, Object> map) {
		Query query = this.getSession().createQuery(hql);
		for (String key : map.keySet()) {
			Object value = map.get(key);
			if (value instanceof String) {
				query.setString(key, (String) value);
			}
			if (value instanceof Boolean) {
				query.setBoolean(key,(Boolean) value);
			}
			if (value instanceof Integer) {
				query.setInteger(key, (Integer) value);
			}
			if (value instanceof Date) {
				query.setTimestamp(key, (Date) value);
			}
		}
		return query.list();
	}

	public int deleteAll(Class<T> clazz){
		this.warn(" ---- DeleteAll " + clazz);
		String hql = "delete from " + clazz.getSimpleName();
		Query query = this.getSession().createQuery(hql);
		return query.executeUpdate();
	}
	
	@Override
	public void delete(Collection<T> list) {
		if(list != null && !list.isEmpty()) {
			for (T t : list) {
				this.getSession().delete(t);
			}
		}
	}

	@Override
	public SessionBean getSessionBean() {
		//System.out.println(this.getUserToken());
		try {
			Object session = ThreadContext.get(this.getUserToken());
			//Object session = SessionRedisUtils.getValue(this.getUserToken());
			// 非iclouds或者后台任务
			if(session == null || !(session instanceof SessionBean)) {
				return new SessionBean(new User(), "NO_TOKEN");
			}
			return (SessionBean) session;
		} catch (Exception e) {
			this.exception(e, "Get SessionBean failure!");
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public PageModel<T> findForPage(Class<T> clazz, Criteria criteria, Map<String, String> order, int start, int max) {
		PageModel<T> pages = new PageModel<T>();
		List<T> list = null;
		if(max != -1) {	// -1表示不做分页
			CriteriaImpl impl = (CriteriaImpl) criteria;
			//先把Projection和OrderBy条件取出来,清空两者来执行Count操作
			Projection projection = impl.getProjection();
			Long result = (Long) criteria.setProjection(Projections.rowCount()).uniqueResult();
			int totalCount = 0;
			//执行查询
			if (result != null) {
				totalCount = result.intValue();
			}
			
			//将之前的Projection和OrderBy条件重新设回去
			criteria.setProjection(projection);
			if (projection == null) {   
				criteria.setResultTransformer(CriteriaSpecification.ROOT_ENTITY);   
			}
			criteria.setFirstResult(start * max);
			criteria.setMaxResults(max);
			pages.setTotalRecords(totalCount);
		} else {
			start = 0;	// 不做分页 开始为0
		}
		if(order != null && !order.isEmpty()) {
			for (String key : order.keySet()) {
				String by = order.get(key);
				if("asc".equals(by)) {
					criteria.addOrder(Order.asc(key));
				} else {
					criteria.addOrder(Order.desc(key));
				}
			}
		}
		try {
			list = criteria.list();	
		} catch (Exception e) {
			list = new ArrayList<T>();
			this.exception(e, "Find for page error, query type:" + clazz.getTypeName());
			e.printStackTrace();
		} finally {
			pages.setPageNo(start);
			pages.setPageSize(max);
		}
		pages.setDatas(list);
		return pages;
	}

	@Override
	public PageModel<T> findForPage(PageEntity entity) {
		return null;
	}

	@Override
	public String getSessionUserId() {
		SessionBean sessionBean = this.getSessionBean();
		String userId = ConfigProperty.SYSTEM_FLAG;
		if(sessionBean != null) {
			userId = sessionBean.getUserId();
		}
		return userId;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean checkRepeat(Class<T> clazz, Map<String, Object> map, String id) {
		List<T> list = null;
		try {
			Criteria criteria = this.getSession().createCriteria(clazz);
			for (String key : map.keySet()) {
				criteria.add(Restrictions.eq(key, StrUtils.tranString(map.get(key))));
			}
			if(StrUtils.checkParam(id)) {
				criteria.add(Restrictions.ne("id", id));
			}
			list = criteria.list();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return (list != null && list.size() > 0) ? false : true;
	}

	@Override
	public boolean checkRepeat(Class<T> clazz, Map<String, Object> map) {
		return this.checkRepeat(clazz, map, null);
	}

	@Override
	public boolean checkRepeat(Class<T> clazz, String key, String value) {
		return this.checkRepeat(clazz, key, value, null);
	}

	@Override
	public boolean checkRepeat(Class<T> clazz, String key, String value, String id) {
		Map<String, Object> map = new HashMap<String, Object>();
		if(value != null && !"".equals(value)) {
			map.put(key, value);
			return this.checkRepeat(clazz, map, id);
		} else {
			return false;
		}
	}

	/**
	 * 获取当前用户登录名
	 * 
	 * @return
	 */
	public String getProjectId() {
		SessionBean sessionBean = getSessionBean();
		if (sessionBean != null) {
			return sessionBean.getProjectId();
		}
		return null;
	}

	@Override
	public T singleByClass(Class<T> clazz, Map<String, Object> params) {
		List<T> list = this.listByClass(clazz, params);
		if(list == null || list.isEmpty()) {
			return null;
		}
		if(list.size() > 1) {
			StringBuffer buffer = new StringBuffer();
			buffer.append("Query single object error. ");
			buffer.append("return more than one record. ");
			buffer.append("query parameter: [" + StrUtils.toJSONString(params) + "]. ");
			buffer.append("query result: [" + StrUtils.toJSONString(list) + "].");
			this.warn("Query single entity error: " + buffer.toString());
		}
		return  list.get(0);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public int count(Class<T> clazz, Map<String, Object> params) {
		Criteria criteria = getSession().createCriteria(clazz);
		if (params != null && params.size() > 0) {
			for (String key : params.keySet()) {
				Object obj = params.get(key);
				if (obj instanceof Collection || obj.getClass().isArray())
					criteria.add(Restrictions.in(key, (Collection) obj));
				else if(obj instanceof String && obj.toString().contains("%"))
					criteria.add(Restrictions.like(key, obj));
				else 
					criteria.add(Restrictions.eq(key, obj));
			}
		}
		
		//先把Projection和OrderBy条件取出来,清空两者来执行Count操作
		Long result = null;
		try {
			result = (Long) criteria.setProjection(Projections.rowCount()).uniqueResult();	
		} catch (Exception e) {
			this.exception(e, "Query count error, type:" + clazz.getTypeName());
		}
		return result == null ? 0 : result.intValue();
	}

	@Override
	public String getTableNameByEntity(Class<T> clazz) {
		String tableName = null;
		try {
			AbstractEntityPersister meta = (AbstractEntityPersister) sessionFactory.getClassMetadata(clazz);
			tableName = meta.getTableName();	
		} catch (Exception e) {
			this.exception(e, "Get table name failure, clazz: " + clazz.getName());
		}
		return tableName;
	}
	
	@Override
	public void addOrder (String asSorting, String columnName, String defaultColumnName, Map<String, String> order) {
		if (StrUtils.checkParam(asSorting, columnName)) {
			if (asSorting.equals("asc")) {
				order.put(columnName.toString(), "asc");
			} else {
				order.put(columnName.toString(), "desc");
			}
		} else {
			order.put(defaultColumnName, "desc");
		}
	}
}

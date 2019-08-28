package com.h3c.iclouds.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.h3c.iclouds.auth.CacheSingleton;
import com.h3c.iclouds.auth.SessionBean;
import com.h3c.iclouds.biz.OperateLogsBiz;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.exception.MessageException;
import com.h3c.iclouds.utils.LogUtils;
import com.h3c.iclouds.utils.StrUtils;
import com.h3c.iclouds.utils.ThreadContext;

/**
 * 业务逻辑层基类实现
 * 
 * @author Administrator
 *
 * @param <T>
 */
public abstract class BaseBizImpl<T extends java.io.Serializable> implements BaseBiz<T> {

	public void flush() {
		baseDAO.getSession().flush();
	}
	
	/**
	 * 数据层接口对象
	 */
	@Resource
	protected BaseDAO<T> baseDAO;
	
	@Resource
	public OperateLogsBiz operateLogsBiz;

	@Resource
	public HttpServletRequest request;

	public Logger log = LoggerFactory.getLogger(this.getClass());
	
	public CacheSingleton singleton = CacheSingleton.getInstance();
	
	/**
	 * 需要记录的信息日志
	 * @param clazz
	 * @param obj
	 */
	public void info(Object obj) {
		log.info(LogUtils.print(obj));
	}
	
	/**
	 * 异常内容日志打印
	 * @param clazz
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
	 * @param clazz
	 * @param e
	 */
	public void warn(Object obj) {
		log.warn(LogUtils.print(obj));
	}
	
	/**
	 * persist
	 */
	@Override
	public void persist(T obj) {
		this.baseDAO.persist(obj);
	}

	/**
	 * 新增
	 */
	@Override
	public String add(T obj) {
		return this.baseDAO.add(obj);
	}

	/**
	 * 更新
	 */
	@Override
	public void update(T obj) {
		this.baseDAO.update(obj);
	}
	@Override
	public int updateByHql(String hql, Object[] args) {
		return baseDAO.updateByHql(hql, args);
	}
	public void addOrUpdate(T obj) {
		this.baseDAO.addOrUpdate(obj);
	}

	/**
	 * 更新
	 * 
	 * @param updateWhere
	 *            更新条件
	 * @param updateMap
	 *            更新值
	 * @param clazz
	 *            更新对象
	 * @return
	 */
	public int update(Map<String, Object> updateWhere, Map<String, Object> updateMap, Class<T> clazz) {
		return this.baseDAO.update(updateWhere, updateMap, clazz);
	}

	/**
	 * 删除
	 */
	@Override
	public void delete(T obj) {
		this.baseDAO.delete(obj);
	}

	@Override
	public int delete(Map<String, Object> deleteWhere, Class<T> clazz) {
		return this.baseDAO.delete(deleteWhere, clazz);
	}

	@Override
	public void deleteById(Class<T> clazz, String objId) {
		this.baseDAO.deleteById(clazz, objId);
	}

	/**
	 * 根据主键查找
	 */
	@Override
	public T findById(Class<T> clazz, String objId) {
		return this.baseDAO.findById(clazz, objId);
	}

	/**
	 * 根据主键查找
	 */
	@Override
	public T findById(Class<T> clazz, int objId) {
		return this.baseDAO.findById(clazz, objId);
	}

	@Override
	public T findById(Class<T> clazz, String objId, boolean clearSession) {
		return this.baseDAO.findById(clazz, objId, clearSession);
	}

	@Override
	public T load(Class<T> clazz, String objId) {
		return this.baseDAO.load(clazz, objId);
	}

	/**
	 * 查找所有
	 * 
	 * @param clazz
	 * @return
	 */
	public List<T> getAll(Class<T> clazz) {
		return this.baseDAO.getAll(clazz);
	}

	public List<T> findByPropertyName(Class<T> clazz, String propertyName, String propertyValue) {
		return this.baseDAO.findByPropertyName(clazz, propertyName, propertyValue);
	}

	public List<T> findByPropertyName(Class<T> clazz, String propertyName, int propertyValue) {
		return this.baseDAO.findByPropertyName(clazz, propertyName, propertyValue);
	}

	public List<T> findByPropertyName(Class<T> clazz) {
		return this.baseDAO.findByPropertyName(clazz, null, null);
	}

	@Override
	public int findCountByPropertyName(Class<T> clazz, String propertyName, String propertyValue) {
		return this.baseDAO.findCountByPropertyName(clazz, propertyName, propertyValue);
	}

	@Override
	public List<T> findByPropertyName(Class<T> clazz, Set<String> groupIds) {
		return this.baseDAO.findByPropertyName(clazz, null, null, groupIds);
	}

	@Override
	public List<T> findByPropertyName(Class<T> clazz, String propertyName, String propertyValue, Set<String> groupIds) {
		return this.baseDAO.findByPropertyName(clazz, propertyName, propertyValue, groupIds);
	}

	public List<T> findByPropertyName(Class<T> clazz, String propertyName, String propertyValue, String[] paramArray) {
		return this.baseDAO.findByPropertyName(clazz, propertyName, propertyValue, paramArray);
	}

	public List<T> findByMap(Class<T> clazz, Map<String, String> map) {
		return this.baseDAO.findByMap(clazz, map);
	}

	public List<Map<String, Object>> findByQuery(String queryName, Map<String, String> paramMap) {
		return this.baseDAO.findByQuery(queryName, paramMap);
	}

	public List<T> findByClazz(Class<T> clazz, String... params) {
		StringBuffer sBuffer = new StringBuffer("SELECT new " + clazz.getSimpleName() + " (");
		for (int i = 0; i < params.length; i++) {
			sBuffer.append("mb." + params[i]);
			if (i != params.length - 1) {
				sBuffer.append(", ");
			}
		}
		sBuffer.append(") FROM " + clazz.getSimpleName());
		sBuffer.append(" as mb");
		return this.baseDAO.findByHql(sBuffer.toString());
	}

	@Override
	public List<T> findByClazz(Class<T> clazz, Map<String, String> queryMap, String... params) {
		StringBuffer sBuffer = new StringBuffer("SELECT new " + clazz.getSimpleName() + " (");
		for (int i = 0; i < params.length; i++) {
			sBuffer.append("mb." + params[i]);
			if (i != params.length - 1) {
				sBuffer.append(", ");
			}
		}
		sBuffer.append(") FROM " + clazz.getSimpleName());
		sBuffer.append(" as mb ");

		if (queryMap != null && !queryMap.isEmpty()) {
			int i = 0;
			for (String key : queryMap.keySet()) {
				if (i == 0) {
					sBuffer.append(" WHERE ");
					i = -1;
				} else {
					sBuffer.append(" AND ");
				}
				sBuffer.append(key + " = '" + queryMap.get(key) + "'");
			}
		}
		try {
			return this.baseDAO.findByHql(sBuffer.toString());
		} catch (Exception e) {
			this.exception(e, "Query hql error:" + sBuffer.toString());
		}
		return new ArrayList<T>();
	}

	public List<T> findByClazz(Class<T> clazz, String[] order, String... params) {
		StringBuffer sBuffer = new StringBuffer("SELECT new " + clazz.getSimpleName() + " (");
		for (int i = 0; i < params.length; i++) {
			sBuffer.append(params[i]);
			if (i != params.length - 1) {
				sBuffer.append(", ");
			}
		}
		sBuffer.append(") FROM " + clazz.getSimpleName());
		if (order != null) {
			sBuffer.append(" ORDER BY " + order[0] + " " + order[1]);
		}
		try {
			return this.baseDAO.findByHql(sBuffer.toString());
		} catch (Exception e) {
			this.exception(e, "Query hql error:" + sBuffer.toString());
		}
		return new ArrayList<T>();
	}

	@Override
	public SessionBean getSessionBean() {
		return this.baseDAO.getSessionBean();
	}

	@Override
	public List<T> listByClass(Class<T> clazz, Map<String, Object> params) {
		return this.baseDAO.listByClass(clazz, params);
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean checkRepeat(Class<T> clazz, Map<String, Object> map, String id) {
		List<T> list = null;
		try {
			Criteria criteria = baseDAO.getSession().createCriteria(clazz);
			for (String key : map.keySet()) {
				criteria.add(Restrictions.eq(key, map.get(key)));
			}
			if (StrUtils.checkParam(id)) {
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
		if (value != null && !"".equals(value)) {
			map.put(key, value);
			return this.checkRepeat(clazz, map, id);
		} else {
			return false;
		}
	}

	@Override
	public PageModel<T> findForPage(PageEntity entity) {
		return this.baseDAO.findForPage(entity);
	}

	/**
	 * 获取当前用户id
	 * 
	 * @return
	 */
	@Override
	public String getLoginUser() {
		SessionBean sessionBean = getSessionBean();
		if (sessionBean != null) {
			String userId = sessionBean.getUserId();
			// junit test
			if (userId == null) {
				return "junitTest";
			}
			return sessionBean.getUserId();
		}
		// return null;
		return "junitTest";
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
	public void evict(T t) {
		this.baseDAO.getSession().evict(t);
	}

	@Override
	public String getSessionUserId() {
		return baseDAO.getSessionUserId();
	}
	
	@Override
	public String getUserToken() {
		return this.baseDAO.getUserToken();
	}
	
	/**
	 * 记录数据，用于删除回滚
	 * @param key
	 * @param value
	 * @return
	 */
	public void setCloudosMapLocal(String key, String value) {
		@SuppressWarnings("unchecked")
		Map<String, Object> cloudosMap = (Map<String, Object>) ThreadContext.get(ConfigProperty.CLOUDOS_COLLBACK_MAP_KEY);
		if(cloudosMap == null) {
			cloudosMap = new HashMap<String, Object>();
			ThreadContext.set(ConfigProperty.CLOUDOS_COLLBACK_MAP_KEY, cloudosMap);
		}
		cloudosMap.put(key, value);
	}

	/**
	 * 记录数据，用于删除回滚
	 * @param header
	 * @return
	 */
	public void setCloudosMapLocal(Map<String, Object> cloudosMap) {
		ThreadContext.set(ConfigProperty.CLOUDOS_COLLBACK_MAP_KEY, cloudosMap);
	}

	/**
	 * 获取记录的数据
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> getCloudosMapLocal() {
		return (Map<String, Object>) ThreadContext.get(ConfigProperty.CLOUDOS_COLLBACK_MAP_KEY);
	}
	
	@Override
	public T singleByClass(Class<T> clazz, Map<String, Object> params) {
		return this.baseDAO.singleByClass(clazz, params);
	}
	
	@Override
	public int count(Class<T> clazz, Map<String, Object> params) {
		return this.baseDAO.count(clazz, params);
	}

	@Override
	public List<T> findByHql(String hql, Object... args) {
		return baseDAO.findByHql(hql, args);
	}

	@Override
	public List<T> listBySql(String sql, Object[] args, Map<String, Object> alias, Class<T> clz, boolean hasEntiry) {
		return baseDAO.listBySql(sql, args, alias, clz, hasEntiry);
	}
	
	@Override
	public String getTableNameByEntity(Class<T> clazz) {
		return this.baseDAO.getTableNameByEntity(clazz);
	}
}
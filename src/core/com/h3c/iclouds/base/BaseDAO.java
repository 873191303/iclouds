package com.h3c.iclouds.base;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.Session;

import com.h3c.iclouds.auth.SessionBean;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageModel;

/**
 * 数据访问层基類藉口
 * 
 * @author Administrator
 * 
 * @param <T>
 */
public interface BaseDAO<T extends java.io.Serializable> {

	/**
	 * 新增
	 */
	String add(T obj);

	void addOrUpdate(T obj);

	/**
	 * 更新
	 */
	void update(T obj);

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
	int update(Map<String, Object> updateWhere, Map<String, Object> updateMap, Class<T> clazz);

	/**
	 * 删除
	 */
	void delete(T obj);

	int delete(Map<String, Object> deleteWhere, Class<T> clazz);

	/*
	 * 根据主键删除
	 */
	void deleteById(Class<T> clazz, String objId);

	void deleteById(Class<T> clazz, int objId);

	/**
	 * 根据主键查找
	 */
	T findById(Class<T> clazz, String objId);

	/**
	 * 根据主键查找
	 */
	T findById(Class<T> clazz, int objId);

	T findById(Class<T> clazz, String objId, boolean clearSession);

	/**
	 * 查找所有
	 * 
	 * @param clazz
	 * @return
	 */
	List<T> getAll(Class<T> clazz);

	void persist(T obj);

	T load(Class<T> clazz, String objId);

	List<T> listByClass(Class<T> clazz, Map<String, Object> params);

	/**
	 * 利用离线查询进行分页查询
	 * 
	 * @param clazz
	 *            查询对象
	 * @param criteria
	 *            查询条件
	 * @return
	 */
	PageModel<T> findForPage(Class<T> clazz, Criteria criteria);

	/**
	 * 利用离线查询进行分页查询
	 * 
	 * @param clazz
	 *            查询对象
	 * @param criteria
	 *            查询条件
	 * @return
	 */
	PageModel<T> findForPage(Class<T> clazz, Criteria criteria, Map<String, String> order, int start, int max);

	/**
	 * 根据属性名称查询列表
	 * 
	 * @param clazz
	 * @param propertyName
	 *            为空则不做条件查询
	 * @param propertyValue
	 * @return
	 */
	List<T> findByPropertyName(Class<T> clazz, String propertyName, String propertyValue);
	
	/**
	 * 根据属性名称查询列表
	 * 
	 * @param clazz
	 * @param propertyName
	 *            为空则不做条件查询
	 * @param propertyValue
	 * @return
	 */
	int findCountByPropertyName(Class<T> clazz, String propertyName, String propertyValue);

	/**
	 * 根据属性名称查询列表
	 * 
	 * @param clazz
	 * @return
	 */
	List<T> findByPropertyName(Class<T> clazz);

	/**
	 * 根据属性名称查询列表
	 * 
	 * @param clazz
	 * @param propertyName
	 *            为空则不做条件查询
	 * @param propertyValue
	 * @return
	 */
	List<T> findByPropertyName(Class<T> clazz, String propertyName, int propertyValue);

	/**
	 * 按属性名称查询，含群组过滤
	 * 
	 * @param clazz
	 * @param propertyName
	 * @param propertyValue
	 * @param groupIds
	 * @return
	 */
	List<T> findByPropertyName(Class<T> clazz, String propertyName, String propertyValue, Set<String> groupIds);

	/**
	 * 根据条件查询，只查询规定字段
	 * 
	 * @param clazz
	 * @param propertyName
	 * @param propertyValue
	 * @param paramArray
	 * @return
	 */
	List<T> findByPropertyName(Class<T> clazz, String propertyName, String propertyValue, String[] paramArray);

	List<T> findByPropertyName(Class<T> clazz, String propertyName, String propertyValue, Set<String> groupIds,
			String[] paramArray);

	/**
	 * 获取hibernate的session
	 * 
	 * @return
	 */
	Session getSession();

	/**
	 * 按多组属性名称查询
	 * 
	 * @param clazz
	 * @return
	 */
	List<T> findByMap(Class<T> clazz, Map<String, String> map);

	/**
	 * 查询条件都为String则可以调用
	 * 
	 * @param queryName
	 * @param paramMap
	 * @return
	 */
	List<Map<String, Object>> findByQuery(String queryName, Map<String, String> paramMap);

	/**
	 * 创建and条件查询
	 * 
	 * @param criteria
	 * @param map
	 * @return
	 */
	void createCriteria(Criteria criteria, Map<String, String> map);

	/**
	 * 根据HQL语句查询列表
	 * 
	 * @param hql
	 * @return
	 */
	List<T> findByHql(String hql,Object... args);

	/**
	 * 删除所有
	 * 
	 * @param clazz
	 * @return
	 */
	int deleteAll(Class<T> clazz);

	void delete(Collection<T> list);

	/**
	 * 获当前用户会话
	 * @return
	 */
	SessionBean getSessionBean();
	
	/**
	 * 获取当前用户id
	 * @return
	 */
	String getSessionUserId();
	
	/**
	 * 分页查询
	 *
	 * @param entity
	 * @return
	 */
	PageModel<T> findForPage(PageEntity entity);

	/**
	 * 查询单个实体
	 *
	 * @param hql
	 * @return
	 */
	Object queryObject(String hql);

	Object queryObject(String hql, Object[] arg);

	Object queryObject(String hql, Object arg);

	/**
	 * 验证是否重复
	 *
	 * @param clazz
	 * @return
	 */
	boolean checkRepeat(Class<T> clazz, String key, String value, String id);

	/**
	 * 验证是否重复
	 *
	 * @param clazz
	 * @return
	 */
	boolean checkRepeat(Class<T> clazz, String key, String value);

	/**
	 * 验证是否重复
	 *
	 * @return
	 */
	boolean checkRepeat(Class<T> clazz, Map<String, Object> map);

	/**
	 * 验证是否重复
	 *
	 * @return
	 */
	boolean checkRepeat(Class<T> clazz, Map<String, Object> map, String id);

	int updateBySql(String sql, Object[] args, Map<String, Object> alias);
	
	int updateByHql(String sql, Object[] args, Map<String, Object> alias);

	int updateByHql(String hql);

	int updateByHql(String hql, Object args);
	
	int updateByHql(String hql, Object[] args);
	
	/**
	 * 获取当前用户信息
	 * @return
	 */
	String getUserToken();

	List<T> findByHql(String hql, Map<String, Object> map);

	T singleByClass(Class<T> clazz, Map<String, Object> params);
	
	/**
	 * 数量查询
	 * @param clazz
	 * @param params	value为集合则为in查询，value为String，且包含%则为like查询
	 * @return
	 */
	int count(Class<T> clazz, Map<String, Object> params);

	List<T> listBySql(String sql, Object[] args, Map<String, Object> alias, Class<T> clz, boolean hasEntiry);
	
	String getTableNameByEntity(Class<T> clazz);

	void addOrder(String asSorting, String columnName, String defaultColumnName, Map<String, String> order);
}
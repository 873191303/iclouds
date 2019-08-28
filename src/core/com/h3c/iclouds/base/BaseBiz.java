package com.h3c.iclouds.base;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.h3c.iclouds.auth.SessionBean;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageModel;

/**
 * 业务逻辑层基类接口
 * 
 * @author Administrator
 *
 * @param <T>
 */
public interface BaseBiz<T extends java.io.Serializable> {

	void flush();

	/**
	 * 新增
	 * 
	 * @param obj
	 */
	String add(T obj);

	/**
	 * 更新
	 * 
	 * @param obj
	 */
	void update(T obj);

	void addOrUpdate(T obj);

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
	 * 
	 * @param obj
	 */
	void delete(T obj);

	int delete(Map<String, Object> deleteWhere, Class<T> clazz);

	/*
	 * 根据主键删除
	 */
	void deleteById(Class<T> clazz, String objId);

	/**
	 * 根据主键查找
	 * 
	 * @param clazz
	 * @param objId
	 * @return
	 */
	T findById(Class<T> clazz, String objId);

	/**
	 * 按照主键查询
	 * 
	 * @param clazz
	 * @param objId
	 * @return
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

	/**
	 * 根据属性名称查询列表
	 * 
	 * @param clazz
	 * @param propertyName
	 * @param propertyValue
	 * @return
	 */
	List<T> findByPropertyName(Class<T> clazz, String propertyName, String propertyValue);

	List<T> findByPropertyName(Class<T> clazz, String propertyName, int propertyValue);

	List<T> findByPropertyName(Class<T> clazz);
	
	/**
	 * 根据属性名称查询列表
	 * 
	 * @param clazz
	 * @param propertyName
	 * @param propertyValue
	 * @return
	 */
	int findCountByPropertyName(Class<T> clazz, String propertyName, String propertyValue);

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

	/**
	 * 查询群组过滤的内容
	 * 
	 * @param clazz
	 * @param groupIds
	 * @return
	 */
	List<T> findByPropertyName(Class<T> clazz, Set<String> groupIds);

	/**
	 * 按属性名称查询，含群组过滤,群组的属性名必须为groupId
	 * 
	 * @param clazz
	 * @param propertyName
	 * @param propertyValue
	 * @param groupIds
	 * @return
	 */
	List<T> findByPropertyName(Class<T> clazz, String propertyName, String propertyValue, Set<String> groupIds);

	/**
	 * 按属性名称查询
	 * 
	 * @param clazz
	 * @param propertyName
	 * @param propertyValue
	 * @param groupIds
	 * @return
	 */
	List<T> findByMap(Class<T> clazz, Map<String, String> map);

	List<T> findByHql(String hql,Object... args);

	List<T> listBySql(String sql, Object[] args, Map<String, Object> alias, Class<T> clz, boolean hasEntiry);

	/**
	 * 查询条件都为String则可以调用
	 * 
	 * @param queryName
	 * @param paramMap
	 * @return
	 */
	List<Map<String, Object>> findByQuery(String queryName, Map<String, String> paramMap);

	/**
	 * 
	 * @param clazz
	 * @param params
	 * @return
	 */
	List<T> findByClazz(Class<T> clazz, String... params);
	
	/**
	 * 
	 * @param clazz
	 * @param params
	 * @return
	 */
	List<T> findByClazz(Class<T> clazz, Map<String, String> queryMap, String... params);

	/**
	 * 
	 * @param clazz
	 * @param params
	 * @return
	 */
	List<T> findByClazz(Class<T> clazz, String[] order, String... params);

	SessionBean getSessionBean();

	List<T> listByClass(Class<T> clazz, Map<String, Object> params);

	/**
	 * 验证是否重复
	 * 
	 * @param clazz
	 * @param params
	 * @return
	 */
	boolean checkRepeat(Class<T> clazz, String key, String value, String id);

	/**
	 * 验证是否重复
	 * 
	 * @param clazz
	 * @param params
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
	
	/**
	 * 分页查询
	 * @param entity
	 * @return
	 */
	PageModel<T> findForPage(PageEntity entity);
	
	void evict(T t);
	
	/**
	 * 获取当前用户id
	 * @return
	 */
	String getSessionUserId();

	/**
	 * 获取当前用户信息
	 * @return
	 */
	String getUserToken();

	int updateByHql(String hql, Object[] args);
	
	T singleByClass(Class<T> clazz, Map<String, Object> params);

	Map<String, Object> getCloudosMapLocal();

	String getLoginUser();

	/**
	 * 数量查询
	 * @param clazz
	 * @param params	value为集合则为in查询，value为String，且包含%则为like查询
	 * @return
	 */
	int count(Class<T> clazz, Map<String, Object> params);
	
	String getTableNameByEntity(Class<T> clazz);
	
}

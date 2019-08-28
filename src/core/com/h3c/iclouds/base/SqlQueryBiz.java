package com.h3c.iclouds.base;

import java.util.List;
import java.util.Map;

public interface SqlQueryBiz extends BaseBiz<String> {

	/**
	 * 根据名称查询sql
	 * @param sqlName
	 * @return
	 */
	List<Map<String, Object>> queryByName(String sqlName, Map<String, Object> map);
	
	/**
	 * 根据名称查询sql,查询单列数据
	 * @param sqlName
	 * @return
	 */
	Map<String, Object> querySingleByName(String sqlName, Map<String, Object> map);
	
	/**
	 * 根据sql查询
	 * @param sql
	 * @return
	 */
	List<Map<String, Object>> queryBySql(String sql);
	
	/**
	 * 根据名称查询sql count
	 * @param sqlName
	 * @return
	 */
	Integer queryByNameForCount(String sqlName, Map<String, Object> map);

	List<Map<String, Object>> queryPageByName(String sqlName, Map<String, Object> map, Integer offset, Integer size);

	List<Map<String, Object>> queryBySqlLike(String sqlName, String text);
	
	int excuteSql(String sql);
	
}

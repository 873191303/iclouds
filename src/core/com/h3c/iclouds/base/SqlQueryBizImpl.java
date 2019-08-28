package com.h3c.iclouds.base;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Service;

import com.h3c.iclouds.utils.StrUtils;

@Service("sqlQueryBiz")
public class SqlQueryBizImpl extends BaseBizImpl<String> implements SqlQueryBiz {
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public List<Map<String, Object>> queryByName(String sqlName, Map<String, Object> map) {
		Query query = baseDAO.getSession().getNamedQuery(sqlName);
		if(map != null && !map.isEmpty()) {
			for(String key : map.keySet()) {
				Object value = map.get(key);
				if(value instanceof Integer) {
					query.setInteger(key, StrUtils.tranInteger(value));
				} else if(value instanceof String) {
					query.setString(key, StrUtils.tranString(value));
				} else if(value instanceof Long) {
					query.setLong(key, StrUtils.tranLong(value));
				} else if(value instanceof Long) {
					query.setDouble(key, StrUtils.tranDouble(value));
				} else if(value instanceof List) {
					query.setParameterList(key, (List)value);
				} else if(value instanceof String[]) {
					query.setParameterList(key, (String[])value);
				} else if(value instanceof Integer[]) {
					query.setParameterList(key, (Integer[])value);
				} else if(value instanceof Set) {
					query.setParameterList(key, (Set)value);
				} else {
					query.setParameter(key, value);
				}
			}
		}
		query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		return query.list();
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public List<Map<String, Object>> queryPageByName(String sqlName, Map<String, Object> map,Integer offset,Integer size) {
		Query query = baseDAO.getSession().getNamedQuery(sqlName);
		if(map != null && !map.isEmpty()) {
			for(String key : map.keySet()) {
				Object value = map.get(key);
				if(value instanceof Integer) {
					query.setInteger(key, StrUtils.tranInteger(value));
				} else if(value instanceof String) {
					query.setString(key, StrUtils.tranString(value));
				} else if(value instanceof Long) {
					query.setLong(key, StrUtils.tranLong(value));
				} else if(value instanceof Long) {
					query.setDouble(key, StrUtils.tranDouble(value));
				} else if(value instanceof List) {
					query.setParameterList(key, (List)value);
				} else if(value instanceof String[]) {
					query.setParameterList(key, (String[])value);
				} else if(value instanceof Integer[]) {
					query.setParameterList(key, (Integer[])value);
				} else if(value instanceof Set) {
					query.setParameterList(key, (Set)value);
				}
			}
		}
		if (null!=offset) {
			query.setFirstResult(offset*size);
		}
		if (null!=size) {
			query.setMaxResults(size);
		}
		query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		return query.list();
	}

	@Override
	public Map<String, Object> querySingleByName(String sqlName, Map<String, Object> map) {
		List<Map<String, Object>> list = this.queryByName(sqlName, map);
		if(list != null && !list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, Object>> queryBySql(String sql) {
		SQLQuery query = baseDAO.getSession().createSQLQuery(sql);
		query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		return query.list();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, Object>> queryBySqlLike(String sqlName,String text) {
		Query query = baseDAO.getSession().getNamedQuery(sqlName);
		query.setParameter("text", "%"+text+"%");
		this.info(query.getQueryString());
		query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		return query.list();
	}

	@Override
	public Integer queryByNameForCount(String sqlName, Map<String, Object> map) {
		Map<String, Object> result = this.querySingleByName(sqlName, map);
		return StrUtils.tranInteger(result.get("ct"));
	}

	@Override
	public int excuteSql(String sql) {
		Query query = null;
		try {
			query = this.baseDAO.getSession().getNamedQuery(sql);
		} catch (Exception e) {
		}
		if(query == null) {
			query = this.baseDAO.getSession().createSQLQuery(sql);	
		}
		return query.executeUpdate();
	}

}

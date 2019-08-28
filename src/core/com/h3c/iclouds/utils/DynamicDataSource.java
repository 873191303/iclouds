package com.h3c.iclouds.utils;

import java.beans.PropertyVetoException;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import com.zaxxer.hikari.HikariDataSource;

/**
 * 动态获取数据源
 * 
 * @author j05542
 *
 * @param <T>
 */
public class DynamicDataSource extends AbstractRoutingDataSource {
	
	private Map<Object, Object> _targetDataSources;
	
	public static final String DEFAULT_DS_NAME = "dataSource";

	@Override
	protected Object determineCurrentLookupKey() {
		String dataSourceName = DbContextHolder.getDbType();
		String dbname = DbContextHolder.getDbName();
		if (StringUtils.isEmpty(dataSourceName)) {
			dataSourceName = DEFAULT_DS_NAME;
		} else {
			this.selectDataSource(dataSourceName, dbname);
		}
		return dataSourceName;
	}

	public void setTargetDataSources(Map<Object, Object> targetDataSources) {
		this._targetDataSources = targetDataSources;
		super.setTargetDataSources(this._targetDataSources);
		afterPropertiesSet();
	}

	public void addTargetDataSource(String key, HikariDataSource dataSource) {
		this._targetDataSources.put(key, dataSource);
		this.setTargetDataSources(this._targetDataSources);
	}

	public HikariDataSource createDataSource(HikariDataSource dataSource) throws PropertyVetoException {
		dataSource.setDriverClassName("com.mysql.jdbc.Driver");
		// String url = "jdbc:mysql://" + cloud.getDbhost() + ":" +
		// cloud.getDbport() + "/" + cloud.getDbinstance() +
		// "?useUnicode=true&amp;characterEncoding=UTF-8&amp;autoReconnect=true";
		// dataSource.setJdbcUrl(url);
		// dataSource.setUser(cloud.getDbuser());
		// dataSource.setPassword(cloud.getDbpasswd());
		// dataSource.setMinPoolSize(5);
		// dataSource.setMaxPoolSize(20);
		return dataSource;
	}

	/**
	 * @param serverId
	 * @describe 数据源存在时不做处理，不存在时创建新的数据源链接，并将新数据链接添加至缓存
	 */
	public void selectDataSource(String serverId, String dbname) {
		Object sid = DbContextHolder.getDbType();
		// 如果传递的数据源名称是缺省名称,设置当前的数据源为缺省的数据源
		if (DEFAULT_DS_NAME.equals(serverId)) {
			DbContextHolder.setDbType(DEFAULT_DS_NAME);
			return;
		}
		Object obj = this._targetDataSources.get(serverId);
		if (obj != null && sid.equals(serverId)) {
			return;
		} else {
			HikariDataSource dataSource = this.getDataSource(serverId, dbname);
			if (null != dataSource)
				this.setDataSource(serverId, dataSource);
		}
	}

	/**
	 * @param serverId
	 * @param dataSource
	 */
	public void setDataSource(String serverId, HikariDataSource t) {
		this.addTargetDataSource(serverId, t);
		DbContextHolder.setDbType(serverId, DbContextHolder.getDbName());
	}

	/**
	 * @describe 查询serverId对应的数据源记录
	 * @param serverId
	 * @return
	 */
	public HikariDataSource getDataSource(String serverId, String dbname) {
		//
		// CmdbOrgClouds cloud =
		// CacheSingleton.getInstance().getClouds().get(serverId);
		// if(cloud!=null){
		// HikariDataSource dataSource= new HikariDataSource();
		// try {
		// dataSource = this.createDataSource(dataSource, cloud);
		// } catch (Exception e)
		// {
		// e.printStackTrace();
		// }
		// return dataSource;
		// }
		return null;
	}

}

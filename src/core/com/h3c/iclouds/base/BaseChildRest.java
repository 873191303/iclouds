package com.h3c.iclouds.base;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.IOException;

public abstract class BaseChildRest<T extends java.io.Serializable> extends BaseRestControl {
	
	/**
	 * 获取列表
	 * @return
	 */
	public abstract Object list(@PathVariable String pid);

	/**
	 * 获取实体
	 * @param id
	 * @return
	 */
	public abstract Object get(@PathVariable String pid, @PathVariable String id);
	
	/**
	 * 删除
	 * @param id
	 * @return
	 */
	public abstract Object delete(@PathVariable String pid, @PathVariable String id);
	
	/**
	 * 添加
	 * @param t
	 * @return
	 */
	public abstract Object save(@PathVariable String pid, @RequestBody T entity);
	
	/**
	 * 修改
	 * @param t
	 * @return
	 * @throws IOException 
	 */
	public abstract Object update(@PathVariable String pid, @PathVariable String id, @RequestBody T entity) throws IOException;
	
}

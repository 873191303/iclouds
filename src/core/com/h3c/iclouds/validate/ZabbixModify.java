package com.h3c.iclouds.validate;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/***
 * 调用zabbixAPi需要加上该注解，用于在拦截器进行拦截
 * 
 * @author j05542
 *
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface ZabbixModify {

	String[] value () default {};
	
}

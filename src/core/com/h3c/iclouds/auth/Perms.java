package com.h3c.iclouds.auth;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;  
import java.lang.annotation.RetentionPolicy; 
import java.lang.annotation.Target;

/***
 * 权限注解
 * @author j05542
 *
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.ANNOTATION_TYPE,ElementType.METHOD})
@Inherited
public @interface Perms {
	String[] value() default {};
}
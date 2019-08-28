package com.h3c.iclouds.auth;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/***
 * 权限注解
 * 
 * @author j05542
 *
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface ThirdPart {

	ThirdPartEnum[] value() default {};
	
}

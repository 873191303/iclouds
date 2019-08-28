package com.h3c.iclouds.utils;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.h3c.iclouds.validate.PatternType;

/***
 * 类对比替换注解，不允许前台传参数对后台修改
 * 
 * @author zkf5485
 *
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.ANNOTATION_TYPE, ElementType.METHOD })
@Inherited
public @interface InvokeAnnotate {

	public PatternType[] type();

	public String[] values() default {};
}
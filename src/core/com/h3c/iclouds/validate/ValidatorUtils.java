package com.h3c.iclouds.validate;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import com.h3c.iclouds.utils.LogUtils;

public class ValidatorUtils {
	
	private static Validator validator;
	
	/**
	 * 初始化验证
	 */
	static {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
	}

	/**
	 * 验证实体内容是否正确
	 * @param obj
	 * @return
	 */
	public static Map<String, String> validator(Object obj) {
		return ValidatorUtils.validator(obj, null);
	}
	
	/**
	 * 验证实体内容是否正确
	 * @param obj
	 * @param map	错误提示内容
	 * @return
	 */
	public static Map<String, String> validator(Object obj, Map<String, String> map) {
		if(map == null) {
			map = new HashMap<String, String>();
		}
		Set<ConstraintViolation<Object>> constraintViolations = validator.validate(obj);
		if(!constraintViolations.isEmpty()) {
			for(ConstraintViolation<Object> con : constraintViolations) {
				map.put(con.getPropertyPath().toString(), con.getMessage());	
			}
		}
		if(!map.isEmpty()) {
			LogUtils.info(obj.getClass(), map);
		}
		return map;
	}
	
}

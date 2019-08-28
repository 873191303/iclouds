package com.h3c.iclouds.utils;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.beanutils.Converter;

@SuppressWarnings("rawtypes")
public class DateConverter implements Converter {
	
	public Object convert(Class type, Object value) {
		if (value == null) {
			return null;
		} else if (type == Timestamp.class) {
			return convertToDate(type, value, "yyyy-MM-dd HH:mm:ss");
		} else if (type == Date.class) {
			return convertToDate(type, value, "yyyy-MM-dd");
		} else if (type == String.class) {
			return convertToString(type, value);
		}
		throw new ConversionException("不能转换 " + value.getClass().getName() + " 为 " + type.getName());
	}

	protected Object convertToDate(Class type, Object value, String pattern) {
		if (value instanceof String) {
			try {
				String val = StrUtils.tranString(value);
				if (!StrUtils.isNotEmpty(val)) {
					return null;
				}
				Date date = DateUtils.getDateByString(val, pattern);
				
				if (type.equals(Timestamp.class)) {
					return new Timestamp(date.getTime());
				}
				return date;
			} catch (Exception pe) {
				return null;
			}
		} else if (value instanceof Date) {
			return value;
		}
		throw new ConversionException("不能转换 " + value.getClass().getName() + " 为 " + type.getName());
	}

	protected Object convertToString(Class type, Object value) {
		if (value instanceof Date) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

			if (value instanceof Timestamp) {
				sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			}

			try {
				return sdf.format(value);
			} catch (Exception e) {
				throw new ConversionException("日期转换为字符串时出错！");
			}
		} else {
			return value.toString();
		}
	}
}
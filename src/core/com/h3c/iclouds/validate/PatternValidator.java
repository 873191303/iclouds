package com.h3c.iclouds.validate;


import com.h3c.iclouds.utils.StrUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatternValidator implements ConstraintValidator<CheckPattern, Object> {

	private PatternType patternType;
	
	private String[] values;
	
	public static boolean ipCheck(String ip) {
		if(ip == null) {
			return true;
		}
		String ipRexp = "^(25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[0-9]{1,2})(\\.(25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[0-9]{1,2})){3}$";
		Pattern pattern = Pattern.compile(ipRexp);
		Matcher matcher = pattern.matcher(ip);
		boolean result = matcher.find();
		return result;
	}
	
	public static boolean emailsCheck(String emails) {
		String[] emailArray = emails.split(";");
		for (String email : emailArray) {
			if(!emailCheck(email)) {
				return false;
			}
		}
		return true;
	}
	
	public static boolean emailCheck(String email) {
		if(email == null) {
			return true;
		}
		String emailRexp = "\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*";
		Pattern pattern = Pattern.compile(emailRexp);
		Matcher matcher = pattern.matcher(email);
		boolean result = matcher.find();
		return result;
	}
	
	public boolean isValid(Object obj, ConstraintValidatorContext constraintContext) {
		String object = null;
		if (StrUtils.checkParam(obj)){
			object = obj.toString();
		}
		if (!StrUtils.checkParam(object))
			return true;
		String rexp = "";
		
		switch (patternType) {
			case IP:	// ip验证
				return ipCheck(object);
			case LENGTH:	// ip验证
				if(object.length() == Integer.valueOf(values[0])) {
					return true;
				}
				return false;
			case SYSTYPE:	// ip验证
				if(object.length() != 4) {
					return true;
				}
				for (int i = 0; i < object.length(); i++) {
					String code = object.substring(i, i + 1);
					if(!"01".contains(code)) {
						return false;
					}
				}
				
				return true;
			case CONTAINS:	// 包含
				if(values != null) {
					for (int i = 0; i < values.length; i++) {
						if(values[i].equals(StrUtils.tranString(object))) {
							return true;
						}
					}
				}
				return false;
			case NOTEQUAL:	// 不能为0
				if(values != null) {
					for (int i = 0; i < values.length; i++) {
						if(values[i].equals(object)) {
							return false;
						}
					}
				}
				return true;
			default:
				break;
		}

		Pattern pattern = Pattern.compile(rexp);
		Matcher matcher = pattern.matcher(object);
		boolean result = matcher.find();
		return result;
	}

	@Override
	public void initialize(CheckPattern check) {
		this.patternType = check.type();
		this.values = check.values();
	}

}

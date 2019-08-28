package com.h3c.iclouds.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatternCheckUtils {
	
	public static final String CHECK_IP_PATTERN = "^(2[5][0-5]|2[0-4]\\d|1\\d{2}|\\d{1,2})\\.(25[0-5]|2[0-4]\\d|1\\d{2}|\\d{1,2})\\.(25[0-5]|2[0-4]\\d|1\\d{2}|\\d{1,2})\\.(25[0-5]|2[0-4]\\d|1\\d{2}|\\d{1,2})";
	
	public static boolean checkIp(String ip) {
		Pattern pattern = Pattern.compile(CHECK_IP_PATTERN);
		Matcher matcher = pattern.matcher(ip);
		boolean result = matcher.find();
		return result;
	}

}

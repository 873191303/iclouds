package com.h3c.iclouds.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class CloudHttpUtils extends HttpUtils {

	public static JSONArray putJSONArray(JSONObject obj,String key,String value, String... node) {
		JSONObject temp=null;
		for (String string : node) {
			 temp=obj.getJSONObject(string);
			 obj=temp;
		}
		temp.put(key, value);
		return null;
	}
}

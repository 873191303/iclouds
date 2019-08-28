package com.h3c.iclouds.common;

import com.alibaba.fastjson.JSONObject;
import com.h3c.iclouds.auth.CacheSingleton;
import com.h3c.iclouds.client.EisooClient;
import com.h3c.iclouds.client.EisooParams;
import com.h3c.iclouds.utils.LogUtils;
import com.h3c.iclouds.utils.StrUtils;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.security.Key;
import java.security.MessageDigest;
import java.util.Map;

public class EisooAPIConst {

	/**
	 * 签名生成算法
	 * @param data
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static String calcShaHash (String data, String key) throws Exception{
		String HMAC_SHA1_ALGORITHM = "HmacSHA1";
		Key signingKey = new SecretKeySpec(key.getBytes(), HMAC_SHA1_ALGORITHM);
		Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
		mac.init(signingKey);
		byte[] rawHmac = mac.doFinal(data.getBytes("UTF-8"));
		return Base64.encodeBase64String(rawHmac);
	}

	/**
	 * 获取签名
	 * @param method 请求的方法名
	 * @param body 请求方法的body参数json串，如果没有参数，直接赋值为空即可
     */
	public static String getSign(String method, String appId, String appKey, String body) throws Exception{
		if (StringUtils.isEmpty(method))
			throw new Exception("未设置方法名");
		if (StringUtils.isEmpty(appId))
			throw new Exception("未配置appid");
		if (StringUtils.isEmpty(appKey))
			throw new Exception("未配置appkey");
		String data = appId + method + body;
		return URLEncoder.encode(calcShaHash(data, appKey), "UTF-8");
	}

	/**
	 * 对字符串md5加密
	 * @param str
	 * @return
	 */
	public static String getMD5(String str) {
		String code = "";
		try {
			// 生成一个MD5加密计算摘要
			MessageDigest md = MessageDigest.getInstance("MD5");
			// 计算md5函数
			md.update(str.getBytes());
			// digest()最后确定返回md5 hash值，返回值为8为字符串。因为md5 hash值是16位的hex值，实际上就是8位的字符
			// BigInteger函数则将8位的字符串转换成16位hex值，用字符串来表示；得到字符串形式的hash值
			code =  new BigInteger(1, md.digest()).toString(16);
		} catch (Exception e) {
			e.printStackTrace();
			LogUtils.exception(EisooAPIConst.class, e);
		}
		return code;
	}
	
	/**
	 * 用户管理与组织管理的、操作
	 * @param param 参数
	 * @param method 方法
	 * @param client 连接
	 * @return 返回结果
	 * @throws Exception
	 */
	public static JSONObject getOptionThirdResult(Map<String, Object> param, String method, EisooClient client) throws Exception {
		CacheSingleton singleton = CacheSingleton.getInstance();
		String body = "";
		if (StrUtils.checkParam(param)) {
			body = JSONObject.toJSONString(param);
		}
		String sign = EisooAPIConst.getSign(method, singleton.getEisooApi(EisooParams.EISOO_APPID), singleton
				.getEisooApi(EisooParams.EISOO_APPKEY), body);
		String url = client.tranUrl(singleton.getEisooApi(EisooParams.EISOO_API_USER_ORG), method, sign);
		return client.post(url, param);
	}
}

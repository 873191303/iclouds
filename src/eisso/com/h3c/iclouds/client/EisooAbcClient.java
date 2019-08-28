package com.h3c.iclouds.client;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.SSLContext;

import com.h3c.iclouds.utils.HttpUtils;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSONObject;
import com.h3c.iclouds.auth.CacheSingleton;
import com.h3c.iclouds.exception.MessageException;
import com.h3c.iclouds.rsa.RSAEncrypt;
import com.h3c.iclouds.utils.DateUtils;
import com.h3c.iclouds.utils.LogUtils;
import com.h3c.iclouds.utils.StrUtils;

public class EisooAbcClient {
	
	private String abcToken;
	
//	private String UID;

	private static boolean loginResult = true;

	private String abcUid = null;

	private String abcUname = null;

	private String userId = null;

	private static final String PUBLICPATH = "encrypt/eisoo-public.pem";

	public static String ABCApiUrl = "https://" + CacheSingleton.getInstance().getConfigValue("iclouds.abc.api.ip")
			+ ":" + CacheSingleton.getInstance().getConfigValue("iclouds.abc.api.port");

	public EisooAbcClient(String abcUname, String abcUid, String userId) throws MessageException {
		this.abcUname = abcUname;
		this.abcUid = abcUid;
		this.userId = userId;
		this.initClient();
	}

	private void initClient() {
		LogUtils.info(this.getClass(), "Start init BaseABCClient...");
		loginResult = this.login();
		if (loginResult) {
			LogUtils.info(this.getClass(), "ABC Api login success!");
		} else {
			LogUtils.info(this.getClass(), "ABC Api login failure!");
		}
	}

	private CloseableHttpClient getClient() {
		try {
			SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
				// 信任所有
				public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
					return true;
				}
			}).build();
			SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext,
					SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			return HttpClients.custom().setSSLSocketFactory(sslsf).build();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (KeyStoreException e) {
			e.printStackTrace();
		}
		return HttpClients.createDefault();
	}

	/**
	 * delete请求
	 * 
	 * @param uri
	 * @return
	 */
	public JSONObject delete(String uri) {
		LogUtils.info(this.getClass(), "##########删除: " + uri);
		CloseableHttpClient client = HttpClients.createDefault();
		CloseableHttpResponse response = queryHttp(client, uri, null, EisooAbcParams.DELETE);
		int statusCode = response.getStatusLine().getStatusCode();
		switch (statusCode) {
		case 401: // Unauthorized
		case 403: // Forbidden
			this.login();
			response = queryHttp(client, uri, null, EisooAbcParams.DELETE);
		default:
			break;
		}
		return tranResponse(response, client);
	}

	/**
	 * post请求
	 * 
	 * @param uri
	 * @param params
	 * @return
	 */
	public JSONObject post(String uri, Map<String, Object> params) {
		LogUtils.info(this.getClass(), "##########post: " + uri);
		CloseableHttpClient client = getClient();
		CloseableHttpResponse response = queryHttp(client, uri, params, EisooAbcParams.POST);
		int statusCode = response.getStatusLine().getStatusCode();
		switch (statusCode) {
		case 401: // Unauthorized
		case 403: // Forbidden
			this.login();
			response = queryHttp(client, uri, params, EisooAbcParams.POST);
		default:
			break;
		}
		return tranResponse(response, client);
	}

	public JSONObject post(String uri) {
		return this.post(uri, null);
	}

	/**
	 * put请求
	 * 
	 * @param uri
	 * @param params
	 * @return
	 */
	public JSONObject put(String uri, Map<String, Object> params) {
		LogUtils.info(this.getClass(), "##########put: " + uri);
		CloseableHttpClient client = getClient();
		CloseableHttpResponse response = queryHttp(client, uri, params, EisooAbcParams.PUT);
		int statusCode = response.getStatusLine().getStatusCode();
		switch (statusCode) {
		case 401: // Unauthorized
		case 403: // Forbidden
			this.login();
			response = queryHttp(client, uri, params, EisooAbcParams.PUT);
		default:
			break;
		}
		return tranResponse(response, client);
	}

	public JSONObject put(String uri) {
		return this.put(uri, null);
	}

	/**
	 * get请求
	 * 
	 * @param uri
	 * @param params
	 * @return
	 */
	public JSONObject get(String uri, Map<String, Object> params) {
		LogUtils.info(this.getClass(), "##########获取: " + uri);
		CloseableHttpClient client = getClient();
		uri = HttpUtils.map2Url(uri, params);
		CloseableHttpResponse response = queryHttp(client, uri, EisooAbcParams.GET);
		int statusCode = response.getStatusLine().getStatusCode();
		switch (statusCode) {
		case 401: // Unauthorized
		case 403: // Forbidden
			this.login();
			response = queryHttp(client, uri, EisooAbcParams.GET);
		default:
			break;
		}
		return tranResponse(response, client);
	}

	public JSONObject get(String uri) {
		return this.get(uri, null);
	}

	/**
	 * 请求结果转化为结果
	 * 
	 * @param response
	 * @return
	 */
	private JSONObject tranResponse(CloseableHttpResponse response, CloseableHttpClient client) {
		JSONObject result = new JSONObject();
		int statusCode = response.getStatusLine().getStatusCode();
		result.put("result", statusCode);
		try {
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				String json = EntityUtils.toString(entity, "UTF-8");
				JSONObject jsonObj = JSONObject.parseObject(json);
				result.put("record", jsonObj); // 返回json
			} else {
				result.put("record", statusCode); // 返回json
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				response.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				client.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		LogUtils.info(this.getClass(), "result:" + result);
		return result;
	}

	/**
	 * 执行查询
	 * 
	 * @param uri
	 * @param type
	 * @param params
	 * @return
	 */
	private CloseableHttpResponse queryHttp(CloseableHttpClient client, String uri, Map<String, Object> params,
			String type) {
		Date now = new Date();
		uri = ABCApiUrl + uri;
		CloseableHttpResponse response = null;
		try {
			if (EisooAbcParams.POST.equals(type)) {
				HttpPost post = getPost(uri, params);
				response = client.execute(post);
			} else if (EisooAbcParams.GET.equals(type)) {
				HttpGet get = getGet(uri);
				response = client.execute(get);
			} else if (EisooAbcParams.DELETE.equals(type)) {
				HttpDelete get = getDelete(uri);
				response = client.execute(get);
			} else if (EisooAbcParams.PUT.equals(type)) {
				HttpPut post = getPut(uri, params);
				response = client.execute(post);
			}
		} catch (IOException e) {
			e.printStackTrace();
			LogUtils.info(this.getClass(), "ABC query failure.");
			LogUtils.info(this.getClass(), "ABC query failure Time:" + DateUtils.getDate(now));
			LogUtils.info(this.getClass(), "ABC query failure Uri:" + uri);
			LogUtils.info(this.getClass(), "ABC query failure Type:" + type);
			if (params != null) {
				LogUtils.info(this.getClass(),
						"ABC query failure Params:" + JSONObject.toJSONString(params).toString());
			}
		}
		return response;
	}

	private CloseableHttpResponse queryHttp(CloseableHttpClient client, String uri, String type) {
		return this.queryHttp(client, uri, null, type);
	}

	/**
	 * 登录cloudos Api
	 * 
	 * @throws UnsupportedEncodingException
	 */
	public boolean login() {
		abcToken = null; // token置空

		Map<String, Object> loginMap = null;
		loginMap = getXauthLoginMap();
		CloseableHttpClient client = getClient();

		HttpResponse response = queryHttp(client, EisooAbcParams.ABC_API_AUTH, loginMap, EisooAbcParams.POST);
		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode == 200) { // 登录获取token成功
			try {
//				Header[] headers = response.getAllHeaders();
//				System.out.println(headers);
//				Header setCookie = response.getFirstHeader("Set-Cookie");
//				String uidTemp = setCookie.getValue();
//				
//				int index = uidTemp.indexOf(";");
//				String uid = uidTemp.substring(0, index).trim();
//		        UID = uid;
				
//				System.out.println(uidTemp);
				JSONObject obj = this.tranResponse((CloseableHttpResponse) response, client);
//				System.out.println(obj);
				JSONObject record = obj.getJSONObject("record");
//				System.out.println(record);
				String mes = StrUtils.tranString(record.get("Message"));
				if ("OK".equals(mes)) {
					JSONObject dataObj = record.getJSONObject("Data");
					String temp = StrUtils.tranString(dataObj.get("Token"));
					abcToken = temp;
					System.out.println(obj);
					return true;
				}
				MessageException e = new MessageException(mes);
				LogUtils.exception(this.getClass(), e);
				return false;
			} catch (Exception e) {
				LogUtils.exception(this.getClass(), e);
				e.printStackTrace();
			}
		}
		return false;
	}

	public Map<String, Object> getXauthLoginMap() {
		RSAEncrypt rsaEncrypt = new RSAEncrypt();

		// 获取系统名称
		String path = null;
		String path1 = this.getClass().getResource("/").getPath();
		CacheSingleton singleTon = CacheSingleton.getInstance();
		String name = singleTon.getOsName();
		if (name.toLowerCase().indexOf("window") > -1) {
			path = path1.substring(1);
		} else {
			path = path1;
		}
		// 密钥文件路径获取
		String publicPath = (path + PUBLICPATH);
		// 加载公钥
		try {
			String public_key = RSAEncrypt.getContentByPath(publicPath);
			rsaEncrypt.loadPublicKey(public_key);
		} catch (Exception e) {
			MessageException mesE = new MessageException("加载公钥失败");
			LogUtils.exception(this.getClass(), mesE);
		}

		// 加密
		String cipherText = null;
		try {
			// 加密
			byte[] cipher = rsaEncrypt.encrypt(rsaEncrypt.getPublicKey(), userId);
			System.out.println("密文长度：" + cipher.length);
			cipherText = Base64.encodeBase64String(cipher);

			cipherText = new String(cipherText).replaceAll("[\\n\\r]", "");
		} catch (Exception e) {
			MessageException mes = new MessageException("加密失败");
			LogUtils.exception(this.getClass(), mes);
		}

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("AppCode", "H3C");
		// 不设置Expire为永不过期
//		map.put("Expire", "7200");
		map.put("UserName", userId);
		map.put("RequestInfo", cipherText);
		LogUtils.info(this.getClass(), map.toString());
		System.out.println(map.toString());
		return map;
	}

	/**
	 * 创建put请求
	 * 
	 * @param uri
	 * @param map
	 * @return
	 */
	public HttpPut getPut(String uri, Map<String, Object> map) {
		if (uri == null) {
			uri = "/v2/auth/token";
		}
		HttpPut put = new HttpPut(uri.trim());
		put.setHeader("Accept", "application/json");
		put.setHeader("Accept-Language", "zh");
		put.setHeader("Content-type", "application/json;charset:UTF-8");

		if (abcToken != null) {
			put.setHeader("X-ABCloud-Auth-Token", abcToken);
			put.setHeader("X-XSS-Protection", "1;mode=block");
		}
		if (map != null) {
			HttpEntity entity = null;
			entity = new StringEntity(JSONObject.toJSONString(map).toString(), "UTF-8");
			put.setEntity(entity);
		}
		return put;
	}

	/**
	 * 创建post请求
	 * 
	 * @param uri
	 * @param map
	 * @return
	 */
	public HttpPost getPost(String uri, Map<String, Object> map) {
		if (uri == null) {
			uri = "/v2/auth/token";
		}
		HttpPost post = new HttpPost(uri.trim());
		post.setHeader("Accept", "application/json");
		post.setHeader("Accept-Language", "zh");
		post.setHeader("Content-type", "application/json;charset:UTF-8");

		if (abcToken != null) {
			post.setHeader("X-ABCloud-Auth-Token", abcToken);
			post.setHeader("X-XSS-Protection", "1;mode=block");
		}
		if (map != null) {
			HttpEntity entity = null;
			entity = new StringEntity(JSONObject.toJSONString(map).toString(), "UTF-8");
			post.setEntity(entity);
		}
		return post;
	}

	/**
	 * 创建post请求
	 * 
	 * @param uri
	 * @param map
	 * @return
	 */
	public HttpDelete getDelete(String uri) {
		if (uri == null) {
			uri = "/v2/auth/token";
		}
		HttpDelete post = new HttpDelete(uri.trim());
		post.setHeader("Accept", "application/json");
		post.setHeader("Accept-Language", "zh");
		post.setHeader("Content-type", "application/json;charset:UTF-8");

		if (abcToken != null) {
			post.setHeader("X-ABCloud-Auth-Token", abcToken);
			post.setHeader("X-XSS-Protection", "1;mode=block");
		}
		return post;
	}

	public HttpPost getPost(String uri) {
		return getPost(uri, null);
	}

	/**
	 * 创建get请求
	 * 
	 * @param uri
	 * @return
	 */
	public HttpGet getGet(String uri) {
		HttpGet get = new HttpGet(uri.trim());
		get.setHeader("Accept", "application/json");
		get.setHeader("Content-Type", "application/json;charset=utf-8");
		get.setHeader("X-ABCloud-Auth-Token", abcToken);
		get.setHeader("X-XSS-Protection", "1;mode=block");
//		get.setHeader("Cookie", UID);
		return get;
	}

	public String getAbcUid() {
		return abcUid;
	}

	public String getAbcUname() {
		return abcUname;
	}

	public String getAbcToken() {
		return abcToken;
	}

	public static boolean isLoginResult() {
		return loginResult;
	}

}

package com.h3c.iclouds.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.h3c.iclouds.auth.CacheSingleton;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.operate.CloudosClient;
import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class HttpUtils {
	
	/**
	 * 验证状态码
	 * @param jsonObj
	 * @param codes
	 * @return
	 */
	public static boolean checkResultCode(JSONObject jsonObj, String... codes) {
		if(jsonObj == null) {
			return false;
		}
		if(codes == null || codes.length == 0) {
			return true;
		}
		String result = jsonObj.getString("result");
		for (int i = 0; i < codes.length; i++) {
			if(result.equals(codes[i])) {
				return true;
			}
		}
		return false;
	}
	
	public static String tranUrl(String uri, Map<String, String> params) {
		if(params != null) {
			for (String key : params.keySet()) {
				uri = uri.replace("{" + key + "}", params.get(key));
			}
		}
		return uri;
	}
	
	public static int getStatusCode(HttpResponse response) {
		int code = 600;	// 认为空指针
		if(response != null) {
			StatusLine statusLine = response.getStatusLine();
			if(statusLine != null) {
				code = statusLine.getStatusCode();
			}
		}
		return code;
	}
	
	/**
	 * 
	 * @param uriKey
	 * @param ids
	 * @return
	 */
	public static String getUrl(String uriKey, String... ids) {
		String uri = CacheSingleton.getInstance().getCloudosApi(uriKey);
		if(uri == null) {
			return null;
		}
		for (int i = 0; i < ids.length; i++) {
			uri = tranUrl(uri, ids[i]);
		}
		return uri;
	}
	
	public static String tranUrl(String uri, String... ids) {
		for (int i = 0; i < ids.length; i++) {
			uri = tranUrl(uri, ids[i]);
		}
		return uri;
	}
	
	private static String tranUrl(String uri, String id) {
		int start = uri.indexOf("{");
		int end = uri.indexOf("}");
		if(start == -1 || end == -1) {
			return uri;
		}
		String key = uri.substring(start, end + 1);
		return uri.replace(key, id);
	}
	
	public static List<Map<String, Object>> createListMap(String key, String... strings) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		if(strings != null && strings.length > 0) {
			for (int i = 0; i < strings.length; i++) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put(key, strings[i]);
				list.add(map);
			}
		}
		return list;
	}
	
	/**
	 * Post一个Rest URL，返回Body内容（Json字符串）
	 * 
	 * @param url
	 * @param json
	 * @return
	 */
	public static String postJson(String url, String json) {
		String resp = null;
		try {
			HttpClient client = HttpClients.createDefault();
			HttpPost post = new HttpPost(url);
			HttpEntity entity = new StringEntity(json,
					ContentType.APPLICATION_JSON);
			post.setEntity(entity);
			HttpResponse response = client.execute(post);
			HttpEntity output = response.getEntity();
			resp = EntityUtils.toString(output, Consts.UTF_8);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resp;
	}

	public static String postJson(String url, String json,
			Map<String, String> header) {
		String resp = null;
		try {
			HttpClient client = HttpClients.createDefault();
			HttpPost post = new HttpPost(url);
			HttpEntity entity = new StringEntity(json,
					ContentType.APPLICATION_JSON);
			post.setEntity(entity);
			if (header != null && header.size() > 0) {
				Set<String> hkeys = header.keySet();
				for (String key : hkeys) {
					post.addHeader(key, header.get(key));
				}
			}
			HttpResponse response = client.execute(post);
			HttpEntity output = response.getEntity();
			resp = EntityUtils.toString(output, Consts.UTF_8);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resp;
	}

	/**
	 * 同时获取Body内容和某个header的信息，放入Map
	 * 
	 * @param url
	 * @param json
	 * @param headerKey
	 * @return
	 */
	public static Map<String, Object> postJson(String url, String json,
			String headerKey) {
		Map<String, Object> respMap = new HashMap<String, Object>();
		try {
			HttpClient client = HttpClients.createDefault();
			HttpPost post = new HttpPost(url);
			HttpEntity entity = new StringEntity(json,
					ContentType.APPLICATION_JSON);
			post.setEntity(entity);
			HttpResponse response = client.execute(post);
			HttpEntity output = response.getEntity();
			respMap.put("body", EntityUtils.toString(output, Consts.UTF_8));
			Header[] headers = response.getHeaders(headerKey);
			respMap.put("headers", headers);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return respMap;
	}

	/**
	 * 根据header请求url获取对应的值
	 *
	 * @param url
	 * @param json
	 * @param header
	 * @return
	 */
	public static Header[] postJsonRespHeader(String url, String json,
			String header) {
		Header[] resp = null;
		try {
			HttpClient client = HttpClients.createDefault();
			HttpPost post = new HttpPost(url);
			HttpEntity entity = new StringEntity(json,
					ContentType.APPLICATION_JSON);
			post.setEntity(entity);
			HttpResponse response = client.execute(post);
			resp = response.getHeaders(header);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resp;
	}

	/**
	 * 请求Rest API,获取Json数据
	 *
	 * @param url
	 * @param headers
	 * @return
	 */
	public static String getJson(String url, Map<String, String> headers) {
		String jsonStr = null;
		HttpClient httpclient = HttpClients.createDefault();
		HttpGet hget = new HttpGet(url);
		if (headers != null) // 设置Header参数
		{
			Set<String> hks = headers.keySet();
			for (String hk : hks) {
				hget.setHeader(hk, headers.get(hk));
			}
		}
		RequestConfig requestConfig = RequestConfig.custom()
				.setConnectionRequestTimeout(2000).setConnectTimeout(2000)
				.setSocketTimeout(2000).build();
		hget.setConfig(requestConfig);
		try {
			HttpResponse response = httpclient.execute(hget);
			HttpEntity entity = response.getEntity();
			jsonStr = EntityUtils.toString(entity);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			hget.releaseConnection();
		}
		return jsonStr;
	}

	public static int delete(String url, Map<String, String> headers) {
		int statusCode = 0;
		HttpClient httpclient = HttpClients.createDefault();
		HttpDelete delete = new HttpDelete(url);
		if (headers != null) // 设置Header参数
		{
			Set<String> hks = headers.keySet();
			for (String hk : hks) {
				delete.setHeader(hk, headers.get(hk));
			}
		}
		RequestConfig requestConfig = RequestConfig.custom()
				.setConnectionRequestTimeout(2000).setConnectTimeout(2000)
				.setSocketTimeout(2000).build();
		delete.setConfig(requestConfig);
		try {
			HttpResponse response = httpclient.execute(delete);
			statusCode = response.getStatusLine().getStatusCode();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			delete.releaseConnection();
		}
		return statusCode;
	}

	public static int put(String url, Map<String, String> headers) {
		int statusCode = 0;
		HttpClient httpclient = HttpClients.createDefault();
		HttpPut put = new HttpPut(url);
		if (headers != null) // 设置Header参数
		{
			Set<String> hks = headers.keySet();
			for (String hk : hks) {
				put.setHeader(hk, headers.get(hk));
			}
		}
		RequestConfig requestConfig = RequestConfig.custom()
				.setConnectionRequestTimeout(2000).setConnectTimeout(2000)
				.setSocketTimeout(2000).build();
		put.setConfig(requestConfig);
		try {
			HttpResponse response = httpclient.execute(put);
			statusCode = response.getStatusLine().getStatusCode();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			put.releaseConnection();
		}
		return statusCode;
	}

	public static JSONObject getJSONObject(JSONObject obj, String key) {
		if(obj.containsKey("result") && obj.getString("result").startsWith("2")) {
			JSONObject record = obj.getJSONObject("record");
			if(record != null && record.containsKey(key)) {
				JSONObject keyObj = record.getJSONObject(key);
				if(keyObj != null) {
					return keyObj;
				}
			}
		}
		return null;
	}
	
	public static JSONArray getJSONArray(JSONObject obj, String key) {
		if(obj.containsKey("result") && obj.getString("result").startsWith("2")) {
			JSONObject record = obj.getJSONObject("record");
			if(record != null && record.containsKey(key)) {
				JSONArray keyArray = record.getJSONArray(key);
				if(keyArray != null) {
					return keyArray;
				}
			}
		}
		return null;
	}
	
	public static JSONArray getJSONArray(JSONObject obj) {
		if(obj.containsKey("result") && obj.getString("result").startsWith("2")) {
			JSONArray record = obj.getJSONArray("record");
			if(record != null) {
				return record;
			}
		}
		return null;
	}
	
	public static JSONObject getJSONObject(JSONObject obj) {
		if(obj.containsKey("result") && obj.getString("result").startsWith("2")) {
			JSONObject record = obj.getJSONObject("record");
			if(record != null) {
				return record;
			}
		}
		return null;
	}
	
	/**
	 * 获取json
	 * @param uri
	 * @param type
	 * @param client
	 * @return
	 */
	public static JSONObject getJson (String uri, String type, CloudosClient client) {
		JSONObject jsonObject = client.get(uri);
		if ("vlb".equals(type) || "norms".equals(type)) {
			return getJSONObject(jsonObject);
		}
		return getJSONObject(jsonObject, type);
	}
	
	/**
	 * 获取array
	 * @param uri
	 * @param type
	 * @param client
	 * @return
	 */
	public static JSONArray getArray (String uri, String type, Map<String, Object> paramMap, CloudosClient client) {
		JSONObject jsonObject = client.get(uri, paramMap);
		if ("vlb".equals(type)) {
			return getJSONArray(jsonObject);
		}
		return getJSONArray(jsonObject, type);
	}
	
	/**
	 * 当cloudos返回错误时，获取错误信息
	 * @param jsonObject
	 * @return
	 */
	public static String getError (JSONObject jsonObject) {
		String result = jsonObject.getString("result");
		String record = jsonObject.getString("record");
		if (StrUtils.checkParam(record)) {
			return record;
		}
		return result.toString();
	}
	
	public static List<Map<String, Object>> tranList(List<Map<String, Object>> list, String[] key) {
		if(key == null || key.length == 0) {
			return list;
		}
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		if(list != null) {
			for (Map<String, Object> tempMap : list) {
				Map<String, Object> map = new HashMap<String, Object>();
				for (int j = 0; j < key.length; j++) {
					map.put(key[j], tempMap.get(key[j]));
				}
				result.add(map);
			}	
		}
		return result;
	}
	
	/**
	 * 设置请求异常时间
	 * @return
	 */
	public static RequestConfig createRequestConfig() {
		RequestConfig requestConfig = RequestConfig.custom()
				.setConnectionRequestTimeout(100000)
		        .setConnectTimeout(2000)
		        .setSocketTimeout(100000).build();
		return requestConfig;
	}
	
	public static JSONObject tranResponse(CloseableHttpResponse response, CloseableHttpClient client) {
		return tranResponse(response, client, false);
	}

	/**
	 * 请求结果转化为结果
	 * @param response
	 * @return
	 */
	public static JSONObject tranResponse(CloseableHttpResponse response, CloseableHttpClient client, boolean isKeepAlive) {
		JSONObject result = new JSONObject();
		int statusCode = HttpUtils.getStatusCode(response);
		result.put("result", statusCode);

		JSONObject headerObj = new JSONObject();
		Header[] headers = response.getAllHeaders();
		for (Header header : headers) {
			headerObj.put(header.getName(), header.getValue());
		}
		result.put("headers", headerObj);
		try {
			HttpEntity entity = null;
			if(response != null) {
				entity = response.getEntity();
			}
			if(entity != null) {
				String json = EntityUtils.toString(entity, "UTF-8").trim();
				if(StrUtils.checkParam(json)) {
					if(json.startsWith("{")) {
						JSONObject jsonObj = JSONObject.parseObject(json);
						result.put("record", jsonObj);	// 返回json
					} else if(json.startsWith("[")) {
						JSONArray jsonArray = JSONObject.parseArray(json);
						result.put("record", jsonArray);	// 返回json
					} else {
						if("null".equals(json)) {
							result.put("record", null);	// 返回原信息
						} else {
							result.put("record", json);	// 返回原信息
						}
					}
				}
			} else {
				result.put("record", statusCode);	// 返回json
			}
		} catch (Exception e) {
			LogUtils.exception(HttpUtils.class, e);
		} finally {
			close(response);
			if(!isKeepAlive) {
				close(client);
			}
		}
		String code = StrUtils.tranString(ThreadContext.get(ConfigProperty.LOG_WRITE_TYPE));
		if(!ConfigProperty.LOG_WRITE_TYPE_STOP.equals(code)) {
			LogUtils.info(HttpUtils.class, "result:" + result);
		}
		return result;
	}

	public static JSONArray getCasJSONArray(JSONObject jsonObj, String key) {
		JSONArray array = null;
		if(jsonObj.containsKey(key)) {
			Object obj = jsonObj.get(key);
			if(obj instanceof String) {
				return null;
			} else if(obj instanceof JSONObject) {
				array = new JSONArray();
				array.add(obj);
			} else if(obj instanceof JSONArray) {
				array = (JSONArray)obj;
			}
		}
		return array;
	}

	public static JSONObject getCasJSONObject(JSONObject jsonObj, String key) {
		JSONObject object = null;
		if(jsonObj.containsKey(key)) {
			Object obj = jsonObj.get(key);
			if(obj instanceof String) {
				return null;
			} else if(obj instanceof JSONObject) {
				object = (JSONObject)obj;
			} else if(obj instanceof JSONArray) {
				object = ((JSONArray) obj).size() > 0 ? ((JSONArray) obj).getJSONObject(0) : null;
			}
		}
		return object;
	}

	public static String map2Url(String uri, Map<String, Object> params) {
		if(params != null && !params.isEmpty()) {
			StringBuffer buffer = new StringBuffer("?");
			for (String key : params.keySet()) {
				buffer.append(key + "=" + StrUtils.tranString(params.get(key)) + "&");
			}
			uri += buffer.toString();
		}
		return uri;
	}

	public static void close(Closeable closeable) {
		try {
			if(closeable != null) {
				closeable.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void close(CloseableHttpResponse response, CloseableHttpClient client) {
		close(response);
		close(client);
	}
}

package com.h3c.iclouds.operate;

import com.alibaba.fastjson.JSONObject;
import com.h3c.iclouds.auth.CacheSingleton;
import com.h3c.iclouds.auth.SessionBean;
import com.h3c.iclouds.base.BaseClient;
import com.h3c.iclouds.base.SpringContextHolder;
import com.h3c.iclouds.biz.UserBiz;
import com.h3c.iclouds.dao.UserDao;
import com.h3c.iclouds.po.User;
import com.h3c.iclouds.utils.CryptoUtils;
import com.h3c.iclouds.utils.DateUtils;
import com.h3c.iclouds.utils.HttpUtils;
import com.h3c.iclouds.utils.LogUtils;
import com.h3c.iclouds.utils.PwdUtils;
import com.h3c.iclouds.utils.StrUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CloudosClient implements java.io.Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private String cloudosToken;
	
	private boolean loginResult = true;
	
	private String tenantId;
	
	private String tenantName;
	
	private String cloudosUid = null;
	
	private String cloudosUname = null;
	
	private static final int retryNum = 3;
	
	public static String cloudosApiUrl = "http://" +
			CacheSingleton.getInstance().getConfigValue("iclouds.cloudos.api.ip") + ":" +
			CacheSingleton.getInstance().getConfigValue("iclouds.cloudos.api.port");
	
	private CloudosClient(String cloudosUid, String cloudosUname) {
		LogUtils.info(this.getClass(), "cloudosApiUrl:" + cloudosApiUrl);
		this.cloudosUname = cloudosUname;
		this.cloudosUid = cloudosUid;
		this.initClient();
	}
	
	/**
	 * 设置特殊头使用
	 */
	private static ThreadLocal<Map<String, String>> headerLocal = new ThreadLocal<Map<String, String>>();
	
	/**
	 * 获取默认的连接（admin权限）
	 * @return
	 */
	public static CloudosClient createAdmin() {
		CloudosClient client = CacheSingleton.getInstance().getAdminClient();
		if(client == null) {
			String baseUserName = CacheSingleton.getInstance().getConfigMap().get("iclouds.cloudos.api.base.username");
			String baseUid = CacheSingleton.getInstance().getConfigMap().get("iclouds.cloudos.api.base.uid");
			client = create(baseUid, baseUserName);
			CacheSingleton.getInstance().setAdminClient(client);
		}
		return client;
	}
	
 
	/**
	 * 获取连接
	 * @param cloudosUid
	 * @param cloudosUname
	 * @return
	 */
	public static CloudosClient create(String cloudosUid, String cloudosUname) {
		LogUtils.info(CloudosClient.class, "cloudosUid:" + cloudosUid);
		LogUtils.info(CloudosClient.class, "cloudosUname:" + cloudosUname);
		CloudosClient client = new CloudosClient(cloudosUid, cloudosUname);
		return client.isLoginResult() ? client : null;
	}
	public static CloudosClient create(UserBiz userBiz, String projectId) {

		Map<String,String> map  = new HashMap<String,String>();
		map.put("projectId", projectId);
		List<User> ListUser = userBiz.findByMap(User.class, map);
		if(ListUser.size()==0)return null;
		User user = ListUser.get(0);
		LogUtils.info(CloudosClient.class, "cloudosUid:" + user.getCloudosId());
		LogUtils.info(CloudosClient.class, "cloudosUname:" + user.getLoginName());
		CloudosClient client = new CloudosClient(user.getCloudosId(), user.getLoginName());
		return client.isLoginResult() ? client : null;
	}
	
//	public static void main(String[] args) throws FileNotFoundException {
//		CloudosClient client = CloudosClient.createAdmin();
//		String uri = CacheSingleton.getInstance().getCloudosApi(CloudosParams.CLOUDOS_API_USERS_ACTION);
//		uri = HttpUtils.tranUrl(uri, "b7fd9c970d204d5aa86465b7ec90798a");
//		System.out.println(client.get("/v3/projects"));
//	}
	
	private void initClient() {
		LogUtils.info(this.getClass(), "Start init BaseCloudClient...");
		// 增加失败尝试次数
		for (int i = 0; i < retryNum; i++) {
			LogUtils.info(SessionBean.class, "Connection cloudos, current attempts: " + i);
			loginResult = this.login();
			if(loginResult) {
				break;
			}
			LogUtils.info(SessionBean.class, "Connection cloudos, result: " + loginResult);
		}
		if (loginResult) {
			LogUtils.info(this.getClass(), "Cloudos Api login success!");
		} else {
			LogUtils.info(this.getClass(), "Cloudos Api login failure!");
		}
	}
	
	private CloseableHttpClient getClient() {
		boolean test = true;
		if(!test) {
			return BaseClient.getClient();
		}
		return HttpClients.createDefault();
	}
	
	/**
	 * delete请求
	 * @param uri
	 * @return
	 */
	public JSONObject delete(String uri) {
		LogUtils.info(this.getClass(), "##########delete: " + uri);
		CloseableHttpClient client = HttpClients.createDefault();
		CloseableHttpResponse response = queryHttp(client, uri, null, CloudosParams.DELETE);
		int statusCode = HttpUtils.getStatusCode(response);
		switch (statusCode) {
			case 401:	// Unauthorized
//			case 403:	// Forbidden
				this.login();	
				response = queryHttp(client, uri, null, CloudosParams.DELETE);
			default:
				break;
		}
		return HttpUtils.tranResponse(response, client);
	}
	
	/**
	 * post请求
	 * @param uri
	 * @param params
	 * @return
	 */
	public JSONObject post(String uri, Map<String, Object> params) {
		LogUtils.info(this.getClass(), "##########post: " + uri);
		LogUtils.info(this.getClass(), "##########params: " + StrUtils.toJSONString(params));
		CloseableHttpClient client = getClient();
		CloseableHttpResponse response = queryHttp(client, uri, params, CloudosParams.POST);
		int statusCode = HttpUtils.getStatusCode(response);
		switch (statusCode) {
			case 401:	// Unauthorized
//			case 403:	// Forbidden
				this.login();	
				response = queryHttp(client, uri, params, CloudosParams.POST);
			default:
				break;
		}
		return HttpUtils.tranResponse(response, client);
	}
	
	public JSONObject post(String uri) {
		return this.post(uri, null);
	}
	
	/**
	 * put请求
	 * @param uri
	 * @param params
	 * @return
	 */
	public JSONObject put(String uri, Map<String, Object> params) {
		LogUtils.info(this.getClass(), "##########put: " + uri);
		LogUtils.info(this.getClass(), "##########params: " + StrUtils.toJSONString(params));
		CloseableHttpClient client = getClient();
		CloseableHttpResponse response = queryHttp(client, uri, params, CloudosParams.PUT);
		int statusCode = HttpUtils.getStatusCode(response);
		switch (statusCode) {
			case 401:	// Unauthorized
//			case 403:	// Forbidden
				this.login();	
				response = queryHttp(client, uri, params, CloudosParams.PUT);
			default:
				break;
		}
		return HttpUtils.tranResponse(response, client);
	}
	
	public JSONObject patch(String uri, Map<String, Object> params) {
		LogUtils.info(this.getClass(), "##########patch: " + uri);
		LogUtils.info(this.getClass(), "##########params: " + StrUtils.toJSONString(params));
		CloseableHttpClient client = getClient();
		CloseableHttpResponse response = queryHttp(client, uri, params, CloudosParams.PATCH);
		int statusCode = HttpUtils.getStatusCode(response);
		switch (statusCode) {
			case 401:	// Unauthorized
//			case 403:	// Forbidden
				this.login();	
				response = queryHttp(client, uri, params, CloudosParams.PATCH);
			default:
				break;
		}
		return HttpUtils.tranResponse(response, client);
	}
	
	public JSONObject put(String uri) {
		return this.put(uri, null);
	}
	public JSONObject patch(String uri) {
		return this.patch(uri, null);
	}
	
	/**
	 * get请求
	 * @param uri
	 * @param params
	 * @return
	 */
	public JSONObject get(String uri, Map<String, Object> params) {
		LogUtils.info(this.getClass(), "##########get: " + uri);
		LogUtils.info(this.getClass(), "##########params: " + StrUtils.toJSONString(params));
		CloseableHttpClient client = getClient();
		uri = HttpUtils.map2Url(uri, params);
		CloseableHttpResponse response = queryHttp(client, uri, CloudosParams.GET);
		int statusCode = HttpUtils.getStatusCode(response);
		switch (statusCode) {
			case 401:	// Unauthorized
//			case 403:	// Forbidden
				this.login();
				response = queryHttp(client, uri, CloudosParams.GET);
			default:
				break;
		}
		return HttpUtils.tranResponse(response, client);
	}
	
	public JSONObject get(String uri) {
		return this.get(uri, null);
	}
	
	/**
	 * 执行查询
	 * @param uri
	 * @param type
	 * @param params
	 * @return
	 */
	private CloseableHttpResponse queryHttp(CloseableHttpClient client, String uri, Map<String, Object> params, String type) {
		Date now = new Date();
		uri = cloudosApiUrl + uri;
		CloseableHttpResponse response = null;
		try {
			HttpRequestBase request = null;
			if(CloudosParams.POST.equals(type)) {
				request = getPost(uri, params);
			} else if(CloudosParams.GET.equals(type)) {
				request = getGet(uri);
			} else if(CloudosParams.DELETE.equals(type)) {
				request = getDelete(uri);
			} else if(CloudosParams.PUT.equals(type)) {
				request = getPut(uri, params);
			}else if(CloudosParams.PATCH.equals(type)) {
				request = getPatch(uri, params);
			}
			if(request != null) {
				request.setConfig(HttpUtils.createRequestConfig());
				this.putHeader(request);	// 设置其他头内容
				Header[] headers = request.getAllHeaders();
				if(headers != null && headers.length > 0) {
					for (Header header : headers) {
						LogUtils.info(this.getClass(), "Header:" + header.getName() + "\t----\t" + header.getValue());
					}
				}
				response = client.execute(request);
			}
		} catch (IOException e) {
			LogUtils.exception(getClass(), e);
			LogUtils.info(this.getClass(), "Cloudos query failure.");
			LogUtils.info(this.getClass(), "Cloudos query failure Time:" + DateUtils.getDate(now));
			LogUtils.info(this.getClass(), "Cloudos query failure Uri:" + uri);
			LogUtils.info(this.getClass(), "Cloudos query failure Type:" + type);
			if(params != null) {
				LogUtils.info(this.getClass(), "Cloudos query failure Params:" + JSONObject.toJSONString(params).toString());
			}
		}
		return response;
	}
	
	
	private CloseableHttpResponse queryHttp(CloseableHttpClient client, String uri, String type) {
		return this.queryHttp(client, uri, null, type);
	}
	
	/**
	 * 登录cloudos Api
	 * @throws UnsupportedEncodingException
	 */
	public boolean login() {
		cloudosToken = null;	// token置空
		CacheSingleton singleton = CacheSingleton.getInstance();
		Map<String, Object> loginMap;
		String loginUri;
		if(singleton.isCloudosTokenByJar()) {	// 用户名+id方式登录
			loginMap = getXauthLoginMap();
			loginUri = CacheSingleton.getInstance().getCloudosApi(CloudosParams.CLOUDOS_API_XAUTH);
		} else {
			UserDao userDao = SpringContextHolder.getBean("userDao");
			User user = userDao.singleByClass(User.class, StrUtils.createMap("cloudosId", this.getCloudosUid()));
			if(user == null) {
				LogUtils.warn(this.getClass(), "Login cloudos API failure. Not found user cloudosId:" + user.getCloudosId());
				return false;
			}
			String password = user.getPassword();
			try {
				password = PwdUtils.decrypt(password, user.getLoginName() + user.getId());
			} catch (Exception e) {
				e.printStackTrace();
			}
			if(!StrUtils.checkParam(password)) {
				LogUtils.warn(this.getClass(), "Login cloudos API failure. No password with user cloudosId:" + user.getCloudosId());
				return false;
			}
			String projectId = user.getProjectId();
			String projectName = singleton.getProjectNameMap().get(projectId);
			if(!StrUtils.checkParam(projectName)) {
				LogUtils.warn(this.getClass(), "Login cloudos API failure. No project with user cloudosId:" + user.getCloudosId());
				return false;
			}
			loginMap = getLoginMap(user.getLoginName(), password, projectName);
			loginUri = CacheSingleton.getInstance().getCloudosApi(CloudosParams.CLOUDOS_API_AUTH);
		}
		
		CloseableHttpClient client = this.getClient();
		CloseableHttpResponse response = queryHttp(client, loginUri, loginMap, CloudosParams.POST);
		JSONObject jsonObject = HttpUtils.tranResponse(response, client);
		int statusCode = jsonObject.getInteger("result");
		if(statusCode == 201) {	// 登录获取token成功
			try {
				JSONObject headers = jsonObject.getJSONObject("headers");
				// 设置token
				cloudosToken = headers.getString("X-Subject-Token");
				if(StrUtils.checkParam(cloudosToken)) {
					return true;
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		} else {
			LogUtils.warn(this.getClass(), "Login cloudos API failure. Cause by:" + jsonObject);
		}
		return false;
	}
	/**
	 * 创建登录参数
	 * @return
	 */
	private Map<String, Object> getLoginMap(String userName, String password, String tenant) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("{");
		buffer.append("	\"auth\": {");
		buffer.append("		\"identity\": {");
		buffer.append("			\"methods\": [");
		buffer.append("				\"password\"");
		buffer.append("			],");
		buffer.append("			\"password\": {");
		buffer.append("				\"user\": {");
		buffer.append("					\"domain\": {");
		buffer.append("						\"id\": \"default\"");
		buffer.append("					},");
		buffer.append("					\"name\": \"" + userName + "\",");
		buffer.append("					\"password\": \"" + password + "\"");
		buffer.append("				}");
		buffer.append("			}");
		buffer.append("		},");
		buffer.append("		\"scope\": {");
		buffer.append("			\"project\": {");
		buffer.append("				\"domain\": {");
		buffer.append("					\"id\": \"default\"");
		buffer.append("				},");
		buffer.append("				\"name\": \"" + tenant + "\"");
		buffer.append("			}");
		buffer.append("		}");
		buffer.append("	}");
		buffer.append("}");
		return JSONObject.parseObject(buffer.toString());
	}
	
	public Map<String, Object> getXauthLoginMap() {
		String xauth = CacheSingleton.getInstance().getConfigValue("iclouds.cloudos.api.xauth");
		String md5Code = null;
		try {
			String encrytCode = cloudosUid + xauth;
			md5Code = CryptoUtils.encryptData(encrytCode, "UTF-8");
		} catch (Exception e) {
			LogUtils.info(this.getClass(), e.getMessage());
			md5Code = "h/m2TRmEtUnNio64sDh3OcHZfA/CYHo6n40m7bSwWKM3MJh5Vb3uUWJ/cGvbbrkEStpR+4ACb4XR7JuYox2YeQ==";
		}
		StringBuffer buffer = new StringBuffer();
		buffer.append("{");
		buffer.append("	\"token\": {");
		buffer.append("		\"username\": \"" + cloudosUname + "\",");
		buffer.append("		\"userid\": \"" + cloudosUid + "\",");
		buffer.append("		\"xauth\": \"" + md5Code + "\"");
		buffer.append("	}");
		buffer.append("}");
		return JSONObject.parseObject(buffer.toString());
	}
	
	/**
	 * 创建put请求
	 * @param uri
	 * @param map
	 * @return
	 */
	public HttpPut getPut(String uri, Map<String, Object> map) {
		if(uri == null) {
			uri = "/v3/auth/tokens";
		}
		HttpPut put = new HttpPut(uri.trim());
		put.setHeader("Accept-Language", "zh");
		
		if(cloudosToken != null) {
			put.setHeader("X-Auth-Token", cloudosToken);
		}
		if(map != null && !map.isEmpty()) {
			put.setHeader("H3CloudOS-Core-Target", "compute");
			put.setHeader("Content-type", "application/json");
			HttpEntity entity = null;
			String isName = (String) map.get(CloudosParams.INPUTSTREAM);	// 是否为文件上传
			if(isName == null) {
				entity = new StringEntity(JSONObject.toJSONString(map).toString(), "UTF-8");
			} else {
				try {
					entity = new InputStreamEntity(new FileInputStream(isName), -1, ContentType.APPLICATION_OCTET_STREAM);
					((InputStreamEntity)entity).setChunked(true);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
					LogUtils.warn(getClass(), "File " + isName + " not Found!");
				}
			}
			put.setEntity(entity);
		}
		return put;
	}
	
	public HttpPatch getPatch(String uri, Map<String, Object> map) {
		if(uri == null) {
			uri = "/v3/auth/tokens";
		}
		HttpPatch patch = new HttpPatch(uri.trim());
		patch.setHeader("Accept", "application/json");
		patch.setHeader("Accept-Language", "zh");
		
		if(cloudosToken != null) {
			patch.setHeader("X-Auth-Token", cloudosToken);
		}
		if(map != null) {
			patch.setHeader("H3CloudOS-Core-Target", "compute");
			patch.setHeader("Content-type", "application/json");
			HttpEntity entity = null;
			entity = new StringEntity(JSONObject.toJSONString(map).toString(), "UTF-8");
			patch.setEntity(entity);
		}
		return patch;
	}

	/**
	 * 创建post请求
	 * @param uri
	 * @param map
	 * @return
	 */
	public HttpPost getPost(String uri, Map<String, Object> map) {
		if(uri == null) {
			uri = "/v3/auth/tokens";
		}
		HttpPost post = new HttpPost(uri.trim());
		post.setHeader("Accept", "application/json");
		post.setHeader("Accept-Language", "zh");
		
		if(cloudosToken != null) {
			post.setHeader("X-Auth-Token", cloudosToken);
		}
		if(map != null) {
			post.setHeader("Content-type", "application/json");
			HttpEntity entity = null;
			entity = new StringEntity(JSONObject.toJSONString(map).toString(), "UTF-8");
			post.setEntity(entity);
		}
		return post;
	}
	
	/**
	 * 创建post请求
	 * @param uri
	 * @return
	 */
	public HttpDelete getDelete(String uri) {
		if(uri == null) {
			uri = "/v3/auth/tokens";
		}
		HttpDelete post = new HttpDelete(uri.trim());
		post.setHeader("Accept", "application/json");
		post.setHeader("Accept-Language", "zh");		
		if(cloudosToken != null) {
			post.setHeader("X-Auth-Token", cloudosToken);
		}
		return post;
	}

	public HttpPost getPost(String uri) {
		return getPost(uri, null);
	}
	
	/**
	 * 创建get请求
	 * @param uri
	 * @return
	 */
	public HttpGet getGet(String uri) {
		HttpGet get = new HttpGet(uri.trim());
		get.setHeader("Accept", "application/json");
		get.setHeader("X-Auth-Token", cloudosToken);
		return get;
	}
	
	public void putHeader(HttpRequestBase request) {
		Map<String, String> map = this.getHeaderLocal();
		if(map != null && !map.isEmpty()) {
			for (String key : map.keySet()) {
				request.setHeader(key, map.get(key));
			}
			map.clear();
		}
	}
	
	public boolean isLoginResult() {
		return loginResult;
	}

	public String getTenantId() {
		if(tenantId == null) {
			queryTenantId();
		}
		return tenantId;
	}
	
	public String queryTenantId() {
		String uri = HttpUtils.tranUrl(CacheSingleton.getInstance().getCloudosApi("cloudos.api.user"), cloudosUid);
		JSONObject jsonObj = this.get(uri);
		int statusCode = jsonObj.getIntValue("result");
		if(statusCode == 200) {
			try {
				JSONObject record = jsonObj.getJSONObject("record");
				this.tenantId = record.getJSONObject("user").getJSONObject("project").getString("id");
				this.tenantName = record.getJSONObject("user").getJSONObject("project").getString("name");
				if(tenantId != null) {
					return tenantId;
				}
			} catch (Exception e) {
				LogUtils.exception(this.getClass(), e);
			}
		}
		return null;
	}

	public String getTenantName() {
		return tenantName;
	}

	public void setTenantName(String tenantName) {
		this.tenantName = tenantName;
	}
	
	public String getCloudosUid() {
		return cloudosUid;
	}

	public String getCloudosUname() {
		return cloudosUname;
	}
	
	/**
	 * 设置特殊头部内容
	 * @param key
	 * @param value
	 * @return
	 */
	public CloudosClient setHeaderLocal(String key, String value) {
		Map<String, String> header = getHeaderLocal();
		if(header == null) {
			header = new HashMap<>();
		}
		header.put(key, value);
		return setHeaderLocal(header);
	}

	/**
	 * 设置特殊头部内容
	 * @param header
	 * @return
	 */
	public CloudosClient setHeaderLocal(Map<String, String> header) {
		headerLocal.set(header);
		return this;
	}

	/**
	 * 获取设置的头部内容
	 * @return
	 */
	public Map<String, String> getHeaderLocal() {
		return headerLocal.get();
	}

}

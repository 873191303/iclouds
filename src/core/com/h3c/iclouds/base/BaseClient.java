package com.h3c.iclouds.base;

import com.alibaba.fastjson.JSONObject;
import com.h3c.iclouds.operate.CloudosParams;
import com.h3c.iclouds.utils.HttpUtils;
import com.h3c.iclouds.utils.LogUtils;
import com.h3c.iclouds.utils.StrUtils;
import org.apache.http.HttpEntity;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;

/**
 * Created by yKF7317 on 2017/5/2.
 */
public abstract class BaseClient {
	
	public static CloseableHttpClient getClient() {
		try {
			SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
				// 信任所有
				public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
					return true;
				}
			}).build();
			SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext);
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
	
	protected CloseableHttpClient getClient(String hostIp, Integer hostPort, String userName, String password) {
		try {
			SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
				// 信任所有
				public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
					return true;
				}
			}).build();
			CredentialsProvider credsProvider = new BasicCredentialsProvider();
			credsProvider.setCredentials(new AuthScope(hostIp, hostPort), new UsernamePasswordCredentials(userName, password));
			SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext, SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			return HttpClients.custom().setDefaultCredentialsProvider(credsProvider).setSSLSocketFactory(sslsf).build();
		} catch (NoSuchAlgorithmException e) {
			LogUtils.exception(this.getClass(), e);
		} catch (KeyManagementException e) {
			LogUtils.exception(this.getClass(), e);
		} catch (KeyStoreException e) {
			LogUtils.exception(this.getClass(), e);
		}
		return HttpClients.createDefault();
	}
	
	/**
	 * 关闭连接
	 * @param client
	 */
	public static void closeClient(CloseableHttpClient client) {
		if(client != null) {
			try {
				client.close();
			} catch (IOException e) {
				LogUtils.warn(BaseClient.class, "Close client failure.");
			}
			client = null;
		}
	}
	
	protected static HttpRequestBase getRequest(String url, Map<String, Object> params, String type) {
		HttpRequestBase request = null;
		HttpEntity entity = null;
		if(CloudosParams.POST.equals(type)) {
			HttpPost post = new HttpPost(url.trim());
			if (StrUtils.checkParam(params)) {
				entity = new StringEntity(JSONObject.toJSONString(params), "utf-8");
				post.setEntity(entity);
			}
			request = post;
		} else if(CloudosParams.GET.equals(type)) {
			HttpGet get = new HttpGet(url.trim());
			request = get;
		} else if(CloudosParams.DELETE.equals(type)) {
			HttpDelete delete = new HttpDelete(url.trim());
			request = delete;
		} else if(CloudosParams.PUT.equals(type)) {
			HttpPut put = new HttpPut(url.trim());
			if (StrUtils.checkParam(params)) {
				entity = new StringEntity(JSONObject.toJSONString(params), "utf-8");
				put.setEntity(entity);
			}
			request = put;
		}
		return request;
	}
	
	protected static JSONObject executeRequest(HttpRequestBase request, CloseableHttpClient client) throws Exception {
		CloseableHttpResponse response = client.execute(request);
		JSONObject jsonObject = HttpUtils.tranResponse(response, client, false);
		return jsonObject;
	}
	
	/**
	 * get请求
	 * @param uri
	 * @return
	 */
	public JSONObject get(String uri) {
		return this.get(uri, null);
	}
	
	/**
	 * get请求
	 * @param uri
	 * @param params
	 * @return
	 */
	public JSONObject get(String uri, Map<String, Object> params) {
		CloseableHttpClient client = getClient();
		if(StrUtils.checkParam(params)) {
			StringBuffer buffer = new StringBuffer("?");
			for (String key : params.keySet()) {
				buffer.append(key + "=" + StrUtils.tranString(params.get(key)) + "&");
			}
			uri = uri + buffer.toString();
		}
		return queryHttp(uri, null, CloudosParams.GET, null);
	}
	
	
	/**
	 * post请求
	 * @param uri
	 * @param params
	 * @return
	 */
	public JSONObject post(String uri, Map<String, Object> params) {
		return queryHttp(uri, params, CloudosParams.POST, null);
	}
	
	/**
	 * delete请求
	 * @param uri
	 * @param params
	 * @return
	 */
	public JSONObject delete(String uri, Map<String, Object> params) {
		return queryHttp(uri, params, CloudosParams.DELETE, null);
	}
	
	/**
	 * put请求
	 * @param uri
	 * @param params
	 * @return
	 */
	public JSONObject put(String uri, Map<String, Object> params) {
		return queryHttp(uri, params, CloudosParams.PUT, null);
	}
	
	protected abstract JSONObject queryHttp(String uri, Map<String, Object> param, String type, Map<String, Object> headers);
}


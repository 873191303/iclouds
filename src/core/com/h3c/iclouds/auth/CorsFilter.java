package com.h3c.iclouds.auth;

import com.alibaba.fastjson.JSONObject;
import com.h3c.iclouds.auth.myfilter.WrapperedRequest;
import com.h3c.iclouds.auth.myfilter.WrapperedResponse;
import com.h3c.iclouds.base.BaseRest;
import com.h3c.iclouds.base.SpringContextHolder;
import com.h3c.iclouds.biz.OpeLogBiz;
import com.h3c.iclouds.biz.OperateLogsBiz;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.common.OperateLogEnum;
import com.h3c.iclouds.utils.LogUtils;
import com.h3c.iclouds.utils.StrUtils;
import com.h3c.iclouds.utils.ThreadContext;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@WebFilter(filterName = "corsFilter", urlPatterns = { "/*" })
public class CorsFilter implements Filter {

	private String allowOrigin;

	private String allowMethods;

	private String allowCredentials;

	private String allowHeaders;

	private String exposeHeaders;
	
	private OpeLogBiz opeLogBiz;

	private OperateLogsBiz operateLogsBiz;

	private static final List<String> filterUriList = new ArrayList<>();

	private static final List<String> filterMethodList = new ArrayList<>();

	static {
		filterUriList.add("firewall");
		filterUriList.add("route");
		filterUriList.add("network");
		filterUriList.add("vlbPool");
		filterUriList.add("vlb");
		filterUriList.add("vlbMember");
		filterUriList.add("policieRule");
		filterUriList.add("vdcInfo");
		filterUriList.add("port");

		filterUriList.add("login");
		filterUriList.add("logout");
		
		filterUriList.add("VOLUME".toLowerCase());
		filterUriList.add("FLOATINGIP".toLowerCase());
		filterUriList.add("SERVER".toLowerCase());
		filterUriList.add("PROJECT".toLowerCase());
		filterUriList.add("RECYCLE".toLowerCase());
		
		filterMethodList.add("POST");
		filterMethodList.add("PUT");
		filterMethodList.add("DELETE");
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		allowOrigin = filterConfig.getInitParameter("allowOrigin");
		allowMethods = filterConfig.getInitParameter("allowMethods");
		allowCredentials = filterConfig.getInitParameter("allowCredentials");
		allowHeaders = filterConfig.getInitParameter("allowHeaders");
		exposeHeaders = filterConfig.getInitParameter("exposeHeaders");
		opeLogBiz = SpringContextHolder.getBean("opeLogBiz");
		operateLogsBiz = SpringContextHolder.getBean("operateLogsBiz");
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		// 清空线程对象，防止线程池复用导致数据错误
		ThreadContext.clear();
		String uuid = StrUtils.getUUID();	// 请求标识，用于记录日志使用
		ThreadContext.set(ConfigProperty.REQUEST_UUID, uuid);
		// TODO 需要增加重复请求过滤
		Date startTime = new Date();
		HttpServletResponse hresponse = (HttpServletResponse) response;
		HttpServletRequest hrequest = (HttpServletRequest) request;
		String uri = hrequest.getRequestURI();
		String remoteIp = BaseRest.getIpAddress(hrequest);
		String method = hrequest.getMethod().toUpperCase();
		WrapperedRequest wrapRequest = null;
		if (request instanceof HttpServletRequest) {
			// get请求和附件上传不做装饰
			if (!"GET".equals(method) && !uri.contains("file")) {
				// 获取POST提交的参数
				wrapRequest = new WrapperedRequest(hrequest);
			}
		}

		if (StrUtils.isNotEmpty(allowOrigin)) {
			hresponse.setHeader("Access-Control-Allow-Origin", allowOrigin);
		}
		if (StrUtils.isNotEmpty(allowMethods)) {
			hresponse.setHeader("Access-Control-Allow-Methods", allowMethods);
		}
		if (StrUtils.isNotEmpty(allowCredentials)) {
			hresponse.setHeader("Access-Control-Allow-Credentials", allowCredentials);
		}
		if (StrUtils.isNotEmpty(allowHeaders)) {
			hresponse.setHeader("Access-Control-Allow-Headers", allowHeaders);
		}
		if (StrUtils.isNotEmpty(exposeHeaders)) {
			hresponse.setHeader("Access-Control-Expose-Headers", exposeHeaders);
		}
		
		WrapperedResponse wrapResponse = new WrapperedResponse((HttpServletResponse) response);
		try {
			if(null != wrapRequest)
				chain.doFilter(wrapRequest, wrapResponse);
			else 
				chain.doFilter(hrequest, wrapResponse);
		} catch (Exception e) {
			LogUtils.exception(this.getClass(), e);
		}

		byte[] data = wrapResponse.getResponseData();
		Map<String, Object> map = StrUtils.createMap("Access address", uri);
		String record = new String(data, Charset.forName("UTF-8"));
		map.put("Return data", record);
		map.put("Access IP", remoteIp);
		map.put("Return status code", wrapResponse.getStatus());
		
		ServletOutputStream out = response.getOutputStream();
		out.write(data);
		out.flush();
		
		if (StrUtils.contains(uri, ".js", ".css", ".png", ".jpg", ".html", "api-docs", 
				"ModelAndView", ".gz", ".jsp", "object", ".woff", ".tff", ".ico")) {
			return;
		}
		
		if(!uri.contains("attachment") && !uri.contains("validCode") && !uri.contains("getKey")) {
			LogUtils.info(this.getClass(), JSONObject.toJSONString(map));	
		}
		
		String params = "";
		if(null != wrapRequest) {
			if(null !=  wrapRequest.getBody()) {
				params = new String(wrapRequest.getBody(), Charset.forName("UTF-8"));
				if(!StrUtils.checkParam(params)) {
					params = "";
				}
			}
		}
		// 保存所有操作日志
		if(!"OPTIONS".equals(method) && !"GET".equals(method) && !uri.contains("login")) {
			try {
				Map<String, Object> logType = StrUtils.createMap("status", wrapResponse.getStatus());
				logType.put("method", method);
				logType.put("url", hrequest.getRequestURL().toString());
				opeLogBiz.save(uuid, JSONObject.toJSONString(logType), params, record, startTime);
			} catch (Exception e) {
				LogUtils.exception(getClass(), e);
			}
		}

		try {
			// 保存模块日志
			if (uriFilter(uri, method)){
				OperateLogEnum operateLogEnum = getLogType(uri, method);
				if (null != operateLogEnum){
					if (StrUtils.checkParam(record)){
						JSONObject json = JSONObject.parseObject(record);
						String result = json.getString("result");
						if (!StrUtils.checkParam(result)){
							result = "unknow";
						}
						String id = null;
						if (StrUtils.checkParam(request.getAttribute("id"))){
							id = request.getAttribute("id").toString();
						}
						String name = null;
						if (StrUtils.checkParam(request.getAttribute("name"))){
							name = request.getAttribute("name").toString();
						}
						operateLogsBiz.save(uuid, operateLogEnum, result, params, id, name);
						request.removeAttribute("id");
						request.removeAttribute("name");
					}
				}
			}
		} catch (Exception e) {
			LogUtils.exception(this.getClass(), e);
		}
		// 清空线程对象，防止线程池复用导致数据错误
		ThreadContext.clear();
	}

	@Override
	public void destroy() {

	}

	public boolean uriFilter(String uri, String method){
		boolean checkUri = false;
		boolean checkMethod = false;
		for (String s : filterUriList) {
			if (uri.contains(s)){
				checkUri = true;
				break;
			}
		}
		for (String s : filterMethodList) {
			if (method.equals(s)){
				checkMethod = true;
				break;
			}
		}
		return checkUri && checkMethod;
	}

	public OperateLogEnum getLogType(String uri, String method){
		StringBuffer type = new StringBuffer();
		for (String s : filterUriList) {
			if (uri.contains(s)){
				type.append(s.toUpperCase());
				break;
			}
		}
		type.append("_");
		for (String s : filterMethodList) {
			if (method.equals(s)){
				switch (method){
					case "POST":
						// 需追加内容
						if (uri.contains("vdcInfo")) {
							type.append("SAVE");
						} else if (uri.contains("unlink")) {
							type.append("UNLINK");
						} else if (uri.contains("link")) {
							type.append("LINK");
						} else if (uri.contains("dettach")) {
							type.append("DETTACH");
						} else if (uri.contains("attach")) {
							type.append("ATTACH");
						} else if (uri.contains("start")) {
							type.append("START");
						} else if (uri.contains("stop")) {
							type.append("STOP");
						} else if (uri.contains("reboot")) {
							type.append("REBOOT");
						} else if (uri.contains("console")) {
							type.append("CONSOLE");
						} else if (uri.contains("to-image")) {
                            type.append("TOIMAGE");
                        } else if (uri.contains("clone")) {
                            type.append("CLONE");
                        } else if (uri.contains("init/monitor")) {
                            type.append("INIT_MONITOR");
                        } else if (uri.contains("remove/monitor")) {
                            type.append("REMOVE_MONITOR");
                        } else {
							type.append("CREATE");
						}
						break;
					case "PUT":
						if(uri.contains("recycle") && uri.contains("volume")) {
							type.append("VOLUME_UPDATE");
						} else {
							type.append("UPDATE");
						}
						break;
					case "DELETE":
						if(uri.contains("recycle") && uri.contains("volume")) {
							type.append("VOLUME_DELETE");
						} else if (uri.contains("port") && uri.contains("os-interface")) {
							type.append("DETTACH");
						} else {
							type.append("DELETE");
						}
						break;
					default:break;
				}
				break;
			}
		}
		OperateLogEnum operateLogEnum = null;
		try {
			operateLogEnum = OperateLogEnum.valueOf(type.toString());
		} catch (Exception e) {
//			LogUtils.exception(getClass(), e, "Get operate log error, type:" + type);
		}
		return operateLogEnum;
	}

}

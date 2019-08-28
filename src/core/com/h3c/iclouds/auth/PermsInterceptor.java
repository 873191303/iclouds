package com.h3c.iclouds.auth;

import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.utils.SessionRedisUtils;
import com.h3c.iclouds.utils.StrUtils;
import com.h3c.iclouds.utils.ThreadContext;
import com.h3c.iclouds.utils.ValidCodeUtils;
import com.wordnik.swagger.annotations.ApiOperation;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

public class PermsInterceptor extends HandlerInterceptorAdapter {

	@SuppressWarnings("unchecked")
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		Object tokenObj = null;
		String tokenKey = request.getHeader(ConfigProperty.PROJECT_TOKEN_KEY);
		tokenKey = StrUtils.checkParam(tokenKey) ? tokenKey : request.getParameter("token");
		if (StrUtils.checkParam(tokenKey)){
			tokenObj = SessionRedisUtils.getValue(tokenKey);
			ThreadContext.set(ConfigProperty.PROJECT_TOKEN_KEY, tokenKey);
		}

		String uri = request.getRequestURI();
		// 是否包含可忽略过滤的内容
		if(StrUtils.checkIgonreURI(uri)) {
			return true;
		}

		// 是不为空，测试作用  StrUtils.checkParam(channelKey)
		if(tokenObj != null) {
			ThreadContext.set(tokenKey, tokenObj);
			if(tokenObj instanceof SessionBean) {
				SessionBean sessionBean = (SessionBean) tokenObj;
				String method = request.getMethod().toUpperCase();
				// 非GET请求需要对比token才允许通过
				if(CacheSingleton.getInstance().isFlagOpe() && !"GET".equals(method)) {
					String opeKey = request.getHeader(ConfigProperty.PROJECT_OPE_KEY);
					if(opeKey == null) {
						response.setStatus(601); // 未带token
						return false;
					}
					if(!ValidCodeUtils.equalCode(opeKey, ConfigProperty.SYSTEM_FLAG)) {
						response.setStatus(602); // token超时
						return false;
					}
				}
				if (handler instanceof HandlerMethod) {
					HandlerMethod handlerMethod = (HandlerMethod) handler;
					this.setMethodName(handlerMethod);
					if(checkPerms(sessionBean, handlerMethod)) {
						return true;
					}
					response.setStatus(403); // 无权限访问
					return false;
				}
			} else {
				Map<String, Object> abcMap = (Map<String, Object>) tokenObj;
				String token = StrUtils.tranString(abcMap.get(ConfigProperty.PROJECT_TOKEN_KEY));
				if(tokenKey.equals(token)) {
					if (handler instanceof HandlerMethod) {
						HandlerMethod handlerMethod = (HandlerMethod) handler;
						this.setMethodName(handlerMethod);
						Annotation[] annotations = handlerMethod.getBean().getClass().getAnnotations();
						if(annotations != null) {
							ThirdPart thirdPart = handlerMethod.getBean().getClass().getAnnotation(ThirdPart.class);
							if(thirdPart != null) {
								// TODO 后续需要做接口分类判断，防止异常请求
								return true;
							}
						}
						response.setStatus(403); // 无权限访问
						return false;
					}
					return true;
				} else {
					return false;
				}
			}
		}
		response.setStatus(401); // 未传递有效token
		return false;
	}

	private void setMethodName(HandlerMethod handlerMethod) {
		ApiOperation apiOpe = handlerMethod.getMethodAnnotation(ApiOperation.class);
		if(apiOpe != null) {
			String value = apiOpe.value();
			if(value != null) {
				ThreadContext.set("methodApi", value);
			}
		}
	}
	
	private boolean checkPerms(SessionBean sessionBean, HandlerMethod handlerMethod) {
		Method method = handlerMethod.getMethod();
		Perms annotation = method.getAnnotation(Perms.class);
		if(annotation != null) {
			String[] values = annotation.value();
			Set<String> set = sessionBean.getKeySet();
			if(values != null) {
				for (String value : values) {
					if(set.contains(value))
						return true;
				}
				return false;
			}
		}
		return true;
	}
	
}

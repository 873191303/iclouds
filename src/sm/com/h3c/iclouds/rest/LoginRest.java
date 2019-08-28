package com.h3c.iclouds.rest;

import com.h3c.iclouds.auth.SessionBean;
import com.h3c.iclouds.base.BaseDAO;
import com.h3c.iclouds.base.BaseRest;
import com.h3c.iclouds.base.BaseRestControl;
import com.h3c.iclouds.po.bean.TokenBean;
import com.h3c.iclouds.biz.OperateLogsBiz;
import com.h3c.iclouds.biz.ResourceBiz;
import com.h3c.iclouds.biz.UserBiz;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.exception.MessageException;
import com.h3c.iclouds.po.LogType;
import com.h3c.iclouds.po.Resource;
import com.h3c.iclouds.po.User;
import com.h3c.iclouds.utils.PwdUtils;
import com.h3c.iclouds.utils.SessionRedisUtils;
import com.h3c.iclouds.utils.StrUtils;
import com.h3c.iclouds.utils.ValidCodeUtils;
import com.h3c.iclouds.validate.ValidatorUtils;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/")
@Api(value = "用户认证", description = "获取凭证")
public class LoginRest extends BaseRest<User> {

	@javax.annotation.Resource
	private UserBiz userBiz;

	@javax.annotation.Resource
	private ResourceBiz resourceBiz;

	@javax.annotation.Resource
	private OperateLogsBiz operateLogsBiz;

	@javax.annotation.Resource(name = "baseDAO")
	public BaseDAO<LogType> logTypeDao;

	@ApiOperation(value = "获取操作token")
	@RequestMapping(value = "/ope/key", method = RequestMethod.GET)
	public Object ope_token() {
		String key = StrUtils.getUUID(ConfigProperty.REDIS_CODE_OPE_TOKEN);
		ValidCodeUtils.setCodeToRedis(key);
		return BaseRestControl.tranReturnValue(key);
	}

	@ApiOperation(value = "验证凭证")
	@RequestMapping(value = "/auth/check", method = RequestMethod.POST)
	public Object check() throws Exception {
		String tokenKey = ThreadContext.get(ConfigProperty.PROJECT_TOKEN_KEY);
		if(tokenKey == null) {
			return BaseRestControl.tranReturnValue(ResultType.not_found_token);
		}
		if (!SessionRedisUtils.existKey(tokenKey)) {
			return BaseRestControl.tranReturnValue(ResultType.failure);
		}
		return BaseRestControl.tranReturnValue(ResultType.success);
	}
	
	@ApiOperation(value = "使用uid换取token")
	@RequestMapping(value = "/v2/uid/token", method = RequestMethod.POST)
	public Object getToken(HttpServletResponse response, @RequestBody Map<String, String> map) throws Exception {
		String uid = map.get("uid");
		if(StrUtils.checkParam(uid) &&
				uid.startsWith(singleton.getConfigValue(ConfigProperty.IYUN_SSO_UID_PROFIX))) {	// 票据标识
			String ticket = (String) SessionRedisUtils.getValue(uid);
			if(null != ticket) {
				// 删除
				SessionRedisUtils.delValue(uid);    // uid只允许使用一次
				// 根据ticket获取token
				String token = singleton.getTicket2TokenMap().get(ticket);
				if(StrUtils.checkParam(token)) {
                    Object tokenObj = SessionRedisUtils.getValue(token);
                    if(null != tokenObj && tokenObj instanceof SessionBean) {
                        SessionBean sessionBean = (SessionBean) tokenObj;
                        Map<String, Object> resultMap = StrUtils.createMap("user", sessionBean.getUser());
                        List<Resource> resources = resourceBiz.getResourceBySessionBean(sessionBean);
                        resultMap.put("resources", resources);
                        resultMap.put("token", sessionBean.getToken());
                        resultMap.put("isSuperUser", sessionBean.getSuperUser());		// 是否为云管理员
                        resultMap.put("isSuperRole", sessionBean.getSuperRole());		// 是否为租户管理员
                        resultMap.put("isSelfAllowed", sessionBean.getSelfAllowed());	// 是否允许自助
                        response.addHeader(ConfigProperty.PROJECT_TOKEN_KEY, token);
                        return BaseRestControl.tranReturnValue(resultMap);
                    }
					return BaseRestControl.tranReturnValue(ResultType.failure);
				}
			}
		}
		return BaseRestControl.tranReturnValue(ResultType.not_found_token);
	}

    @ApiOperation(value = "获取用户可使用资源", response = Resource.class)
    @RequestMapping(value = "/v2/resources", method = RequestMethod.GET)
    public Object resources() {
        List<Resource> list = this.resourceBiz.getResourceBySessionBean(this.getSessionBean());
        return BaseRestControl.tranReturnValue(list);
    }

	/**
	 * 用户登录
	 * 
	 * @param response
	 * @param bean
	 * @return
	 * @throws Exception
	 */
	@ApiOperation(value = "获取凭证", response = User.class)
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public Object login(HttpServletResponse response, @RequestBody TokenBean bean) throws Exception {
		String remoteIp = BaseRest.getIpAddress(request);
		Map<String, String> validatorMap = ValidatorUtils.validator(bean);
		if (validatorMap.isEmpty()) {
			// TODO 设置开发模式,开发状态下不限制功能
//			if(!CacheSingleton.getInstance().isDevMode() && !ValidCodeUtils.equalCode(codestr, code)) {
//				return BaseRestControl.tranReturnValue(ResultType.login_code_error); // 验证码错误
//			}
			List<User> list = userBiz.findByPropertyName(User.class, "loginName", bean.getLoginName());
			if (!StrUtils.checkCollection(list)) {
                this.userBiz.saveLogin2Log(ResultType.user_status_error, null, remoteIp, bean.getLoginName());
				return BaseRestControl.tranReturnValue(ResultType.user_not_exist); // 用户名或密码错误
			}
			User user = list.get(0);
			if (operateLogsBiz.checkRestrictLogin(user.getId())) {
				return BaseRestControl.tranReturnValue(ResultType.pwd_error_too_many_times);
			}

			// 验证密码
			ResultType rt = checkUser(user, bean.getPassword());
			if (rt == ResultType.loginName_or_password_error) { // 检查密码错误次数是否过多
				this.userBiz.saveLogin2Log(rt, user.getId(), remoteIp, bean.getLoginName());
				if (operateLogsBiz.checkPwdError(user.getId())) {
					return BaseRestControl.tranReturnValue(ResultType.pwd_error_too_many_times);
				} else {
					return BaseRestControl.tranReturnValue(rt);
				}
			}

            try {
                SessionBean sessionBean = this.userBiz.createSessionBean(user, bean.getSysFlag(), remoteIp);
                response.addHeader(ConfigProperty.PROJECT_TOKEN_KEY, sessionBean.getToken());

                Map<String, Object> resultMap = StrUtils.createMap("user", user);
                resultMap.put("resources", sessionBean.getResources());
                resultMap.put("token", sessionBean.getToken());
                resultMap.put("isSuperUser", sessionBean.getSuperUser());		// 是否为云管理员
                resultMap.put("isSuperRole", sessionBean.getSuperRole());		// 是否为租户管理员
                resultMap.put("isSelfAllowed", sessionBean.getSelfAllowed());	// 是否允许自助
				resultMap.put("isMonitor", singleton.isMonitorSyn());           // 是否监控
                return BaseRestControl.tranReturnValue(resultMap);
            } catch (Exception e) {
                ResultType returnValue = ResultType.failure;
                this.exception(e, "Login user [" + user.getLoginName() + "][" + user.getId() +"] failure.");
                if(e instanceof MessageException) {
                    returnValue =((MessageException) e).getResultCode();
                }
                this.userBiz.saveLogin2Log(returnValue, user.getId(), remoteIp, bean.getLoginName());
                return BaseRestControl.tranReturnValue(returnValue);
            }
		}
        this.userBiz.saveLogin2Log(ResultType.parameter_error, null, remoteIp, bean.getLoginName());
		return BaseRestControl.tranReturnValue(ResultType.parameter_error); // 用户名或密码错误;
	}

	@ApiOperation(value = "注销凭证")
	@RequestMapping(value = "/logout", method = RequestMethod.POST)
	public Object logout() {
		this.info("User [" + this.getLoginUser() + "] logout");
		try {
			String token = this.getUserToken();
			if(token == null) {
				return BaseRestControl.tranReturnValue(ResultType.not_found_token);		
			}
            this.userBiz.logout(token, false);
		} catch (Exception e) {
			this.exception(clazz, e, "User logout failure!");
		}
		return BaseRestControl.tranReturnValue(ResultType.success);
	}
	
	@ApiOperation(value = "获取验证key")
	@RequestMapping(value = "/getKey", method = RequestMethod.GET)
	public Object key() throws IOException {
		String key = StrUtils.getUUID(ConfigProperty.REDIS_CODE_VERIFY);
		ValidCodeUtils.setCodeToRedis(key);
		return BaseRestControl.tranReturnValue(key);
	}
	
	@ApiOperation(value = "获取验证码")
	@RequestMapping(value = "/validCode/{key}", method = RequestMethod.GET)
	public void code(@PathVariable String key, HttpServletResponse response) throws IOException {
		if(!ValidCodeUtils.existKey(key)) {
			response.getOutputStream().write(ResultType.valid_key_error.toString().getBytes());
			return;
		}
		// 设置响应的类型格式为图片格式
		response.setContentType("image/jpeg");
		// 禁止图像缓存。
		response.setHeader("Pragma", "no-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);
		
		ValidCodeUtils codeUtils = ValidCodeUtils.create();
		ValidCodeUtils.setCodeToRedis(key, codeUtils.getCode());
		codeUtils.write(response.getOutputStream());
	}

    public static void main(String[] args) {
        try {
            System.out.println(PwdUtils.decrypt("6ol41LZdKo+HM3ICO4P5rQ==", "fygskyy8a48a2825d1ae01a015d1ae144950009"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	private ResultType checkUser(User user, String password) {
		try {
			String checkPassword = PwdUtils.encrypt(password, user.getLoginName() + user.getId());
			if (checkPassword.equals(user.getPassword())) {
				return ResultType.success;
			}
		} catch (Exception e) {
			this.exception(this.getClass(), e);
		}
		return ResultType.loginName_or_password_error;
	}

    @Override
	public Object get(String id) {
		return null;
	}

	@Override
	public Object list() {
		return null;
	}

	@Override
	public Object delete(String id) {
		return null;
	}

	@Override
	public Object save(User t) {
		return null;
	}

	@Override
	public Object update(String id, User t) {
		return null;
	}

}
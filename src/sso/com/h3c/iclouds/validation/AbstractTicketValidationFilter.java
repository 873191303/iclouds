/**
 * Licensed to Jasig under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Jasig licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a
 * copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.h3c.iclouds.validation;

import com.h3c.iclouds.auth.CacheSingleton;
import com.h3c.iclouds.auth.SessionBean;
import com.h3c.iclouds.base.BaseRest;
import com.h3c.iclouds.base.SpringContextHolder;
import com.h3c.iclouds.biz.UserBiz;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.exception.MessageException;
import com.h3c.iclouds.po.User;
import com.h3c.iclouds.utils.SessionRedisUtils;
import com.h3c.iclouds.utils.StrUtils;
import com.h3c.iclouds.utils.AbstractCasFilter;
import com.h3c.iclouds.utils.CommonUtils;
import com.h3c.iclouds.utils.ReflectUtils;

import javax.net.ssl.HostnameVerifier;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * The filter that handles all the work of validating ticket requests.
 * <p>
 * This filter can be configured with the following values:
 * <ul>
 * <li><code>redirectAfterValidation</code> - redirect the CAS client to the same URL without the ticket.</li>
 * <li><code>exceptionOnValidationFailure</code> - throw an exception if the validation fails.  Otherwise, continue
 *  processing.</li>
 * <li><code>useSession</code> - store any of the useful information in a session attribute.</li>
 * </ul>
 *
 * @author Scott Battaglia
 * @version $Revision$ $Date$
 * @since 3.1
 */
public abstract class AbstractTicketValidationFilter extends AbstractCasFilter {

    /** The TicketValidator we will use to validate tickets. */
    private TicketValidator ticketValidator;

    /**
     * Specify whether the filter should redirect the user agent after a
     * successful validation to remove the ticket parameter from the query
     * string.
     */
    private boolean redirectAfterValidation = false;

    /** Determines whether an exception is thrown when there is a ticket validation failure. */
    private boolean exceptionOnValidationFailure = true;

    private UserBiz userBiz = null;

    /**
     * Template method to return the appropriate validator.
     *
     * @param filterConfig the FilterConfiguration that may be needed to construct a validator.
     * @return the ticket validator.
     */
    protected TicketValidator getTicketValidator(final FilterConfig filterConfig) {
        return this.ticketValidator;
    }
   
    /**
     * Gets the configured {@link HostnameVerifier} to use for HTTPS connections
     * if one is configured for this filter.
     * @param filterConfig Servlet filter configuration.
     * @return Instance of specified host name verifier or null if none specified.
     */
    protected HostnameVerifier getHostnameVerifier(final FilterConfig filterConfig) {
        final String className = getPropertyFromInitParams(filterConfig, "hostnameVerifier", null);
        log.trace("Using hostnameVerifier parameter: " + className);
        final String config = getPropertyFromInitParams(filterConfig, "hostnameVerifierConfig", null);
        log.trace("Using hostnameVerifierConfig parameter: " + config);
        if (className != null) {
            if (config != null) {
                return ReflectUtils.newInstance(className, config);
            } else {
                return ReflectUtils.newInstance(className);
            }
        }
        return null;
    }

    protected void initInternal(final FilterConfig filterConfig) throws ServletException {
        setExceptionOnValidationFailure(parseBoolean(getPropertyFromInitParams(filterConfig, "exceptionOnValidationFailure", "true")));
        log.trace("Setting exceptionOnValidationFailure parameter: " + this.exceptionOnValidationFailure);
        setRedirectAfterValidation(parseBoolean(getPropertyFromInitParams(filterConfig, "redirectAfterValidation", "true")));
        log.trace("Setting redirectAfterValidation parameter: " + this.redirectAfterValidation);
        setTicketValidator(getTicketValidator(filterConfig));
        super.initInternal(filterConfig);
    }

    public void init() {
        super.init();
        CommonUtils.assertNotNull(this.ticketValidator, "ticketValidator cannot be null.");
    }

    /**
     * Pre-process the request before the normal filter process starts.  This could be useful for pre-empting code.
     *
     * @param servletRequest The servlet request.
     * @param servletResponse The servlet response.
     * @param filterChain the filter chain.
     * @return true if processing should continue, false otherwise.
     * @throws IOException if there is an I/O problem
     * @throws ServletException if there is a servlet problem.
     */
    protected boolean preFilter(final ServletRequest servletRequest, final ServletResponse servletResponse, final FilterChain filterChain) throws IOException, ServletException {
        return true;
    }

    /**
     * Template method that gets executed if validation fails.  This method is called right after the exception is caught from the ticket validator
     * but before any of the processing of the exception occurs.
     *
     * @param request the HttpServletRequest.
     * @param response the HttpServletResponse.
     */
    protected void onFailedValidation(final HttpServletRequest request, final HttpServletResponse response) {
        // nothing to do here.
    }

    /**
     * Template method that gets executed if ticket validation succeeds.  Override if you want additional behavior to occur
     * if ticket validation succeeds.  This method is called after all ValidationFilter processing required for a successful authentication
     * occurs.
     *
     * @param remoteIp the HttpServletRequest.
     * @param user the successful loginName from the server.
     * @param sysFlag the successful platform type from the server.
     * @param ticket the successful ticket from the server.
     */
    protected ResultType onSuccessfulValidation(String remoteIp, User user, String sysFlag, String ticket) {
        SessionBean sessionBean = this.userBiz.createSessionBean(user, sysFlag, remoteIp);
        Map<String, String> map = CacheSingleton.getInstance().getTicket2TokenMap();
        map.put(ticket, sessionBean.getToken());
        return ResultType.success;
    }

    public final void doFilter(final ServletRequest servletRequest, final ServletResponse servletResponse, final FilterChain filterChain) throws IOException, ServletException {
        if (!preFilter(servletRequest, servletResponse, filterChain)) {
            return;
        }
        String queryTokenUrl = "";
        final HttpServletRequest request = (HttpServletRequest) servletRequest;
        final HttpServletResponse response = (HttpServletResponse) servletResponse;
        final String ticket = CommonUtils.safeGetParameter(request, getArtifactParameterName());

        if (CommonUtils.isNotBlank(ticket)) {
            if (log.isDebugEnabled()) {
                log.debug("Attempting to validate ticket: " + ticket);
            }

            String remoteIp = BaseRest.getIpAddress(request);
            String name = null;
            String userId = null;
            if(null == userBiz) {
                userBiz = SpringContextHolder.getBean("userBiz");
            }
            try {
                final Assertion assertion = this.ticketValidator.validate(ticket, constructServiceUrl(request, response));
                if (log.isDebugEnabled()) {
                    log.debug("Successfully authenticated user: " + assertion.getPrincipal().getName());
                }
                name = assertion.getPrincipal().getName();

                String sysFlag = request.getParameter("sysFlag");

                if(ConfigProperty.SYS_FLAG_YUNWEI.equals(sysFlag)) {    // 运营平台
                    queryTokenUrl = CacheSingleton.getInstance().getConfigValue("iyun.sso.redirect.yunying");
                } else {    // 用户平台
                    queryTokenUrl = CacheSingleton.getInstance().getConfigValue("iyun.sso.redirect.yonghu");
                    // 是否需要根据地址栏重写
                    if(!this.isIndependent()) {
                        String resultUrl = CommonUtils.getUrlByRequest(request);
                        if(resultUrl != null) {
                            queryTokenUrl = resultUrl + CacheSingleton.getInstance().getConfigValue("iyun.sso.redirect.yonghu.profix");
                        }
                    }
                }

                User user = userBiz.singleByClass(User.class, StrUtils.createMap("loginName", name));
                if(user == null) {
                    response.sendRedirect(queryTokenUrl + "?fr=" + ResultType.user_not_grant.toString());
                    return;
                }
                userId = user.getId();
                ResultType resultType = onSuccessfulValidation(remoteIp, user, sysFlag, ticket);

                // 在行业云登录失败，显示失败原因
                if(resultType != ResultType.success) {
                    response.sendRedirect(queryTokenUrl + "?fr=" + resultType.toString());
                    return;
                }
                String uid = request.getParameter("uid");
                SessionRedisUtils.setValue2Redis(uid, ticket, 5000);	// 设置映射
                response.sendRedirect(queryTokenUrl + "?uid=" + uid);
            } catch (final TicketValidationException e) {
                this.userBiz.saveLogin2Log(ResultType.sso_login_failure, userId, remoteIp, name);
                response.sendRedirect(queryTokenUrl + "?fr=" + ResultType.sso_login_failure.toString());
            } catch (final MessageException e) {
                this.userBiz.saveLogin2Log(e.getResultCode(), userId, remoteIp, name);
                response.sendRedirect(queryTokenUrl + "?fr=" + e.getResultCode().toString());
            } catch  (final Exception e) {
                this.userBiz.saveLogin2Log(ResultType.failure, userId, remoteIp, name);
                response.sendRedirect(queryTokenUrl + "?fr=" + ResultType.failure);
            }
        }
        // 不进行下一个过滤
        //filterChain.doFilter(request, response);
    }

    public final void setTicketValidator(final TicketValidator ticketValidator) {
    this.ticketValidator = ticketValidator;
}

    public final void setRedirectAfterValidation(final boolean redirectAfterValidation) {
        this.redirectAfterValidation = redirectAfterValidation;
    }

    public final void setExceptionOnValidationFailure(final boolean exceptionOnValidationFailure) {
        this.exceptionOnValidationFailure = exceptionOnValidationFailure;
    }

}

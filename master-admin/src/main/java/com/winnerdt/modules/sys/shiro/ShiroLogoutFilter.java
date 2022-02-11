package com.winnerdt.modules.sys.shiro;


import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.LogoutFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * @author:zsk
 * @CreateTime:2019-02-18 11:09
 */
public class ShiroLogoutFilter extends LogoutFilter {
    private static Logger logger = LoggerFactory.getLogger(ShiroLogoutFilter.class);

    @Override
    protected boolean preHandle (ServletRequest request, ServletResponse response) throws Exception {
        /*
        * 这里只需要一个登出操作
        * */
        //return super.preHandle (request, response);
        Subject subject = getSubject(request, response);
//        String redirectUrl = getRedirectUrl(request, response, subject);
//        HttpServletRequest req = (HttpServletRequest) request;
//        String clientType =  req.getSession().getAttribute ("isPhone")+"";//在前面登陆的过滤器中存储的数据，用来分辨是不是app登陆
        subject.logout();
//        logger.debug("[LOGIN]logout success( client:" + clientType + ")");
//        if(!"null".equals (clientType) && null!=clientType) { // 请求方为pc，执行原方法
//            issueRedirect(request, response, "/app/login");
//
//        }else{
//            issueRedirect(request, response, "/login");
//        }
        return false;
    }
}
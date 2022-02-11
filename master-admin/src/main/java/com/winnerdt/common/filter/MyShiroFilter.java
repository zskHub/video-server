package com.winnerdt.common.filter;


import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.apache.shiro.web.util.WebUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author zsk
 * @date 2017/12/11.
 *
 * 拦截器 校验用户是否已授权 未授权返回到登录界面
 */
public class MyShiroFilter extends FormAuthenticationFilter {


  @Override
  protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
    if (request instanceof HttpServletRequest) {
      if (((HttpServletRequest) request).getMethod().toUpperCase().equals("OPTIONS")) {
        return true;
      }
    }
    return super.isAccessAllowed(request, response, mappedValue);
  }





  @Override
  protected boolean onAccessDenied(ServletRequest request, ServletResponse response)
          throws Exception {
    WebUtils.toHttp(response).sendError(HttpServletResponse.SC_UNAUTHORIZED);
    return false;
  }

}

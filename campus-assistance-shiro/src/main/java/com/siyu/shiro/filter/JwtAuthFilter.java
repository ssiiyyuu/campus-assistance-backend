package com.siyu.shiro.filter;

import com.siyu.common.domain.R;
import com.siyu.common.enums.ErrorStatus;
import com.siyu.common.exception.BusinessException;
import com.siyu.shiro.entity.ShiroUser;
import com.siyu.shiro.token.JwtToken;
import com.siyu.common.utils.JwtUtils;
import com.siyu.common.utils.WebUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.AuthenticatingFilter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;


public class JwtAuthFilter extends AuthenticatingFilter {

    /**
     * executeLogin()中调用
     */
    @Override
    protected AuthenticationToken createToken(ServletRequest request, ServletResponse response) {
        String token = JwtUtils.getToken(request);
        return token == null ? null : new JwtToken(token);
    }

    /**
     * 请求进入拦截器后调用该方法, 返回true则继续, 返回false则调用onAccessDenied()。
     */
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        //不拦截登录请求
        if(this.isLoginRequest(request, response)) {
            return true;
        }
        boolean flag = false;
        try {
            flag = executeLogin(request, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag || super.isPermissive(mappedValue);

    }

    /**
     * 如果creatToken()返回null即无法从header中获取token,抛出异常交isAccessAllowed()处理
     */
    @Override
    protected boolean executeLogin(ServletRequest request, ServletResponse response) {
        JwtToken token = (JwtToken) createToken(request, response);
        if(token == null) {
            throw new BusinessException(ErrorStatus.TOKEN_ERROR);
        }
        try {
            Subject subject = SecurityUtils.getSubject();
            subject.login(token); //这里会根据token的类型找到对应的Realm
            return onLoginSuccess(token, subject, request, response);
        } catch (AuthenticationException e) {
            return onLoginFailure(token, e, request, response);
        }
    }

    /**
     *  executeLogin()中验证成功,则进入这个方法
     *  此方法实现token无感刷新
     */
    @Override
    protected boolean onLoginSuccess(AuthenticationToken token, Subject subject, ServletRequest request, ServletResponse response) {
        if(token instanceof JwtToken) {
            ShiroUser user = (ShiroUser) subject.getPrincipal();
            if(JwtUtils.needRefresh(((JwtToken) token).getToken())) {
                //重新签发token
                WebUtils.setHeader(WebUtils.AUTHENTICATION_HEADER, JwtUtils.generateToken(user), (HttpServletResponse) response);
            }
        }
        return true;
    }

    /**
     *  executeLogin()中验证成功失败,则会进入这个方法
     */
    @Override
    protected boolean onLoginFailure(AuthenticationToken token, AuthenticationException e, ServletRequest request, ServletResponse response) {
        return false;
    }

    /**
     * isAccessAllowed()返回false后调用此方法
     */
    @Override
    protected boolean onAccessDenied(ServletRequest servletRequest, ServletResponse servletResponse) throws Exception {
        WebUtils.writeInResponse(R.fail(ErrorStatus.TOKEN_ERROR), (HttpServletResponse) servletResponse);
        return false;
    }
}

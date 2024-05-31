package com.siyu.shiro.filter;

import com.siyu.common.domain.R;
import com.siyu.common.enums.ErrorStatus;
import com.siyu.common.utils.WebUtils;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authz.AuthorizationFilter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class RoleAuthFilter extends AuthorizationFilter {
    @Override
    protected boolean isAccessAllowed(ServletRequest servletRequest, ServletResponse servletResponse, Object o) throws Exception {
        Subject subject = getSubject(servletRequest, servletResponse);
        String[] roles = (String[]) o;
        if(roles == null || roles.length == 0) {
            return true;
        }
        for(String role : roles) {
            if(subject.hasRole(role)) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws IOException {
        try {
            WebUtils.write2Response(R.fail(ErrorStatus.AUTHOR_ERROR), (HttpServletResponse) response);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}

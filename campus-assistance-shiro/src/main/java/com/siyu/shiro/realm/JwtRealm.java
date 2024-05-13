package com.siyu.shiro.realm;

import com.siyu.common.domain.entity.SysPermission;
import com.siyu.common.domain.entity.SysRole;
import com.siyu.common.enums.ErrorStatus;
import com.siyu.common.exception.BusinessException;
import com.siyu.shiro.entity.ShiroRole;
import com.siyu.shiro.entity.ShiroUser;
import com.siyu.shiro.token.JwtToken;
import com.siyu.common.utils.JwtUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

import java.util.stream.Collectors;


public class JwtRealm extends AuthorizingRealm {

    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof JwtToken;
    }

    /**
     * 授权前调用此方法
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
        ShiroUser user =  (ShiroUser) principalCollection.getPrimaryPrincipal();
        simpleAuthorizationInfo.addRoles(user.getRoles().stream().map(ShiroRole::getRoleCode).collect(Collectors.toList()));
        return simpleAuthorizationInfo;
    }

    /**
     * subject.login()调用此方法验证
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        String token = ((JwtToken) authenticationToken).getToken();
        ShiroUser user = JwtUtils.parseToken(token, ShiroUser.class);
        return new SimpleAuthenticationInfo(user, token, "jwtRealm");

    }
}

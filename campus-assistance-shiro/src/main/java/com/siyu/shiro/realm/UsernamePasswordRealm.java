package com.siyu.shiro.realm;

import com.siyu.common.constants.GlobalConstants;
import com.siyu.common.utils.BeanUtils;
import com.siyu.shiro.entity.ShiroUser;
import com.siyu.shiro.service.ShiroService;
import org.apache.shiro.authc.*;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;

import javax.annotation.Resource;

public class UsernamePasswordRealm extends AuthorizingRealm {
    @Resource
    private ShiroService shiroService;

    public UsernamePasswordRealm() {
        this.setCredentialsMatcher(new HashedCredentialsMatcher(Md5Hash.ALGORITHM_NAME));
    }

    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof UsernamePasswordToken;
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        return new SimpleAuthorizationInfo();
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        UsernamePasswordToken token = (UsernamePasswordToken) authenticationToken;
        String username = token.getUsername();
        ShiroUser user = shiroService.getShiroUserByUsername(username);

        if(user == null) {
            throw new AuthenticationException("用户名或密码错误");
        }
        if(user.getStatus().equals(0)) {
            throw new AuthenticationException("账号已禁用");
        }

        //交给Shiro管理的HashedCredentialsMatcher进行校验
        return new SimpleAuthenticationInfo(user,
                user.getPassword(),
                ByteSource.Util.bytes(GlobalConstants.USER_SALT),
                "usernamePasswordRealm");
    }
}

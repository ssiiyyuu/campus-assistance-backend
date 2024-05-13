package com.siyu.server.controller;

import com.siyu.common.domain.R;
import com.siyu.common.utils.WebUtils;
import com.siyu.shiro.entity.ShiroLogin;
import com.siyu.shiro.entity.ShiroUser;
import com.siyu.shiro.service.ShiroService;
import com.siyu.shiro.utils.ShiroUtils;
import io.swagger.annotations.Api;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Api("用户基础服务")
@RestController
public class ShiroController {

    @Autowired
    private ShiroService shiroService;

    @PostMapping("/login")
    public R<String> login(@RequestBody ShiroLogin in) {
        String token = shiroService.login(in);
        WebUtils.setHeader(WebUtils.AUTHENTICATION_HEADER, token);
        return R.ok("登录成功");
    }

    @GetMapping("/info")
    public R<ShiroUser> info() {
        ShiroUser user = ShiroUtils.getCurrentUser();
        return R.ok(user);
    }

    @PostMapping("/logout")
    public R<String> logout() {
        Subject subject = SecurityUtils.getSubject();
        subject.logout();
        return R.ok("登出成功");
    }
}

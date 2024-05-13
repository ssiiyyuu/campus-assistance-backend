package com.siyu.shiro.utils;

import com.siyu.shiro.entity.ShiroUser;
import org.apache.shiro.SecurityUtils;

public class ShiroUtils {

    public static ShiroUser getCurrentUser() {
        ShiroUser principal = (ShiroUser) SecurityUtils.getSubject().getPrincipal();
        return principal;
    }
}

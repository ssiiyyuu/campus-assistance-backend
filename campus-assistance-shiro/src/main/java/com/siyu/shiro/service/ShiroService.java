package com.siyu.shiro.service;


import com.siyu.common.domain.dto.*;
import com.siyu.common.utils.JwtUtils;
import com.siyu.shiro.mapper.ShiroMapper;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ShiroService {

    @Autowired
    ShiroMapper shiroMapper;

    public ShiroDepartment getDepartmentByDepartmentCode(String departmentCode) {return shiroMapper.selectDepartmentByDepartmentCode(departmentCode);}

    public List<ShiroPermission> getPermissionsByRoleIds(List<String> roleIds) {return shiroMapper.selectPermissionsByRoleIds(roleIds);}

    public List<ShiroRole> getRolesByUserId(String userId) {return shiroMapper.selectRolesByUserId(userId);}

    public ShiroUser getShiroUserByUsername(String username) {
        return shiroMapper.selectShiroUserByUsername(username);
    }

    public String login(ShiroLogin in) {
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken usernamePasswordToken = new UsernamePasswordToken(in.getUsername(), in.getPassword());

        //进入UsernamePasswordRealm的doGetAuthenticationInfo()方法
        subject.login(usernamePasswordToken);
        ShiroUser user = (ShiroUser) subject.getPrincipal();

        ShiroDepartment department = getDepartmentByDepartmentCode(user.getDepartmentCode());
        List<ShiroRole> roles = getRolesByUserId(user.getId());
        List<ShiroPermission> permissions = getPermissionsByRoleIds(roles.stream().map(ShiroRole::getId).collect(Collectors.toList()));
        user.setPassword(null);
        user.setDepartment(department);
        user.setRoles(roles);
        user.setPermissions(permissions);
        return JwtUtils.generateToken(user);
    }

}

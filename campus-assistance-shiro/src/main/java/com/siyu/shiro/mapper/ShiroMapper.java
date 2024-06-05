package com.siyu.shiro.mapper;

import com.siyu.shiro.entity.ShiroDepartment;
import com.siyu.shiro.entity.ShiroPermission;
import com.siyu.shiro.entity.ShiroRole;
import com.siyu.shiro.entity.ShiroUser;

import java.util.List;

public interface ShiroMapper {

    ShiroUser selectShiroUserByUsername(String username);

    List<ShiroRole> selectRolesByUserId(String userId);

    List<ShiroPermission> selectPermissionsByRoleIds(List<String> roleIds);

    ShiroDepartment selectDepartmentByDepartmentCode(String departmentCode);
}

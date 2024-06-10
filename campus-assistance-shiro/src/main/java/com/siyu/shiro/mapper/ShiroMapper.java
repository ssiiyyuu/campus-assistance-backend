package com.siyu.shiro.mapper;

import com.siyu.common.domain.dto.ShiroDepartment;
import com.siyu.common.domain.dto.ShiroPermission;
import com.siyu.common.domain.dto.ShiroRole;
import com.siyu.common.domain.dto.ShiroUser;

import java.util.List;

public interface ShiroMapper {

    ShiroUser selectShiroUserByUsername(String username);

    List<ShiroRole> selectRolesByUserId(String userId);

    List<ShiroPermission> selectPermissionsByRoleIds(List<String> roleIds);

    ShiroDepartment selectDepartmentByDepartmentCode(String departmentCode);
}

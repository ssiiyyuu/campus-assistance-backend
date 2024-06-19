package com.siyu.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.siyu.common.domain.entity.SysUser;
import com.siyu.common.domain.dto.SysUserBaseDTO;

import java.util.List;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author MybatisPlusGenerator
 * @since 2024-05-09 02:02:08
 */
public interface SysUserService extends IService<SysUser> {

    List<SysUser> getByCodeAndRoleId(String departmentCode, String roleId);

    List<SysUser> getUserByCodeAndRoleName(String departmentCode, String roleName);

    List<SysUser> getByPrefixCodeAndRoleId(String departmentCode, String roleId);

    List<SysUser> getUserByPrefixCodeAndRoleName(String departmentCode, String roleName);

    SysUserBaseDTO getBaseUserById(String userId);

    /**
     * 判断是否处于统一department
     * @param currentDepartmentCode
     * @param targetUserId
     * @return
     */
    boolean belongTheSameDepartment(String currentDepartmentCode, String targetUserId);

    /**
     * 判断是否 [currentDepartmentLevel > targetUserDepartmentLevel]
     * @param currentDepartmentCode
     * @param targetUserId
     * @return
     */
    boolean isParentDepartment(String currentDepartmentCode, String targetUserId);

}

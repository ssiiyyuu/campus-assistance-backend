package com.siyu.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.siyu.common.domain.entity.SysRole;
import com.siyu.common.domain.entity.SysUser;
import com.siyu.common.domain.dto.SysUserBaseDTO;
import com.siyu.common.mapper.SysRoleMapper;
import com.siyu.common.mapper.SysUserMapper;
import com.siyu.common.service.SysDepartmentService;
import com.siyu.common.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author MybatisPlusGenerator
 * @since 2024-05-09 02:02:08
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private SysRoleMapper sysRoleMapper;

    @Override
    public List<SysUser> getByCodeAndRoleId(String departmentCode, String roleId) {
        return sysUserMapper.selectByCodeAndRoleId(departmentCode, roleId);
    }

    @Override
    public List<SysUser> getUserByCodeAndRoleName(String departmentCode, String roleName) {
        String roleId = sysRoleMapper.selectOne(new LambdaQueryWrapper<SysRole>()
                .eq(SysRole::getRoleName, roleName)).getId();
        return getByCodeAndRoleId(departmentCode, roleId);
    }

    @Override
    public SysUserBaseDTO getBaseUserById(String userId) {
        return sysUserMapper.selectBaseUserById(userId);
    }

    @Override
    public boolean belongTheSameDepartment(String currentDepartmentCode, String targetUserId) {
        SysUser user = sysUserMapper.selectById(targetUserId);
        return user != null && currentDepartmentCode.equals(user.getDepartmentCode());
    }

    @Override
    public boolean isParentDepartment(String currentDepartmentCode, String targetUserId) {
        SysUser user = sysUserMapper.selectById(targetUserId);
        return user != null && SysDepartmentService.isParentDepartment(currentDepartmentCode, user.getDepartmentCode());
    }
}

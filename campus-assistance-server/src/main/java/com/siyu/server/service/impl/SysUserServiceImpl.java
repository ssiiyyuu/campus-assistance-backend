package com.siyu.server.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.siyu.common.domain.entity.SysUser;
import com.siyu.server.entity.dto.SysUserBaseDTO;
import com.siyu.server.mapper.SysUserMapper;
import com.siyu.server.service.SysUserService;
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

    @Override
    public List<SysUser> getByCodeAndRoleId(String departmentCode, String roleId) {
        return sysUserMapper.selectByCodeAndRoleId(departmentCode, roleId);
    }

    @Override
    public SysUserBaseDTO getBaseUserById(String userId) {
        return sysUserMapper.selectBaseUserById(userId);
    }
}

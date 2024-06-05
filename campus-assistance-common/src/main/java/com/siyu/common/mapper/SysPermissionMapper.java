package com.siyu.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.siyu.common.domain.entity.SysPermission;

import java.util.List;

/**
 * <p>
 * 权限 Mapper 接口
 * </p>
 *
 * @author MybatisPlusGenerator
 * @since 2024-05-09 02:06:23
 */
public interface SysPermissionMapper extends BaseMapper<SysPermission> {

    List<SysPermission> selectPermissionsByRoleId(String roleId);
}

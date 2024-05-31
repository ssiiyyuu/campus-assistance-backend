package com.siyu.server.mapper;

import com.siyu.common.domain.entity.SysRole;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.siyu.server.entity.dto.SysRoleBaseDTO;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author MybatisPlusGenerator
 * @since 2024-05-09 02:06:00
 */
public interface SysRoleMapper extends BaseMapper<SysRole> {

    List<SysRole> selectRolesByUserId(String id);
    List<SysRoleBaseDTO> selectBaseRolesByUserId(String id);
}

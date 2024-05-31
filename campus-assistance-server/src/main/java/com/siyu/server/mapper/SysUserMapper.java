package com.siyu.server.mapper;

import com.siyu.common.domain.entity.SysUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.siyu.server.entity.dto.SysUserBaseDTO;
import com.siyu.server.entity.vo.SysUserVO;

import java.util.List;

/**
 * <p>
 * 用户表 Mapper 接口
 * </p>
 *
 * @author MybatisPlusGenerator
 * @since 2024-05-09 02:02:08
 */
public interface SysUserMapper extends BaseMapper<SysUser> {

    List<SysUser> selectByCodeAndRoleId(String departmentCode, String roleId);

    SysUserBaseDTO selectBaseUserById(String userId);
}

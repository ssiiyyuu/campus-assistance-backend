package com.siyu.server.service;

import com.siyu.common.domain.entity.SysUser;
import com.baomidou.mybatisplus.extension.service.IService;
import com.siyu.server.entity.dto.SysUserBaseDTO;
import com.siyu.server.entity.vo.SysUserVO;

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

    SysUserBaseDTO getBaseUserById(String userId);
}

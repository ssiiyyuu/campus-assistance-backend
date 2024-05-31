package com.siyu.server.service;

import com.siyu.common.domain.entity.SysRole;
import com.baomidou.mybatisplus.extension.service.IService;
import com.siyu.server.entity.vo.SysRoleVO;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author MybatisPlusGenerator
 * @since 2024-05-09 02:06:00
 */
public interface SysRoleService extends IService<SysRole> {

    List<SysRoleVO.Assign> getAssignRoles(String userId);

    void assignRoles(String userId, List<String> roleIds);

    SysRole getByName(String name);
}

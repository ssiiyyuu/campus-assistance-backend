package com.siyu.server.service;

import com.siyu.common.domain.entity.SysPermission;
import com.baomidou.mybatisplus.extension.service.IService;
import com.siyu.server.entity.vo.SysPermissionVO;

import java.util.List;

/**
 * <p>
 * 权限 服务类
 * </p>
 *
 * @author MybatisPlusGenerator
 * @since 2024-05-09 02:06:23
 */
public interface SysPermissionService extends IService<SysPermission> {

    void deleteTree(String id);

    void assignPermissions(String roleId, List<String> permissionIds);

    List<SysPermissionVO.Tree> getAssignPermissions(String roleId);
}

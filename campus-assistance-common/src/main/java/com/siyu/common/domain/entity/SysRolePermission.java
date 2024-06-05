package com.siyu.common.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 角色权限
 * </p>
 *
 * @author MybatisPlusGenerator
 * @since 2024-05-11 11:23:21
 */
@Getter
@Setter
@TableName("sys_role_permission")
@ApiModel(value = "SysRolePermission对象", description = "角色权限")
public class SysRolePermission {

    private String roleId;

    private String permissionId;
}

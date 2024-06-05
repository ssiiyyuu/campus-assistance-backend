package com.siyu.common.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 
 * </p>
 *
 * @author MybatisPlusGenerator
 * @since 2024-05-11 11:23:04
 */
@Getter
@Setter
@TableName("sys_user_role")
@ApiModel(value = "SysUserRole对象", description = "")
public class SysUserRole {

    private String roleId;

    private String userId;
}

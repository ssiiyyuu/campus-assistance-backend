package com.siyu.common.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.siyu.common.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 用户表
 * </p>
 *
 * @author MybatisPlusGenerator
 * @since 2024-05-09 02:02:08
 */
@Getter
@Setter
@TableName("sys_user")
@ApiModel(value = "SysUser对象", description = "用户表")
public class SysUser extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("用户名")
    private String username;

    @ApiModelProperty("密码")
    private String password;

    @ApiModelProperty("昵称")
    private String nickname;

    @ApiModelProperty("用户头像")
    private String avatar;

    @ApiModelProperty("用户邮箱")
    private String email;

    @ApiModelProperty("用户类型")
    private String userType;

    @ApiModelProperty("部门编码")
    private String departmentCode;

    @ApiModelProperty("状态(0:禁止,1:正常)")
    private Integer status;
}

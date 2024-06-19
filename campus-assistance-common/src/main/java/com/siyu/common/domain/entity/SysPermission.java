package com.siyu.common.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.siyu.common.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 权限
 * </p>
 *
 * @author MybatisPlusGenerator
 * @since 2024-05-09 02:06:23
 */
@Getter
@Setter
@TableName("sys_permission")
@ApiModel(value = "SysPermission对象", description = "权限")
public class SysPermission extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("所属上级")
    private String parentId;

    @ApiModelProperty("名称")
    private String name;

    @ApiModelProperty("类型(1:菜单,2:按钮)")
    private Integer type;

    @ApiModelProperty("权限值")
    private String permissionValue;

    @ApiModelProperty("访问路径")
    private String path;

    @ApiModelProperty("组件路径")
    private String component;

    @ApiModelProperty("图标")
    private String icon;

    @ApiModelProperty("状态(0:禁止,1:正常)")
    private Integer status;
}

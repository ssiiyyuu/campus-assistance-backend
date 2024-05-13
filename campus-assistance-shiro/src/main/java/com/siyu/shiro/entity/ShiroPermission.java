package com.siyu.shiro.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ShiroPermission {
    @ApiModelProperty("id")
    private String id;

    @ApiModelProperty("名称")
    private String name;

    @ApiModelProperty("权限值")
    private String permissionValue;
}

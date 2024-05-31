package com.siyu.shiro.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ShiroLogin {

    @ApiModelProperty(example = "siyu")
    private String username;

    @ApiModelProperty(example = "123456")
    private String password;

}

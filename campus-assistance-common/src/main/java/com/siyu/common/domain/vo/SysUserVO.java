package com.siyu.common.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

public interface SysUserVO {
    @Data
    @ApiModel("in")
    class In {
        @NotBlank
        @ApiModelProperty("用户名")
        private String username;

        @ApiModelProperty("密码")
        private String password;

        @NotBlank
        @ApiModelProperty("昵称")
        private String nickname;

        @ApiModelProperty("用户头像")
        private String avatar;

        @ApiModelProperty("用户邮箱")
        private String email;

        @ApiModelProperty("状态(0:禁止,1:正常)")
        private Integer status;

        @NotBlank
        @ApiModelProperty("用户类型")
        private String userType;

        @NotBlank
        @ApiModelProperty("部门编码")
        private String departmentCode;
    }

    @Data
    @ApiModel("out")
    class Out {
        @ApiModelProperty("id")
        private String id;

        @ApiModelProperty("用户名")
        private String username;

        @ApiModelProperty("昵称")
        private String nickname;

        @ApiModelProperty("用户头像")
        private String avatar;

        @ApiModelProperty("用户邮箱")
        private String email;

        @ApiModelProperty("用户类型")
        private String userType;

        @ApiModelProperty("状态(0:禁止,1:正常)")
        private Integer status;

        @ApiModelProperty("部门编码")
        private String departmentCode;
    }

    @Data
    @ApiModel("condition")
    class Condition {
        @ApiModelProperty("用户名")
        private String username;

        @ApiModelProperty("用户类型")
        private String userType;

        @ApiModelProperty("状态(0:禁止,1:正常)")
        private Integer status;
    }

    @Data
    @ApiModel("table")
    class Table {
        @ApiModelProperty("id")
        private String id;

        @ApiModelProperty("用户名")
        private String username;

        @ApiModelProperty("昵称")
        private String nickname;

        @ApiModelProperty("用户头像")
        private String avatar;

        @ApiModelProperty("用户邮箱")
        private String email;

        @ApiModelProperty("用户类型")
        private String userType;

        @ApiModelProperty("状态(0:禁止,1:正常)")
        private Integer status;

        @ApiModelProperty("部门编码")
        private String departmentCode;
    }
}

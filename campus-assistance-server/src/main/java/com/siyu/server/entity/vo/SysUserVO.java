package com.siyu.server.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

public interface SysUserVO {
    @Data
    @ApiModel("in")
    class In {
        @ApiModelProperty(value = "id", hidden = true)
        private String id;

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

        @ApiModelProperty("部门编码")
        private String departmentCode;
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

        @ApiModelProperty("部门编码")
        private String departmentCode;
    }
}

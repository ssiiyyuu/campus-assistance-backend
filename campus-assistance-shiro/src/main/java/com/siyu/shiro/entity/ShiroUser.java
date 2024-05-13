package com.siyu.shiro.entity;

import com.siyu.common.domain.entity.SysDepartment;
import com.siyu.common.domain.entity.SysPermission;
import com.siyu.common.domain.entity.SysRole;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

@Data
public class ShiroUser implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;

    private String username;

    private String password;

    private String nickname;

    private String avatar;

    private String email;

    private String userType;

    private String departmentCode;

    private Integer status;

    private ShiroDepartment department;

    private List<ShiroRole> roles;

    private List<ShiroPermission> permissions;
}

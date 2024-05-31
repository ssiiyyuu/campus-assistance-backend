package com.siyu.server.entity.dto;

import lombok.Data;

@Data
public class SysUserBaseDTO {

    private String id;

    private String nickname;

    private SysDepartmentBaseDTO department;
}

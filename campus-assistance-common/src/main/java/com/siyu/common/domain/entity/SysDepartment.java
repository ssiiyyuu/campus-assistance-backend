package com.siyu.common.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.siyu.common.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 
 * </p>
 *
 * @author MybatisPlusGenerator
 * @since 2024-05-09 02:06:57
 */
@Getter
@Setter
@TableName("sys_department")
@ApiModel(value = "SysDepartment对象", description = "")
public class SysDepartment extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private String parentId;

    private String name;

    private String remark;

    private Integer level;

    private String code;
}

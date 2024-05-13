package com.siyu.server.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.siyu.common.domain.BaseEntity;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 
 * </p>
 *
 * @author MybatisPlusGenerator
 * @since 2024-05-11 09:00:20
 */
@Getter
@Setter
@ApiModel(value = "Category对象", description = "")
public class Category extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private String parentId;

    private String name;

    private String remark;
}

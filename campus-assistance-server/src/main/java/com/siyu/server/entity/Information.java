package com.siyu.server.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.siyu.common.domain.BaseEntity;
import java.io.Serializable;
import java.time.LocalDateTime;
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
 * @since 2024-05-11 08:55:29
 */
@Getter
@Setter
@ApiModel(value = "Information对象", description = "")
public class Information extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private String categoryId;

    private String title;

    private String cover;

    private String content;

    private Integer visits;

    private String status;

    private String authorId;

    private String departmentCode;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", locale = "zh", timezone = "GMT+8")
    private LocalDateTime publishTime;
}

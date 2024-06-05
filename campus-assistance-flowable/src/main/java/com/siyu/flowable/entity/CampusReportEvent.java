package com.siyu.flowable.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.siyu.common.domain.BaseEntity;
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
 * @since 2024-05-28 11:18:13
 */
@Getter
@Setter
@TableName("campus_report_event")
@ApiModel(value = "CampusReportEvent对象", description = "")
public class CampusReportEvent extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private String parentId;

    @ApiModelProperty("校园上报事件名")
    private String name;

    @ApiModelProperty("校园上报事件level")
    private String level;
}

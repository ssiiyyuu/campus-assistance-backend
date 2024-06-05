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
 * @since 2024-05-31 08:37:04
 */
@Getter
@Setter
@ApiModel(value = "Notification对象", description = "")
public class Notification extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("消息类型(SYSTEM，ADMIN，COUNSELOR)")
    private String type;

    @ApiModelProperty("消息来自于")
    private String from;

    @ApiModelProperty("消息传递去(若为'0'则广播到所有)")
    private String to;

    @ApiModelProperty("消息内容")
    private String content;
}

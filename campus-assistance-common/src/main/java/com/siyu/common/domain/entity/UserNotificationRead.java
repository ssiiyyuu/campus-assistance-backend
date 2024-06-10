package com.siyu.common.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author MybatisPlusGenerator
 * @since 2024-06-01 10:01:01
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("user_notification_read")
@ApiModel(value = "UserNotificationRead对象", description = "")
public class UserNotificationRead implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("用户id")
    private String userId;

    @ApiModelProperty("通知id")
    private String notificationId;
}

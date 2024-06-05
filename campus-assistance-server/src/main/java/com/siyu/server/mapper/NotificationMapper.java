package com.siyu.server.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.siyu.server.entity.Notification;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author MybatisPlusGenerator
 * @since 2024-05-31 08:37:04
 */
public interface NotificationMapper extends BaseMapper<Notification> {

    Notification loadById(String id);

    Page<Notification> pageWithName(Page<Notification> page, @Param(Constants.WRAPPER) LambdaQueryWrapper<Notification> wrapper);
}

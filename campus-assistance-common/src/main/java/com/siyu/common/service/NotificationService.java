package com.siyu.common.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.siyu.common.domain.dto.ShiroUser;
import com.siyu.common.domain.entity.Notification;
import com.siyu.common.domain.vo.NotificationVO;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author MybatisPlusGenerator
 * @since 2024-05-31 08:37:04
 */
public interface NotificationService extends IService<Notification> {

    Notification setBaseInfo(Notification notification, String type, String from, String to);


    Page<Notification> pageWithName(Page<Notification> page, LambdaQueryWrapper<Notification> wrapper);

    List<Notification> generateCounselorNotification(ShiroUser currentUser, NotificationVO.In in);

    Notification generateAdminNotification(ShiroUser currentUser, NotificationVO.In in);

    NotificationVO.Out loadById(String id);

    NotificationVO.Out loadMineById(String id, ShiroUser currentUser);
}

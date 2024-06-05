package com.siyu.server.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.siyu.server.entity.Notification;
import com.baomidou.mybatisplus.extension.service.IService;
import com.siyu.server.entity.vo.NotificationVO;
import com.siyu.shiro.entity.ShiroUser;

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

    void sendCounselorNotification(ShiroUser currentUser, NotificationVO.In in);

    void sendAdminNotification(ShiroUser currentUser, NotificationVO.In in);

    NotificationVO.Out loadById(String id);

    NotificationVO.Out loadMineById(String id, String userId);
}

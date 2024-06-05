package com.siyu.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.siyu.common.domain.entity.SysUser;
import com.siyu.common.enums.ErrorStatus;
import com.siyu.common.enums.NotificationType;
import com.siyu.common.exception.BusinessException;
import com.siyu.common.mapper.SysUserMapper;
import com.siyu.common.service.SysDepartmentService;
import com.siyu.common.utils.BeanUtils;
import com.siyu.server.entity.Notification;
import com.siyu.server.entity.UserNotificationRead;
import com.siyu.server.entity.vo.NotificationVO;
import com.siyu.server.mapper.NotificationMapper;
import com.siyu.server.mapper.UserNotificationReadMapper;
import com.siyu.server.service.NotificationService;
import com.siyu.shiro.entity.ShiroUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author MybatisPlusGenerator
 * @since 2024-05-31 08:37:04
 */
@Service
public class NotificationServiceImpl extends ServiceImpl<NotificationMapper, Notification> implements NotificationService {

    @Autowired
    private NotificationMapper notificationMapper;

    @Autowired
    private UserNotificationReadMapper userNotificationReadMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Override
    public Notification setBaseInfo(Notification notification, String type, String from, String to) {
        notification.setType(type);
        notification.setFrom(from);
        notification.setTo(to);
        return notification;
    }


    @Override
    public Page<Notification> pageWithName(Page<Notification> page, LambdaQueryWrapper<Notification> wrapper) {
        return notificationMapper.pageWithName(page ,wrapper);
    }

    @Override
    public void sendCounselorNotification(ShiroUser currentUser, NotificationVO.In in) {
        List<String> toList = in.getToList();

        //辅导员无广播权限
        if(toList.contains("0")) {
            throw new BusinessException(ErrorStatus.AUTHOR_ERROR, "当前账号没有广播消息权限");
        }
        List<SysUser> users = sysUserMapper.selectBatchIds(toList);

        //如果当前用户所处部门并非待发送人部门的父级，权限不足
        boolean flag = users.stream().anyMatch(user -> !SysDepartmentService.isParentDepartment(currentUser.getDepartmentCode(), user.getDepartmentCode()));
        if(flag) {
            throw new BusinessException(ErrorStatus.AUTHOR_ERROR);
        }

        //数据量不大，无所谓是否批量插入
        toList.forEach(to -> {
            Notification notification = BeanUtils.copyProperties(in, new Notification());
            notification = setBaseInfo(notification, NotificationType.COUNSELOR.name(), currentUser.getId(), to);
            notificationMapper.insert(notification);
        });
    }

    @Override
    public void sendAdminNotification(ShiroUser currentUser, NotificationVO.In in) {
        Notification notification = BeanUtils.copyProperties(in, new Notification());
        notification = setBaseInfo(notification, NotificationType.ADMIN.name(), currentUser.getId(), "0");
        notificationMapper.insert(notification);
    }

    @Override
    public
    NotificationVO.Out loadById(String id) {
        Notification notification = notificationMapper.loadById(id);
        return BeanUtils.copyProperties(notification, new NotificationVO.Out());
    }

    @Override
    public NotificationVO.Out loadMineById(String id, String userId) {
        Notification notification = notificationMapper.loadById(id);
        if(!"0".equals(notification.getTo()) && !userId.equals(notification.getTo())) {
            throw new BusinessException(ErrorStatus.QUERY_ERROR);
        }
        //添加已读表
        UserNotificationRead userNotificationRead = new UserNotificationRead(userId, notification.getId());
        userNotificationReadMapper.insert(userNotificationRead);
        return BeanUtils.copyProperties(notification, new NotificationVO.Out());
    }
}

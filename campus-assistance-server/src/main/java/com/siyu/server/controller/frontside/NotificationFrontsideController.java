package com.siyu.server.controller.frontside;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.siyu.common.domain.PaginationQuery;
import com.siyu.common.domain.PaginationResult;
import com.siyu.common.domain.R;
import com.siyu.common.domain.dto.ShiroUser;
import com.siyu.common.domain.entity.Notification;
import com.siyu.common.domain.entity.UserNotificationRead;
import com.siyu.common.domain.vo.NotificationVO;
import com.siyu.common.service.NotificationService;
import com.siyu.common.service.UserNotificationReadService;
import com.siyu.common.utils.BeanUtils;
import com.siyu.rabbitMQ.service.MQService;
import com.siyu.shiro.utils.ShiroUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/frontside/notification")
public class NotificationFrontsideController {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserNotificationReadService userNotificationReadService;

    @Autowired
    private MQService mqService;

    @ApiOperation("分页查询当前用户通知")
    @PostMapping("/page")
    public R<PaginationResult<NotificationVO.Table>> page(@RequestBody PaginationQuery<NotificationVO.Condition> query) {
        NotificationVO.Condition condition = query.getCondition();
        Page<Notification> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<Notification> wrapper = new LambdaQueryWrapper<Notification>()
                .eq(StringUtils.hasText(condition.getType()), Notification::getType, condition.getType())
                .and(innerWrapper -> {
                    innerWrapper.eq(Notification::getTo, ShiroUtils.getCurrentUserId()).or().eq(Notification::getTo, "0");
                }).orderByDesc(Notification::getCreateTime);
        page = notificationService.pageWithName(page, wrapper);
        List<Notification> notifications = page.getRecords();
        if(notifications.isEmpty()) {
            return R.ok(PaginationResult.of(page, new ArrayList<>()));
        }
        //根据当前分页通知Ids查询已读表
        List<String> read = userNotificationReadService.list(new LambdaQueryWrapper<UserNotificationRead>()
                .in(UserNotificationRead::getNotificationId, notifications.stream().map(Notification::getId).collect(Collectors.toList()))
                .eq(UserNotificationRead::getUserId, ShiroUtils.getCurrentUserId()))
                .stream().map(UserNotificationRead::getNotificationId)
                .collect(Collectors.toList());
        List<NotificationVO.Table> list = notifications.stream()
                .map(item -> {
                    NotificationVO.Table table = BeanUtils.copyProperties(item, new NotificationVO.Table());
                    //已读表中存在
                    table.setRead(read.contains(table.getId()));
                    return table;
                })
                .collect(Collectors.toList());
        return R.ok(PaginationResult.of(page, list));
    }

    @ApiOperation("条件查询当前用户未读通知数量")
    @PostMapping("/count")
    public R<Long> count(@RequestBody NotificationVO.Condition condition) {
        List<Notification> notifications = notificationService.list(new LambdaQueryWrapper<Notification>()
                .eq(StringUtils.hasText(condition.getType()), Notification::getType, condition.getType())
                .and(innerWrapper -> {
                    innerWrapper.eq(Notification::getTo, ShiroUtils.getCurrentUserId()).or().eq(Notification::getTo, "0");
                })
        );
        if(notifications.isEmpty()) {
            return R.ok(0L);
        }
        //已读数
        long read = userNotificationReadService.count(new LambdaQueryWrapper<UserNotificationRead>()
                .in(UserNotificationRead::getNotificationId, notifications.stream().map(Notification::getId).collect(Collectors.toList()))
                .eq(UserNotificationRead::getUserId, ShiroUtils.getCurrentUserId()));

        //未读数
        long count = notifications.size() - read;
        return R.ok(count);
    }

    @ApiOperation("查看通知")
    @GetMapping("/{id}")
    public R<NotificationVO.Out> load(@PathVariable String id) {
        ShiroUser user = ShiroUtils.getCurrentUser();
        NotificationVO.Out out = notificationService.loadMineById(id, user);
        return R.ok(out);
    }

    @RequiresRoles(value = {"grade_counselor", "department_counselor"}, logical = Logical.OR)
    @ApiOperation("辅导员发送通知")
    @PostMapping
    public R<?> create(@RequestBody @Valid NotificationVO.In in) {
        ShiroUser currentUser = ShiroUtils.getCurrentUser();
        List<Notification> notifications = notificationService.generateCounselorNotification(currentUser, in);
        for (Notification notification : notifications) {
            mqService.sendMessageToNotificationExchange(notification);
        }
        return R.noContent();
    }

}

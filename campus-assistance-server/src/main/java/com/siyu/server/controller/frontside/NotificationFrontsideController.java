package com.siyu.server.controller.frontside;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.siyu.common.domain.PaginationQuery;
import com.siyu.common.domain.PaginationResult;
import com.siyu.common.domain.R;
import com.siyu.common.utils.BeanUtils;
import com.siyu.server.entity.Notification;
import com.siyu.server.entity.UserNotificationRead;
import com.siyu.server.entity.vo.NotificationVO;
import com.siyu.server.service.NotificationService;
import com.siyu.server.service.UserNotificationReadService;
import com.siyu.shiro.entity.ShiroUser;
import com.siyu.shiro.utils.ShiroUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Api(tags = "前台——通知模块")
@RestController
@RequestMapping("/frontside/notification")
public class NotificationFrontsideController {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserNotificationReadService userNotificationReadService;

    @ApiOperation("分页查询当前用户通知")
    @PostMapping("/page")
    public R<PaginationResult<NotificationVO.Table>> page(@RequestBody PaginationQuery<NotificationVO.Condition> query) {
        NotificationVO.Condition condition = query.getCondition();
        Page<Notification> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<Notification> wrapper = new LambdaQueryWrapper<Notification>()
                .eq(StringUtils.hasText(condition.getType()), Notification::getType, condition.getType())
                .orderByDesc(Notification::getCreateTime);
        page = notificationService.page(page, wrapper);
        List<Notification> notifications = page.getRecords();
        //根据当前分页通知Ids查询已读表
        List<String> read = userNotificationReadService.list(new LambdaQueryWrapper<UserNotificationRead>()
                .in(UserNotificationRead::getNotificationId, notifications.stream().map(Notification::getId)))
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

    @ApiOperation("查看通知")
    @GetMapping("/{id}")
    public R<NotificationVO.Out> load(@PathVariable String id) {
        String userId = ShiroUtils.getCurrentUserId();
        NotificationVO.Out out = notificationService.loadMineById(id, userId);
        return R.ok(out);
    }

    @RequiresRoles(value = {"grade_counselor", "department_counselor"}, logical = Logical.OR)
    @ApiOperation("辅导员发送通知")
    @PostMapping
    public R<?> create(@RequestBody @Valid NotificationVO.In in) {
        ShiroUser currentUser = ShiroUtils.getCurrentUser();
        notificationService.sendCounselorNotification(currentUser, in);
        //TODO
        return R.noContent();
    }

}

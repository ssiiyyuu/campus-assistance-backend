package com.siyu.server.controller.backside;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.siyu.common.domain.PaginationQuery;
import com.siyu.common.domain.PaginationResult;
import com.siyu.common.domain.R;
import com.siyu.common.utils.BeanUtils;
import com.siyu.server.entity.Notification;
import com.siyu.server.entity.vo.NotificationVO;
import com.siyu.server.service.NotificationService;
import com.siyu.shiro.entity.ShiroUser;
import com.siyu.shiro.utils.ShiroUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Api(tags = "后台——通知模块")
@RestController
@RequestMapping("/admin/notification")
public class NotificationBacksideController {

    @Autowired
    private NotificationService notificationService;

    @ApiOperation("分页查询")
    @PostMapping("/page")
    public R<PaginationResult<NotificationVO.Table>> page(@RequestBody PaginationQuery<NotificationVO.Condition> query) {
        NotificationVO.Condition condition = query.getCondition();
        Page<Notification> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<Notification> wrapper = new LambdaQueryWrapper<Notification>()
			.eq(StringUtils.hasText(condition.getType()), Notification::getType, condition.getType());
        page = notificationService.pageWithName(page, wrapper);
        List<NotificationVO.Table> list = page.getRecords().stream()
                .map(item -> BeanUtils.copyProperties(item, new NotificationVO.Table()))
                .collect(Collectors.toList());
        return R.ok(PaginationResult.of(page, list));
    }

    @ApiOperation("查")
    @GetMapping("/{id}")
    public R<NotificationVO.Out> load(@PathVariable String id) {
        NotificationVO.Out out = notificationService.loadById(id);
        return R.ok(out);
    }

    @ApiOperation("广播管理员消息")
    @PostMapping
    public R<?> create(@RequestBody @Valid NotificationVO.In in) {
        ShiroUser currentUser = ShiroUtils.getCurrentUser();
        notificationService.sendAdminNotification(currentUser, in);
        //TODO
        return R.noContent();
    }


    @ApiOperation("删")
    @DeleteMapping("/{ids}")
    public R<?> deleteByIds(@PathVariable String ids) {
        notificationService.removeByIds(Arrays.stream(ids.split(",")).toList());
        return R.noContent();
    }
}

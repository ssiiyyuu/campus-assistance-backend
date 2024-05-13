package com.siyu.server.controller.backside;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.siyu.common.domain.PaginationQuery;
import com.siyu.common.domain.PaginationResult;
import com.siyu.common.domain.R;
import com.siyu.common.enums.ErrorStatus;
import com.siyu.common.enums.InformationStatus;
import com.siyu.common.exception.BusinessException;
import com.siyu.common.utils.BeanUtils;
import com.siyu.server.entity.Information;
import com.siyu.server.entity.vo.InformationVO;
import com.siyu.server.service.InformationService;
import com.siyu.shiro.entity.ShiroUser;
import com.siyu.shiro.utils.ShiroUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Api("后台——信息发布")
@RestController
@RequestMapping("/admin/information")
public class InformationController {

    @Autowired
    private InformationService informationService;

    @ApiOperation("分页查询")
    @PostMapping("/page")
    public R<PaginationResult<InformationVO.Table>> page(@RequestBody PaginationQuery<InformationVO.Condition> query) {
        InformationVO.Condition condition = query.getCondition();
        Page<Information> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<Information> wrapper = new LambdaQueryWrapper<Information>()
			.eq(StringUtils.hasText(condition.getCategoryId()), Information::getCategoryId, condition.getCategoryId())
			.like(StringUtils.hasText(condition.getTitle()), Information::getTitle, "%" + condition.getTitle() + "%")
			.eq(StringUtils.hasText(condition.getStatus()), Information::getStatus, condition.getStatus())
			.eq(StringUtils.hasText(condition.getDepartmentCode()), Information::getDepartmentCode, condition.getDepartmentCode())
            .between(null != condition.getPublishTime() && condition.getPublishTime().size() == 2, Information::getPublishTime, condition.getPublishTime().get(0), condition.getPublishTime().get(1));
        page = informationService.page(page, wrapper);
        List<InformationVO.Table> list = page.getRecords().stream()
                .map(item -> informationService.setTableBaseInfo(item))
                .collect(Collectors.toList());
        return R.ok(PaginationResult.of(page, list));
    }

    @ApiOperation("查")
    @GetMapping("/{id}")
    public R<InformationVO.Out> load(@PathVariable String id) {
        Information information = informationService.getById(id);
        InformationVO.Out out = informationService.setOutBaseInfo(information);
        return R.ok(out);
    }

    @ApiOperation("增")
    @PostMapping
    public R<?> create(@RequestBody @Valid InformationVO.In in) {
        informationService.create(in);
        return R.noContent();
    }

    @ApiOperation("定时发布")
    @PutMapping("/schedule/{id}")
    public R<?> schedule(@PathVariable String id) {
        //TODO 定时发布
        return R.noContent();
    }

    @ApiOperation("发布")
    @PutMapping("/publish/{id}")
    public R<?> publish(@PathVariable String id) {
        informationService.publish(id);
        return R.noContent();
    }

    @ApiOperation("下线")
    @PutMapping("/offline/{id}")
    public R<?> offline(@PathVariable String id) {
        informationService.offline(id);
        return R.noContent();
    }

    @ApiOperation("改")
    @PutMapping("/{id}")
    public R<?> update(@PathVariable String id, @RequestBody @Valid InformationVO.In in) {
        informationService.updateById(BeanUtils.copyProperties(in, informationService.getById(id)));
        return R.noContent();
    }

    @ApiOperation("删")
    @DeleteMapping("/{ids}")
    public R<?> deleteByIds(@PathVariable String ids) {
        informationService.removeByIds(Arrays.stream(ids.split(",")).toList());
        return R.noContent();
    }
}

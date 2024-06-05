package com.siyu.flowable.controller.attach;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.siyu.common.domain.PaginationQuery;
import com.siyu.common.domain.PaginationResult;
import com.siyu.common.domain.R;
import com.siyu.common.utils.BeanUtils;
import com.siyu.common.utils.TreeUtils;
import com.siyu.flowable.entity.CampusReportEvent;
import com.siyu.flowable.entity.vo.CampusReportEventVO;
import com.siyu.flowable.service.CampusReportEventService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Api(tags = "工作流——校园上报事件模块")
@RestController
@RequestMapping("/campusReportEvent")
public class CampusReportEventController {

    @Autowired
    private CampusReportEventService campusReportEventService;

    @ApiOperation("树形数据")
    @GetMapping("/tree")
    public R<List<CampusReportEventVO.Tree>> tree() {
        List<CampusReportEventVO.Tree> tree = campusReportEventService.list().stream()
                .map(item -> BeanUtils.copyProperties(item, new CampusReportEventVO.Tree()))
                .collect(Collectors.toList());
        List<CampusReportEventVO.Tree> result = TreeUtils.buildTree(tree, "0");
        return R.ok(result);
    }

    @ApiOperation("分页查询")
    @PostMapping("/page")
    public R<PaginationResult<CampusReportEventVO.Table>> page(@RequestBody PaginationQuery<CampusReportEventVO.Condition> query) {
        CampusReportEventVO.Condition condition = query.getCondition();
        Page<CampusReportEvent> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<CampusReportEvent> wrapper = new LambdaQueryWrapper<CampusReportEvent>()
			.eq(StringUtils.hasText(condition.getName()), CampusReportEvent::getName, condition.getName())
			.eq(StringUtils.hasText(condition.getLevel()), CampusReportEvent::getLevel, condition.getLevel());
        page = campusReportEventService.page(page, wrapper);
        List<CampusReportEventVO.Table> list = page.getRecords().stream()
                .map(item -> BeanUtils.copyProperties(item, new CampusReportEventVO.Table()))
                .collect(Collectors.toList());
        return R.ok(PaginationResult.of(page, list));
    }

    @ApiOperation("查")
    @GetMapping("/{id}")
    public R<CampusReportEventVO.Out> load(@PathVariable String id) {
        CampusReportEventVO.Out out = BeanUtils.copyProperties(campusReportEventService.getById(id), new CampusReportEventVO.Out());
        return R.ok(out);
    }

    @ApiOperation("增")
    @PostMapping
    public R<?> create(@RequestBody @Valid CampusReportEventVO.In in) {
        CampusReportEvent campusReportEvent = BeanUtils.copyProperties(in, new CampusReportEvent());
        campusReportEventService.save(campusReportEvent);
        return R.noContent();
    }

    @ApiOperation("改")
    @PutMapping("/{id}")
    public R<?> update(@PathVariable String id, @RequestBody @Valid CampusReportEventVO.In in) {
        CampusReportEvent campusReportEvent = BeanUtils.copyProperties(in, campusReportEventService.getById(id));
        campusReportEvent.setUpdateTime(null);
        campusReportEventService.updateById(campusReportEvent);
        return R.noContent();
    }

    @ApiOperation("删")
    @DeleteMapping("/{ids}")
    public R<?> deleteByIds(@PathVariable String ids) {
        campusReportEventService.removeByIds(Arrays.stream(ids.split(",")).toList());
        return R.noContent();
    }
}

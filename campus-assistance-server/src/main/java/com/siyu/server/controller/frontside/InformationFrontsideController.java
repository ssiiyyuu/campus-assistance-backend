package com.siyu.server.controller.frontside;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.siyu.common.domain.PaginationQuery;
import com.siyu.common.domain.PaginationResult;
import com.siyu.common.domain.R;
import com.siyu.common.enums.ErrorStatus;
import com.siyu.common.exception.BusinessException;
import com.siyu.server.entity.Category;
import com.siyu.server.entity.Information;
import com.siyu.server.entity.vo.InformationVO;
import com.siyu.server.service.CategoryService;
import com.siyu.server.service.InformationService;
import com.siyu.server.service.SysDepartmentService;
import com.siyu.shiro.utils.ShiroUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Api(tags = "前台——信息发布")
@RestController
@RequestMapping("/frontside/information")
public class InformationFrontsideController {

    @Autowired
    private InformationService informationService;

    @Autowired
    private CategoryService categoryService;

    @ApiOperation("系统公告与系统动态")
    @PostMapping("/page/system")
    public R<PaginationResult<InformationVO.Table>> pageSystem(@RequestBody PaginationQuery<InformationVO.Condition> query) {
        InformationVO.Condition condition = query.getCondition();
        String categoryId = condition.getCategoryId();
        Category category = categoryService.getById(categoryId);
        if(category == null || (!category.getName().equals("系统公告") && !category.getName().equals("系统动态"))) {
            throw new BusinessException(ErrorStatus.QUERY_ERROR);
        }
        Page<Information> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<Information> wrapper = new LambdaQueryWrapper<Information>()
                .select(Information::getId , Information::getAuthorId, Information::getCover, Information::getTitle, Information::getDepartmentCode, Information::getVisits, Information::getCategoryId, Information::getPublishTime)
                .eq(StringUtils.hasText(condition.getCategoryId()), Information::getCategoryId, condition.getCategoryId())
                .eq(Information::getStatus, "publish")
                .eq(StringUtils.hasText(condition.getDepartmentCode()), Information::getDepartmentCode, condition.getDepartmentCode())
                .like(StringUtils.hasText(condition.getTitle()), Information::getTitle, "%" + condition.getTitle() + "%")
                .between(null != condition.getPublishTime() && condition.getPublishTime().size() == 2, Information::getPublishTime, condition.getPublishTime().get(0), condition.getPublishTime().get(1))
                .orderByDesc(Information::getPublishTime);
        page = informationService.page(page, wrapper);
        List<InformationVO.Table> list = page.getRecords().stream()
                .map(item -> informationService.setTableBaseInfo(item))
                .collect(Collectors.toList());
        return R.ok(PaginationResult.of(page, list));
    }

    @ApiOperation("校园公告与校园动态")
    @PostMapping("/page/campus")
    public R<PaginationResult<InformationVO.Table>> pageCampus(@RequestBody PaginationQuery<InformationVO.Condition> query) {
        InformationVO.Condition condition = query.getCondition();
        String categoryId = condition.getCategoryId();
        Category category = categoryService.getById(categoryId);
        if(category == null || (!category.getName().equals("校园公告") && !category.getName().equals("校园动态"))) {
            throw new BusinessException(ErrorStatus.QUERY_ERROR);
        }

        //获取当前部门编码的根编码 rootCode
        String curDepartmentCode = ShiroUtils.getCurrentDepartmentCode();
        String rootCode = SysDepartmentService.getLevelCode(curDepartmentCode, 1);

        Page<Information> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<Information> wrapper = new LambdaQueryWrapper<Information>()
                .select(Information::getId , Information::getAuthorId, Information::getCover, Information::getTitle, Information::getDepartmentCode, Information::getVisits, Information::getCategoryId, Information::getPublishTime)
                //与系统公告与系统动态区别在此 只能查询当前校园范围内的
                .like(Information::getDepartmentCode, rootCode + "%")
                .eq(StringUtils.hasText(condition.getCategoryId()), Information::getCategoryId, condition.getCategoryId())
                .eq(Information::getStatus, "publish")
                .eq(StringUtils.hasText(condition.getDepartmentCode()), Information::getDepartmentCode, condition.getDepartmentCode())
                .like(StringUtils.hasText(condition.getTitle()), Information::getTitle, "%" + condition.getTitle() + "%")
                .between(null != condition.getPublishTime() && condition.getPublishTime().size() == 2, Information::getPublishTime, condition.getPublishTime().get(0), condition.getPublishTime().get(1))
                .orderByDesc(Information::getPublishTime);
        page = informationService.page(page, wrapper);
        List<InformationVO.Table> list = page.getRecords().stream()
                .map(item -> informationService.setTableBaseInfo(item))
                .collect(Collectors.toList());
        return R.ok(PaginationResult.of(page, list));
    }

    @ApiOperation("我创建的")
    @PostMapping("/page/me")
    public R<PaginationResult<InformationVO.Table>> myInformation(@RequestBody PaginationQuery<InformationVO.Condition> query) {
        String curUserId = ShiroUtils.getCurrentUserId();
        InformationVO.Condition condition = query.getCondition();
        Page<Information> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<Information> wrapper = new LambdaQueryWrapper<Information>()
                .select(Information::getId , Information::getAuthorId, Information::getCover, Information::getTitle, Information::getDepartmentCode, Information::getVisits, Information::getCategoryId, Information::getPublishTime)
                //只能查询作者为当前用户的
                .eq(Information::getAuthorId, curUserId)
                .eq(StringUtils.hasText(condition.getCategoryId()), Information::getCategoryId, condition.getCategoryId())
                .eq(Information::getStatus, "publish")
                .eq(StringUtils.hasText(condition.getDepartmentCode()), Information::getDepartmentCode, condition.getDepartmentCode())
                .like(StringUtils.hasText(condition.getTitle()), Information::getTitle, "%" + condition.getTitle() + "%")
                .between(null != condition.getPublishTime() && condition.getPublishTime().size() == 2, Information::getPublishTime, condition.getPublishTime().get(0), condition.getPublishTime().get(1));
        page = informationService.page(page, wrapper);
        List<InformationVO.Table> list = page.getRecords().stream()
                .map(item -> informationService.setTableBaseInfo(item))
                .collect(Collectors.toList());
        return R.ok(PaginationResult.of(page, list));
    }

    @ApiOperation("查看系统公告与系统动态")
    @GetMapping("/{id}/system")
    public R<InformationVO.Out> load(@PathVariable String id) {
        Information information = informationService.loadSystem(id);
        InformationVO.Out out = informationService.setOutBaseInfo(information);
        return R.ok(out);
    }

    @ApiOperation("查看校园公告与校园动态")
    @GetMapping("/{id}/campus")
    public R<InformationVO.Out> loadCampus(@PathVariable String id) {
        Information information = informationService.loadCampus(id);
        InformationVO.Out out = informationService.setOutBaseInfo(information);
        return R.ok(out);
    }

    @ApiOperation("创建校园动态")
    @PostMapping("/campus/dynamics")
    public R<?> createCampusDynamics(@RequestBody @Valid InformationVO.In in) {
        informationService.createCampusDynamics(in);
        return R.noContent();
    }

    //需要辅导员权限
    @RequiresRoles("grade_counselor")
    @ApiOperation("创建校园公告")
    @PostMapping("/campus/announcement")
    public R<?> createCampusAnnouncement(@RequestBody @Valid InformationVO.In in) {
        informationService.createCampusAnnouncement(in);
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
        informationService.checkAuthorPublish(id);
        return R.noContent();
    }

    @ApiOperation("下线")
    @PutMapping("/offline/{id}")
    public R<?> offline(@PathVariable String id) {
        informationService.checkAuthorOffline(id);
        return R.noContent();
    }

    @ApiOperation("改")
    @PutMapping("/{id}")
    public R<?> update(@PathVariable String id, @RequestBody @Valid InformationVO.In in) {
        informationService.checkAuthorUpdate(id, in);
        return R.noContent();
    }

    @ApiOperation("删")
    @DeleteMapping("/{ids}")
    public R<?> deleteByIds(@PathVariable String ids) {
        informationService.checkAuthorRemove(Arrays.stream(ids.split(",")).toList());
        return R.noContent();
    }
}

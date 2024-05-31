package com.siyu.server.controller.backside;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.siyu.common.domain.PaginationQuery;
import com.siyu.common.domain.PaginationResult;
import com.siyu.common.domain.R;
import com.siyu.common.domain.entity.SysDepartment;
import com.siyu.common.utils.BeanUtils;
import com.siyu.common.utils.TreeUtils;
import com.siyu.server.entity.vo.SysDepartmentVO;
import com.siyu.server.entity.vo.SysPermissionVO;
import com.siyu.server.service.SysDepartmentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Api(tags = "后台——部门")
@RestController
@RequestMapping("/admin/sysDepartment")
public class SysDepartmentController {

    @Autowired
    private SysDepartmentService sysDepartmentService;

    @ApiOperation(value = "获取树形菜单")
    @GetMapping("/tree")
    public R<List<SysDepartmentVO.Tree>> tree() {
        List<SysDepartmentVO.Tree> list = sysDepartmentService.list().stream()
                .map(item -> BeanUtils.copyProperties(item, new SysDepartmentVO.Tree()))
                .collect(Collectors.toList());
        List<SysDepartmentVO.Tree> tree = TreeUtils.buildTree(list, "0");
        return R.ok(tree);
    }

    @ApiOperation(value = "递归删除菜单")
    @DeleteMapping("/{id}/tree")
    public R<?> deleteTree(@PathVariable String id) {
        sysDepartmentService.deleteTree(id);
        return R.noContent();
    }

    @ApiOperation("分页查询")
    @PostMapping("/page")
    public R<PaginationResult<SysDepartmentVO.Table>> page(@RequestBody PaginationQuery<SysDepartmentVO.Condition> query) {
        SysDepartmentVO.Condition condition = query.getCondition();
        Page<SysDepartment> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<SysDepartment> wrapper = new LambdaQueryWrapper<SysDepartment>()
			.eq(StringUtils.hasText(condition.getName()), SysDepartment::getName, condition.getName())
			.eq(null != condition.getLevel(), SysDepartment::getLevel, condition.getLevel());
        page = sysDepartmentService.page(page, wrapper);
        List<SysDepartmentVO.Table> list = page.getRecords().stream()
                .map(item -> BeanUtils.copyProperties(item, new SysDepartmentVO.Table()))
                .collect(Collectors.toList());
        return R.ok(PaginationResult.of(page, list));
    }

    @ApiOperation("查")
    @GetMapping("/{id}")
    public R<SysDepartmentVO.Out> load(@PathVariable String id) {
        SysDepartmentVO.Out out = BeanUtils.copyProperties(sysDepartmentService.getById(id), new SysDepartmentVO.Out());
        return R.ok(out);
    }

    @ApiOperation("增")
    @PostMapping
    public R<?> create(@RequestBody @Valid SysDepartmentVO.In in) {
        SysDepartment sysDepartment = BeanUtils.copyProperties(in, new SysDepartment());
        sysDepartmentService.save(sysDepartment);
        return R.noContent();
    }

    @ApiOperation("改")
    @PutMapping("/{id}")
    public R<?> update(@PathVariable String id, @RequestBody @Valid SysDepartmentVO.In in) {
        SysDepartment department = BeanUtils.copyProperties(in, sysDepartmentService.getById(id));
        department.setUpdateTime(null);
        sysDepartmentService.updateById(department);
        return R.noContent();
    }

    @ApiOperation("删")
    @DeleteMapping("/{ids}")
    public R<?> deleteByIds(@PathVariable String ids) {
        sysDepartmentService.removeByIds(Arrays.stream(ids.split(",")).toList());
        return R.noContent();
    }
}

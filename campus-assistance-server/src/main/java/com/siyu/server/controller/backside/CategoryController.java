package com.siyu.server.controller.backside;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.siyu.common.domain.PaginationQuery;
import com.siyu.common.domain.PaginationResult;
import com.siyu.common.domain.R;
import com.siyu.common.utils.BeanUtils;
import com.siyu.common.utils.TreeUtils;
import com.siyu.server.entity.Category;
import com.siyu.server.entity.vo.CategoryVO;
import com.siyu.server.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@Api("后台——信息分类")
@RestController
@RequestMapping("/admin/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;


    @ApiOperation("树形数据")
    @GetMapping("/tree")
    public R<List<CategoryVO.Tree>> tree() {
        List<CategoryVO.Tree> tree = categoryService.list().stream()
                .map(item -> BeanUtils.copyProperties(item, new CategoryVO.Tree()))
                .collect(Collectors.toList());
        List<CategoryVO.Tree> result = TreeUtils.buildTree(tree, "0");
        return R.ok(result);
    }

    @ApiOperation("分页查询")
    @PostMapping("/page")
    public R<PaginationResult<CategoryVO.Table>> page(@RequestBody PaginationQuery<CategoryVO.Condition> query) {
        CategoryVO.Condition condition = query.getCondition();
        Page<Category> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<Category>()
			.eq(StringUtils.hasText(condition.getName()), Category::getName, condition.getName());
        page = categoryService.page(page, wrapper);
        List<CategoryVO.Table> list = page.getRecords().stream()
                .map(item -> categoryService.setTableBaseInfo(item))
                .collect(Collectors.toList());
        return R.ok(PaginationResult.of(page, list));
    }

    @ApiOperation("查")
    @GetMapping("/{id}")
    public R<CategoryVO.Out> load(@PathVariable String id) {
        CategoryVO.Out out = categoryService.setOutBaseInfo(categoryService.getById(id));
        return R.ok(out);
    }

    @ApiOperation("增")
    @PostMapping
    public R<?> create(@RequestBody @Valid CategoryVO.In in) {
        Category category = BeanUtils.copyProperties(in, new Category());
        categoryService.save(category);
        return R.noContent();
    }

    @ApiOperation("改")
    @PutMapping("/{id}")
    public R<?> update(@PathVariable String id, @RequestBody @Valid CategoryVO.In in) {
        categoryService.updateById(BeanUtils.copyProperties(in, categoryService.getById(id)));
        return R.noContent();
    }

    @ApiOperation("删")
    @DeleteMapping("/{ids}")
    public R<?> deleteByIds(@PathVariable String ids) {
        categoryService.removeByIds(Arrays.stream(ids.split(",")).toList());
        return R.noContent();
    }
}

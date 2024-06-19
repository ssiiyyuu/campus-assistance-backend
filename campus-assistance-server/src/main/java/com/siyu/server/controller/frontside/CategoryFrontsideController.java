package com.siyu.server.controller.frontside;

import com.siyu.common.domain.R;
import com.siyu.common.utils.BeanUtils;
import com.siyu.common.utils.TreeUtils;
import com.siyu.server.entity.vo.CategoryVO;
import com.siyu.server.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@Api(tags = "前台——信息分类模块")

@RestController
@RequestMapping("/frontside/category")
public class CategoryFrontsideController {
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

    @ApiOperation("所有数据")
    @GetMapping("/list")
    public R<List<CategoryVO.Table>> list() {
        List<CategoryVO.Table> result = categoryService.list().stream()
                .map(item -> BeanUtils.copyProperties(item, new CategoryVO.Table()))
                .collect(Collectors.toList());
        return R.ok(result);
    }
}

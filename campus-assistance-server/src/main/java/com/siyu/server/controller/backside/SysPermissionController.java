package com.siyu.server.controller.backside;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.siyu.common.domain.PaginationQuery;
import com.siyu.common.domain.PaginationResult;
import com.siyu.common.domain.R;
import com.siyu.common.domain.entity.SysPermission;
import com.siyu.common.domain.vo.SysPermissionVO;
import com.siyu.common.service.SysPermissionService;
import com.siyu.common.utils.BeanUtils;
import com.siyu.common.utils.TreeUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Api(tags = "后台——权限模块")
@RestController
@RequestMapping("/admin/sysPermission")
public class SysPermissionController {

    @Autowired
    private SysPermissionService sysPermissionService;

    @ApiOperation(value = "查询角色权限(树形菜单)")
    @GetMapping("/{roleId}/assign")
    public R<List<SysPermissionVO.Tree>> getPermissions(@PathVariable String roleId) {
        List<SysPermissionVO.Tree> permissions = sysPermissionService.getAssignPermissions(roleId);
        return R.ok(permissions);
    }

    @ApiOperation(value = "为角色分配权限")
    @PostMapping("/{roleId}/assign/{ids}")
    public R<?> assignPermissions(@PathVariable String roleId, @PathVariable String ids) {
        sysPermissionService.assignPermissions(roleId, Arrays.stream(ids.split(",")).collect(Collectors.toList()));
        return R.noContent();
    }

    @ApiOperation(value = "获取树形菜单")
    @GetMapping("/tree")
    public R<List<SysPermissionVO.Tree>> tree() {
        List<SysPermissionVO.Tree> list = sysPermissionService.list().stream()
                .map(item -> BeanUtils.copyProperties(item, new SysPermissionVO.Tree()))
                .collect(Collectors.toList());
        List<SysPermissionVO.Tree> tree = TreeUtils.buildTree(list, "0");
        return R.ok(tree);
    }

    @ApiOperation(value = "递归删除菜单")
    @DeleteMapping("/{id}/tree")
    public R<?> deleteTree(@PathVariable String id) {
        sysPermissionService.deleteTree(id);
        return R.noContent();
    }

    @ApiOperation("分页查询")
    @PostMapping("/page")
    public R<PaginationResult<SysPermissionVO.Table>> page(@RequestBody PaginationQuery<SysPermissionVO.Condition> query) {
        SysPermissionVO.Condition condition = query.getCondition();
        Page<SysPermission> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<SysPermission> wrapper = new LambdaQueryWrapper<SysPermission>()
			.eq(StringUtils.hasText(condition.getName()), SysPermission::getName, condition.getName())
			.eq(null != condition.getType(), SysPermission::getType, condition.getType())
			.eq(null != condition.getStatus(), SysPermission::getStatus, condition.getStatus());
        page = sysPermissionService.page(page, wrapper);
        List<SysPermissionVO.Table> list = page.getRecords().stream()
                .map(item -> BeanUtils.copyProperties(item, new SysPermissionVO.Table()))
                .collect(Collectors.toList());
        return R.ok(PaginationResult.of(page, list));
    }

    @ApiOperation("查")
    @GetMapping("/{id}")
    public R<SysPermissionVO.Out> load(@PathVariable String id) {
        SysPermissionVO.Out out = BeanUtils.copyProperties(sysPermissionService.getById(id), new SysPermissionVO.Out());
        return R.ok(out);
    }

    @ApiOperation("增")
    @PostMapping
    public R<?> create(@RequestBody @Valid SysPermissionVO.In in) {
        SysPermission sysPermission = BeanUtils.copyProperties(in, new SysPermission());
        sysPermissionService.save(sysPermission);
        return R.noContent();
    }

    @ApiOperation("改")
    @PutMapping("/{id}")
    public R<?> update(@PathVariable String id, @RequestBody @Valid SysPermissionVO.In in) {
        SysPermission sysPermission = BeanUtils.copyProperties(in, sysPermissionService.getById(id));
        sysPermission.setUpdateTime(null);
        sysPermissionService.updateById(sysPermission);
        return R.noContent();
    }

    @ApiOperation("删")
    @DeleteMapping("/{ids}")
    public R<?> deleteByIds(@PathVariable String ids) {
        sysPermissionService.removeByIds(Arrays.stream(ids.split(",")).toList());
        return R.noContent();
    }
}

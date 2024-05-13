package com.siyu.server.controller.backside;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.siyu.common.domain.PaginationQuery;
import com.siyu.common.domain.PaginationResult;
import com.siyu.common.domain.R;
import com.siyu.common.domain.entity.SysRole;
import com.siyu.common.utils.BeanUtils;
import com.siyu.server.entity.vo.SysPermissionVO;
import com.siyu.server.entity.vo.SysRoleVO;
import com.siyu.server.service.SysPermissionService;
import com.siyu.server.service.SysRoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Api("后台——角色")
@RestController
@RequestMapping("/admin/sysRole")
public class SysRoleController {

    @Autowired
    private SysRoleService sysRoleService;

    @ApiOperation(value = "查询用户分配角色")
    @GetMapping("/{userId}/assign")
    public R<List<SysRoleVO.Assign>> getRoles(@PathVariable String userId) {
        List<SysRoleVO.Assign> roles = sysRoleService.getAssignRoles(userId);
        return R.ok(roles);
    }

    @ApiOperation(value = "为用户分配角色")
    @PostMapping("/{userId}/assign/{ids}")
    public R<?> assignRoles(@PathVariable String userId, @PathVariable String ids) {
        sysRoleService.assignRoles(userId, Arrays.stream(ids.split(",")).collect(Collectors.toList()));
        return R.noContent();
    }

    @ApiOperation("分页查询")
    @PostMapping("/page")
    public R<PaginationResult<SysRoleVO.Table>> page(@RequestBody PaginationQuery<SysRoleVO.Condition> query) {
        SysRoleVO.Condition condition = query.getCondition();
        Page<SysRole> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<SysRole>()
			.eq(StringUtils.hasText(condition.getRoleName()), SysRole::getRoleName, condition.getRoleName())
			.eq(StringUtils.hasText(condition.getRoleCode()), SysRole::getRoleCode, condition.getRoleCode());
        page = sysRoleService.page(page, wrapper);
        List<SysRoleVO.Table> list = page.getRecords().stream()
                .map(item -> BeanUtils.copyProperties(item, new SysRoleVO.Table()))
                .collect(Collectors.toList());
        return R.ok(PaginationResult.of(page, list));
    }


    @ApiOperation("查")
    @GetMapping("/{id}")
    public R<SysRoleVO.Out> load(@PathVariable String id) {
        SysRoleVO.Out out = BeanUtils.copyProperties(sysRoleService.getById(id), new SysRoleVO.Out());
        return R.ok(out);
    }

    @ApiOperation("增")
    @PostMapping
    public R<?> create(@RequestBody @Valid SysRoleVO.In in) {
        SysRole sysRole = BeanUtils.copyProperties(in, new SysRole());
        sysRoleService.save(sysRole);
        return R.noContent();
    }

    @ApiOperation("改")
    @PutMapping("/{id}")
    public R<?> update(@PathVariable String id, @RequestBody @Valid SysRoleVO.In in) {
        sysRoleService.updateById(BeanUtils.copyProperties(in, sysRoleService.getById(id)));
        return R.noContent();
    }

    @ApiOperation("删")
    @DeleteMapping("/{ids}")
    public R<?> deleteByIds(@PathVariable String ids) {
        sysRoleService.removeByIds(Arrays.stream(ids.split(",")).toList());
        return R.noContent();
    }
}

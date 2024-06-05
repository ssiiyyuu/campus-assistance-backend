package com.siyu.server.controller.backside;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.siyu.common.constants.GlobalConstants;
import com.siyu.common.domain.PaginationQuery;
import com.siyu.common.domain.PaginationResult;
import com.siyu.common.domain.R;
import com.siyu.common.domain.entity.SysUser;
import com.siyu.common.domain.vo.SysUserVO;
import com.siyu.common.enums.ErrorStatus;
import com.siyu.common.exception.BusinessException;
import com.siyu.common.service.SysUserService;
import com.siyu.common.utils.BeanUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Api(tags = "后台——用户模块")
@RestController
@RequestMapping("/admin/user")
public class SysUserController {

    @Autowired
    private SysUserService sysUserService;

    @ApiOperation("分页查询")
    @PostMapping("/page")
    public R<PaginationResult<SysUserVO.Table>> page(@RequestBody PaginationQuery<SysUserVO.Condition> query) {
        SysUserVO.Condition condition = query.getCondition();
        Page<SysUser> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<SysUser>()
                .eq(StringUtils.hasText(condition.getUsername()), SysUser::getUsername, condition.getUsername())
                .eq(StringUtils.hasText(condition.getUserType()), SysUser::getUserType, condition.getUserType())
                .eq(StringUtils.hasText(condition.getDepartmentCode()), SysUser::getDepartmentCode, condition.getDepartmentCode());
        page = sysUserService.page(page, wrapper);
        List<SysUserVO.Table> list = page.getRecords().stream()
                .map(item -> BeanUtils.copyProperties(item, new SysUserVO.Table()))
                .collect(Collectors.toList());
        return R.ok(PaginationResult.of(page, list));
    }

    @ApiOperation("查")
    @GetMapping("/{id}")
    public R<SysUserVO.Out> load(@PathVariable String id) {
        SysUserVO.Out out = BeanUtils.copyProperties(sysUserService.getById(id), new SysUserVO.Out());
        return R.ok(out);
    }

    @ApiOperation("增")
    @PostMapping
    public R<?> create(@RequestBody @Valid SysUserVO.In in) {
        SysUser repeat = sysUserService.getOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, in.getUsername()));
        if(null != repeat) {
            throw new BusinessException(ErrorStatus.INSERT_ERROR, "用户名重复");
        }
        SysUser user = BeanUtils.copyProperties(in, new SysUser());
        user.setPassword(new Md5Hash(in.getPassword(), GlobalConstants.USER_SALT).toHex());
        sysUserService.save(user);
        return R.noContent();
    }

    @ApiOperation("改")
    @PutMapping("/{id}")
    public R<?> update(@PathVariable String id, @RequestBody @Valid SysUserVO.In in) {
        SysUser repeat = sysUserService.getOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, in.getUsername())
                .ne(SysUser::getId, id));
        if(null != repeat) {
            throw new BusinessException(ErrorStatus.UPDATE_ERROR, "用户名重复");
        }
        SysUser user = BeanUtils.copyProperties(in, sysUserService.getById(id));
        user.setPassword(new Md5Hash(in.getPassword(), GlobalConstants.USER_SALT).toHex());
        user.setUpdateTime(null);
        sysUserService.updateById(user);
        return R.noContent();
    }

    @ApiOperation("删")
    @DeleteMapping("/{ids}")
    public R<?> deleteByIds(@PathVariable String ids) {
        sysUserService.removeByIds(Arrays.stream(ids.split(",")).toList());
        return R.noContent();
    }
}

package com.siyu.server.controller;

import com.siyu.common.domain.R;
import com.siyu.common.domain.dto.ShiroRole;
import com.siyu.common.domain.dto.ShiroUser;
import com.siyu.common.domain.entity.SysPermission;
import com.siyu.common.domain.vo.SysPermissionVO;
import com.siyu.common.service.SysPermissionService;
import com.siyu.common.utils.BeanUtils;
import com.siyu.common.utils.TreeUtils;
import com.siyu.shiro.utils.ShiroUtils;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Api(tags = "当前用户信息")
@RestController
@RequestMapping("/current")
public class CurrentUserController {

    @Autowired
    private SysPermissionService sysPermissionService;

    @GetMapping("/menu")
    public R<List<SysPermissionVO.Tree>> menu() {
        ShiroUser currentUser = ShiroUtils.getCurrentUser();
        List<ShiroRole> roles = currentUser.getRoles();
        if(roles == null || roles.isEmpty()) {
            return R.ok(new ArrayList<>());
        } else {
            List<SysPermission> menus = sysPermissionService.menuListByRoleIds(roles.stream().map(ShiroRole::getId).collect(Collectors.toList()));
            List<SysPermissionVO.Tree> list = menus.stream()
                    .map(item -> BeanUtils.copyProperties(item, new SysPermissionVO.Tree()))
                    .collect(Collectors.toList());
            List<SysPermissionVO.Tree> tree = TreeUtils.buildTree(list, "0");
            return R.ok(tree);
        }
    }

    @GetMapping("/permission")
    public R<List<SysPermissionVO.Table>> permission() {
        ShiroUser currentUser = ShiroUtils.getCurrentUser();
        List<ShiroRole> roles = currentUser.getRoles();
        if(roles == null || roles.isEmpty()) {
            return R.ok(new ArrayList<>());
        } else {
            List<SysPermission> permissions = sysPermissionService.buttonListByRoleIds(roles.stream().map(ShiroRole::getId).collect(Collectors.toList()));
            List<SysPermissionVO.Table> list = permissions.stream()
                    .map(item -> BeanUtils.copyProperties(item, new SysPermissionVO.Table()))
                    .collect(Collectors.toList());
            return R.ok(list);
        }
    }
}

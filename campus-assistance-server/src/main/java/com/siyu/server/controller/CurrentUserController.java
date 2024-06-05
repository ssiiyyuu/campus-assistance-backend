package com.siyu.server.controller;

import com.siyu.common.domain.R;
import com.siyu.common.domain.vo.SysPermissionVO;
import com.siyu.common.utils.BeanUtils;
import com.siyu.common.utils.TreeUtils;
import com.siyu.shiro.entity.ShiroUser;
import com.siyu.shiro.utils.ShiroUtils;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@Api(tags = "当前用户信息")
@RestController
@RequestMapping("/current")
public class CurrentUserController {

    @GetMapping("/menu")
    public R<List<SysPermissionVO.Tree>> menu() {
        ShiroUser user = ShiroUtils.getCurrentUser();
        List<SysPermissionVO.Tree> list = user.getPermissions().stream()
                .map(item -> BeanUtils.copyProperties(item, new SysPermissionVO.Tree()))
                .collect(Collectors.toList());
        List<SysPermissionVO.Tree> tree = TreeUtils.buildTree(list, "0");
        return R.ok(tree);
    }
}

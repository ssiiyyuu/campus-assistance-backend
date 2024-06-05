package com.siyu.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.siyu.common.domain.entity.SysPermission;
import com.siyu.common.domain.entity.SysRolePermission;
import com.siyu.common.utils.BeanUtils;
import com.siyu.common.utils.TreeUtils;
import com.siyu.common.domain.vo.SysPermissionVO;
import com.siyu.common.mapper.SysPermissionMapper;
import com.siyu.common.mapper.SysRolePermissionMapper;
import com.siyu.common.service.SysPermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 权限 服务实现类
 * </p>
 *
 * @author MybatisPlusGenerator
 * @since 2024-05-09 02:06:23
 */
@Service
public class SysPermissionServiceImpl extends ServiceImpl<SysPermissionMapper, SysPermission> implements SysPermissionService {

    @Autowired
    private SysPermissionMapper sysPermissionMapper;

    @Autowired
    SysRolePermissionMapper sysRolePermissionMapper;

    @Override
    public void assignPermissions(String roleId, List<String> permissionIds) {
        //删除角色原先角色-权限关联数据
        sysRolePermissionMapper.delete(new LambdaQueryWrapper<SysRolePermission>().eq(SysRolePermission::getRoleId, roleId));
        //保存新的角色权限关联数据
        //数据量不大 循环插入与批处理性能几乎没差
        permissionIds.forEach(permissionId -> {
            SysRolePermission sysRolePermission = new SysRolePermission();
            sysRolePermission.setPermissionId(permissionId);
            sysRolePermission.setRoleId(roleId);
            sysRolePermissionMapper.insert(sysRolePermission);
        });
    }

    @Override
    public List<SysPermissionVO.Tree> getAssignPermissions(String roleId) {
        //所有权限
        List<SysPermissionVO.Tree> all = sysPermissionMapper.selectList(null).stream()
                .map(item -> BeanUtils.copyProperties(item, new SysPermissionVO.Tree())).collect(Collectors.toList());
        //当前用户权限
        List<String> assignedIds = sysPermissionMapper.selectPermissionsByRoleId(roleId).stream()
                .map(SysPermission::getId).collect(Collectors.toList());
        //建树
        List<SysPermissionVO.Tree> tree = TreeUtils.buildTree(all, "0");
        //标注是否分配
        tree.forEach(item -> item.setAssigned(assignedIds.contains(item.getId())));
        return tree;
    }

    @Override
    public void deleteTree(String id) {
        List<String> ids = new ArrayList<>();
        getChildIdsByParentId(id, ids);
        ids.add(id);
        sysPermissionMapper.deleteBatchIds(ids);
    }


    private void getChildIdsByParentId(String id, List<String> ids) {
        //根据id查询childIds
        List<SysPermission> permissions = sysPermissionMapper.selectList(new LambdaQueryWrapper<SysPermission>()
                .eq(SysPermission::getParentId, id)
                .select(SysPermission::getId));
        permissions.forEach(permission -> {
            ids.add(permission.getId());
            //递归查询所有子孙节点
            getChildIdsByParentId(permission.getId(), ids);
        });
    }

}

package com.siyu.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.siyu.common.domain.entity.SysRole;
import com.siyu.common.domain.entity.SysUserRole;
import com.siyu.server.entity.vo.SysRoleVO;
import com.siyu.server.mapper.SysRoleMapper;
import com.siyu.server.mapper.SysUserRoleMapper;
import com.siyu.server.service.SysRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author MybatisPlusGenerator
 * @since 2024-05-09 02:06:00
 */
@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {

    @Autowired
    private SysRoleMapper sysRoleMapper;

    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;

    @Override
    public List<SysRoleVO.Assign> getAssignRoles(String userId) {
        //所有角色
        List<SysRole> all = sysRoleMapper.selectList(null);
        //当前用户角色
        List<String> assignedIds = sysRoleMapper.selectRolesByUserId(userId).stream()
                .map(SysRole::getId).collect(Collectors.toList());
        List<SysRoleVO.Assign> result = all.stream().map(item -> {
            SysRoleVO.Assign assigned = new SysRoleVO.Assign();
            assigned.setId(item.getId());
            assigned.setRoleName(item.getRoleName());
            assigned.setAssigned(assignedIds.contains(item.getId()));
            return assigned;
        }).collect(Collectors.toList());
        return result;
    }

    @Transactional
    @Override
    public void assignRoles(String userId, List<String> roleIds) {
        //删除用户原先用户-角色关联数据
        sysUserRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, userId));
        //保存新的用户角色关联数据
        //数据量不大 循环插入与批处理性能几乎没差
        roleIds.forEach(roleId -> {
            SysUserRole sysUserRole = new SysUserRole();
            sysUserRole.setUserId(userId);
            sysUserRole.setRoleId(roleId);
            sysUserRoleMapper.insert(sysUserRole);
        });
    }
}

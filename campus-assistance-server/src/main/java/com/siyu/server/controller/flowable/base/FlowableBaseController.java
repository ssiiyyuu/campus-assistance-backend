package com.siyu.server.controller.flowable.base;

import com.siyu.common.domain.entity.SysUser;
import com.siyu.common.enums.DepartmentLevel;
import com.siyu.common.enums.ErrorStatus;
import com.siyu.common.exception.BusinessException;
import com.siyu.server.service.SysDepartmentService;
import com.siyu.server.service.SysRoleService;
import com.siyu.server.service.SysUserService;
import com.siyu.shiro.entity.ShiroUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public abstract class FlowableBaseController {
    @Autowired
    protected SysUserService sysUserService;

    @Autowired
    protected SysRoleService sysRoleService;

    /**
     * 判断是否处于统一department
     * @param currentUser
     * @param userId
     * @return
     */

    protected boolean belongTheSameDepartment(ShiroUser currentUser, String userId) {
        SysUser user = sysUserService.getById(userId);
        return user.getDepartmentCode().equals(currentUser.getDepartmentCode());
    }

    /**
     * 根据department判断是否有权限操作 [currentDepartmentLevel > userDepartmentLevel 即有权限]
     * @param currentUser
     * @param userId
     * @return
     */
    protected boolean hasPermission(ShiroUser currentUser, String userId) {
        SysUser user = sysUserService.getById(userId);
        return user != null &&
                SysDepartmentService.isParentDepartment(currentUser.getDepartmentCode(), user.getDepartmentCode());

    }

    /**
     * 根据当前用户获取其父级部门的用户
     * @param currentUser 当前用户
     * @param roleName 待查询用户role
     * @param level 待查询用户部门level
     * @return
     */
    protected SysUser getAssignee(ShiroUser currentUser, String roleName, DepartmentLevel level) {
        String assigneeRoleId = sysRoleService.getByName(roleName).getId();
        String assigneeDepartmentCode = SysDepartmentService.getLevelCode(currentUser.getDepartmentCode(), level);
        List<SysUser> assignees = sysUserService.getByCodeAndRoleId(assigneeDepartmentCode, assigneeRoleId);
        if(null == assignees || assignees.isEmpty()) {
            throw new BusinessException(ErrorStatus.QUERY_ERROR, "本院系暂未设置[" + level.getName() + "]的[" + roleName + "]");
        }
        return assignees.get(0);
    }
}

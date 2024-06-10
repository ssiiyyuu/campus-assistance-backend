package com.siyu.flowable.controller;

import com.siyu.common.domain.PaginationQuery;
import com.siyu.common.domain.PaginationResult;
import com.siyu.common.domain.R;
import com.siyu.common.domain.entity.SysUser;
import com.siyu.common.enums.DepartmentLevel;
import com.siyu.common.enums.ErrorStatus;
import com.siyu.common.service.SysDepartmentService;
import com.siyu.common.service.SysUserService;
import com.siyu.flowable.entity.vo.DormitoryRepairVO;
import com.siyu.flowable.service.DormitoryRepairService;
import com.siyu.common.domain.dto.ShiroUser;
import com.siyu.shiro.utils.ShiroUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Api(tags = "工作流——宿舍报修流程")
@RestController
@RequestMapping("/flowable/dormitoryRepair")
public class DormitoryRepairController {

    @Autowired
    private DormitoryRepairService dormitoryRepairService;

    @Autowired
    private SysUserService sysUserService;

    @GetMapping("/{processId}")
    @ApiOperation("宿舍报修流程详情")
    public R<DormitoryRepairVO.Detail> detail(@PathVariable String processId) {
        DormitoryRepairVO.Detail detail = dormitoryRepairService.detail(processId);
        return R.ok(detail);
    }

    @PostMapping("/page/myCreated")
    @ApiOperation("我发起的请假流程")
    public R<PaginationResult<DormitoryRepairVO.Created>> myCreated(@RequestBody PaginationQuery<?> query) {
        ShiroUser user = ShiroUtils.getCurrentUser();
        PaginationResult<DormitoryRepairVO.Created> result = dormitoryRepairService.myCreated(user.getId(), query);
        return R.ok(result);
    }

    @RequiresRoles("student")
    @PostMapping
    @ApiOperation("学生发起宿舍报修流程")
    public R<String> studentCreate(@RequestBody @Valid DormitoryRepairVO.Create create) {
        ShiroUser user = ShiroUtils.getCurrentUser();
        List<SysUser> assignees = sysUserService.getUserByCodeAndRoleName(SysDepartmentService.getLevelCode(user.getDepartmentCode(), DepartmentLevel.DEPARTMENT), "维修工");
        if(assignees.isEmpty()) {
            return R.fail(ErrorStatus.QUERY_ERROR);
        }
        String assigneeId = assignees.get(0).getId();
        ProcessInstance processInstance = dormitoryRepairService.create(create, user.getId(), assigneeId);
        return R.ok(processInstance.getId());
    }

    @PostMapping("/rate/{taskId}/student/{score}")
    @ApiOperation("学生发起宿舍报修流程")
    public R<String> studentRate(@PathVariable String taskId, @PathVariable Integer score) {
        ShiroUser user = ShiroUtils.getCurrentUser();
        Task task = dormitoryRepairService.rate(taskId, "发起人评分", user.getId(), score);
        return R.ok(task.getProcessInstanceId());
    }

    @PostMapping("/transact/{taskId}/repairman")
    @ApiOperation("维修工办理宿舍报修流程")
    public R<String> departmentCounselorTransact(@PathVariable String taskId, @Valid @RequestBody DormitoryRepairVO.Transact transact) {
        ShiroUser user = ShiroUtils.getCurrentUser();
        Task task = dormitoryRepairService.transact(taskId, "维修工维修", user.getId(), transact);
        return R.ok(task.getProcessInstanceId());
    }

    @PostMapping("/page/myAssigned")
    @ApiOperation("我受理的请假流程")
    public R<PaginationResult<DormitoryRepairVO.Assigned>> myAssigned(@RequestBody PaginationQuery<?> query) {
        ShiroUser user = ShiroUtils.getCurrentUser();
        PaginationResult<DormitoryRepairVO.Assigned> result = dormitoryRepairService.myAssigned(user.getId(), query);
        return R.ok(result);
    }
}

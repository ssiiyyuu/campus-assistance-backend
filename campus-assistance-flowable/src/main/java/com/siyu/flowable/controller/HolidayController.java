package com.siyu.flowable.controller;

import com.siyu.common.domain.PaginationQuery;
import com.siyu.common.domain.PaginationResult;
import com.siyu.common.domain.R;
import com.siyu.common.domain.entity.SysUser;
import com.siyu.common.enums.DepartmentLevel;
import com.siyu.common.enums.ErrorStatus;
import com.siyu.common.service.SysDepartmentService;
import com.siyu.common.service.SysUserService;
import com.siyu.flowable.entity.dto.CommentDTO;
import com.siyu.flowable.entity.vo.HolidayVO;
import com.siyu.flowable.service.HolidayService;
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


@Api(tags = "工作流——请假流程")
@RestController
@RequestMapping("/flowable/holiday")
public class HolidayController {

    @Autowired
    private HolidayService holidayService;

    @Autowired
    private SysUserService sysUserService;

    @GetMapping("/{processId}")
    @ApiOperation("请假流程详情")
    public R<HolidayVO.Detail> detail(@PathVariable String processId) {
        HolidayVO.Detail detail = holidayService.detail(processId);
        return R.ok(detail);
    }

    @RequiresRoles("student")
    @PostMapping
    @ApiOperation("学生发起请假流程")
    public R<String> studentCreate(@RequestBody @Valid HolidayVO.Create create) {
        ShiroUser user = ShiroUtils.getCurrentUser();
        List<SysUser> assignees = sysUserService.getUserByCodeAndRoleName(SysDepartmentService.getLevelCode(user.getDepartmentCode(), DepartmentLevel.GRADE), "年级辅导员");
        if(assignees.isEmpty()) {
            return R.fail(ErrorStatus.QUERY_ERROR);
        }
        String assigneeId = assignees.get(0).getId();
        ProcessInstance processInstance = holidayService.create(create, user.getId(), assigneeId);
        return R.ok(processInstance.getId());
    }

    @DeleteMapping("/{taskId}")
    @ApiOperation("学生请假流程销假")
    public R<String> studentDestroy(@PathVariable String taskId) {
        ShiroUser user = ShiroUtils.getCurrentUser();
        Task task = holidayService.destroy(taskId, "发起人销假", user.getId());
        return R.ok(task.getProcessInstanceId());
    }

    @PostMapping("/page/myCreated")
    @ApiOperation("我发起的请假流程")
    public R<PaginationResult<HolidayVO.Created>> myCreated(@RequestBody PaginationQuery<?> query) {
        ShiroUser user = ShiroUtils.getCurrentUser();
        PaginationResult<HolidayVO.Created> result = holidayService.myCreated(user.getId(), query);
        return R.ok(result);
    }

    @PostMapping("/report/{taskId}/gradeCounselor")
    @ApiOperation("年级辅导员上报请假流程")
    public R<String> gradeCounselorReport(@PathVariable String taskId, @RequestBody @Valid CommentDTO commentDTO) {
        ShiroUser user = ShiroUtils.getCurrentUser();
        List<SysUser> assignees = sysUserService.getUserByCodeAndRoleName(SysDepartmentService.getLevelCode(user.getDepartmentCode(), DepartmentLevel.DEPARTMENT), "系级辅导员");
        if(assignees.isEmpty()) {
            return R.fail(ErrorStatus.QUERY_ERROR);
        }
        String assigneeId = assignees.get(0).getId();
        Task task = holidayService.report(taskId, "年级辅导员审批", user.getId(), assigneeId, commentDTO.getContent());
        return R.ok(task.getProcessInstanceId());
    }

    @PostMapping("/agree/{taskId}/gradeCounselor")
    @ApiOperation("年级辅导员同意请假流程")
    public R<String> gradeCounselorAgree(@PathVariable String taskId) {
        ShiroUser user = ShiroUtils.getCurrentUser();
        Task task = holidayService.agree(taskId, "年级辅导员审批", user.getId());
        return R.ok(task.getProcessInstanceId());
    }

    @PostMapping("/reject/{taskId}/gradeCounselor")
    @ApiOperation("年级辅导员驳回请假流程")
    public R<String> gradeCounselorReject(@PathVariable String taskId, @RequestBody @Valid CommentDTO commentDTO) {
        ShiroUser user = ShiroUtils.getCurrentUser();
        Task task = holidayService.reject(taskId, "年级辅导员审批", user.getId(), commentDTO.getContent());
        return R.ok(task.getProcessInstanceId());
    }

    @PostMapping("/agree/{taskId}/departmentCounselor")
    @ApiOperation("系级辅导员同意请假流程")
    public R<String> departmentCounselorAgree(@PathVariable String taskId) {
        ShiroUser user = ShiroUtils.getCurrentUser();
        Task task = holidayService.agree(taskId, "系级辅导员审批", user.getId());
        return R.ok(task.getProcessInstanceId());
    }

    @PostMapping("/reject/{taskId}/departmentCounselor")
    @ApiOperation("系级辅导员驳回请假流程")
    public R<String> departmentCounselorReject(@PathVariable String taskId, @RequestBody @Valid CommentDTO commentDTO) {
        ShiroUser user = ShiroUtils.getCurrentUser();
        Task task = holidayService.reject(taskId, "系级辅导员审批", user.getId(), commentDTO.getContent());
        return R.ok(task.getProcessInstanceId());
    }

    @PostMapping("/page/myAssigned")
    @ApiOperation("我受理的请假流程")
    public R<PaginationResult<HolidayVO.Assigned>> myAssigned(@RequestBody PaginationQuery<?> query) {
        ShiroUser user = ShiroUtils.getCurrentUser();
        PaginationResult<HolidayVO.Assigned> result = holidayService.myAssigned(user.getId(), query);
        return R.ok(result);
    }

}

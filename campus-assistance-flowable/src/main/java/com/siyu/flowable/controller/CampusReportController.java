package com.siyu.flowable.controller;

import com.siyu.common.domain.PaginationQuery;
import com.siyu.common.domain.PaginationResult;
import com.siyu.common.domain.R;
import com.siyu.common.domain.dto.ShiroUser;
import com.siyu.common.domain.entity.Notification;
import com.siyu.common.domain.entity.SysUser;
import com.siyu.common.domain.vo.SysUserVO;
import com.siyu.common.enums.DepartmentLevel;
import com.siyu.common.enums.ErrorStatus;
import com.siyu.common.enums.NotificationType;
import com.siyu.common.service.NotificationService;
import com.siyu.common.service.SysDepartmentService;
import com.siyu.common.service.SysUserService;
import com.siyu.common.utils.BeanUtils;
import com.siyu.flowable.entity.dto.CommentDTO;
import com.siyu.flowable.entity.vo.CampusReportVO;
import com.siyu.flowable.service.CampusReportService;
import com.siyu.rabbitMQ.service.MQService;
import com.siyu.shiro.utils.ShiroUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;


@Api(tags = "工作流——校园上报流程")
@RestController
@RequestMapping("/flowable/campusReport")
public class CampusReportController {

    @Autowired
    private CampusReportService campusReportService;

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private MQService mqService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private RepositoryService repositoryService;

    @GetMapping("/{processId}")
    @ApiOperation("校园上报流程详情")
    public R<CampusReportVO.Detail> detail(@PathVariable String processId) {
        CampusReportVO.Detail detail = campusReportService.detail(processId);
        return R.ok(detail);
    }

    @PostMapping("/page/myCreated")
    @ApiOperation("我发起的请假流程")
    public R<PaginationResult<CampusReportVO.Created>> myCreated(@RequestBody PaginationQuery<?> query) {
        ShiroUser user = ShiroUtils.getCurrentUser();
        PaginationResult<CampusReportVO.Created> result = campusReportService.myCreated(user.getId(), query);
        return R.ok(result);
    }

    @RequiresRoles("student")
    @PostMapping
    @ApiOperation("学生发起校园上报流程")
    public R<String> studentCreate(@RequestBody @Valid CampusReportVO.Create create) {
        ShiroUser user = ShiroUtils.getCurrentUser();
        List<SysUser> assignees = sysUserService.getUserByCodeAndRoleName(SysDepartmentService.getLevelCode(user.getDepartmentCode(), DepartmentLevel.DEPARTMENT), "系级辅导员");
        if(assignees.isEmpty()) {
            return R.fail(ErrorStatus.QUERY_ERROR);
        }
        String assigneeId = assignees.get(0).getId();
        ProcessInstance processInstance = campusReportService.create(create, user.getId(), assigneeId);
        return R.ok(processInstance.getId());
    }

    @PostMapping("/page/myAssigned")
    @ApiOperation("我受理的请假流程")
    public R<PaginationResult<CampusReportVO.Assigned>> myAssigned(@RequestBody PaginationQuery<?> query) {
        ShiroUser user = ShiroUtils.getCurrentUser();
        PaginationResult<CampusReportVO.Assigned> result = campusReportService.myAssigned(user.getId(), query);
        return R.ok(result);
    }
    
    @PostMapping("/reject/{taskId}/departmentCounselor")
    @ApiOperation("系级辅导员驳回校园上报流程")
    public R<String> departmentCounselorReject(@PathVariable String taskId, @Valid @RequestBody CommentDTO commentDTO) {
        ShiroUser user = ShiroUtils.getCurrentUser();
        Task task = campusReportService.reject(taskId, "系级辅导员受理", user.getId(), commentDTO.getContent());
        return R.ok(task.getProcessInstanceId());
    }

    @PostMapping("/delegate/{taskId}/departmentCounselor/{delegateId}")
    @ApiOperation("系级辅导员委派校园上报流程")
    public R<String> departmentCounselorDelegate(@PathVariable String taskId, @PathVariable String delegateId) {
        ShiroUser user = ShiroUtils.getCurrentUser();
        if(sysUserService.isParentDepartment(user.getDepartmentCode(), delegateId)) {
            Task task = campusReportService.delegate(taskId, "系级辅导员受理", user.getId(), delegateId);
            return R.ok(task.getProcessInstanceId());
        } else {
            return R.fail(ErrorStatus.AUTHOR_ERROR);
        }
    }

    @PostMapping("/transact/{taskId}/departmentCounselor")
    @ApiOperation("系级辅导员办理校园上报流程")
    public R<String> departmentCounselorTransact(@PathVariable String taskId, @Valid @RequestBody CampusReportVO.Transact transact) {
        ShiroUser user = ShiroUtils.getCurrentUser();
        Task task = campusReportService.transact(taskId, "系级辅导员受理", user.getId(), transact);
        return R.ok(task.getProcessInstanceId());
    }

    @PostMapping("/transact/{taskId}/gradeCounselor")
    @ApiOperation("年级辅导员办理校园上报流程")
    public R<String> gradeCounselorTransact(@PathVariable String taskId, @Valid @RequestBody CampusReportVO.Transact transact) {
        ShiroUser user = ShiroUtils.getCurrentUser();
        Task task = campusReportService.transact(taskId, "年级辅导员受理", user.getId(), transact);
        return R.ok(task.getProcessInstanceId());
    }

    @PostMapping("/handover/{taskId}/gradeCounselor/{delegateId}")
    @ApiOperation("年级辅导员移交校园上报流程")
    public R<String> gradeCounselorHandover(@PathVariable String taskId, @PathVariable String delegateId, @Valid @RequestBody CommentDTO commentDTO) {
        ShiroUser user = ShiroUtils.getCurrentUser();

        if(sysUserService.belongTheSameDepartment(user.getDepartmentCode(), delegateId)) {
            Task task = campusReportService.handover(taskId, "年级辅导员受理", user.getId(), delegateId, commentDTO.getContent());

            //发送代办消息
            ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                    .processDefinitionId(task.getProcessDefinitionId())
                    .singleResult();
            Notification notification = new Notification();
            notification.setContent("您有一条流程待办[" + processDefinition.getName() + "]");
            notificationService.setBaseInfo(notification, NotificationType.SYSTEM.name(), "0", delegateId);
            mqService.sendMessageToNotificationExchange(notification);
            return R.ok(task.getProcessInstanceId());
        } else {
            return R.fail(ErrorStatus.AUTHOR_ERROR);
        }
    }

    @GetMapping("/delegator/list")
    @ApiOperation("查询可委派人员")
    public R<List<SysUserVO.Out>> getDelegator() {
        ShiroUser currentUser = ShiroUtils.getCurrentUser();
        List<SysUserVO.Out> delegators = sysUserService.getUserByPrefixCodeAndRoleName(currentUser.getDepartmentCode(), "年级辅导员")
                .stream().map(item -> BeanUtils.copyProperties(item, new SysUserVO.Out()))
                .collect(Collectors.toList());
        return R.ok(delegators);
    }

    @GetMapping("/handover/list")
    @ApiOperation("查询可移交人员")
    public R<List<SysUserVO.Out>> getHandovers() {
        ShiroUser currentUser = ShiroUtils.getCurrentUser();
        List<SysUserVO.Out> Handovers = sysUserService.getUserByCodeAndRoleName(currentUser.getDepartmentCode(), "年级辅导员")
                .stream().filter(item -> !item.getId().equals(currentUser.getId()))
                .map(item -> BeanUtils.copyProperties(item, new SysUserVO.Out()))
                .collect(Collectors.toList());
        return R.ok(Handovers);
    }
}

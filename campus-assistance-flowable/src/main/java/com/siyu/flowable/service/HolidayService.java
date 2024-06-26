package com.siyu.flowable.service;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.siyu.flowable.constants.FlowableConstants;
import com.siyu.common.domain.PaginationQuery;
import com.siyu.common.domain.PaginationResult;
import com.siyu.common.domain.dto.SysUserBaseDTO;
import com.siyu.common.enums.ErrorStatus;
import com.siyu.common.enums.flowable.HolidayStatus;
import com.siyu.common.exception.BusinessException;
import com.siyu.common.service.SysUserService;
import com.siyu.flowable.entity.dto.AttachmentDTO;
import com.siyu.flowable.entity.dto.CommentDTO;
import com.siyu.flowable.entity.mapper.AttachmentDTOMapper;
import com.siyu.flowable.entity.mapper.CommentDTOMapper;
import com.siyu.flowable.entity.vo.HolidayVO;
import org.flowable.common.engine.impl.identity.Authentication;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.history.HistoricProcessInstanceQuery;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.engine.task.Attachment;
import org.flowable.engine.task.Comment;
import org.flowable.task.api.Task;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.flowable.task.api.history.HistoricTaskInstanceQuery;
import org.flowable.variable.api.history.HistoricVariableInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class HolidayService {

    private static final String TYPE_KEY = "type";
    private static final String REASON_KEY = "reason";
    private static final String START_TIME_KEY = "start_time";
    private static final String END_TIME_KEY = "end_time";
    private static final String DESTROY_TIME_KEY = "destroy_time";
    private static final String STATUS_KEY = "status";

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    protected TaskService taskService;

    @Autowired
    protected RepositoryService repositoryService;

    @Autowired
    protected HistoryService historyService;

    @Autowired
    protected RuntimeService runtimeService;
    public ProcessInstance create(HolidayVO.Create create, String userId, String assigneeId) {

        Map<String, Object> map = new HashMap<>();
        //assignee
        map.put(FlowableConstants.ASSIGNEE_VARIABLE_KEY, assigneeId);
        //data
        map.put(TYPE_KEY, create.getType());
        map.put(REASON_KEY, create.getReason());
        map.put(START_TIME_KEY, create.getStartTime());
        map.put(END_TIME_KEY, create.getEndTime());
        //status
        map.put(STATUS_KEY, HolidayStatus.APPLYING.getStatus());


        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey(FlowableConstants.HOLIDAY_PROCESS_KEY)
                .latestVersion()
                .singleResult();

        Authentication.setAuthenticatedUserId(userId);
        ProcessInstance processInstance = runtimeService.startProcessInstanceById(processDefinition.getId(), map);
        Authentication.setAuthenticatedUserId(null);

        List<AttachmentDTO> attachments = create.getAttachments();
        for (AttachmentDTO attachment : attachments) {
            taskService.createAttachment(attachment.getType(), null, processInstance.getId(), FlowableConstants.HOLIDAY_ATTACHMENT_NAME, null, attachment.getContent());
        }
        return processInstance;
    }


    public PaginationResult<HolidayVO.Created> myCreated(String id, PaginationQuery<?> query) {
        HistoricProcessInstanceQuery processInstanceQuery = historyService.createHistoricProcessInstanceQuery()
                .processDefinitionKey(FlowableConstants.HOLIDAY_PROCESS_KEY)
                .startedBy(id);
        long total = processInstanceQuery.count();
        List<HistoricProcessInstance> processInstances = processInstanceQuery
                .orderByProcessInstanceStartTime()
                .desc()
                .listPage((query.getPageNum() - 1) * query.getPageSize(), query.getPageSize());

        List<HolidayVO.Created> result = processInstances.stream().map(process -> {
            Map<String, Object> variables = historyService.createHistoricVariableInstanceQuery()
                    .processInstanceId(process.getId())
                    .list().stream()
                    .collect(Collectors.toMap(HistoricVariableInstance::getVariableName, HistoricVariableInstance::getValue));

            HolidayVO.Created created = new HolidayVO.Created();
            created.setType((String) variables.get(TYPE_KEY));
            created.setStatus((String) variables.get(STATUS_KEY));
            created.setStartTime((LocalDateTime) variables.get(START_TIME_KEY));
            created.setEndTime((LocalDateTime) variables.get(END_TIME_KEY));
            created.setDestroyTime((LocalDateTime) variables.get(DESTROY_TIME_KEY));
            created.setCreateTime(DateUtil.toLocalDateTime(process.getStartTime()));
            created.setProcessId(process.getId());
            if(null == process.getEndTime()) {
                Task task = taskService.createTaskQuery()
                        .processInstanceId(process.getId())
                        .active()
                        .singleResult();
                created.setTaskId(task.getId());
            } else {
                created.setTaskId("-1");
            }
            return created;
        }).collect(Collectors.toList());

        Page<?> page = new Page<>(query.getPageNum(), query.getPageSize(), total);
        return PaginationResult.of(page, result);
    }
    public PaginationResult<HolidayVO.Assigned> myAssigned(String id, PaginationQuery<?> query) {
        HistoricTaskInstanceQuery taskInstanceQuery = historyService.createHistoricTaskInstanceQuery()
                .processDefinitionKey(FlowableConstants.HOLIDAY_PROCESS_KEY)
                .taskAssignee(id);
        long total = taskInstanceQuery.count();
        List<HistoricTaskInstance> taskInstances = taskInstanceQuery
                .orderByHistoricTaskInstanceStartTime()
                .desc()
                .listPage((query.getPageNum() - 1) * query.getPageSize(), query.getPageSize());
        List<HolidayVO.Assigned> result = taskInstances.stream().map(task -> {
            HistoricProcessInstance process = historyService.createHistoricProcessInstanceQuery()
                    .processInstanceId(task.getProcessInstanceId())
                    .singleResult();
            Map<String, Object> variables = historyService.createHistoricVariableInstanceQuery()
                    .processInstanceId(process.getId())
                    .list().stream()
                    .collect(Collectors.toMap(HistoricVariableInstance::getVariableName, HistoricVariableInstance::getValue));

            HolidayVO.Assigned assigned = new HolidayVO.Assigned();
            assigned.setType((String) variables.get(TYPE_KEY));
            assigned.setStatus((String) variables.get(STATUS_KEY));
            assigned.setStartTime((LocalDateTime) variables.get(START_TIME_KEY));
            assigned.setEndTime((LocalDateTime) variables.get(END_TIME_KEY));
            assigned.setDestroyTime((LocalDateTime) variables.get(DESTROY_TIME_KEY));
            assigned.setCreatTime(DateUtil.toLocalDateTime(process.getStartTime()));
            assigned.setProcessId(process.getId());
            assigned.setInitiator(sysUserService.getById(process.getStartUserId()).getNickname());
            if (null == process.getEndTime()) {
                Task curTask = taskService.createTaskQuery()
                        .processInstanceId(process.getId())
                        .active()
                        .singleResult();
                assigned.setTaskId(task.getId());
                assigned.setIsAssigned(!curTask.getId().equals(task.getId()));
            } else {
                assigned.setTaskId("-1");
                assigned.setIsAssigned(true);
            }
            return assigned;
        }).sorted((o1, o2) -> {
            if (o1.getIsAssigned() && o2.getIsAssigned()) {
                return o1.getCreatTime().compareTo(o2.getCreatTime());
            } else if (o1.getIsAssigned()) {
                return 1;
            } else {
                return -1;
            }
        }).collect(Collectors.toList());

        Page<?> page = new Page<>(query.getPageNum(), query.getPageSize(), total);
        return PaginationResult.of(page, result);
    }

    public Task report(String taskId, String taskName, String userId, String assigneeId, String comment) {
        Task task = getTask(taskId, taskName, userId);
        taskService.addComment(task.getId(), task.getProcessInstanceId(), FlowableConstants.REPORT_COMMENT_TYPE, comment);

        Map<String, Object> map = new HashMap<>();
        //assignee
        map.put(FlowableConstants.ASSIGNEE_VARIABLE_KEY, assigneeId);
        //gateway
        map.put(FlowableConstants.BRANCH_VARIABLE_KEY, FlowableConstants.REPORT);
        taskService.complete(task.getId(), map);
        return task;
    }

    public Task agree(String taskId, String taskName, String userId) {
        Task task = getTask(taskId, taskName, userId);
        taskService.addComment(task.getId(), task.getProcessInstanceId(), FlowableConstants.BASE_COMMENT_TYPE, "同意休假");

        Map<String, Object> map = new HashMap<>();
        //gateway
        map.put(FlowableConstants.BRANCH_VARIABLE_KEY, FlowableConstants.AGREE);
        //status
        map.put(STATUS_KEY, HolidayStatus.LEAVING.getStatus());
        taskService.complete(task.getId(), map);

        return task;
    }

    public Task reject(String taskId, String taskName, String userId, String comment) {
        Task task = getTask(taskId, taskName, userId);
        taskService.addComment(task.getId(), task.getProcessInstanceId(), FlowableConstants.BASE_COMMENT_TYPE, comment);

        Map<String, Object> map = new HashMap<>();
        //gateway
        map.put(FlowableConstants.BRANCH_VARIABLE_KEY, FlowableConstants.REJECT);
        //status
        map.put(STATUS_KEY, HolidayStatus.REJECTED.getStatus());
        taskService.complete(task.getId(), map);

        return task;
    }


    public Task destroy(String taskId, String taskName, String userId) {
        Task task = getTask(taskId, taskName, userId);

        Map<String, Object> map = new HashMap<>();
        //data
        map.put(DESTROY_TIME_KEY, LocalDateTime.now());
        //gateway
        map.put(FlowableConstants.BRANCH_VARIABLE_KEY, FlowableConstants.REJECT);
        //status
        map.put(STATUS_KEY, HolidayStatus.LEAVED.getStatus());
        taskService.complete(task.getId(), map);

        return task;
    }

    public HolidayVO.Detail detail(String processId) {
        HistoricProcessInstance processInstance = historyService.createHistoricProcessInstanceQuery()
                .processInstanceId(processId)
                .singleResult();
        Map<String, Object> variables = historyService.createHistoricVariableInstanceQuery()
                .processInstanceId(processId).list()
                .stream()
                .collect(Collectors.toMap(HistoricVariableInstance::getVariableName, HistoricVariableInstance::getValue));
        Map<String, List<Attachment>> attachments = taskService.getProcessInstanceAttachments(processId).stream()
                .collect(Collectors.groupingBy(Attachment::getName));
        List<Comment> comments = taskService.getProcessInstanceComments(processId, FlowableConstants.BASE_COMMENT_TYPE);

        //请假信息
        HolidayVO.Create create = new HolidayVO.Create();
        create.setType((String) variables.get(TYPE_KEY));
        create.setReason((String) variables.get(REASON_KEY));
        create.setStartTime((LocalDateTime) variables.get(START_TIME_KEY));
        create.setEndTime((LocalDateTime) variables.get(END_TIME_KEY));

        List<AttachmentDTO> attachmentDTOS = Optional.ofNullable(attachments.get(FlowableConstants.HOLIDAY_ATTACHMENT_NAME)).orElseGet(ArrayList::new)
                .stream()
                .map(item -> AttachmentDTOMapper.copy(item, new AttachmentDTO()))
                .collect(Collectors.toList());
        create.setAttachments(attachmentDTOS);

        //销假信息
        HolidayVO.Destroy destroy = new HolidayVO.Destroy();
        destroy.setDestroyTime((LocalDateTime) variables.get(DESTROY_TIME_KEY));

        //办理信息
        List<CommentDTO> commentDTOS = comments.stream().map(item -> CommentDTOMapper.copy(item, new CommentDTO()))
                .collect(Collectors.toList());

        //发起人信息
        SysUserBaseDTO initiator = sysUserService.getBaseUserById(processInstance.getStartUserId());

        HolidayVO.Detail detail = new HolidayVO.Detail();
        detail.setCreate(create);
        detail.setDestroy(destroy);
        detail.setComments(commentDTOS);
        detail.setInitiator(initiator);
        detail.setStatus((String) variables.get(STATUS_KEY));
        detail.setCreateTime(DateUtil.toLocalDateTime(processInstance.getStartTime()));

        return detail;
    }

    public Task getTask(String taskId, String taskName, String userId) {
        Task task = taskService.createTaskQuery()
                .taskId(taskId)
                .taskName(taskName)
                .taskAssignee(userId)
                .singleResult();
        if(null == task) {
            throw new BusinessException(ErrorStatus.QUERY_ERROR, " 您未受理id为[" + taskId + "]的任务");
        }
        return task;
    }

}

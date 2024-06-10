package com.siyu.flowable.service;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.siyu.flowable.constants.FlowableConstants;
import com.siyu.common.domain.PaginationQuery;
import com.siyu.common.domain.PaginationResult;
import com.siyu.common.domain.dto.SysUserBaseDTO;
import com.siyu.common.enums.ErrorStatus;
import com.siyu.common.enums.flowable.CampusReportStatus;
import com.siyu.common.exception.BusinessException;
import com.siyu.common.service.SysUserService;
import com.siyu.flowable.entity.CampusReportEvent;
import com.siyu.flowable.entity.dto.AttachmentDTO;
import com.siyu.flowable.entity.dto.CommentDTO;
import com.siyu.flowable.entity.mapper.AttachmentDTOMapper;
import com.siyu.flowable.entity.mapper.CommentDTOMapper;
import com.siyu.flowable.entity.vo.CampusReportVO;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CampusReportService {

    private static final String EVENT_ID_KEY = "event_id";
    private static final String DESCRIPTION_KEY = "description";
    private static final String CREATE_LOCATION_KEY = "create_location";
    private static final String STATUS_KEY = "status";

    @Autowired
    private CampusReportEventService campusReportEventService;

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

    public ProcessInstance create(CampusReportVO.Create create, String userId, String assigneeId) {

        CampusReportEvent event = campusReportEventService.getById(create.getEventId());
        if(event == null) {
            throw new BusinessException(ErrorStatus.INSERT_ERROR);
        }
        Map<String, Object> map = new HashMap<>();
        //assignee
        map.put(FlowableConstants.ASSIGNEE_VARIABLE_KEY, assigneeId);
        //data
        map.put(EVENT_ID_KEY, create.getEventId());
        map.put(DESCRIPTION_KEY, create.getDescription());
        map.put(CREATE_LOCATION_KEY, create.getCreateLocation());
        //status
        map.put(STATUS_KEY, CampusReportStatus.REPORTED.getStatus());

        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey(FlowableConstants.CAMPUS_REPORT_PROCESS_KEY)
                .latestVersion()
                .singleResult();

        Authentication.setAuthenticatedUserId(userId);
        ProcessInstance processInstance = runtimeService.startProcessInstanceById(processDefinition.getId(), map);
        Authentication.setAuthenticatedUserId(null);

        List<AttachmentDTO> attachments = create.getAttachments();
        for (AttachmentDTO attachment : attachments) {
            taskService.createAttachment(attachment.getType(), null, processInstance.getId(), FlowableConstants.CREATE_ATTACHMENT_NAME, null, attachment.getContent());
        }
        return processInstance;
    }

    public Task transact(String taskId, String taskName, String userId, CampusReportVO.Transact transact) {
        Task task = getTask(taskId, taskName, userId);

        //办理comment 与 办理attachment
        taskService.addComment(task.getId(), task.getProcessInstanceId(), FlowableConstants.BASE_COMMENT_TYPE, transact.getComment().getContent());
        List<AttachmentDTO> attachments = transact.getAttachments();
        for (AttachmentDTO attachment : attachments) {
            taskService.createAttachment(attachment.getType(), task.getId(), task.getProcessInstanceId(), FlowableConstants.DONE_ATTACHMENT_NAME, null, attachment.getContent());
        }

        Map<String, Object> map = new HashMap<>();
        //gateway
        map.put(FlowableConstants.BRANCH_VARIABLE_KEY, FlowableConstants.TRANSACT);
        //status
        map.put(STATUS_KEY, CampusReportStatus.DONE.getStatus());
        taskService.complete(task.getId(), map);

        return task;
    }

    public Task delegate(String taskId, String taskName, String userId, String delegateId) {

        Task task = getTask(taskId, taskName, userId);

        Map<String, Object> map = new HashMap<>();
        //gateway
        map.put(FlowableConstants.BRANCH_VARIABLE_KEY, FlowableConstants.DELEGATE);
        //assignee
        map.put(FlowableConstants.ASSIGNEE_VARIABLE_KEY, delegateId);

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
        map.put(STATUS_KEY, CampusReportStatus.REJECTED.getStatus());
        taskService.complete(task.getId(), map);

        return task;
    }

    public Task handover(String taskId, String taskName, String userId, String delegateId, String comment) {
        Task task = getTask(taskId, taskName, userId);
        taskService.addComment(task.getId(), task.getProcessInstanceId(), FlowableConstants.HANDOVER_COMMENT_TYPE, comment);
        taskService.setAssignee(taskId, delegateId);

        return task;
    }

    public CampusReportVO.Detail detail(String processId) {
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
        CampusReportVO.Create create = new CampusReportVO.Create();
        String eventId = (String) variables.get(EVENT_ID_KEY);
        create.setEventId(eventId);
        CampusReportEvent event = campusReportEventService.getById(eventId);
        create.setEvent(event.getName());
        create.setLevel(event.getLevel());
        create.setDescription((String) variables.get(DESCRIPTION_KEY));
        create.setCreateLocation((String) variables.get(CREATE_LOCATION_KEY));
        List<AttachmentDTO> attachmentCreateDTOS = attachments.get(FlowableConstants.CREATE_ATTACHMENT_NAME).stream()
                .map(item -> AttachmentDTOMapper.copy(item, new AttachmentDTO()))
                .collect(Collectors.toList());
        create.setAttachments(attachmentCreateDTOS);

        //办理信息
        CampusReportVO.Transact transact = new CampusReportVO.Transact();
        List<AttachmentDTO> attachmentDoneDTOS = attachments.get(FlowableConstants.DONE_ATTACHMENT_NAME).stream()
                .map(item -> AttachmentDTOMapper.copy(item, new AttachmentDTO()))
                .collect(Collectors.toList());
        transact.setAttachments(attachmentDoneDTOS);
        List<CommentDTO> commentDTOS = comments.stream().map(item -> CommentDTOMapper.copy(item, new CommentDTO()))
                .collect(Collectors.toList());
        if(!commentDTOS.isEmpty()) {
            transact.setComment(commentDTOS.get(0));
        }

        //发起人信息
        SysUserBaseDTO initiator = sysUserService.getBaseUserById(processInstance.getStartUserId());

        CampusReportVO.Detail detail = new CampusReportVO.Detail();
        detail.setCreate(create);
        detail.setTransact(transact);
        detail.setInitiator(initiator);
        detail.setStatus((String) variables.get(STATUS_KEY));
        detail.setCreateTime(DateUtil.toLocalDateTime(processInstance.getStartTime()));

        return detail;
    }

    public PaginationResult<CampusReportVO.Created> myCreated(String id, PaginationQuery<?> query) {
        HistoricProcessInstanceQuery processInstanceQuery = historyService.createHistoricProcessInstanceQuery()
                .processDefinitionKey(FlowableConstants.CAMPUS_REPORT_PROCESS_KEY)
                .startedBy(id);
        long total = processInstanceQuery.count();
        List<HistoricProcessInstance> processInstances = processInstanceQuery
                .orderByProcessInstanceStartTime()
                .desc()
                .listPage((query.getPageNum() - 1) * query.getPageSize(), query.getPageSize());

        List<CampusReportVO.Created> result = processInstances.stream().map(process -> {
            Map<String, Object> variables = historyService.createHistoricVariableInstanceQuery()
                    .processInstanceId(process.getId())
                    .list().stream()
                    .collect(Collectors.toMap(HistoricVariableInstance::getVariableName, HistoricVariableInstance::getValue));

            CampusReportVO.Created created = new CampusReportVO.Created();
            String eventId = (String) variables.get(EVENT_ID_KEY);
            CampusReportEvent event = campusReportEventService.getById(eventId);
            created.setEvent(event.getName());
            created.setLevel(event.getLevel());
            created.setDescription((String) variables.get(DESCRIPTION_KEY));
            created.setStatus((String) variables.get(STATUS_KEY));
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

    public PaginationResult<CampusReportVO.Assigned> myAssigned(String id, PaginationQuery<?> query) {
        HistoricTaskInstanceQuery taskInstanceQuery = historyService.createHistoricTaskInstanceQuery()
                .processDefinitionKey(FlowableConstants.HOLIDAY_PROCESS_KEY)
                .taskAssignee(id);
        long total = taskInstanceQuery.count();
        List<HistoricTaskInstance> taskInstances = taskInstanceQuery
                .orderByHistoricTaskInstanceStartTime()
                .desc()
                .listPage((query.getPageNum() - 1) * query.getPageSize(), query.getPageSize());
        List<CampusReportVO.Assigned> result = taskInstances.stream().map(task -> {
            HistoricProcessInstance process = historyService.createHistoricProcessInstanceQuery()
                    .processInstanceId(task.getProcessInstanceId())
                    .singleResult();
            Map<String, Object> variables = historyService.createHistoricVariableInstanceQuery()
                    .processInstanceId(process.getId())
                    .list().stream()
                    .collect(Collectors.toMap(HistoricVariableInstance::getVariableName, HistoricVariableInstance::getValue));

            CampusReportVO.Assigned assigned = new CampusReportVO.Assigned();
            String eventId = (String) variables.get(EVENT_ID_KEY);
            CampusReportEvent event = campusReportEventService.getById(eventId);
            assigned.setEvent(event.getName());
            assigned.setLevel(event.getLevel());
            assigned.setDescription((String) variables.get(DESCRIPTION_KEY));
            assigned.setStatus((String) variables.get(STATUS_KEY));
            assigned.setCreateTime(DateUtil.toLocalDateTime(process.getStartTime()));
            assigned.setProcessId(process.getId());
            assigned.setInitiator(sysUserService.getById(process.getStartUserId()).getNickname());
            if(null == process.getEndTime()) {
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
        }).collect(Collectors.toList());

        Page<?> page = new Page<>(query.getPageNum(), query.getPageSize(), total);
        return PaginationResult.of(page, result);
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

package com.siyu.server.service.flowable;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.siyu.common.constants.FlowableConstants;
import com.siyu.common.domain.PaginationQuery;
import com.siyu.common.domain.PaginationResult;
import com.siyu.common.enums.flowable.DormitoryRepairStatus;
import com.siyu.server.entity.dto.AttachmentDTO;
import com.siyu.server.entity.dto.SysUserBaseDTO;
import com.siyu.server.entity.dto.flowable.CommentDTO;
import com.siyu.server.entity.mapper.AttachmentDTOMapper;
import com.siyu.server.entity.mapper.CommentDTOMapper;
import com.siyu.server.entity.vo.flowable.DormitoryRepairVO;
import com.siyu.server.service.SysUserService;
import com.siyu.server.service.flowable.base.FlowableBaseService;
import org.flowable.common.engine.impl.identity.Authentication;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DormitoryRepairService extends FlowableBaseService {

    public static final String TYPE_KEY = "type";
    private static final String DESCRIPTION_KEY = "description";
    private static final String CREATE_LOCATION_KEY = "create_location";
    private static final String STATUS_KEY = "status";
    private static final String SCORE_KEY = "score";
    private static final String TRANSACTOR_ID_KEY = "transactor_id";
    private static final String TRANSACT_TIME_KEY = "transact_time";
    
    @Autowired
    private SysUserService sysUserService;

    public ProcessInstance create(DormitoryRepairVO.Create create, String userId, String assigneeId) {
        Map<String, Object> map = new HashMap<>();
        //assignee
        map.put(FlowableConstants.ASSIGNEE_VARIABLE_KEY, assigneeId);
        //data
        map.put(TYPE_KEY, create.getType());
        map.put(DESCRIPTION_KEY, create.getDescription());
        map.put(CREATE_LOCATION_KEY, create.getCreateLocation());
        //status
        map.put(STATUS_KEY, DormitoryRepairStatus.REPORTED.getStatus());

        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey(FlowableConstants.DORMITORY_REPAIR_PROCESS_KEY)
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

    public Task transact(String taskId, String taskName, String userId, DormitoryRepairVO.Transact transact) {
        Task task = getTask(taskId, taskName, userId);

        //办理comment 与 办理attachment
        taskService.addComment(task.getId(), task.getProcessInstanceId(), FlowableConstants.BASE_COMMENT_TYPE, transact.getComment().getContent());
        List<AttachmentDTO> attachments = transact.getAttachments();
        for (AttachmentDTO attachment : attachments) {
            taskService.createAttachment(attachment.getType(), task.getId(), task.getProcessInstanceId(), FlowableConstants.DONE_ATTACHMENT_NAME, null, attachment.getContent());
        }

        Map<String, Object> map = new HashMap<>();
        //data
        map.put(TRANSACTOR_ID_KEY, userId);
        map.put(TRANSACT_TIME_KEY, LocalDateTime.now());
        //status
        map.put(STATUS_KEY, DormitoryRepairStatus.REPAIRED.getStatus());
        taskService.complete(task.getId(), map);

        return task;
    }

    public Task rate(String taskId, String taskName, String userId, Integer score) {
        Task task = getTask(taskId, taskName, userId);

        Map<String, Object> map = new HashMap<>();
        //data
        map.put(SCORE_KEY, score);
        //status
        map.put(STATUS_KEY, DormitoryRepairStatus.RATED.getStatus());
        taskService.complete(task.getId(), map);

        return task;
    }

    public DormitoryRepairVO.Detail detail(String processId) {
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

        //报修信息
        DormitoryRepairVO.Create create = new DormitoryRepairVO.Create();
        create.setType((String) variables.get(TYPE_KEY));
        create.setDescription((String) variables.get(DESCRIPTION_KEY));
        create.setCreateLocation((String) variables.get(CREATE_LOCATION_KEY));
        List<AttachmentDTO> attachmentCreateDTOS = attachments.get(FlowableConstants.CREATE_ATTACHMENT_NAME).stream()
                .map(item -> AttachmentDTOMapper.copy(item, new AttachmentDTO()))
                .collect(Collectors.toList());
        create.setAttachments(attachmentCreateDTOS);

        //办理信息
        DormitoryRepairVO.Transact transact = new DormitoryRepairVO.Transact();
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
        //办理人信息
        SysUserBaseDTO transactor = sysUserService.getBaseUserById((String) variables.get(TRANSACTOR_ID_KEY));

        DormitoryRepairVO.Detail detail = new DormitoryRepairVO.Detail();
        detail.setCreate(create);
        detail.setTransact(transact);
        detail.setInitiator(initiator);
        detail.setTransactor(transactor);
        detail.setScore((Integer) variables.get(SCORE_KEY));
        detail.setStatus((String) variables.get(STATUS_KEY));
        detail.setCreateTime(DateUtil.toLocalDateTime(processInstance.getStartTime()));
        detail.setTransactTime((LocalDateTime) variables.get(TRANSACT_TIME_KEY));
        return detail;
    }

    public PaginationResult<DormitoryRepairVO.Created> myCreated(String id, PaginationQuery<?> query) {
        HistoricProcessInstanceQuery processInstanceQuery = historyService.createHistoricProcessInstanceQuery()
                .processDefinitionKey(FlowableConstants.DORMITORY_REPAIR_PROCESS_KEY)
                .startedBy(id);
        long total = processInstanceQuery.count();
        List<HistoricProcessInstance> processInstances = processInstanceQuery
                .orderByProcessInstanceStartTime()
                .desc()
                .listPage((query.getPageNum() - 1) * query.getPageSize(), query.getPageSize());

        List<DormitoryRepairVO.Created> result = processInstances.stream().map(process -> {
            Map<String, Object> variables = historyService.createHistoricVariableInstanceQuery()
                    .processInstanceId(process.getId())
                    .list().stream()
                    .collect(Collectors.toMap(HistoricVariableInstance::getVariableName, HistoricVariableInstance::getValue));

            DormitoryRepairVO.Created created = new DormitoryRepairVO.Created();
            created.setDescription((String) variables.get(DESCRIPTION_KEY));
            created.setStatus((String) variables.get(STATUS_KEY));
            created.setType((String) variables.get(TYPE_KEY));
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

    public PaginationResult<DormitoryRepairVO.Assigned> myAssigned(String id, PaginationQuery<?> query) {
        HistoricTaskInstanceQuery taskInstanceQuery = historyService.createHistoricTaskInstanceQuery()
                .processDefinitionKey(FlowableConstants.DORMITORY_REPAIR_PROCESS_KEY)
                .taskAssignee(id);
        long total = taskInstanceQuery.count();
        List<HistoricTaskInstance> taskInstances = taskInstanceQuery
                .orderByHistoricTaskInstanceStartTime()
                .desc()
                .listPage((query.getPageNum() - 1) * query.getPageSize(), query.getPageSize());
        List<DormitoryRepairVO.Assigned> result = taskInstances.stream().map(task -> {
            HistoricProcessInstance process = historyService.createHistoricProcessInstanceQuery()
                    .processInstanceId(task.getProcessInstanceId())
                    .singleResult();
            Map<String, Object> variables = historyService.createHistoricVariableInstanceQuery()
                    .processInstanceId(process.getId())
                    .list().stream()
                    .collect(Collectors.toMap(HistoricVariableInstance::getVariableName, HistoricVariableInstance::getValue));

            DormitoryRepairVO.Assigned assigned = new DormitoryRepairVO.Assigned();
            assigned.setType((String) variables.get(TYPE_KEY));
            assigned.setDescription((String) variables.get(DESCRIPTION_KEY));
            assigned.setStatus((String) variables.get(STATUS_KEY));
            assigned.setScore((Integer) variables.get(SCORE_KEY));
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
}

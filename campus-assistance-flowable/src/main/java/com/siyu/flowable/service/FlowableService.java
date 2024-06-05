package com.siyu.flowable.service;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.siyu.common.domain.PaginationQuery;
import com.siyu.common.domain.PaginationResult;
import com.siyu.common.enums.ErrorStatus;
import com.siyu.common.exception.BusinessException;
import com.siyu.common.service.SysUserService;
import com.siyu.common.utils.BeanUtils;
import com.siyu.flowable.entity.dto.CommentDTO;
import com.siyu.flowable.entity.mapper.CommentDTOMapper;
import com.siyu.flowable.entity.vo.FlowableVO;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.history.HistoricProcessInstanceQuery;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.DeploymentQuery;
import org.flowable.engine.task.Comment;
import org.flowable.image.impl.DefaultProcessDiagramGenerator;
import org.flowable.task.api.Task;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FlowableService {
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


    /*------------------------deployment----------------------- **/
    public Deployment deployDeployment(MultipartFile file) {
        try {
            Deployment deployment = repositoryService.createDeployment()
                    .addInputStream(file.getOriginalFilename(), file.getInputStream())
                    .deploy();
            return deployment;
        } catch (IOException e) {
            throw new BusinessException(ErrorStatus.IO_ERROR);
        }
    }

    public void deleteDeployment(String id) {
        repositoryService.deleteDeployment(id, true);
    }

    public InputStream getDiagram(String processId) {
        HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
                .processInstanceId(processId)
                .singleResult();
        BpmnModel bpmnModel = repositoryService.getBpmnModel(historicProcessInstance.getProcessDefinitionId());
        DefaultProcessDiagramGenerator defaultProcessDiagramGenerator = new DefaultProcessDiagramGenerator();
        List<String> highLightedActivities = new ArrayList<>();
        List<String> highLightedFlows = new ArrayList<>();
        List<HistoricActivityInstance> historicActivityInstances = historyService.createHistoricActivityInstanceQuery()
                .processInstanceId(processId)
                .list();
        historicActivityInstances.forEach(historicActivityInstance -> {
            if("sequenceFlow".equals(historicActivityInstance.getActivityType())) {
                highLightedFlows.add(historicActivityInstance.getActivityId());
            } else {
                highLightedActivities.add(historicActivityInstance.getActivityId());
            }
        });
        InputStream inputStream = defaultProcessDiagramGenerator.generateDiagram(bpmnModel, "png", highLightedActivities, highLightedFlows,
                "宋体", "微软雅黑", "宋体", null,
                1.0d, true);
        return inputStream;
    }

    public PaginationResult<FlowableVO.Deployment> pageDeployment(PaginationQuery<?> query) {
        DeploymentQuery deploymentQuery = repositoryService.createDeploymentQuery()
                .latest();
        long total = deploymentQuery.count();
        List<FlowableVO.Deployment> result = deploymentQuery
                .listPage((query.getPageNum() - 1) * query.getPageSize(), query.getPageSize()).stream()
                .map(item -> BeanUtils.copyProperties(item, new FlowableVO.Deployment()))
                .collect(Collectors.toList());
        Page<?> page = new Page<>(query.getPageNum(), query.getPageSize(), total);
        return PaginationResult.of(page, result);
    }

    /*------------------------process----------------------- **/
    public void deleteProcess(String processId) {
        HistoricProcessInstance processInstance = historyService.createHistoricProcessInstanceQuery()
                .processInstanceId(processId).singleResult();
        if(null == processInstance.getEndTime()) {
            runtimeService.deleteProcessInstance(processId, "手动删除");
            historyService.deleteHistoricProcessInstance(processId);
        } else {
            historyService.deleteHistoricProcessInstance(processId);
        }
    }

    public List<FlowableVO.Stage> stageProcess(String processId) {
        List<HistoricTaskInstance> tasks = historyService.createHistoricTaskInstanceQuery()
                .processInstanceId(processId)
                .orderByHistoricTaskInstanceStartTime().desc()
                .list();
        Map<String, List<Comment>> commentMap = taskService.getProcessInstanceComments(processId).stream()
                .filter(item -> !"event".equals(item.getType()))
                .collect(Collectors.groupingBy(Comment::getTaskId));
        List<FlowableVO.Stage> result = tasks.stream().map(task -> {
            FlowableVO.Stage stage = new FlowableVO.Stage();
            List<Comment> comments = commentMap.get(task.getId());
            if (null != comments) {
                List<CommentDTO> commentDTOs = comments.stream()
                        .sorted(Comparator.comparing(Comment::getTime))
                        .map(item -> CommentDTOMapper.copy(item, new CommentDTO()))
                        .collect(Collectors.toList());
                stage.setComments(commentDTOs);
            }
            stage.setName(task.getName());
            stage.setStartTime(DateUtil.toLocalDateTime(task.getCreateTime()));
            stage.setEndTime(DateUtil.toLocalDateTime(task.getEndTime()));
            stage.setDuration(task.getDurationInMillis());
            stage.setAssignee(sysUserService.getById(task.getAssignee()).getNickname());
            return stage;
        }).collect(Collectors.toList());
        return result;
    }

    public PaginationResult<FlowableVO.Table> pageProcess(PaginationQuery<FlowableVO.Condition> query) {
        FlowableVO.Condition condition = query.getCondition();
        HistoricProcessInstanceQuery processInstanceQuery = historyService.createHistoricProcessInstanceQuery();
        if(StringUtils.hasText(condition.getDefinitionKey())) {
            processInstanceQuery.processDefinitionKey(condition.getDefinitionKey());
        }
        if(null != condition.getStartTime() && condition.getStartTime().size() == 2) {
            ZoneId zoneId = ZoneId.systemDefault();
            ZonedDateTime startZonedDateTime = condition.getStartTime().get(0).atZone(zoneId);
            ZonedDateTime endZonedDateTime = condition.getStartTime().get(1).atZone(zoneId);
            Date startDate = Date.from(startZonedDateTime.toInstant());
            Date endDate = Date.from(endZonedDateTime.toInstant());
            processInstanceQuery.startedAfter(startDate);
            processInstanceQuery.startedBefore(endDate);
        }
        long total = processInstanceQuery.count();
        Map<String, String> cache = new HashMap<>();
        List<FlowableVO.Table> result = processInstanceQuery.listPage((query.getPageNum() - 1) * query.getPageSize(), query.getPageSize()).stream()
                .map(processInstance -> {
                    FlowableVO.Table table = new FlowableVO.Table();
                    table.setProcessId(processInstance.getId());
                    table.setCreatTime(DateUtil.toLocalDateTime(processInstance.getStartTime()));
                    table.setDefinitionKey(processInstance.getProcessDefinitionKey());
                    //先查缓存再查库
                    String initiator = Optional.ofNullable(cache.get(processInstance.getStartUserId())).orElseGet(() -> {
                        String name = sysUserService.getById(processInstance.getStartUserId()).getNickname();
                        cache.put(processInstance.getStartUserId(), name);
                        return name;
                    });
                    table.setInitiator(initiator);
                    if (processInstance.getEndTime() == null) {
                        Task task = taskService.createTaskQuery().active().processInstanceId(processInstance.getId()).singleResult();
                        table.setTaskId(task.getId());
                        table.setTaskName(task.getName());
                        table.setEndTime(null);
                        //先查缓存再查库
                        String assignee = Optional.ofNullable(cache.get(task.getAssignee())).orElseGet(() -> {
                            String name = sysUserService.getById(task.getAssignee()).getNickname();
                            cache.put(task.getAssignee(), name);
                            return name;
                        });
                        table.setAssignee(assignee);
                    } else {
                        table.setAssignee("无");
                        table.setTaskId("-1");
                        table.setTaskName("已办结");
                        table.setEndTime(DateUtil.toLocalDateTime(processInstance.getEndTime()));
                    }
                    return table;
                }).collect(Collectors.toList());

        Page<?> page = new Page<>(query.getPageNum(), query.getPageSize(), total);
        return PaginationResult.of(page, result);
    }


    /*------------------------task----------------------- **/
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

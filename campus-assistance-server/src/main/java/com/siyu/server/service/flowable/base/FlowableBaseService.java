package com.siyu.server.service.flowable.base;

import com.siyu.common.enums.ErrorStatus;
import com.siyu.common.exception.BusinessException;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.task.api.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public abstract class FlowableBaseService {

    @Autowired
    protected TaskService taskService;

    @Autowired
    protected RepositoryService repositoryService;

    @Autowired
    protected HistoryService historyService;

    @Autowired
    protected RuntimeService runtimeService;


    protected Task getTask(String taskId, String taskName, String userId) {
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

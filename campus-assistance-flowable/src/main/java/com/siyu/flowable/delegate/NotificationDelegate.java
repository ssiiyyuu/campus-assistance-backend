package com.siyu.flowable.delegate;

import com.siyu.common.domain.entity.Notification;
import com.siyu.common.enums.NotificationType;
import com.siyu.common.service.NotificationService;
import com.siyu.flowable.constants.FlowableConstants;
import com.siyu.rabbitMQ.service.MQService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.flowable.engine.repository.ProcessDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class NotificationDelegate implements JavaDelegate {

    @Autowired
    private MQService mqService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private RepositoryService repositoryService;

    @Override
    public void execute(DelegateExecution execution) {
        String processDefinitionId = execution.getProcessDefinitionId();
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionId(processDefinitionId)
                .singleResult();
        String assigneeId = (String) execution.getVariable(FlowableConstants.ASSIGNEE_VARIABLE_KEY);

        Notification notification = new Notification();
        notification.setContent("您有一条流程待办[" + processDefinition.getName() + "]");
        notificationService.setBaseInfo(notification, NotificationType.SYSTEM.name(), "0", assigneeId);

        mqService.sendMessageToNotificationExchange(notification);
    }
}

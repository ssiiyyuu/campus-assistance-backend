package com.siyu.server.delegate;

import com.siyu.flowable.constants.FlowableConstants;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component
public class MessageDelegate implements JavaDelegate {
    @Override
    public void execute(DelegateExecution execution) {
        System.out.println(execution.getVariable(FlowableConstants.ASSIGNEE_VARIABLE_KEY));
        System.out.println("系统任务执行中...");
    }
}

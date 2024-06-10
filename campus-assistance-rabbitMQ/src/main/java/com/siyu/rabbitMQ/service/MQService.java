package com.siyu.rabbitMQ.service;

import com.siyu.common.domain.entity.Notification;
import com.siyu.rabbitMQ.constants.MQConstants;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MQService {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendMessageToNotificationExchange(Notification notification) {
        rabbitTemplate.convertAndSend(MQConstants.NOTIFICATION_EXCHANGE, "", notification);
    }

}

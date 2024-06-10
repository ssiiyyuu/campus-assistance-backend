package com.siyu.server.listener;

import com.siyu.rabbitMQ.constants.MQConstants;
import com.siyu.common.domain.entity.Notification;
import com.siyu.common.service.NotificationService;
import com.siyu.websocket.utils.WebSocketUtils;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class NotificationListener {

    @Autowired
    private NotificationService notificationService;

    //保存notification表
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = MQConstants.NOTIFICATION_QUEUE_1),
            exchange = @Exchange(name = MQConstants.NOTIFICATION_EXCHANGE, type = ExchangeTypes.FANOUT),
            key = ""
    ))
    public void listenNotificationSave(Notification notification) {
        notificationService.save(notification);
    }

    //向Android端发送消息
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = MQConstants.NOTIFICATION_QUEUE_2),
            exchange = @Exchange(name = MQConstants.NOTIFICATION_EXCHANGE, type = ExchangeTypes.FANOUT),
            key = ""
    ))
    public void listenNotificationInfo(Notification notification) {
        if(notification.getTo().equals("0")) {
            WebSocketUtils.sendToAllUser(notification.getType());
        } else {
            WebSocketUtils.sendToUser(notification.getTo(), notification.getType());
        }
    }
}

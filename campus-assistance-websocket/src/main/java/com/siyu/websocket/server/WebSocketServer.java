package com.siyu.websocket.server;

import com.siyu.websocket.utils.WebSocketUtils;
import lombok.Getter;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

@Getter
@Component
@ServerEndpoint("/WebSocket/{userId}")
public class WebSocketServer {

    private Session session;

    @OnOpen
    public void onOpen(Session session, @PathParam(value = "userId") String userId) {
        this.session = session;
        WebSocketUtils.addWebSocketServer(userId, this);
    }

    @OnClose
    public void onClose() {
        WebSocketUtils.removeWebSocketServer(this);
    }

    @OnMessage
    public void onMessage(String message, Session session) {
    }

    @OnError
    public void onError(Session session, Throwable error) {

    }
}
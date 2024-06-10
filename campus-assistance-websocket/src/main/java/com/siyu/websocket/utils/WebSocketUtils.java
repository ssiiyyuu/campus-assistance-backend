package com.siyu.websocket.utils;

import com.siyu.websocket.server.WebSocketServer;

import javax.websocket.Session;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

public class WebSocketUtils {
    private final static CopyOnWriteArraySet<WebSocketServer> webSocketServerSet = new CopyOnWriteArraySet<>();

    private final static ConcurrentHashMap<String, WebSocketServer> webSocketServerMap = new ConcurrentHashMap<>();

    public static void addWebSocketServer(String userId, WebSocketServer webSocketServer){
        if (webSocketServer != null){
            webSocketServerSet.add(webSocketServer);
            webSocketServerMap.put(userId, webSocketServer);
        }
    }

    public static void removeWebSocketServer(WebSocketServer webSocketServer) {
        Collection<WebSocketServer> values = webSocketServerMap.values();
        values.removeIf(item -> item.equals(webSocketServer));
        webSocketServerSet.remove(webSocketServer);
    }

    public static void sendToSession(Session session, String msg) {
        session.getAsyncRemote().sendText(msg);
    }

    public static void sendToUser(String userId, String msg) {
        WebSocketServer webSocketServer = webSocketServerMap.get(userId);
        if(webSocketServer == null) {
            return;
        }
        webSocketServer.getSession().getAsyncRemote().sendText(msg);
    }

    public static void sendToAllUser(String msg) {
        for (WebSocketServer webSocketServer : webSocketServerSet) {
            sendToSession(webSocketServer.getSession(), msg);
        }
    }
}

package com.github.linyuzai.connection.loadbalance.websocket.javax;

import com.github.linyuzai.connection.loadbalance.core.concept.Connection;
import com.github.linyuzai.connection.loadbalance.websocket.concept.WebSocketLoadBalanceConcept;

import javax.websocket.*;
import java.nio.ByteBuffer;

/**
 * 基于 {@link JavaxWebSocketConnection} 转发消息客户端的端点配置。
 * <p>
 * Endpoint configuration based on {@link JavaxWebSocketConnection} forwarding message client.
 */
@ClientEndpoint
public class JavaxWebSocketSubscriberEndpoint {

    @OnOpen
    public void onOpen(Session session) {
    }

    @OnClose
    public void onClose(Session session, CloseReason reason) {
        WebSocketLoadBalanceConcept.getInstance().onClose(session.getId(), Connection.Type.SUBSCRIBER, reason);
    }

    @OnMessage
    public void onMessage(Session session, String message) {
        WebSocketLoadBalanceConcept.getInstance().onMessage(session.getId(), Connection.Type.SUBSCRIBER, message);
    }

    @OnMessage
    public void onMessage(Session session, PongMessage message) {
        WebSocketLoadBalanceConcept.getInstance().onMessage(session.getId(), Connection.Type.SUBSCRIBER, message);
    }

    @OnMessage
    public void onMessage(Session session, ByteBuffer message) {
        WebSocketLoadBalanceConcept.getInstance().onMessage(session.getId(), Connection.Type.SUBSCRIBER, message);
    }

    @OnError
    public void onError(Session session, Throwable e) {
        WebSocketLoadBalanceConcept.getInstance().onError(session.getId(), Connection.Type.SUBSCRIBER, e);
    }
}

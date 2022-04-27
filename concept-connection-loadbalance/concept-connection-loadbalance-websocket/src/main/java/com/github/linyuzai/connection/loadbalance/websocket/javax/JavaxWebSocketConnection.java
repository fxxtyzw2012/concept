package com.github.linyuzai.connection.loadbalance.websocket.javax;

import com.github.linyuzai.connection.loadbalance.core.message.PingMessage;
import com.github.linyuzai.connection.loadbalance.core.message.PongMessage;
import com.github.linyuzai.connection.loadbalance.websocket.concept.WebSocketConnection;
import lombok.Getter;
import lombok.SneakyThrows;

import javax.websocket.Session;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.Map;

@Getter
public class JavaxWebSocketConnection extends WebSocketConnection {

    private final Session session;

    public JavaxWebSocketConnection(Session session, String type) {
        super(type);
        this.session = session;
    }

    public JavaxWebSocketConnection(Session session,
                                    String type,
                                    Map<Object, Object> metadata) {
        super(type, metadata);
        this.session = session;
    }

    @Override
    public Object getId() {
        return session.getId();
    }

    @Override
    public URI getUri() {
        return session.getRequestURI();
    }

    @SneakyThrows
    @Override
    public void doSend(Object message) {
        if (message instanceof String) {
            session.getAsyncRemote().sendText((String) message);
        } else if (message instanceof ByteBuffer) {
            session.getAsyncRemote().sendBinary((ByteBuffer) message);
        } else if (message instanceof byte[]) {
            session.getAsyncRemote().sendBinary(ByteBuffer.wrap((byte[]) message));
        } else {
            session.getAsyncRemote().sendObject(message);
        }
    }

    @SneakyThrows
    @Override
    public void ping(PingMessage ping) {
        Object payload = ping.getPayload();
        if (payload instanceof ByteBuffer) {
            session.getAsyncRemote().sendPing((ByteBuffer) payload);
        } else if (payload instanceof byte[]) {
            session.getAsyncRemote().sendPing(ByteBuffer.wrap((byte[]) payload));
        } else {
            throw new IllegalArgumentException(payload.toString());
        }
    }

    @SneakyThrows
    @Override
    public void pong(PongMessage pong) {
        Object payload = pong.getPayload();
        if (payload instanceof ByteBuffer) {
            session.getAsyncRemote().sendPong((ByteBuffer) payload);
        } else if (payload instanceof byte[]) {
            session.getAsyncRemote().sendPong(ByteBuffer.wrap((byte[]) payload));
        } else {
            throw new IllegalArgumentException(payload.toString());
        }
    }

    @SneakyThrows
    @Override
    public void close() {
        session.close();
    }
}

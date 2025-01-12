package com.github.linyuzai.connection.loadbalance.core.message;

import java.nio.ByteBuffer;

/**
 * pong 消息。
 * <p>
 * Pong message.
 */
public interface PongMessage extends Message {

    @SuppressWarnings("unchecked")
    @Override
    ByteBuffer getPayload();
}

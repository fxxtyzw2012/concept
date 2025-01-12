package com.github.linyuzai.connection.loadbalance.autoconfigure.subscribe.rabbitmq;

import com.github.linyuzai.connection.loadbalance.core.concept.ConnectionLoadBalanceConcept;
import com.github.linyuzai.connection.loadbalance.core.message.AbstractMessageCodecAdapter;
import com.github.linyuzai.connection.loadbalance.core.message.Message;
import com.github.linyuzai.connection.loadbalance.core.message.MessageCodecAdapter;
import com.github.linyuzai.connection.loadbalance.core.message.decode.MessageDecoder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * RabbitMQ 消息编解码适配器。
 * <p>
 * {@link MessageCodecAdapter} for RabbitMQ.
 */
public class RabbitMessageCodecAdapter extends AbstractMessageCodecAdapter {

    @Override
    public MessageDecoder getForwardMessageDecoder(MessageDecoder decoder) {
        return new RabbitMessageDecoder(decoder);
    }

    @Getter
    @RequiredArgsConstructor
    public static class RabbitMessageDecoder implements MessageDecoder {

        private final MessageDecoder decoder;

        @Override
        public Message decode(Object message, ConnectionLoadBalanceConcept concept) {
            if (message instanceof org.springframework.amqp.core.Message) {
                return decoder.decode(((org.springframework.amqp.core.Message) message).getBody(), concept);
            }
            return decoder.decode(message, concept);
        }
    }
}

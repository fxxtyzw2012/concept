package com.github.linyuzai.connection.loadbalance.autoconfigure.websocket.reactive;

import com.github.linyuzai.connection.loadbalance.websocket.concept.DefaultEndpointConfigurer;
import com.github.linyuzai.connection.loadbalance.websocket.concept.WebSocketLoadBalanceConcept;
import com.github.linyuzai.connection.loadbalance.websocket.reactive.ReactiveWebSocketServerHandlerMapping;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;

import java.util.List;

@Configuration(proxyBeanMethods = false)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
public class ReactiveWebSocketDefaultEndpointConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public WebSocketHandlerAdapter webSocketHandlerAdapter() {
        return new WebSocketHandlerAdapter();
    }

    @Bean
    @ConditionalOnMissingBean
    public ReactiveWebSocketServerHandlerMapping reactiveWebSocketServerHandlerMapping(
            WebSocketLoadBalanceConcept concept,
            List<DefaultEndpointConfigurer> configurers) {
        return new ReactiveWebSocketServerHandlerMapping(concept, configurers);
    }
}

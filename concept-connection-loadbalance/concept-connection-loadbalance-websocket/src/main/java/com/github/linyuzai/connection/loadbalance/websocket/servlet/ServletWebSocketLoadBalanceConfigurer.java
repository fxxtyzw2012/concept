package com.github.linyuzai.connection.loadbalance.websocket.servlet;

import com.github.linyuzai.connection.loadbalance.websocket.concept.WebSocketLoadBalanceConcept;
import lombok.AllArgsConstructor;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * 基于 {@link ServletWebSocketConnection} 的服务间负载均衡的 {@link WebSocketConfigurer}。
 * <p>
 * {@link WebSocketConfigurer} for service load balancing based on {@link ServletWebSocketConnection}.
 */
@AllArgsConstructor
public class ServletWebSocketLoadBalanceConfigurer implements WebSocketConfigurer {

    private WebSocketLoadBalanceConcept concept;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new ServletWebSocketLoadBalanceHandler(concept),
                        WebSocketLoadBalanceConcept.SUBSCRIBER_ENDPOINT)
                .setAllowedOrigins("*");
    }
}

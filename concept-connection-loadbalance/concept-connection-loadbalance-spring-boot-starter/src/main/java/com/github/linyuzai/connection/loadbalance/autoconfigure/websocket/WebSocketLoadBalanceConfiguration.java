package com.github.linyuzai.connection.loadbalance.autoconfigure.websocket;

import com.github.linyuzai.connection.loadbalance.autoconfigure.ApplicationConnectionEventPublisher;
import com.github.linyuzai.connection.loadbalance.autoconfigure.LoggerEventPublishErrorHandler;
import com.github.linyuzai.connection.loadbalance.core.concept.Connection;
import com.github.linyuzai.connection.loadbalance.core.concept.ConnectionFactory;
import com.github.linyuzai.connection.loadbalance.core.event.ConnectionEventListener;
import com.github.linyuzai.connection.loadbalance.core.event.ConnectionEventPublisher;
import com.github.linyuzai.connection.loadbalance.core.event.EventPublishErrorHandler;
import com.github.linyuzai.connection.loadbalance.core.heartbeat.ConnectionHeartbeatAutoSender;
import com.github.linyuzai.connection.loadbalance.core.message.MessageCodecAdapter;
import com.github.linyuzai.connection.loadbalance.core.message.MessageFactory;
import com.github.linyuzai.connection.loadbalance.core.select.ConnectionSelector;
import com.github.linyuzai.connection.loadbalance.core.server.ConnectionServerProvider;
import com.github.linyuzai.connection.loadbalance.core.subscribe.ConnectionSubscriber;
import com.github.linyuzai.connection.loadbalance.core.subscribe.monitor.ConnectionSubscribeMonitor;
import com.github.linyuzai.connection.loadbalance.core.subscribe.monitor.ScheduledExecutorConnectionSubscribeMonitor;
import com.github.linyuzai.connection.loadbalance.core.utils.ScheduledExecutorServiceFactory;
import com.github.linyuzai.connection.loadbalance.core.utils.SingleThreadScheduledExecutorServiceFactory;
import com.github.linyuzai.connection.loadbalance.websocket.concept.WebSocketLoadBalanceConcept;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration(proxyBeanMethods = false)
public class WebSocketLoadBalanceConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public EventPublishErrorHandler eventPublishErrorHandler() {
        return new LoggerEventPublishErrorHandler();
    }

    @Bean
    @ConditionalOnMissingBean
    public ConnectionEventPublisher connectionEventPublisher(ApplicationEventPublisher publisher,
                                                             EventPublishErrorHandler errorHandler) {
        return new ApplicationConnectionEventPublisher(publisher, errorHandler);
    }

    @Bean
    @ConditionalOnMissingBean
    public ScheduledExecutorServiceFactory scheduledExecutorServiceFactory() {
        return new SingleThreadScheduledExecutorServiceFactory();
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "concept.websocket.load-balance.subscriber.monitor",
            name = "enabled", havingValue = "true", matchIfMissing = true)
    public ConnectionSubscribeMonitor connectionSubscribeMonitor(
            ScheduledExecutorServiceFactory factory,
            WebSocketLoadBalanceProperties properties) {
        return new ScheduledExecutorConnectionSubscribeMonitor(
                factory.create(ConnectionSubscribeMonitor.class),
                properties.getSubscriber().getMonitor().getPeriod());
    }

    @Bean
    @ConditionalOnProperty(prefix = "concept.websocket.load-balance.subscriber.heartbeat",
            name = "enabled", havingValue = "true", matchIfMissing = true)
    public ConnectionHeartbeatAutoSender loadBalanceConnectionHeartbeatAutoSender(
            ScheduledExecutorServiceFactory factory,
            WebSocketLoadBalanceProperties properties) {
        WebSocketLoadBalanceProperties.HeartbeatProperties heartbeat = properties.getSubscriber().getHeartbeat();
        return new ConnectionHeartbeatAutoSender(Connection.Type.OBSERVABLE,
                heartbeat.getTimeout(), heartbeat.getPeriod(),
                factory.create(ConnectionHeartbeatAutoSender.class));
    }

    @Bean
    @ConditionalOnProperty(prefix = "concept.websocket.load-balance.server.heartbeat",
            name = "enabled", havingValue = "true", matchIfMissing = true)
    public ConnectionHeartbeatAutoSender clientConnectionHeartbeatSender(
            ScheduledExecutorServiceFactory factory,
            WebSocketLoadBalanceProperties properties) {
        WebSocketLoadBalanceProperties.HeartbeatProperties heartbeat = properties.getServer().getHeartbeat();
        return new ConnectionHeartbeatAutoSender(Connection.Type.CLIENT,
                heartbeat.getTimeout(), heartbeat.getPeriod(),
                factory.create(ConnectionHeartbeatAutoSender.class));
    }

    @Bean(destroyMethod = "destroy")
    @ConditionalOnMissingBean
    public WebSocketLoadBalanceConcept webSocketLoadBalanceConcept(
            ConnectionServerProvider provider,
            ConnectionSubscriber subscriber,
            List<ConnectionFactory> connectionFactories,
            List<ConnectionSelector> connectionSelectors,
            MessageCodecAdapter messageCodecAdapter,
            List<MessageFactory> messageFactories,
            ConnectionEventPublisher eventPublisher,
            List<ConnectionEventListener> eventListeners) {
        return new WebSocketLoadBalanceConcept.Builder()
                .connectionServerProvider(provider)
                .connectionSubscriber(subscriber)
                .addConnectionFactories(connectionFactories)
                .addConnectionSelectors(connectionSelectors)
                .messageCodecAdapter(messageCodecAdapter)
                .addMessageFactories(messageFactories)
                .eventPublisher(eventPublisher)
                .addEventListeners(eventListeners)
                .build();
    }
}

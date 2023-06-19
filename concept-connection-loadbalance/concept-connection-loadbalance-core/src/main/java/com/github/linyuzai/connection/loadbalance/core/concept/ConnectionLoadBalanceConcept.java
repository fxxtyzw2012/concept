package com.github.linyuzai.connection.loadbalance.core.concept;

import com.github.linyuzai.connection.loadbalance.core.message.Message;

import java.util.Collection;
import java.util.Map;

/**
 * 连接负载均衡概念
 */
public interface ConnectionLoadBalanceConcept {

    /**
     * 初始化
     */
    void initialize();

    /**
     * 销毁
     */
    void destroy();

    /**
     * 创建连接
     *
     * @param o        底层连接
     * @param metadata 元数据
     * @return 连接
     */
    Connection createConnection(Object o, Map<Object, Object> metadata);

    /**
     * 当连接建立时调用
     *
     * @param o        底层连接
     * @param metadata 元数据
     * @return 连接
     */
    Connection onEstablish(Object o, Map<Object, Object> metadata);

    /**
     * 当连接建立时调用
     *
     * @param connection 连接
     */
    void onEstablish(Connection connection);

    /**
     * 当连接关闭时调用
     *
     * @param id     连接 id
     * @param type   连接类型
     * @param reason 关闭原因
     */
    void onClose(Object id, String type, Object reason);

    /**
     * 当连接关闭时调用
     *
     * @param connection 连接
     * @param reason     关闭原因
     */
    void onClose(Connection connection, Object reason);

    /**
     * 当连接接收消息时调用
     *
     * @param id      连接 id
     * @param type    连接类型
     * @param message 消息数据
     */
    void onMessage(Object id, String type, Object message);

    /**
     * 当连接接收消息时调用
     *
     * @param connection 连接
     * @param message    消息数据
     */
    void onMessage(Connection connection, Object message);

    /**
     * 当连接异常时调用
     *
     * @param id   连接 id
     * @param type 连接类型
     * @param e    异常
     */
    void onError(Object id, String type, Throwable e);

    /**
     * 当连接异常时调用
     *
     * @param connection 连接
     * @param e          异常
     */
    void onError(Connection connection, Throwable e);

    /**
     * 创建消息
     *
     * @param o 消息数据
     * @return {@link Message} 实例
     */
    Message createMessage(Object o);

    /**
     * 发送消息
     *
     * @param msg 消息
     */
    void send(Object msg);

    /**
     * 发送消息
     *
     * @param msg     消息
     * @param headers 消息头
     */
    void send(Object msg, Map<String, String> headers);

    /**
     * 发布事件
     *
     * @param event 事件
     */
    void publish(Object event);

    /**
     * 获得连接
     *
     * @param id   连接 id
     * @param type 连接类型
     * @return 连接
     */
    Connection getConnection(Object id, String type);

    /**
     * 获得连接集合
     *
     * @param type 连接类型
     * @return 连接集合
     */
    Collection<Connection> getConnections(String type);
}

package com.github.linyuzai.connection.loadbalance.core.concept;

import java.util.Map;

public interface ConnectionLoadBalanceConcept {

    void initialize();

    void destroy();

    Connection create(Object o, Map<String, String> metadata);

    void add(Object o, Map<String, String> metadata);

    void add(Connection connection);

    void remove(Object id);

    void message(Object id, byte[] message);

    void error(Object id, Throwable e);

    void send(Object msg);

    void send(Object msg, Map<String, String> headers);

    void publish(Object event);

    Connection getConnection(Object id);

    Map<Object, Connection> getConnections();
}

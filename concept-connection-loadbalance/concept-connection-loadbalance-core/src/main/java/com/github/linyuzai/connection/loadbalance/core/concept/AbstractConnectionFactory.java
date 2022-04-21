package com.github.linyuzai.connection.loadbalance.core.concept;

import com.github.linyuzai.connection.loadbalance.core.message.decode.MessageDecoder;
import com.github.linyuzai.connection.loadbalance.core.message.encode.MessageEncoder;
import com.github.linyuzai.connection.loadbalance.core.proxy.ProxyMarker;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

import java.util.Map;

@Getter
@AllArgsConstructor
public abstract class AbstractConnectionFactory implements ConnectionFactory {

    protected MessageEncoder messageEncoder;

    protected MessageDecoder messageDecoder;

    @Override
    public boolean support(Object o, Map<String, String> metadata) {
        return support(metadata) && support(o);
    }

    public boolean support(Map<String, String> metadata) {
        return !hasProxyFlag(metadata);
    }

    public boolean hasProxyFlag(Map<String, String> metadata) {
        return metadata.containsKey(ProxyMarker.FLAG);
    }

    public abstract boolean support(Object o);
}
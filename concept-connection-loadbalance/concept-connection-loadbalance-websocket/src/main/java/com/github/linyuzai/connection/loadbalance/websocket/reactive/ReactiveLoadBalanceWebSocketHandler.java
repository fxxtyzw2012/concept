package com.github.linyuzai.connection.loadbalance.websocket.reactive;

import com.github.linyuzai.connection.loadbalance.core.concept.Connection;
import com.github.linyuzai.connection.loadbalance.websocket.concept.WebSocketLoadBalanceConcept;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@AllArgsConstructor
public class ReactiveLoadBalanceWebSocketHandler implements WebSocketHandler {

    private WebSocketLoadBalanceConcept concept;

    @NonNull
    @Override
    public Mono<Void> handle(WebSocketSession session) {
        //session.getHandshakeInfo().getUri();
        Mono<Void> send = session.send(Flux.create(sink -> {
            concept.open(session, null, Connection.Type.CLIENT);
        }));

        Mono<Void> receive = session.receive().map(it -> it.getPayload().asByteBuffer().array())
                .doOnNext(it -> concept.message(session.getId(), it, Connection.Type.CLIENT))
                .doOnError(it -> concept.error(session.getId(), it, Connection.Type.CLIENT))
                .then();

        return Mono.zip(send, receive).then();
    }
}

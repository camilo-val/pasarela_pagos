package com.pasarela.infrastructure.commons;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class WebSocketConnectionManager {

    private final Map<UUID, WebSocketSession> sessions = new ConcurrentHashMap<>();

    public void register(UUID sessionId, WebSocketSession session) {
        WebSocketSession previous = sessions.put(sessionId, session);
        log.info("WS session registered. userId={} session={} replaced={}",
                sessionId, session.getId(), previous == null ? "none" : previous.getId());
    }

    /**
     * Removes the entry only if it still points to the given session. A late close of an old
     * connection must not remove the newer connection registered by a reconnect.
     */
    public void remove(UUID sessionId, WebSocketSession session) {
        boolean removed = sessions.remove(sessionId, session);
        log.info("WS session closed. userId={} session={} removedFromRegistry={}",
                sessionId, session.getId(), removed);
    }

    public Mono<Void> send(
            UUID sessionId,
            String message
    ) {
        return Mono.defer(() -> {
            WebSocketSession session = sessions.get(sessionId);

            if (session == null || !session.isOpen()) {
                return Mono.error(new IllegalStateException(
                        "No open WebSocket session for userId " + sessionId));
            }

            return session.send(Mono.just(session.textMessage(message)))
                    .doOnSuccess(unused -> log.info(
                            "WS frame written. userId={} session={}", sessionId, session.getId()));
        });
    }
}

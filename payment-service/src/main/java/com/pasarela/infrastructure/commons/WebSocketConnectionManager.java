package com.pasarela.infrastructure.commons;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebSocketConnectionManager {

    private final Map<UUID, WebSocketSession> sessions = new ConcurrentHashMap<>();

    public void register(UUID sessionId, WebSocketSession session) {
        sessions.put(sessionId, session);
    }

    public void remove(UUID sessionId) {
        sessions.remove(sessionId);
    }

    public Mono<Void> send(
            UUID sessionId,
            String message
    ) {
        WebSocketSession session = sessions.get(sessionId);

        if (session == null || !session.isOpen()) {
            return Mono.empty();
        }

        return session.send(
                Mono.just(session.textMessage(message))
        );
    }}

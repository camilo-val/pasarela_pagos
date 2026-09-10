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

    public void register(UUID userId, WebSocketSession session) {
        sessions.put(userId, session);
    }

    public void remove(UUID userId) {
        sessions.remove(userId);
    }

    public Mono<Void> send(
            UUID userId,
            String message
    ) {
        WebSocketSession session = sessions.get(userId);

        if (session == null || !session.isOpen()) {
            return Mono.empty();
        }

        return session.send(
                Mono.just(session.textMessage(message))
        );
    }}

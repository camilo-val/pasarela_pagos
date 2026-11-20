package com.pasarela.infrastructure.commons;

import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class WebSocketConnectionManagerTest {

    private final WebSocketConnectionManager manager = new WebSocketConnectionManager();
    private final UUID key = UUID.randomUUID();

    /** Reproduces the production bug: the client reconnects before the server notices the old connection closed. */
    @Test
    void lateCloseOfOldSessionDoesNotRemoveReconnectedSession() {
        WebSocketSession oldSession = openSession("A");
        WebSocketSession newSession = openSession("B");

        manager.register(key, oldSession);
        manager.register(key, newSession);
        manager.remove(key, oldSession);

        StepVerifier.create(manager.send(key, "{}")).verifyComplete();
        verify(newSession).send(any());
        verify(oldSession, never()).send(any());
    }

    @Test
    void missingSessionIsAnErrorNotASilentSuccess() {
        StepVerifier.create(manager.send(key, "{}"))
                .expectError(IllegalStateException.class)
                .verify();
    }

    @Test
    void closedSessionIsAnError() {
        WebSocketSession session = openSession("A");
        when(session.isOpen()).thenReturn(false);
        manager.register(key, session);

        StepVerifier.create(manager.send(key, "{}"))
                .expectError(IllegalStateException.class)
                .verify();
    }

    private static WebSocketSession openSession(String id) {
        WebSocketSession session = mock(WebSocketSession.class);
        when(session.getId()).thenReturn(id);
        when(session.isOpen()).thenReturn(true);
        when(session.textMessage(any())).thenReturn(mock(WebSocketMessage.class));
        when(session.send(any())).thenReturn(Mono.empty());
        return session;
    }
}

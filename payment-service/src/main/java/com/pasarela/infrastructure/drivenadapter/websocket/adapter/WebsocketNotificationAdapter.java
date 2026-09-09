package com.pasarela.infrastructure.drivenadapter.websocket.adapter;

import com.pasarela.application.command.ProcessPaymentCommand;
import com.pasarela.application.port.PaymentNotificationWsPort;
import com.pasarela.infrastructure.commons.WebSocketConnectionManager;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

@AllArgsConstructor
@Component
public class WebsocketNotificationAdapter implements PaymentNotificationWsPort {

    private final WebSocketConnectionManager connectionManager;
    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> notify(UUID orderId, ProcessPaymentCommand event) {
        try {

            String message =
                    objectMapper.writeValueAsString(event);

            return connectionManager.send(
                    orderId,
                    message
            );

        } catch (Exception e) {
            return Mono.error(e);
        }
    }
}

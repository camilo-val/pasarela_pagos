package com.pasarela.infrastructure.entrypoint.websocket;

import com.pasarela.application.command.ProcessPaymentCommand;
import com.pasarela.application.usecase.PaymentUseCase;
import com.pasarela.infrastructure.commons.WebSocketConnectionManager;
import com.pasarela.infrastructure.entrypoint.websocket.dto.PaymentRqDto;
import com.pasarela.infrastructure.entrypoint.websocket.mapper.MapperWebsocketEntry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentWebSocketHandler implements WebSocketHandler {
    private final PaymentUseCase paymentUseCase;
    private final ObjectMapper objectMapper;
    private final WebSocketConnectionManager connectionManager;
    private final MapperWebsocketEntry mapper;

    @Override
    public List<String> getSubProtocols() {
        return WebSocketHandler.super.getSubProtocols();
    }

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        UUID userId = extractUserId(session);
        connectionManager.register(userId, session);

        return session
                .receive()
                .map(WebSocketMessage::getPayloadAsText)
                .flatMap(body -> this.deserialize(body).map(mapper::toCommand))
                .flatMap(command -> {
                    System.out.println("PaymentWebSocketHandler.handle: " + command);
                    return paymentUseCase.processPayment(userId,command)
                            .onErrorResume(e -> {
                                log.error("Error processing payment: {}", e.getMessage(), e);
                                return Mono.empty();
                            });
                })
                .doOnNext(message -> log.info("message: {}", message))
                .doFinally(signalType -> connectionManager.remove(userId))
                .then();
    }

    private Mono<PaymentRqDto> deserialize(String message) {
        try{
            return Mono.just(objectMapper.readValue(message, PaymentRqDto.class));
        }catch (Exception e){
           e.printStackTrace();
            return Mono.error(e);
        }
    }

    private UUID extractUserId(WebSocketSession session) {
        String userId = UriComponentsBuilder
                .fromUri(session.getHandshakeInfo().getUri())
                .build()
                .getQueryParams()
                .getFirst("userId");
        return UUID.fromString(userId);
    }
}

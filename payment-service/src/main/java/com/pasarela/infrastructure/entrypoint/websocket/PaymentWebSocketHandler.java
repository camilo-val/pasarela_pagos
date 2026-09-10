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
import reactor.core.publisher.Mono;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

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
        return session
                .receive()
                .map(WebSocketMessage::getPayloadAsText)
                .flatMap(body -> {
                    return this.deserialize(body).map(paymentRqDto -> {
                        connectionManager.register(paymentRqDto.userId(),session);
                        return mapper.toCommand(paymentRqDto);
                    });
                })
                .flatMap(paymentUseCase::processPayment)
                .doOnNext(message -> log.info("message: {}", message))
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
}

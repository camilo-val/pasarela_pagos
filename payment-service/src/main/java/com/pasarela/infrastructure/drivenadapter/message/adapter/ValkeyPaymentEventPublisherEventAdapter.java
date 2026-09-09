package com.pasarela.infrastructure.drivenadapter.message.adapter;

import com.pasarela.application.command.ProcessPaymentCommand;
import com.pasarela.application.port.PaymentEventPublisherPort;
import com.pasarela.infrastructure.commons.Constants;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import tools.jackson.databind.ObjectMapper;

@AllArgsConstructor
@Component
public class ValkeyPaymentEventPublisherEventAdapter implements PaymentEventPublisherPort {
    private final ReactiveRedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> publish(ProcessPaymentCommand event) {
        return Mono.fromCallable(
                () -> objectMapper.writeValueAsString(event)
                )
                .flatMap(json -> redisTemplate.opsForList()
                        .rightPush(Constants.QUEUE, json)
                )
                .then();
    }
}

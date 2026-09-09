package com.pasarela.infrastructure.drivenadapter.messaging;

import com.pasarela.application.PasarelaApplication;
import com.pasarela.application.command.ProcessPaymentCommand;
import com.pasarela.application.port.PaymentEventPublisherPort;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.UUID;

@SpringBootTest(classes = PasarelaApplication.class)
class ValkeyPaymentEventPublisherAdapterIT {

    @Autowired
    private PaymentEventPublisherPort eventPublisher;

    @Autowired
    private ReactiveRedisTemplate<String, String> redisTemplate;

    @Test
    void shouldPublishAuthorizationRequest() {

        ProcessPaymentCommand event =
                new ProcessPaymentCommand(
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        new BigDecimal("50000"),
                        "COP",
                        "Prueba de autorización"
                );

        StepVerifier
                .create(eventPublisher.publish(event))
                .verifyComplete();
    }
}
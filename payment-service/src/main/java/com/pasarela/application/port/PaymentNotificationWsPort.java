package com.pasarela.application.port;

import com.pasarela.application.command.ProcessPaymentCommand;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface PaymentNotificationWsPort {
    Mono<Void> notify(UUID orderId, ProcessPaymentCommand event);
}

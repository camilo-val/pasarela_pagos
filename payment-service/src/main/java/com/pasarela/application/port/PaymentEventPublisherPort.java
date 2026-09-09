package com.pasarela.application.port;

import com.pasarela.application.command.ProcessPaymentCommand;
import reactor.core.publisher.Mono;

public interface PaymentEventPublisherPort {
    Mono<Void> publish(ProcessPaymentCommand event);
}

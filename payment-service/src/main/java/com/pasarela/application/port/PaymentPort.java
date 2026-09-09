package com.pasarela.application.port;

import com.pasarela.domain.model.Payment;
import reactor.core.publisher.Mono;

import java.util.UUID;


public interface PaymentPort {
    Mono<Payment> processPayment(Payment payment);
    Mono<Payment> updatePayment(Payment payment);
    Mono<Payment> getPaymentById(UUID id);
    Mono<Payment> getPaymentByOrderId(UUID orderId);
    Mono<Boolean> existsByOrderId(UUID orderId);

}

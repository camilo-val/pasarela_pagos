package com.pasarela.application.usecase;

import com.pasarela.application.command.ProcessPaymentCommand;
import com.pasarela.application.mapper.PaymentMapper;
import com.pasarela.application.port.PaymentNotificationWsPort;
import com.pasarela.application.port.PaymentPort;
import com.pasarela.domain.enums.PaymentStatus;
import com.pasarela.domain.model.Payment;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UpdatePaymentUCTest {

    private final PaymentPort paymentPort = mock(PaymentPort.class);
    private final PaymentNotificationWsPort notifier = mock(PaymentNotificationWsPort.class);
    private final PaymentMapper mapper = mock(PaymentMapper.class);
    private final UUID orderId = UUID.randomUUID();

    @Test
    void retriesNotificationUntilClientReconnects() {
        givenPendingPayment();
        AtomicInteger attempts = new AtomicInteger();
        when(notifier.notify(any(), any())).thenAnswer(invocation ->
                attempts.incrementAndGet() < 3
                        ? Mono.error(new IllegalStateException("No open WebSocket session"))
                        : Mono.empty());

        StepVerifier.create(useCase(5).updatePayment(orderId.toString(), PaymentStatus.APPROVED, "ok"))
                .expectNextMatches(payment -> payment.getStatus() == PaymentStatus.APPROVED)
                .verifyComplete();
        org.assertj.core.api.Assertions.assertThat(attempts.get()).isEqualTo(3);
    }

    @Test
    void reportsFailureWhenClientNeverReconnects() {
        givenPendingPayment();
        when(notifier.notify(any(), any()))
                .thenReturn(Mono.error(new IllegalStateException("No open WebSocket session")));

        StepVerifier.create(useCase(2).updatePayment(orderId.toString(), PaymentStatus.APPROVED, "ok"))
                .expectError()
                .verify();
    }

    private UpdatePaymentUC useCase(int retries) {
        return new UpdatePaymentUC(paymentPort, notifier, mapper, retries, 1);
    }

    private void givenPendingPayment() {
        Payment pending = Payment.create(UUID.randomUUID(), BigDecimal.valueOf(50000), "COP", "test", orderId);
        when(paymentPort.getPaymentByOrderId(orderId)).thenReturn(Mono.just(pending));
        when(paymentPort.updatePayment(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        when(mapper.toCommand(any())).thenReturn(ProcessPaymentCommand.builder().orderId(orderId).build());
    }
}

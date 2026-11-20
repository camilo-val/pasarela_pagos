package com.pasarela.application.usecase;

import com.pasarela.application.mapper.PaymentMapper;
import com.pasarela.application.port.PaymentNotificationWsPort;
import com.pasarela.application.port.PaymentPort;
import com.pasarela.domain.enums.PaymentStatus;
import com.pasarela.domain.exceptions.BusinessExceptions;
import com.pasarela.domain.exceptions.BusinessTransactionalExceptions;
import com.pasarela.domain.model.Payment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.UUID;

@Component
@Slf4j
public class UpdatePaymentUC {
    private final PaymentPort paymentPort;
    private final PaymentNotificationWsPort paymentNotificationWsPort;
    private final PaymentMapper mapper;
    private final int notifyAttempts;
    private final Duration notifyBackoff;

    public UpdatePaymentUC(PaymentPort paymentPort,
                           PaymentNotificationWsPort paymentNotificationWsPort,
                           PaymentMapper mapper,
                           @Value("${payment.notify.retries:10}") int notifyAttempts,
                           @Value("${payment.notify.backoff-ms:2000}") long notifyBackoffMs) {
        this.paymentPort = paymentPort;
        this.paymentNotificationWsPort = paymentNotificationWsPort;
        this.mapper = mapper;
        this.notifyAttempts = notifyAttempts;
        this.notifyBackoff = Duration.ofMillis(notifyBackoffMs);
    }

    public Mono<Payment> updatePayment(String orderId, PaymentStatus status , String description){
        return paymentPort.getPaymentByOrderId(UUID.fromString(orderId))
                .switchIfEmpty(Mono.error(() -> new BusinessExceptions(BusinessTransactionalExceptions.TRANSACTION_NOT_EXISTS)))
                .map(paymentBd -> paymentBd.updateStatus(status, description))
                .flatMap(paymentPort::updatePayment)
                .flatMap(event -> notifyWithRetry(event).thenReturn(event));
    }

    /**
     * The client may be reconnecting when the result is ready (restart, redeploy, dropped proxy
     * connection). The session is looked up again on every attempt, so a reconnect within the
     * retry window still receives the result.
     */
    private Mono<Void> notifyWithRetry(Payment event) {
        return Mono.defer(() -> paymentNotificationWsPort.notify(event.getUserId(), mapper.toCommand(event)))
                .retryWhen(Retry.fixedDelay(notifyAttempts, notifyBackoff)
                        .doBeforeRetry(signal -> log.warn(
                                "WS notify retry {} for orderId={}: {}",
                                signal.totalRetries() + 1, event.getOrderId(), signal.failure().getMessage())))
                .doOnSuccess(unused -> log.info(
                        "WS RESPONSE SENT. orderId={} status={}", event.getOrderId(), event.getStatus()))
                .doOnError(error -> log.error(
                        "WS RESPONSE NOT DELIVERED. orderId={} status={} (payment is updated in Mongo)",
                        event.getOrderId(), event.getStatus(), error));
    }
}

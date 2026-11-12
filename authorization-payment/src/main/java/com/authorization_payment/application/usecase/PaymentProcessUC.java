package com.authorization_payment.application.usecase;

import com.authorization_payment.application.command.ProcessPaymentCommand;
import com.authorization_payment.application.mapper.PaymentCommandMapper;
import com.authorization_payment.application.port.PublishTransactionPort;
import com.authorization_payment.domain.enums.PaymentStatus;
import com.authorization_payment.domain.model.PaymentAuthorization;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.concurrent.ThreadLocalRandom;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentProcessUC {
    private final PublishTransactionPort port;
    private final PaymentCommandMapper mapper;
    @Value("${sleep.start}")
    private int sleepStart;

    @Value("${sleep.end}")
    private int sleepEnd;

    public Mono<PaymentAuthorization> updatePaymentStatus(ProcessPaymentCommand command){
        PaymentAuthorization paymentAuthorization = PaymentAuthorization.authorization(command.amount(),command.currency(),command.description(),
                command.orderId(), PaymentStatus.random());

        log.info("Updating payment status for orderId: {} with status: {}", command.orderId(), paymentAuthorization.getStatus());
        log.info("Sleeping for a random duration between {} and {} milliseconds", sleepStart, sleepEnd);
        log.info("start {}", OffsetDateTime.now());

        return Mono.delay(
                Duration.ofMillis(
                        ThreadLocalRandom.current()
                                .nextInt(
                                        sleepStart,
                                        sleepEnd
                                )
                )
        ).doOnSuccess(ignored -> log.info("Finish {}", OffsetDateTime.now()))
                .flatMap(ignored ->
                port.UpdateStatusTransaction(mapper.toCommand(paymentAuthorization)).doOnNext(updatedPayment -> log.info("Updated payment status: {}", updatedPayment))
        );
    }
}

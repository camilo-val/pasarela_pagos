package com.pasarela.application.usecase;

import com.pasarela.application.command.ProcessPaymentCommand;
import com.pasarela.application.mapper.PaymentMapper;
import com.pasarela.application.port.PaymentPort;
import com.pasarela.application.port.PaymentEventPublisherPort;
import com.pasarela.domain.exceptions.BusinessExceptions;
import com.pasarela.domain.exceptions.BusinessTransactionalExceptions;
import com.pasarela.domain.model.Payment;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.UUID;

@AllArgsConstructor
@Component
public class PaymentUseCase {
    private final PaymentPort paymentPort;
    private final PaymentEventPublisherPort eventPublisher;
    private final PaymentMapper mapper;

    public Mono<Payment> processPayment(ProcessPaymentCommand payment) {
        System.out.println("processPayment: " + payment);
        return paymentPort.existsByOrderId(payment.orderId())
                .flatMap(exist -> {
                    System.out.println("exist: " + exist);
                    if (exist) {
                        return Mono.error(new BusinessExceptions(BusinessTransactionalExceptions.TRANSACTION_ALREADY_EXISTS));
                    }
                    return paymentPort.processPayment(Payment.create(UUID.randomUUID(),payment.amount(),
                            payment.currency(), payment.description(),payment.orderId()))

                            .flatMap(savedPayment -> {
                                ProcessPaymentCommand event = mapper.toCommand(savedPayment);
                                return eventPublisher.publish(event).thenReturn(savedPayment);
                            });
               });
    }
}

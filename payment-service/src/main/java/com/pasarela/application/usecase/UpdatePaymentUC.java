package com.pasarela.application.usecase;

import com.pasarela.application.mapper.PaymentMapper;
import com.pasarela.application.port.PaymentEventPublisherPort;
import com.pasarela.application.port.PaymentNotificationWsPort;
import com.pasarela.application.port.PaymentPort;
import com.pasarela.domain.enums.PaymentStatus;
import com.pasarela.domain.exceptions.BusinessExceptions;
import com.pasarela.domain.exceptions.BusinessTransactionalExceptions;
import com.pasarela.domain.model.Payment;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
@AllArgsConstructor
public class UpdatePaymentUC {
    private final PaymentPort paymentPort;
    private final PaymentEventPublisherPort eventPort;
    private final PaymentNotificationWsPort paymentNotificationWsPort;
    private final PaymentMapper mapper;

    public Mono<Payment> updatePayment(String orderId, PaymentStatus status , String description){

        return paymentPort.getPaymentByOrderId(UUID.fromString(orderId))
                .map(paymentBd -> paymentBd.updateStatus(status, description))
                .flatMap(paymentPort::updatePayment)
                .flatMap(updatePayment -> eventPort.publish(mapper.toCommand(updatePayment)).thenReturn(updatePayment))
                .flatMap(event ->  paymentNotificationWsPort.notify(event.getOrderId(),mapper.toCommand(event)).thenReturn(event))
                .switchIfEmpty(Mono.error(new BusinessExceptions(BusinessTransactionalExceptions.TRANSACTION_NOT_EXISTS)));
    }
}

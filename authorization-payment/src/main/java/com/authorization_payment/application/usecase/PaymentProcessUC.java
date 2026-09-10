package com.authorization_payment.application.usecase;

import com.authorization_payment.application.command.ProcessPaymentCommand;
import com.authorization_payment.application.mapper.PaymentCommandMapper;
import com.authorization_payment.application.port.PublishTransactionPort;
import com.authorization_payment.domain.enums.PaymentStatus;
import com.authorization_payment.domain.model.PaymentAuthorization;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@AllArgsConstructor
public class PaymentProcessUC {
    private final PublishTransactionPort port;
    private final PaymentCommandMapper mapper;

    public Mono<PaymentAuthorization> updatePaymentStatus(ProcessPaymentCommand command){
        PaymentAuthorization paymentAuthorization = PaymentAuthorization.authorization(command.amount(),command.currency(),command.description(),
                command.orderId(), PaymentStatus.random());
        return port.UpdateStatusTransaction(mapper.toCommand(paymentAuthorization));
    }
}

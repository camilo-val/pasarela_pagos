package com.authorization_payment.application.port;

import com.authorization_payment.application.command.ProcessPaymentCommand;
import com.authorization_payment.domain.model.PaymentAuthorization;
import reactor.core.publisher.Mono;

public interface PublishTransactionPort {
    Mono<PaymentAuthorization> UpdateStatusTransaction(ProcessPaymentCommand command);
}

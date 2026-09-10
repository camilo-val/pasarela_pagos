package com.authorization_payment.infrastructure.drivenadapter.rest.adapter;

import com.authorization_payment.application.command.ProcessPaymentCommand;
import com.authorization_payment.application.port.PublishTransactionPort;
import com.authorization_payment.domain.model.PaymentAuthorization;
import com.authorization_payment.infrastructure.drivenadapter.rest.mapper.PaymentAdapterMapper;
import com.authorization_payment.infrastructure.drivenadapter.rest.model.PaymentModel;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@AllArgsConstructor
public class PaymentAdapter implements PublishTransactionPort {
    private final WebClient.Builder builder;
    private final PaymentAdapterMapper mapper;
    @Override
    public Mono<PaymentAuthorization> UpdateStatusTransaction(ProcessPaymentCommand command) {
        return builder.build()
                .put()
                .uri("/webhook/{orderId}", command.orderId())
                .bodyValue(command)
                .retrieve()
                .bodyToMono(PaymentModel.class)
                .map(mapper::toDomain);
    }
}

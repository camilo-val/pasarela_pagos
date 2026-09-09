package com.authorization_payment.application.command;

import com.authorization_payment.domain.enums.PaymentStatus;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record ProcessPaymentCommand (
        BigDecimal amount,
        String currency,
        String description,
        UUID orderId
){
}

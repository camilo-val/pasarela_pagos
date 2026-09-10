package com.pasarela.application.command;

import com.pasarela.domain.enums.PaymentStatus;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Builder
public record ProcessPaymentCommand (
        String id,
        UUID userId,
        BigDecimal amount,
        String currency,
        PaymentStatus status,
        String description,
        UUID orderId,
        Instant createdAt,
        Instant updatedAt
){
}
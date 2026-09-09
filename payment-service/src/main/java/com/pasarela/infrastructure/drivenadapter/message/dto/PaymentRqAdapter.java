package com.pasarela.infrastructure.drivenadapter.message.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record PaymentRqAdapter(
        BigDecimal amount,
        String currency,
        String description,
        UUID orderId)
{
}

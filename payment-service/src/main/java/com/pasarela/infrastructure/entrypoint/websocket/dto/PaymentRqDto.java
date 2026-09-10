package com.pasarela.infrastructure.entrypoint.websocket.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record PaymentRqDto(
        UUID userId,
        BigDecimal amount,
        String currency,
        String description,
        UUID orderId)
{
}

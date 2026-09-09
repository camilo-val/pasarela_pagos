package com.pasarela.application.command;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record PaymentCommand(
        BigDecimal amount,
        String currency,
        String description,
        UUID orderId)
{
}

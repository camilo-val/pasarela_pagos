package com.pasarela.infrastructure.entrypoint.webhook.dto;

import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record UpdatePaymentRsDto(
        UUID orderId,
        String message,
        Instant date
) {
}

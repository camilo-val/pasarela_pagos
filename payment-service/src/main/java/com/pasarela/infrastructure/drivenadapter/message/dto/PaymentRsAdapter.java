package com.pasarela.infrastructure.drivenadapter.message.dto;

import com.pasarela.domain.enums.PaymentStatus;
import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record PaymentRsAdapter(
        UUID orderID,
        UUID transactionId,
        PaymentStatus status,
        String message,
        Instant date
){
}

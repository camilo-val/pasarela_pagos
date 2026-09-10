package com.pasarela.infrastructure.entrypoint.webhook.dto;

import com.pasarela.domain.enums.PaymentStatus;
import lombok.Builder;

import java.util.UUID;

@Builder
public record UpdatePaymentRqDto (
        PaymentStatus status,
        String description
){
}

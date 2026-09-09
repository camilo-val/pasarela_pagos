package com.authorization_payment.infrastructure.drivenadapter.rest.model;

import com.authorization_payment.domain.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class PaymentModel {
    private BigDecimal amount;
    private String currency;
    private String description;
    private UUID orderId;
    private PaymentStatus status;
}

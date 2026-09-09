package com.authorization_payment.domain.model;

import com.authorization_payment.domain.enums.PaymentStatus;
import com.authorization_payment.domain.exceptions.PaymentExceptions;
import com.authorization_payment.domain.exceptions.PaymentMessageExceptions;

import java.math.BigDecimal;
import java.util.UUID;

public class PaymentAuthorization {

    private final BigDecimal amount;
    private final String currency;
    private final String description;
    private final UUID orderId;
    private final PaymentStatus status;

    private PaymentAuthorization(BigDecimal amount, String currency, String description, UUID orderId, PaymentStatus status) {
        this.amount = amount;
        this.currency = currency;
        this.description = description;
        this.orderId = orderId;
        this.status = status;
    }

    public static PaymentAuthorization authorization(BigDecimal amount, String currency, String description, UUID orderId, PaymentStatus status) {
        boolean parameterIsInvalid = isNullOrBlank(currency) && isNullOrBlank(description);
        if (parameterIsInvalid || amount == null || orderId == null || status == null ) {
            throw new PaymentExceptions(PaymentMessageExceptions.INVALID_TRANSACTION);
        }
        return new PaymentAuthorization(amount, currency, description, orderId, status);
    }
    private static Boolean isNullOrBlank(String validator){
        return validator == null || validator.isBlank();
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public String getDescription() {
        return description;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public PaymentStatus getStatus() {
        return status;
    }
}

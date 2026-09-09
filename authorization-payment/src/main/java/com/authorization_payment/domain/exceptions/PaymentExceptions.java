package com.authorization_payment.domain.exceptions;

public class PaymentExceptions extends RuntimeException {
    private final PaymentMessageExceptions exceptions;

    public PaymentExceptions(PaymentMessageExceptions exceptions) {
        super(exceptions.getMessage());
        this.exceptions = exceptions;
    }

    public PaymentMessageExceptions getExceptions() {
        return exceptions;
    }
}

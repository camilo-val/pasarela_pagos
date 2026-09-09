package com.authorization_payment.domain.exceptions;

public enum PaymentMessageExceptions {

    INVALID_TRANSACTION("PA_001","Invalid transaction")
    ;
    private final String code;
    private final String message;

    PaymentMessageExceptions(String code, String mesage) {
        this.code = code;
        this.message = mesage;
    }

    public String getMessage() {
        return message;
    }

    public String getCode() {
        return code;
    }
}

package com.pasarela.domain.exceptions;

public enum BusinessTransactionalExceptions {

    INVALID_STATUS("PS_001", "INVALID STATUS"),
    INVALID_CURRENCY("PS_002", "INVALID CURRENCY"),
    INVALID_DATA_FOR_TRANSACTION("PS_003", "INVALID DATA FOR TRANSACTION"),
    TRANSACTION_ALREADY_EXISTS("PS_004", "TRANSACTION ALREADY EXISTS"),
    TRANSACTION_NOT_EXISTS("PS_005", "TRANSACTION NOT EXISTS"),
    ;

    private final String code;
    private final String message;

    BusinessTransactionalExceptions(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}

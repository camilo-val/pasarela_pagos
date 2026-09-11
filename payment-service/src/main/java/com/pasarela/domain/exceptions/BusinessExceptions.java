package com.pasarela.domain.exceptions;

public class BusinessExceptions extends RuntimeException{
    private final BusinessTransactionalExceptions businessTransactionalExceptions;

    public BusinessExceptions(BusinessTransactionalExceptions businessTransactionalExceptions) {
        super(businessTransactionalExceptions.getMessage());
        this.businessTransactionalExceptions = businessTransactionalExceptions;
    }

    public BusinessTransactionalExceptions getBusinessTransactionalExceptions() {
        return businessTransactionalExceptions;
    }
}

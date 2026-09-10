package com.pasarela.domain.model;

import com.pasarela.domain.enums.PaymentStatus;
import com.pasarela.domain.exceptions.BusinessExceptions;
import com.pasarela.domain.exceptions.BusinessTransactionalExceptions;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class Payment {
    private final String id;
    private final UUID userId;
    private final BigDecimal amount;
    private final String currency;
    private final PaymentStatus status;
    private final String description;
    private final UUID orderId;
    private final Instant createdAt;
    private final Instant updatedAt;

    private Payment(String id, UUID userId, BigDecimal amount, String currency, PaymentStatus status,
                    String description, UUID orderId, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.userId = userId;
        this.amount = amount;
        this.currency = currency;
        this.status = status;
        this.description = description;
        this.orderId = orderId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Payment create(UUID userId, BigDecimal amount, String currency,
                                 String description, UUID orderId) {
        if (userId == null || amount == null || orderId == null ) {
            throw new BusinessExceptions(BusinessTransactionalExceptions.INVALID_DATA_FOR_TRANSACTION);
        }
        validateAttribute(currency,description);
        return new Payment(null,userId,amount,currency,PaymentStatus.PENDING,description,orderId,Instant.now(),null);
    }

    public Payment updateStatus(PaymentStatus status, String description){
        if (status == PaymentStatus.PENDING ) {
            throw new BusinessExceptions(BusinessTransactionalExceptions.INVALID_STATUS);
        }
        return new Payment(this.id,this.userId,this.amount,this.currency,status, description,this.orderId,this.createdAt,Instant.now());
    }

    public static Payment rebuild(String id, UUID userId, BigDecimal amount, String currency, PaymentStatus status,
                                 String description, UUID orderId, Instant createdAt, Instant updatedAt) {
        if (id == null || userId == null || amount == null || status == null || orderId == null || createdAt == null) {
            throw new BusinessExceptions(BusinessTransactionalExceptions.INVALID_DATA_FOR_TRANSACTION);
        }
        validateAttribute(currency,description);
        return new Payment(id,userId,amount,currency,status,description,orderId,createdAt,updatedAt);
    }

    private static void validateAttribute(String currency,
                                 String description){

        boolean isValid = isNullOrBlak(currency)
                || isNullOrBlak(description);
        if(isValid){
            throw new BusinessExceptions(BusinessTransactionalExceptions.INVALID_DATA_FOR_TRANSACTION);
        }
    }

    private static boolean isNullOrBlak(String text){
        return text == null || text.isBlank();
    }

    public String getId() {
        return id;
    }

    public UUID getUserId() {
        return userId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public String getDescription() {
        return description;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public String toString() {
        return "Payment{" +
                "id=" + id +
                ", userId=" + userId +
                ", amount=" + amount +
                ", currency='" + currency + '\'' +
                ", status=" + status +
                ", description='" + description + '\'' +
                ", orderId=" + orderId +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}

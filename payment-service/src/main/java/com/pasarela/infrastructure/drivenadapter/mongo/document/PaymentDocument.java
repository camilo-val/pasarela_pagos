package com.pasarela.infrastructure.drivenadapter.mongo.document;

import com.pasarela.domain.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@AllArgsConstructor
@Getter
@Document(collection = "payment")
public class PaymentDocument {
    @Id
    private String id;
    private UUID transactionId;
    private BigDecimal amount;
    private String currency;
    private PaymentStatus status;
    private String description;
    private UUID orderId;
    private Instant createdAt;
    private Instant updatedAt;


    @Override
    public String toString() {
        return "PaymentDocument{" +
                "id=" + id +
                ", transactionId=" + transactionId +
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

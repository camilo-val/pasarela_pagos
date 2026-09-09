package com.pasarela.infrastructure.drivenadapter.mongo.mapper;

import com.pasarela.domain.model.Payment;
import com.pasarela.infrastructure.drivenadapter.mongo.document.PaymentDocument;
import org.mapstruct.Mapper;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface MapperAdapter {
    PaymentDocument toAdapter(Payment payment);

    default Payment toDomain(PaymentDocument paymentDocument) {
        return Payment.rebuild(paymentDocument.getId(),
                paymentDocument.getTransactionId(),
                paymentDocument.getAmount(),
                paymentDocument.getCurrency(),
                paymentDocument.getStatus(),
                paymentDocument.getDescription(),
                paymentDocument.getOrderId(),
                paymentDocument.getCreatedAt(),
                paymentDocument.getUpdatedAt());
    }
}

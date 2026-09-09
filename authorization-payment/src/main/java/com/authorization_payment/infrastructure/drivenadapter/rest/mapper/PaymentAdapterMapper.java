package com.authorization_payment.infrastructure.drivenadapter.rest.mapper;

import com.authorization_payment.domain.model.PaymentAuthorization;
import com.authorization_payment.infrastructure.drivenadapter.rest.model.PaymentModel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PaymentAdapterMapper {

    default PaymentAuthorization toDomain(PaymentModel model){
        return PaymentAuthorization.authorization(
                model.getAmount(),
                model.getCurrency(),
                model.getDescription(),
                model.getOrderId(),
                model.getStatus()
        );
    }
}

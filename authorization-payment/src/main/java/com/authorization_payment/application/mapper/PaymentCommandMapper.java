package com.authorization_payment.application.mapper;

import com.authorization_payment.application.command.ProcessPaymentCommand;
import com.authorization_payment.domain.model.PaymentAuthorization;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PaymentCommandMapper {
    ProcessPaymentCommand toCommand (PaymentAuthorization paymentAuthorization);
}

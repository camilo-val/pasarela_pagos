package com.pasarela.application.mapper;

import com.pasarela.application.command.ProcessPaymentCommand;
import com.pasarela.domain.model.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PaymentMapper {
    ProcessPaymentCommand toCommand(Payment payment);
}

package com.pasarela.infrastructure.entrypoint.websocket.mapper;

import com.pasarela.application.command.ProcessPaymentCommand;
import com.pasarela.infrastructure.entrypoint.websocket.dto.PaymentRqDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MapperWebsocketEntry {
    ProcessPaymentCommand toCommand (PaymentRqDto paymentRqDto);
}

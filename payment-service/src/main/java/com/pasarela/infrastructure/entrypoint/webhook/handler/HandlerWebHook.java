package com.pasarela.infrastructure.entrypoint.webhook.handler;

import com.pasarela.application.usecase.UpdatePaymentUC;
import com.pasarela.infrastructure.entrypoint.webhook.dto.UpdatePaymentRqDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@AllArgsConstructor
public class HandlerWebHook {

    private final UpdatePaymentUC updatePaymentUC;

    public Mono<ServerResponse> updateTransaction(ServerRequest request){
        System.out.printf("rquest : " + request.pathVariable("orderId"));
        return request.bodyToMono(UpdatePaymentRqDto.class)
                .flatMap(requestDto ->{

                            System.out.printf("123123");
                            return updatePaymentUC.updatePayment(request.pathVariable("orderId"),
                                    requestDto.status(),
                                    requestDto.description());
                        }
                        )
                .flatMap(payment -> ServerResponse.accepted().bodyValue(payment))
                .switchIfEmpty(ServerResponse.badRequest().bodyValue("Invalid request"));

    }

}

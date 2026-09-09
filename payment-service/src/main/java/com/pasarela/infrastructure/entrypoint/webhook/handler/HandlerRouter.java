package com.pasarela.infrastructure.entrypoint.webhook.handler;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.*;

@Configuration
public class HandlerRouter {

    @Bean
    public RouterFunction<ServerResponse> route(HandlerWebHook handlerWebHook){
        return RouterFunctions.route(RequestPredicates.PUT("/webhook/{orderId}"), handlerWebHook::updateTransaction);
    }
}

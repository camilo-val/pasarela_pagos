package com.authorization_payment.infrastructure.entrypoint.message.handler;

import com.authorization_payment.application.command.ProcessPaymentCommand;
import com.authorization_payment.application.usecase.PaymentProcessUC;
import com.authorization_payment.infrastructure.utils.Constants;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.util.Objects;

@Component
@AllArgsConstructor
public class RedisHandler {


    private final Logger log = LoggerFactory.getLogger(RedisHandler.class);
    private final ReactiveRedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;
    private final PaymentProcessUC useCase;

   @PostConstruct
    public void start(){
        Flux.interval(Duration.ofMillis(500))
                .flatMap(i ->
                        redisTemplate.opsForList()
                                .leftPop(Constants.QUEUE)
                )
                .filter(Objects::nonNull)
                .map(this::toCommand)
                .flatMap(useCase::updatePaymentStatus)
                .subscribe();
    }
    private ProcessPaymentCommand toCommand(String json) {
            try {
                    return objectMapper.readValue(json, ProcessPaymentCommand.class);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
        }

}

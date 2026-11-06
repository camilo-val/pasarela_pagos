package com.authorization_payment.infrastructure.entrypoint.message.handler;

import com.authorization_payment.application.command.ProcessPaymentCommand;
import com.authorization_payment.application.usecase.PaymentProcessUC;
import com.authorization_payment.infrastructure.utils.Constants;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.util.Objects;

@Component
@AllArgsConstructor
@Slf4j
public class RedisHandler {

    private final ReactiveRedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;
    private final PaymentProcessUC useCase;

    @PostConstruct
    public void start() {

        log.info("Starting Redis consumer for queue {}", Constants.QUEUE);

        Flux.interval(Duration.ofMillis(500))

                .flatMap(tick ->
                        redisTemplate.opsForList()
                                .leftPop(Constants.QUEUE)
                )

                .filter(Objects::nonNull)

                .flatMap(message ->

                        Mono.fromCallable(() -> toCommand(message))

                                .flatMap(useCase::updatePaymentStatus)

                                .doOnNext(result ->
                                        log.info(
                                                "Payment processed successfully. OrderId: {} Status: {}",
                                                result.getOrderId(),
                                                result.getStatus()
                                        )
                                )

                                .onErrorResume(error -> {

                                    log.error(
                                            "Error processing message: {}",
                                            message,
                                            error
                                    );

                                    return Mono.empty();
                                })
                )

                .doOnSubscribe(subscription ->
                        log.info("Redis consumer subscribed")
                )

                .doOnError(error ->
                        log.error("Redis consumer stopped unexpectedly", error)
                )

                .retryWhen(
                        Retry.fixedDelay(
                                        Long.MAX_VALUE,
                                        Duration.ofSeconds(5)
                                )
                                .doBeforeRetry(retry ->
                                        log.warn(
                                                "Retrying Redis consumer. Attempt: {}",
                                                retry.totalRetries() + 1
                                        )
                                )
                )

                .subscribe(
                        result -> {
                        },
                        error -> log.error(
                                "Fatal Redis consumer error",
                                error
                        )
                );
    }

    private ProcessPaymentCommand toCommand(String json) {

        try {

            return objectMapper.readValue(
                    json,
                    ProcessPaymentCommand.class
            );

        } catch (Exception e) {

            log.error(
                    "Error converting message to ProcessPaymentCommand: {}",
                    json,
                    e
            );

            throw new RuntimeException(e);
        }
    }
}
package com.authorization_payment.infrastructure.entrypoint.message.handler;

import com.authorization_payment.application.command.ProcessPaymentCommand;
import com.authorization_payment.application.usecase.PaymentProcessUC;
import com.authorization_payment.infrastructure.utils.Constants;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Component;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

@Component
@RequiredArgsConstructor
@Slf4j
public class RedisHandler {

    private final ReactiveRedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;
    private final PaymentProcessUC useCase;

    @Value("${sleep.start}")
    private int sleepStart;

    @Value("${sleep.end}")
    private int sleepEnd;

    private Disposable consumerSubscription;

    private volatile boolean shuttingDown = false;

    @PostConstruct
    public void start() {

        log.info("Starting Redis consumer for queue {}", Constants.QUEUE);

        consumerSubscription = createConsumer()
                .retryWhen(
                        Retry.backoff(
                                        Long.MAX_VALUE,
                                        Duration.ofSeconds(5)
                                )
                                .maxBackoff(Duration.ofMinutes(1))
                                .doBeforeRetry(signal ->
                                        log.warn(
                                                "Redis consumer restarting. Retry: {}",
                                                signal.totalRetries() + 1,
                                                signal.failure()
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

    private Flux<Void> createConsumer() {

        return Flux.interval(Duration.ofMillis(500))

                .takeWhile(tick -> !shuttingDown)

                .flatMap(tick ->
                        redisTemplate.opsForList()
                                .leftPop(Constants.QUEUE)
                )

                .filter(Objects::nonNull)

                .flatMap(message ->

                        Mono.delay(
                                        Duration.ofMillis(
                                                ThreadLocalRandom.current()
                                                        .nextInt(
                                                                sleepStart,
                                                                sleepEnd
                                                        )
                                        )
                                )

                                .then(
                                        Mono.fromCallable(
                                                () -> toCommand(message)
                                        )
                                )

                                .flatMap(useCase::updatePaymentStatus)

                                .doOnNext(result ->
                                        log.info(
                                                "Payment processed successfully. OrderId: {} Status: {}",
                                                result.getOrderId(),
                                                result.getStatus()
                                        )
                                )

                                .then()

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

                .doOnCancel(() ->
                        log.warn("Redis consumer cancelled")
                )

                .doOnComplete(() ->
                        log.warn("Redis consumer completed")
                )

                .doOnError(error ->
                        log.error(
                                "Redis consumer infrastructure error",
                                error
                        )
                );
    }

    @PreDestroy
    public void stop() {

        log.info("Stopping Redis consumer");

        shuttingDown = true;

        if (consumerSubscription != null
                && !consumerSubscription.isDisposed()) {

            consumerSubscription.dispose();

            log.info("Redis consumer stopped");
        }
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
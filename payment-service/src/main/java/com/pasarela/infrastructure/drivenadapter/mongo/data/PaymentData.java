package com.pasarela.infrastructure.drivenadapter.mongo.data;

import com.pasarela.infrastructure.drivenadapter.mongo.document.PaymentDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface PaymentData extends ReactiveMongoRepository<PaymentDocument, UUID> {

    Mono<PaymentDocument> findByOrderId(UUID orderId);
    Mono<Boolean> existsByOrderId(UUID orderId);
}

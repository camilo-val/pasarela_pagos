package com.pasarela.infrastructure.drivenadapter.mongo.adapter;

import com.pasarela.application.port.PaymentPort;
import com.pasarela.domain.model.Payment;
import com.pasarela.infrastructure.drivenadapter.mongo.data.PaymentData;
import com.pasarela.infrastructure.drivenadapter.mongo.document.PaymentDocument;
import com.pasarela.infrastructure.drivenadapter.mongo.mapper.MapperAdapter;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
@AllArgsConstructor
public class PaymentAdapter implements PaymentPort {

    private final PaymentData paymentData;
    private final MapperAdapter mapperAdapter;

    @Override
    public Mono<Payment> processPayment(Payment payment) {
        /*return paymentData.save(mapperAdapter.toAdapter(payment))
                .map(mapperAdapter::toDomain);ç*/

        System.out.println("ADAPTER - processPayment: " + payment);

        PaymentDocument document = mapperAdapter.toAdapter(payment);

        System.out.println("ADAPTER - document: " + document);

        return paymentData.save(document)

                .doOnNext(saved ->
                        System.out.println(
                                "MONGO - documento guardado: " + saved
                        )
                )

                .doOnSuccess(saved ->
                        System.out.println(
                                "MONGO - save terminó. Resultado: " + saved
                        )
                )

                .doOnError(error ->
                        System.err.println(
                                "MONGO - ERROR: " + error
                        )
                )

                .map(mapperAdapter::toDomain)

                .doOnNext(domain ->
                        System.out.println(
                                "MONGO - domain recuperado: " + domain
                        )
                )

                .doOnError(error ->
                        System.err.println(
                                "MONGO - ERROR convirtiendo a domain: " + error
                        )
                );
    }

    @Override
    public Mono<Payment> updatePayment(Payment payment) {
        return paymentData.save(mapperAdapter.toAdapter(payment))
                .map(mapperAdapter::toDomain);
    }

    @Override
    public Mono<Payment> getPaymentById(UUID id) {
        return paymentData.findById(id).map(mapperAdapter::toDomain);
    }

    @Override
    public Mono<Payment> getPaymentByOrderId(UUID orderId) {
        return paymentData.findByOrderId(orderId).map(mapperAdapter::toDomain);
    }

    @Override
    public Mono<Boolean> existsByOrderId(UUID orderId) {
        return paymentData.existsByOrderId(orderId);
    }
}

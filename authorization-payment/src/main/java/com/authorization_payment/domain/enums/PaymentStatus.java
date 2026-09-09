package com.authorization_payment.domain.enums;

import java.util.concurrent.ThreadLocalRandom;

public enum PaymentStatus {
    APPROVED,
    DECLINED;

    private static final PaymentStatus[] VALUES = values();
    public static PaymentStatus random() {
        return VALUES[ThreadLocalRandom.current().nextInt(VALUES.length)];
    }

}

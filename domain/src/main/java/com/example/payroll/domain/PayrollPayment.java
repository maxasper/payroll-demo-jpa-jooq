package com.example.payroll.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;

@Getter
public class PayrollPayment {
    private final UUID id;
    private final String beneficiary;
    private final BigDecimal amount;
    private PayrollPaymentStatus status;
    private final Instant createdAt;

    public PayrollPayment(UUID id, String beneficiary, BigDecimal amount, PayrollPaymentStatus status, Instant createdAt) {
        this.id = id;
        this.beneficiary = beneficiary;
        this.amount = amount;
        this.status = status;
        this.createdAt = createdAt;
    }

    void markExecuted(Instant executedAt) {
        status = PayrollPaymentStatus.EXECUTED;
    }
}

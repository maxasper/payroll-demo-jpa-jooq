package com.example.payroll.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.Getter;

@Getter
public class PayrollBatch {
    private final UUID id;
    private final long customerId;
    private PayrollBatchStatus status;
    private BigDecimal totalAmount;
    private final Instant createdAt;
    private Instant updatedAt;
    private final List<PayrollPayment> payments;

    public PayrollBatch(UUID id, long customerId, Instant createdAt, Instant updatedAt) {
        this(id, customerId, PayrollBatchStatus.NEW, BigDecimal.ZERO, createdAt, updatedAt, new ArrayList<>());
    }

    public PayrollBatch(
        UUID id,
        long customerId,
        PayrollBatchStatus status,
        BigDecimal totalAmount,
        Instant createdAt,
        Instant updatedAt,
        List<PayrollPayment> payments
    ) {
        this.id = id;
        this.customerId = customerId;
        this.status = status;
        this.totalAmount = totalAmount == null ? BigDecimal.ZERO : totalAmount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.payments = payments == null ? new ArrayList<>() : new ArrayList<>(payments);
        ensureTotalsMatch();
    }

    public PayrollPayment addPayment(UUID paymentId, String beneficiary, BigDecimal amount, Instant createdAt) {
        ensureStatus(PayrollBatchStatus.NEW, "Payments can only be added to NEW batches.");
        PayrollPayment payment = new PayrollPayment(paymentId, beneficiary, amount, PayrollPaymentStatus.NEW, createdAt);
        payments.add(payment);
        recalculateTotal();
        return payment;
    }

    public void validate() {
        ensureStatus(PayrollBatchStatus.NEW, "Only NEW batches can be validated.");
        if (payments.isEmpty()) {
            throw new IllegalStateException("Batch must contain at least one payment to validate.");
        }
        status = PayrollBatchStatus.VALIDATED;
    }

    public void execute(Instant executedAt) {
        ensureStatus(PayrollBatchStatus.VALIDATED, "Only VALIDATED batches can be executed.");
        payments.forEach(payment -> payment.markExecuted(executedAt));
        status = PayrollBatchStatus.EXECUTED;
        updatedAt = executedAt;
        recalculateTotal();
    }

    private void recalculateTotal() {
        totalAmount = payments.stream()
            .map(PayrollPayment::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void ensureTotalsMatch() {
        BigDecimal calculated = payments.stream()
            .map(PayrollPayment::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (totalAmount.compareTo(calculated) != 0) {
            throw new IllegalStateException("Batch total does not match payment sum.");
        }
    }

    private void ensureStatus(PayrollBatchStatus expected, String message) {
        if (status != expected) {
            throw new IllegalStateException(message);
        }
    }
}

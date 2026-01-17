package com.example.payroll.application.usecase;

import com.example.payroll.domain.PayrollBatch;
import com.example.payroll.domain.PayrollPayment;
import com.example.payroll.domain.port.PayrollBatchRepository;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AddPayrollPaymentUseCase {
    private final PayrollBatchRepository repository;
    private final Clock clock;

    public UUID addPayment(UUID batchId, String beneficiary, BigDecimal amount) {
        PayrollBatch batch = repository.findWithPaymentsById(batchId)
            .orElseThrow(() -> new IllegalArgumentException("Batch not found: " + batchId));
        Instant now = clock.instant();
        PayrollPayment payment = batch.addPayment(UUID.randomUUID(), beneficiary, amount, now);
        repository.save(batch);
        return payment.getId();
    }
}

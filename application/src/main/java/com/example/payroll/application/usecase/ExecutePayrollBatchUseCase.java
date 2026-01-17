package com.example.payroll.application.usecase;

import com.example.payroll.domain.PayrollBatch;
import com.example.payroll.domain.port.PayrollBatchRepository;
import jakarta.transaction.Transactional;
import java.time.Clock;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ExecutePayrollBatchUseCase {
    private final PayrollBatchRepository repository;
    private final Clock clock;

    @Transactional
    public void execute(UUID batchId) {
        PayrollBatch batch = repository.findWithPaymentsById(batchId)
            .orElseThrow(() -> new IllegalArgumentException("Batch not found: " + batchId));
        batch.validate();
        Instant now = clock.instant();
        batch.execute(now);
        repository.save(batch);
    }
}

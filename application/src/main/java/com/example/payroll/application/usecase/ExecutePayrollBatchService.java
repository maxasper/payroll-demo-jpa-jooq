package com.example.payroll.application.usecase;

import com.example.payroll.domain.PayrollBatch;
import com.example.payroll.domain.port.PayrollBatchRepositoryPort;
import com.example.payroll.domain.port.PayrollStatsPort;
import jakarta.transaction.Transactional;
import java.time.Clock;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ExecutePayrollBatchService implements ExecutePayrollBatchUseCase {
    private final PayrollBatchRepositoryPort repository;
    private final PayrollStatsPort statsPort;
    private final Clock clock;

    @Override
    @Transactional
    public void execute(UUID batchId) {
        PayrollBatch batch = repository.findWithPaymentsById(batchId)
            .orElseThrow(() -> new IllegalArgumentException("Batch not found: " + batchId));
        batch.validate();
        Instant now = clock.instant();
        batch.execute(now);
        repository.save(batch);
        statsPort.upsertBatchStats(batchId);
    }
}

package com.example.payroll.application.usecase;

import com.example.payroll.domain.PayrollBatch;
import com.example.payroll.domain.port.PayrollBatchRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CreatePayrollBatchService implements CreatePayrollBatchUseCase {
    private final PayrollBatchRepository repository;
    private final Clock clock;

    @Override
    public UUID create(long customerId) {
        Instant now = clock.instant();
        PayrollBatch batch = new PayrollBatch(UUID.randomUUID(), customerId, now, now);
        return repository.save(batch).getId();
    }
}

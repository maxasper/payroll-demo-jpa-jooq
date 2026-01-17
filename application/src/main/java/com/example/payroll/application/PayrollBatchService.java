package com.example.payroll.application;

import com.example.payroll.domain.PayrollBatch;
import com.example.payroll.domain.port.PayrollBatchRepository;
import jakarta.transaction.Transactional;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PayrollBatchService {
    private final PayrollBatchRepository repository;

    @Transactional
    public PayrollBatch createBatch(String name) {
        PayrollBatch batch = new PayrollBatch(UUID.randomUUID(), name);
        return repository.save(batch);
    }

    public Optional<PayrollBatch> findBatch(UUID id) {
        return repository.findById(id);
    }
}

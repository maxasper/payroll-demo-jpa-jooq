package com.example.payroll.integrations.adapters.jpa;

import com.example.payroll.domain.PayrollBatch;
import com.example.payroll.domain.port.PayrollBatchRepository;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PayrollBatchJpaAdapter implements PayrollBatchRepository {
    private final PayrollBatchJpaRepository repository;

    @Override
    public PayrollBatch save(PayrollBatch batch) {
        PayrollBatchEntity entity = new PayrollBatchEntity(batch.getId(), batch.getName());
        PayrollBatchEntity saved = repository.save(entity);
        return new PayrollBatch(saved.getId(), saved.getName());
    }

    @Override
    public Optional<PayrollBatch> findById(UUID id) {
        return repository.findById(id)
            .map(entity -> new PayrollBatch(entity.getId(), entity.getName()));
    }
}

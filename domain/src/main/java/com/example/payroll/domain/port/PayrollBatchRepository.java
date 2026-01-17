package com.example.payroll.domain.port;

import com.example.payroll.domain.PayrollBatch;
import java.util.Optional;
import java.util.UUID;

public interface PayrollBatchRepository {
    PayrollBatch save(PayrollBatch batch);

    Optional<PayrollBatch> findById(UUID id);
}

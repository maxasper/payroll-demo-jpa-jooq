package com.example.payroll.application.usecase;

import com.example.payroll.domain.BatchSummary;
import java.util.List;

public interface ListPayrollBatchesJpaUseCase {
    List<BatchSummary> listBatches(String status, Long customerId, int page, int size, String sort);
}

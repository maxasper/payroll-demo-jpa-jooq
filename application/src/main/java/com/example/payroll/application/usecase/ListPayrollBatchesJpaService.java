package com.example.payroll.application.usecase;

import com.example.payroll.domain.BatchSummary;
import com.example.payroll.domain.port.PayrollBatchJpaQueryPort;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ListPayrollBatchesJpaService implements ListPayrollBatchesJpaUseCase {
    private final PayrollBatchJpaQueryPort queryPort;

    @Override
    public List<BatchSummary> listBatches(String status, Long customerId, int page, int size, String sort) {
        return queryPort.listBatches(status, customerId, page, size, sort);
    }
}

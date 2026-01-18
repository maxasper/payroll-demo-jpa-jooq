package com.example.payroll.persistence.jooq;

import com.example.payroll.domain.BatchSummary;
import com.example.payroll.domain.port.PayrollBatchQueryPort;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.jooq.SortField;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PayrollBatchQueryJooqAdapter implements PayrollBatchQueryPort {
    private final PayrollBatchReportRepository repository;

    @Override
    public List<BatchSummary> listBatches(
        String status,
        Long customerId,
        int page,
        int size,
        String sort
    ) {
        SortField<?> sortField = repository.resolveSort(sort);
        return repository.fetchBatchSummaries(status, customerId, page, size, sortField);
    }
}

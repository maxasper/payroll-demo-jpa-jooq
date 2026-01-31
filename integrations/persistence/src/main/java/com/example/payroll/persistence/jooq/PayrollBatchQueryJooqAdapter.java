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
    private final PayrollBatchReportQuery reportQuery;

    @Override
    public List<BatchSummary> listBatches(
        String status,
        Long customerId,
        int page,
        int size,
        String sort
    ) {
        SortField<?> sortField = reportQuery.resolveSort(sort);
        return reportQuery.fetchBatchSummaries(status, customerId, page, size, sortField);
    }
}

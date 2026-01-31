package com.example.payroll.persistence.jooq;

import com.example.payroll.domain.BatchSummary;
import java.util.List;
import org.jooq.SortField;

public interface PayrollBatchReportQuery {
    List<BatchSummary> fetchBatchSummaries(
        String status,
        Long customerId,
        int page,
        int size,
        SortField<?> sortField
    );

    SortField<?> resolveSort(String sort);
}

package com.example.payroll.persistence.jooq;

import com.example.payroll.persistence.jooq.dto.BatchSummaryDto;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.jooq.SortField;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PayrollBatchQueryService {
    private final PayrollBatchReportRepository repository;

    public List<BatchSummaryDto> listBatches(
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

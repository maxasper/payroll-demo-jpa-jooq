package com.example.payroll.integrations.adapters.jpa;

import com.example.payroll.integrations.adapters.jpa.dto.BatchSummaryDto;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PayrollBatchJpaQueryService {
    private final PayrollBatchJpaRepository repository;

    public List<BatchSummaryDto> listBatches(String status, Long customerId, int page, int size, String sort) {
        PayrollBatchStatusEntity statusEntity = status == null ? null : PayrollBatchStatusEntity.valueOf(status);
        Sort sortSpec = resolveSort(sort);

        Page<UUID> pageIds = repository.findBatchIds(statusEntity, customerId, PageRequest.of(page, size, sortSpec));
        List<UUID> ids = pageIds.getContent();
        if (ids.isEmpty()) {
            return List.of();
        }

        // Naive alternative (do not use):
        // `findAll` with join fetch + pageable often breaks paging (duplicates, multiple bag fetch).
        // It can explode row counts and lead to incorrect page sizes or multiple bag exceptions.

        List<PayrollBatchEntity> batches = repository.findWithPaymentsByIdIn(ids);
        Map<UUID, BatchSummaryDto> summaries = batches.stream()
            .collect(Collectors.toMap(
                PayrollBatchEntity::getId,
                batch -> {
                    BigDecimal totalAmount = batch.getPayments().stream()
                        .map(PayrollPaymentEntity::getAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                    return new BatchSummaryDto(
                        batch.getId(),
                        batch.getCustomerId(),
                        batch.getStatus().name(),
                        batch.getPayments().size(),
                        totalAmount,
                        batch.getCreatedAt(),
                        batch.getUpdatedAt()
                    );
                }
            ));

        return ids.stream()
            .map(summaries::get)
            .filter(java.util.Objects::nonNull)
            .toList();
    }

    private Sort resolveSort(String sort) {
        if (sort != null && sort.equalsIgnoreCase("status")) {
            return Sort.by(Sort.Direction.DESC, "status");
        }
        return Sort.by(Sort.Direction.DESC, "createdAt");
    }
}

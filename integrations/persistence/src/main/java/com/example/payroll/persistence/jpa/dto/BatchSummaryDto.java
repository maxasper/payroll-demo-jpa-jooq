package com.example.payroll.persistence.jpa.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import lombok.Value;

@Value
public class BatchSummaryDto {
    UUID batchId;
    long customerId;
    String status;
    int paymentsCount;
    BigDecimal totalAmount;
    Instant createdAt;
    Instant updatedAt;
}

package com.example.payroll.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import lombok.Value;

@Value
public class BatchSummary {
    UUID batchId;
    long customerId;
    String status;
    int paymentsCount;
    BigDecimal totalAmount;
    Instant createdAt;
    Instant updatedAt;
}

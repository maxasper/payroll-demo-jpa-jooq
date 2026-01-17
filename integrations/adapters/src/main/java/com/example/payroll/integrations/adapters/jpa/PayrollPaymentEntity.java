package com.example.payroll.integrations.adapters.jpa;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "payroll_payment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PayrollPaymentEntity {
    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "batch_id", nullable = false)
    private PayrollBatchEntity batch;

    private String beneficiary;
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private PayrollPaymentStatusEntity status;

    private Instant createdAt;
}

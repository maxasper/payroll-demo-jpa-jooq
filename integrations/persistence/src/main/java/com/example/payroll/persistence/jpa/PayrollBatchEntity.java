package com.example.payroll.persistence.jpa;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "payroll_batch")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PayrollBatchEntity {
    @Id
    private UUID id;
    private long customerId;

    @Enumerated(EnumType.STRING)
    private PayrollBatchStatusEntity status;

    private BigDecimal totalAmount;
    private Instant createdAt;
    private Instant updatedAt;

    @OneToMany(mappedBy = "batch", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<PayrollPaymentEntity> payments = new ArrayList<>();
}

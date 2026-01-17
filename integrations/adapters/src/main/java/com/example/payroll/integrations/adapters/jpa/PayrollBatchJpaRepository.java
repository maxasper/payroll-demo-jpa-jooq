package com.example.payroll.integrations.adapters.jpa;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PayrollBatchJpaRepository extends JpaRepository<PayrollBatchEntity, UUID> {
}

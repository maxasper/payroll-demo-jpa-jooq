package com.example.payroll.integrations.adapters.jpa;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PayrollBatchJpaRepository extends JpaRepository<PayrollBatchEntity, UUID> {
    @Query("select distinct batch from PayrollBatchEntity batch left join fetch batch.payments where batch.id = :id")
    Optional<PayrollBatchEntity> findWithPaymentsById(@Param("id") UUID id);
}

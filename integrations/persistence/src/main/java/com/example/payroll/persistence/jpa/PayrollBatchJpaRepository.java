package com.example.payroll.persistence.jpa;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PayrollBatchJpaRepository extends JpaRepository<PayrollBatchEntity, UUID> {
    @Query("select distinct batch from PayrollBatchEntity batch left join fetch batch.payments where batch.id = :id")
    Optional<PayrollBatchEntity> findWithPaymentsById(@Param("id") UUID id);

    @Query("select batch.id from PayrollBatchEntity batch where (:status is null or batch.status = :status) and (:customerId is null or batch.customerId = :customerId)")
    Page<UUID> findBatchIds(@Param("status") PayrollBatchStatusEntity status,
                             @Param("customerId") Long customerId,
                             Pageable pageable);

    @Query("select distinct batch from PayrollBatchEntity batch left join fetch batch.payments where batch.id in :ids")
    List<PayrollBatchEntity> findWithPaymentsByIdIn(@Param("ids") Collection<UUID> ids);
}

package com.example.payroll.persistence.jpa;

import com.example.payroll.domain.PayrollBatch;
import com.example.payroll.domain.PayrollBatchStatus;
import com.example.payroll.domain.PayrollPayment;
import com.example.payroll.domain.PayrollPaymentStatus;
import com.example.payroll.domain.port.PayrollBatchRepositoryPort;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PayrollBatchRepositoryPortAdapter implements PayrollBatchRepositoryPort {
    private static final Logger logger = LoggerFactory.getLogger(PayrollBatchRepositoryPortAdapter.class);

    private final PayrollBatchJpaRepository repository;

    @Override
    public PayrollBatch save(PayrollBatch batch) {
        logger.info("JPA saving payroll batch id={} payments={}", batch.getId(), batch.getPayments().size());
        PayrollBatchEntity entity = toEntity(batch);
        PayrollBatchEntity saved = repository.save(entity);
        return toDomain(saved, true);
    }

    @Override
    public Optional<PayrollBatch> findById(UUID id) {
        logger.info("JPA fetching payroll batch id={}", id);
        return repository.findById(id)
            .map(entity -> toDomain(entity, false));
    }

    @Override
    public Optional<PayrollBatch> findWithPaymentsById(UUID id) {
        logger.info("JPA fetching payroll batch with payments id={}", id);
        return repository.findWithPaymentsById(id)
            .map(entity -> toDomain(entity, true));
    }

    private PayrollBatchEntity toEntity(PayrollBatch batch) {
        PayrollBatchEntity entity = new PayrollBatchEntity();
        entity.setId(batch.getId());
        entity.setCustomerId(batch.getCustomerId());
        entity.setStatus(PayrollBatchStatusEntity.valueOf(batch.getStatus().name()));
        entity.setTotalAmount(batch.getTotalAmount());
        entity.setCreatedAt(batch.getCreatedAt());
        entity.setUpdatedAt(batch.getUpdatedAt());
        List<PayrollPaymentEntity> payments = batch.getPayments().stream()
            .map(payment -> toPaymentEntity(entity, payment))
            .collect(Collectors.toList());
        entity.setPayments(payments);
        return entity;
    }

    private PayrollPaymentEntity toPaymentEntity(PayrollBatchEntity batchEntity, PayrollPayment payment) {
        PayrollPaymentEntity entity = new PayrollPaymentEntity();
        entity.setId(payment.getId());
        entity.setBatch(batchEntity);
        entity.setBeneficiary(payment.getBeneficiary());
        entity.setAmount(payment.getAmount());
        entity.setStatus(PayrollPaymentStatusEntity.valueOf(payment.getStatus().name()));
        entity.setCreatedAt(payment.getCreatedAt());
        return entity;
    }

    private PayrollBatch toDomain(PayrollBatchEntity entity, boolean includePayments) {
        List<PayrollPayment> payments = includePayments
            ? entity.getPayments().stream().map(this::toDomainPayment).collect(Collectors.toList())
            : List.of();
        return new PayrollBatch(
            entity.getId(),
            entity.getCustomerId(),
            PayrollBatchStatus.valueOf(entity.getStatus().name()),
            entity.getTotalAmount(),
            entity.getCreatedAt(),
            entity.getUpdatedAt(),
            payments
        );
    }

    private PayrollPayment toDomainPayment(PayrollPaymentEntity entity) {
        return new PayrollPayment(
            entity.getId(),
            entity.getBeneficiary(),
            entity.getAmount(),
            PayrollPaymentStatus.valueOf(entity.getStatus().name()),
            entity.getCreatedAt()
        );
    }
}

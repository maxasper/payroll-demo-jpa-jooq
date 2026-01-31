package com.example.payroll.persistence.jooq;

import com.example.payroll.domain.BatchSummary;
import com.example.payroll.jooq.tables.PayrollBatch;
import com.example.payroll.jooq.tables.PayrollPayment;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record7;
import org.jooq.SortField;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

@Repository
@Profile("jooq-codegen")
@Primary
@RequiredArgsConstructor
public class PayrollBatchReportCodegenQuery implements PayrollBatchReportQuery {
    private static final Logger logger = LoggerFactory.getLogger(PayrollBatchReportCodegenQuery.class);

    private static final PayrollBatch PAYROLL_BATCH = PayrollBatch.PAYROLL_BATCH;
    private static final PayrollPayment PAYROLL_PAYMENT = PayrollPayment.PAYROLL_PAYMENT;

    private final DSLContext dsl;

    @Override
    public List<BatchSummary> fetchBatchSummaries(
        String status,
        Long customerId,
        int page,
        int size,
        SortField<?> sortField
    ) {
        logger.info(
            "jOOQ CODEGEN fetching batch summaries status={} customerId={} page={} size={} sort={}",
            status,
            customerId,
            page,
            size,
            sortField
        );
        Condition condition = DSL.noCondition();
        if (status != null) {
            condition = condition.and(PAYROLL_BATCH.STATUS.eq(status));
        }
        if (customerId != null) {
            condition = condition.and(PAYROLL_BATCH.CUSTOMER_ID.eq(customerId));
        }

        List<Record7<UUID, Long, String, Integer, BigDecimal, OffsetDateTime, OffsetDateTime>> records = dsl.select(
                PAYROLL_BATCH.ID,
                PAYROLL_BATCH.CUSTOMER_ID,
                PAYROLL_BATCH.STATUS,
                DSL.count(PAYROLL_PAYMENT.ID).cast(Integer.class),
                PAYROLL_BATCH.TOTAL_AMOUNT,
                PAYROLL_BATCH.CREATED_AT,
                PAYROLL_BATCH.UPDATED_AT
            )
            .from(PAYROLL_BATCH)
            .leftJoin(PAYROLL_PAYMENT).on(PAYROLL_PAYMENT.BATCH_ID.eq(PAYROLL_BATCH.ID))
            .where(condition)
            .groupBy(
                PAYROLL_BATCH.ID,
                PAYROLL_BATCH.CUSTOMER_ID,
                PAYROLL_BATCH.STATUS,
                PAYROLL_BATCH.TOTAL_AMOUNT,
                PAYROLL_BATCH.CREATED_AT,
                PAYROLL_BATCH.UPDATED_AT
            )
            .orderBy(sortField)
            .limit(size)
            .offset(page * size)
            .fetch();

        return records.stream()
            .map(record -> new BatchSummary(
                record.value1(),
                record.value2(),
                record.value3(),
                record.value4(),
                record.value5(),
                toInstant(record.value6()),
                toInstant(record.value7())
            ))
            .toList();
    }

    @Override
    public SortField<?> resolveSort(String sort) {
        String value = sort == null ? "createdAt" : sort;
        if (value.equalsIgnoreCase("status")) {
            return PAYROLL_BATCH.STATUS.desc();
        }
        return PAYROLL_BATCH.CREATED_AT.desc();
    }

    private Instant toInstant(OffsetDateTime value) {
        return value == null ? null : value.toInstant();
    }
}

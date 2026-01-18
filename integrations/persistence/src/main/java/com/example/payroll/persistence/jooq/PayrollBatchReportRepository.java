package com.example.payroll.persistence.jooq;

import com.example.payroll.persistence.jooq.dto.BatchSummaryDto;
import lombok.RequiredArgsConstructor;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record7;
import org.jooq.SortField;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;

@Repository
@RequiredArgsConstructor
public class PayrollBatchReportRepository {
    private static final Logger logger = LoggerFactory.getLogger(PayrollBatchReportRepository.class);

    private static final Field<UUID> BATCH_ID = field("payroll_batch.id", UUID.class);
    private static final Field<Long> CUSTOMER_ID = field("payroll_batch.customer_id", Long.class);
    private static final Field<String> STATUS = field("payroll_batch.status", String.class);
    private static final Field<BigDecimal> TOTAL_AMOUNT = field("payroll_batch.total_amount", BigDecimal.class);
    private static final Field<Instant> CREATED_AT = field("payroll_batch.created_at", Instant.class);
    private static final Field<Instant> UPDATED_AT = field("payroll_batch.updated_at", Instant.class);
    private static final Field<Integer> PAYMENT_COUNT = DSL.count(field("payroll_payment.id")).cast(Integer.class);

    private final DSLContext dsl;

    public List<BatchSummaryDto> fetchBatchSummaries(
            String status,
            Long customerId,
            int page,
            int size,
            SortField<?> sortField
    ) {
        logger.info(
            "jOOQ fetching batch summaries status={} customerId={} page={} size={} sort={}",
            status,
            customerId,
            page,
            size,
            sortField
        );
        Condition condition = DSL.noCondition();
        if (status != null) {
            condition = condition.and(STATUS.eq(status));
        }
        if (customerId != null) {
            condition = condition.and(CUSTOMER_ID.eq(customerId));
        }

        List<Record7<UUID, Long, String, Integer, BigDecimal, Instant, Instant>> records = dsl.select(
                        BATCH_ID,
                        CUSTOMER_ID,
                        STATUS,
                        PAYMENT_COUNT,
                        TOTAL_AMOUNT,
                        CREATED_AT,
                        UPDATED_AT
                )
                .from(table("payroll_batch"))
                .leftJoin(table("payroll_payment")).on(field("payroll_payment.batch_id").eq(BATCH_ID))
                .where(condition)
                .groupBy(BATCH_ID, CUSTOMER_ID, STATUS, TOTAL_AMOUNT, CREATED_AT, UPDATED_AT)
                .orderBy(sortField)
                .limit(size)
                .offset(page * size)
                .fetch();

        return records.stream()
                .map(record -> new BatchSummaryDto(
                        record.value1(),
                        record.value2(),
                        record.value3(),
                        record.value4(),
                        record.value5(),
                        record.value6(),
                        record.value7()
                ))
                .toList();
    }

    public SortField<?> resolveSort(String sort) {
        String value = sort == null ? "createdAt" : sort;
        if (value.equalsIgnoreCase("status")) {
            return STATUS.desc();
        }
        return CREATED_AT.desc();
    }
}

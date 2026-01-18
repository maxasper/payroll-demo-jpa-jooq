package com.example.payroll.persistence.jooq;

import com.example.payroll.application.port.PayrollStatsPort;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;

@Repository
@RequiredArgsConstructor
public class PayrollStatsJooqAdapter implements PayrollStatsPort {
    private static final Field<UUID> BATCH_ID = field("batch_id", UUID.class);
    private static final Field<Integer> PAYMENTS_CNT = field("payments_cnt", Integer.class);
    private static final Field<BigDecimal> TOTAL_AMOUNT = field("total_amount", BigDecimal.class);
    private static final Field<Instant> LAST_EXEC = field("last_exec", Instant.class);

    private final DSLContext dsl;

    @Override
    public void upsertBatchStats(UUID batchId) {
        Field<Integer> paymentsCount = DSL.select(DSL.count())
            .from(table("payroll_payment"))
            .where(field("batch_id").eq(batchId))
            .asField()
            .cast(Integer.class);
        Field<BigDecimal> totalAmount = DSL.select(DSL.coalesce(DSL.sum(field("amount", BigDecimal.class)), DSL.inline(BigDecimal.ZERO)))
            .from(table("payroll_payment"))
            .where(field("batch_id").eq(batchId))
            .asField();

        dsl.insertInto(table("payroll_stats"))
            .columns(BATCH_ID, PAYMENTS_CNT, TOTAL_AMOUNT, LAST_EXEC)
            .values(
                DSL.val(batchId),
                paymentsCount,
                totalAmount,
                DSL.currentTimestamp().cast(Instant.class)
            )
            .onConflict(BATCH_ID)
            .doUpdate()
            .set(PAYMENTS_CNT, paymentsCount)
            .set(TOTAL_AMOUNT, totalAmount)
            .set(LAST_EXEC, DSL.currentTimestamp().cast(Instant.class))
            .execute();
    }
}

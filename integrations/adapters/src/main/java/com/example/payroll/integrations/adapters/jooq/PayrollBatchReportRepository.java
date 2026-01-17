package com.example.payroll.integrations.adapters.jooq;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PayrollBatchReportRepository {
    private final DSLContext dsl;

    public int countBatches() {
        return dsl.fetchCount(DSL.table("payroll_batch"));
    }
}

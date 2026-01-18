package com.example.payroll.domain.port;

import java.util.UUID;

public interface PayrollStatsPort {
    void upsertBatchStats(UUID batchId);
}

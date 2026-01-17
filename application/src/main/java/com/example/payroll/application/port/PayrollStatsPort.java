package com.example.payroll.application.port;

import java.util.UUID;

public interface PayrollStatsPort {
    void upsertBatchStats(UUID batchId);
}

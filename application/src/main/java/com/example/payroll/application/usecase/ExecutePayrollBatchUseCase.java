package com.example.payroll.application.usecase;

import java.util.UUID;

public interface ExecutePayrollBatchUseCase {
    void execute(UUID batchId);
}

package com.example.payroll.application.usecase;

import java.util.UUID;

public interface CreatePayrollBatchUseCase {
    UUID create(long customerId);
}

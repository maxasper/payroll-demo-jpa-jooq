package com.example.payroll.application.usecase;

import java.math.BigDecimal;
import java.util.UUID;

public interface AddPayrollPaymentUseCase {
    UUID addPayment(UUID batchId, String beneficiary, BigDecimal amount);
}

package com.example.payroll.domain.port;

import com.example.payroll.domain.BatchSummary;
import java.util.List;

public interface PayrollBatchQueryPort {
    List<BatchSummary> listBatches(String status, Long customerId, int page, int size, String sort);
}

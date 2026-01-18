package com.example.payroll.web.rest;

import com.example.payroll.application.usecase.ListPayrollBatchesUseCase;
import com.example.payroll.domain.BatchSummary;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/batches")
@RequiredArgsConstructor
public class PayrollBatchReadController {
    private final ListPayrollBatchesUseCase queryService;

    @GetMapping
    public List<BatchSummary> listBatches(
        @RequestParam(name = "status", required = false) String status,
        @RequestParam(name = "customerId", required = false) Long customerId,
        @RequestParam(name = "page", defaultValue = "0") int page,
        @RequestParam(name = "size", defaultValue = "20") int size,
        @RequestParam(name = "sort", required = false) String sort
    ) {
        return queryService.listBatches(status, customerId, page, size, sort);
    }
}

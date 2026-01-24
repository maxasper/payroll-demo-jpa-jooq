package com.example.payroll.web.rest;

import com.example.payroll.application.usecase.ListPayrollBatchesUseCase;
import com.example.payroll.domain.BatchSummary;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/batches")
@RequiredArgsConstructor
public class PayrollBatchReadController {
    private static final Logger logger = LoggerFactory.getLogger(PayrollBatchReadController.class);

    private final ListPayrollBatchesUseCase queryService;

    @GetMapping
    @Operation(
        summary = "List payroll batches (jOOQ)",
        description = "Controller: PayrollBatchReadController. Technology: jOOQ read model."
    )
    public List<BatchSummary> listBatches(
        @RequestParam(name = "status", required = false) String status,
        @RequestParam(name = "customerId", required = false) Long customerId,
        @RequestParam(name = "page", defaultValue = "0") int page,
        @RequestParam(name = "size", defaultValue = "20") int size,
        @RequestParam(name = "sort", required = false) String sort
    ) {
        long startTime = System.nanoTime();
        logger.info(
            "Endpoint GET /batches called with status={}, customerId={}, page={}, size={}, sort={}",
            status,
            customerId,
            page,
            size,
            sort
        );
        try {
            return queryService.listBatches(status, customerId, page, size, sort);
        } finally {
            long durationMs = (System.nanoTime() - startTime) / 1_000_000;
            logger.info("Endpoint GET /batches completed in {} ms", durationMs);
        }
    }
}

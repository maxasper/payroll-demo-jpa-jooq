package com.example.payroll.web.rest;

import com.example.payroll.application.usecase.AddPayrollPaymentUseCase;
import com.example.payroll.application.usecase.CreatePayrollBatchUseCase;
import com.example.payroll.application.usecase.ExecutePayrollBatchUseCase;
import com.example.payroll.web.rest.dto.AddPaymentRequest;
import com.example.payroll.web.rest.dto.BatchResponse;
import com.example.payroll.web.rest.dto.CreateBatchRequest;
import com.example.payroll.web.rest.dto.PaymentResponse;
import io.swagger.v3.oas.annotations.Operation;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/batches")
@RequiredArgsConstructor
public class PayrollBatchController {
    private static final Logger logger = LoggerFactory.getLogger(PayrollBatchController.class);

    private final CreatePayrollBatchUseCase createBatchUseCase;
    private final AddPayrollPaymentUseCase addPaymentUseCase;
    private final ExecutePayrollBatchUseCase executeBatchUseCase;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
        summary = "Create a payroll batch",
        description = "Controller: PayrollBatchController. Technology: JPA write model."
    )
    public BatchResponse createBatch(@RequestBody CreateBatchRequest request) {
        long startTime = System.nanoTime();
        logger.info("Endpoint POST /batches called with customerId={}", request.getCustomerId());
        try {
            UUID batchId = createBatchUseCase.create(request.getCustomerId());
            return new BatchResponse(batchId);
        } finally {
            long durationMs = (System.nanoTime() - startTime) / 1_000_000;
            logger.info("Endpoint POST /batches completed in {} ms", durationMs);
        }
    }

    @PostMapping("/{batchId}/payments")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
        summary = "Add a payment to a batch",
        description = "Controller: PayrollBatchController. Technology: JPA write model."
    )
    public PaymentResponse addPayment(@PathVariable("batchId") UUID batchId, @RequestBody AddPaymentRequest request) {
        long startTime = System.nanoTime();
        logger.info(
            "Endpoint POST /batches/{}/payments called with beneficiary={}, amount={}",
            batchId,
            request.getBeneficiary(),
            request.getAmount()
        );
        try {
            UUID paymentId = addPaymentUseCase.addPayment(batchId, request.getBeneficiary(), request.getAmount());
            return new PaymentResponse(paymentId);
        } finally {
            long durationMs = (System.nanoTime() - startTime) / 1_000_000;
            logger.info("Endpoint POST /batches/{}/payments completed in {} ms", batchId, durationMs);
        }
    }

    @PostMapping("/{batchId}/execute")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @Operation(
        summary = "Execute a payroll batch",
        description = "Controller: PayrollBatchController. Technology: JPA write model with jOOQ stats upsert."
    )
    public void executeBatch(@PathVariable("batchId") UUID batchId) {
        long startTime = System.nanoTime();
        logger.info("Endpoint POST /batches/{}/execute called", batchId);
        try {
            executeBatchUseCase.execute(batchId);
        } finally {
            long durationMs = (System.nanoTime() - startTime) / 1_000_000;
            logger.info("Endpoint POST /batches/{}/execute completed in {} ms", batchId, durationMs);
        }
    }
}

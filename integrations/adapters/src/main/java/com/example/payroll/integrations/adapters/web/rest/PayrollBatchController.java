package com.example.payroll.integrations.adapters.web.rest;

import com.example.payroll.application.usecase.AddPayrollPaymentUseCase;
import com.example.payroll.application.usecase.CreatePayrollBatchUseCase;
import com.example.payroll.application.usecase.ExecutePayrollBatchUseCase;
import com.example.payroll.integrations.adapters.web.rest.dto.AddPaymentRequest;
import com.example.payroll.integrations.adapters.web.rest.dto.BatchResponse;
import com.example.payroll.integrations.adapters.web.rest.dto.CreateBatchRequest;
import com.example.payroll.integrations.adapters.web.rest.dto.PaymentResponse;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
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
    private final CreatePayrollBatchUseCase createBatchUseCase;
    private final AddPayrollPaymentUseCase addPaymentUseCase;
    private final ExecutePayrollBatchUseCase executeBatchUseCase;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BatchResponse createBatch(@RequestBody CreateBatchRequest request) {
        UUID batchId = createBatchUseCase.create(request.getCustomerId());
        return new BatchResponse(batchId);
    }

    @PostMapping("/{batchId}/payments")
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentResponse addPayment(@PathVariable("batchId") UUID batchId, @RequestBody AddPaymentRequest request) {
        UUID paymentId = addPaymentUseCase.addPayment(batchId, request.getBeneficiary(), request.getAmount());
        return new PaymentResponse(paymentId);
    }

    @PostMapping("/{batchId}/execute")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void executeBatch(@PathVariable("batchId") UUID batchId) {
        executeBatchUseCase.execute(batchId);
    }
}

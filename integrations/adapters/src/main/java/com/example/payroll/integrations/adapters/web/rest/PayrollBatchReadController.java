package com.example.payroll.integrations.adapters.web.rest;

import com.example.payroll.integrations.adapters.jooq.PayrollBatchQueryService;
import com.example.payroll.integrations.adapters.jooq.dto.BatchSummaryDto;
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
    private final PayrollBatchQueryService queryService;

    @GetMapping
    public List<BatchSummaryDto> listBatches(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long customerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String sort
    ) {
        return queryService.listBatches(status, customerId, page, size, sort);
    }
}

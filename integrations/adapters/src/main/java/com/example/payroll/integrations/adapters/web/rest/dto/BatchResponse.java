package com.example.payroll.integrations.adapters.web.rest.dto;

import java.util.UUID;
import lombok.Value;

@Value
public class BatchResponse {
    UUID batchId;
}

package com.example.payroll.web.rest.dto;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class AddPaymentRequest {
    private String beneficiary;
    private BigDecimal amount;
}

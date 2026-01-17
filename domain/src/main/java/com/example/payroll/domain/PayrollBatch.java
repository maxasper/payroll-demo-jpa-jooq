package com.example.payroll.domain;

import java.util.UUID;
import lombok.Value;

@Value
public class PayrollBatch {
    UUID id;
    String name;
}

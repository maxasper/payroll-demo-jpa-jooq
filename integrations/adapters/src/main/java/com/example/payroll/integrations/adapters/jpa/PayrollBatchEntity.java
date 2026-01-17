package com.example.payroll.integrations.adapters.jpa;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "payroll_batch")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PayrollBatchEntity {
    @Id
    private UUID id;
    private String name;
}

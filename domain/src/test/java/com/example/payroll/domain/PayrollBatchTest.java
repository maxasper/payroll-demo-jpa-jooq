package com.example.payroll.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PayrollBatchTest {
    @Test
    void executeTransitionsStatusAndPayments() {
        Instant now = Instant.parse("2026-01-17T00:00:00Z");
        PayrollBatch batch = new PayrollBatch(UUID.randomUUID(), 12L, now, now);

        batch.addPayment(UUID.randomUUID(), "Alice", new BigDecimal("10.00"), now);
        batch.addPayment(UUID.randomUUID(), "Bob", new BigDecimal("20.00"), now);
        batch.validate();

        batch.execute(now.plusSeconds(60));

        assertEquals(PayrollBatchStatus.EXECUTED, batch.getStatus());
        assertEquals(new BigDecimal("30.00"), batch.getTotalAmount());
        batch.getPayments().forEach(payment ->
            assertEquals(PayrollPaymentStatus.EXECUTED, payment.getStatus())
        );
    }

    @Test
    void addPaymentFailsWhenNotNew() {
        Instant now = Instant.parse("2026-01-17T00:00:00Z");
        PayrollBatch batch = new PayrollBatch(UUID.randomUUID(), 12L, now, now);
        batch.addPayment(UUID.randomUUID(), "Alice", new BigDecimal("10.00"), now);
        batch.validate();

        assertThrows(IllegalStateException.class, () ->
            batch.addPayment(UUID.randomUUID(), "Bob", new BigDecimal("5.00"), now)
        );
    }
}

package com.example.payroll;

import com.playtika.testcontainer.common.spring.DockerPresenceBootstrapConfiguration;
import com.playtika.testcontainer.postgresql.EmbeddedPostgreSQLBootstrapConfiguration;
import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = PayrollDemoApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({
    DockerPresenceBootstrapConfiguration.class,
    EmbeddedPostgreSQLBootstrapConfiguration.class
})
@ActiveProfiles("test")
class BootstrapContextTest {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void contextLoads() {
    }

    @Test
    void migrationsCreatePayrollTables() {
        Integer tables = jdbcTemplate.queryForObject(
            """
            select count(*)
            from information_schema.tables
            where table_schema = 'public'
              and table_name in ('payroll_batch', 'payroll_payment', 'payroll_stats')
            """,
            Integer.class
        );

        assertThat(tables).isEqualTo(3);
    }

    @Test
    void createAddExecuteFlowUpdatesStatusesAndTotals() {
        ResponseEntity<Map> createResponse = restTemplate.postForEntity(
            "/batches",
            Map.of("customerId", 1001),
            Map.class
        );
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        UUID batchId = UUID.fromString(createResponse.getBody().get("batchId").toString());

        ResponseEntity<Map> paymentOne = restTemplate.postForEntity(
            "/batches/" + batchId + "/payments",
            Map.of("beneficiary", "Alice", "amount", new BigDecimal("10.50")),
            Map.class
        );
        assertThat(paymentOne.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        ResponseEntity<Map> paymentTwo = restTemplate.postForEntity(
            "/batches/" + batchId + "/payments",
            Map.of("beneficiary", "Bob", "amount", new BigDecimal("5.25")),
            Map.class
        );
        assertThat(paymentTwo.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        ResponseEntity<Void> executeResponse = restTemplate.postForEntity(
            "/batches/" + batchId + "/execute",
            null,
            Void.class
        );
        assertThat(executeResponse.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);

        Map<String, Object> batchRow = jdbcTemplate.queryForMap(
            "select status, total_amount from payroll_batch where id = ?",
            batchId
        );
        assertThat(batchRow.get("status")).isEqualTo("EXECUTED");
        assertThat(new BigDecimal(batchRow.get("total_amount").toString()))
            .isEqualByComparingTo(new BigDecimal("15.75"));

        Integer executedPayments = jdbcTemplate.queryForObject(
            "select count(*) from payroll_payment where batch_id = ? and status = 'EXECUTED'",
            Integer.class,
            batchId
        );
        assertThat(executedPayments).isEqualTo(2);
    }

    @Test
    void executeNewBatchFailsAndAddingAfterExecutionFails() {
        ResponseEntity<Map> createResponse = restTemplate.postForEntity(
            "/batches",
            Map.of("customerId", 2002),
            Map.class
        );
        UUID batchId = UUID.fromString(createResponse.getBody().get("batchId").toString());

        ResponseEntity<Void> executeResponse = restTemplate.postForEntity(
            "/batches/" + batchId + "/execute",
            null,
            Void.class
        );
        assertThat(executeResponse.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);

        restTemplate.postForEntity(
            "/batches/" + batchId + "/payments",
            Map.of("beneficiary", "Alice", "amount", new BigDecimal("12.00")),
            Map.class
        );
        restTemplate.postForEntity(
            "/batches/" + batchId + "/execute",
            null,
            Void.class
        );

        ResponseEntity<Map> addAfterExecute = restTemplate.postForEntity(
            "/batches/" + batchId + "/payments",
            Map.of("beneficiary", "Bob", "amount", new BigDecimal("8.00")),
            Map.class
        );
        assertThat(addAfterExecute.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

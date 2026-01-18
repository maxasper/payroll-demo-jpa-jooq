package com.example.payroll;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import com.playtika.testcontainer.common.spring.DockerPresenceBootstrapConfiguration;
import com.playtika.testcontainer.postgresql.EmbeddedPostgreSQLBootstrapConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.http.client.ClientHttpResponse;
import java.io.IOException;
import org.testcontainers.containers.GenericContainer;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = ComponentTestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "embedded.containers.enabled=true",
    "embedded.postgresql.enabled=true"
})
class BootstrapContextTest {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private GenericContainer<?> payrollServiceContainer;

    @Autowired
    private PayrollServiceContainerProperties serviceProperties;

    private final RestTemplate restTemplate = new RestTemplate();

    BootstrapContextTest() {
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {
            @Override
            public boolean hasError(ClientHttpResponse response) throws IOException {
                return false;
            }
        });
    }

    private String baseUrl() {
        return "http://" + payrollServiceContainer.getHost() + ":" + payrollServiceContainer.getMappedPort(serviceProperties.getPort());
    }

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
            baseUrl() + "/batches",
            Map.of("customerId", 1001),
            Map.class
        );
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        UUID batchId = UUID.fromString(createResponse.getBody().get("batchId").toString());

        ResponseEntity<Map> paymentOne = restTemplate.postForEntity(
            baseUrl() + "/batches/" + batchId + "/payments",
            Map.of("beneficiary", "Alice", "amount", new BigDecimal("10.50")),
            Map.class
        );
        assertThat(paymentOne.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        ResponseEntity<Map> paymentTwo = restTemplate.postForEntity(
            baseUrl() + "/batches/" + batchId + "/payments",
            Map.of("beneficiary", "Bob", "amount", new BigDecimal("5.25")),
            Map.class
        );
        assertThat(paymentTwo.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        Integer paymentCount = jdbcTemplate.queryForObject(
            "select count(*) from payroll_payment where batch_id = ?",
            Integer.class,
            batchId
        );
        assertThat(paymentCount).isEqualTo(2);

        ResponseEntity<Void> executeResponse = restTemplate.postForEntity(
            baseUrl() + "/batches/" + batchId + "/execute",
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

        Map<String, Object> statsRow = jdbcTemplate.queryForMap(
            "select payments_cnt, total_amount from payroll_stats where batch_id = ?",
            batchId
        );
        assertThat(((Number) statsRow.get("payments_cnt")).intValue()).isEqualTo(2);
        assertThat(new BigDecimal(statsRow.get("total_amount").toString()))
            .isEqualByComparingTo(new BigDecimal("15.75"));

        ResponseEntity<List<Map<String, Object>>> jpaResponse = restTemplate.exchange(
            baseUrl() + "/batches-jpa?page=0&size=10",
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<>() {}
        );
        assertThat(jpaResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(jpaResponse.getBody()).isNotNull();
        assertThat(jpaResponse.getBody()).isNotEmpty();
    }

    @Test
    void executeNewBatchFailsAndAddingAfterExecutionFails() {
        ResponseEntity<Map> createResponse = restTemplate.postForEntity(
            baseUrl() + "/batches",
            Map.of("customerId", 2002),
            Map.class
        );
        UUID batchId = UUID.fromString(createResponse.getBody().get("batchId").toString());

        ResponseEntity<Void> executeResponse = restTemplate.postForEntity(
            baseUrl() + "/batches/" + batchId + "/execute",
            null,
            Void.class
        );
        assertThat(executeResponse.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);

        restTemplate.postForEntity(
            baseUrl() + "/batches/" + batchId + "/payments",
            Map.of("beneficiary", "Alice", "amount", new BigDecimal("12.00")),
            Map.class
        );
        restTemplate.postForEntity(
            baseUrl() + "/batches/" + batchId + "/execute",
            null,
            Void.class
        );

        ResponseEntity<Map> addAfterExecute = restTemplate.postForEntity(
            baseUrl() + "/batches/" + batchId + "/payments",
            Map.of("beneficiary", "Bob", "amount", new BigDecimal("8.00")),
            Map.class
        );
        assertThat(addAfterExecute.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

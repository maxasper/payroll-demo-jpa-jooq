package com.example.payroll;

import com.playtika.testcontainer.common.spring.DockerPresenceBootstrapConfiguration;
import com.playtika.testcontainer.postgresql.EmbeddedPostgreSQLBootstrapConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = PayrollDemoApplication.class)
@Import({
    DockerPresenceBootstrapConfiguration.class,
    EmbeddedPostgreSQLBootstrapConfiguration.class
})
@ActiveProfiles("test")
class BootstrapContextTest {
    @Autowired
    private JdbcTemplate jdbcTemplate;

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
}

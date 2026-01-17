package com.example.payroll;

import com.playtika.testcontainer.common.spring.DockerPresenceBootstrapConfiguration;
import com.playtika.testcontainer.postgresql.EmbeddedPostgreSQLBootstrapConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.context.annotation.Import;

@SpringBootTest(classes = PayrollDemoApplication.class)
@Import({
    DockerPresenceBootstrapConfiguration.class,
    EmbeddedPostgreSQLBootstrapConfiguration.class
})
@ActiveProfiles("test")
class BootstrapContextTest {
    @Test
    void contextLoads() {
    }
}

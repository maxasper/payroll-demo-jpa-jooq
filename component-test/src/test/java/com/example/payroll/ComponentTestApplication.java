package com.example.payroll;

import com.playtika.testcontainer.common.spring.DockerPresenceBootstrapConfiguration;
import com.playtika.testcontainer.postgresql.EmbeddedPostgreSQLBootstrapConfiguration;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Import;

@SpringBootConfiguration
@EnableAutoConfiguration
@Import({
    PayrollServiceContainerConfiguration.class,
    DockerPresenceBootstrapConfiguration.class,
    EmbeddedPostgreSQLBootstrapConfiguration.class
})
public class ComponentTestApplication {
}

package com.example.payroll;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.beans.factory.annotation.Qualifier;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

@Configuration
@EnableConfigurationProperties(PayrollServiceContainerProperties.class)
@RequiredArgsConstructor
public class PayrollServiceContainerConfiguration {
    private final PayrollServiceContainerProperties properties;

    @Bean(destroyMethod = "stop")
    @DependsOn("embeddedPostgreSql")
    @ConditionalOnProperty(prefix = "embedded.payroll-service", name = "enabled", havingValue = "true", matchIfMissing = true)
    public GenericContainer<?> payrollServiceContainer(
        @Qualifier("embeddedPostgreSql") PostgreSQLContainer<?> postgresql
    ) {
        String dockerImage = Optional.ofNullable(properties.getDockerImage())
            .orElse(properties.getDefaultDockerImage());
        GenericContainer<?> container = new GenericContainer<>(DockerImageName.parse(dockerImage))
            .withNetworkAliases("payroll-service")
            .withExposedPorts(properties.getPort())
            .waitingFor(Wait.forListeningPort())
            .withLogConsumer(new Slf4jLogConsumer(LoggerFactory.getLogger("payroll-service-container")))
            .withExtraHost("host.docker.internal", "host-gateway")
            .withEnv(properties.getEnv());

        String dbHost = "host.docker.internal";
        int dbPort = postgresql.getMappedPort(5432);
        String jdbcUrl = String.format(
            "jdbc:postgresql://%s:%d/%s",
            dbHost,
            dbPort,
            postgresql.getDatabaseName()
        );

        container.withEnv("JDBC_URL", jdbcUrl);
        container.withEnv("DB_USERNAME", postgresql.getUsername());
        container.withEnv("DB_PASSWORD", postgresql.getPassword());
        container.start();
        return container;
    }
}

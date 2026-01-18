package com.example.payroll;

import com.playtika.testcontainer.common.properties.CommonContainerProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "embedded.payroll-service")
public class PayrollServiceContainerProperties extends CommonContainerProperties {
    private int port = 8080;

    @Override
    public String getDefaultDockerImage() {
        return "payroll-demo-jpa-jooq:local";
    }
}

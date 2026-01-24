package com.example.payroll;

import com.example.payroll.application.usecase.AddPayrollPaymentUseCase;
import com.example.payroll.application.usecase.AddPayrollPaymentService;
import com.example.payroll.application.usecase.CreatePayrollBatchService;
import com.example.payroll.application.usecase.CreatePayrollBatchUseCase;
import com.example.payroll.application.usecase.ExecutePayrollBatchService;
import com.example.payroll.application.usecase.ExecutePayrollBatchUseCase;
import com.example.payroll.application.usecase.ListPayrollBatchesService;
import com.example.payroll.application.usecase.ListPayrollBatchesUseCase;
import com.example.payroll.application.usecase.ListPayrollBatchesJpaService;
import com.example.payroll.application.usecase.ListPayrollBatchesJpaUseCase;
import com.example.payroll.domain.port.PayrollBatchRepositoryPort;
import com.example.payroll.domain.port.PayrollBatchJpaQueryPort;
import com.example.payroll.domain.port.PayrollBatchQueryPort;
import com.example.payroll.domain.port.PayrollStatsPort;
import java.time.Clock;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class PayrollDemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(PayrollDemoApplication.class, args);
    }

    @Bean
    public Clock systemClock() {
        return Clock.systemUTC();
    }

    @Bean
    public CreatePayrollBatchUseCase createPayrollBatchUseCase(PayrollBatchRepositoryPort repository, Clock clock) {
        return new CreatePayrollBatchService(repository, clock);
    }

    @Bean
    public AddPayrollPaymentUseCase addPayrollPaymentUseCase(PayrollBatchRepositoryPort repository, Clock clock) {
        return new AddPayrollPaymentService(repository, clock);
    }

    @Bean
    public ExecutePayrollBatchUseCase executePayrollBatchUseCase(
        PayrollBatchRepositoryPort repository,
        PayrollStatsPort statsPort,
        Clock clock
    ) {
        return new ExecutePayrollBatchService(repository, statsPort, clock);
    }

    @Bean
    public ListPayrollBatchesUseCase listPayrollBatchesUseCase(PayrollBatchQueryPort queryPort) {
        return new ListPayrollBatchesService(queryPort);
    }

    @Bean
    public ListPayrollBatchesJpaUseCase listPayrollBatchesJpaUseCase(PayrollBatchJpaQueryPort queryPort) {
        return new ListPayrollBatchesJpaService(queryPort);
    }
}

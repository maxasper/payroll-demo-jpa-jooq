package com.example.payroll;

import com.example.payroll.application.usecase.AddPayrollPaymentUseCase;
import com.example.payroll.application.usecase.CreatePayrollBatchUseCase;
import com.example.payroll.application.usecase.ExecutePayrollBatchUseCase;
import com.example.payroll.domain.port.PayrollBatchRepository;
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
    public CreatePayrollBatchUseCase createPayrollBatchUseCase(PayrollBatchRepository repository, Clock clock) {
        return new CreatePayrollBatchUseCase(repository, clock);
    }

    @Bean
    public AddPayrollPaymentUseCase addPayrollPaymentUseCase(PayrollBatchRepository repository, Clock clock) {
        return new AddPayrollPaymentUseCase(repository, clock);
    }

    @Bean
    public ExecutePayrollBatchUseCase executePayrollBatchUseCase(PayrollBatchRepository repository, Clock clock) {
        return new ExecutePayrollBatchUseCase(repository, clock);
    }
}

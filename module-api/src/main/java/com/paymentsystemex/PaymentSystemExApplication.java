package com.paymentsystemex;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
@ComponentScan(basePackages = {"core.domain", "com.paymentsystemex"})
public class PaymentSystemExApplication {

    public static void main(String[] args) {
        SpringApplication.run(PaymentSystemExApplication.class, args);
    }

}

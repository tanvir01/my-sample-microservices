package com.tanservices.order;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories
@TestPropertySource("classpath:application-test.properties")
@EnableTransactionManagement
public class H2JpaConfig {
    // ...
}
package ru.shulenin.impl.integration;

import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

@SpringBootTest
public abstract class TestBase {
    private final static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:14.9");
    @BeforeAll
    public static void containerStart() {
        postgres.start();
    }

    @DynamicPropertySource
    public static void postgresProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
    }
}

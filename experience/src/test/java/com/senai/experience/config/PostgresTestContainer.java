package com.senai.experience.config;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import org.testcontainers.junit.jupiter.Container;

@Testcontainers
public abstract class PostgresTestContainer {

    @Container
    static final PostgreSQLContainer<?> POSTGRES =
       new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("db_experience_test")
            .withUsername("postgres")
            .withPassword("postgres");

    static { POSTGRES.start(); }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url",      POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }
}


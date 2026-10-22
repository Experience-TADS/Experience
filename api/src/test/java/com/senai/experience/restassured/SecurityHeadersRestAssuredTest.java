package com.senai.experience.restassured;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;


@DisplayName("Cabeçalhos de segurança HTTP — testes de integração (REST Assured)")
class SecurityHeadersRestAssuredTest extends RestAssuredBaseTest {

    private static final String ENDPOINT_PUBLICO = "/v3/api-docs";

    @Test
    @DisplayName("X-Content-Type-Options = nosniff")
    void contentTypeOptions_nosniff() {
        given()
        .when()
                .get(ENDPOINT_PUBLICO)
        .then()
                .statusCode(200)
                .header("X-Content-Type-Options", equalTo("nosniff"));
    }

    @Test
    @DisplayName("X-Frame-Options = DENY")
    void frameOptions_deny() {
        given()
        .when()
                .get(ENDPOINT_PUBLICO)
        .then()
                .statusCode(200)
                .header("X-Frame-Options", equalTo("DENY"));
    }

    @Test
    @DisplayName("Content-Security-Policy contém default-src 'self'")
    void contentSecurityPolicy_basico() {
        given()
        .when()
                .get(ENDPOINT_PUBLICO)
        .then()
                .statusCode(200)
                .header("Content-Security-Policy", containsString("default-src 'self'"))
                .header("Content-Security-Policy", containsString("frame-ancestors 'none'"));
    }

    @Test
    @DisplayName("X-XSS-Protection = 1; mode=block")
    void xssProtection_modeBlock() {
        given()
        .when()
                .get(ENDPOINT_PUBLICO)
        .then()
                .statusCode(200)
                .header("X-XSS-Protection", equalTo("1; mode=block"));
    }

    @Test
    @DisplayName("Referrer-Policy = strict-origin-when-cross-origin")
    void referrerPolicy() {
        given()
        .when()
                .get(ENDPOINT_PUBLICO)
        .then()
                .statusCode(200)
                .header("Referrer-Policy", equalTo("strict-origin-when-cross-origin"));
    }
}

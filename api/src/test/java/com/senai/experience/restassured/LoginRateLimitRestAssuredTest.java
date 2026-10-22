package com.senai.experience.restassured;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestPropertySource;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;


@TestPropertySource(properties = {
        "ratelimit.login.max-tentativas=3",
        "ratelimit.login.janela-minutos=10"
})
@DisplayName("Login rate limiting — força bruta (REST Assured)")
class LoginRateLimitRestAssuredTest extends RestAssuredBaseTest {

    private static final String LOGIN = "/api/usuario/login";
    private static final int MAX_TENTATIVAS = 3;
    /** IP sintético exclusivo deste teste para não colidir com outros logins. */
    private static final String IP_SINTETICO = "203.0.113.77";

    private static final String CORPO_INVALIDO = """
            { "email": "admin@experience.com", "senha": "senhaErrada" }
            """;

    @Test
    @DisplayName("Excede o limite por IP → primeiras tentativas 401, a seguinte 429")
    void forcaBruta_excedeLimite_retorna429() {
        // As primeiras MAX_TENTATIVAS requisições consomem tokens: credenciais
        // inválidas → 401 (passam pelo filtro e chegam ao controller).
        for (int i = 0; i < MAX_TENTATIVAS; i++) {
            given()
                    .header("X-Forwarded-For", IP_SINTETICO)
                    .contentType(ContentType.JSON)
                    .body(CORPO_INVALIDO)
            .when()
                    .post(LOGIN)
            .then()
                    .statusCode(401);
        }

        // A próxima requisição excede o limite → 429 com corpo JSON de erro.
        given()
                .header("X-Forwarded-For", IP_SINTETICO)
                .contentType(ContentType.JSON)
                .body(CORPO_INVALIDO)
        .when()
                .post(LOGIN)
        .then()
                .statusCode(429)
                .contentType(ContentType.JSON)
                .body("error", containsString("Muitas tentativas"));
    }
}

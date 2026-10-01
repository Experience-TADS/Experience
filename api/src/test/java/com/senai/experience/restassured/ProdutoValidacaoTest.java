package com.senai.experience.restassured;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.notNullValue;

/**
 * Testes de validação do campo {@code preco} para {@code /api/produto}.
 *
 * <p>Cobre os critérios de aceite: preço nulo, preço negativo e preço zero
 * devem retornar {@code 400 Bad Request} com detalhe da violação.
 * Preços válidos devem ser aceitos com {@code 201}.</p>
 */
@DisplayName("Produto — validação de preço (REST Assured)")
class ProdutoValidacaoTest extends RestAssuredBaseTest {

    private static final String BASE = "/api/produto";

    private String corpo(String modelo, String cor, String versao, int ano, String preco) {
        if (preco == null) {
            return """
                    { "modelo": "%s", "cor": "%s", "versao": "%s", "ano": %d }
                    """.formatted(modelo, cor, versao, ano);
        }
        return """
                { "modelo": "%s", "cor": "%s", "versao": "%s", "ano": %d, "preco": %s }
                """.formatted(modelo, cor, versao, ano, preco);
    }

    // ── Preço ausente / nulo ──────────────────────────────────────────────────

    @Test
    @DisplayName("POST sem campo preco retorna 400")
    void criar_semPreco_retorna400() {
        String token = obterTokenAdmin();

        given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(corpo("Corolla", "Branco", "XEi", 2024, null))
        .when()
                .post(BASE)
        .then()
                .statusCode(400)
                .body("detalhes", containsString("preco"));
    }

    // ── Preço inválido ────────────────────────────────────────────────────────

    @Test
    @DisplayName("POST com preco negativo retorna 400")
    void criar_precoNegativo_retorna400() {
        String token = obterTokenAdmin();

        given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(corpo("Corolla", "Branco", "XEi", 2024, "-100.00"))
        .when()
                .post(BASE)
        .then()
                .statusCode(400)
                .body("detalhes", containsString("preco"));
    }

    @Test
    @DisplayName("POST com preco zero retorna 400")
    void criar_precoZero_retorna400() {
        String token = obterTokenAdmin();

        given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(corpo("Corolla", "Branco", "XEi", 2024, "0.0"))
        .when()
                .post(BASE)
        .then()
                .statusCode(400)
                .body("detalhes", containsString("preco"));
    }

    // ── Preço válido ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("POST com preco positivo retorna 201 e inclui preco na resposta")
    void criar_precoValido_retorna201() {
        String token = obterTokenAdmin();

        given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(corpo("Corolla", "Branco", "XEi", 2024, "149900.00"))
        .when()
                .post(BASE)
        .then()
                .statusCode(201)
                .body("preco", notNullValue());
    }

    @Test
    @DisplayName("POST com preco de centavos (valor baixo positivo) retorna 201")
    void criar_precoMinimo_retorna201() {
        String token = obterTokenAdmin();

        given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(corpo("Yaris", "Vermelho", "XLS", 2024, "0.01"))
        .when()
                .post(BASE)
        .then()
                .statusCode(201)
                .body("preco", notNullValue());
    }

    // ── PUT também valida ─────────────────────────────────────────────────────

    @Test
    @DisplayName("PUT com preco negativo retorna 400")
    void atualizar_precoNegativo_retorna400() {
        String token = obterTokenAdmin();

        given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(corpo("Hilux", "Prata", "SRX", 2024, "-1.00"))
        .when()
                .put(BASE + "/1")
        .then()
                .statusCode(400)
                .body("detalhes", containsString("preco"));
    }
}

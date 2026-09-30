package com.senai.experience.restassured;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.notNullValue;

/**
 * Testes de unicidade do campo {@code chassi} para {@code /api/veiculo}.
 *
 * <p>O chassi é um identificador único de veículo — tentar cadastrar dois
 * veículos com o mesmo chassi deve retornar {@code 409 Conflict} com uma
 * mensagem descritiva no corpo da resposta.</p>
 *
 * <p>O {@code DataSeeder} já ocupa os chassi 10001–10010 no perfil de teste.
 * Os testes aqui usam faixas distintas (9xxxx) para evitar colisão com o seed
 * e entre si.</p>
 */
@DisplayName("Veiculo — unicidade de chassi (REST Assured)")
class VeiculoChassiDuplicadoTest extends RestAssuredBaseTest {

    private static final String BASE = "/api/veiculo";

    private String corpo(int chassi) {
        return """
                {
                  "idProduto": 1,
                  "chassi": %d,
                  "statusVeiculo": "AGUARDANDO"
                }
                """.formatted(chassi);
    }

    // ── Cenário principal: chassi duplicado ───────────────────────────────────

    @Test
    @DisplayName("POST com chassi duplicado retorna 409 Conflict")
    void criar_chassiDuplicado_retorna409() {
        String token = obterTokenAdmin();
        int chassi = 90001;

        // Primeira inserção — deve ser aceita
        given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(corpo(chassi))
        .when()
                .post(BASE)
        .then()
                .statusCode(201)
                .body("chassi", notNullValue());

        // Segunda inserção com o mesmo chassi — deve ser rejeitada
        given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(corpo(chassi))
        .when()
                .post(BASE)
        .then()
                .statusCode(409)
                .body("mensagem", containsString("Conflito"))
                .body("detalhes", containsString("chassi"));
    }

    @Test
    @DisplayName("POST com chassi duplicado retorna corpo com status 409 no JSON")
    void criar_chassiDuplicado_corpoContemStatus409() {
        String token = obterTokenAdmin();
        int chassi = 90002;

        // Primeira inserção
        given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(corpo(chassi))
        .when()
                .post(BASE)
        .then()
                .statusCode(201);

        // Segunda inserção — verifica campo "status" no corpo do erro
        given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(corpo(chassi))
        .when()
                .post(BASE)
        .then()
                .statusCode(409)
                .body("status", notNullValue());
    }

    // ── Cenário positivo: chassi único é aceito ───────────────────────────────

    @Test
    @DisplayName("POST com chassi único retorna 201")
    void criar_chassiUnico_retorna201() {
        String token = obterTokenAdmin();

        given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(corpo(90003))
        .when()
                .post(BASE)
        .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("chassi", notNullValue());
    }

    // ── Chassi do DataSeeder já existente ─────────────────────────────────────

    @Test
    @DisplayName("POST com chassi já criado pelo DataSeeder retorna 409")
    void criar_chassiDoSeeder_retorna409() {
        String token = obterTokenAdmin();

        // O DataSeeder cria chassi 10001–10010; tentar reusar o 10001 deve falhar
        given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(corpo(10001))
        .when()
                .post(BASE)
        .then()
                .statusCode(409)
                .body("detalhes", containsString("chassi"));
    }
}

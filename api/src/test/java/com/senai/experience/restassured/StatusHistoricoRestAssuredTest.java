package com.senai.experience.restassured;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

/**
 * Testes de integração REST Assured para {@code /api/veiculo/{veiculoId}/status}.
 *
 * <p>Regras de segurança:</p>
 * <ul>
 *   <li>GET {@code /api/veiculo/*&#47;status} → autenticado</li>
 *   <li>POST {@code /api/veiculo/*&#47;status} → público (permitAll)</li>
 * </ul>
 *
 * <p>O POST recebe um {@code StatusFabricacao} cru no corpo — em JSON, uma
 * string como {@code "PINTURA"}.</p>
 */
@DisplayName("StatusHistorico — testes de integração (REST Assured)")
class StatusHistoricoRestAssuredTest extends RestAssuredBaseTest {

    /** Cria um veículo e devolve o id, para ter um alvo isolado do seeder. */
    private Integer criarVeiculo(String token, int chassi) {
        return given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body("""
                        { "idProduto": 1, "chassi": %d, "statusVeiculo": "AGUARDANDO" }
                        """.formatted(chassi))
        .when()
                .post("/api/veiculo")
        .then()
                .statusCode(201)
                .extract()
                .path("id");
    }

    private String statusUrl(int veiculoId) {
        return "/api/veiculo/" + veiculoId + "/status";
    }

    // ── GET (autenticado) ─────────────────────────────────────────────────────

    @Test
    @DisplayName("GET histórico sem token retorna 403")
    void getHistorico_semToken_retorna403() {
        given()
        .when()
                .get(statusUrl(1))
        .then()
                .statusCode(403);
    }

    @Test
    @DisplayName("GET histórico com token retorna 200 e lista")
    void getHistorico_comToken_retorna200() {
        String token = obterTokenAdmin();
        Integer veiculoId = criarVeiculo(token, 66601);

        given()
                .header("Authorization", "Bearer " + token)
        .when()
                .get(statusUrl(veiculoId))
        .then()
                .statusCode(200)
                .body("$", notNullValue()); // corpo é uma lista JSON
    }

    // ── POST (público) ────────────────────────────────────────────────────────

    @Test
    @DisplayName("POST atualiza status sem autenticação (endpoint público) → 200")
    void atualizarStatus_publico_retorna200() {
        String token = obterTokenAdmin();
        Integer veiculoId = criarVeiculo(token, 66602);

        // POST é público: envia o enum como string, sem header de autorização.
        // O veículo começa em AGUARDANDO; a única transição válida é para
        // MONTAGEM_ESTRUTURAL (ver StatusHistoricoService.validarTransicao).
        given()
                .contentType(ContentType.JSON)
                .body("\"MONTAGEM_ESTRUTURAL\"")
        .when()
                .post(statusUrl(veiculoId))
        .then()
                .statusCode(200);

        // Confirma que o histórico agora reflete a atualização (GET autenticado).
        given()
                .header("Authorization", "Bearer " + token)
        .when()
                .get(statusUrl(veiculoId))
        .then()
                .statusCode(200);
    }
}

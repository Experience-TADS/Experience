package com.senai.experience.restassured;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

@DisplayName("Autorização do status do veículo — testes de integração (REST Assured)")
class VeiculoStatusAutorizacaoRestAssuredTest extends RestAssuredBaseTest {

    private static final String VENDEDOR_EMAIL = "vendedor.toyota@experience.com";
    private static final String VENDEDOR_SENHA = "vendedor123";

    /** Corpo de sucesso: única transição válida a partir de AGUARDANDO. */
    private static final String NOVO_STATUS = "\"MONTAGEM_ESTRUTURAL\"";

    private String statusUrl(int veiculoId) {
        return "/api/veiculo/" + veiculoId + "/status";
    }

    /** Cria um veículo via admin e devolve o id, isolado do seeder por chassi único. */
    private Integer criarVeiculo(String tokenAdmin, int chassi) {
        return given()
                .header("Authorization", "Bearer " + tokenAdmin)
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

    // ── ADMIN → 2xx ────────────────────────────────────────────────────────────

    @Test
    @DisplayName("ADMIN altera status → 200")
    void atualizarStatus_comAdmin_retorna200() {
        String tokenAdmin = obterTokenAdmin();
        Integer veiculoId = criarVeiculo(tokenAdmin, 77701);

        given()
                .header("Authorization", "Bearer " + tokenAdmin)
                .contentType(ContentType.JSON)
                .body(NOVO_STATUS)
        .when()
                .post(statusUrl(veiculoId))
        .then()
                .statusCode(200);
    }

    // ── VENDEDOR → 2xx ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("VENDEDOR altera status → 200")
    void atualizarStatus_comVendedor_retorna200() {
        String tokenAdmin = obterTokenAdmin();
        String tokenVendedor = obterToken(VENDEDOR_EMAIL, VENDEDOR_SENHA);
        Integer veiculoId = criarVeiculo(tokenAdmin, 77702);

        given()
                .header("Authorization", "Bearer " + tokenVendedor)
                .contentType(ContentType.JSON)
                .body(NOVO_STATUS)
        .when()
                .post(statusUrl(veiculoId))
        .then()
                .statusCode(200);
    }

    // ── CLIENTE → 403 ────────────────────────────────────────────────────────────

    @Test
    @DisplayName("CLIENTE não pode alterar status → 403")
    void atualizarStatus_comCliente_retorna403() {
        String tokenAdmin = obterTokenAdmin();
        String tokenCliente = criarUsuarioEObterToken(
                "cliente.status." + System.currentTimeMillis() + "@teste.com",
                "Senha@123",
                "CLIENTE");
        Integer veiculoId = criarVeiculo(tokenAdmin, 77703);

        given()
                .header("Authorization", "Bearer " + tokenCliente)
                .contentType(ContentType.JSON)
                .body(NOVO_STATUS)
        .when()
                .post(statusUrl(veiculoId))
        .then()
                .statusCode(403);
    }

    // ── Sem token → 401 ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("Requisição sem token → 401")
    void atualizarStatus_semToken_retorna401() {
        String tokenAdmin = obterTokenAdmin();
        Integer veiculoId = criarVeiculo(tokenAdmin, 77704);

        given()
                .contentType(ContentType.JSON)
                .body(NOVO_STATUS)
        .when()
                .post(statusUrl(veiculoId))
        .then()
                .statusCode(401);
    }
}

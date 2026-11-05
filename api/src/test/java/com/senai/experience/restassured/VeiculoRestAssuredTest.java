package com.senai.experience.restassured;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

/**
 * Testes de integração REST Assured para {@code /api/veiculo}.
 *
 * <p>Os endpoints CRUD exigem autenticação ({@code .anyRequest().authenticated()}).</p>
 *
 * <p>O {@code DataSeeder} cria 5 produtos (id 1–5) e 10 veículos (chassi 10001–10010)
 * no startup do perfil de teste.</p>
 *
 * <p><b>Nota:</b> o endpoint {@code POST /api/veiculo/nodered/evento} foi removido.
 * O backend agora consome o tópico MQTT {@code senai/pii_toy} diretamente via
 * {@code MqttEspEventoListener}, sem passar pelo Node-RED.</p>
 */
@DisplayName("Veiculo — testes de integração (REST Assured)")
class VeiculoRestAssuredTest extends RestAssuredBaseTest {

    private static final String BASE = "/api/veiculo";

    private String corpoVeiculo(int chassi, String status) {
        return """
                {
                  "idProduto": 1,
                  "chassi": %d,
                  "statusVeiculo": "%s"
                }
                """.formatted(chassi, status);
    }

    // ── Autorização ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("GET sem token retorna 403")
    void listar_semToken_retorna403() {
        given()
        .when()
                .get(BASE)
        .then()
                .statusCode(403);
    }

    @Test
    @DisplayName("POST sem token retorna 403")
    void criar_semToken_retorna403() {
        given()
                .contentType(ContentType.JSON)
                .body(corpoVeiculo(55501, "AGUARDANDO"))
        .when()
                .post(BASE)
        .then()
                .statusCode(403);
    }

    // ── Leitura ───────────────────────────────────────────────────────────────

    @Test
    @DisplayName("GET lista com token retorna 200 e página")
    void listar_comToken_retorna200() {
        String token = obterTokenAdmin();

        given()
                .header("Authorization", "Bearer " + token)
        .when()
                .get(BASE)
        .then()
                .statusCode(200)
                .body("content", notNullValue());
    }

    // ── CRUD ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Fluxo CRUD completo: criar, buscar, atualizar e deletar")
    void crudCompleto() {
        String token = obterTokenAdmin();

        // CREATE → 201
        Integer id = given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(corpoVeiculo(55502, "AGUARDANDO"))
        .when()
                .post(BASE)
        .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("chassi", equalTo(55502))
                .body("statusVeiculo", equalTo("AGUARDANDO"))
                .extract()
                .path("id");

        // READ → 200
        given()
                .header("Authorization", "Bearer " + token)
        .when()
                .get(BASE + "/" + id)
        .then()
                .statusCode(200)
                .body("id", equalTo(id));

        // UPDATE → 200 (avança o status)
        given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(corpoVeiculo(55502, "PINTURA"))
        .when()
                .put(BASE + "/" + id)
        .then()
                .statusCode(200)
                .body("statusVeiculo", equalTo("PINTURA"));

        // DELETE → 204
        given()
                .header("Authorization", "Bearer " + token)
        .when()
                .delete(BASE + "/" + id)
        .then()
                .statusCode(204);
    }

    @Test
    @DisplayName("GET por id inexistente retorna 404")
    void buscarInexistente_retorna404() {
        String token = obterTokenAdmin();

        given()
                .header("Authorization", "Bearer " + token)
        .when()
                .get(BASE + "/99999999")
        .then()
                .statusCode(404);
    }
}

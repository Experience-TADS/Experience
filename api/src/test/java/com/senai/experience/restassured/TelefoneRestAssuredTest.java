package com.senai.experience.restassured;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

/**
 * Testes de integração REST Assured para {@code /api/telefones}.
 *
 * <p>Todos os métodos exigem autenticação. O telefone referencia um usuário
 * via {@code idUsuario}.</p>
 */
@DisplayName("Telefone — testes de integração (REST Assured)")
class TelefoneRestAssuredTest extends RestAssuredBaseTest {

    private static final String BASE = "/api/telefones";

    private Integer criarUsuario(String email) {
        return given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                          "nome": "Dono Telefone",
                          "email": "%s",
                          "senha": "senha123",
                          "dataNascimento": "1990-01-01",
                          "role": "CLIENTE"
                        }
                        """.formatted(email))
        .when()
                .post("/api/usuario")
        .then()
                .statusCode(201)
                .extract()
                .path("id");
    }

    private String corpo(String numero, int idUsuario) {
        return """
                { "numero": "%s", "idUsuario": %d }
                """.formatted(numero, idUsuario);
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
                .body(corpo("11999998888", 1))
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
        Integer idUsuario = criarUsuario("dono.telefone@teste.com");

        // CREATE → 201
        Integer id = given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(corpo("11999998888", idUsuario))
        .when()
                .post(BASE)
        .then()
                .statusCode(201)
                .body("id", notNullValue())
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

        // UPDATE → 200
        given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(corpo("21988887777", idUsuario))
        .when()
                .put(BASE + "/" + id)
        .then()
                .statusCode(200)
                .body("numero", equalTo("21988887777"));

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

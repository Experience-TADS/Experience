package com.senai.experience.restassured;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

/**
 * Testes de integração REST Assured para {@code /api/endereco}.
 *
 * <p>Todos os métodos exigem autenticação ({@code .anyRequest().authenticated()}).
 * O endereço referencia um usuário via {@code idUsuario}.</p>
 */
@DisplayName("Endereco — testes de integração (REST Assured)")
class EnderecoRestAssuredTest extends RestAssuredBaseTest {

    private static final String BASE = "/api/endereco";

    /** Cria um usuário e retorna o id, para vincular ao endereço. */
    private Integer criarUsuario(String email) {
        return given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                          "nome": "Dono Endereco",
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

    private String corpo(int idUsuario) {
        return """
                {
                  "cep": "01001000",
                  "logradouro": "Praca da Se",
                  "numero": 100,
                  "bairro": "Se",
                  "cidade": "Sao Paulo",
                  "estado": "SP",
                  "idUsuario": %d
                }
                """.formatted(idUsuario);
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
                .body(corpo(1))
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
        Integer idUsuario = criarUsuario("dono.endereco@teste.com");

        // CREATE → 201
        Integer id = given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(corpo(idUsuario))
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
                .body("""
                        {
                          "cep": "20040002",
                          "logradouro": "Av Rio Branco",
                          "numero": 200,
                          "bairro": "Centro",
                          "cidade": "Rio de Janeiro",
                          "estado": "RJ",
                          "idUsuario": %d
                        }
                        """.formatted(idUsuario))
        .when()
                .put(BASE + "/" + id)
        .then()
                .statusCode(200)
                .body("cidade", equalTo("Rio de Janeiro"));

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

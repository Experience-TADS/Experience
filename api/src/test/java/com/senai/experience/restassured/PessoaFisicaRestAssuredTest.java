package com.senai.experience.restassured;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * Testes de integração REST Assured para {@code /api/pessoaFisica}.
 *
 * <p>Valida o contrato DTO: request usa campo {@code senha} (não {@code senhaHash}),
 * o response não expõe {@code senhaHash}, e POST retorna 201.</p>
 */
@DisplayName("PessoaFisica — testes de integração (REST Assured)")
class PessoaFisicaRestAssuredTest extends RestAssuredBaseTest {

    private static final String BASE = "/api/pessoaFisica";

    private static final String CPF_CRIAR  = "39053344705";
    private static final String CPF_GET    = "16899535009";
    private static final String CPF_DELETE = "12345678909";

    private String corpo(String nome, String email, String cpf) {
        return """
                {
                  "nome": "%s",
                  "email": "%s",
                  "senha": "Senha@123",
                  "dataNascimento": "1990-01-01",
                  "cpf": "%s",
                  "role": "CLIENTE"
                }
                """.formatted(nome, email, cpf);
    }

    private Integer criarPessoaFisica(String email, String cpf) {
        return given()
                .contentType(ContentType.JSON)
                .body(corpo("Pessoa Teste", email, cpf))
        .when()
                .post(BASE)
        .then()
                .statusCode(201)
                .extract()
                .path("id");
    }

    // ── POST público ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("POST com CPF válido cria pessoa física → 201 sem senhaHash")
    void criar_cpfValido_retorna201() {
        given()
                .contentType(ContentType.JSON)
                .body(corpo("Ana Teste", "ana.pf@teste.com", CPF_CRIAR))
        .when()
                .post(BASE)
        .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("cpf", equalTo(CPF_CRIAR))
                .body("senhaHash", nullValue());
    }

    @Test
    @DisplayName("POST com CPF inválido retorna 400")
    void criar_cpfInvalido_retorna400() {
        given()
                .contentType(ContentType.JSON)
                .body(corpo("Invalido", "invalido.pf@teste.com", "11111111111"))
        .when()
                .post(BASE)
        .then()
                .statusCode(400);
    }

    @Test
    @DisplayName("POST sem campo obrigatório retorna 400")
    void criar_semCampoObrigatorio_retorna400() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                          "email": "semcampo.pf@teste.com",
                          "senha": "Senha@123",
                          "dataNascimento": "1990-01-01",
                          "cpf": "39053344705",
                          "role": "CLIENTE"
                        }
                        """)
        .when()
                .post(BASE)
        .then()
                .statusCode(400);
    }

    // ── Leitura protegida ─────────────────────────────────────────────────────

    @Test
    @DisplayName("GET lista sem token retorna 403")
    void listar_semToken_retorna403() {
        given()
        .when()
                .get(BASE)
        .then()
                .statusCode(403);
    }

    @Test
    @DisplayName("GET lista com token retorna 200 sem senhaHash")
    void listar_comToken_retorna200() {
        String token = obterTokenAdmin();

        given()
                .header("Authorization", "Bearer " + token)
        .when()
                .get(BASE)
        .then()
                .statusCode(200)
                .body("content", notNullValue())
                .body("content.senhaHash", everyItem(nullValue()));
    }

    @Test
    @DisplayName("GET por id existente retorna 200 sem senhaHash")
    void buscarPorId_existente_retorna200() {
        String token = obterTokenAdmin();
        Integer id = criarPessoaFisica("get.pf@teste.com", CPF_GET);

        given()
                .header("Authorization", "Bearer " + token)
        .when()
                .get(BASE + "/" + id)
        .then()
                .statusCode(200)
                .body("id", equalTo(id))
                .body("senhaHash", nullValue());
    }

    @Test
    @DisplayName("GET por id inexistente retorna 404")
    void buscarPorId_inexistente_retorna404() {
        String token = obterTokenAdmin();

        given()
                .header("Authorization", "Bearer " + token)
        .when()
                .get(BASE + "/99999999")
        .then()
                .statusCode(404);
    }

    // ── DELETE protegido ──────────────────────────────────────────────────────

    @Test
    @DisplayName("DELETE com token retorna 204")
    void deletar_comToken_retorna204() {
        String token = obterTokenAdmin();
        Integer id = criarPessoaFisica("delete.pf@teste.com", CPF_DELETE);

        given()
                .header("Authorization", "Bearer " + token)
        .when()
                .delete(BASE + "/" + id)
        .then()
                .statusCode(204);
    }

    @Test
    @DisplayName("DELETE sem token retorna 403")
    void deletar_semToken_retorna403() {
        given()
        .when()
                .delete(BASE + "/1")
        .then()
                .statusCode(403);
    }
}

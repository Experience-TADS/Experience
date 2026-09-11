package com.senai.experience.restassured;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

/**
 * Testes de integração REST Assured para {@code /api/pessoaFisica}.
 *
 * <p>O POST é público (permitAll). Os demais métodos exigem autenticação.
 * O corpo é a entidade {@code PessoaFisica} crua, então o campo de senha é
 * {@code senhaHash} e o CPF deve ter 11 dígitos sem máscara e ser válido.
 * Erros de validação/persistência são convertidos para 404 pelo
 * {@code GlobalHandlerException}.</p>
 */
@DisplayName("PessoaFisica — testes de integração (REST Assured)")
class PessoaFisicaRestAssuredTest extends RestAssuredBaseTest {

    private static final String BASE = "/api/pessoaFisica";

    // CPFs válidos e distintos entre si — evitam colisão com o UNIQUE do banco.
    // Nenhum destes é usado pelo DataSeeder.
    private static final String CPF_CRIAR   = "39053344705";
    private static final String CPF_GET     = "16899535009";
    private static final String CPF_DELETE  = "12345678909";

    private String corpo(String nome, String email, String cpf) {
        return """
                {
                  "nome": "%s",
                  "email": "%s",
                  "senhaHash": "senha123",
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
                .statusCode(200)
                .extract()
                .path("id");
    }

    // ── POST público ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("POST com CPF válido cria pessoa física (público) → 200")
    void criar_cpfValido_retorna200() {
        given()
                .contentType(ContentType.JSON)
                .body(corpo("Ana Teste", "ana.pf@teste.com", CPF_CRIAR))
        .when()
                .post(BASE)
        .then()
                .statusCode(200)
                .body("id", notNullValue())
                .body("cpf", equalTo(CPF_CRIAR));
    }

    @Test
    @DisplayName("POST com CPF inválido retorna erro (404 pelo handler)")
    void criar_cpfInvalido_retornaErro() {
        given()
                .contentType(ContentType.JSON)
                .body(corpo("Invalido", "invalido.pf@teste.com", "11111111111"))
        .when()
                .post(BASE)
        .then()
                .statusCode(404);
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
    @DisplayName("GET lista com token retorna 200")
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

    @Test
    @DisplayName("GET por id existente retorna 200")
    void buscarPorId_existente_retorna200() {
        String token = obterTokenAdmin();
        Integer id = criarPessoaFisica("get.pf@teste.com", CPF_GET);

        given()
                .header("Authorization", "Bearer " + token)
        .when()
                .get(BASE + "/" + id)
        .then()
                .statusCode(200)
                .body("id", equalTo(id));
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

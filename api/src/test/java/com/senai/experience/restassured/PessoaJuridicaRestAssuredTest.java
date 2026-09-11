package com.senai.experience.restassured;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

/**
 * Testes de integração REST Assured para {@code /api/pessoaJuridica}.
 *
 * <p>O POST é público. Demais métodos exigem autenticação. O corpo é a entidade
 * {@code PessoaJuridica} crua ({@code senhaHash}, {@code cnpj} com 14 dígitos,
 * {@code razaoSocial}). Erros de validação/persistência → 404.</p>
 */
@DisplayName("PessoaJuridica — testes de integração (REST Assured)")
class PessoaJuridicaRestAssuredTest extends RestAssuredBaseTest {

    private static final String BASE = "/api/pessoaJuridica";

    // CNPJs válidos e distintos entre si — evitam colisão com o UNIQUE do banco.
    private static final String CNPJ_CRIAR  = "11222333000181";
    private static final String CNPJ_GET    = "11444777000161";
    private static final String CNPJ_DELETE = "34028316000103";

    private String corpo(String nome, String email, String cnpj, String razaoSocial) {
        return """
                {
                  "nome": "%s",
                  "email": "%s",
                  "senhaHash": "senha123",
                  "dataNascimento": "2000-01-01",
                  "cnpj": "%s",
                  "razaoSocial": "%s",
                  "role": "VENDEDOR"
                }
                """.formatted(nome, email, cnpj, razaoSocial);
    }

    private Integer criarPessoaJuridica(String email, String cnpj) {
        return given()
                .contentType(ContentType.JSON)
                .body(corpo("Empresa Teste", email, cnpj, "Empresa Teste LTDA"))
        .when()
                .post(BASE)
        .then()
                .statusCode(200)
                .extract()
                .path("id");
    }

    // ── POST público ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("POST com CNPJ válido cria pessoa jurídica (público) → 200")
    void criar_cnpjValido_retorna200() {
        given()
                .contentType(ContentType.JSON)
                .body(corpo("Empresa Valida", "empresa.valida@teste.com", CNPJ_CRIAR, "Empresa Valida LTDA"))
        .when()
                .post(BASE)
        .then()
                .statusCode(200)
                .body("id", notNullValue())
                .body("cnpj", equalTo(CNPJ_CRIAR))
                .body("razaoSocial", equalTo("Empresa Valida LTDA"));
    }

    @Test
    @DisplayName("POST com CNPJ inválido retorna erro (404 pelo handler)")
    void criar_cnpjInvalido_retornaErro() {
        given()
                .contentType(ContentType.JSON)
                .body(corpo("Empresa Invalida", "empresa.invalida@teste.com", "11111111111111", "Empresa Invalida LTDA"))
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
        Integer id = criarPessoaJuridica("get.pj@teste.com", CNPJ_GET);

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
        Integer id = criarPessoaJuridica("delete.pj@teste.com", CNPJ_DELETE);

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

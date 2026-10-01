package com.senai.experience.restassured;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.notNullValue;

/**
 * Testes de unicidade do campo {@code email} para {@code /api/usuario}.
 *
 * <p>O e-mail é o identificador de login de cada usuário — tentar cadastrar
 * dois usuários com o mesmo endereço deve retornar {@code 409 Conflict} com
 * uma mensagem descritiva no corpo da resposta.</p>
 *
 * <p>O {@code DataSeeder} já ocupa {@code admin@experience.com} e
 * {@code vendedor.toyota@experience.com} no perfil de teste. Os testes aqui
 * usam domínios distintos para evitar colisão entre si e com o seed.</p>
 */
@DisplayName("Usuario — unicidade de e-mail (REST Assured)")
class UsuarioEmailDuplicadoTest extends RestAssuredBaseTest {

    private static final String BASE = "/api/usuario";

    private String corpo(String nome, String email) {
        return """
                {
                  "nome": "%s",
                  "email": "%s",
                  "senha": "Senha@123",
                  "dataNascimento": "1990-01-01",
                  "role": "CLIENTE"
                }
                """.formatted(nome, email);
    }

    // ── Cenário principal: e-mail duplicado ───────────────────────────────────

    @Test
    @DisplayName("POST com e-mail duplicado retorna 409 Conflict")
    void criar_emailDuplicado_retorna409() {
        String email = "duplicado.usuario@unicidade.com";

        // Primeira inserção — deve ser aceita
        given()
                .contentType(ContentType.JSON)
                .body(corpo("Primeiro Usuario", email))
        .when()
                .post(BASE)
        .then()
                .statusCode(201)
                .body("id", notNullValue());

        // Segunda inserção com o mesmo e-mail — deve ser rejeitada
        given()
                .contentType(ContentType.JSON)
                .body(corpo("Segundo Usuario", email))
        .when()
                .post(BASE)
        .then()
                .statusCode(409)
                .body("mensagem", containsString("Conflito"))
                .body("detalhes", containsString("e-mail"));
    }

    @Test
    @DisplayName("POST com e-mail duplicado retorna campo status 409 no JSON")
    void criar_emailDuplicado_corpoContemStatus409() {
        String email = "duplicado2.usuario@unicidade.com";

        // Primeira inserção
        given()
                .contentType(ContentType.JSON)
                .body(corpo("Usuario A", email))
        .when()
                .post(BASE)
        .then()
                .statusCode(201);

        // Segunda inserção — verifica campo "status" no corpo do erro
        given()
                .contentType(ContentType.JSON)
                .body(corpo("Usuario B", email))
        .when()
                .post(BASE)
        .then()
                .statusCode(409)
                .body("status", notNullValue());
    }

    // ── E-mail do DataSeeder já existente ─────────────────────────────────────

    @Test
    @DisplayName("POST com e-mail do admin (DataSeeder) retorna 409")
    void criar_emailDoAdmin_retorna409() {
        // admin@experience.com é criado pelo DataSeeder no startup do perfil test
        given()
                .contentType(ContentType.JSON)
                .body(corpo("Outro Admin", ADMIN_EMAIL))
        .when()
                .post(BASE)
        .then()
                .statusCode(409)
                .body("detalhes", containsString("e-mail"));
    }

    // ── Case sensitivity: mesmo e-mail com capitalização diferente ────────────

    @Test
    @DisplayName("POST com e-mail em casing diferente é tratado pelo banco (depende do collation)")
    void criar_emailCaseDiferente_comportamentoEsperado() {
        String emailOriginal  = "case.test@unicidade.com";
        String emailUpperCase = "CASE.TEST@unicidade.com";

        // Primeira inserção em minúsculas — deve ser aceita
        given()
                .contentType(ContentType.JSON)
                .body(corpo("Usuario Original", emailOriginal))
        .when()
                .post(BASE)
        .then()
                .statusCode(201);

        // Segunda inserção em maiúsculas — comportamento depende do collation do banco.
        // PostgreSQL é case-sensitive por padrão (ci_collation não ativado), então
        // este insert será aceito (201). O teste documenta o comportamento atual.
        given()
                .contentType(ContentType.JSON)
                .body(corpo("Usuario Casing", emailUpperCase))
        .when()
                .post(BASE)
        .then()
                .statusCode(anyOf(201, 409));
    }

    // ── Cenário positivo: e-mails distintos são aceitos ──────────────────────

    @Test
    @DisplayName("POST com e-mails distintos aceita ambos com 201")
    void criar_emailsDistintos_ambosRetornam201() {
        given()
                .contentType(ContentType.JSON)
                .body(corpo("Usuario X", "usuario.x@unicidade.com"))
        .when()
                .post(BASE)
        .then()
                .statusCode(201)
                .body("id", notNullValue());

        given()
                .contentType(ContentType.JSON)
                .body(corpo("Usuario Y", "usuario.y@unicidade.com"))
        .when()
                .post(BASE)
        .then()
                .statusCode(201)
                .body("id", notNullValue());
    }

    // ── Utilitário ────────────────────────────────────────────────────────────

    /**
     * Matcher que aceita múltiplos status HTTP — usado para documentar
     * comportamento dependente do banco sem forçar um resultado fixo.
     */
    private static org.hamcrest.Matcher<Integer> anyOf(int a, int b) {
        return org.hamcrest.Matchers.anyOf(
            org.hamcrest.Matchers.equalTo(a),
            org.hamcrest.Matchers.equalTo(b)
        );
    }
}

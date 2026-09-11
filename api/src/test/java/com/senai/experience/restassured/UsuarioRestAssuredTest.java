package com.senai.experience.restassured;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

/**
 * Testes de integração REST Assured para o recurso {@code /api/usuario}.
 *
 * <p>Regras de segurança relevantes:</p>
 * <ul>
 *   <li>POST {@code /api/usuario} e POST {@code /api/usuario/login} → públicos</li>
 *   <li>GET/PUT/DELETE → autenticado</li>
 *   <li>PATCH ativar/desativar → apenas ADMIN ({@code @PreAuthorize})</li>
 * </ul>
 */
@DisplayName("Usuario — testes de integração (REST Assured)")
class UsuarioRestAssuredTest extends RestAssuredBaseTest {

    private static final String BASE = "/api/usuario";

    private String corpoUsuario(String nome, String email, String senha, String role) {
        return """
                {
                  "nome": "%s",
                  "email": "%s",
                  "senha": "%s",
                  "dataNascimento": "1990-01-01",
                  "role": "%s"
                }
                """.formatted(nome, email, senha, role);
    }

    // ── Login ───────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Login com credenciais válidas retorna 200 e token")
    void login_valido_retornaToken() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                        { "email": "%s", "senha": "%s" }
                        """.formatted(ADMIN_EMAIL, ADMIN_SENHA))
        .when()
                .post(BASE + "/login")
        .then()
                .statusCode(200)
                .body("token", notNullValue())
                .body("email", equalTo(ADMIN_EMAIL))
                .body("role", equalTo("ADMIN"));
    }

    @Test
    @DisplayName("Login com senha errada retorna 401")
    void login_senhaErrada_retorna401() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                        { "email": "%s", "senha": "senhaErrada" }
                        """.formatted(ADMIN_EMAIL))
        .when()
                .post(BASE + "/login")
        .then()
                .statusCode(401);
    }

    // ── Criação (pública) ─────────────────────────────────────────────────────

    @Test
    @DisplayName("POST cria usuário sem autenticação (endpoint público) → 201")
    void criar_semToken_retorna201() {
        given()
                .contentType(ContentType.JSON)
                .body(corpoUsuario("Novo Usuario", "novo.usuario@teste.com", "senha123", "CLIENTE"))
        .when()
                .post(BASE)
        .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("email", equalTo("novo.usuario@teste.com"));
    }

    // ── /me ─────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("GET /me com token retorna dados do usuário logado")
    void me_comToken_retornaUsuario() {
        String token = obterTokenAdmin();

        given()
                .header("Authorization", "Bearer " + token)
        .when()
                .get(BASE + "/me")
        .then()
                .statusCode(200)
                .body("email", equalTo(ADMIN_EMAIL));
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

    // ── Ativar / Desativar (apenas ADMIN) ─────────────────────────────────────

    @Test
    @DisplayName("ADMIN consegue desativar e reativar um usuário")
    void ativarDesativar_comAdmin() {
        String tokenAdmin = obterTokenAdmin();

        // Cria usuário alvo (público) e captura o id
        Integer id = given()
                .contentType(ContentType.JSON)
                .body(corpoUsuario("Alvo", "alvo.toggle@teste.com", "senha123", "CLIENTE"))
        .when()
                .post(BASE)
        .then()
                .statusCode(201)
                .extract()
                .path("id");

        // Desativa → 200
        given()
                .header("Authorization", "Bearer " + tokenAdmin)
        .when()
                .patch(BASE + "/" + id + "/desativar")
        .then()
                .statusCode(200);

        // Reativa → 200
        given()
                .header("Authorization", "Bearer " + tokenAdmin)
        .when()
                .patch(BASE + "/" + id + "/ativar")
        .then()
                .statusCode(200);
    }

    @Test
    @DisplayName("CLIENTE não pode ativar usuário → 403")
    void ativar_comCliente_retorna403() {
        String tokenCliente = criarUsuarioEObterToken(
                "cliente.toggle@teste.com", "senha123", "CLIENTE");

        given()
                .header("Authorization", "Bearer " + tokenCliente)
        .when()
                .patch(BASE + "/1/ativar")
        .then()
                .statusCode(403);
    }
}

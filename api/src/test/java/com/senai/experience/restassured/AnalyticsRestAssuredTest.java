package com.senai.experience.restassured;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * Testes de integração REST Assured para {@code /api/analytics}.
 *
 * <p>Endpoints cobertos:</p>
 * <ul>
 *   <li>{@code POST /api/analytics/sessao} — registra uma sessão de navegação</li>
 *   <li>{@code GET  /api/analytics/sessao/resumo} — resumo agregado por seção</li>
 * </ul>
 *
 * <p>Regras de segurança: ambos os endpoints exigem autenticação
 * ({@code @SecurityRequirement(name = "bearerAuth")} no controller).</p>
 */
@DisplayName("Analytics — testes de integração (REST Assured)")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AnalyticsRestAssuredTest extends RestAssuredBaseTest {

    private static final String BASE = "/api/analytics";

    // Email único por execução evita colisão com outros testes no mesmo contexto Spring
    private final String emailCliente = "analytics.cliente." + System.currentTimeMillis() + "@teste.com";

    private String tokenAdmin;
    private String tokenCliente;
    private Long idCliente;

    @BeforeEach
    void setup() {
        tokenAdmin = obterTokenAdmin();

        io.restassured.response.Response res = given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                          "nome": "Cliente Analytics",
                          "email": "%s",
                          "senha": "senha123",
                          "dataNascimento": "1990-01-01",
                          "role": "CLIENTE"
                        }
                        """.formatted(emailCliente))
        .when()
                .post("/api/usuario");

        if (res.statusCode() == 201) {
            idCliente = res.jsonPath().getLong("id");
        } else {
            String tk = obterToken(emailCliente, "senha123");
            idCliente = given()
                    .header("Authorization", "Bearer " + tk)
                    .when().get("/api/usuario/me")
                    .then().statusCode(200)
                    .extract().jsonPath().getLong("id");
        }

        tokenCliente = obterToken(emailCliente, "senha123");
    }

    // ── POST /sessao — autorização ────────────────────────────────────────────

    @Test
    @DisplayName("POST /sessao sem token retorna 403")
    void registrar_semToken_retorna403() {
        given()
                .contentType(ContentType.JSON)
                .body(corpoSessao(idCliente, "catalogo-produtos", 120))
        .when()
                .post(BASE + "/sessao")
        .then()
                .statusCode(403);
    }

    // ── POST /sessao — criação ────────────────────────────────────────────────

    @Test
    @DisplayName("POST /sessao com token de CLIENTE → 201 e dados retornados")
    void registrar_comCliente_retorna201() {
        given()
                .header("Authorization", "Bearer " + tokenCliente)
                .contentType(ContentType.JSON)
                .body(corpoSessao(idCliente, "catalogo-produtos", 120))
        .when()
                .post(BASE + "/sessao")
        .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("clienteId", equalTo(idCliente.intValue()))
                .body("secao", equalTo("catalogo-produtos"))
                .body("duracaoSegundos", equalTo(120))
                .body("registradoEm", notNullValue());
    }

    @Test
    @DisplayName("POST /sessao com token de ADMIN → 201")
    void registrar_comAdmin_retorna201() {
        given()
                .header("Authorization", "Bearer " + tokenAdmin)
                .contentType(ContentType.JSON)
                .body(corpoSessao(idCliente, "acompanhamento-pedido", 60))
        .when()
                .post(BASE + "/sessao")
        .then()
                .statusCode(201)
                .body("secao", equalTo("acompanhamento-pedido"));
    }

    // ── POST /sessao — validações ─────────────────────────────────────────────

    @Test
    @DisplayName("POST /sessao sem clienteId retorna 400")
    void registrar_semClienteId_retorna400() {
        given()
                .header("Authorization", "Bearer " + tokenCliente)
                .contentType(ContentType.JSON)
                .body("""
                        { "secao": "home", "duracaoSegundos": 30 }
                        """)
        .when()
                .post(BASE + "/sessao")
        .then()
                .statusCode(400);
    }

    @Test
    @DisplayName("POST /sessao sem secao retorna 400")
    void registrar_semSecao_retorna400() {
        given()
                .header("Authorization", "Bearer " + tokenCliente)
                .contentType(ContentType.JSON)
                .body("""
                        { "clienteId": %d, "duracaoSegundos": 30 }
                        """.formatted(idCliente))
        .when()
                .post(BASE + "/sessao")
        .then()
                .statusCode(400);
    }

    @Test
    @DisplayName("POST /sessao com secao em branco retorna 400")
    void registrar_secaoEmBranco_retorna400() {
        given()
                .header("Authorization", "Bearer " + tokenCliente)
                .contentType(ContentType.JSON)
                .body(corpoSessao(idCliente, "", 30))
        .when()
                .post(BASE + "/sessao")
        .then()
                .statusCode(400);
    }

    @Test
    @DisplayName("POST /sessao sem duracaoSegundos retorna 400")
    void registrar_semDuracao_retorna400() {
        given()
                .header("Authorization", "Bearer " + tokenCliente)
                .contentType(ContentType.JSON)
                .body("""
                        { "clienteId": %d, "secao": "home" }
                        """.formatted(idCliente))
        .when()
                .post(BASE + "/sessao")
        .then()
                .statusCode(400);
    }

    @Test
    @DisplayName("POST /sessao com duracao zero retorna 400")
    void registrar_duracaoZero_retorna400() {
        given()
                .header("Authorization", "Bearer " + tokenCliente)
                .contentType(ContentType.JSON)
                .body(corpoSessao(idCliente, "home", 0))
        .when()
                .post(BASE + "/sessao")
        .then()
                .statusCode(400);
    }

    @Test
    @DisplayName("POST /sessao com duracao negativa retorna 400")
    void registrar_duracaoNegativa_retorna400() {
        given()
                .header("Authorization", "Bearer " + tokenCliente)
                .contentType(ContentType.JSON)
                .body(corpoSessao(idCliente, "home", -10))
        .when()
                .post(BASE + "/sessao")
        .then()
                .statusCode(400);
    }

    // ── GET /sessao/resumo ────────────────────────────────────────────────────

    @Test
    @DisplayName("GET /sessao/resumo sem token retorna 403")
    void resumo_semToken_retorna403() {
        given()
        .when()
                .get(BASE + "/sessao/resumo")
        .then()
                .statusCode(403);
    }

    @Test
    @DisplayName("GET /sessao/resumo com token retorna 200 e lista")
    void resumo_comToken_retorna200() {
        // Registra algumas sessões para garantir resultado não vazio
        registrarSessao(tokenCliente, "home", 45);
        registrarSessao(tokenCliente, "home", 90);
        registrarSessao(tokenCliente, "catalogo-produtos", 200);

        given()
                .header("Authorization", "Bearer " + tokenAdmin)
        .when()
                .get(BASE + "/sessao/resumo")
        .then()
                .statusCode(200)
                .body("$", instanceOf(java.util.List.class))
                .body("size()", greaterThan(0))
                .body("[0].secao", notNullValue())
                .body("[0].mediaDuracaoSegundos", notNullValue())
                .body("[0].totalRegistros", notNullValue());
    }

    @Test
    @DisplayName("GET /sessao/resumo com CLIENTE retorna 403 (apenas ADMIN)")
    void resumo_comCliente_retorna403() {
        given()
                .header("Authorization", "Bearer " + tokenCliente)
        .when()
                .get(BASE + "/sessao/resumo")
        .then()
                .statusCode(403);
    }

    @Test
    @DisplayName("Resumo agrega corretamente sessões da mesma seção")
    void resumo_agregaMediaCorretamente() {
        String secao = "detalhe-veiculo-" + System.currentTimeMillis(); // evita colisão com outros testes

        registrarSessao(tokenCliente, secao, 100);
        registrarSessao(tokenCliente, secao, 200);

        given()
                .header("Authorization", "Bearer " + tokenAdmin)
        .when()
                .get(BASE + "/sessao/resumo")
        .then()
                .statusCode(200)
                .body("find { it.secao == '" + secao + "' }.totalRegistros", equalTo(2))
                .body("find { it.secao == '" + secao + "' }.mediaDuracaoSegundos", equalTo(150.0f));
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private String corpoSessao(Long clienteId, String secao, int duracao) {
        return """
                {
                  "clienteId": %d,
                  "secao": "%s",
                  "duracaoSegundos": %d
                }
                """.formatted(clienteId, secao, duracao);
    }

    private void registrarSessao(String token, String secao, int duracao) {
        given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(corpoSessao(idCliente, secao, duracao))
        .when()
                .post(BASE + "/sessao")
        .then()
                .statusCode(201);
    }
}

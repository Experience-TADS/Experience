package com.senai.experience.restassured;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.notNullValue;

/**
 * Testes de integração REST Assured para o recurso {@code /api/produto}.
 *
 * <p>Cobre o CRUD completo e as regras de autorização definidas no
 * {@code SecurityConfig}:</p>
 * <ul>
 *   <li>GET  → qualquer usuário autenticado</li>
 *   <li>POST/PUT → ADMIN ou VENDEDOR</li>
 *   <li>DELETE → apenas ADMIN</li>
 * </ul>
 */
@DisplayName("Produto — testes de integração (REST Assured)")
class ProdutoRestAssuredTest extends RestAssuredBaseTest {

    private static final String BASE = "/api/produto";

    private String corpoProduto(String modelo, String cor, String versao, int ano) {
        return """
                { "modelo": "%s", "cor": "%s", "versao": "%s", "ano": %d }
                """.formatted(modelo, cor, versao, ano);
    }

    // ── Autenticação / Autorização ───────────────────────────────────────────

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
                .body(corpoProduto("Corolla", "Branco", "XEi", 2024))
        .when()
                .post(BASE)
        .then()
                .statusCode(403);
    }

    @Test
    @DisplayName("POST com token de CLIENTE retorna 403 (sem permissão)")
    void criar_comCliente_retorna403() {
        String tokenCliente = criarUsuarioEObterToken(
                "cliente.produto@teste.com", "senha123", "CLIENTE");

        given()
                .header("Authorization", "Bearer " + tokenCliente)
                .contentType(ContentType.JSON)
                .body(corpoProduto("Hilux", "Prata", "SRX", 2024))
        .when()
                .post(BASE)
        .then()
                .statusCode(403);
    }

    // ── Leitura ───────────────────────────────────────────────────────────────

    @Test
    @DisplayName("GET com token autenticado lista produtos paginados")
    void listar_comToken_retornaPagina() {
        String token = obterTokenAdmin();

        given()
                .header("Authorization", "Bearer " + token)
        .when()
                .get(BASE)
        .then()
                .statusCode(200)
                .body("content", notNullValue())
                // O DataSeeder cria 5 produtos no startup
                .body("content.size()", greaterThan(0));
    }

    // ── CRUD completo (ADMIN) ──────────────────────────────────────────────────

    @Test
    @DisplayName("Fluxo CRUD completo como ADMIN: criar, buscar, atualizar e deletar")
    void crudCompleto_comAdmin() {
        String token = obterTokenAdmin();

        // CREATE → 201
        Integer id = given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(corpoProduto("Yaris", "Vermelho", "XLS", 2024))
        .when()
                .post(BASE)
        .then()
                .statusCode(201)
                .body("idProduto", notNullValue())
                .body("modelo", equalTo("Yaris"))
                .body("cor", equalTo("Vermelho"))
                .body("versao", equalTo("XLS"))
                .body("ano", equalTo(2024))
                .extract()
                .path("idProduto");

        // READ by id → 200
        given()
                .header("Authorization", "Bearer " + token)
        .when()
                .get(BASE + "/" + id)
        .then()
                .statusCode(200)
                .body("idProduto", equalTo(id))
                .body("modelo", equalTo("Yaris"));

        // UPDATE → 200
        given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(corpoProduto("Yaris", "Azul", "XL", 2025))
        .when()
                .put(BASE + "/" + id)
        .then()
                .statusCode(200)
                .body("idProduto", equalTo(id))
                .body("cor", equalTo("Azul"))
                .body("versao", equalTo("XL"))
                .body("ano", equalTo(2025));

        // DELETE → 204
        given()
                .header("Authorization", "Bearer " + token)
        .when()
                .delete(BASE + "/" + id)
        .then()
                .statusCode(204);

        // Confirma remoção → 404
        given()
                .header("Authorization", "Bearer " + token)
        .when()
                .get(BASE + "/" + id)
        .then()
                .statusCode(404);
    }

    @Test
    @DisplayName("VENDEDOR pode criar produto mas não pode deletar")
    void vendedor_criaMasNaoDeleta() {
        String tokenVendedor = criarUsuarioEObterToken(
                "vendedor.produto@teste.com", "senha123", "VENDEDOR");

        // VENDEDOR cria → 201
        Integer id = given()
                .header("Authorization", "Bearer " + tokenVendedor)
                .contentType(ContentType.JSON)
                .body(corpoProduto("RAV4", "Preto", "GR-S", 2025))
        .when()
                .post(BASE)
        .then()
                .statusCode(201)
                .extract()
                .path("idProduto");

        // VENDEDOR tenta deletar → 403 (só ADMIN pode)
        given()
                .header("Authorization", "Bearer " + tokenVendedor)
        .when()
                .delete(BASE + "/" + id)
        .then()
                .statusCode(403);
    }

    @Test
    @DisplayName("GET de produto inexistente retorna 404")
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
